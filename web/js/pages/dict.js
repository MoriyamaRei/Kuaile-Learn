import { h, hanzi, pinyin, speak, speakBtn, header, levelBadge, segmented } from '../ui.js';
import * as S from '../store.js';
import * as L from '../logic.mjs';
import { words, byId, theme, themes, posName } from '../data.js';
import { go, back } from '../app.js';
import { handPad } from './write.js';
import { prepare, rank } from '../hand.js';
import { icon, themeIcon } from '../icons.js';

const f = { q: '', level: 0, status: -1, star: false, theme: null };
const STATUS = ['Baru', 'Sedang Dipelajari', 'Hafal'];
const statusIdx = (id) => ({ baru: 0, belajar: 1, hafal: 2 })[S.status(id)];

function results() {
  const q = f.q.trim().toLowerCase();
  const qp = L.pyPlain(q).replace(/ /g, '');
  let list = words.filter((w) => (f.level === 0 || w.level === f.level) && (f.status < 0 || statusIdx(w.id) === f.status)
    && (!f.star || S.card(w.id).star) && (!f.theme || w.theme === f.theme)
    && (!q || w.hanzi.includes(q) || (qp && w.plain.includes(qp)) || w.search.includes(q)));
  if (q) {
    const rk = (w) => (w.hanzi === q || w.plain === qp || w.main.toLowerCase() === q ? 0 : w.plain.startsWith(qp) || w.main.toLowerCase().startsWith(q) ? 1 : 2);
    list = list.sort((a, b) => rk(a) - rk(b));
  }
  return list;
}

const row = (w) => h('a.card.word-row.tap', { href: `#/kata/${w.id}` },
  hanzi(w.hanzi), h('div.grow', pinyin(w.pinyin, w.hanzi), h('div.mean', w.main)),
  S.card(w.id).star ? h('span.star-on', icon('star')) : null, levelBadge(w.level));

export function dictPage() {
  const list = h('div.stack.word-list');
  const count = h('p.muted.small');
  let shown = 60;
  function refresh() {
    const r = results(); shown = Math.min(shown, Math.max(60, shown));
    count.textContent = `${r.length} kata`;
    list.replaceChildren(...r.slice(0, shown).map(row),
      r.length > shown ? h('button.btn.ghost', { onclick: () => { shown += 100; refresh(); } }, `Tampilkan Lebih Banyak (${r.length - shown} lagi)`) : null,
      r.length === 0 ? h('div.empty', h('div.big', icon('search')), h('p', 'Tidak ada kata yang cocok.')) : null);
  }
  const input = h('input', { placeholder: 'Cari hanzi, pinyin, atau arti', value: f.q, oninput: (e) => { f.q = e.target.value; shown = 60; refresh(); } });
  const filters = h('div.card.pad.stack', { style: { display: 'none' } },
    h('b', 'Status'), h('div.chips', ['Semua', ...STATUS].map((s, i) => h(`button.chip${f.status === i - 1 ? '.on' : ''}`, { onclick: (e) => { f.status = i - 1; e.currentTarget.parentNode.querySelectorAll('.chip').forEach((x, k) => x.classList.toggle('on', k === i)); refresh(); } }, s))),
    h('b', 'Tema'), h('div.chips', [h(`button.chip${!f.theme ? '.on' : ''}`, { onclick: (e) => { f.theme = null; mark(e); refresh(); } }, 'Semua'),
      ...Object.entries(themes).map(([k, t]) => h(`button.chip${f.theme === k ? '.on' : ''}`, { onclick: (e) => { f.theme = k; mark(e); refresh(); } }, themeIcon(k), ` ${t.name}`))]),
    h('label.row', h('span.grow.row', icon('star'), 'Hanya Kata Berbintang'), h(`button.switch${f.star ? '.on' : ''}`, { onclick: (e) => { f.star = !f.star; e.currentTarget.classList.toggle('on', f.star); refresh(); } })));
  function mark(e) { e.currentTarget.parentNode.querySelectorAll('.chip').forEach((x) => x.classList.toggle('on', x === e.currentTarget)); }

  const el = h('div',
    header('Kamus', `${words.length} kosakata HSK 1–4`, {
      actions: [h('button.icon-btn.on-brand', { title: 'Pengubah Angka', onclick: () => go('pengubah'), 'aria-label': 'Pengubah Angka' }, icon('calc'))],
      extra: h('div.stack', { style: { marginTop: '14px' } },
        h('div.row',
          h('div.search.grow', icon('search'), input, h('button', { 'aria-label': 'Hapus', onclick: () => { input.value = ''; f.q = ''; refresh(); } }, icon('close'))),
          h('button.icon-btn.on-brand', { title: 'Cari Lewat Tulisan', onclick: () => writeSearch((c) => { input.value += c; f.q = input.value; refresh(); }), 'aria-label': 'Cari Lewat Tulisan' }, icon('pen')),
          h('button.icon-btn.on-brand', { title: 'Filter', onclick: () => { filters.style.display = filters.style.display ? '' : 'none'; }, 'aria-label': 'Filter' }, icon('filter'))),
        segmented(['Semua', 'HSK 1', 'HSK 2', 'HSK 3', 'HSK 4'], f.level, (i) => { f.level = i; el.querySelectorAll('.hero .seg button').forEach((b, k) => b.classList.toggle('on', k === i)); refresh(); })),
    }),
    h('div.wrap.stack', filters, count, list));
  refresh();
  return el;
}

