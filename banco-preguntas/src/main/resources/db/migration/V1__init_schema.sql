-- Esquema inicial del Banco de Preguntas Saber Pro.
-- Compatible con PostgreSQL y con H2 en modo PostgreSQL (pruebas).
-- Autor: Santiago Caicedo

CREATE TABLE usuarios (
    id       VARCHAR(20)  PRIMARY KEY,
    nombre   VARCHAR(150) NOT NULL,
    email    VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    rol      VARCHAR(20)  NOT NULL
);

CREATE TABLE competencias (
    id     VARCHAR(20)  PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL
);

CREATE TABLE temas (
    id             VARCHAR(20)  PRIMARY KEY,
    nombre         VARCHAR(150) NOT NULL,
    competencia_id VARCHAR(20)  NOT NULL REFERENCES competencias(id)
);

CREATE TABLE subtemas (
    id      VARCHAR(20)  PRIMARY KEY,
    nombre  VARCHAR(150) NOT NULL,
    tema_id VARCHAR(20)  NOT NULL REFERENCES temas(id)
);

CREATE TABLE preguntas (
    id                  VARCHAR(20)  PRIMARY KEY,
    nombre              VARCHAR(255),
    contexto            TEXT,
    enunciado           TEXT,
    respuesta_correcta  VARCHAR(5),
    justificacion       TEXT,
    bibliografia        VARCHAR(500),
    competencia         VARCHAR(150),
    tema                VARCHAR(150),
    subtema             VARCHAR(150),
    nivel_dificultad    VARCHAR(20),
    estado              VARCHAR(30)  NOT NULL,
    autor_id            VARCHAR(20)  REFERENCES usuarios(id),
    fecha_creacion      TIMESTAMP    NOT NULL,
    fecha_modificacion  TIMESTAMP    NOT NULL
);

CREATE INDEX idx_preguntas_autor  ON preguntas(autor_id);
CREATE INDEX idx_preguntas_estado ON preguntas(estado);

CREATE TABLE pregunta_opciones (
    pregunta_id VARCHAR(20) NOT NULL REFERENCES preguntas(id) ON DELETE CASCADE,
    opcion_id   VARCHAR(5)  NOT NULL,
    texto       VARCHAR(500),
    PRIMARY KEY (pregunta_id, opcion_id)
);

CREATE TABLE asignaciones (
    id          VARCHAR(20) PRIMARY KEY,
    pregunta_id VARCHAR(20) NOT NULL REFERENCES preguntas(id),
    fecha       TIMESTAMP   NOT NULL
);

CREATE TABLE asignacion_revisores (
    asignacion_id VARCHAR(20) NOT NULL REFERENCES asignaciones(id) ON DELETE CASCADE,
    revisor_id    VARCHAR(20) NOT NULL,
    PRIMARY KEY (asignacion_id, revisor_id)
);
