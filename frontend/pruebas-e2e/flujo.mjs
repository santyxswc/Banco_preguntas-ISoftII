import { launch } from './cdp.mjs';
// Prueba de extremo a extremo del frontend con clics reales (HU-01 a HU-06, cuatro roles).
// Uso: node flujo.mjs <carpeta-de-capturas> <carpeta-de-perfil-temporal>   (requiere Chromium y el sistema arriba)
const OUT = process.argv[2], BASE = process.env.FRONTEND_URL || 'http://localhost:5173';
const b = await launch(process.argv[3]);
const paso = (t) => console.log('· ' + t);
const txt = (sel) => b.evalJs(`document.querySelector(${JSON.stringify(sel)})?.innerText || ''`);
// selector del botón de una tarjeta por texto dentro de un contenedor
const btn = (cont, texto, marca) => `([...document.querySelectorAll('${cont} .card, ${cont} > *')].filter(c=>c.textContent.includes(${JSON.stringify(marca)})).flatMap(c=>[...c.querySelectorAll('button')]).find(x=>x.innerText.trim()===${JSON.stringify(texto)}))`;
async function login(u, p) {
    await b.goto(BASE); await b.waitFor(`document.querySelector('#login-form')`, 'formulario de login');
    await b.type('#login-form input[name=username]', u); await b.type('#login-form input[name=password]', p);
    await b.click('#login-form button[type=submit]');
    await b.waitFor(`!document.querySelector('#app-view').hidden`, 'entrar como ' + u);
    paso(`login ${u}: ${await txt('#session-name')} [${await txt('#session-roles')}] pestañas: ${(await txt('#tabs')).replace(/\n/g, ' | ')}`);
}
async function logout() { await b.click('#logout'); await b.waitFor(`!document.querySelector('#login-view').hidden`, 'salir'); }
const tab = async (id) => { await b.click(`#tabs button[data-tab=${id}]`); await b.sleep(900); };
const toast = async () => { await b.sleep(500); return (await txt('#toast')).trim(); };
const MARCA = 'prueba con clics ' + Date.now().toString().slice(-5);
try {
    await b.goto(BASE); await b.shot(`${OUT}/ui-01-login.png`);
    // credenciales incorrectas
    await b.type('#login-form input[name=username]', 'autor1'); await b.type('#login-form input[name=password]', 'mala');
    await b.click('#login-form button[type=submit]'); paso('login incorrecto → ' + await toast());
    // ---- AUTOR: HU-01, HU-02, HU-03
    await login('autor1', 'autor123'); await b.shot(`${OUT}/ui-02-autor-mis-preguntas.png`);
    await b.click('[data-goto=tab-form]'); await b.waitFor(`!document.querySelector('#tab-form').hidden`, 'formulario');
    // guardar vacío con opciones repetidas para ver la validación
    await b.type('#question-form textarea[name=context]', `Un equipo de desarrollo mantiene un servicio (${MARCA}) con varias reglas de negocio.`);
    await b.type('#question-form textarea[name=text]', '¿Qué patrón permite cambiar el algoritmo de validación sin modificar al cliente?');
    for (const [k, v] of [['A', 'Strategy'], ['B', 'Singleton'], ['C', 'Facade'], ['D', 'Builder']]) await b.type(`#question-form input[name=opt-${k}]`, v);
    await b.click('#question-form input[name=correct][value=A]');
    await b.type('#question-form textarea[name=justification]', 'Strategy encapsula cada algoritmo en una clase intercambiable.');
    await b.type('#question-form input[name=bibliography]', 'Gamma et al. (1994). Design Patterns.');
    await b.evalJs(`(()=>{const s=document.querySelector('#question-form select[name=competency]'); s.selectedIndex=Math.max(1,s.selectedIndex); s.dispatchEvent(new Event('change',{bubbles:true})); const d=document.querySelector('#question-form select[name=difficulty]'); d.selectedIndex=Math.max(1,d.selectedIndex); d.dispatchEvent(new Event('change',{bubbles:true}));})()`);
    await b.type('#question-form input[name=topic]', 'Patrones de diseño'); await b.type('#question-form input[name=subtopic]', 'Comportamiento');
    await b.shot(`${OUT}/ui-03-autor-nueva-pregunta.png`, true);
    await b.click('#question-form button[type=submit]'); paso('guardar → ' + await toast() + ' | errores: ' + (await txt('#form-errors')).replace(/\n/g, ' / '));
    await b.waitFor(`!document.querySelector('#tab-mine').hidden`, 'volver a mis preguntas'); await b.sleep(700);
    await b.waitFor(`document.querySelector('#mine-list').textContent.includes(${JSON.stringify(MARCA)})`, 'pregunta en la lista');
    // filtro por palabra
    await b.type('#filters input[name=keyword]', MARCA); await b.click('#filters button[type=submit]'); await b.sleep(900);
    paso('filtro → tarjetas: ' + await b.evalJs(`document.querySelectorAll('#mine-list [data-action=submit]').length`));
    await b.click(btn('#mine-list', 'Enviar a revisión', MARCA)); paso('enviar → ' + await toast()); await b.sleep(3500);
    await b.click('#filters button[type=submit]'); await b.sleep(1200);
    paso('estado tras enviar: ' + (await txt('#mine-list')).split('\n').filter((l) => /revisi|Borrador|Pendiente/i.test(l)).slice(0, 2).join(' / '));
    await b.shot(`${OUT}/ui-04-autor-enviada.png`); await logout();
    // ---- ADMIN: HU-04
    await login('admin', 'admin123'); await b.sleep(900);
    const formAsignar = `[...document.querySelectorAll('#pending-list form.assign')].find(f=>f.closest('.item').textContent.includes(${JSON.stringify(MARCA)}))`;
    await b.waitFor(formAsignar, 'pregunta pendiente para asignar');
    await b.click(`(${formAsignar}.querySelector('input[name=reviewer][value="4"]'))`);
    await b.shot(`${OUT}/ui-05-admin-asignar.png`);
    await b.click(`(${formAsignar}.querySelector('button[type=submit]'))`); paso('asignar → ' + await toast());
    await tab('tab-all'); paso('todas las preguntas: ' + await b.evalJs(`document.querySelectorAll('#all-list [data-action=history]').length`) + ' con historial'); await logout();
    // ---- REVISOR: HU-05 (rechazo)
    await login('revisor1', 'revisor123'); await b.sleep(900);
    const formEval = `[...document.querySelectorAll('#review-list form.evaluate')].find(f=>f.closest('.item').textContent.includes(${JSON.stringify(MARCA)}))`;
    await b.waitFor(formEval, 'pregunta asignada');
    await b.click(`(${formEval}.querySelector('button[value=REJECTED]'))`); paso('rechazar sin observaciones → ' + await toast());
    await b.type(`(${formEval}.querySelector('textarea'))`, 'El contexto no dice qué regla cambia; precise el escenario.');
    await b.shot(`${OUT}/ui-06-revisor-evaluar.png`);
    await b.click(`(${formEval}.querySelector('button[value=REJECTED]'))`); paso('rechazar → ' + await toast()); await logout();
    // ---- AUTOR: HU-06
    await login('autor1', 'autor123'); await b.type('#filters input[name=keyword]', MARCA); await b.click('#filters button[type=submit]'); await b.sleep(1200);
    await b.shot(`${OUT}/ui-07-autor-rechazada.png`);
    await b.click(btn('#mine-list', 'Historial de revisiones', MARCA)); await b.waitFor(`document.querySelector('#dialog').open`, 'diálogo de historial'); await b.sleep(500);
    paso('historial: ' + (await txt('#dialog-content')).replace(/\n+/g, ' | ').slice(0, 260));
    await b.shot(`${OUT}/ui-08-autor-historial-ronda1.png`);
    await b.click('#dialog-content [data-improve]'); await b.waitFor(`!document.querySelector('#tab-form').hidden`, 'formulario de versión mejorada'); await b.sleep(500);
    paso('formulario: ' + await txt('#form-title') + ' | nota: ' + (await txt('#form-observations')).replace(/\n+/g, ' ').slice(0, 140));
    await b.type('#question-form textarea[name=context]', `Un equipo mantiene un servicio (${MARCA}) cuyas reglas de validación cambian cada semestre según la convocatoria.`);
    await b.shot(`${OUT}/ui-09-autor-version-mejorada.png`, true);
    await b.click('#question-form button[type=submit]'); paso('guardar versión mejorada → ' + await toast());
    await b.waitFor(`!document.querySelector('#tab-mine').hidden`, 'volver a la lista'); await b.sleep(900);
    paso('tarjeta: ' + (await txt('#mine-list')).split('\n').filter((l) => /versi|Borrador/i.test(l)).slice(0, 3).join(' / '));
    await b.click(btn('#mine-list', 'Enviar a revisión', MARCA)); paso('reenviar → ' + await toast()); await b.sleep(3500); await logout();
    // ---- ADMIN ronda 2 y REVISOR aprueba
    await login('admin', 'admin123'); await b.sleep(900); await b.waitFor(formAsignar, 'pendiente ronda 2');
    await b.click(`(${formAsignar}.querySelector('input[name=reviewer][value="4"]'))`);
    await b.click(`(${formAsignar}.querySelector('button[type=submit]'))`); paso('asignar ronda 2 → ' + await toast()); await logout();
    await login('revisor1', 'revisor123'); await b.sleep(900); await b.waitFor(formEval, 'asignada ronda 2');
    await b.click(`(${formEval}.querySelector('button[value=APPROVED]'))`); paso('aprobar → ' + await toast());
    await tab('tab-notifications'); await b.shot(`${OUT}/ui-11-revisor-notificaciones.png`); await logout();
    // ---- AUTOR: resultado final
    await login('autor1', 'autor123'); await b.type('#filters input[name=keyword]', MARCA); await b.click('#filters button[type=submit]'); await b.sleep(1200);
    await b.click(btn('#mine-list', 'Historial de revisiones', MARCA)); await b.waitFor(`document.querySelector('#dialog').open`, 'historial final'); await b.sleep(500);
    paso('historial final: ' + (await txt('#dialog-content')).replace(/\n+/g, ' | ').slice(0, 300));
    await b.shot(`${OUT}/ui-10-autor-historial-dos-rondas.png`);
    await b.click('#dialog button[value=close]'); await tab('tab-notifications');
    paso('notificaciones del autor: ' + await b.evalJs(`document.querySelector('#notifications-list').children.length`));
    await b.shot(`${OUT}/ui-12-autor-notificaciones.png`); await logout();
    // ---- ESTUDIANTE: solo notificaciones
    await login('estudiante1', 'estudiante123'); await logout();
    console.log('FLUJO COMPLETO');
} catch (e) { console.log('FALLO: ' + e.message); await b.shot(`${OUT}/fallo.png`).catch(() => {}); }
console.log('--- consola del navegador:'); console.log(b.logs.join('\n') || '(sin errores)');
b.close(); process.exit(0);
