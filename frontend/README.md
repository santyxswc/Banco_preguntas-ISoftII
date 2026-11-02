# Frontend web — Banco de Preguntas Saber Pro

Aplicación web de una sola página (HTML, CSS y JavaScript sin framework ni paso de compilación).
Está **separada del backend**: es un proceso propio (puerto 5173) y solo se comunica con el
**API Gateway** (`http://localhost:8080/api/v1`) mediante la API REST en JSON. Nunca llama
directamente a un microservicio.

## Ejecutar

Requiere Node.js 18 o superior (no necesita `npm install`).

```powershell
cd frontend
node server.mjs
# abrir http://localhost:5173
```

`microservicios/start-all.ps1` también lo inicia junto con el backend.

La URL del gateway se cambia en `public/config.js`.

## Pantallas por rol

| Rol | Usuario de prueba | Pantallas | HU |
|---|---|---|---|
| Autor | `autor1` / `autor123` | Crear y editar pregunta con todos los campos DCE · Mis preguntas con filtros, paginación y estados con color · Enviar a revisión (asíncrono) · Ver observaciones | HU-01, HU-02, HU-03 |
| Administrador | `admin` / `admin123` | Preguntas pendientes · Asignar 1 a 3 revisores (envía correo) · Todas las preguntas | HU-04 |
| Revisor | `revisor1` / `revisor123` | Preguntas asignadas paginadas · Aprobar o rechazar con observaciones | HU-05 |
| Todos | — | Notificaciones recibidas (correo enviado por notification-service) | HU-04, HU-05 |

## Archivos

| Archivo | Responsabilidad |
|---|---|
| `server.mjs` | Servidor estático mínimo con módulos nativos de Node |
| `public/config.js` | URL base del API Gateway |
| `public/api.js` | Cliente REST: un método por endpoint del gateway |
| `public/app.js` | Vistas, eventos y estado de la sesión |
| `public/styles.css` | Estilos, colores de estado, modo oscuro y diseño adaptable |
