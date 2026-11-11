import { h, hanzi, pinyin, speak, header, segmented, progress, confetti, confirmDialog, toast } from '../ui.js';
import * as S from '../store.js';
import * as L from '../logic.mjs';
import { pick } from '../data.js';
import { go, back, setFocus } from '../app.js';
import { icon, catIcon } from '../icons.js';

const CATS = () => JSON.parse(L.numCats());
const RANGES = () => JSON.parse(L.numRanges());
const DIFF = {
  UMUM: ['Angka 0–99', 'Angka 100–9.999', 'Angka 10.000–100 juta'],
  HARGA: ['Harga yuan bulat', 'Yuan dengan 毛/分 dan soal cerita', 'Harga lengkap dan label 元/角/分'],
  DISKON: ['Membaca 打X折', 'X五折 dan menghitung harga bayar', '满减 dan hitungan lebih sulit'],
  PERSEN: ['Persen puluhan, pecahan sederhana', 'Persen 1–100, pecahan sampai 10', 'Persen desimal, pecahan sampai 20'],
  JAM: ['Jam bulat dan 半', '刻 dan menit', '差 dan keterangan waktu (24 jam)'],
  TANGGAL: ['Nama hari dan bulan', 'Tanggal lengkap', '前天, 明天, 后天, dan lainnya'],
  TELEPON: ['Nomor kamar', 'Nomor telepon dan kamar', 'Telepon dan nomor darurat'],
  UKURAN: ['Umur, jumlah orang, lantai', 'Umur, tinggi, dan berat', 'Tinggi, berat, dan 斤'],
  KIRA: ['几, 三四, 十多', '几十, 二三十, 一百多', '几百, 两千多, 几万'],
  CAMPUR: ['Semua kategori, tingkat mudah', 'Semua kategori, tingkat sedang', 'Semua kategori, tingkat sulit'],
};
const cfg = { cat: 'UMUM', diff: 0, count: 10, range: -1, lo: '250', hi: '3000' };

/** Daftar materi angka. */
export function numbersPage() {
  return h('div',
    header('Materi Angka', 'Teori, rumus, langkah, dan contoh', { back }),
    h('div.wrap.stack',
      h('div.card.pad.row.tap', { onclick: () => go('latihan/ANGKA') }, h('div.tile-ico', icon('numbers')),
        h('div.grow', h('b', 'Kuis Angka'), h('p.muted.small', 'Latih semua kategori setelah membaca materinya')), h('b', '›')),
      h('div.card.pad.row.tap', { onclick: () => go('pengubah') }, h('div.tile-ico', icon('calc')),
        h('div.grow', h('b', 'Pengubah Angka'), h('p.muted.small', 'Ketik angka, lihat cara bacanya langkah demi langkah')), h('b', '›')),
      h('h3.section-title', 'Pilih Materi'),
      h('div.grid2', CATS().filter((c) => c.id !== 'CAMPUR').map((c, i) => h('div.card.pad.tap.appear', { style: { animationDelay: `${i * 0.03}s` }, onclick: () => go(`materi/${c.id}`) },
        h('div.tile-ico', catIcon(c.id)), h('b', c.title), h('p.muted.small', c.sub))))));
}

const spokenEl = (s, cls = '') => h(`div${cls}`, hanzi(s.h, 'ex-hz'), h('div.small', pinyin(s.p, s.h)));

export function lessonPage(catId) {
  const cat = CATS().find((c) => c.id === catId) || CATS()[0];
  const rules = JSON.parse(L.lesson(cat.id));
  return h('div',
    header(cat.title, 'Materi lengkap', { back }),
    h('div.wrap.stack',
      ...rules.map((r, i) => h('div.card.pad.stack.appear', { style: { animationDelay: `${Math.min(i, 8) * 0.04}s` } },
        h('h3', r.title), h('p', r.body),
        r.formula ? h('div.formula', r.formula) : null,
        r.table.length ? h('div.kgrid', r.table.map((t) => h('div.kcell', { onclick: () => speak(t.s.h) }, hanzi(t.s.h), h('div.small', pinyin(t.s.p, t.s.h)), h('div.muted.small', t.m)))) : null,
        r.examples.length ? h('div.stack', r.examples.map((e) => h('div.ex', { onclick: () => speak(e.s.h) }, h('div', hanzi(e.s.h), h('div.small', pinyin(e.s.p, e.s.h))), h('div.muted.small', { style: { textAlign: 'right' } }, e.m)))) : null,
        r.steps.length ? h('div', h('b', r.stepsTitle || 'Langkah-Langkah'), h('ol.steps', r.steps.map((s) => h('li', s)))) : null)),
      h('button.btn', { onclick: () => { cfg.cat = cat.id; go('latihan/ANGKA'); } }, `Latihan ${cat.title}`)));
}

