# Kuaile Mandarin Academy · Situs Belajar Kosakata HSK 1–4

Situs belajar untuk murid: flashcard, latihan kosakata, tulis hanzi, kamus, dan teori angka. Tanpa akun, rank, maupun leaderboard. Progres murid tersimpan di browser masing-masing (bisa dicadangkan dari halaman Profil).

## Cara Memasang (untuk Tim IT)

Ini **situs statis**: hanya HTML, CSS, JavaScript, gambar, dan satu file data JSON. Tidak butuh database, PHP, Node.js, atau server khusus.

1. Ekstrak ZIP ini.
2. Unggah **seluruh isinya** (termasuk folder `css`, `js`, `data`, `fonts`, `img`) ke hosting, misalnya ke subfolder `/belajar` atau subdomain `belajar.domainkamu.com`.
3. Pastikan `index.html` berada tepat di folder yang dituju.
4. Buka alamatnya lewat `https://`. Selesai.

Lalu tambahkan menu atau tab di website utama yang mengarah ke alamat itu. Sebaiknya buka di tab baru (`target="_blank"`).

### Catatan teknis

- Situs harus dibuka lewat `http://` atau `https://`, **bukan** dengan membuka `index.html` langsung dari komputer (`file://`). Browser memblokir modul JavaScript dari file lokal.
- Pastikan server mengirim tipe file yang benar. Hosting umum sudah otomatis benar:
  - `.mjs` dan `.js` → `text/javascript`
  - `.json` → `application/json`
  - `.woff2` → `font/woff2`
  - `.webmanifest` → `application/manifest+json`
- Jangan memasang situs di dalam `<iframe>`. Suara, layar penuh, dan penyimpanan progres sering bermasalah di dalam iframe, terutama di iPhone.
- Situs bisa dipasang seperti aplikasi di HP (menu browser → Tambahkan ke Layar Utama) dan tetap bisa dibuka tanpa internet setelah kunjungan pertama, berkat `sw.js` (service worker, butuh HTTPS).
- Ukuran total sekitar 2 MB.

### Mencoba di komputer sendiri

```
cd folder-hasil-ekstrak
python3 -m http.server 8000
```

lalu buka http://localhost:8000.

## Catatan Lain

- Suara memakai suara Mandarin bawaan browser atau perangkat. Kalau tidak terdengar, pasang suara Mandarin (zh-CN) di pengaturan perangkat.
- Pengenal tulisan tangan adalah versi sederhana, cocok untuk hanzi yang ditulis rapi.
- `js/logic.mjs` dihasilkan otomatis dari kode aplikasi Android. Jangan diedit langsung.
