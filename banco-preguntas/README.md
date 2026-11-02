# Banco de Preguntas Saber Pro — Iteración 1

Aplicación en Java para gestionar un banco de
preguntas de selección múltiple con única respuesta para la preparación
de las pruebas Saber Pro. Proyecto del curso Ingeniería de Software II,
Universidad del Cauca (2026.2).

Primera iteración: arquitectura **monolítica en 3 capas** con el
micro patrón **MVC**, principios **SOLID** y patrones **GoF**.

En el corte 2 se retiró la interfaz de escritorio Swing (HU-T7.4): sus
pantallas se migraron al frontend web (`frontend/`), que consume la API
REST. Este módulo queda como backend con los controladores de
`presentation/rest`.

## Historias de usuario implementadas

| HU | Descripción | Dónde |
|----|-------------|-------|
| HU1 | El autor crea preguntas con contexto, pregunta directa, 4 opciones, respuesta correcta, justificación, bibliografía, competencia, tema, subtema y nivel. Al guardar se aplica la validación estructural (HU03). | `PreguntaRestController`, `QuestionBuilder`, `ValidadorEstructuralPregunta` |
| HU2 | El autor pasa sus preguntas de *Borrador* a *Pendiente de revisión*. Los estados se ven con colores. | `PreguntaRestController`, `QuestionService.enviarARevision`, `EstadoPregunta` |
| HU3 | El autor lista sus preguntas con filtros, orden y paginación, y edita las que siguen en borrador. | `PreguntaRestController`, `QuestionFilter`, `PaginaResultado` |
| HU4 | El administrador asigna uno o más revisores a preguntas pendientes y el sistema les envía un correo. | `AsignacionRestController`, `AsignacionService`, `EmailNotificacionService` |

Validación estructural (HU03): contexto, única pregunta directa,
exactamente 4 opciones, una sola respuesta correcta, opciones no vacías
ni repetidas, prohibido "Todas/Ninguna de las anteriores" y longitud
similar entre opciones.

## Arquitectura

```
presentation   Controladores REST (presentation/rest) y DTO
     │
     ▼
domain         Entidades, servicios, interfaces de repositorio, validación
     ▲
     │ implementa
access         Repositorios en memoria y JDBC (PostgreSQL / H2 + Flyway)

infra          Observer/Subject, eventos de dominio, configuración y envío de correo
```

- `domain` no depende de `access`: los servicios usan interfaces
  (`QuestionRepository`, `UsuarioRepository`, `AsignacionRepository`) y
  la configuración de Spring inyecta las implementaciones (DIP).
- Los controladores REST no tienen lógica de negocio: delegan en los
  servicios del dominio.

### Patrones de diseño

| Patrón | Clases | Uso |
|--------|--------|-----|
| Observer | `Subject`, `Observer`, `QuestionService` | Avisa a los suscriptores cuando cambia una pregunta (en el corte 1 refrescaba las vistas Swing). |
| Strategy | `ReglaValidacion`, `ValidadorEstructuralPregunta` | Reglas de validación intercambiables sin tocar el servicio. |
| Builder | `QuestionBuilder` | Construcción de preguntas con muchos campos y valores por defecto. |
| State (simplificado) | `EstadoPregunta` | Cada estado conoce su color, si permite edición y sus transiciones válidas. |
| Repository | `*Repository`, `*ImplRepository`, `QuestionJdbcRepository` | Aísla la persistencia del dominio. |
| Publicador/Suscriptor | `PreguntaEventPublisher`, `RevisorAsignadoEvent`, `EmailNotificacionService` | El correo a revisores queda desacoplado de la asignación. |

## Tecnologías

- Java 21, Maven, Spring Boot
- JUnit 5
- PostgreSQL + Flyway (H2 en memoria para pruebas)
- Jakarta Mail

## Ejecución

```
mvn clean install
mvn spring-boot:run
```

La API queda en `http://localhost:8080` y la interfaz es el frontend web
de la carpeta `frontend/`.

Usuarios de prueba:

| Rol | Correo | Contraseña |
|-----|--------|------------|
| Administrador | admin@unicauca.edu.co | admin123 |
| Autor | autor1@unicauca.edu.co | autor123 |
| Autor | autor2@unicauca.edu.co | autor123 |
| Autor | autor3@unicauca.edu.co | autor123 |
| Autor | autor4@unicauca.edu.co | autor123 |

### Base de datos

Las preguntas se guardan en PostgreSQL. Para levantarlo con Docker:

```
docker compose up -d
```

o, si Docker no tiene el plugin `compose`:

```
docker run -d --name banco-preguntas-db -e POSTGRES_DB=banco_preguntas \
  -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres -p 5432:5432 \
  -v banco-preguntas-data:/var/lib/postgresql/data postgres:16
```

Al iniciar, la aplicación crea las tablas, los usuarios y las preguntas
de prueba con Flyway. La conexión se configura con `DB_HOST`, `DB_PORT`, `DB_NAME`,
`DB_USER` y `DB_PASSWORD` (por defecto `localhost:5432/banco_preguntas`,
usuario y contraseña `postgres`). Si no hay conexión, la aplicación
avisa en consola y guarda las preguntas en memoria. Usuarios y
asignaciones siguen en memoria en esta iteración.

El correo se simula en consola; para enviarlo de verdad se configuran las variables
`SMTP_HOST`, `SMTP_PORT`, `SMTP_USER`, `SMTP_PASSWORD` y `SMTP_FROM`.

## Pruebas

```
mvn test
```

Hay pruebas unitarias para las entidades, servicios y la validación del
dominio, y pruebas de integración del repositorio JDBC sobre H2. GitHub
Actions las corre en cada push y pull request a `main`.

## Integrantes

- Yeferson Santiago Caicedo Orozco
- Ivan Alexander Lopez
- Adrian Araujo Urbano
- Carlos Bambague

## Docentes

- Paola Andrea Bedoya Toro
- Libardo Pantoja
