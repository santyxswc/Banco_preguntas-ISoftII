# Guion del video de sustentación — Corte 2

Historia técnica **HU-T16.1** (SCRUM-115). Duración máxima: **15 minutos**. No se puede acelerar el video ni pasarse del tiempo (−1 punto). Participan los tres integrantes.

| Integrante | Bloques |
|---|---|
| Yeferson Santiago Caicedo Orozco | 1, 4, 7 y la parte del autor en el 9 |
| Adrián Camilo Araujo Urbano | 2, 5, 6 y la parte del revisor y los eventos en el 9 |
| Iván Alexander López Lasso | 3, 8 y la parte del administrador en el 9 |

## Antes de grabar

1. Docker Desktop abierto. En `microservicios`: `docker compose down` y luego `.\start-all.ps1`. Esto levanta RabbitMQ, los 7 servicios y el frontend.
2. Abrir en pestañas:
   - Frontend: http://localhost:5173
   - Swagger del gateway: http://localhost:8080/swagger-ui.html
   - Consola de RabbitMQ: http://localhost:15672 (guest/guest)
   - El repositorio en GitHub (commits y Actions)
   - El tablero del Sprint 2 en Jira
3. Abrir `DOCUMENTO_ARQUITECTURA_CORTE2.md` en la vista previa de IntelliJ y dejar visibles las ventanas de consola de notification-service y lifecycle-service.
4. Tener corrida la prueba de carga (`pruebas-carga\comparar-resultados.ps1`) para mostrar la tabla de resultados.
5. Usuarios de la demo: `autor1 / autor123`, `admin / admin123`, `revisor1 / revisor123`, `revisor2 / revisor123`.

## Bloques

### 1. Historias de usuario — 0:00 a 1:00 · Santiago

**Muestra:** sección 2 del documento.

**Dice:** «Somos Santiago, Adrián e Iván. En esta iteración refactorizamos el banco de preguntas Saber PRO a microservicios orientados a eventos. Implementamos cinco historias: HU-01 crear preguntas con los once elementos del Diseño Centrado en Evidencia y validación estructural; HU-02 enviarlas a revisión con estados por color; HU-03 listar con paginación y filtros; HU-04 asignar de uno a tres revisores con notificación por correo; y la nueva, HU-05, en la que el revisor evalúa, deja observaciones y la pregunta queda aprobada o rechazada.»

### 2. Escenario de escalabilidad — 1:00 a 2:00 · Adrián

**Muestra:** sección 4 del documento, el script `pruebas-carga/escenario-escalabilidad.js` y `resultados/comparacion.md`.

**Dice:** «El estímulo son 500 usuarios concurrentes durante la convocatoria. Medimos con k6 la latencia p95 y la tasa de errores. El resultado esperado es p95 menor a 500 milisegundos y menos de 1 % de errores, y que al pasar de una a dos instancias de question-service la p95 se mantenga o baje sin tocar el código, solo con el perfil `escalado` del gateway. Aquí están los resultados medidos.» (Leer la fila de p95 de la tabla.)

### 3. Arquitectura C4 y UML — 2:00 a 4:00 · Iván

**Muestra:** sección 5: C4 nivel 1, nivel 2, nivel 3, diagrama de clases y diagrama de secuencia.

**Dice:**
- Nivel 1: «El sistema lo usan autores, administradores y revisores, y se apoya en un servidor de correo externo.»
- Nivel 2: «El frontend web está separado del backend y solo habla con el API Gateway por REST en `/api/v1`. Detrás están los seis microservicios, RabbitMQ y la base de datos propia de cada servicio que la necesita.»
- Nivel 3: «Dentro de question-service el controlador solo conoce la fachada, que coordina validación, ciclo de vida, repositorio y mensajería.»
- Secuencia: recorrer el flujo crear → enviar → asignar → evaluar → notificar.

### 4. Seis patrones GoF — 4:00 a 6:00 · Santiago

**Muestra:** sección 7 y, en IntelliJ, una clase por patrón (unos 15 segundos cada una).

