// Komponen tampilan kecil yang dipakai bersama.
import * as L from './logic.mjs';
import { state } from './store.js';
import { icon } from './icons.js';

// replaceChildren/append bawaan menulis null sebagai teks "null"; abaikan anak kosong.
for (const m of ['replaceChildren', 'append']) {
  const orig = Element.prototype[m];
  Element.prototype[m] = function (...kids) { return orig.apply(this, kids.filter((k) => k !== null && k !== undefined && k !== false)); };
}

/** Membuat elemen: h('div.kelas#id', {atribut, onclick}, anak…) */
export function h(sel, attrs = {}, ...kids) {
  const [tagPart, ...rest] = sel.split(/(?=[.#])/);
  const el = document.createElement(tagPart || 'div');
  for (const r of rest) { if (r[0] === '.') el.classList.add(r.slice(1)); else el.id = r.slice(1); }
  if (attrs && (typeof attrs !== 'object' || attrs instanceof Node || Array.isArray(attrs))) { kids.unshift(attrs); attrs = {}; }
  for (const [k, v] of Object.entries(attrs || {})) {
    if (v === undefined || v === null || v === false) continue;
    if (k.startsWith('on')) el.addEventListener(k.slice(2), v);
    else if (k === 'style' && typeof v === 'object') { for (const [sk, sv] of Object.entries(v)) { if (sk.startsWith('--')) el.style.setProperty(sk, sv); else el.style[sk] = sv; } }
    else if (k === 'html') el.innerHTML = v;
    else el.setAttribute(k, v === true ? '' : v);
  }
  for (const k of kids.flat(Infinity)) { if (k === null || k === undefined || k === false) continue; el.append(k instanceof Node ? k : document.createTextNode(String(k))); }
  return el;
}

/** Pinyin berwarna nada (1 merah, 2 hijau, 3 biru, 4 ungu). */
export function pinyin(py, hanzi, cls = '') {
  const segs = JSON.parse(L.pySegments(py, hanzi || ''));
  return h(`span.py${cls ? '.' + cls : ''}`, segs.map((s) => h(`span.t${s.tone < 0 ? 'x' : s.tone}`, s.t)));
}

export const hanzi = (text, cls = '') => h(`span.hz${cls ? '.' + cls : ''}`, { lang: 'zh-CN' }, text);

// ---------- Suara ----------
let voice = null;
function pickVoice() {
  const vs = speechSynthesis.getVoices();
  voice = vs.find((v) => /zh[-_]CN/i.test(v.lang)) || vs.find((v) => /^zh/i.test(v.lang)) || null;
}
if ('speechSynthesis' in window) { pickVoice(); speechSynthesis.onvoiceschanged = pickVoice; }
export function speak(text) {
  if (!('speechSynthesis' in window) || !text) return;
  speechSynthesis.cancel();
  const u = new SpeechSynthesisUtterance(text.replace(/[（(].*?[）)]/g, ''));
  u.lang = 'zh-CN'; if (voice) u.voice = voice;
  u.rate = state.settings.voice === 0 ? 0.6 : 0.9;
  speechSynthesis.speak(u);
}
export const speakBtn = (text, cls = '') => h(`button.icon-btn.speak${cls ? '.' + cls : ''}`, { 'aria-label': 'Dengarkan', title: 'Dengarkan', onclick: (e) => { e.stopPropagation(); speak(text); } }, icon('speaker'));

// ---------- Kabar singkat ----------
export function toast(msg) {
  const t = h('div.toast', msg);
  document.body.append(t);
  requestAnimationFrame(() => t.classList.add('show'));
  setTimeout(() => { t.classList.remove('show'); setTimeout(() => t.remove(), 300); }, 2600);
}

// ---------- Konfeti ----------
export function confetti() {
  const c = h('canvas.confetti'); document.body.append(c);
  const ctx = c.getContext('2d'); const W = c.width = innerWidth, H = c.height = innerHeight;
  const cols = ['#2563EB', '#60A5FA', '#BFDBFE', '#FBBF24', '#ffffff', '#1E40AF'];
  const ps = Array.from({ length: 140 }, () => ({ x: Math.random() * W, y: -Math.random() * H, vy: 2 + Math.random() * 4, vx: (Math.random() - 0.5) * 2, r: Math.random() * 6.28, vr: (Math.random() - 0.5) * 0.3, w: 6 + Math.random() * 8, h: 4 + Math.random() * 6, c: cols[(Math.random() * cols.length) | 0] }));
  const start = performance.now();
  (function tick(t) {
    const p = (t - start) / 3500; if (p >= 1) { c.remove(); return; }
    ctx.clearRect(0, 0, W, H); ctx.globalAlpha = p > 0.8 ? (1 - p) / 0.2 : 1;
    for (const q of ps) { q.x += q.vx; q.y += q.vy; q.r += q.vr; ctx.save(); ctx.translate(q.x, q.y); ctx.rotate(q.r); ctx.fillStyle = q.c; ctx.fillRect(-q.w / 2, -q.h / 2, q.w, q.h * Math.abs(Math.cos(q.r))); ctx.restore(); }
    requestAnimationFrame(tick);
  })(start);
}

/** Kepala halaman biru dengan judul, subjudul, dan tombol kembali/opsional. */
export function header(title, sub, { back, actions = [], extra } = {}) {
  return h('header.hero',
    h('div.hero-row',
      back ? h('button.icon-btn.on-brand', { 'aria-label': 'Kembali', onclick: back }, icon('back')) : null,
      h('div.hero-text', h('h1', title), sub ? h('p', sub) : null),
      ...actions),
    extra || null);
}

export const progress = (f, cls = '') => h(`div.bar${cls ? '.' + cls : ''}`, h('i', { style: { width: `${Math.round(Math.min(1, Math.max(0, f)) * 100)}%` } }));

export const LEVEL_COLOR = { 1: '#22C55E', 2: '#3B82F6', 3: '#F97316', 4: '#8B5CF6' };
export const levelBadge = (lv) => h('span.lvl', { style: { '--c': LEVEL_COLOR[lv] } }, `HSK ${lv}`);

export function segmented(opts, sel, onSelect, cls = '') {
  return h(`div.seg${cls ? '.' + cls : ''}`, opts.map((o, i) => h(`button${i === sel ? '.on' : ''}`, { onclick: () => onSelect(i) }, o)));
}

export function confirmDialog(title, text, okLabel, onOk, danger = false) {
  const close = () => back.remove();
  const back = h('div.modal-back', { onclick: (e) => { if (e.target === back) close(); } },
    h('div.modal', h('h3', title), h('p', text),
      h('div.modal-actions',
        h('button.btn.ghost', { onclick: close }, 'Batal'),
        h(`button.btn${danger ? '.danger' : ''}`, { onclick: () => { close(); onOk(); } }, okLabel))));
  document.body.append(back);
}
