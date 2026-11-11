// Progres belajar disimpan di browser (localStorage). Tanpa akun, tanpa server.
import { words, byId } from './data.js';

const KEY = 'kuaile-learn-web-v1';

export const today = () => {
  const d = new Date();
  return Math.floor((Date.UTC(d.getFullYear(), d.getMonth(), d.getDate())) / 86400000);
};

const blankCard = () => ({ reps: 0, interval: 0, ease: 2.5, due: 0, lapses: 0, wrong: 0, right: 0, learnedOn: -1, star: false });
const blankDay = () => ({ reviews: 0, newWords: 0, correct: 0, wrong: 0, quiz: 0, opened: false });

function fresh() {
  return { cards: {}, days: {}, settings: { newPerDay: 20, byTheme: false, dark: 0, voice: 1 }, best: { kilat: 0, exam: 0 } };
}

export let state = load();
const listeners = new Set();

function load() {
  try {
    const raw = localStorage.getItem(KEY);
    if (!raw) return fresh();
    const s = JSON.parse(raw);
    const f = fresh();
    return { ...f, ...s, settings: { ...f.settings, ...(s.settings || {}) }, best: { ...f.best, ...(s.best || {}) } };
  } catch { return fresh(); }
}

function save() {
  try { localStorage.setItem(KEY, JSON.stringify(state)); } catch { /* penyimpanan penuh atau diblokir */ }
  listeners.forEach((f) => f());
}

export const onChange = (f) => { listeners.add(f); return () => listeners.delete(f); };

export const card = (id) => state.cards[id] || blankCard();
const day = (t = today()) => state.days[t] || blankDay();
const setDay = (fn) => { const t = today(); state.days[t] = fn({ ...day(t) }); };

/** Sudah dipelajari = pernah dijawab di latihan. Flashcard tidak dihitung. */
export const isSeen = (id) => { const c = card(id); return c.right + c.wrong > 0; };
/** Hafal = sudah benar minimal 3 kali di latihan dan benarnya minimal dua kali lipat salahnya. */
export const isMastered = (c) => c.right >= 3 && c.right >= 2 * c.wrong;
export const status = (id) => { const c = card(id); if (isMastered(c)) return 'hafal'; return isSeen(id) ? 'belajar' : 'baru'; };

export function quizAnswer(id, ok) {
  const c = { ...card(id) };
  if (ok) c.right++; else c.wrong++;
  state.cards[id] = c;
  setDay((d) => ({ ...d, quiz: d.quiz + 1, correct: d.correct + (ok ? 1 : 0), wrong: d.wrong + (ok ? 0 : 1), opened: true }));
  save();
}

export function numAnswer(ok) {
  setDay((d) => ({ ...d, quiz: d.quiz + 1, correct: d.correct + (ok ? 1 : 0), wrong: d.wrong + (ok ? 0 : 1), opened: true }));
  save();
}

export function toggleStar(id) { state.cards[id] = { ...card(id), star: !card(id).star }; save(); }

export function markOpen() { if (!day().opened) { setDay((d) => ({ ...d, opened: true })); save(); } }

export function setBest(key, v) { if (v > (state.best[key] || 0)) { state.best[key] = v; save(); } }

export function setSettings(patch) { state.settings = { ...state.settings, ...patch }; save(); }

// ---------- Target harian: jumlah soal latihan ----------
export const doneToday = () => day().quiz;
export const dailyTarget = () => state.settings.newPerDay;
export const goalReached = () => doneToday() >= dailyTarget();

// ---------- Pengaturan flashcard ----------
export const flashSettings = () => ({ auto: false, pos: {}, last: null, ...(state.settings.flash || {}) });
export function setFlash(patch) { state.settings = { ...state.settings, flash: { ...flashSettings(), ...patch } }; save(); }

// ---------- Streak: hari berturut-turut belajar (buka situs dan belajar) ----------
const attended = (t) => !!state.days[t]?.opened;
export function streak() {
  const t = today();
  let d = attended(t) ? t : t - 1; let n = 0;
  while (attended(d)) { n++; d--; }
  return n;
}
export function bestStreak() {
  const ks = Object.keys(state.days).map(Number).filter(attended).sort((a, b) => a - b);
  let best = 0, run = 0, prev = null;
  for (const k of ks) { run = prev !== null && k === prev + 1 ? run + 1 : 1; best = Math.max(best, run); prev = k; }
  return best;
}

export const seenCount = (lv) => words.filter((w) => (!lv || w.level === lv) && isSeen(w.id)).length;
export const masteredCount = (lv) => words.filter((w) => (!lv || w.level === lv) && status(w.id) === 'hafal').length;

export function totals() {
  let correct = 0, wrong = 0, reviews = 0, quiz = 0;
  for (const d of Object.values(state.days)) { correct += d.correct; wrong += d.wrong; reviews += d.reviews; quiz += d.quiz; }
  return { correct, wrong, reviews, quiz, acc: correct + wrong ? Math.round(correct * 100 / (correct + wrong)) : 0 };
}

export function mostWrong(n) {
  return Object.entries(state.cards).filter(([, c]) => c.wrong > 0).sort((a, b) => b[1].wrong - a[1].wrong)
    .slice(0, n).map(([id, c]) => ({ w: byId.get(+id), c })).filter((x) => x.w);
}

/** Kata hari ini: acak tetapi tetap sepanjang hari, dari level yang sedang dipelajari. */
export function wordOfDay() {
  const lv = [1, 2, 3, 4].find((l) => words.some((w) => w.level === l && !isSeen(w.id))) || 4;
  const pool = words.filter((w) => w.level === lv);
  return pool[today() % pool.length];
}

// ---------- Cadangan ----------
export const exportJson = () => JSON.stringify({ app: 'kuaile-learn-web', version: 1, ...state });
export function importJson(txt) {
  try {
    const s = JSON.parse(txt);
    if (!s || typeof s !== 'object' || !s.cards) return false;
    const f = fresh();
    state = { ...f, cards: s.cards, days: s.days || {}, settings: { ...f.settings, ...(s.settings || {}) }, best: { ...f.best, ...(s.best || {}) } };
    save(); return true;
  } catch { return false; }
}
export function resetAll() { state = fresh(); save(); }
