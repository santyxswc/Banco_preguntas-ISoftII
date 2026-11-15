-- HU-06: versión de la pregunta. Empieza en 1 y aumenta cada vez que el autor saca una
-- versión mejorada a partir de las observaciones de un rechazo.
ALTER TABLE question ADD COLUMN version INTEGER DEFAULT 1 NOT NULL;
