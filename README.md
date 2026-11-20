# Banco de Preguntas Saber Pro

[![Pruebas](https://github.com/santyxswc/Banco_preguntas-ISoftII/actions/workflows/pruebas.yml/badge.svg)](https://github.com/santyxswc/Banco_preguntas-ISoftII/actions/workflows/pruebas.yml)

Sistema para la gestión, validación y administración de un banco de
preguntas de selección múltiple con única respuesta, orientado a la
preparación de estudiantes de Ingeniería de Sistemas para las pruebas
**Saber Pro**.

Proyecto del curso **Ingeniería de Software II** — Universidad del
Cauca, Facultad de Ingeniería Electrónica y Telecomunicaciones, periodo
2026.2.

## Contenido

- [Inicio en un comando](#inicio-en-un-comando)
- [Entrega del corte 3](#entrega-del-corte-3)
- [Entrega del corte 2](#entrega-del-corte-2)
- [El problema](#el-problema)
- [Estado del proyecto](#estado-del-proyecto)
- [Historias de usuario](#historias-de-usuario)
- [Arquitectura por corte](#arquitectura-por-corte)
- [Estructura del repositorio](#estructura-del-repositorio)
- [Inicio rápido](#inicio-rápido)
- [Tecnologías](#tecnologías)
- [Forma de trabajo](#forma-de-trabajo)
- [Integrantes](#integrantes)

## Inicio en un comando

Solo se necesita Docker:

```bash
git clone https://github.com/santyxswc/Banco_preguntas-ISoftII.git
cd Banco_preguntas-ISoftII/microservicios
docker compose up -d --build     # 15 contenedores: frontend, gateway, 6 servicios, RabbitMQ, Keycloak y 5 PostgreSQL
```

| Qué | Dónde | Credenciales |
|---|---|---|
| **Aplicación web** | http://localhost:5173 | `autor1` / `autor123` · `admin` / `admin123` · `revisor1` / `revisor123` |
| API y Swagger | http://localhost:8080/swagger-ui.html | JWT de Keycloak |
| Keycloak (usuarios y roles) | http://localhost:8180 | `admin` / `admin` |
| RabbitMQ | http://localhost:15672 | `guest` / `guest` |

La primera construcción tarda unos minutos (descarga las dependencias de Maven). Si el puerto
8080 está ocupado: `GATEWAY_PORT=8088 docker compose up -d --build`. Para detener:
`docker compose down` (los datos quedan en los volúmenes; con `-v` se borran).

## Entrega del corte 3

| Entregable | Enlace |
|---|---|
| **Documento de arquitectura (PDF)** | [`DOCUMENTO_ARQUITECTURA_CORTE3.pdf`](DOCUMENTO_ARQUITECTURA_CORTE3.pdf) |
| Documento de arquitectura (Markdown, fuente) | [`DOCUMENTO_ARQUITECTURA_CORTE3.md`](DOCUMENTO_ARQUITECTURA_CORTE3.md) |
| RF 6 · Historial de revisiones y versión mejorada (HU-06) | [criterios de aceptación](DOCUMENTO_ARQUITECTURA_CORTE3.md#16-hu-06--ver-el-historial-de-revisiones-y-generar-una-versión-mejorada-autor--nueva-en-el-corte-3) |
| RNF 1 · JWT con Keycloak | [`arquitectura/08-seguridad-keycloak-jwt.md`](arquitectura/08-seguridad-keycloak-jwt.md) |
| RNF 2 · Arquitectura hexagonal en review-service | [`arquitectura/07-arquitectura-hexagonal.md`](arquitectura/07-arquitectura-hexagonal.md) |
| RNF 3 · Docker Compose con volúmenes | [`arquitectura/09-docker-compose.md`](arquitectura/09-docker-compose.md) |
| Colección de Postman (usuarios y roles en Keycloak, 401/403 y flujo HU-01 a HU-06) | [`microservicios/banco-preguntas-corte3.postman_collection.json`](microservicios/banco-preguntas-corte3.postman_collection.json) |
| Tablero Scrum (Jira, proyecto SCRUM) | https://ingsoft2bancodepreguntas.atlassian.net |
| Sustentación del corte 3 | Presencial, con todos los integrantes (no requiere video) |

## Entrega del corte 2

| Entregable | Enlace |
|---|---|
| Video de sustentación (YouTube) | https://youtu.be/B-chmKrIsbY |
| **Documento de arquitectura (PDF para entregar, formato de la plantilla institucional)** | [`DOCUMENTO_ARQUITECTURA_CORTE2.pdf`](DOCUMENTO_ARQUITECTURA_CORTE2.pdf) |
| Documento de arquitectura (Word editable, fuente del PDF) | [`DOCUMENTO_ARQUITECTURA_CORTE2.docx`](DOCUMENTO_ARQUITECTURA_CORTE2.docx) |
| Documento de arquitectura (Markdown) | [`DOCUMENTO_ARQUITECTURA_CORTE2.md`](DOCUMENTO_ARQUITECTURA_CORTE2.md) |
| Repositorio Git | https://github.com/santyxswc/Banco_preguntas-ISoftII |
| Resultados de la prueba de carga (k6, 1 y 2 instancias) | [`microservicios/pruebas-carga/resultados/comparacion.md`](microservicios/pruebas-carga/resultados/comparacion.md) |
| Tablero del Sprint 2 (Jira, proyecto SCRUM) | https://ingsoft2bancodepreguntas.atlassian.net · capturas en [`arquitectura/diagramas/sprint2-jira-lista.png`](arquitectura/diagramas/sprint2-jira-lista.png) y [`sprint2-jira-resumen.png`](arquitectura/diagramas/sprint2-jira-resumen.png) |

## El problema

En muchas instituciones las preguntas de preparación para Saber Pro las
escribe cada docente por su cuenta, sin criterios comunes de redacción
ni revisión. El resultado son preguntas ambiguas, distractores poco
plausibles, errores conceptuales y ninguna trazabilidad de los cambios.

Este sistema cubre el ciclo de vida completo de una pregunta: creación,
validación estructural automática, revisión por pares, aprobación,
publicación y uso en simulacros, con seguimiento del desempeño de los
estudiantes. Contempla cinco perfiles: **administrador**, **autor**,
**revisor**, **docente** y **estudiante**.

## Estado del proyecto

El proyecto se desarrolla en tres iteraciones, una por corte:

| Corte | Iteración | Estilo arquitectónico | Estado |
|-------|-----------|-----------------------|--------|
| 1 | Monolito en capas | 3 capas + MVC, principios SOLID y patrones GoF | ✅ Entregado |
| 2 | Refactorización distribuida | Microservicios + arquitectura orientada a eventos, frontend web separado por API REST | ✅ Implementado ([documento de arquitectura](DOCUMENTO_ARQUITECTURA_CORTE2.md)) |
| 3 | Evolución de la solución distribuida | Arquitectura hexagonal en review-service, HU-06, autenticación y autorización con JWT y Keycloak, y despliegue con Docker Compose | ✅ Implementado ([documento de arquitectura](DOCUMENTO_ARQUITECTURA_CORTE3.md)) |

## Historias de usuario

Historias de las tres iteraciones, implementadas en los microservicios
y el frontend web. Los criterios de aceptación están en el
[documento de arquitectura](DOCUMENTO_ARQUITECTURA_CORTE3.md#1-historias-de-usuario-tres-iteraciones).

| HU | Rol | Historia | Corte 1 (monolito) | Corte 2 (microservicios) |
|----|-----|----------|--------------------|--------------------------|
| HU-01 | Autor | Crear preguntas con validación estructural DCE | ✅ | ✅ question-service + validation-service |
| HU-02 | Autor | Enviar a revisión; estados con colores | ✅ | ✅ asíncrono por RabbitMQ (202 + operationId) |
| HU-03 | Autor | Listar con filtros y paginación, editar en Borrador/Rechazada | ✅ | ✅ |
| HU-04 | Administrador | Asignar entre 1 y 3 revisores con aviso por correo | ✅ | ✅ review-service + notification-service |
| HU-05 | Revisor | Evaluar las preguntas asignadas (aprobar/rechazar con observaciones) | — | ✅ nueva en el corte 2 |
| HU-06 | Autor | Ver el historial de revisiones por ronda y generar una versión mejorada que vuelve a revisión, con aviso a los implicados | — | ✅ nueva en el corte 3 (review-service + question-service) |

Simulacros, seguimiento académico y administración/trazabilidad quedan
para iteraciones posteriores.

Estados de una pregunta: *Borrador → Pendiente de revisión → En
revisión → Aprobada / Rechazada → Publicada → Archivada*. Ninguna
pregunta se elimina físicamente (RNF-16).

## Arquitectura por corte

### Corte 1 — Monolito en capas

Aplicación de escritorio en Java Swing organizada en capas:

```
presentation   Vistas Swing y controladores MVC
     │
     ▼
domain         Entidades, servicios, reglas de validación, interfaces de repositorio
     ▲
     │ implementa
access         Repositorios en memoria y JDBC (PostgreSQL / H2 + Flyway)

infra          Eventos de dominio, Observer y envío de correo
app            Punto de entrada: arma las dependencias y abre el login
```

El dominio no depende de la persistencia (inversión de dependencias) y
las vistas no contienen lógica de negocio. Patrones aplicados: Builder,
Factory Method, Singleton, Facade, Decorator, Strategy, Observer y State.

La interfaz Swing se retiró en el corte 2 (HU-T7.4): sus pantallas ya
existen en el frontend web, y el monolito queda como backend con API REST.

El detalle de clases, patrones y pruebas está en
[`banco-preguntas/README.md`](banco-preguntas/README.md).

### Corte 2 — Microservicios y eventos

El monolito se refactorizó en servicios independientes para cumplir el
escenario de escalabilidad (RNF-17 y RNF-18: 500 usuarios concurrentes
con latencia p95 < 500 ms, y crecer añadiendo instancias sin tocar la
lógica de negocio).

```
Frontend web (:5173) ──REST JSON──▶ API Gateway (:8080) ──▶ user · question · validation
                                                            lifecycle · review · notification
question ──AMQP──▶ RabbitMQ ──▶ lifecycle          review ──AMQP──▶ RabbitMQ ──▶ notification ──▶ SMTP
```

- **Frontend y backend separados:** el frontend (`frontend/`) solo conoce
  la URL del gateway y consume la API REST `/api/v1/*`.
- **Comunicación síncrona (REST)** para validar y consultar estados, y
  **asíncrona (RabbitMQ)** para enviar a revisión y notificar, con colas
  durables, DLQ y consumidores idempotentes.
- **Patrones GoF en los microservicios:** Builder, Factory Method, Facade,
  Decorator, Adapter, Strategy y Observer.
- **Swagger unificado** en el gateway.

Documentación de esta iteración:

| Documento | Contenido |
|---|---|
| [`DOCUMENTO_ARQUITECTURA_CORTE2.md`](DOCUMENTO_ARQUITECTURA_CORTE2.md) / [`.pdf`](DOCUMENTO_ARQUITECTURA_CORTE2.pdf) / [`.docx`](DOCUMENTO_ARQUITECTURA_CORTE2.docx) | Documento de entrega con la estructura de la plantilla institucional: HU con criterios de aceptación, Sprint 2 en Jira, escalabilidad, C4 + UML, contextos, patrones, API, pruebas, enlaces del video y del repositorio |
| [`arquitectura/`](arquitectura/README.md) | Contextos delimitados, contratos REST/eventos, C4 de contenedores, escenario de escalabilidad y entrega garantizada |
| [`frontend/README.md`](frontend/README.md) | Pantallas por rol y cómo ejecutar el frontend |
| [`TALLER09_DOCUMENTACION.md`](TALLER09_DOCUMENTACION.md) | Taller 09: comunicación síncrona y asíncrona, colección de Postman |
| [`TALLER10_JPA_DOCUMENTACION.md`](TALLER10_JPA_DOCUMENTACION.md) | Taller 10: persistencia limpia con JPA |
| [`TALLER11_DDD_DOCUMENTACION.md`](TALLER11_DDD_DOCUMENTACION.md) | Taller 11: DDD estratégico y táctico |

### Corte 3 — Arquitectura limpia, seguridad y contenedores

```
Navegador ─▶ Frontend (:5173) ─ JWT ─▶ API Gateway (:8080) ─ X-User-* ─▶ microservicios ─▶ PostgreSQL (un volumen por base)
                  │  usuario y clave        │ valida firma, emisor y vigencia;
                  └────────────────────────▶│ autoriza por rol
                                            ▼
                                   Keycloak (:8180) ◀── Postman (usuarios y roles)
```

- **Arquitectura hexagonal en review-service (RNF 2):** dominio, aplicación,
  infraestructura y presentación; cinco puertos con sus adaptadores y una
  prueba (`ArquitecturaHexagonalTest`) que falla si el dominio depende de
  un framework.
- **HU-06 (RF 6):** el dominio agrupa las revisiones en rondas
  (`ReviewRound`, `ReviewHistory`); el autor ve el historial y, si la
  pregunta fue rechazada, crea la versión mejorada que vuelve a revisión.
- **JWT con Keycloak (RNF 1):** Keycloak autentica y emite los tokens; el
  gateway los valida, autoriza por rol (401/403) y propaga la identidad.
  Ningún microservicio guarda contraseñas. Los usuarios y roles se
  gestionan por Postman.
- **Docker Compose (RNF 3):** un Dockerfile multi-etapa para los siete
  servicios y una base PostgreSQL por contexto, cada una con su volumen.
  Solo se publican el frontend, el gateway, Keycloak y RabbitMQ.
- **Pruebas:** 160 pruebas en los microservicios (114 en el corte 2); el
  CI también valida el `docker-compose.yml` y construye las imágenes.

| Documento | Contenido |
|---|---|
| [`DOCUMENTO_ARQUITECTURA_CORTE3.md`](DOCUMENTO_ARQUITECTURA_CORTE3.md) / [`.pdf`](DOCUMENTO_ARQUITECTURA_CORTE3.pdf) | Documento de entrega: HU de las tres iteraciones, sprints, escenarios de modificabilidad y escalabilidad, C4 de las iteraciones 2 y 3, patrones, pruebas y evidencia de Keycloak y Docker Compose |
| [`arquitectura/07-arquitectura-hexagonal.md`](arquitectura/07-arquitectura-hexagonal.md) | Capas, regla de dependencias y tabla puerto → adaptador |
| [`arquitectura/08-seguridad-keycloak-jwt.md`](arquitectura/08-seguridad-keycloak-jwt.md) | Flujo del JWT, reglas por rol, realm y gestión de usuarios con Postman |
| [`arquitectura/09-docker-compose.md`](arquitectura/09-docker-compose.md) | Contenedores, volúmenes y evidencia de persistencia |

## Estructura del repositorio

```
.
├── README.md                         Este archivo: visión general del proyecto
├── DOCUMENTO_ARQUITECTURA_CORTE3.md  Documento de arquitectura del corte 3 (entrega) y su PDF
├── DOCUMENTO_ARQUITECTURA_CORTE2.md  Documento de arquitectura del corte 2 (entrega)
├── DOCUMENTO_ARQUITECTURA_CORTE2.pdf   El mismo documento en PDF con la plantilla institucional (entrega)
├── DOCUMENTO_ARQUITECTURA_CORTE2.docx  Fuente editable en Word del PDF
├── TALLER09/10/11_*.md               Documentación de los talleres de laboratorio
├── .github/workflows/                CI: pruebas del monolito, de los microservicios y del frontend
├── arquitectura/                     Contextos, contratos, C4, escalabilidad, guion del video y diagramas (.mmd/.png)
├── frontend/                         Frontend web (HTML + JS) que consume la API del gateway (:5173)
├── saberpro-domain/                  Taller 11 (DDD): dominio en Java puro, sin Spring ni JPA (solo JUnit 5)
├── banco-preguntas/                  Corte 1: monolito en capas (API REST; la GUI Swing se retiró)
└── microservicios/                   Cortes 2 y 3: microservicios (Maven multi-módulo)
    ├── pom.xml                       POM padre (Java 21, Spring Boot 3.3.4, Spring Cloud 2023.0)
    ├── docker-compose.yml            Todo el sistema: servicios, PostgreSQL con volúmenes, RabbitMQ y Keycloak
    ├── Dockerfile                    Imagen multi-etapa común a los siete servicios
    ├── keycloak/                     Realm banco-preguntas que se importa al arrancar (roles, cliente, usuarios)
    ├── shared-contracts/             DTO, comandos y eventos compartidos
    ├── api-gateway/                  Punto de entrada /api/v1: valida el JWT, autoriza por rol, CORS y Swagger unificado (:8080)
    ├── question-service/             Autoría de preguntas (:8081)
    ├── validation-service/           Validación estructural DCE (:8082)
    ├── user-service/                 Perfiles y roles sincronizados con Keycloak (:8083)
    ├── lifecycle-service/            Estados y transiciones, JPA; consumidor RabbitMQ (:8084)
    ├── review-service/               Asignación, evaluación e historial por rondas; arquitectura hexagonal, JPA (:8085)
    ├── notification-service/         Notificaciones por eventos y correo (:8086)
    ├── pruebas/                      Script de prueba de entrega garantizada de eventos
    ├── pruebas-carga/                Prueba de carga k6 del escenario de escalabilidad y resultados medidos
    ├── banco-preguntas-corte3.postman_collection.json  Postman del corte 3: Keycloak, JWT y HU-01 a HU-06
    ├── lisw2_taller09_postman_collection.json  Colección de Postman del taller 09
    ├── start-all.ps1                 Inicia RabbitMQ y Keycloak (Docker) y los servicios, el gateway y el frontend (Maven)
    └── stop-all.ps1                  Detiene todo
```


## Inicio rápido

### Con Docker (recomendado)

Ver [Inicio en un comando](#inicio-en-un-comando). Solo requiere Docker.

### Cortes 2 y 3 — microservicios con Maven

Requisitos: **Java 21** (`JAVA_HOME` debe apuntar a un JDK 21; con Java 25
fallan las pruebas que usan Mockito), Maven, Node.js 18+ y Docker (para
RabbitMQ y Keycloak).

```powershell
cd microservicios
mvn clean install          # compila y corre las 160 pruebas
.\start-all.ps1            # RabbitMQ y Keycloak en Docker; 7 servicios y el frontend con Maven
```

En Linux o macOS: `docker compose up -d rabbitmq keycloak` y luego
`mvn -pl <servicio> spring-boot:run` por cada servicio y `node server.mjs`
en `frontend/`.

- Frontend: http://localhost:5173 (usuarios de prueba: `admin`, `autor1`, `revisor1`, `revisor2`; ver `frontend/README.md`)
- Swagger unificado: http://localhost:8080/swagger-ui.html
- Keycloak: http://localhost:8180 (`admin` / `admin`)
- Consola de RabbitMQ: http://localhost:15672
- Detener todo: `.\stop-all.ps1`

Obtener un token y llamar a la API:

```bash
TOKEN=$(curl -s -X POST http://localhost:8080/api/v1/auth/token -d grant_type=password \
  -d client_id=banco-preguntas-frontend -d username=autor1 -d password=autor123 | jq -r .access_token)
curl http://localhost:8080/api/v1/preguntas -H "Authorization: Bearer $TOKEN"
```

Correo: por defecto se simula en la consola de notification-service
(`EMAIL_MODE=console`). Para enviar correos reales use `EMAIL_MODE=smtp` y
las variables `SPRING_MAIL_HOST`, `SPRING_MAIL_PORT`,
`SPRING_MAIL_USERNAME` y `SPRING_MAIL_PASSWORD`.

**Problema conocido:** si una versión anterior creó la cola
`reviewer.assigned.queue` sin la configuración de DLQ, los servicios
registran `PRECONDITION_FAILED` y los correos de asignación no salen.
Borre la cola y reinicie review-service y notification-service:

```bash
docker exec bp-rabbitmq rabbitmqctl delete_queue reviewer.assigned.queue
# o, para empezar desde cero: docker compose down -v
```

### Taller 11 — dominio DDD en Java puro

```bash
cd saberpro-domain
mvn test     # 22 pruebas JUnit 5; el módulo no depende de Spring ni de JPA
```

### Corte 1 — monolito

```bash
cd banco-preguntas
docker compose up -d     # base de datos (opcional)
mvn clean install
mvn spring-boot:run
```

Si no hay base de datos disponible, la aplicación guarda las preguntas
en memoria. Para correr las pruebas:

```bash
mvn test
```

Usuarios de prueba, variables de entorno y configuración del correo:
ver [`banco-preguntas/README.md`](banco-preguntas/README.md#ejecución).

## Tecnologías

| Uso | Corte 1 (monolito) | Cortes 2 y 3 (microservicios) |
|-----|--------------------|--------------------------|
| Lenguaje y build | Java 21, Maven | Java 21, Maven multi-módulo |
| Framework | — | Spring Boot 3.3, Spring Cloud Gateway 2023.0 |
| Interfaz | Swing (retirada en el corte 2) | Frontend web HTML + CSS + JavaScript (servidor Node) |
| Mensajería | Observer en memoria | RabbitMQ 3.13 (Spring AMQP) con DLQ |
| Base de datos | PostgreSQL 16 + Flyway, H2 para pruebas | Una base por servicio con Spring Data JPA + Flyway (H2 / PostgreSQL): question_db, lifecycle_db, review_db y user_db |
| Seguridad | Usuarios en memoria | Keycloak 26 (OpenID Connect, JWT RS256) + Spring Security OAuth2 Resource Server en el gateway |
| Documentación de la API | — | springdoc-openapi (Swagger UI) |
| Pruebas | JUnit 5 | JUnit 5, Mockito, Spring Boot Test; k6 para carga |
| Correo | Jakarta Mail (simulado en consola) | Spring Mail (consola o SMTP) |
| Contenedores e integración continua | Docker Compose, GitHub Actions | Dockerfile multi-etapa, Docker Compose con volúmenes, GitHub Actions |

## Forma de trabajo

- Cada funcionalidad se trabaja en su propia rama y entra a `main`
  mediante merge o pull request.
- Los mensajes de commit se escriben en español y describen el cambio
  en infinitivo o presente ("Guardar las preguntas en PostgreSQL").

## Integrantes

- Yeferson Santiago Caicedo Orozco
- Iván Alexander López Lasso
- Adrián Camilo Araujo Urbano
- Carlos Arturo Bambague

**Docentes:** Paola Andrea Bedoya Toro, Libardo Pantoja

Universidad del Cauca — Ingeniería de Software II, 2026.2
