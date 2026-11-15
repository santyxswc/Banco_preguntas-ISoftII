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

La URL del gateway se cambia en `public/config.js` o, sin tocar archivos, con la variable
`API_BASE` (`API_BASE=http://localhost:8088/api/v1 node server.mjs`); así lo hace la imagen Docker.

Con Docker Compose el frontend ya viene incluido: `docker compose up -d --build` en `microservicios/`.

## Sesión (JWT con Keycloak)

El inicio de sesión envía usuario y contraseña a `POST /api/v1/auth/token` (el gateway lo reenvía
a Keycloak). El frontend guarda el `access_token` y el `refresh_token` en `sessionStorage`, envía
`Authorization: Bearer <JWT>` en cada petición, renueva el token antes de que venza y al salir
llama a `/api/v1/auth/logout`. Los usuarios y roles se administran en Keycloak (por Postman).

## Pantallas por rol

| Rol | Usuario de prueba | Pantallas | HU |
|---|---|---|---|
| Autor | `autor1` / `autor123` | Crear y editar pregunta con todos los campos DCE · Mis preguntas con filtros, paginación y estados con color · Enviar a revisión (asíncrono) · **Historial de revisiones por ronda** · **Crear versión mejorada** de una pregunta rechazada | HU-01, HU-02, HU-03, HU-06 |
| Administrador | `admin` / `admin123` | Preguntas pendientes · Asignar 1 a 3 revisores (envía correo) · Todas las preguntas | HU-04 |
| Revisor | `revisor1` / `revisor123` | Preguntas asignadas paginadas · Aprobar o rechazar con observaciones | HU-05 |
| Todos | — | Notificaciones recibidas (correo enviado por notification-service) | HU-04, HU-05, HU-06 |

## Archivos

| Archivo | Responsabilidad |
|---|---|
| `server.mjs` | Servidor estático mínimo con módulos nativos de Node; genera `config.js` si se define `API_BASE` |
| `Dockerfile` | Imagen del frontend (Node 20 Alpine) |
| `public/config.js` | URL base del API Gateway y cliente de Keycloak |
| `public/api.js` | Cliente REST: un método por endpoint del gateway; maneja el JWT y su renovación |
| `public/app.js` | Vistas, eventos y estado de la sesión |
| `public/styles.css` | Estilos, colores de estado, modo oscuro y diseño adaptable |
