INSERT INTO app_user (id, username, password_hash, full_name, email) VALUES
    (1, 'admin', 'bp-admin$4175d48300f0ffc083fb8d89dcf351caf726d1412358361868fb698aa46a008f', 'Administrador General', 'admin@unicauca.edu.co'),
    (2, 'autor1', 'bp-autor1$c6e4ecb18c504cd8c0103767faafc29ea36283067573fc3d2f5dc741ec58c756', 'Profesor Juan Pérez (Autor)', 'autor1@unicauca.edu.co'),
    (3, 'autor2', 'bp-autor2$9a06bdb5d8d12aad613bae9cd14afe75f1237e79729f02b1ca0d4dd66b0bc213', 'Profesora María Gómez (Autora)', 'autor2@unicauca.edu.co'),
    (4, 'revisor1', 'bp-revisor1$975a7daa8eaae5823664deb1d023265a700dbe6730c66a525a388558528e932a', 'Profesor Carlos Revisor', 'revisor1@unicauca.edu.co'),
    (5, 'revisor2', 'bp-revisor2$11109fc593a503dd7dc1ee51769d91ad62c1d91752d5fc35e18ba7d4fdfc73d6', 'Profesora Ana Revisora', 'revisor2@unicauca.edu.co'),
    (6, 'estudiante1', 'bp-estudiante1$a1ca7846c1452d8a098c1aa90255be887343686e6def872ce852a0a48eca8412', 'Estudiante Pedro', 'estudiante1@unicauca.edu.co');

INSERT INTO user_role (user_id, role_name) VALUES
    (1, 'ADMINISTRADOR'),
    (2, 'AUTOR'), (2, 'DOCENTE'),
    (3, 'AUTOR'), (3, 'DOCENTE'),
    (4, 'REVISOR'), (4, 'DOCENTE'),
    (5, 'REVISOR'), (5, 'DOCENTE'),
    (6, 'ESTUDIANTE');

ALTER TABLE app_user ALTER COLUMN id RESTART WITH 100;
