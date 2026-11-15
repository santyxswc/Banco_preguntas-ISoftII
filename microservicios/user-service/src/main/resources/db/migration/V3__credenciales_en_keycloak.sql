-- RNF #1 (corte 3): la autenticación pasa a Keycloak. El servicio de usuarios deja de guardar
-- contraseñas y conserva solo el perfil (nombre, correo y roles) enlazado por nombre de usuario.
ALTER TABLE app_user DROP COLUMN password_hash;
