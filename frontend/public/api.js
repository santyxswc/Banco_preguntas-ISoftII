// Cliente de la API REST del API Gateway. Toda la comunicación frontend → backend pasa por aquí.
// Cada petición lleva el JWT emitido por Keycloak en la cabecera Authorization: Bearer <token>.
(function () {
    const BASE = window.APP_CONFIG.API_BASE;
    const CLIENT_ID = window.APP_CONFIG.AUTH_CLIENT_ID;
    const SESSION_KEY = 'bp-session';

    // Sesión OAuth2: access token JWT de Keycloak, refresh token y vencimiento (RNF #1)
    function session() {
        try { return JSON.parse(sessionStorage.getItem(SESSION_KEY)); } catch { return null; }
    }

    function saveSession(tokens) {
        const data = {
            accessToken: tokens.access_token,
            refreshToken: tokens.refresh_token,
            expiresAt: Date.now() + (tokens.expires_in || 60) * 1000,
        };
        try { sessionStorage.setItem(SESSION_KEY, JSON.stringify(data)); } catch { }
        return data;
    }

    function clearSession() {
        try { sessionStorage.removeItem(SESSION_KEY); } catch { }
    }

    // Pide tokens a Keycloak a través del gateway (formulario application/x-www-form-urlencoded)
    async function tokenRequest(params) {
        let res;
        try {
            res = await fetch(BASE + '/auth/token', {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: new URLSearchParams({ client_id: CLIENT_ID, ...params }),
            });
        } catch {
            throw new ApiError(0, { message: 'No hay conexión con el API Gateway (' + BASE + '). ¿Están levantados los servicios?' });
        }
        const data = await res.json().catch(() => ({}));
        if (!res.ok) {
            throw new ApiError(res.status === 400 ? 401 : res.status, { message: data.error_description || 'No se pudo iniciar sesión' });
        }
        return saveSession(data);
    }

    // Devuelve un access token vigente; lo renueva con el refresh token poco antes de que venza
    async function token() {
        const current = session();
        if (!current) return null;
        if (Date.now() < current.expiresAt - 30000) return current.accessToken;
        if (!current.refreshToken) return current.accessToken;
        try {
            return (await tokenRequest({ grant_type: 'refresh_token', refresh_token: current.refreshToken })).accessToken;
        } catch {
            clearSession();
            return null;
        }
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
        const hadSession = !!session();
        const current = await token();
        if (hadSession && !current) {
            throw new ApiError(401, { message: 'La sesión venció. Cierre sesión e ingrese de nuevo.' });
        }
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
            clearSession();
            throw new ApiError(401, { message: 'La sesión venció. Cierre sesión e ingrese de nuevo.' });
        }
        if (!res.ok) throw new ApiError(res.status, data);
        return data;
    }

    window.Api = {
        ApiError,
        // Inicio de sesión: Keycloak valida las credenciales y emite el JWT; luego se carga el perfil
        login: async (username, password) => {
            await tokenRequest({ grant_type: 'password', username, password, scope: 'openid' });
            try {
                return { user: await request('GET', '/usuarios/me') };
            } catch (e) {
                clearSession();
                throw e;
            }
        },
        // Cierre de sesión: invalida la sesión en Keycloak (revoca el refresh token) y borra los tokens locales
        logout: async () => {
            const current = session();
            clearSession();
            if (!current?.refreshToken) return;
            try {
                await fetch(BASE + '/auth/logout', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                    body: new URLSearchParams({ client_id: CLIENT_ID, refresh_token: current.refreshToken }),
                });
            } catch { /* la sesión local ya se borró */ }
        },
        clearSession,
        hasSession: () => !!session(),
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
        reviewHistory: (questionId, solicitanteId) => request('GET', `/revisiones/pregunta/${questionId}/historial`, { query: { solicitanteId } }),

        notifications: (recipientId) => request('GET', `/notificaciones/recipient/${recipientId}`),
    };
})();
