import http from 'k6/http';
import { check, sleep } from 'k6';
import { Trend } from 'k6/metrics';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
const ETIQUETA = __ENV.ETIQUETA || 'sin-etiqueta';
const USUARIOS = Number(__ENV.USUARIOS || 500);
const MANTENER = __ENV.MANTENER || '2m';

const crearPregunta = new Trend('latencia_crear_pregunta', true);
const listarAsignadas = new Trend('latencia_listar_asignadas', true);

export const options = {
    scenarios: {
        convocatoria: {
            executor: 'ramping-vus',
            startVUs: 0,
            stages: [
                { duration: '30s', target: USUARIOS },
                { duration: MANTENER, target: USUARIOS },
                { duration: '15s', target: 0 },
            ],
            gracefulRampDown: '10s',
        },
    },
    thresholds: {
        http_req_failed: ['rate<0.01'],
        http_req_duration: ['p(95)<500'],
    },
    summaryTrendStats: ['avg', 'med', 'p(90)', 'p(95)', 'p(99)', 'max'],
};

// Corte 3: el gateway exige un JWT de Keycloak. setup() se ejecuta una vez antes de la carga,
// pide un token para el autor y otro para el revisor y los entrega a los usuarios virtuales.
// El token dura 15 minutos, más que la prueba (2 min 45 s por defecto).
function pedirToken(usuario, clave) {
    const res = http.post(`${BASE_URL}/api/v1/auth/token`, {
        grant_type: 'password',
        client_id: __ENV.CLIENT_ID || 'banco-preguntas-frontend',
        username: usuario,
        password: clave,
    });
    if (res.status !== 200) {
        throw new Error(`No se pudo obtener el token de ${usuario}: HTTP ${res.status}. ¿Está Keycloak arriba?`);
    }
    return res.json('access_token');
}

export function setup() {
    return {
        autor: pedirToken(__ENV.AUTOR || 'autor1', __ENV.AUTOR_CLAVE || 'autor123'),
        revisor: pedirToken(__ENV.REVISOR || 'revisor1', __ENV.REVISOR_CLAVE || 'revisor123'),
    };
}

const OPCIONES = ['Popayán', 'Cali', 'Pasto', 'Neiva'];

function cuerpoPregunta() {
    return JSON.stringify({
        context: 'El departamento del Cauca se ubica en el suroccidente de Colombia.',
        text: `Pregunta de carga ${__VU}-${__ITER}: ¿cuál es la capital del Cauca?`,
        options: OPCIONES.map((texto, i) => ({ id: 'ABCD'[i], text: `${texto} ${__VU}-${__ITER}` })),
        correctOptionId: 'A',
        justification: 'Popayán es la capital del departamento del Cauca desde su creación.',
        bibliography: 'Gobernación del Cauca, 2024',
        competency: 'Programación',
        topic: 'Prueba de carga',
        subtopic: 'Escenario de escalabilidad',
        difficulty: 'BAJO',
        authorId: 2,
    });
}

export default function (tokens) {
    if (Math.random() < 0.9) {
        const res = http.post(`${BASE_URL}/api/v1/preguntas`, cuerpoPregunta(), {
            headers: { 'Content-Type': 'application/json', Authorization: `Bearer ${tokens.autor}` },
            tags: { operacion: 'crear_pregunta' },
        });
        crearPregunta.add(res.timings.duration);
        check(res, { 'pregunta creada (201)': (r) => r.status === 201 });
    } else {
        const res = http.get(`${BASE_URL}/api/v1/revisiones/asignadas?page=0&size=10`, {
            headers: { Authorization: `Bearer ${tokens.revisor}` },
            tags: { operacion: 'listar_asignadas' },
        });
        listarAsignadas.add(res.timings.duration);
        check(res, { 'asignadas listadas (200)': (r) => r.status === 200 });
    }
    sleep(1);
}

function valor(metrica, stat) {
    return metrica && metrica.values && metrica.values[stat] !== undefined ? metrica.values[stat] : null;
}

export function handleSummary(data) {
    const m = data.metrics;
    const resumen = {
        etiqueta: ETIQUETA,
        fecha: new Date().toISOString(),
        baseUrl: BASE_URL,
        usuariosConcurrentes: USUARIOS,
        peticiones: valor(m.http_reqs, 'count'),
        peticionesPorSegundo: valor(m.http_reqs, 'rate'),
        tasaErrores: valor(m.http_req_failed, 'rate'),
        latenciaMs: {
            promedio: valor(m.http_req_duration, 'avg'),
            mediana: valor(m.http_req_duration, 'med'),
            p95: valor(m.http_req_duration, 'p(95)'),
            p99: valor(m.http_req_duration, 'p(99)'),
            maxima: valor(m.http_req_duration, 'max'),
        },
        p95CrearPreguntaMs: valor(m.latencia_crear_pregunta, 'p(95)'),
        p95ListarAsignadasMs: valor(m.latencia_listar_asignadas, 'p(95)'),
        umbralesCumplidos: Object.values(m).every((x) => !x.thresholds || Object.values(x.thresholds).every((t) => t.ok)),
    };
    const fmt = (n) => (n === null ? 'n/d' : n.toFixed(1));
    const texto = [
        `Escenario de escalabilidad (${ETIQUETA})`,
        `Usuarios concurrentes: ${USUARIOS}`,
        `Peticiones: ${resumen.peticiones} (${fmt(resumen.peticionesPorSegundo)} req/s)`,
        `Errores: ${fmt((resumen.tasaErrores || 0) * 100)} %`,
        `Latencia p95: ${fmt(resumen.latenciaMs.p95)} ms (crear: ${fmt(resumen.p95CrearPreguntaMs)} ms, listar: ${fmt(resumen.p95ListarAsignadasMs)} ms)`,
        `Umbrales (p95 < 500 ms y errores < 1 %): ${resumen.umbralesCumplidos ? 'CUMPLIDOS' : 'NO CUMPLIDOS'}`,
        '',
    ].join('\n');
    return {
        stdout: texto,
        [`resultados/resumen-${ETIQUETA}.json`]: JSON.stringify(resumen, null, 2),
        [`resultados/k6-completo-${ETIQUETA}.json`]: JSON.stringify(data, null, 2),
    };
}
