// Servidor estático mínimo para el frontend (solo módulos nativos de Node, sin npm install).
// Uso: node server.mjs            -> http://localhost:5173
//      PORT=3000 node server.mjs  -> otro puerto
//      API_BASE=http://localhost:8088/api/v1 node server.mjs  -> gateway en otra URL (sin tocar public/config.js)
import { createServer } from 'node:http';
import { readFile } from 'node:fs/promises';
import { extname, join, normalize, resolve } from 'node:path';
import { fileURLToPath } from 'node:url';

const ROOT = resolve(fileURLToPath(new URL('./public', import.meta.url)));
const PORT = Number(process.env.PORT || 5173);
const TYPES = {
    '.html': 'text/html; charset=utf-8',
    '.js': 'text/javascript; charset=utf-8',
    '.css': 'text/css; charset=utf-8',
    '.json': 'application/json; charset=utf-8',
    '.svg': 'image/svg+xml',
    '.ico': 'image/x-icon',
};

// Con API_BASE definido, /config.js se genera desde el entorno: la misma imagen Docker sirve
// para cualquier URL del gateway. Sin la variable se entrega public/config.js tal cual.
const API_BASE = process.env.API_BASE;
const RUNTIME_CONFIG = API_BASE && `window.APP_CONFIG = ${JSON.stringify({
    API_BASE,
    SWAGGER_URL: process.env.SWAGGER_URL || API_BASE.replace(/\/api\/v1\/?$/, '') + '/swagger-ui.html',
    AUTH_CLIENT_ID: process.env.AUTH_CLIENT_ID || 'banco-preguntas-frontend',
})};\n`;

createServer(async (req, res) => {
    const path = decodeURIComponent(new URL(req.url, 'http://localhost').pathname);
    if (RUNTIME_CONFIG && path === '/config.js') {
        res.writeHead(200, { 'Content-Type': TYPES['.js'], 'Cache-Control': 'no-store' }).end(RUNTIME_CONFIG);
        return;
    }
    const file = normalize(join(ROOT, path === '/' ? 'index.html' : path));
    if (!file.startsWith(ROOT)) {
        res.writeHead(403).end('Prohibido');
        return;
    }
    try {
        const body = await readFile(file);
        res.writeHead(200, { 'Content-Type': TYPES[extname(file)] || 'application/octet-stream' }).end(body);
    } catch {
        // SPA: cualquier ruta desconocida devuelve la aplicación
        const index = await readFile(join(ROOT, 'index.html'));
        res.writeHead(200, { 'Content-Type': TYPES['.html'] }).end(index);
    }
}).listen(PORT, () => {
    console.log(`Frontend Banco de Preguntas en http://localhost:${PORT}`);
    console.log('Consume la API REST del API Gateway configurada en public/config.js');
});
