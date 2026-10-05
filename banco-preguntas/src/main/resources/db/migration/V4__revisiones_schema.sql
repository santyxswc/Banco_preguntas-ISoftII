-- Migración V4: Tabla de revisiones (HU05 - Revisión por pares)
-- Autores: Santiago Caicedo, Ivan Alexander Lopez, Adrian Araujo Urbano, Carlos Bambague

CREATE TABLE revisiones (
    id            VARCHAR(36)  PRIMARY KEY,
    pregunta_id   VARCHAR(20)  NOT NULL REFERENCES preguntas(id) ON DELETE CASCADE,
    revisor_id    VARCHAR(20)  NOT NULL REFERENCES usuarios(id),
    veredicto     VARCHAR(30)  NOT NULL,
    observaciones TEXT,
    fecha         TIMESTAMP    NOT NULL
);

CREATE INDEX idx_revisiones_pregunta ON revisiones(pregunta_id);
CREATE INDEX idx_revisiones_revisor  ON revisiones(revisor_id);
