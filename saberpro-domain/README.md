# saberpro-domain — Taller 11 (DDD)

Módulo del dominio del Banco de Preguntas Saber Pro en **Java puro (Java 17+)**.
No hereda de Spring Boot ni depende de JPA: su `pom.xml` solo declara **JUnit 5** con alcance `test`.

```
co.edu.unicauca.saberpro.domain/
├── model/
│   ├── Pregunta.java          AGGREGATE ROOT (invariantes de envío y ciclo de vida)
│   ├── Enunciado.java         VALUE OBJECT (no nulo, >= 10 caracteres)
│   ├── Justificacion.java     VALUE OBJECT (>= 15 caracteres, sin palabras prohibidas)
│   ├── OpcionRespuesta.java   VALUE OBJECT (texto + indicador de clave)
│   ├── Revisor.java           ENTITY (identidad: ID de docente)
│   ├── Revision.java          ENTITY (identidad: ID de evaluación)
│   ├── EstadoPregunta.java    ENUM (BORRADOR, PENDIENTE_REVISION, EN_REVISION, APROBADA, RECHAZADA)
│   └── Dictamen.java          ENUM (APROBADA, RECHAZADA)
├── exception/
│   ├── DomainException.java
│   └── ReglaNegocioException.java
└── repository/
    ├── PreguntaRepository.java        Interfaz pura
    └── PreguntaMemoryRepository.java  Implementación en memoria para pruebas
```

## Pruebas

```bash
mvn test
```

22 pruebas: invariantes del agregado, objetos de valor, entidades de revisión, repositorio en
memoria y `DominioPuroTest`, que falla si alguna clase del dominio importa Spring, JPA, Hibernate o Lombok.

Documentación del taller: [`../TALLER11_DDD_DOCUMENTACION.md`](../TALLER11_DDD_DOCUMENTACION.md).
