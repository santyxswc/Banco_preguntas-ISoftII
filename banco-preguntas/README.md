# Banco de Preguntas Saber PRO — Arquitectura en capas + Micro patrón MVC + Observer

Aplicación de escritorio monolítica en Java (Swing) para gestionar un
banco de preguntas de preparación para las pruebas Saber Pro,
desarrollada para el Taller de Patrón en Capas y Micro Patrón MVC —
Laboratorio de Ingeniería de Software II, Universidad del Cauca
(2026.2).

## Tecnologías

- Java 21
  igual que el proyecto de referencia)
- Maven
- Swing
- JUnit 5

## Arquitectura en capas

```
presentation
 ├── GUIQuestions     (ventana principal: selector + formulario)
 ├── GUIObserver1      (vista de estadísticas)
 └── GUIObserver2      (vista gráfica de pastel)
        │
        ▼
domain
 ├── Question
 ├── QuestionDistractors
 ├── QuestionService   (reglas de negocio + Subject)
 └── QuestionRepository (abstracción de persistencia)
        │
        ▼
access
 └── QuestionImplRepository (persistencia en memoria)

infra (capa transversal)
 ├── Observer
 └── Subject
```

- **presentation**: interacción con el usuario (Swing).
- **domain**: entidades, reglas de negocio y el servicio de la
  entidad `Question`.
- **access**: persistencia. Se usa una estructura en memoria (`Map`)
  tal como lo permite el enunciado del taller — no es una base de
  datos relacional.
- **infra**: lógica transversal a las demás capas — en este caso, el
  patrón Observer (`Observer` y `Subject`), reutilizable por
  cualquier clase del dominio que necesite notificar cambios.

`QuestionService` depende de la interfaz `QuestionRepository` (no de
`QuestionImplRepository`), aplicando el mismo Principio de Inversión
de Dependencias del ejemplo de la teoría.

## Micro patrón MVC + patrón Observer

- **Modelo**: `Question`, `QuestionDistractors` y `QuestionService`
  (que además hace de **Subject**).
- **Vista/Controlador**: `GUIQuestions` es la ventana activa: permite
  elegir una pregunta, ver su formulario y cambiar su estado. Al
  llamar `QuestionService.updateEstado(...)`, delega la regla de
  negocio en el servicio (rol de controlador) y refresca su propio
  formulario (rol de vista).
- **Vistas pendientes del cambio de estado (Observer)**:
  `GUIObserver1` (estadísticas por estado) y `GUIObserver2` (gráfica
  de pastel) implementan `Observer` y se registran ante
  `QuestionService` (`attach(this)`) en su propio constructor. Tan
  pronto `QuestionService.updateEstado(...)` cambia el estado de una
  pregunta, llama a `notifyObservers()` (heredado de `Subject`), y
  ambas vistas se enteran solas y se renderizan de nuevo — sin que
  `GUIQuestions` las conozca ni las llame directamente.

```
GUIQuestions ──actualiza estado──▶ QuestionService (Subject)
                                          │
                                notifyObservers()
                                   ┌──────┴──────┐
                                   ▼             ▼
                            GUIObserver1   GUIObserver2
                            (estadísticas) (gráfica de pastel)
```

`ClientMain` es el composition root: crea `QuestionImplRepository`,
lo inyecta en `QuestionService`, y construye las tres ventanas
compartiendo la misma instancia del servicio.

## Datos de ejemplo

`QuestionImplRepository` se inicializa con 10 preguntas de ejemplo de
distintas competencias Saber Pro (lectura crítica, razonamiento
cuantitativo, competencias ciudadanas, comunicación escrita, inglés),
repartidas entre los tres estados (`Borrador`, `Pendiente de
revisión`, `Eliminada`) para poder ver de inmediato las vistas de
estadísticas y de gráfica al ejecutar la aplicación.

## Pruebas

Pruebas unitarias con un doble de prueba (`FakeQuestionRepository`),
sin depender de `QuestionImplRepository`, incluyendo una prueba de
que `updateEstado` notifica correctamente a los observadores
registrados:

```
mvn test
```

## Ejecución

```
mvn clean install
mvn exec:java
```

Al iniciar se abren tres ventanas: la ventana principal de gestión de
preguntas, la vista de estadísticas y la vista gráfica. Cambia el
estado de una pregunta desde la ventana principal y observa cómo las
otras dos se actualizan automáticamente.

## Integrantes

- Yeferson Santiago Caicedo Orozco
- Adrian Araujo Urbano
- Ivan Alexander Lopez Lasso
- Carlos Arturo Bambague Martinez

## Docente

- Wilson Pantoja Yepez, Paola Bedoya
