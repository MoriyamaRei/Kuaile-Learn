// Pembuat soal kuis kosakata (sama dengan aplikasi Android).
import * as L from './logic.mjs';
import { words, shuffle, pick, isPureHanzi } from './data.js';
import { card, isSeen } from './store.js';

export const MODES = {
  HANZI_ARTI: { title: 'Hanzi → Arti', sub: 'Baca hanzi, pilih artinya', icon: 'translate' },
  ARTI_HANZI: { title: 'Arti → Hanzi', sub: 'Lihat arti, pilih hanzinya', icon: 'bulb' },
  HANZI_PINYIN: { title: 'Hanzi → Pinyin', sub: 'Baca hanzi, pilih pinyinnya', icon: 'letters' },
  PINYIN_HANZI: { title: 'Pinyin → Hanzi', sub: 'Lihat pinyin, pilih hanzinya', icon: 'cardText' },
  NADA: { title: 'Tebak Nada', sub: 'Baca hanzi, pilih nadanya', icon: 'music' },
  SUSUN: { title: 'Susun Kalimat', sub: 'Urutkan potongan kalimat', icon: 'puzzle' },
  TULIS: { title: 'Tulis Hanzi', sub: 'Lihat pinyin, tulis hanzinya', icon: 'pen' },
  KILAT: { title: 'Kuis Kilat', sub: '60 detik · tanpa bantuan', icon: 'bolt' },
  UJIAN: { title: 'Ujian HSK', sub: '20 soal · 10 menit · lulus 60%', icon: 'cap' },
  ANGKA: { title: 'Kuis Angka', sub: 'Angka, harga, diskon, jam, dan lainnya', icon: 'numbers' },
};

export const SOURCES = { SEMUA: 'Semua Kata', DIPELAJARI: 'Sudah Dipelajari', SALAH: 'Sering Salah', BINTANG: 'Berbintang' };

export const QTYPE_LABEL = {
  HANZI_ARTI: 'Hanzi → Arti', ARTI_HANZI: 'Arti → Hanzi', HANZI_PINYIN: 'Hanzi → Pinyin', PINYIN_HANZI: 'Pinyin → Hanzi', NADA: 'Tebak Nada', SUSUN: 'Susun Kalimat',
};
export const HINTABLE = new Set(['HANZI_ARTI', 'ARTI_HANZI', 'SUSUN']);
const MCQ = ['HANZI_ARTI', 'ARTI_HANZI', 'HANZI_PINYIN', 'PINYIN_HANZI', 'NADA'];

// ---------- Potongan kalimat (Susun Kalimat) ----------
const END = '。！？!?';
const ATTACH = '，、：；“”‘’,';
let dict = null;
const getDict = () => dict || (dict = new Set(words.map((w) => w.hanzi).filter((h) => h && [...h].every((c) => c >= '一' && c <= '鿿'))));

export function chunks(sentence) {
  let s = sentence.trim();
  while (s && END.includes(s[s.length - 1])) s = s.slice(0, -1);
  if (!s) return [];
  const D = getDict(); const out = [];
  let i = 0;
  while (i < s.length) {
    const c = s[i];
    if (ATTACH.includes(c)) { if (out.length) out[out.length - 1] += c; else out.push(c); i++; continue; }
    let len = 1;
    for (let l = 4; l >= 2; l--) if (i + l <= s.length && D.has(s.slice(i, i + l))) { len = l; break; }
    out.push(s.slice(i, i + len)); i += len;
  }
  while (out.length > 6) {
    let best = 0, bestScore = Infinity;
    for (let k = 0; k < out.length - 1; k++) {
      const j = out[k] + out[k + 1];
      const score = j.length - (D.has(j) ? 10 : 0);
      if (score < bestScore) { bestScore = score; best = k; }
    }
    out[best] += out[best + 1]; out.splice(best + 1, 1);
  }
  return out.length >= 3 && out.length <= 6 ? out : [];
}
export const ending = (s) => { const m = s.trim().match(/[。！？!?]+$/); return m ? m[0] : ''; };

// ---------- Kumpulan kata ----------
const sentenceOk = (w) => chunks(w.example).length >= 3;
export const writable = (w) => isPureHanzi(w) && w.hanzi.length <= 4;

export function pool(level, source, n, accept = () => true) {
  const base = words.filter((w) => (level === 0 || w.level === level) && accept(w));
  switch (source) {
    case 'DIPELAJARI': return shuffle(base.filter((w) => isSeen(w.id))).slice(0, n);
    case 'BINTANG': return shuffle(base.filter((w) => card(w.id).star)).slice(0, n);
    case 'SALAH': return base.filter((w) => card(w.id).wrong > 0)
      .sort((a, b) => card(b.id).wrong - card(a.id).wrong || card(a.id).right - card(b.id).right).slice(0, n);
    default: {
      if (level !== 0) return shuffle(base).slice(0, n);
      const per = [1, 2, 3, 4].map((lv) => shuffle(base.filter((w) => w.level === lv)));
      const out = []; let i = 0;
      while (out.length < n && per.some((l) => i < l.length)) { for (const l of shuffle(per)) if (i < l.length && out.length < n) out.push(l[i]); i++; }
      return shuffle(out);
    }
  }
}

export function available(mode, level, source) {
  const acc = mode === 'SUSUN' ? sentenceOk : mode === 'TULIS' ? writable : () => true;
  return pool(level, source, Infinity, acc).length;
}

