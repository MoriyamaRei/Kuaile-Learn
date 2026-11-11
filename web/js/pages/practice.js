import { h, hanzi, pinyin, speak, header, segmented, progress, confetti, confirmDialog } from '../ui.js';
import * as S from '../store.js';
import { MODES, SOURCES, QTYPE_LABEL, HINTABLE, build, available, ending } from '../quiz.js';
import { go, setFocus } from '../app.js';
import { icon } from '../icons.js';
import { writeGame } from './write.js';
import { numberSetup } from './numbers.js';

const COUNTS = [10, 20, 30, 40, 50, 60, 70, 80, 90, 100];
const HINTS = 3, HINT_MS = 3000;
const pref = { level: 0, source: 'SEMUA', count: 10 };

export function practicePage(mode) {
  if (mode && MODES[mode]) return mode === 'ANGKA' ? numberSetup() : setup(mode);
  return menu();
}

function modeCard(id, i) {
  const m = MODES[id];
  return h('div.card.pad.tap.appear', { style: { animationDelay: `${i * 0.04}s` }, onclick: () => go(`latihan/${id}`) },
    h('div.tile-ico', icon(m.icon)), h('b', m.title), h('p.muted.small', { style: { minHeight: '2.6em' } }, m.sub));
}

function menu() {
  return h('div',
    header('Latihan', 'Uji kemampuanmu membaca hanzi'),
    h('div.wrap.stack',
      h('div.card.pad', { style: { background: 'color-mix(in srgb, var(--gold) 14%, var(--surface))' } },
        h('b', 'Aturan Latihan'),
        h('p.muted.small', `Soal hanya menampilkan hanzi, tanpa pinyin dan arti. Kamu punya ${HINTS} bantuan pinyin per latihan, masing-masing tampil 3 detik. Pinyin, arti, dan suara muncul setelah kamu menjawab.`)),
      h('div.mode-grid.featured', modeCard('KILAT', 0), modeCard('UJIAN', 1)),
      h('h3.section-title', 'Kosakata'),
      h('div.mode-grid', ['HANZI_ARTI', 'HANZI_PINYIN', 'NADA', 'ARTI_HANZI', 'PINYIN_HANZI', 'SUSUN'].map((m, i) => modeCard(m, i + 2))),
      h('h3.section-title', 'Menulis & Angka'),
      h('div.mode-grid', modeCard('TULIS', 8), modeCard('ANGKA', 9))));
}

function setup(mode) {
  const m = MODES[mode];
  const fixed = mode === 'KILAT' || mode === 'UJIAN';
  const needed = mode === 'UJIAN' ? 20 : mode === 'KILAT' ? 4 : 2;
  const root = h('div');
  function draw() {
    const avail = available(mode, pref.level, pref.source);
    const real = fixed ? 0 : Math.min(pref.count, avail);
    const problem = avail < needed ? (avail === 0 ? 'Belum ada kata untuk pilihan ini. Coba ganti level atau sumber kata.' : `Kata untuk pilihan ini baru ${avail}, minimal ${needed}.`) : null;
    root.replaceChildren(
      header(m.title, m.sub, { back: () => go('latihan') }),
      h('div.wrap.stack',
        h('div.card.pad.stack',
          h('b', 'Level'),
          segmented(['HSK 1–4', 'HSK 1', 'HSK 2', 'HSK 3', 'HSK 4'], pref.level, (i) => { pref.level = i; draw(); }),
          h('b', { style: { marginTop: '6px' } }, 'Sumber Kata'),
          h('div.grid2', Object.entries(SOURCES).map(([k, label]) => h(`button.card.pad${k === pref.source ? '.sel' : ''}`,
            { style: { textAlign: 'left', border: k === pref.source ? '2px solid var(--blue)' : '2px solid transparent' }, onclick: () => { pref.source = k; draw(); } },
            h('b', label), h('div.muted.small', `${available(mode, pref.level, k)} kata`))))),
        h('div.card.pad.stack', fixed
          ? [h('b', mode === 'KILAT' ? '60 Detik' : '20 Soal · 10 Menit'),
            h('p.muted.small', mode === 'KILAT' ? 'Jawab sebanyak mungkin sebelum waktu habis. Di mode ini tidak ada bantuan.' : `Soal campuran semua jenis. Lulus kalau nilainya minimal 60%. Bantuan pinyin tetap ${HINTS}×.`)]
          : [h('div.row', h('div.grow', h('b', 'Jumlah Soal'), h('p.muted.small', 'Soal diacak setiap kali main.')), h('b', { style: { fontSize: '1.6rem', color: 'var(--blue)' } }, `${real}`)),
            h('div.chips', COUNTS.map((c) => h(`button.chip${c === pref.count ? '.on' : ''}`, { disabled: c > Math.max(avail, 10) && c !== 10, onclick: () => { pref.count = c; draw(); } }, `${c}`)))]),
        problem ? h('p', { style: { color: 'var(--bad)' } }, problem) : null,
        h('button.btn', { disabled: !!problem, onclick: () => start() }, fixed ? 'Mulai' : `Mulai ${real} Soal`)));
  }
  function start() {
    const cfg = { mode, level: pref.level, source: pref.source, count: Math.min(pref.count, available(mode, pref.level, pref.source)) };
    if (cleanupRef) cleanupRef();
    const page = mode === 'TULIS' ? writeGame(cfg, () => draw(), start) : quizGame(cfg, () => draw(), start);
    root.replaceChildren(page.el || page);
    if (page.cleanup) cleanupRef = page.cleanup;
  }
  let cleanupRef = null;
  draw();
  return { el: root, cleanup: () => { if (cleanupRef) cleanupRef(); setFocus(false); } };
}

