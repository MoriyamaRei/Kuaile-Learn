// Kuaile Learn versi web: khusus belajar, tanpa akun. Navigasi berbasis hash (#/…).
import { loadWords } from './data.js';
import { markOpen, state, onChange } from './store.js';
import { h } from './ui.js';
import { icon } from './icons.js';
import { homePage } from './pages/home.js';
import { learnPage, flashPage } from './pages/flash.js';
import { practicePage } from './pages/practice.js';
import { dictPage, wordPage } from './pages/dict.js';
import { numbersPage, lessonPage, converterPage } from './pages/numbers.js';
import { profilePage } from './pages/profile.js';

const TABS = [
  { route: '', label: 'Beranda', icon: 'home' },
  { route: 'belajar', label: 'Belajar', icon: 'cards' },
  { route: 'latihan', label: 'Latihan', icon: 'quiz' },
  { route: 'kamus', label: 'Kamus', icon: 'book' },
  { route: 'profil', label: 'Profil', icon: 'person' },
];

// Ikon navigasi: set ikon yang sama dengan seluruh situs.
const tabIcon = (k) => h('span.tab-ico', icon(k));


const ROUTES = [
  [/^$/, () => homePage()],
  [/^belajar$/, () => learnPage()],
  [/^flashcard(?:\/([\w-]+))?$/, (m) => flashPage(m[1])],
  [/^latihan(?:\/(\w+))?$/, (m) => practicePage(m[1])],
  [/^kamus$/, () => dictPage()],
  [/^kata\/(\d+)$/, (m) => wordPage(+m[1])],
  [/^angka$/, () => numbersPage()],
  [/^materi\/(\w+)$/, (m) => lessonPage(m[1])],
  [/^pengubah$/, () => converterPage()],
  [/^profil$/, () => profilePage()],
];

export const go = (route) => { location.hash = '#/' + route; };
export const back = () => (history.length > 1 ? history.back() : go(''));

/** Mode fokus: sembunyikan navigasi saat sedang latihan. */
export function setFocus(on) { document.body.classList.toggle('focus', !!on); }

// Tema bawaan halaman (misalnya dari pembungkus) dipakai saat memilih "Ikuti Perangkat".
const hostTheme = document.documentElement.getAttribute('data-theme');
function applyTheme() {
  const d = state.settings.dark;
  const t = d === 1 ? 'light' : d === 2 ? 'dark' : hostTheme;
  if (t) document.documentElement.setAttribute('data-theme', t); else document.documentElement.removeAttribute('data-theme');
}

function nav() {
  const cur = location.hash.replace(/^#\/?/, '').split('/')[0];
  const tabOf = currentTab(cur);
  return h('nav.tabs',
    h('div.nav-brand', h('img.nav-logo', { src: 'img/logo.svg', alt: '' }), h('div', h('b', 'Kuaile Mandarin Academy'), h('small', 'Belajar Kosakata HSK 1–4'))),
    TABS.map((t) => h(`a.tab${t.route === tabOf ? '.on' : ''}`, { href: '#/' + t.route, onclick: () => closePeek() },
      tabIcon(t.icon), h('span.tab-txt', t.label))));
}

const currentTab = (cur) => ({ flashcard: 'belajar', kata: 'kamus', angka: 'belajar', materi: 'belajar', pengubah: 'kamus' })[cur] ?? cur;

// ---------- Menu kiri di laptop ----------
// Mouse mendekati tombol ☰: menu muncul dari tombol itu, lalu menutup saat mouse menjauh.
// Klik ☰: menu tetap terbuka sampai ☰ diklik lagi atau mengklik di luar menu.
const desktop = () => matchMedia('(min-width: 900px)').matches;
let pinned = false;
let peekTimer = 0;
function openPeek() { if (!desktop()) return; clearTimeout(peekTimer); document.body.classList.add('nav-peek'); }
function closePeek(delay = 0) {
  clearTimeout(peekTimer);
  peekTimer = setTimeout(() => document.body.classList.remove('nav-peek'), delay);
}
function setPinned(on) {
  pinned = on;
  document.body.classList.toggle('nav-pinned', on);
  if (!on) closePeek();
  const btn = document.getElementById('menu-btn');
  if (btn) { btn.replaceChildren(icon(on ? 'close' : 'menu')); btn.setAttribute('aria-expanded', String(on)); btn.title = on ? 'Tutup Menu' : 'Menu'; }
}
function setupMenu() {
  const btn = h('button#menu-btn', { 'aria-label': 'Menu', 'aria-controls': 'nav', onclick: () => setPinned(!pinned) });
  const navEl = document.getElementById('nav');
  for (const el of [btn, navEl]) {
    el.addEventListener('mouseenter', openPeek);
    el.addEventListener('mouseleave', () => closePeek(280));
  }
  navEl.addEventListener('focusin', openPeek);
  navEl.addEventListener('focusout', () => closePeek(200));
  document.addEventListener('keydown', (e) => { if (e.key === 'Escape') { closePeek(); if (pinned) setPinned(false); } });
  // Menu yang disematkan juga tertutup kalau mengklik di luar menu.
  document.addEventListener('pointerdown', (e) => { if (pinned && !navEl.contains(e.target) && !btn.contains(e.target)) setPinned(false); });
  document.body.append(btn);
  // Setelah digulir, tombol ☰ jadi padat supaya tetap jelas di atas isi halaman.
  addEventListener('scroll', () => document.body.classList.toggle('scrolled', scrollY > 140), { passive: true });
  setPinned(false);
}

let cleanup = null;
function render() {
  const path = location.hash.replace(/^#\/?/, '');
  const view = document.getElementById('view');
  if (typeof cleanup === 'function') { try { cleanup(); } catch { /* abaikan */ } }
  cleanup = null;
  setFocus(false);
  let page = null;
  for (const [re, fn] of ROUTES) { const m = path.match(re); if (m) { page = fn(m); break; } }
  if (!page) page = homePage();
  if (page && page.cleanup) cleanup = page.cleanup;
  view.replaceChildren(page.el || page);
  view.classList.remove('enter'); void view.offsetWidth; view.classList.add('enter');
  document.getElementById('nav').replaceChildren(nav());
  // Tema sampul mengikuti tab yang sedang dibuka.
  document.body.dataset.tab = currentTab(path.split('/')[0]) || 'beranda';
  window.scrollTo(0, 0);
}

async function start() {
  applyTheme();
  try {
    await Promise.all([loadWords(), document.fonts.load('40px WenKai', '读').catch(() => {})]);
  } catch (e) {
    document.getElementById('view').replaceChildren(h('div.card.pad', h('h2', 'Data Belum Bisa Dimuat'), h('p', 'Muat ulang halaman ini. Kalau masih gagal, pastikan situs dibuka lewat server web (bukan langsung dari file).')));
    return;
  }
  markOpen();
  onChange(applyTheme);
  window.addEventListener('hashchange', render);
  setupMenu();
  document.getElementById('splash')?.classList.add('hide');
  setTimeout(() => document.getElementById('splash')?.remove(), 600);
  render();
  if ('serviceWorker' in navigator && location.protocol.startsWith('http')) navigator.serviceWorker.register('sw.js').catch(() => {});
}

start();
