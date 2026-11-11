import { h, hanzi, pinyin, speak, header, progress, confetti, confirmDialog } from '../ui.js';
import * as S from '../store.js';
import { pool, writable, isAmbiguous } from '../quiz.js';
import { setFocus } from '../app.js';
import { icon } from '../icons.js';
import { judge } from '../hand.js';

/** Kotak tulis: menyimpan goresan sebagai [[{x,y}…]…] dan menggambarnya. */
export function handPad({ ghost = null, onStroke = () => {} } = {}) {
  const c = h('canvas'); const strokes = []; let cur = null; let color = null;
  const ghostEl = ghost ? h('div.ghost.hz', ghost) : null;
  const wrap = h('div.wrap0.wpad', c, ghostEl);
  function size() { const r = c.getBoundingClientRect(); const d = devicePixelRatio || 1; c.width = r.width * d; c.height = r.width * d; draw(); }
  function draw() {
    const ctx = c.getContext('2d'); const W = c.width; const d = devicePixelRatio || 1;
    ctx.clearRect(0, 0, W, W);
    ctx.setLineDash([10 * d, 10 * d]); ctx.strokeStyle = getComputedStyle(document.body).getPropertyValue('--line'); ctx.lineWidth = 2 * d;
    ctx.beginPath(); ctx.moveTo(W / 2, 0); ctx.lineTo(W / 2, W); ctx.moveTo(0, W / 2); ctx.lineTo(W, W / 2);
    ctx.moveTo(0, 0); ctx.lineTo(W, W); ctx.moveTo(W, 0); ctx.lineTo(0, W); ctx.stroke(); ctx.setLineDash([]);
    ctx.strokeStyle = color || getComputedStyle(document.body).getPropertyValue('--text'); ctx.lineWidth = 9 * d; ctx.lineCap = 'round'; ctx.lineJoin = 'round';
    for (const s of [...strokes, cur].filter(Boolean)) {
      ctx.beginPath(); s.forEach((p, i) => (i ? ctx.lineTo(p.x * d, p.y * d) : ctx.moveTo(p.x * d, p.y * d)));
      if (s.length === 1) ctx.lineTo(s[0].x * d + 0.1, s[0].y * d); ctx.stroke();
    }
  }
  const pos = (e) => { const r = c.getBoundingClientRect(); return { x: e.clientX - r.left, y: e.clientY - r.top }; };
  c.addEventListener('pointerdown', (e) => { if (wrap.dataset.locked) return; e.preventDefault(); c.setPointerCapture(e.pointerId); cur = [pos(e)]; color = null; draw(); });
  c.addEventListener('pointermove', (e) => { if (!cur) return; cur.push(pos(e)); draw(); });
  const up = () => { if (!cur) return; strokes.push(cur); cur = null; draw(); onStroke(strokes); };
  c.addEventListener('pointerup', up); c.addEventListener('pointercancel', up);
  new ResizeObserver(size).observe(c);
  return {
    el: wrap, strokes,
    undo() { strokes.pop(); draw(); onStroke(strokes); },
    clear() { strokes.length = 0; color = null; draw(); onStroke(strokes); },
    tint(col) { color = col; draw(); },
    lock(on) { if (on) wrap.dataset.locked = '1'; else delete wrap.dataset.locked; },
    setGhost(t, alpha) { if (ghostEl) { ghostEl.textContent = t; ghostEl.style.opacity = alpha; } },
  };
}