| Patrón | Clase que se abre | Frase clave |
|---|---|---|
| Builder | `question-service/domain/QuestionBuilder` | «Arma paso a paso una pregunta de once elementos.» |
| Factory Method | `notification-service/email/EmailSenderFactory` | «Decide si el correo es simulado o SMTP sin tocar quien notifica.» |
| Facade | `question-service/application/QuestionAuthoringFacade` | «El controlador no conoce validación, ciclo de vida ni RabbitMQ.» |
| Decorator | `notification-service/email/LoggingEmailSenderDecorator` | «Agrega trazabilidad a cualquier emisor de correo.» |
| Strategy | `validation-service` reglas `ValidationRule` y `review-service` `UnanimousApprovalPolicy` | «Cada regla es una clase; agregar una no modifica las demás.» |
| Observer | consumidores de RabbitMQ en notification-service | «review-service publica eventos sin saber quién los escucha.» |

### 5. Bounded contexts — 6:00 a 7:00 · Adrián

**Muestra:** sección 6 y `arquitectura/diagramas/contextos.png`.

**Dice:** «Partimos de la Figura 1, pero separamos Validación y Ciclo de vida como contextos propios: las reglas Saber PRO cambian por su cuenta y el ciclo de vida es la única autoridad del estado. Cada contexto implementado tiene su microservicio, y ninguno lee la base de datos de otro: se comunican por REST o por eventos. Simulacros y Analítica quedan identificados para iteraciones futuras.»

### 6. Pruebas unitarias — 7:00 a 8:00 · Adrián

**Muestra:** terminal con `cd microservicios` y `..\mvnw.cmd test` (puede estar ya ejecutado para ahorrar tiempo) y la pestaña Actions de GitHub en verde.

**Dice:** «Cada microservicio tiene pruebas de dominio, servicio, repositorio con H2 y controlador. GitHub Actions las corre en cada push: el monolito, los microservicios y la verificación del frontend.»

### 7. Git y Scrum — 8:00 a 9:00 · Santiago

**Muestra:** commits en GitHub filtrados por autor (santyxswc, MrUrbano753, alexlopez1000), los Pull Requests #4 y #5, y el tablero del Sprint 2 en Jira.

**Dice:** «Trabajamos con ramas por funcionalidad y Pull Requests hacia main. Cada commit referencia su tarea de Jira. En el tablero se ven las historias del Sprint 2 con sus subtareas por integrante.»

### 8. Swagger — 9:00 a 10:00 · Iván

**Muestra:** http://localhost:8080/swagger-ui.html, cambiando el selector entre question-service y review-service. Ejecutar `GET /api/v1/preguntas` con «Try it out».

**Dice:** «El gateway reúne la documentación OpenAPI de todos los servicios en un solo Swagger. Cada endpoint describe sus códigos HTTP: 201 al crear, 400 por validación, 403 si el revisor no tiene la pregunta asignada y 404 si no existe.»

### 9. Software funcional — 10:00 a 15:00 · los tres

| Tiempo | Quién | Acción en http://localhost:5173 |
|---|---|---|
| 10:00–11:30 | Santiago (autor1) | Crea una pregunta con una opción vacía y muestra el error de validación. La corrige, la guarda en Borrador (gris), filtra el listado por competencia y la envía a revisión: queda Pendiente (naranja). Muestra en la consola de notification-service el aviso al administrador. |
| 11:30–12:30 | Iván (admin) | Abre la pregunta pendiente y le asigna dos revisores. La pregunta pasa a En revisión (azul). Muestra en la consola los dos correos de RevisorAsignado. |
| 12:30–14:00 | Adrián (revisor1 y revisor2) | revisor1 aprueba. revisor2 intenta rechazar sin observación y sale el mensaje «Debe registrar al menos una observación para rechazar»; escribe la observación y rechaza. La pregunta queda Rechazada (rojo) y salen los correos al autor y al administrador. |
| 14:00–14:45 | Santiago (autor1) | Ve la pregunta en rojo, lee las observaciones de cada revisor, la edita y la reenvía. |
| 14:45–15:00 | Adrián | Cierra: «Mostramos la fachada, el gateway y los consumidores de RabbitMQ funcionando de punta a punta. Gracias.» |

Si sobra tiempo en algún bloque, no se rellena: es mejor terminar en 14:30 que pasarse.

## Después de grabar

1. Subir a YouTube como **No listado**, sin acelerar.
2. Pegar el enlace en la sección 11 del documento (`DOCUMENTO_ARQUITECTURA_CORTE2.md`) y en el README.
3. Verificar que el video dure 15:00 o menos.
