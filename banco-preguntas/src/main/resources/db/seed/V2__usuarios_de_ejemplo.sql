-- Usuarios de ejemplo del Banco de Preguntas.
-- Deben coincidir con los de UsuarioImplRepository, porque el login
-- sigue en memoria y preguntas.autor_id es llave foránea a usuarios.
-- Solo la aplicación carga esta carpeta; las pruebas usan db/migration.

INSERT INTO usuarios (id, nombre, email, password, rol) VALUES
    ('U-001', 'Admin Sistema',  'admin@unicauca.edu.co',    'admin123', 'ADMINISTRADOR'),
    ('U-002', 'Carlos Morales', 'autor1@unicauca.edu.co',   'autor123', 'AUTOR'),
    ('U-003', 'Laura Pérez',    'autor2@unicauca.edu.co',   'autor123', 'AUTOR'),
    ('U-004', 'Marco Rivas',    'revisor1@unicauca.edu.co', 'rev123',   'AUTOR'),
    ('U-005', 'Diana Castro',   'revisor2@unicauca.edu.co', 'rev123',   'AUTOR'),
    ('U-006', 'Andrés Gómez',   'autor3@unicauca.edu.co',   'autor123', 'AUTOR'),
    ('U-007', 'Sofía Martínez', 'autor4@unicauca.edu.co',   'autor123', 'AUTOR');
