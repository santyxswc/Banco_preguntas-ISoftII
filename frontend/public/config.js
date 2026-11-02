// Único punto de contacto entre el frontend y el backend: la API REST del API Gateway.
// El frontend nunca llama directamente a un microservicio.
window.APP_CONFIG = {
    API_BASE: 'http://localhost:8080/api/v1',
    SWAGGER_URL: 'http://localhost:8080/swagger-ui.html',
};
