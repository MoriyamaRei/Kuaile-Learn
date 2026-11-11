package com.nesxha.readit.platform

import androidx.compose.runtime.Composable

/** Hasil permintaan HTTP: kode status dan isi jawaban. */
data class HttpResult(val code: Int, val body: String)

/** Kemampuan yang hanya ada di sebagian platform (Android punya semuanya, web sebagian). */
data class Features(
    val reminder: Boolean = false,      // pengingat harian lewat notifikasi
    val widgets: Boolean = false,       // widget layar utama
    val cardScanner: Boolean = false,   // scan kartu KMC dengan kamera
    val photoPicker: Boolean = true,    // pilih foto profil
    val appUpdate: Boolean = false,     // update APK dari dalam aplikasi
    val fileBackup: Boolean = true,     // simpan/pulihkan cadangan sebagai file
)

/**
 * Semua hal yang berbeda antara Android dan web. Android mengisinya dari modul :app,
 * web dari wasmJsMain. Kode bersama hanya memakai antarmuka ini.
 */
interface Platform {
    val isWeb: Boolean
    val features: Features

    /** Penyimpanan kecil kunci-nilai per "file" (SharedPreferences di Android, localStorage di web). */
    fun prefGet(file: String, key: String): String?
    fun prefPut(file: String, key: String, value: String?)

    /** Kirim permintaan HTTP. Melempar exception kalau jaringan gagal. */
    suspend fun http(method: String, url: String, headers: Map<String, String>, body: String?): HttpResult

    fun hasInternet(): Boolean

    fun speak(text: String, rate: Float)
    val ttsAvailable: Boolean

    fun toast(message: String)
    fun openUrl(url: String)
    fun shareText(text: String)

    /** Berkas cache kecil (misalnya cetakan pengenal tulisan). */
    fun readCache(name: String): ByteArray?
    fun writeCache(name: String, data: ByteArray)

    /** Mencetak gambar karakter (untuk pengenal tulisan sederhana): true = piksel tinta, ukuran [size]×[size]. */
    fun glyphMask(c: Char, size: Int): BooleanArray

    /** Pilih file (cadangan JSON / CSV) lalu kembalikan isinya; null kalau batal. */
    suspend fun pickTextFile(mime: String): String?
    /** Simpan teks sebagai file unduhan / dokumen. */
    suspend fun saveTextFile(name: String, mime: String, text: String): Boolean

    /** Pilih foto profil dari galeri: dipotong kotak, versi kecil + asli (≤ 1 MB) dalam base64. null kalau batal. */
    suspend fun pickProfilePhoto(): com.nesxha.readit.util.Photos.Prepared?
}

/** Akun KMC (login, leaderboard, rank, surat, admin). Dimatikan: aplikasi berjalan tanpa login. */
object Accounts { const val ON = false }

/** Titik akses platform yang sedang berjalan. Diisi sekali saat aplikasi mulai. */
object Plat {
    lateinit var p: Platform
    val ready: Boolean get() = ::p.isInitialized
}

/** Layar atau komponen yang isinya khusus platform (misalnya scan kartu, dialog update). */
interface PlatformUi {
    @Composable fun UpdateCheck() {}
    /** Pemindai kartu KMC (kamera). Mengembalikan fungsi untuk membuka pemindai, atau null kalau tidak ada. */
    @Composable fun rememberCardScanner(onResult: (String) -> Unit): (() -> Unit)? = null
    /** Pengenal tulisan tambahan (ML Kit di Android). null = tidak ada, pakai pengenal sederhana. */
    val ink: InkEngine? get() = null
    /** Dipanggil saat pengaturan pengingat berubah. */
    fun scheduleReminder() {}
    /** Minta izin notifikasi (Android 13+). true kalau diizinkan. */
    suspend fun requestNotificationPermission(): Boolean = true
    /** Dialog pilih jam bawaan platform. */
    fun pickTime(hour: Int, minute: Int, onPicked: (Int, Int) -> Unit) {}
    /** Dipanggil saat data belajar berubah (misalnya menyegarkan widget). */
    fun onDataChanged() {}
    /** Mulai pemeriksaan surat berkala di latar belakang. */
    fun startMailWorker() {}
    fun onSignedOut() {}
}

object PlatUi {
    var ui: PlatformUi = object : PlatformUi {}
}

/** Pengenal tulisan tangan dari platform (ML Kit). */
interface InkEngine {
    enum class State { BELUM, MENGUNDUH, SIAP, GAGAL }
    val state: State
    suspend fun ensure(onState: (State) -> Unit = {}): Boolean
    suspend fun recognize(strokes: List<List<com.nesxha.readit.util.Pt>>, width: Float, height: Float): List<Char>?
}
