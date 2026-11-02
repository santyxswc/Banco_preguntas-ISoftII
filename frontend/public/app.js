// Lógica de la interfaz: vistas por rol (autor, administrador, revisor) sobre la API REST.
(function () {
    const PAGE_SIZE = 5;
    const $ = (sel, root = document) => root.querySelector(sel);
    const state = { user: null, catalog: null, reviewers: null, pages: {}, filters: {} };

    // ---------- utilidades ----------
    const esc = (v) => String(v ?? '').replace(/[&<>"']/g, (c) => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c]));
    const hasRole = (role) => state.user?.roles?.some((r) => r.toUpperCase() === role);
    const fmtDate = (d) => (d ? new Date(d).toLocaleString('es-CO', { dateStyle: 'medium', timeStyle: 'short' }) : '');
    const stateInfo = (code) => state.catalog?.states.find((s) => s.code === code) || { code, label: code, color: '#777', editable: false };
    const sleep = (ms) => new Promise((r) => setTimeout(r, ms));

    function toast(message, kind = 'ok') {
        const el = $('#toast');
        el.textContent = message;
        el.className = `toast ${kind}`;
        el.hidden = false;
        clearTimeout(toast.timer);
        toast.timer = setTimeout(() => (el.hidden = true), 4500);
    }

    function showError(e) {
        const detail = e.errors?.length ? ': ' + e.errors.join(' · ') : '';
        toast((e.message || 'Error inesperado') + detail, 'error');
    }

    function badge(code) {
        const s = stateInfo(code);
        return `<span class="badge" style="--c:${esc(s.color)}">${esc(s.label)}</span>`;
    }

    function openDialog(html) {
        $('#dialog-content').innerHTML = html;
        $('#dialog').showModal();
    }

    function pager(containerId, page, onChange) {
        const el = $('#' + containerId);
        if (!page || page.totalPages <= 1) {
            el.innerHTML = page ? `<span class="muted">${page.totalElements} resultado(s)</span>` : '';
            return;
        }
        el.innerHTML = `
            <button class="btn ghost" ${page.page === 0 ? 'disabled' : ''} data-p="${page.page - 1}">← Anterior</button>
            <span>Página ${page.page + 1} de ${page.totalPages} · ${page.totalElements} resultado(s)</span>
            <button class="btn ghost" ${page.page + 1 >= page.totalPages ? 'disabled' : ''} data-p="${page.page + 1}">Siguiente →</button>`;
        el.querySelectorAll('button[data-p]').forEach((b) => b.addEventListener('click', () => onChange(Number(b.dataset.p))));
    }

    function questionDetails(q) {
        const options = (q.options || []).map((o) => `
            <li class="${o.id === q.correctOptionId ? 'correct' : ''}"><b>${esc(o.id)}.</b> ${esc(o.text)}${o.id === q.correctOptionId ? ' ✔' : ''}</li>`).join('');
        return `
            <dl class="details">
                <dt>Contexto</dt><dd>${esc(q.context) || '<span class="muted">—</span>'}</dd>
                <dt>Opciones</dt><dd><ul class="options-view">${options}</ul></dd>
                <dt>Justificación</dt><dd>${esc(q.justification) || '<span class="muted">—</span>'}</dd>
                <dt>Bibliografía</dt><dd>${esc(q.bibliography) || '<span class="muted">—</span>'}</dd>
                <dt>Subtema</dt><dd>${esc(q.subtopic) || '<span class="muted">—</span>'}</dd>
            </dl>`;
    }

    function questionCard(q, actionsHtml = '') {
        return `
            <article class="item" data-id="${q.id}" style="--c:${esc(stateInfo(q.status).color)}">
                <header class="row between wrap">
                    <div class="row wrap gap">
                        ${badge(q.status)}
                        <span class="muted">#${q.id}</span>
                        <span class="chip">${esc(q.competency || 'Sin competencia')}</span>
                        <span class="chip">${esc(q.topic || 'Sin tema')}</span>
                        <span class="chip">${esc(q.difficulty || '—')}</span>
                    </div>
                    <span class="muted small">Actualizada ${fmtDate(q.updatedAt)}</span>
                </header>
                <p class="statement">${esc(q.text)}</p>
                <details><summary>Ver pregunta completa</summary>${questionDetails(q)}</details>
                <div class="actions">${actionsHtml}</div>
            </article>`;
    }

    // ---------- sesión ----------
    async function login(event) {
        event.preventDefault();
        const form = new FormData(event.target);
        try {
            const res = await Api.login(form.get('username'), form.get('password'));
            state.user = res.user;
            sessionStorage.setItem('bp-user', JSON.stringify(res.user));
            await startApp();
        } catch (e) {
            showError(e.status === 401 ? { message: 'Usuario o contraseña incorrectos' } : e);
        }
    }

    function logout() {
        sessionStorage.removeItem('bp-user');
        Api.clearSession();
        state.user = null;
        $('#app-view').hidden = true;
        $('#session').hidden = true;
        $('#login-view').hidden = false;
    }

    async function startApp() {
        $('#login-view').hidden = true;
        $('#app-view').hidden = false;
        $('#session').hidden = false;
        $('#session-name').textContent = state.user.fullName;
        $('#session-roles').textContent = state.user.roles.join(' · ');
        try {
            state.catalog = await Api.catalog();
        } catch (e) {
            showError(e);
            state.catalog = { competencies: [], difficulties: ['BAJO', 'MEDIO', 'ALTO'], states: [] };
        }
        fillCatalogSelects();
        buildTabs();
    }

    // ---------- pestañas por rol ----------
    function buildTabs() {
        const tabs = [];
        if (hasRole('AUTOR')) tabs.push(['tab-mine', 'Mis preguntas'], ['tab-form', 'Nueva pregunta']);
        if (hasRole('ADMINISTRADOR')) tabs.push(['tab-assign', 'Asignar revisores'], ['tab-all', 'Todas las preguntas']);
        if (hasRole('REVISOR')) tabs.push(['tab-review', 'Preguntas asignadas']);
        tabs.push(['tab-notifications', 'Notificaciones']);
        $('#tabs').innerHTML = tabs.map(([id, label]) => `<button role="tab" data-tab="${id}">${label}</button>`).join('');
        $('#tabs').querySelectorAll('button').forEach((b) => b.addEventListener('click', () => showTab(b.dataset.tab)));
        showTab(tabs[0][0]);
    }

    function showTab(id) {
        document.querySelectorAll('.tab').forEach((t) => (t.hidden = t.id !== id));
        $('#tabs').querySelectorAll('button').forEach((b) => b.setAttribute('aria-selected', String(b.dataset.tab === id)));
        const loaders = {
            'tab-mine': () => loadMine(0),
            'tab-form': () => (!$('#question-form [name=id]').value ? resetForm() : null),
            'tab-assign': () => loadPending(0),
            'tab-all': () => loadAll(0),
            'tab-review': () => loadAssigned(0),
            'tab-notifications': loadNotifications,
        };
        loaders[id]?.();
    }

    function fillCatalogSelects() {
        const opts = (list, fmt = (v) => [v, v]) => list.map((v) => { const [val, lab] = fmt(v); return `<option value="${esc(val)}">${esc(lab)}</option>`; }).join('');
        $('#filters [name=status]').innerHTML = '<option value="">Todos los estados</option>' + opts(state.catalog.states, (s) => [s.code, s.label]);
        $('#filters [name=competency]').innerHTML = '<option value="">Todas las competencias</option>' + opts(state.catalog.competencies);
        $('#filters [name=difficulty]').innerHTML = '<option value="">Toda dificultad</option>' + opts(state.catalog.difficulties);
        $('#question-form [name=competency]').innerHTML = '<option value="">Seleccione…</option>' + opts(state.catalog.competencies);
        $('#question-form [name=difficulty]').innerHTML = '<option value="">Seleccione…</option>' + opts(state.catalog.difficulties);
        $('#legend').innerHTML = state.catalog.states.map((s) => badge(s.code)).join('');
    }

    // ---------- AUTOR: mis preguntas (HU-02, HU-03) ----------
    async function loadMine(page) {
        const list = $('#mine-list');
        list.innerHTML = '<p class="muted">Cargando…</p>';
        try {
            const res = await Api.questions({ authorId: state.user.id, ...state.filters, page, size: PAGE_SIZE });
            list.innerHTML = res.content.length
                ? res.content.map((q) => questionCard(q, authorActions(q))).join('')
                : '<p class="empty">No hay preguntas con esos filtros.</p>';
            pager('mine-pager', res, loadMine);
            state.pages.mine = page;
        } catch (e) {
            list.innerHTML = '';
            showError(e);
        }
    }

    function authorActions(q) {
        const s = stateInfo(q.status);
        const buttons = [];
        if (s.editable) {
            buttons.push(`<button class="btn" data-action="edit" data-id="${q.id}">Editar</button>`);
            buttons.push(`<button class="btn primary" data-action="submit" data-id="${q.id}">Enviar a revisión</button>`);
        }
        if (['IN_REVIEW', 'APPROVED', 'REJECTED', 'PUBLISHED'].includes(q.status)) {
            buttons.push(`<button class="btn ghost" data-action="observations" data-id="${q.id}">Ver observaciones</button>`);
        }
        return buttons.join('');
    }

    async function onMineClick(event) {
        const btn = event.target.closest('button[data-action]');
        if (!btn) return;
        const id = Number(btn.dataset.id);
        if (btn.dataset.action === 'edit') return editQuestion(id);
        if (btn.dataset.action === 'observations') return showObservations(id);
        if (btn.dataset.action === 'submit') return submitForReview(id, btn);
    }

    async function submitForReview(id, btn) {
        btn.disabled = true;
        try {
            const { operationId } = await Api.submitForReview(id, state.user.id);
            toast('Solicitud enviada por RabbitMQ (202 Accepted). Esperando a lifecycle-service…');
            const result = await waitForOperation(operationId);
            if (result?.status === 'COMPLETED') toast('La pregunta pasó a "Pendiente de revisión"');
            else if (result?.status === 'FAILED') toast(result.message, 'error');
            else toast('La operación sigue en proceso; actualice en unos segundos.', 'warn');
            await loadMine(state.pages.mine || 0);
        } catch (e) {
            showError(e);
            btn.disabled = false;
        }
    }

    async function waitForOperation(operationId) {
        for (let i = 0; i < 12; i++) {
            await sleep(500);
            try {
                const op = await Api.operation(operationId);
                if (op.status !== 'PENDING') return op;
            } catch (e) {
                if (e.status !== 404) throw e;
            }
        }
        return null;
    }

    async function showObservations(questionId) {
        try {
            const reviews = await Api.observations(questionId);
            openDialog(`
                <h3>Observaciones de los revisores · pregunta #${questionId}</h3>
                ${reviews.length ? reviews.map((r) => `
                    <div class="observation ${r.result === 'APPROVED' ? 'ok' : 'bad'}">
                        <strong>${r.result === 'APPROVED' ? 'Aprobada' : 'Rechazada'}</strong>
                        <span class="muted">Revisor #${r.reviewerId} · ronda ${r.round} · ${fmtDate(r.createdAt)}</span>
                        <p>${esc(r.comments) || '<span class="muted">Sin observaciones</span>'}</p>
                    </div>`).join('') : '<p class="empty">Aún no hay evaluaciones registradas.</p>'}`);
        } catch (e) {
            showError(e);
        }
    }

    // ---------- AUTOR: formulario (HU-01) ----------
    function renderOptions(options = [], correct = 'A') {
        $('#options').innerHTML = ['A', 'B', 'C', 'D'].map((id, i) => `
            <label class="option">
                <input type="radio" name="correct" value="${id}" ${id === correct ? 'checked' : ''} aria-label="Respuesta correcta ${id}">
                <b>${id}</b>
                <input name="opt-${id}" value="${esc(options[i]?.text || '')}" placeholder="Opción ${id}" required>
            </label>`).join('');
    }

    function resetForm() {
        const form = $('#question-form');
        form.reset();
        form.elements.namedItem('id').value = '';
        $('#form-title').textContent = 'Nueva pregunta';
        $('#form-errors').hidden = true;
        renderOptions();
    }

    async function editQuestion(id) {
        try {
            const q = await Api.question(id);
            const form = $('#question-form');
            ['context', 'text', 'justification', 'bibliography', 'competency', 'topic', 'subtopic', 'difficulty']
                .forEach((f) => (form[f].value = q[f] || ''));
            form.elements.namedItem('id').value = q.id;
            renderOptions(q.options, q.correctOptionId);
            $('#form-title').textContent = `Editar pregunta #${q.id}`;
            $('#form-errors').hidden = true;
            showTab('tab-form');
        } catch (e) {
            showError(e);
        }
    }

    async function saveQuestion(event) {
        event.preventDefault();
        const form = event.target;
        const data = new FormData(form);
        const body = {
            context: data.get('context'),
            text: data.get('text'),
            options: ['A', 'B', 'C', 'D'].map((id) => ({ id, text: data.get('opt-' + id) })),
            correctOptionId: data.get('correct'),
            justification: data.get('justification'),
            bibliography: data.get('bibliography'),
            competency: data.get('competency'),
            topic: data.get('topic'),
            subtopic: data.get('subtopic'),
            difficulty: data.get('difficulty'),
            authorId: state.user.id,
        };
        const errorsBox = $('#form-errors');
        try {
            const id = data.get('id');
            if (id) {
                await Api.updateQuestion(id, body, state.user.id);
                toast(`Pregunta #${id} actualizada`);
            } else {
                const res = await Api.createQuestion(body);
                toast(`Pregunta #${res.questionId ?? res.id ?? ''} creada en borrador`);
            }
            resetForm();
            showTab('tab-mine');
        } catch (e) {
            errorsBox.hidden = false;
            errorsBox.innerHTML = `<strong>${esc(e.message)}</strong>${e.errors.length ? '<ul>' + e.errors.map((x) => `<li>${esc(x)}</li>`).join('') + '</ul>' : ''}`;
        }
    }

    // ---------- ADMINISTRADOR: asignación (HU-04) ----------
    async function loadReviewers() {
        if (!state.reviewers) state.reviewers = await Api.reviewers();
        return state.reviewers;
    }

    async function loadPending(page) {
        const list = $('#pending-list');
        list.innerHTML = '<p class="muted">Cargando…</p>';
        try {
            const [res, reviewers] = await Promise.all([
                Api.questions({ status: 'PENDING_REVIEW', page, size: PAGE_SIZE }), loadReviewers()]);
            list.innerHTML = res.content.length
                ? res.content.map((q) => questionCard(q, assignForm(q, reviewers))).join('')
                : '<p class="empty">No hay preguntas pendientes de revisión.</p>';
            pager('pending-pager', res, loadPending);
            state.pages.pending = page;
        } catch (e) {
            list.innerHTML = '';
            showError(e);
        }
    }

    function assignForm(q, reviewers) {
        const options = reviewers.filter((r) => r.id !== q.authorId).map((r) => `
            <label class="check"><input type="checkbox" name="reviewer" value="${r.id}"> ${esc(r.fullName)} <span class="muted small">${esc(r.email)}</span></label>`).join('');
        return `
            <form class="assign" data-id="${q.id}">
                <span class="muted small">Revisores (1 a 3; el autor #${q.authorId} no aparece):</span>
                <div class="checks">${options}</div>
                <button class="btn primary" type="submit">Asignar y notificar</button>
            </form>`;
    }

    async function onAssign(event) {
        const form = event.target.closest('form.assign');
        if (!form) return;
        event.preventDefault();
        const ids = [...form.querySelectorAll('input[name=reviewer]:checked')].map((i) => Number(i.value));
        if (ids.length < 1 || ids.length > 3) {
            toast('Seleccione entre 1 y 3 revisores', 'warn');
            return;
        }
        try {
            await Api.assign(Number(form.dataset.id), ids, state.user.id);
            toast(`Revisores asignados a la pregunta #${form.dataset.id}. Se enviaron ${ids.length} correo(s).`);
            await loadPending(state.pages.pending || 0);
        } catch (e) {
            showError(e);
        }
    }

    async function loadAll(page) {
        const list = $('#all-list');
        list.innerHTML = '<p class="muted">Cargando…</p>';
        try {
            const res = await Api.questions({ page, size: PAGE_SIZE });
            list.innerHTML = res.content.length
                ? res.content.map((q) => questionCard(q, `<span class="muted small">Autor #${q.authorId}</span>
                    <button class="btn ghost" data-action="observations" data-id="${q.id}">Ver observaciones</button>`)).join('')
                : '<p class="empty">No hay preguntas registradas.</p>';
            pager('all-pager', res, loadAll);
        } catch (e) {
            list.innerHTML = '';
            showError(e);
        }
    }

    // ---------- REVISOR: evaluación (HU-05) ----------
    async function loadAssigned(page) {
        const list = $('#review-list');
        list.innerHTML = '<p class="muted">Cargando…</p>';
        try {
            const res = await Api.assigned(state.user.id, page, PAGE_SIZE);
            const questions = await Promise.all(res.content.map((a) => Api.question(a.questionId).catch(() => null)));
            list.innerHTML = res.content.length
                ? res.content.map((a, i) => {
                    const q = questions[i] || { id: a.questionId, text: a.questionText, status: 'IN_REVIEW', authorId: a.authorId };
                    return questionCard(q, reviewForm(a));
                }).join('')
                : '<p class="empty">No tiene preguntas pendientes de evaluar.</p>';
            pager('review-pager', res, loadAssigned);
            state.pages.review = page;
        } catch (e) {
            list.innerHTML = '';
            showError(e);
        }
    }

    function reviewForm(a) {
        return `
            <form class="evaluate" data-id="${a.questionId}">
                <span class="muted small">Asignada ${fmtDate(a.assignedAt)} · ronda ${a.round}</span>
                <textarea name="comments" rows="2" placeholder="Observaciones para el autor (obligatorias al rechazar)"></textarea>
                <div class="row end gap">
                    <button class="btn danger" name="result" value="REJECTED">Rechazar</button>
                    <button class="btn success" name="result" value="APPROVED">Aprobar</button>
                </div>
            </form>`;
    }

    async function onEvaluate(event) {
        const form = event.target.closest('form.evaluate');
        if (!form) return;
        event.preventDefault();
        const result = event.submitter?.value;
        const comments = form.comments.value.trim();
        if (result === 'REJECTED' && !comments) {
            toast('Para rechazar debe escribir observaciones', 'warn');
            return;
        }
        try {
            const res = await Api.evaluate(Number(form.dataset.id), state.user.id, result, comments || null);
            const final = res.questionFinalState ? ` La pregunta quedó: ${stateInfo(res.questionFinalState).label}.` : ' Faltan evaluaciones de otros revisores.';
            toast(res.message + '.' + final);
            await loadAssigned(state.pages.review || 0);
        } catch (e) {
            showError(e);
        }
    }

    // ---------- notificaciones ----------
    async function loadNotifications() {
        const list = $('#notifications-list');
        list.innerHTML = '<p class="muted">Cargando…</p>';
        try {
            const items = (await Api.notifications(state.user.id)).sort((a, b) => new Date(b.sentAt) - new Date(a.sentAt));
            list.innerHTML = items.length ? items.map((n) => `
                <article class="item notification">
                    <header class="row between wrap">
                        <strong>${esc(n.subject)}</strong>
                        <span class="badge" style="--c:${n.status === 'SENT' ? '#28A745' : n.status === 'FAILED' ? '#DC3545' : '#E6A014'}">${esc(n.status)}</span>
                    </header>
                    <p>${esc(n.message)}</p>
                    <span class="muted small">${esc(n.type)} · correo a ${esc(n.recipientEmail || '—')} · ${fmtDate(n.sentAt)}</span>
                </article>`).join('') : '<p class="empty">No tiene notificaciones.</p>';
        } catch (e) {
            list.innerHTML = '';
            showError(e);
        }
    }

    // ---------- arranque ----------
    function init() {
        $('#api-base').textContent = window.APP_CONFIG.API_BASE;
        $('#swagger-link').href = window.APP_CONFIG.SWAGGER_URL;
        $('#login-form').addEventListener('submit', login);
        $('#logout').addEventListener('click', logout);
        $('#question-form').addEventListener('submit', saveQuestion);
        $('#mine-list').addEventListener('click', onMineClick);
        $('#all-list').addEventListener('click', onMineClick);
        $('#pending-list').addEventListener('submit', onAssign);
        $('#review-list').addEventListener('submit', onEvaluate);
        $('#refresh-notifications').addEventListener('click', loadNotifications);
        document.querySelectorAll('[data-goto]').forEach((b) => b.addEventListener('click', () => {
            if (b.dataset.goto === 'tab-form') resetForm();
            showTab(b.dataset.goto);
        }));
        $('#filters').addEventListener('submit', (e) => {
            e.preventDefault();
            state.filters = Object.fromEntries(new FormData(e.target));
            loadMine(0);
        });
        $('#filters').addEventListener('reset', () => {
            state.filters = {};
            setTimeout(() => loadMine(0));
        });
        renderOptions();
        try {
            const saved = JSON.parse(sessionStorage.getItem('bp-user'));
            if (saved) {
                state.user = saved;
                startApp();
            }
        } catch { /* sesión no disponible */ }
    }

    init();
})();
