// Data kosakata HSK 1–4 (sama dengan aplikasi Android).
import * as L from './logic.mjs';

export let words = [];
export let themes = {};
export let byId = new Map();

const THEME_ORDER = ['keluarga', 'profesi', 'tubuh', 'makanan', 'waktu', 'tempat', 'sekolah', 'uang', 'rumah', 'transport',
  'alam', 'hobi', 'perasaan', 'komunikasi', 'masyarakat', 'konsep', 'angka', 'ganti',
  'kerja', 'sifat', 'keterangan', 'depan', 'sambung', 'bantu', 'partikel'];

export const capFirst = (s) => (s ? s[0].toUpperCase() + s.slice(1) : s);

export function posName(p) {
  const k = (p || '').trim().replace(/\.$/, '').toLowerCase();
  return ({
    n: 'Kata Benda', v: 'Kata Kerja', adj: 'Kata Sifat', adv: 'Kata Keterangan', pron: 'Kata Ganti', num: 'Kata Bilangan',
    nm: 'Kata Satuan', vm: 'Kata Satuan', m: 'Kata Satuan', 'nm./vm': 'Kata Satuan', conj: 'Kata Sambung', prep: 'Kata Depan',
    aux: 'Kata Bantu', sa: 'Partikel', mp: 'Partikel', int: 'Kata Seru', sv: 'Verba + Objek', '': 'Ungkapan',
  })[k] || p;
}

export async function loadWords() {
  const res = await fetch('data/words.json');
  const d = await res.json();
  themes = d.themes;
  words = d.words.map((o) => {
    const senses = o.s.map((s) => ({ pos: s.p, mean: s.m, example: s.e }));
    const w = {
      id: o.id, hanzi: o.h, pinyin: o.py, level: o.lv, theme: o.t, book: o.b || 0, senses,
      main: capFirst(senses[0].mean),
      others: [...new Set(senses.slice(1).map((s) => capFirst(s.mean)))].filter((m) => m !== capFirst(senses[0].mean)),
      pos: senses[0].pos, example: senses[0].example,
    };
    w.plain = L.pyPlain(w.pinyin).replace(/ /g, '');
    w.search = senses.map((s) => s.mean).join(' ').toLowerCase();
    return w;
  });
  byId = new Map(words.map((w) => [w.id, w]));
}

export const theme = (id) => themes[id] || { name: id };

export function studyOrder(byTheme) {
  if (!byTheme) return [...words].sort((a, b) => a.id - b.id);
  const ti = (t) => { const i = THEME_ORDER.indexOf(t); return i < 0 ? 99 : i; };
  return [...words].sort((a, b) => a.level - b.level || ti(a.theme) - ti(b.theme) || a.id - b.id);
}

export const isPureHanzi = (w) => w.hanzi.length > 0 && [...w.hanzi].every((c) => c >= '一' && c <= '鿿');

/** Semua hanzi unik di kamus (untuk pengenal tulisan). */
export function allChars() {
  const s = new Set();
  for (const w of words) for (const c of w.hanzi) if ((c >= '一' && c <= '鿿') || c === '〇') s.add(c);
  return [...s];
}

export const shuffle = (a) => { const x = [...a]; for (let i = x.length - 1; i > 0; i--) { const j = Math.floor(Math.random() * (i + 1)); [x[i], x[j]] = [x[j], x[i]]; } return x; };
export const pick = (a) => a[Math.floor(Math.random() * a.length)];
