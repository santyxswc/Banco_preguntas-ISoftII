# Banco de Preguntas Saber Pro

Sistema para la gestión, validación y administración de un banco de
preguntas de selección múltiple con única respuesta, orientado a la
preparación de estudiantes de Ingeniería de Sistemas para las pruebas
**Saber Pro**.

Proyecto del curso **Ingeniería de Software II** — Universidad del
Cauca, Facultad de Ingeniería Electrónica y Telecomunicaciones, periodo
2026.2.

## Contenido

- [El problema](#el-problema)
- [Estado del proyecto](#estado-del-proyecto)
- [Historias de usuario](#historias-de-usuario)
- [Arquitectura por corte](#arquitectura-por-corte)
- [Estructura del repositorio](#estructura-del-repositorio)
- [Inicio rápido](#inicio-rápido)
- [Tecnologías](#tecnologías)
- [Forma de trabajo](#forma-de-trabajo)
- [Integrantes](#integrantes)

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
| 2 | Refactorización distribuida | Microservicios + arquitectura orientada a eventos, para escalabilidad | 🚧 En curso |
| 3 | Evolución de la solución distribuida | Arquitectura hexagonal, tres requisitos funcionales nuevos, más patrones GoF, autenticación y autorización | ⏳ Pendiente |

## Historias de usuario

| HU | Nombre | Corte 1 |
|----|--------|---------|
| HU01 | Gestión de usuarios y roles | Login con roles (usuarios de prueba) |
| HU02 | Gestión del banco de preguntas | ✅ Crear, editar en borrador, listar con filtros, orden y paginación |
| HU03 | Validación estructural | ✅ Contexto, pregunta única, 4 opciones, una respuesta correcta, sin "Todas/Ninguna de las anteriores", longitud similar |
| HU04 | Ciclo de vida de la pregunta | ✅ Estados con colores y transiciones válidas |
| HU05 | Revisión por pares | Parcial: asignación de revisores con notificación por correo; el autor no puede revisar su propia pregunta |
| HU06 | Simulacros | — |
| HU07 | Seguimiento académico | — |
| HU08 | Administración y trazabilidad | — |

Estados de una pregunta: *Borrador → Pendiente de revisión → En
revisión → Aprobada / Rechazada → Publicada → Archivada*. Ninguna
pregunta se elimina físicamente (RNF-16).

## Arquitectura por corte

### Corte 1 — Monolito en capas (actual)

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
las vistas no contienen lógica de negocio. Patrones aplicados:
**Observer**, **Strategy**, **Builder**, **State**, **Repository** y
**Publicador/Suscriptor**.

El detalle de clases, patrones y pruebas está en
[`banco-preguntas/README.md`](banco-preguntas/README.md).

### Corte 2 — Microservicios y eventos (siguiente)

Se refactoriza el monolito en servicios independientes que se comunican
por eventos, para cumplir los requisitos de escalabilidad (RNF-17 y
RNF-18: 500 usuarios concurrentes y crecimiento añadiendo instancias
sin tocar la lógica de negocio). Los eventos de dominio que ya existen
en el corte 1 (por ejemplo, la asignación de revisores) son el punto de
partida para separar los servicios.

### Corte 3 — Arquitectura hexagonal

Cada servicio se organiza en puertos y adaptadores, se agregan tres
requisitos funcionales nuevos de alta prioridad y se implementan la
autenticación y autorización por rol (RNF-06 a RNF-09).

## Estructura del repositorio

```
.
├── README.md               Este archivo: visión general del proyecto
└── banco-preguntas/        Corte 1: aplicación monolítica (Maven)
    ├── README.md           Detalle técnico, ejecución y usuarios de prueba
    ├── docker-compose.yml  PostgreSQL para desarrollo
    ├── pom.xml
    └── src/
        ├── main/java/co/unicauca/iso2/bancopreguntas/
        │   ├── app/  presentation/  domain/  access/  infra/
        └── main/resources/db/  migraciones y datos de prueba (Flyway)
```

Los documentos del curso (enunciado, informe de arquitectura) no se
versionan.

## Inicio rápido

Requisitos: Java 21, Maven y, opcionalmente, Docker para PostgreSQL.

```bash
cd banco-preguntas
docker compose up -d     # base de datos (opcional)
mvn clean install
mvn exec:java
```

Si no hay base de datos disponible, la aplicación guarda las preguntas
en memoria. Para correr las pruebas:

```bash
mvn test
```

Usuarios de prueba, variables de entorno y configuración del correo:
ver [`banco-preguntas/README.md`](banco-preguntas/README.md#ejecución).

## Tecnologías

| Uso | Tecnología |
|-----|------------|
| Lenguaje y build | Java 21, Maven |
| Interfaz | Swing |
| Base de datos | PostgreSQL 16, migraciones con Flyway, H2 para pruebas |
| Pruebas | JUnit 5 |
| Correo | Jakarta Mail (simulado en consola por defecto) |
| Contenedores | Docker / Docker Compose |

## Forma de trabajo

- Cada funcionalidad se trabaja en su propia rama y entra a `main`
  mediante merge o pull request.
- Los mensajes de commit se escriben en español y describen el cambio
  en infinitivo o presente ("Guardar las preguntas en PostgreSQL").

## Integrantes

- Santiago Caicedo
- Ivan Alexander Lopez
- Adrian Araujo Urbano
- Carlos Bambague

**Docentes:** Wilson Pantoja Yepez, Paola Bedoya

Universidad del Cauca — Ingeniería de Software II, 2026.2