/** Tulis Hanzi: lihat pinyin (dan arti kalau pinyinnya dipakai beberapa kata), tulis hanzinya. */
export function writeGame(cfg, onExit, onAgain) {
  const list = pool(cfg.level, cfg.source, cfg.count, writable);
  let idx = 0, score = 0, finished = false;
  const root = h('div');
  setFocus(true);
  const stop = () => setFocus(false);

  function exit() {
    if (idx > 0 && !finished) confirmDialog('Keluar dari Latihan?', 'Kata yang sudah ditulis tetap tercatat.', 'Keluar', () => { stop(); onExit(); }, true);
    else { stop(); onExit(); }
  }

  function end() {
    finished = true; stop();
    if (score * 100 / list.length >= 80) confetti();
    root.replaceChildren(header('Hasil Tulis Hanzi', `${score} dari ${list.length} kata benar`, { back: onExit }),
      h('div.wrap.stack', h('div.brand', { style: { textAlign: 'center' } }, h('div.big-ico', icon('pen')), h('h2', `${score} dari ${list.length} Benar`)),
        h('button.btn', { onclick: onAgain }, 'Main Lagi'), h('button.btn.ghost', { onclick: onExit }, 'Selesai')));
  }

  function word() {
    if (idx >= list.length) return end();
    const w = list[idx]; let ci = 0; let wordOk = true; let revealed = false;
    const msg = h('p.muted', { style: { textAlign: 'center', minHeight: '1.5em', fontWeight: 700 } });
    const pad = handPad({ ghost: '', onStroke: () => { msg.textContent = `Tulis huruf ke-${ci + 1} di kotak`; msg.style.color = ''; } });
    const charsRow = h('div.row', { style: { justifyContent: 'center', gap: '6px' } });
    const drawChars = () => charsRow.replaceChildren(...[...w.hanzi].map((ch, i) => h('span.hz', { style: { fontSize: '2rem', width: '44px', textAlign: 'center', borderBottom: '3px solid var(--line)', color: i < ci ? 'var(--ok)' : 'transparent' } }, i < ci ? ch : '口')));
    drawChars();
    msg.textContent = `Tulis huruf ke-1 di kotak`;
    // Baris atas: alat (Urungkan, Hapus, dan Jawaban kalau salah). Baris bawah: tombol utama penuh.
    const buttons = h('div.stack', { style: { gap: '10px' } });
    function setButtons(state) {
      const b = (label, fn, cls = '.ghost') => h(`button.btn${cls}`, { onclick: fn }, label);
      if (state === 'reveal') { buttons.replaceChildren(b([icon('chevron'), ' Lanjut'], nextChar, '')); return; }
      const tools = [b([icon('undo'), ' Urungkan'], () => pad.undo()), b([icon('trash'), ' Hapus'], () => pad.clear())];
      if (state === 'wrong') tools.push(b([icon('eye'), ' Jawaban'], () => { revealed = true; wordOk = false; pad.setGhost(w.hanzi[ci], 0.35); pad.lock(true); msg.textContent = `Jawabannya: ${w.hanzi[ci]}`; setButtons('reveal'); }));
      buttons.replaceChildren(h(tools.length === 3 ? 'div.grid3' : 'div.grid2', tools), b([icon('check'), ' Periksa'], check, ''));
    }
    function check() {
      if (!pad.strokes.length) return;
      const v = judge(w.hanzi[ci], pad.strokes);
      if (v === 'benar') { pad.tint('#16A34A'); msg.textContent = 'Benar!'; msg.style.color = 'var(--ok)'; setTimeout(nextChar, 700); }
      else { wordOk = false; msg.textContent = v === 'hampir' ? 'Hampir! Coba tulis lebih rapi.' : 'Belum tepat. Coba lagi atau lihat jawaban.'; msg.style.color = v === 'hampir' ? 'var(--warn)' : 'var(--bad)'; pad.tint(v === 'hampir' ? '#F59E0B' : '#DC2626'); setButtons('wrong'); }
    }
    function nextChar() {
      ci++; drawChars(); pad.clear(); pad.lock(false); pad.setGhost('', 0);
      if (ci >= w.hanzi.length) {
        if (wordOk && !revealed) score++;
        S.quizAnswer(w.id, wordOk && !revealed); speak(w.hanzi);
        msg.textContent = `${w.hanzi} · ${w.main}`; msg.style.color = '';
        setTimeout(() => { idx++; word(); }, 1300);
      } else { msg.textContent = `Tulis huruf ke-${ci + 1} di kotak`; setButtons('normal'); }
    }
    setButtons('normal');
    root.replaceChildren(
      h('div.hero', h('div.hero-row', h('button.icon-btn.on-brand', { onclick: exit, 'aria-label': 'Tutup' }, icon('close')),
        h('div.hero-text', h('b', 'Tulis Hanzi'), h('p.small', `Kata ${idx + 1} dari ${list.length} · Benar ${score}`))),
        h('div', { style: { marginTop: '12px' } }, progress(idx / list.length))),
      h('div.wrap.stack',
        h('div.card.q-box', h('div', { style: { fontSize: '2.2rem' } }, pinyin(w.pinyin, w.hanzi)),
          isAmbiguous(w) ? h('p.muted', w.main) : null, h('div', { style: { marginTop: '10px' } }, charsRow)),
        pad.el, msg, buttons));
  }

  if (!list.length) { root.replaceChildren(header('Tulis Hanzi', null, { back: onExit }), h('div.empty', h('div.big', icon('sprout')), h('h3', 'Belum Ada Kata'))); return { el: root, cleanup: stop }; }
  word();
  return { el: root, cleanup: stop };
}
