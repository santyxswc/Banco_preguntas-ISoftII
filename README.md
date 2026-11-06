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
| 3 | Evolución de la solución distribuida | Arquitectura hexagonal, tres requisitos funcionales nuevos, más patrones GoF, autenticación y autorización | ⏳ Pendiente |

## Historias de usuario

Historias de la iteración 2 (corte 2), implementadas en los microservicios
y el frontend web. Los criterios de aceptación están en el
[documento de arquitectura](DOCUMENTO_ARQUITECTURA_CORTE2.md#1-historias-de-usuario-de-la-segunda-iteración).

| HU | Rol | Historia | Corte 1 (monolito) | Corte 2 (microservicios) |
|----|-----|----------|--------------------|--------------------------|
| HU-01 | Autor | Crear preguntas con validación estructural DCE | ✅ | ✅ question-service + validation-service |
| HU-02 | Autor | Enviar a revisión; estados con colores | ✅ | ✅ asíncrono por RabbitMQ (202 + operationId) |
| HU-03 | Autor | Listar con filtros y paginación, editar en Borrador/Rechazada | ✅ | ✅ |
| HU-04 | Administrador | Asignar entre 1 y 3 revisores con aviso por correo | ✅ | ✅ review-service + notification-service |
| HU-05 | Revisor | Evaluar las preguntas asignadas (aprobar/rechazar con observaciones) | — | ✅ nueva en el corte 2 |

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

### Corte 3 — Arquitectura hexagonal

Cada servicio se organiza en puertos y adaptadores, se agregan tres
requisitos funcionales nuevos de alta prioridad y se implementan la
autenticación y autorización por rol (RNF-06 a RNF-09).

## Estructura del repositorio

```
.
├── README.md                         Este archivo: visión general del proyecto
├── DOCUMENTO_ARQUITECTURA_CORTE2.md  Documento de arquitectura del corte 2 (entrega)
├── DOCUMENTO_ARQUITECTURA_CORTE2.pdf   El mismo documento en PDF con la plantilla institucional (entrega)
├── DOCUMENTO_ARQUITECTURA_CORTE2.docx  Fuente editable en Word del PDF
├── TALLER09/10/11_*.md               Documentación de los talleres de laboratorio
├── .github/workflows/                CI: pruebas del monolito, de los microservicios y del frontend
├── arquitectura/                     Contextos, contratos, C4, escalabilidad, guion del video y diagramas (.mmd/.png)
├── frontend/                         Frontend web (HTML + JS) que consume la API del gateway (:5173)
├── saberpro-domain/                  Taller 11 (DDD): dominio en Java puro, sin Spring ni JPA (solo JUnit 5)
├── banco-preguntas/                  Corte 1: monolito en capas (API REST; la GUI Swing se retiró)
└── microservicios/                   Corte 2: microservicios (Maven multi-módulo)
    ├── pom.xml                       POM padre (Java 21, Spring Boot 3.3.4, Spring Cloud 2023.0)
    ├── docker-compose.yml            RabbitMQ con consola de administración
    ├── shared-contracts/             DTO, comandos y eventos compartidos
    ├── api-gateway/                  Punto de entrada /api/v1, CORS, balanceo y Swagger unificado (:8080)
    ├── question-service/             Autoría de preguntas (:8081)
    ├── validation-service/           Validación estructural DCE (:8082)
    ├── user-service/                 Usuarios, roles y login (:8083)
    ├── lifecycle-service/            Estados y transiciones; consumidor RabbitMQ (:8084)
    ├── review-service/               Asignación y evaluación de revisores, JPA (:8085)
    ├── notification-service/         Notificaciones por eventos y correo (:8086)
    ├── pruebas/                      Script de prueba de entrega garantizada de eventos
    ├── pruebas-carga/                Prueba de carga k6 del escenario de escalabilidad y resultados medidos
    ├── lisw2_taller09_postman_collection.json  Colección de Postman
    ├── start-all.ps1                 Inicia RabbitMQ, los servicios, el gateway y el frontend
    └── stop-all.ps1                  Detiene todo
```


## Inicio rápido

Requisitos: **Java 21** (`JAVA_HOME` debe apuntar a un JDK 21; con Java 25
fallan las pruebas que usan Mockito), Maven, Node.js 18+ y Docker.

### Corte 2 — microservicios + frontend

```powershell
cd microservicios
mvn clean install          # compila y corre las pruebas
.\start-all.ps1            # RabbitMQ, 7 servicios y el frontend
```

- Frontend: http://localhost:5173 (usuarios de prueba: `admin`, `autor1`, `revisor1`, `revisor2`; ver `frontend/README.md`)
- Swagger unificado: http://localhost:8080/swagger-ui.html
- Consola de RabbitMQ: http://localhost:15672
- Detener todo: `.\stop-all.ps1`

Correo: por defecto se simula en la consola de notification-service
(`EMAIL_MODE=console`). Para enviar correos reales use `EMAIL_MODE=smtp` y
las variables `SPRING_MAIL_HOST`, `SPRING_MAIL_PORT`,
`SPRING_MAIL_USERNAME` y `SPRING_MAIL_PASSWORD`.

**Problema conocido:** si una versión anterior creó la cola
`reviewer.assigned.queue` sin la configuración de DLQ, los servicios
registran `PRECONDITION_FAILED` y los correos de asignación no salen.
Borre la cola y reinicie review-service y notification-service:

```bash
docker exec rabbitmq-taller09 rabbitmqctl delete_queue reviewer.assigned.queue
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

| Uso | Corte 1 (monolito) | Corte 2 (microservicios) |
|-----|--------------------|--------------------------|
| Lenguaje y build | Java 21, Maven | Java 21, Maven multi-módulo |
| Framework | — | Spring Boot 3.3, Spring Cloud Gateway 2023.0 |
| Interfaz | Swing (retirada en el corte 2) | Frontend web HTML + CSS + JavaScript (servidor Node) |
| Mensajería | Observer en memoria | RabbitMQ 3.13 (Spring AMQP) con DLQ |
| Base de datos | PostgreSQL 16 + Flyway, H2 para pruebas | Una base por servicio con Spring Data JPA + Flyway (H2 / PostgreSQL): question_db, review_db y user_db |
| Documentación de la API | — | springdoc-openapi (Swagger UI) |
| Pruebas | JUnit 5 | JUnit 5, Mockito, Spring Boot Test; k6 para carga |
| Correo | Jakarta Mail (simulado en consola) | Spring Mail (consola o SMTP) |
| Contenedores e integración continua | Docker Compose, GitHub Actions | Docker Compose, GitHub Actions |

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
