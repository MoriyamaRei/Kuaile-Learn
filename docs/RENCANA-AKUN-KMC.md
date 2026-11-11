# Rencana Akun KMC untuk Kuaile Learn

Status: **Versi lengkap pertama sudah dibuat** (nama aplikasi: **Kuaile Learn**). Semua fitur di dokumen ini sudah ada di aplikasi; server Supabase memakai `supabase/schema.sql` versi 2 (jalankan ulang seluruh file di SQL Editor setiap kali skemanya diperbarui).

## 1. Tujuan

- APK Read It dibagikan lewat grup, tetapi hanya bisa dipakai oleh anggota Kuaile Mandarin Club (KMC).
- Akses memakai **Nomor KMC + PIN**, dan satu KMC hanya boleh aktif di **satu HP**.
- Belum terhubung ke website KMC (website belum jadi). Nanti datanya bisa dipindahkan atau disambungkan ke website.

## 2. Server

- **Supabase paket gratis**, akun atas nama pemilik (pengelola KMC), memakai email pribadi dengan **verifikasi 2 langkah (2FA)** aktif.
- Kuota gratis (cek ulang di https://supabase.com/pricing sebelum mulai):
  - Database 500 MB (total)
  - Storage 1 GB (total)
  - Egress 5 GB per bulan
  - 50.000 pengguna aktif per bulan, 2 proyek gratis
- Perkiraan pemakaian untuk ±100 anggota:
  - Database kurang dari 1 MB
  - Foto ±100 MB
  - Egress ±150–300 MB per bulan
- Proyek gratis tidur setelah 1 minggu tidak dipakai, jadi disiapkan **ping harian otomatis** (GitHub Actions).
- Paket gratis tidak punya backup otomatis, jadi disiapkan **backup mingguan otomatis**. Isinya hanya hash PIN, tidak pernah PIN asli.

## 3. Nomor KMC dan Pendaftaran

- Format nomor: `KMC-00001-00` (sama dengan website KMC).
- Pendaftaran klub masih lewat Google Form. Nomor KMC **dibuat manual oleh admin**.
- Admin memasukkan anggota yang sudah terverifikasi ke server: `KMC`, `Nama`, `Tempat Lahir`, `Tanggal Lahir` (satu per satu, atau sekaligus lewat CSV dari Google Sheets). Statusnya menjadi **Aktif** (belum dipakai di aplikasi), tanpa masa berlaku.
- Status KMC: `Aktif` → `Terdaftar` (bisa juga `Diblokir`).
- **Daftar** di aplikasi (sekali, wajib online):
  1. Nomor KMC (scan kartu atau ketik). Server mengecek: belum aktif, sudah terdaftar, diblokir, atau aktif.
  2. **Tempat Lahir** + **Tanggal Lahir** (dipilih dari kalender) sebagai verifikasi. Tempat lahir dibandingkan tanpa peduli huruf besar-kecil, spasi, dan tanda baca. Salah 3× ikut aturan kunci.
  3. Buat PIN + Konfirmasi PIN.
  4. Buat Nickname.
  5. Selesai → status **Terdaftar**. Setelah itu anggota **tetap masuk lewat layar Masuk**.
- Data lahir disimpan di server sebagai **hash** (tidak bisa ditampilkan, hanya dicocokkan). Data aslinya tetap di Google Sheets admin.

## 4. PIN

- PIN 6 angka dibuat oleh anggota sendiri saat Daftar. Admin, pengurus, dan anggota lain **tidak bisa melihatnya**.
- PIN yang terlalu mudah ditolak (angka sama semua, berurutan, pola berulang seperti `121212`).
- Server hanya menyimpan **hash bcrypt** PIN, tidak pernah PIN asli.
- Aturan salah PIN, sama dengan website KMC dan diperiksa di server:
  - 3× salah → terkunci **1 menit**
  - 3× salah lagi → terkunci **3 menit**, lalu **5 menit**, **10 menit**, **1 jam**, kemudian **permanen**
  - Kunci permanen hanya bisa dibuka oleh admin.
- **Lupa PIN:** anggota menghubungi admin → admin menekan Reset PIN → status kembali **Aktif** → anggota Daftar ulang (KMC, tempat dan tanggal lahir, PIN baru).
- **Ganti PIN** (rencana): di Profil, PIN lama + PIN baru + konfirmasi, wajib online.

## 5. Login, Satu HP, dan Logout

- **Login cukup sekali** dan wajib online. Setelah itu aplikasi mengingat login, jadi tidak perlu mengetik KMC + PIN setiap dibuka.
- Masuk dengan **scan kartu KMC** atau **mengetik Nomor KMC**, lalu PIN.
- HP dikenali dengan ID acak yang dibuat aplikasi saat pertama dipasang (bukan IMEI).
- Kalau KMC sedang aktif di HP lain, login **ditolak**.
- **Logout** wajib online. Setelah logout, KMC bisa dipakai di HP lain.
- **HP hilang atau rusak:** admin menekan "Reset Perangkat", lalu anggota bisa login di HP baru.
- Kalau KMC direset atau diblokir, HP lama otomatis logout begitu terhubung ke server.

## 6. Saat Membuka Aplikasi dan Mode Offline

- **Ada internet:** muncul layar loading singkat untuk cek status KMC dan mengirim data leaderboard, lalu aplikasi terbuka.
- **Tidak ada internet:** aplikasi tetap terbuka dalam **Mode Offline**, maksimal **3 hari**.
- Ketentuan Mode Offline:
  1. Hitungan 3 hari hanya berjalan kalau HP **tidak ada internet**. Kalau server yang error, hitungannya tidak berjalan dan anggota tidak terkunci.
  2. Progres offline tetap tersimpan di HP, tetapi **tidak masuk leaderboard**.
  3. Setelah 3 hari, aplikasi meminta online **sekali** untuk verifikasi, **tanpa login ulang**.
  4. Kalau saat verifikasi ternyata KMC sudah direset atau diblokir, aplikasi logout otomatis.

## 7. Profil

- **Foto profil** dari galeri, maksimal 1 MB, otomatis dipotong kotak. Disimpan dua versi:
  - **kecil** (±15 KB) untuk leaderboard;
  - **asli** (maks. 1 MB), diunduh hanya saat profil dibuka, lalu disimpan di HP.
- **Nickname** ditampilkan di leaderboard.
- **Nomor KMC** di profil bisa **Ditampilkan / Disembunyikan** oleh anggota. Di leaderboard Nomor KMC **tidak pernah** tampil.
- **Nama asli tidak ditampilkan** di mana pun. Hanya admin yang tahu.

## 8. Leaderboard

Hanya dua papan:

| Papan | Isi | Aturan |
|---|---|---|
| 🔥 Streak | Streak yang sedang menyala | Dihitung dari hari ketika aplikasi dibuka **saat online** |
| ⚡ Juara Kilat | Skor terbaik Kuis Kilat 60 detik | Hanya skor yang dikerjakan **saat online** |

- Ditampilkan: 10 besar, ditambah posisi anggota sendiri.
- Anti curang: server menolak angka yang tidak masuk akal, misalnya skor Kilat di atas batas yang mungkin dicapai dalam 60 detik.

## 9. Menu Admin (di Aplikasi, Khusus Akun Admin)

- Tambah anggota (satu per satu atau impor CSV)
- Reset PIN, reset perangkat, buka kunci, blokir anggota
- Hapus foto atau reset nickname yang tidak pantas

### Menjadi Admin

Menu Admin di aplikasi (Profil → Akun Saya → Menu Admin) hanya muncul untuk akun admin. Jadikan akunmu admin sekali lewat SQL Editor:

```sql
update private.members set is_admin = true where kmc = 'KMC-00001-00';
```

Menu Admin bisa: lihat dan cari anggota, tambah anggota, impor CSV dari Google Sheets, reset PIN, reset perangkat, buka kunci, blokir/buka blokir, hapus foto, reset nickname, dan hapus anggota.

### Perintah Admin Lewat SQL Editor (cadangan)

| Keperluan | Perintah |
|---|---|
| Tambah anggota | `select private.add_member('KMC-00001-00', 'Nama Lengkap', 'Batulicin', '2005-08-17');` |
| Impor CSV | Impor CSV ke tabel `private.import_members` (kolom `kmc, nama, tempat_lahir, tanggal_lahir`), lalu `select private.import_pending();` |
| Lihat anggota | `select * from private.member_list;` |
| Lupa PIN | `select private.reset_pin('KMC-00001-00');` |
| HP hilang/rusak | `select private.reset_device('KMC-00001-00');` |
| Buka kunci | `select private.unlock('KMC-00001-00');` |
| Blokir / buka blokir | `select private.block('KMC-00001-00');` / `select private.unblock('KMC-00001-00');` |

Skema lengkap ada di `supabase/schema.sql`.

## 10. Aturan Tambahan

- Reset perangkat oleh admin: **tanpa batas**.
- Nickname: unik (tidak boleh sama dengan anggota lain), **1–16 karakter**, bisa diganti maksimal **sekali seminggu**.

## 10a. Fitur Lain: Cari Hanzi Lewat Tulisan

- Di Kamus ada kotak tulis. Anggota menulis satu hanzi dengan **urutan goresan bebas** (yang penting bentuknya mirip).
- Setiap selesai satu goresan, muncul **8–10 kandidat** paling mirip. Kandidat yang diketuk masuk ke kolom pencarian.
- Ada tombol Hapus Goresan Terakhir dan Bersihkan.
- Hanya mengenali hanzi yang ada di kamus Read It (HSK 1–4). Berjalan offline dan tidak butuh server.
- Memakai ulang pengenal tulisan dari fitur Tulis Hanzi.

## 11. Batasan yang Perlu Diketahui

- Orang yang tahu nomor KMC orang lain bisa sengaja salah PIN/data lahir berkali-kali sampai KMC itu terkunci. Datanya tetap aman (tidak bisa masuk), dan admin bisa membuka kuncinya. Ini konsekuensi dari aturan kunci permanen.
- Aplikasi yang dibongkar oleh orang yang sangat ahli tetap bisa diakali. Sistem ini cukup sebagai pencegah untuk skala komunitas.
- Kalau ada HP yang sengaja dibuat offline terus, HP itu baru logout setelah online lagi. Mode Offline 3 hari membatasi celah ini.

## 11a. Fitur yang Sudah Ada di Aplikasi

- Layar pembuka, Masuk, Daftar, Lupa PIN, Mode Offline 3 hari, dan Keluar.
- **Scan Kartu**: pindai QR/barcode atau foto nomor yang tercetak di kartu (dibaca otomatis). Nomor juga tetap bisa diketik.
- **Akun Saya**: foto profil dari galeri, nickname (seminggu sekali), tampil/sembunyikan nomor KMC, Ganti PIN.
- **Leaderboard**: 🔥 Streak (hari dibuka saat online) dan ⚡ Juara Kilat (skor Kuis Kilat saat online), 10 besar + posisi sendiri, profil anggota bisa dibuka.
- **Menu Admin** untuk akun admin.
- **Kamus → Cari Lewat Tulisan**.
- Widget terkunci sampai anggota masuk.
- GitHub Actions: **Ping Server Harian** dan **Backup Database Mingguan** (terenkripsi). Jadwal otomatis GitHub hanya berjalan dari branch `main`. Backup butuh secret `SUPABASE_DB_URL` dan `BACKUP_PASSPHRASE`.

## 12. Urutan Pengerjaan (Rencana)

0. ✅ Tampilan akun dalam Mode Demo (server tiruan di HP, akun contoh tampil di layar).
1. Siapkan proyek Supabase: tabel, fungsi login, PIN, kunci, dan perangkat, dengan akses langsung ke tabel ditutup.
2. Siapkan ping harian dan backup mingguan.
3. Read It: layar aktivasi, login, loading, Mode Offline, dan logout.
4. Profil (foto, nickname, tampil/sembunyikan KMC).
5. Leaderboard Streak dan Juara Kilat.
6. Menu admin.
7. Uji coba dengan beberapa anggota sebelum dibagikan ke grup.

## 13. Catatan Perbaikan (Sudah Dikerjakan di Versi Lengkap Kedua)

- **Pengenal tulisan (Kamus → Cari Lewat Tulisan, dan Tulis Hanzi)**: hasil sekarang kurang tepat. Contoh: 是 dan 我 yang ditulis cukup jelas tidak masuk kandidat atas, karena cetakannya memakai font bergaya kuas dan pencocokannya hanya berdasarkan jarak garis.
  - Rencana: pakai **Google ML Kit Digital Ink Recognition** (model Mandarin ±20 MB diunduh sekali, lalu offline; urutan goresan bebas, toleran untuk tulisan kurang rapi).
  - Cara lama tetap jadi cadangan sebelum model terunduh.
  - Tulis Hanzi: dianggap benar kalau hanzi target masuk beberapa kandidat teratas.
  - Kandidat ditampilkan 2 baris supaya semua 10 terlihat tanpa digeser.
- **Notif streak** hanya sekali, saat aplikasi pertama dibuka di hari baru.
- **Nomor KMC di profil** disamarkan `KMC-•••••-••` dengan tombol mata 👁.
- **Tab Profil dirombak total**: cincin XP di foto, level, gelar, angka utama, pajangan 3 lencana, kalender aktivitas, statistik bisa dibuka-tutup. Ide tambahan dipilih bebas oleh pengembang dulu, lalu dinilai pemilik: Kartu Anggota Digital (bisa dibalik, ada QR nomor KMC), bingkai foto mengikuti rank, dan gelar yang dipilih dari lencana.
- **Halaman Lencana**: 2 kolom, tinggi teks seragam, bar progres sejajar di dasar kartu, beda jelas antara terkunci (abu-abu + 🔒) dan didapat (berwarna + tanggal), ringkasan dan filter.
- **Leaderboard 3 papan**: 🔥 **Streak Terpanjang**, ⚡ **Kilat Mingguan**, dan 🏅 **Rank** (urut dari rank tertinggi, lalu RP); papan Kilat **direset tiap Senin 00.00 WITA**, hitung mundur reset, lencana **Juara Minggu Ini** untuk peringkat 1 Kilat Mingguan (tanpa papan Juara Minggu Lalu); tampilan podium baru, panah naik/turun, baris sendiri disorot.
- **Layar sambutan/sukses**: "Selamat Datang," kecil + nama di baris sendiri (mengecil otomatis), maskot melambai menggantikan centang, konfeti memenuhi layar. Hanzi latar dibiarkan.
- **Flashcard**: kembali ke model geser (kanan = Hafal, kiri = Lupa), kartu miring mengikuti jari dan terlempar keluar.
- **Rank + Games** (disetujui):
  - Tiga game main sendiri: **Enggang Terbang**, **Hanzi Jatuh**, **Pasangkan Kartu**, semuanya mengisi satu Rank.
  - **9 tingkatan**: Iron 铁, Bronze 铜, Silver 银, Gold 金, Platinum 铂, Diamond 钻, Obsidian 曜, Phoenix 凤 (masing-masing divisi III–I, 100 RP per divisi), lalu **Dragon 龙** di puncak tanpa divisi (diurutkan berdasarkan RP). Total 2.400 RP untuk mencapai Dragon.
  - RP per permainan dibanding skor target rank: ≥130% +30, 110–129% +20, 90–109% +10, 70–89% −5, <70% −10, keluar di tengah −10.
  - Perlindungan 3 permainan setelah naik; Iron III tidak bisa turun; 3 permainan pertama tiap hari RP ×2 (kalau positif); RP hanya saat online; musim 3 bulan. **Reset musim**: Dragon/Phoenix/Obsidian → Platinum; Diamond dan Platinum → Gold; Gold → Silver; Silver, Bronze, Iron tetap. Semua kembali ke **divisi III dengan 0 RP**. Rank tertinggi tiap musim disimpan sebagai lencana.
- **Update aplikasi**: APK otomatis disalin ke repo publik `MoriyamaRei/Kuaile-Learn-Update` (token disimpan sebagai secret `RELEASE_TOKEN` di repo ini). Aplikasi mengecek versi saat dibuka, jendela "Pembaruan Tersedia" + unduh di aplikasi, ada opsi update wajib.
- **Kotak Surat**: Pengumuman, Info Update (otomatis), Acara, Hadiah, Akun; lencana belum dibaca; disematkan; admin kirim dari Menu Admin ke semua/tertentu; notifikasi lewat cek berkala (tanpa Firebase).

### Pemasangan Server Versi 3

Jalankan `supabase/bagian-5.sql`, `bagian-6.sql`, lalu `bagian-7.sql` berurutan di SQL Editor (bagian 1–4 sudah terpasang sebelumnya). `supabase/schema.sql` tetap berisi skema lengkap versi 1–3.

### Update Otomatis

Setiap build menyalin APK ke repo publik `MoriyamaRei/Kuaile-Learn-Update`. Tulis `[WAJIB]` di pesan commit untuk membuat update wajib.

## 14. Rank dari Kuis Kilat dan Perapian (Sudah Dikerjakan)

**Rank dari Kuis Kilat** (Enggang Terbang, Hanzi Jatuh, Pasangkan Kartu dihapus). Satu kali main Kilat dihitung untuk RP dan papan Kilat Mingguan.
- Langkah 1, % salah = salah ÷ total dijawab: di bawah 30% → lanjut ke langkah 2; tepat 30% → +5; 31–40% → −5; 41–50% → −10; 51–70% → −15; di atas 70% → −20.
- Langkah 2, skor dibanding target rank: ≥130% +30; 110–129% +20; 90–109% +10; 70–89% +5; di bawah 70% 0.
- Keluar di tengah −10; 3 permainan pertama tiap hari RP plus ×2; perlindungan 3 permainan setelah naik divisi; target dan kosakata naik sesuai rank.
- Layar hasil menampilkan alasan perhitungan RP.

**Arena / Rank**
- Halaman Aturan Rank (cara hitung, contoh, bonus, perlindungan, reset musim).
- Daftar Rank: katalog 9 rank dari Dragon ke Iron dengan lencana Divisi I, hanzi, pinyin, rentang RP; ketuk untuk melihat Divisi III, II, I.
- Lencana per divisi: III polos + ★, II bingkai ganda + ★★, I ornamen sayap + ★★★, Dragon mahkota naga. Animasi: rank 1–4 polos, 5–6 kilau halus, 7–9 penuh.

**Perapian Profil dan Beranda**
- Beranda: streak cukup 🔥 + angka; label pendek Title Case ("10 Kata Baru · 0 Ulangan", "0 Kartu", "0 Hafal").
- Profil: streak, level, rank, dan XP disatukan di kepala profil; kartu Level terpisah dan Kalender Aktivitas dihapus; kartu api pindah ke Statistik Lengkap; angka utama jadi 3 (Hafal, Kilat Minggu Ini, Juara Mingguan).

Server: jalankan `supabase/bagian-8.sql` (versi 4). `supabase/schema.sql` tetap berisi skema lengkap versi 1–4.
