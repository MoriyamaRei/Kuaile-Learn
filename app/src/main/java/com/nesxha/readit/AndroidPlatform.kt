package com.nesxha.readit

import android.Manifest
import android.app.TimePickerDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.core.content.ContextCompat
import com.nesxha.readit.auth.Auth
import com.nesxha.readit.data.Store
import com.nesxha.readit.data.Words
import com.nesxha.readit.platform.Features
import com.nesxha.readit.platform.HttpResult
import com.nesxha.readit.platform.InkEngine
import com.nesxha.readit.platform.Plat
import com.nesxha.readit.platform.PlatUi
import com.nesxha.readit.platform.Platform
import com.nesxha.readit.platform.PlatformUi
import com.nesxha.readit.util.AndroidPhotos
import com.nesxha.readit.util.AndroidTts
import com.nesxha.readit.util.MailWorker
import com.nesxha.readit.util.MlKitInk
import com.nesxha.readit.util.Photos
import com.nesxha.readit.util.Reminder
import com.nesxha.readit.widget.WidgetSync
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.lang.ref.WeakReference
import java.net.HttpURLConnection
import java.net.URL

/** Lokasi berkas Compose Resources di dalam APK (assets). */
private const val RES_DIR = "composeResources/com.nesxha.readit.res"

/** Semua hal khusus Android yang dibutuhkan kode bersama. */
object AndroidPlatform : Platform {
    private lateinit var app: Context
    private var activityRef: WeakReference<ComponentActivity>? = null
    private val main = Handler(Looper.getMainLooper())

    // Peluncur ActivityResult harus didaftarkan saat onCreate; hasilnya diteruskan ke permintaan yang sedang menunggu.
    private var openDoc: ActivityResultLauncher<Array<String>>? = null
    private var createDoc: ActivityResultLauncher<String>? = null
    private var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>? = null
    private var notifPerm: ActivityResultLauncher<String>? = null
    private var waitUri: CompletableDeferred<Uri?>? = null
    private var waitPerm: CompletableDeferred<Boolean>? = null

    /** Menyiapkan data dan platform tanpa layar (dipakai juga oleh widget dan pekerja latar). */
    fun installBase(ctx: Context) {
        if (Plat.ready) return
        synchronized(this) {
            if (Plat.ready) return
            app = ctx.applicationContext
            Plat.p = this
            PlatUi.ui = AndroidUi
            Words.init { app.assets.open("$RES_DIR/files/words.json").bufferedReader().use { it.readText() } }
            val file = File(app.filesDir, "progress.json")
            Store.init(object : Store.StoreIo {
                override fun read(): String? = if (file.exists()) file.readText() else null
                override fun write(text: String) {
                    val tmp = File(file.parentFile, "progress.json.tmp")
                    tmp.writeText(text)
                    tmp.renameTo(file)
                }
            })
            Store.onChange = { WidgetSync.refresh(app) }
            Auth.init()
            // Cetakan lama pengenal tulisan (format lama) tidak dipakai lagi.
            File(app.filesDir, "hw_cetakan_v1.bin").delete()
        }
    }