/** Bantuan pinyin: 3× per latihan, tampil 3 detik. */
function hints() { let left = HINTS; return { left: () => left, use: () => (left > 0 ? (left--, true) : false) }; }

export function quizGame(cfg, onExit, onAgain) {
  const { mode } = cfg;
  const qs = build(mode, cfg.level, cfg.source, cfg.count);
  const limit = mode === 'KILAT' ? 60 : mode === 'UJIAN' ? 600 : 0;
  let idx = 0, score = 0, answered = 0, timeLeft = limit, finished = false, timer = null;
  const wrongs = []; const hint = hints();
  const root = h('div');
  setFocus(true);

  if (qs.length < 2) {
    root.replaceChildren(header(MODES[mode].title, null, { back: onExit }), h('div.empty', h('div.big', icon('sprout')), h('h3', 'Belum Ada Soal'), h('p', 'Kata untuk pilihan ini belum cukup.')));
    return { el: root };
  }
  if (limit) timer = setInterval(() => { timeLeft--; const t = root.querySelector('.timer'); if (t) t.textContent = fmtTime(timeLeft); if (timeLeft <= 0) end(); }, 1000);
  const fmtTime = (s) => `${Math.floor(s / 60)}:${String(s % 60).padStart(2, '0')}`;

  function exit() {
    if (answered > 0 && !finished) confirmDialog('Keluar dari Latihan?', 'Soal yang sudah dijawab tetap tercatat, tetapi latihan ini tidak selesai.', 'Keluar', () => { stop(); onExit(); }, true);
    else { stop(); onExit(); }
  }
  const stop = () => { if (timer) clearInterval(timer); timer = null; setFocus(false); };

  function end() {
    if (finished) return; finished = true; stop();
    const total = mode === 'KILAT' ? answered : qs.length;
    const pct = total ? Math.round(score * 100 / total) : 0;
    if (mode === 'KILAT') S.setBest('kilat', score);
    if (mode === 'UJIAN') S.setBest('exam', pct);
    if (total && (pct >= 80 || (mode === 'KILAT' && score >= 10))) confetti();
    const passed = mode === 'UJIAN' ? pct >= 60 : null;
    const list = [...new Map(wrongs.map((w) => [w.id, w])).values()];
    root.replaceChildren(
      header('Hasil Latihan', MODES[mode].title, { back: onExit }),
      h('div.wrap.stack',
        h('div.brand', { style: { textAlign: 'center' } },
          h('div.big-ico', icon(mode === 'UJIAN' ? (passed ? 'cap' : 'library') : pct >= 80 ? 'trophy' : pct >= 50 ? 'thumb' : 'muscle')),
          h('h2', mode === 'KILAT' ? `Skor ${score}` : `${score} dari ${total} Benar`),
          h('p.muted', mode === 'UJIAN' ? (passed ? `Lulus dengan nilai ${pct}%!` : `Nilai ${pct}%, belum lulus (minimal 60%).`) : mode === 'KILAT' ? `${answered} dijawab · Rekor ${S.state.best.kilat}` : `Akurasi ${pct}%`)),
        list.length ? h('h3.section-title', 'Perlu Diulang') : null,
        ...list.map((w) => h('a.card.word-row', { href: `#/kata/${w.id}` }, hanzi(w.hanzi), h('div.grow', pinyin(w.pinyin, w.hanzi), h('div.mean', w.main)))),
        h('button.btn', { onclick: () => onAgain() }, 'Main Lagi'),
        h('button.btn.ghost', { onclick: onExit }, 'Selesai')));
  }

  function draw() {
    if (finished) return;
    const q = qs[idx];
    let picked = null;
    const hintable = HINTABLE.has(q.type) && mode !== 'KILAT';
    const hintBox = h('div', { style: { minHeight: '28px', textAlign: 'center' } });
    const top = h('div.hero',
      h('div.hero-row',
        h('button.icon-btn.on-brand', { onclick: exit, 'aria-label': 'Tutup' }, icon('close')),
        h('div.hero-text', h('b', MODES[mode].title), h('p.small', mode === 'KILAT' ? `Skor ${score}` : `Soal ${idx + 1} dari ${qs.length} · Benar ${score}`)),
        limit ? h('span.timer', fmtTime(timeLeft)) : null),
      mode !== 'KILAT' ? h('div', { style: { marginTop: '12px' } }, progress(idx / qs.length)) : null);

    const prompt = h('div.card.q-box.appear',
      (mode === 'UJIAN' || mode === 'KILAT') ? h('span.pill', QTYPE_LABEL[q.type]) : null,
      q.type === 'ARTI_HANZI' ? h('h2', { style: { margin: '14px 0' } }, q.word.main)
        : q.type === 'PINYIN_HANZI' ? h('div', { style: { fontSize: '2rem', margin: '14px 0' } }, pinyin(q.word.pinyin, q.word.hanzi))
          : q.type === 'SUSUN' ? h('div', h('p.muted', { style: { margin: '10px 0' } }, 'Susun potongan menjadi kalimat yang benar'), h('h3', q.word.main))
            : hanzi(q.word.hanzi, 'q-hz'),
      hintBox);

    const reveal = h('div');
    function after(ok) {
      answered++; if (ok) score++; else wrongs.push(q.word);
      S.quizAnswer(q.word.id, ok);
      speak(q.type === 'SUSUN' ? q.sentence : q.word.hanzi);
      reveal.replaceChildren(h(`div.feedback.${ok ? 'ok' : 'no'}.appear`,
        h('b.row', icon(ok ? 'check' : 'warn'), ok ? 'Benar!' : 'Belum Tepat'),
        h('div.row', { style: { marginTop: '6px' } }, hanzi(q.word.hanzi, 'big-hz'), h('div.grow', pinyin(q.word.pinyin, q.word.hanzi), h('div', { style: { fontWeight: 700 } }, q.word.main))),
        q.type === 'SUSUN' ? h('p.hz', { style: { fontSize: '1.2rem', marginTop: '6px' } }, q.sentence) : null));
      const delay = mode === 'KILAT' ? (ok ? 450 : 1300) : q.type === 'SUSUN' ? (ok ? 1800 : 3200) : ok ? 1400 : 2400;
      setTimeout(() => { if (finished) return; if (idx + 1 >= qs.length) end(); else { idx++; draw(); } }, delay);
    }

    let body;
    if (q.type === 'SUSUN') {
      const chosen = [];
      const line = h('div.answer-line');
      const pool = h('div.chips');
      const refresh = () => {
        line.replaceChildren(...chosen.map((i, k) => h('button.chunk.hz', { onclick: () => { if (picked) return; chosen.splice(k, 1); refresh(); } }, q.options[i])));
        pool.replaceChildren(...q.options.map((c, i) => h(`button.chunk.hz${chosen.includes(i) ? '.used' : ''}`, { onclick: () => { if (picked) return; chosen.push(i); refresh(); if (chosen.length === q.options.length) { picked = true; const ok = chosen.map((j) => q.options[j]).join('') === q.chunks.join(''); line.classList.add(ok ? 'pop' : 'no'); after(ok); } } }, c)));
      };
      refresh();
      body = h('div.stack', line, pool, h('p.muted.small', `Tanda baca akhir: ${ending(q.sentence) || '–'}`));
    } else {
      const btns = q.options.map((o, i) => h('button.opt', { onclick: () => {
        if (picked !== null) return; picked = i; const ok = i === q.answer;
        btns[i].classList.add(ok ? 'ok' : 'no'); btns[q.answer].classList.add('ok');
        if (ok) btns[i].classList.add('pop');
        after(ok);
      } }, q.type === 'ARTI_HANZI' || q.type === 'PINYIN_HANZI' ? hanzi(o) : q.type === 'HANZI_PINYIN' || q.type === 'NADA' ? pinyin(o, q.word.hanzi) : o));
      body = h('div.opts', btns);
    }

    const hintBtn = hintable ? h('button.chip', { onclick: (e) => {
      if (picked !== null || !hint.use()) return;
      hintBox.replaceChildren(h('div.appear', { style: { fontSize: '1.3rem' } }, pinyin(q.word.pinyin, q.word.hanzi)));
      e.currentTarget.disabled = true; e.currentTarget.replaceChildren(icon('bulb'), ` Bantuan (${hint.left()} lagi)`);
      setTimeout(() => hintBox.replaceChildren(), HINT_MS);
    } }, icon('bulb'), ` Bantuan Pinyin (${hint.left()} lagi)`) : null;

    root.replaceChildren(top, h('div.wrap.stack', prompt, hintBtn && hint.left() > 0 ? h('div', { style: { textAlign: 'center' } }, hintBtn) : null, body, reveal));
  }

  draw();
  return { el: root, cleanup: stop };
}