// ---------- Pembuat soal ----------
const syllables = (py) => JSON.parse(L.pySplit(py)).filter((p) => /\p{L}/u.test(p)).length;
const plainKey = (py) => L.pyPlain(py).replace(/ /g, '');

function toneVariants(w) {
  const parts = JSON.parse(L.pySplit(w.pinyin));
  const idx = parts.map((p, i) => (/\p{L}/u.test(p) ? i : -1)).filter((i) => i >= 0);
  const out = new Set([w.pinyin]); let guard = 0;
  while (out.size < 4 && guard++ < 40 && idx.length) {
    const p = [...parts];
    const n = idx.length > 1 && guard % 2 === 0 ? 2 : 1;
    shuffle(idx).slice(0, n).forEach((i) => {
      const cur = L.pyToneOf(p[i]);
      p[i] = L.pyWithTone(p[i], pick([1, 2, 3, 4].filter((t) => t !== cur)));
    });
    out.add(p.join(''));
  }
  for (const o of shuffle(words)) { if (out.size >= 4) break; out.add(o.pinyin); }
  return [...out];
}

const sylCache = new Map();
const sylOf = (o) => { let v = sylCache.get(o.id); if (v === undefined) { v = syllables(o.pinyin); sylCache.set(o.id, v); } return v; };

/** Pengecoh yang mirip (huruf awal / jumlah suku kata / panjang / level sama) tetapi tidak ambigu. */
function similar(w, valid) {
  const key = plainKey(w.pinyin); const syl = sylOf(w);
  return shuffle(words.filter((o) => o.id !== w.id && valid(o)).map((o) => {
    let s = 0;
    if (o.plain[0] === key[0]) s += 2;
    if (sylOf(o) === syl) s += 2;
    if (o.hanzi.length === w.hanzi.length) s += 2;
    if (o.level === w.level) s += 1;
    return [o, s + Math.floor(Math.random() * 3)];
  }).sort((a, b) => b[1] - a[1]).slice(0, 30).map((x) => x[0]));
}

const uniq = (a) => [...new Set(a)];

export function makeQuestion(w, type) {
  let opts;
  if (type === 'HANZI_ARTI') {
    const d = [...shuffle(words.filter((o) => o.level === w.level && o.id !== w.id)), ...shuffle(words)];
    opts = [w.main, ...uniq(d.map((o) => o.main).filter((m) => m !== w.main)).slice(0, 3)];
  } else if (type === 'ARTI_HANZI') {
    const d = [...shuffle(words.filter((o) => o.level === w.level && o.id !== w.id)), ...shuffle(words)];
    opts = [w.hanzi, ...uniq(d.filter((o) => o.main !== w.main).map((o) => o.hanzi).filter((h) => h !== w.hanzi)).slice(0, 3)];
  } else if (type === 'HANZI_PINYIN') {
    const key = plainKey(w.pinyin);
    opts = [w.pinyin, ...uniq(similar(w, (o) => plainKey(o.pinyin) !== key).map((o) => o.pinyin)).slice(0, 3)];
  } else if (type === 'PINYIN_HANZI') {
    const py = w.pinyin.toLowerCase();
    opts = [w.hanzi, ...uniq(similar(w, (o) => o.pinyin.toLowerCase() !== py && o.hanzi !== w.hanzi).map((o) => o.hanzi)).slice(0, 3)];
  } else if (type === 'NADA') {
    opts = toneVariants(w);
  } else if (type === 'SUSUN') {
    const ch = chunks(w.example);
    if (ch.length < 3) return null;
    let sh = shuffle(ch); let g = 0;
    while (sh.join('|') === ch.join('|') && g++ < 10) sh = shuffle(ch);
    return { word: w, type, options: sh, answer: 0, chunks: ch, sentence: w.example };
  }
  if (!opts || opts.length < 4) return null;
  const correct = opts[0];
  const sh = shuffle(opts);
  return { word: w, type, options: sh, answer: sh.indexOf(correct) };
}

export function build(mode, level, source, count) {
  const mk = (list, t) => list.map((w) => makeQuestion(w, t)).filter(Boolean);
  if (mode === 'KILAT') return pool(level, source, 80).map((w) => makeQuestion(w, pick(MCQ))).filter(Boolean);
  if (mode === 'SUSUN') return mk(pool(level, source, count, sentenceOk), 'SUSUN');
  if (mode === 'UJIAN') {
    const plan = [['HANZI_ARTI', 4], ['ARTI_HANZI', 3], ['HANZI_PINYIN', 3], ['PINYIN_HANZI', 3], ['NADA', 3], ['SUSUN', 4]];
    const ws = pool(level, source, 60); const out = [];
    for (const [t, n] of plan) {
      let made = 0;
      for (let i = 0; i < ws.length && made < n; i++) { const q = makeQuestion(ws[i], t); if (q) { out.push(q); ws.splice(i, 1); i--; made++; } }
    }
    return shuffle(out);
  }
  return mk(pool(level, source, count), mode);
}

let ambiguous = null;
export function isAmbiguous(w) {
  if (!ambiguous) {
    const m = new Map();
    for (const o of words) { const k = o.pinyin.toLowerCase().replace(/ /g, ''); m.set(k, (m.get(k) || 0) + 1); }
    ambiguous = new Set([...m].filter(([, n]) => n > 1).map(([k]) => k));
  }
  return ambiguous.has(w.pinyin.toLowerCase().replace(/ /g, ''));
}