    /** Dipanggil di MainActivity.onCreate sebelum setContent. */
    fun install(activity: ComponentActivity) {
        installBase(activity)
        activityRef = WeakReference(activity)
        AndroidTts.init(activity)
        openDoc = activity.registerForActivityResult(ActivityResultContracts.OpenDocument()) { waitUri?.complete(it) }
        createDoc = activity.registerForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { waitUri?.complete(it) }
        pickMedia = activity.registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { waitUri?.complete(it) }
        notifPerm = activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) { waitPerm?.complete(it) }
    }

    private val activity: ComponentActivity? get() = activityRef?.get()

    override val isWeb = false
    override val features = Features(reminder = true, widgets = true, cardScanner = true, photoPicker = true, appUpdate = true, fileBackup = true)

    // ---------- Simpanan kecil ----------
    private fun sp(file: String) = app.getSharedPreferences(file, Context.MODE_PRIVATE)
    override fun prefGet(file: String, key: String): String? = sp(file).all[key]?.toString()
    override fun prefPut(file: String, key: String, value: String?) {
        val e = sp(file).edit()
        if (value == null) e.remove(key) else e.putString(key, value)
        e.apply()
    }

    // ---------- Jaringan ----------
    override suspend fun http(method: String, url: String, headers: Map<String, String>, body: String?): HttpResult = withContext(Dispatchers.IO) {
        val c = URL(url).openConnection() as HttpURLConnection
        try {
            c.requestMethod = method
            c.connectTimeout = 10_000; c.readTimeout = 15_000
            c.setRequestProperty("User-Agent", "KuaileLearn")
            headers.forEach { (k, v) -> c.setRequestProperty(k, v) }
            if (body != null) {
                c.doOutput = true
                c.outputStream.use { it.write(body.toByteArray()) }
            }
            val code = c.responseCode
            val stream = if (code in 200..299) c.inputStream else c.errorStream
            HttpResult(code, stream?.bufferedReader()?.use { it.readText() } ?: "")
        } finally { c.disconnect() }
    }

    override fun hasInternet(): Boolean {
        val cm = app.getSystemService(ConnectivityManager::class.java) ?: return false
        val caps = cm.getNetworkCapabilities(cm.activeNetwork) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
            caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }

    // ---------- Suara, pesan, tautan ----------
    override fun speak(text: String, rate: Float) { AndroidTts.init(app); AndroidTts.speakAt(text, rate) }
    override val ttsAvailable: Boolean get() = AndroidTts.available

    override fun toast(message: String) {
        main.post { Toast.makeText(app, message, if (message.length > 40) Toast.LENGTH_LONG else Toast.LENGTH_SHORT).show() }
    }

    override fun openUrl(url: String) {
        runCatching { (activity ?: app).startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) }
    }

    override fun shareText(text: String) {
        val send = Intent(Intent.ACTION_SEND).setType("text/plain").putExtra(Intent.EXTRA_TEXT, text)
        runCatching { (activity ?: app).startActivity(Intent.createChooser(send, null).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) }
    }

    // ---------- Berkas cache ----------
    private fun cacheFile(name: String) = File(File(app.filesDir, "kc").apply { mkdirs() }, name)
    override fun readCache(name: String): ByteArray? = cacheFile(name).takeIf { it.exists() }?.readBytes()
    override fun writeCache(name: String, data: ByteArray) {
        // Foto lama anggota yang sama (versi sebelumnya) dihapus supaya tidak menumpuk.
        if (name.startsWith("photo_")) {
            val parts = name.split('_')
            if (parts.size == 4) cacheFile(name).parentFile?.listFiles { f -> f.name.startsWith("photo_${parts[1]}_") && f.name.endsWith("_" + parts[3]) && f.name != name }?.forEach { it.delete() }
        }
        val f = cacheFile(name)
        val tmp = File(f.parentFile, "$name.tmp")
        tmp.writeBytes(data); tmp.renameTo(f)
    }

    // ---------- Cetakan hanzi untuk pengenal tulisan ----------
    private val hanziFace: Typeface by lazy {
        runCatching { Typeface.createFromAsset(app.assets, "$RES_DIR/font/hanzi_regular.otf") }.getOrDefault(Typeface.DEFAULT)
    }

    override fun glyphMask(c: Char, size: Int): BooleanArray {
        val bmp = Bitmap.createBitmap(size, size, Bitmap.Config.ALPHA_8)
        val p = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            typeface = hanziFace; textSize = size * 0.8f; textAlign = Paint.Align.CENTER; color = android.graphics.Color.BLACK
        }
        val fm = p.fontMetrics
        Canvas(bmp).drawText(c.toString(), size / 2f, size / 2f - (fm.ascent + fm.descent) / 2f, p)
        val buf = java.nio.ByteBuffer.allocate(bmp.rowBytes * size)
        bmp.copyPixelsToBuffer(buf)
        val bytes = buf.array()
        val out = BooleanArray(size * size) { i -> (bytes[(i / size) * bmp.rowBytes + i % size].toInt() and 0xFF) > 128 }
        bmp.recycle()
        return out
    }

    // ---------- Pilih / simpan berkas ----------
    private suspend fun awaitUri(launch: () -> Unit): Uri? {
        val d = CompletableDeferred<Uri?>()
        waitUri?.complete(null)
        waitUri = d
        withContext(Dispatchers.Main) {
            try { launch() } catch (e: ActivityNotFoundException) { d.complete(null) } catch (e: IllegalStateException) { d.complete(null) }
        }
        return d.await().also { if (waitUri === d) waitUri = null }
    }

    override suspend fun pickTextFile(mime: String): String? {
        val l = openDoc ?: return null
        val types = if (mime == "text/csv") arrayOf("text/*", "application/csv", "application/vnd.ms-excel", "application/octet-stream")
        else arrayOf("application/json", "text/plain", "application/octet-stream")
        val uri = awaitUri { l.launch(types) } ?: return null
        return withContext(Dispatchers.IO) {
            app.contentResolver.openInputStream(uri)?.use { it.readBytes().toString(Charsets.UTF_8) }
        } ?: throw IllegalStateException("File tidak bisa dibaca.")
    }

    override suspend fun saveTextFile(name: String, mime: String, text: String): Boolean {
        val l = createDoc ?: return false
        val uri = awaitUri { l.launch(name) } ?: return false
        return withContext(Dispatchers.IO) {
            app.contentResolver.openOutputStream(uri)?.use { it.write(text.toByteArray()) } != null
        }
    }

    override suspend fun pickProfilePhoto(): Photos.Prepared? {
        val l = pickMedia ?: return null
        val uri = awaitUri { l.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) } ?: return null
        return AndroidPhotos.prepare(app, uri) ?: throw IllegalStateException("Foto tidak bisa dipakai.")
    }

    // ---------- Izin notifikasi & jam ----------
    internal suspend fun requestNotificationPermission(): Boolean {
        if (Build.VERSION.SDK_INT < 33) return true
        if (ContextCompat.checkSelfPermission(app, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) return true
        val l = notifPerm ?: return false
        val d = CompletableDeferred<Boolean>()
        waitPerm?.complete(false)
        waitPerm = d
        withContext(Dispatchers.Main) { runCatching { l.launch(Manifest.permission.POST_NOTIFICATIONS) }.onFailure { d.complete(false) } }
        return d.await()
    }

    internal fun pickTime(hour: Int, minute: Int, onPicked: (Int, Int) -> Unit) {
        val a = activity ?: return
        TimePickerDialog(a, { _, h, m -> onPicked(h, m) }, hour, minute, true).show()
    }

    internal val appContext: Context get() = app
}

/** Bagian tampilan khusus Android. */
object AndroidUi : PlatformUi {
    @Composable override fun UpdateCheck() = com.nesxha.readit.ui.member.UpdateCheck()

    @Composable override fun rememberCardScanner(onResult: (String) -> Unit): (() -> Unit)? =
        com.nesxha.readit.ui.auth.rememberCardScanner(onResult)

    override val ink: InkEngine get() = MlKitInk

    override fun scheduleReminder() = Reminder.schedule(AndroidPlatform.appContext)
    override suspend fun requestNotificationPermission(): Boolean = AndroidPlatform.requestNotificationPermission()
    override fun pickTime(hour: Int, minute: Int, onPicked: (Int, Int) -> Unit) = AndroidPlatform.pickTime(hour, minute, onPicked)
    override fun onDataChanged() = WidgetSync.refresh(AndroidPlatform.appContext)
    override fun startMailWorker() = MailWorker.schedule(AndroidPlatform.appContext)
    override fun onSignedOut() = WidgetSync.refresh(AndroidPlatform.appContext)
}
