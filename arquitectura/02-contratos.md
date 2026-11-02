# Contratos de comunicación entre servicios

Historia técnica **HU-T1.2** (SCRUM-65). Las clases de los mensajes y los DTO compartidos viven en el módulo `microservicios/shared-contracts`.

## 1. Mensajería (RabbitMQ)

- Exchange de dominio: `banco.exchange` (topic, durable).
- Exchange de mensajes fallidos: `banco.dlx` (direct, durable). Cada cola de eventos tiene su cola `<cola>.dlq`.
- Los mensajes se publican como JSON persistente y llevan el `operationId` como `messageId`, que es la clave de idempotencia de los consumidores.

| Evento / comando | Clase Java | Routing key → cola | Productor | Consumidores | Contenido | Estado |
|---|---|---|---|---|---|---|
| SubmitQuestionForReview (comando) | `SubmitQuestionForReview` | `question.submit` → `question.submit.queue` | question-service | lifecycle-service | operationId, questionId, requestedBy, requestedAt | Implementado (Taller 09) |
| PreguntaEnviadaARevision | `QuestionSubmittedForReview` | `question.submitted.event` → `question.submitted.event.queue` | lifecycle-service | notification-service | operationId, questionId, newState, occurredAt | Contrato definido; falta publicarlo (HU-T3.4) |
| RevisorAsignado | `ReviewerAssigned` | `reviewer.assigned` → `reviewer.assigned.queue` | review-service | notification-service | operationId, questionId, reviewerId, assignedBy, occurredAt | **Implementado** (uno por revisor asignado) |
| RevisionCompletada | `ReviewCompleted` | `review.completed` → `review.completed.queue` | review-service | notification-service | operationId, questionId, reviewerId, authorId, result, comments, questionFinalState, occurredAt | **Implementado** (uno por evaluación; `questionFinalState` solo trae valor en la última) |

Garantías (HU-T9):

1. **Durabilidad:** las colas son durables y los mensajes persistentes. Si notification-service está caído, los eventos esperan en la cola y se consumen cuando vuelve a arrancar. El productor también declara las colas, así que existen aunque el consumidor nunca haya arrancado.
2. **Idempotencia:** el consumidor registra `tipo:operationId` después de procesar. Una entrega repetida se descarta.
3. **Reintentos y DLQ:** el consumidor reintenta 3 veces con espera exponencial (1 s, 2 s). Si sigue fallando, el mensaje pasa a `<cola>.dlq` en lugar de perderse.
4. **No bloqueo:** el envío de la notificación ocurre en el consumidor; la respuesta REST de review-service no espera el correo.

## 2. Endpoints REST

Todas las rutas públicas pasan por el API Gateway (`http://localhost:8080`). La columna *Servicio* muestra la ruta interna a la que el gateway reenvía.

### 2.1 Revisión por pares — `review-service` (8085)

| Verbo | Ruta pública (gateway) | Ruta en el servicio | Descripción | Códigos |
|---|---|---|---|---|
| POST | `/api/v1/asignaciones` | igual | Asigna de 1 a 3 revisores a una pregunta en PENDING_REVIEW. Cuerpo: `{questionId, reviewerIds[], assignedBy}` | 201, 400, 404, 409, 503 |
| GET | `/api/v1/asignaciones?questionId=` | igual | Lista asignaciones (todas o de una pregunta) | 200 |
| GET | `/api/v1/revisiones/asignadas?revisorId=&page=&size=` | igual | Preguntas asignadas al revisor y pendientes de su decisión, paginadas (máx. 50) | 200, 400 |
| POST | `/api/v1/revisiones/evaluar` | igual | Registra APPROVED o REJECTED con observaciones. Cuerpo: `{questionId, reviewerId, result, comments}` | 200, 400, 403, 409, 503 |
| GET | `/api/v1/revisiones/pregunta/{questionId}` | igual | Observaciones de cada revisor, en solo lectura, para el autor | 200 |
| POST | — | `/reviews/assign`, `/reviews/evaluate` | Rutas del Taller 09, se conservan para la colección de Postman y delegan en la misma lógica | 201 / 200 |

Errores: el cuerpo siempre es `{error, message, timestamp}`.

