// Único punto de contacto entre el frontend y el backend: la API REST del API Gateway.
// El frontend nunca llama directamente a un microservicio ni a Keycloak: el inicio de sesión
// también pasa por el gateway (/api/v1/auth/token), que lo reenvía al realm de Keycloak.
window.APP_CONFIG = {
    API_BASE: 'http://localhost:8080/api/v1',
    SWAGGER_URL: 'http://localhost:8080/swagger-ui.html',
    // Cliente público OpenID Connect registrado en el realm "banco-preguntas" de Keycloak
    AUTH_CLIENT_ID: 'banco-preguntas-frontend',
};
