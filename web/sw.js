// Menyimpan file situs di perangkat supaya situs tetap bisa dibuka tanpa internet.
const CACHE = 'kuaile-learn-web-v4';
const FILES = [
  './', 'index.html', 'css/app.css', 'manifest.webmanifest', 'data/words.json',
  'js/app.js', 'js/data.js', 'js/store.js', 'js/quiz.js', 'js/hand.js', 'js/ui.js', 'js/icons.js', 'js/logic.mjs',
  'js/pages/home.js', 'js/pages/flash.js', 'js/pages/practice.js', 'js/pages/write.js', 'js/pages/dict.js', 'js/pages/numbers.js', 'js/pages/profile.js',
  'fonts/jakarta.woff2', 'fonts/wenkai-regular.woff2', 'fonts/wenkai-bold.woff2',
  'img/logo.svg', 'img/icon-192.png', 'img/icon-512.png', 'img/favicon.png',
];
self.addEventListener('install', (e) => { e.waitUntil(caches.open(CACHE).then((c) => c.addAll(FILES)).then(() => self.skipWaiting())); });
self.addEventListener('activate', (e) => { e.waitUntil(caches.keys().then((ks) => Promise.all(ks.filter((k) => k !== CACHE).map((k) => caches.delete(k)))).then(() => self.clients.claim())); });
// Coba ambil versi terbaru dari internet dulu; kalau offline, pakai salinan.
self.addEventListener('fetch', (e) => {
  if (e.request.method !== 'GET') return;
  e.respondWith(fetch(e.request).then((r) => { const copy = r.clone(); caches.open(CACHE).then((c) => c.put(e.request, copy)); return r; }).catch(() => caches.match(e.request)));
});
