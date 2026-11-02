// Servidor estático mínimo para el frontend (solo módulos nativos de Node, sin npm install).
// Uso: node server.mjs            -> http://localhost:5173
//      PORT=3000 node server.mjs  -> otro puerto
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

createServer(async (req, res) => {
    const path = decodeURIComponent(new URL(req.url, 'http://localhost').pathname);
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
