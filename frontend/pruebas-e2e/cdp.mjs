// Controlador mínimo de Chromium por DevTools Protocol: clics de ratón y tecleo reales.
import { spawn } from 'node:child_process';
export async function launch(profile, port = 9333) {
    const proc = spawn('chromium', ['--headless=new', `--remote-debugging-port=${port}`, `--user-data-dir=${profile}`,
        '--window-size=1366,900', '--hide-scrollbars', '--lang=es', 'about:blank'], { stdio: 'ignore' });
    let target;
    for (let i = 0; i < 60; i++) {
        try { const l = await (await fetch(`http://127.0.0.1:${port}/json`)).json(); target = l.find((t) => t.type === 'page'); if (target) break; } catch {}
        await new Promise((r) => setTimeout(r, 250));
    }
    const ws = new WebSocket(target.webSocketDebuggerUrl);
    await new Promise((r) => (ws.onopen = r));
    let id = 0; const pending = new Map(); const logs = [];
    ws.onmessage = (m) => {
        const d = JSON.parse(m.data);
        if (d.id && pending.has(d.id)) { const { res, rej } = pending.get(d.id); pending.delete(d.id); d.error ? rej(new Error(d.error.message)) : res(d.result); }
        if (d.method === 'Runtime.exceptionThrown') logs.push('EXCEPCION: ' + (d.params.exceptionDetails.exception?.description || d.params.exceptionDetails.text));
        if (d.method === 'Runtime.consoleAPICalled' && ['error', 'warning'].includes(d.params.type)) logs.push(d.params.type + ': ' + d.params.args.map((a) => a.value ?? a.description).join(' '));
        if (d.method === 'Log.entryAdded' && d.params.entry.level === 'error') logs.push('log: ' + d.params.entry.text + ' ' + (d.params.entry.url || ''));
        if (d.method === 'Page.javascriptDialogOpening') { logs.push('dialogo: ' + d.params.message); send('Page.handleJavaScriptDialog', { accept: true }); }
    };
    const send = (method, params = {}) => new Promise((res, rej) => { pending.set(++id, { res, rej }); ws.send(JSON.stringify({ id, method, params })); });
    for (const d of ['Page', 'Runtime', 'Log', 'DOM']) await send(d + '.enable');
    await send('Emulation.setDeviceMetricsOverride', { width: 1366, height: 900, deviceScaleFactor: 1, mobile: false });
    const sleep = (ms) => new Promise((r) => setTimeout(r, ms));
    const evalJs = async (expr) => { const r = await send('Runtime.evaluate', { expression: expr, returnByValue: true, awaitPromise: true }); if (r.exceptionDetails) throw new Error(r.exceptionDetails.exception?.description || 'eval'); return r.result.value; };
    const waitFor = async (expr, what = expr, ms = 10000) => { const t = Date.now(); while (Date.now() - t < ms) { if (await evalJs(`!!(${expr})`)) return; await sleep(150); } throw new Error('Tiempo agotado esperando: ' + what); };
    const box = async (sel) => { const b = await evalJs(`(()=>{const e=${sel.startsWith('(') ? sel : `document.querySelector(${JSON.stringify(sel)})`}; if(!e) return null; e.scrollIntoView({block:'center'}); const r=e.getBoundingClientRect(); return {x:r.x+r.width/2,y:r.y+r.height/2,w:r.width,h:r.height};})()`); if (!b || !b.w) throw new Error('No visible: ' + sel); return b; };
    const click = async (sel) => { const b = await box(sel); await sleep(80); const p = { x: b.x, y: b.y, button: 'left', clickCount: 1 };
        await send('Input.dispatchMouseEvent', { type: 'mouseMoved', x: b.x, y: b.y }); await send('Input.dispatchMouseEvent', { type: 'mousePressed', ...p }); await send('Input.dispatchMouseEvent', { type: 'mouseReleased', ...p }); await sleep(250); };
    const type = async (sel, text) => { await click(sel); await send('Input.dispatchKeyEvent', { type: 'keyDown', key: 'a', code: 'KeyA', modifiers: 2, windowsVirtualKeyCode: 65 }); await send('Input.dispatchKeyEvent', { type: 'keyUp', key: 'a', code: 'KeyA', modifiers: 2, windowsVirtualKeyCode: 65 }); await send('Input.dispatchKeyEvent', { type: 'keyDown', key: 'Delete', code: 'Delete', windowsVirtualKeyCode: 46 }); await send('Input.dispatchKeyEvent', { type: 'keyUp', key: 'Delete', code: 'Delete', windowsVirtualKeyCode: 46 }); await send('Input.insertText', { text }); await sleep(60); };
    const shot = async (file, full = false) => { const { writeFileSync } = await import('node:fs'); const r = await send('Page.captureScreenshot', { format: 'png', captureBeyondViewport: full }); writeFileSync(file, Buffer.from(r.data, 'base64')); };
    const goto = async (url) => { await send('Page.navigate', { url }); await sleep(900); };
    return { send, evalJs, waitFor, click, type, shot, goto, sleep, logs, close: () => { ws.close(); proc.kill(); } };
}
