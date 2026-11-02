// Cliente de la API REST del API Gateway. Toda la comunicación frontend → backend pasa por aquí.
(function () {
    const BASE = window.APP_CONFIG.API_BASE;
    const TOKEN_KEY = 'bp-token';

    function token() {
        try { return sessionStorage.getItem(TOKEN_KEY); } catch { return null; }
    }

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
        const headers = body ? { 'Content-Type': 'application/json' } : {};
        const current = path.startsWith('/auth/') ? null : token();
        if (current) headers.Authorization = 'Bearer ' + current;
        let res;
        try {
            res = await fetch(url, {
                method,
                headers,
                body: body ? JSON.stringify(body) : undefined,
            });
        } catch {
            throw new ApiError(0, { message: 'No hay conexión con el API Gateway (' + BASE + '). ¿Están levantados los servicios?' });
        }
        const text = await res.text();
        let data = null;
        try { data = text ? JSON.parse(text) : null; } catch { data = { message: text }; }
        if (res.status === 401 && current) {
            try { sessionStorage.removeItem(TOKEN_KEY); } catch { }
            throw new ApiError(401, { message: 'La sesión venció. Cierre sesión e ingrese de nuevo.' });
        }
        if (!res.ok) throw new ApiError(res.status, data);
        return data;
    }

    window.Api = {
        ApiError,
        login: async (username, password) => {
            const res = await request('POST', '/auth/login', { body: { username, password } });
            try { if (res && res.token) sessionStorage.setItem(TOKEN_KEY, res.token); } catch { }
            return res;
        },
        clearSession: () => { try { sessionStorage.removeItem(TOKEN_KEY); } catch { } },
        me: () => request('GET', '/usuarios/me'),
        reviewers: (authorId) => request('GET', '/usuarios/reviewers/eligible', { query: { authorId } }),
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