export function converterPage() {
  const out = h('div.stack');
  const input = h('input.field', { inputmode: 'numeric', placeholder: 'Contoh: 30508', oninput: () => show() });
  function show() {
    const raw = input.value.replace(/\D/g, '');
    if (!raw) { out.replaceChildren(h('p.muted', 'Ketik angka 0 sampai 999.999.999.999.')); return; }
    const n = Number(raw);
    if (n > 999999999999) { out.replaceChildren(h('p', { style: { color: 'var(--bad)' } }, 'Maksimal 999.999.999.999.')); return; }
    const r = JSON.parse(L.convert(n));
    out.replaceChildren(
      h('div.card.pad', { style: { textAlign: 'center' } }, h('p.muted', r.fmt), hanzi(r.s.h, r.s.h.length > 20 ? 'big-hz.xlong' : r.s.h.length > 12 ? 'big-hz.long' : 'big-hz'), h('div', { style: { fontSize: '1.3rem' } }, pinyin(r.s.p, r.s.h)),
        h('button.btn.ghost', { style: { marginTop: '12px' }, onclick: () => speak(r.s.h) }, icon('speaker'), ' Dengarkan')),
      h('div.card.pad', h('b', 'Langkah-Langkah'), h('ol.steps', r.steps.map((s) => h('li', s)))));
  }
  show();
  return h('div', header('Pengubah Angka', 'Angka → bacaan Mandarin', { back }), h('div.wrap.stack', input, out));
}

/** Pengaturan Kuis Angka. */
export function numberSetup() {
  const root = h('div');
  let cleanupRef = null;
  function draw() {
    const cats = CATS();
    const ranges = RANGES();
    const custom = cfg.cat === 'UMUM' && cfg.range === -2;
    let err = null;
    if (custom) { const lo = +cfg.lo.replace(/\D/g, ''), hi = +cfg.hi.replace(/\D/g, ''); if (!cfg.lo || !cfg.hi || lo > hi) err = 'Isi jarak angka dengan benar (angka awal ≤ angka akhir).'; else if (hi > 999999999999) err = 'Angka maksimal 999.999.999.999.'; }
    root.replaceChildren(
      header('Kuis Angka', 'Angka, harga, diskon, jam, dan lainnya', { back: () => go('latihan') }),
      h('div.wrap.stack',
        h('div.card.pad.stack', h('b', 'Kategori'),
          h('div.grid2', cats.map((c) => h('button.card.pad', { style: { textAlign: 'left', border: c.id === cfg.cat ? '2px solid var(--blue)' : '2px solid transparent' }, onclick: () => { cfg.cat = c.id; draw(); } },
            h('b.row', catIcon(c.id), c.title), h('div.muted.small', c.sub)))),
          cfg.cat !== 'CAMPUR' ? h('button.chip', { onclick: () => go(`materi/${cfg.cat}`) }, icon('library'), ' Baca Materinya Dulu') : null),
        h('div.card.pad.stack',
          cfg.cat === 'UMUM' ? [h('b', 'Jarak Angka'),
            h('div.chips', [h(`button.chip${cfg.range === -1 ? '.on' : ''}`, { onclick: () => { cfg.range = -1; draw(); } }, 'Sesuai Tingkat'),
              ...ranges.map((r, i) => h(`button.chip${cfg.range === i ? '.on' : ''}`, { onclick: () => { cfg.range = i; draw(); } }, r.label)),
              h(`button.chip${cfg.range === -2 ? '.on' : ''}`, { onclick: () => { cfg.range = -2; draw(); } }, 'Atur Sendiri')]),
            custom ? h('div.row', h('input.field', { inputmode: 'numeric', value: cfg.lo, oninput: (e) => { cfg.lo = e.target.value; } , onchange: draw }), '–',
              h('input.field', { inputmode: 'numeric', value: cfg.hi, oninput: (e) => { cfg.hi = e.target.value; }, onchange: draw })) : null] : null,
          (cfg.cat !== 'UMUM' || cfg.range === -1) ? [h('b', 'Tingkat'), segmented(['Mudah', 'Sedang', 'Sulit'], cfg.diff, (i) => { cfg.diff = i; draw(); }),
            h('p.muted.small', DIFF[cfg.cat][cfg.diff])] : null,
          h('b', 'Jumlah Soal'), h('div.chips', [10, 20, 30, 40, 50].map((c) => h(`button.chip${cfg.count === c ? '.on' : ''}`, { onclick: () => { cfg.count = c; draw(); } }, `${c}`)))),
        err ? h('p', { style: { color: 'var(--bad)' } }, err) : null,
        h('button.btn', { disabled: !!err, onclick: start }, `Mulai ${cfg.count} Soal`)));
  }
  function start() {
    let lo = -1, hi = -1;
    if (cfg.cat === 'UMUM' && cfg.range >= 0) { const r = RANGES()[cfg.range]; lo = r.lo; hi = r.hi; }
    if (cfg.cat === 'UMUM' && cfg.range === -2) { lo = +cfg.lo.replace(/\D/g, ''); hi = +cfg.hi.replace(/\D/g, ''); }
    const qs = JSON.parse(L.numBuild(cfg.cat, cfg.diff, cfg.count, lo, hi));
    if (cleanupRef) cleanupRef();
    const g = numGame(qs, draw, start); cleanupRef = g.cleanup;
    root.replaceChildren(g.el);
  }
  draw();
  return { el: root, cleanup: () => { if (cleanupRef) cleanupRef(); setFocus(false); } };
}