| Código | Cuándo |
|---|---|
| 400 | Cantidad de revisores fuera de 1–3, revisor repetido, revisor sin rol, autor como revisor, rechazo sin observación («Debe registrar al menos una observación para rechazar»), paginación inválida |
| 403 | El revisor no tiene asignada la pregunta («No tiene asignada esta pregunta») |
| 404 | La pregunta o el usuario no existen |
| 409 | La pregunta no está en PENDING_REVIEW, ya tiene revisores pendientes, el revisor ya evaluó o lifecycle-service rechazó la transición |
| 503 | question-service, user-service o lifecycle-service no responden (timeout de conexión 2 s, de lectura 3 s) |

Consultas que hace review-service a otros servicios:

| Destino | Llamada | Para qué |
|---|---|---|
| question-service | `GET /questions/{id}` | Comprobar que la pregunta existe y conocer su autor |
| user-service | `GET /users/{id}` | Comprobar que el revisor existe y tiene rol REVISOR o DOCENTE |
| lifecycle-service | `GET /lifecycle/{id}` | Comprobar que la pregunta está en PENDING_REVIEW |
| lifecycle-service | `POST /lifecycle/{id}/transition` | PENDING_REVIEW → IN_REVIEW al asignar; IN_REVIEW → APPROVED / REJECTED con la última evaluación |

### 2.2 Autoría — `question-service` (8081)

| Verbo | Ruta pública | Ruta en el servicio | Descripción | Códigos |
|---|---|---|---|---|
| POST | `/api/v1/preguntas` | `/questions` | Crear pregunta con todos los campos DCE (contexto, enunciado, 4 opciones, clave, justificación, bibliografía, competencia, tema, subtema, dificultad). Valida con validation-service | 201, 400, 503 |
| PUT | `/api/v1/preguntas/{id}?requestedBy=` | `/questions/{id}` | Editar; solo el autor y solo en DRAFT o REJECTED | 200, 400, 403, 404, 409 |
| GET | `/api/v1/preguntas?authorId=&status=&competency=&topic=&difficulty=&keyword=&page=&size=` | `/questions` | Listado paginado con filtros; cada pregunta trae `status`, `statusLabel` y `statusColor` consultados en lote a lifecycle-service | 200, 400 |
| GET | `/api/v1/preguntas/{id}` | `/questions/{id}` | Detalle con estado actual y color | 200, 404 |
| GET | `/api/v1/preguntas/catalog` | `/questions/catalog` | Competencias, niveles y estados con su color | 200 |
| POST | `/api/v1/preguntas/{id}/submit-review?requestedBy=` | `/questions/{id}/submit-review` | Enviar a revisión (asíncrono, solo el autor) | 202, 403, 404, 503 |
| GET | `/api/v1/preguntas/operations/{operationId}` | `/questions/operations/{operationId}` | Estado de la operación asíncrona | 200, 404 |

### 2.3 Identidad — `user-service` (8083)

| Verbo | Ruta pública | Ruta en el servicio | Descripción | Códigos |
|---|---|---|---|---|
| POST | `/api/v1/auth/login` | `/auth/login` | Iniciar sesión | 200, 401 |
| GET | `/api/v1/usuarios` | `/users` | Listar usuarios | 200 |
| GET | `/api/v1/usuarios/{id}` | `/users/{id}` | Usuario por id | 200, 404 |
| GET | `/api/v1/usuarios/role/{role}` | `/users/role/{role}` | Usuarios por rol (revisores elegibles) | 200 |

### 2.4 Ciclo de vida, validación y notificaciones

| Servicio | Verbo | Ruta pública | Ruta en el servicio | Códigos |
|---|---|---|---|---|
| lifecycle-service (8084) | GET | `/api/v1/ciclo-vida/{questionId}` | `/lifecycle/{questionId}` | 200 |
| lifecycle-service | GET | `/api/v1/ciclo-vida?ids=1,2,3` | `/lifecycle?ids=` (estados en lote, lo usa question-service al listar) | 200, 400 |
| lifecycle-service | POST | `/api/v1/ciclo-vida/{questionId}/transition` | `/lifecycle/{questionId}/transition` | 200, 400 |
| validation-service (8082) | POST | `/api/v1/validaciones` | `/validate` | 200 |
| notification-service (8086) | GET | `/api/v1/notificaciones` | `/notifications` | 200 |
| notification-service | GET | `/api/v1/notificaciones/recipient/{id}` | `/notifications/recipient/{id}` | 200 |

## 3. Configuración por variables de entorno

Ningún puerto, URL ni credencial está fijo en el código. Cada `application.properties` usa `${VARIABLE:valor-local}` y la lista completa está en [`microservicios/.env.example`](../microservicios/.env.example).
