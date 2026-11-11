// Service worker sederhana: simpan berkas aplikasi supaya bisa dibuka lagi tanpa internet.
// Permintaan ke server (Supabase, GitHub) tidak disimpan.
const CACHE = "kuaile-web";
self.addEventListener("install", (e) => { self.skipWaiting(); });
self.addEventListener("activate", (e) => { e.waitUntil(self.clients.claim()); });
self.addEventListener("fetch", (e) => {
  const url = new URL(e.request.url);
  if (e.request.method !== "GET" || url.origin !== location.origin) return;
  // Jaringan dulu (supaya versi baru langsung terpakai), simpanan kalau offline.
  e.respondWith(
    fetch(e.request).then((r) => {
      if (r.ok) { const copy = r.clone(); caches.open(CACHE).then((c) => c.put(e.request, copy)); }
      return r;
    }).catch(() => caches.match(e.request).then((r) => r || caches.match("./")))
  );
});