/** Cari lewat tulisan: tulis satu hanzi (urutan goresan bebas), pilih dari 10 kandidat. */
function writeSearch(onPick) {
  const cands = h('div.stack', h('p.muted.small', 'Menyiapkan pengenal tulisan…'));
  let ready = false; let picked = '';
  const pickedEl = h('b.hz', { style: { fontSize: '1.6rem', color: 'var(--blue)' } });
  const pad = handPad({ onStroke: (st) => update(st) });
  function update(st) {
    if (!ready) return;
    if (!st.length) { cands.replaceChildren(h('p.muted.small', 'Kandidat hanzi akan muncul di sini.')); return; }
    const r = rank(st, 10);
    cands.replaceChildren(...[0, 5].map((o) => h('div.grid5', r.slice(o, o + 5).map((c, i) => h(`button.opt.hz${o + i === 0 ? '.ok' : ''}`, { style: { padding: '8px 0', fontSize: '1.7rem' }, onclick: () => { picked += c; pickedEl.textContent = picked; onPick(c); pad.clear(); } }, c)))));
  }
  const close = () => back2.remove();
  const back2 = h('div.modal-back', { onclick: (e) => { if (e.target === back2) close(); } },
    h('div.modal',
      h('div.row', h('div.grow', h('h3', 'Cari Lewat Tulisan'), h('p.muted.small', 'Tulis satu hanzi. Urutan goresan bebas.')), pickedEl),
      cands, pad.el,
      // Dua tombol alat di satu baris, tombol Selesai penuh di bawahnya (rapi di layar HP yang sempit).
      h('div.grid2', h('button.btn.ghost', { onclick: () => pad.undo() }, icon('undo'), ' Urungkan'), h('button.btn.ghost', { onclick: () => pad.clear() }, icon('trash'), ' Bersihkan')),
      h('button.btn', { onclick: close }, icon('check'), ' Selesai')));
  document.body.append(back2);
  prepare((p) => { if (!ready) cands.replaceChildren(h('p.muted.small', `Menyiapkan pengenal tulisan… ${Math.round(p * 100)}%`), h('div.bar', h('i', { style: { width: `${p * 100}%` } }))); })
    .then(() => { ready = true; update(pad.strokes); });
}

export function wordPage(id) {
  const w = byId.get(id);
  if (!w) return h('div', header('Kata Tidak Ditemukan', null, { back }));
  const c = S.card(id);
  const starIco = () => icon(S.card(id).star ? 'star' : 'starOutline');
  const star = h('button.icon-btn.on-brand', { title: 'Bintang', 'aria-label': 'Bintang', onclick: () => { S.toggleStar(id); star.replaceChildren(starIco()); } }, starIco());
  speak(w.hanzi);
  const st = S.status(id);
  return h('div',
    header('Detail Kata', theme(w.theme).name, { back, actions: [star] }),
    h('div.wrap.stack',
      h('div.card.pad.appear', { style: { textAlign: 'center' } },
        h('div.row', levelBadge(w.level), w.book ? h('span.pill', `4下 Bab ${w.book}`) : null, h('span.grow'), speakBtn(w.hanzi)),
        hanzi(w.hanzi, 'q-hz'), h('div', { style: { fontSize: '1.7rem' } }, pinyin(w.pinyin, w.hanzi)),
        h('p.muted', { style: { marginTop: '6px' } }, `Status: ${{ baru: 'Baru', belajar: 'Sedang Dipelajari', hafal: 'Hafal' }[st]} · Benar ${c.right} · Salah ${c.wrong}`)),
      ...w.senses.map((s, i) => h('div.card.pad.appear', { style: { animationDelay: `${0.05 * (i + 1)}s` } },
        h('span.pill', posName(s.pos)), h('h2', { style: { margin: '8px 0' } }, s.mean.charAt(0).toUpperCase() + s.mean.slice(1)),
        s.example ? h('div.ex', { onclick: () => speak(s.example) }, h('span.hz', s.example), icon('speaker')) : null))));
}
