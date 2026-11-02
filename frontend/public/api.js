// Cliente de la API REST del API Gateway. Toda la comunicación frontend → backend pasa por aquí.
(function () {
    const BASE = window.APP_CONFIG.API_BASE;

    class ApiError extends Error {
        constructor(status, body) {
            super((body && (body.message || body.error)) || `Error HTTP ${status}`);
            this.status = status;
            this.errors = (body && body.errors) || [];
        }
    }

    async function request(method, path, { query, body } = {}) {
        const url = new URL(BASE + path);
        Object.entries(query || {}).forEach(([k, v]) => {
            if (v !== undefined && v !== null && v !== '') url.searchParams.set(k, v);
        });
        let res;
        try {
            res = await fetch(url, {
                method,
                headers: body ? { 'Content-Type': 'application/json' } : {},
                body: body ? JSON.stringify(body) : undefined,
            });
        } catch {
            throw new ApiError(0, { message: 'No hay conexión con el API Gateway (' + BASE + '). ¿Están levantados los servicios?' });
        }
        const text = await res.text();
        let data = null;
        try { data = text ? JSON.parse(text) : null; } catch { data = { message: text }; }
        if (!res.ok) throw new ApiError(res.status, data);
        return data;
    }

    window.Api = {
        ApiError,
        login: (username, password) => request('POST', '/auth/login', { body: { username, password } }),
        reviewers: () => request('GET', '/usuarios/role/REVISOR'),
        users: () => request('GET', '/usuarios'),

        catalog: () => request('GET', '/preguntas/catalog'),
        questions: (query) => request('GET', '/preguntas', { query }),
        question: (id) => request('GET', `/preguntas/${id}`),
        createQuestion: (q) => request('POST', '/preguntas', { body: q }),
        updateQuestion: (id, q, requestedBy) => request('PUT', `/preguntas/${id}`, { query: { requestedBy }, body: q }),
        submitForReview: (id, requestedBy) => request('POST', `/preguntas/${id}/submit-review`, { query: { requestedBy } }),
        operation: (operationId) => request('GET', `/preguntas/operations/${operationId}`),

        assign: (questionId, reviewerIds, assignedBy) => request('POST', '/asignaciones', { body: { questionId, reviewerIds, assignedBy } }),
        assignments: (questionId) => request('GET', '/asignaciones', { query: { questionId } }),
        assigned: (revisorId, page, size) => request('GET', '/revisiones/asignadas', { query: { revisorId, page, size } }),
        evaluate: (questionId, reviewerId, result, comments) => request('POST', '/revisiones/evaluar', { body: { questionId, reviewerId, result, comments } }),
        observations: (questionId) => request('GET', `/revisiones/pregunta/${questionId}`),

        notifications: (recipientId) => request('GET', `/notificaciones/recipient/${recipientId}`),
    };
})();