const KEYS = { NUMBER: '', DECIMAL: ',', TIME: ':', DATE: '/', FRACTION: '/', PERCENT: ',' };

function numGame(qs, onExit, onAgain) {
  let idx = 0, score = 0, done = false;
  const root = h('div');
  setFocus(true);
  const stop = () => setFocus(false);
  function exit() { if (idx > 0 && !done) confirmDialog('Keluar dari Kuis?', 'Jawaban yang sudah dikerjakan tetap tercatat.', 'Keluar', () => { stop(); onExit(); }, true); else { stop(); onExit(); } }
  function end() {
    done = true; stop();
    const pct = Math.round(score * 100 / qs.length);
    if (pct >= 80) confetti();
    root.replaceChildren(header('Hasil Kuis Angka', `${score} dari ${qs.length} benar`, { back: onExit }),
      h('div.wrap.stack', h('div.brand', { style: { textAlign: 'center' } }, h('div.big-ico', icon(pct >= 80 ? 'trophy' : 'muscle')), h('h2', `${score} dari ${qs.length} Benar`), h('p.muted', `Akurasi ${pct}%`)),
        h('div.card.pad', h('b', 'Tahukah Kamu?'), h('p', pick(JSON.parse(L.culture())))),
        h('button.btn', { onclick: onAgain }, 'Main Lagi'), h('button.btn.ghost', { onclick: onExit }, 'Selesai')));
  }
  function draw() {
    if (idx >= qs.length) return end();
    const q = qs[idx]; let answered = false;
    const fb = h('div');
    function result(ok, chosen) {
      answered = true; if (ok) score++;
      S.numAnswer(ok);
      if (q.hanzi) speak(q.hanzi);
      fb.replaceChildren(h(`div.feedback.${ok ? 'ok' : 'no'}.appear`,
        h('b.row', icon(ok ? 'check' : 'warn'), ok ? 'Benar!' : `Belum tepat. Jawaban: ${q.answerText}`),
        q.hanzi ? h('div', { style: { marginTop: '6px' } }, hanzi(q.hanzi, 'ex-hz'), q.pinyin ? h('div', pinyin(q.pinyin, q.hanzi)) : null) : null,
        q.explain ? h('p.small', { style: { marginTop: '6px' } }, q.explain) : null,
        h('button.btn', { style: { marginTop: '12px' }, onclick: () => { idx++; draw(); } }, idx + 1 >= qs.length ? 'Lihat Hasil' : 'Lanjut')));
    }
    let body;
    if (q.options.length) {
      const btns = q.options.map((o, i) => h('button.opt', { onclick: () => {
        if (answered) return; const ok = i === q.answer; btns[i].classList.add(ok ? 'ok' : 'no'); btns[q.answer].classList.add('ok'); result(ok);
      } }, o.chinese ? h('div', hanzi(o.text), o.pinyin ? h('div.small', pinyin(o.pinyin, o.text)) : null) : o.text));
      body = h('div.opts', btns);
    } else {
      const extra = KEYS[q.keys] || '';
      const input = h('input.field', { inputmode: extra ? 'text' : 'numeric', placeholder: extra ? `Gunakan "${extra}" bila perlu` : 'Ketik jawabanmu', style: { fontSize: '1.4rem', textAlign: 'center' },
        onkeydown: (e) => { if (e.key === 'Enter') submit(); } });
      const submit = () => { if (answered || !input.value.trim()) return; const ok = q.accepted.includes(L.numNorm(input.value)); input.disabled = true; result(ok); };
      body = h('div.stack', h('div.row', input, q.unit ? h('b', q.unit) : null),
        extra ? h('div.chips', [extra, '0', '00'].map((k) => h('button.chip', { onclick: () => { input.value += k; input.focus(); } }, k))) : null,
        h('button.btn', { onclick: submit }, 'Periksa'));
      setTimeout(() => input.focus(), 50);
    }
    root.replaceChildren(
      h('div.hero', h('div.hero-row', h('button.icon-btn.on-brand', { onclick: exit, 'aria-label': 'Tutup' }, icon('close')),
        h('div.hero-text', h('b', 'Kuis Angka'), h('p.small', `Soal ${idx + 1} dari ${qs.length} · Benar ${score}`))),
        h('div', { style: { marginTop: '12px' } }, progress(idx / qs.length))),
      h('div.wrap.stack',
        h('div.card.q-box.appear', h('p.muted', q.instruction),
          q.hanzi ? h('div', { style: { margin: '12px 0' } }, hanzi(q.hanzi, q.story ? 'ex-hz' : 'q-hz2')) : null,
          q.display ? h('h2', { style: { margin: '12px 0' } }, q.display) : null,
          q.hanzi ? h('button.chip', { onclick: () => speak(q.hanzi) }, icon('speaker'), ' Dengarkan') : null),
        body, fb));
  }
  draw();
  return { el: root, cleanup: stop };
}

export { toast };
