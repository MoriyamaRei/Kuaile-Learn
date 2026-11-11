package com.nesxha.readit.util

import com.google.android.gms.tasks.Task
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.common.model.RemoteModelManager
import com.google.mlkit.vision.digitalink.DigitalInkRecognition
import com.google.mlkit.vision.digitalink.DigitalInkRecognitionModel
import com.google.mlkit.vision.digitalink.DigitalInkRecognitionModelIdentifier
import com.google.mlkit.vision.digitalink.DigitalInkRecognizer
import com.google.mlkit.vision.digitalink.DigitalInkRecognizerOptions
import com.google.mlkit.vision.digitalink.Ink
import com.google.mlkit.vision.digitalink.RecognitionContext
import com.google.mlkit.vision.digitalink.WritingArea
import com.nesxha.readit.platform.InkEngine
import com.nesxha.readit.platform.InkEngine.State
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.coroutines.resume

/**
 * Pengenal tulisan tangan Google ML Kit (dilatih dengan tulisan tangan asli, urutan goresan bebas).
 * Model Mandarin (±20 MB) diunduh sekali saat pertama dipakai, setelah itu bekerja tanpa internet.
 * Kalau model belum ada, aplikasi memakai pengenal sederhana ([Handwriting]) sebagai cadangan.
 */
object MlKitInk : InkEngine {
    @Volatile override var state = State.BELUM
        private set
    private var recognizer: DigitalInkRecognizer? = null
    private val lock = Mutex()

    private val model: DigitalInkRecognitionModel? by lazy {
        val id = runCatching { DigitalInkRecognitionModelIdentifier.fromLanguageTag("zh-Hani-CN") }.getOrNull()
            ?: runCatching { DigitalInkRecognitionModelIdentifier.fromLanguageTag("zh-Hani") }.getOrNull()
        id?.let { DigitalInkRecognitionModel.builder(it).build() }
    }

    private suspend fun <T> Task<T>.await(): T? = suspendCancellableCoroutine { c ->
        addOnSuccessListener { c.resume(it) }
        addOnFailureListener { c.resume(null) }
        addOnCanceledListener { c.resume(null) }
    }

    /** Menyiapkan model (mengunduh kalau perlu). True kalau siap dipakai. */
    override suspend fun ensure(onState: (State) -> Unit): Boolean = lock.withLock {
        if (state == State.SIAP && recognizer != null) return@withLock true
        val m = model ?: run { state = State.GAGAL; onState(state); return@withLock false }
        val mgr = RemoteModelManager.getInstance()
        val have = runCatching { mgr.isModelDownloaded(m).await() }.getOrNull() == true
        if (!have) {
            state = State.MENGUNDUH; onState(state)
            val ok = runCatching { mgr.download(m, DownloadConditions.Builder().build()).await() }.isSuccess &&
                runCatching { mgr.isModelDownloaded(m).await() }.getOrNull() == true
            if (!ok) { state = State.GAGAL; onState(state); return@withLock false }
        }
        recognizer = DigitalInkRecognition.getClient(DigitalInkRecognizerOptions.builder(m).build())
        state = State.SIAP; onState(state)
        true
    }

    /** Kandidat hanzi (satu karakter) dari yang paling mungkin. Null kalau model belum siap. */
    override suspend fun recognize(strokes: List<List<Pt>>, width: Float, height: Float): List<Char>? {
        val r = recognizer ?: return null
        if (strokes.isEmpty()) return emptyList()
        val ink = Ink.builder().apply {
            var t = 0L
            strokes.forEach { s ->
                val b = Ink.Stroke.builder()
                s.forEach { p -> b.addPoint(Ink.Point.create(p.x, p.y, t)); t += 12 }
                t += 200
                addStroke(b.build())
            }
        }.build()
        val ctx = RecognitionContext.builder().setPreContext("").setWritingArea(WritingArea(width, height)).build()
        val res = runCatching { r.recognize(ink, ctx).await() }.getOrNull() ?: return null
        return res.candidates.flatMap { it.text.toList() }.filter { it in '一'..'鿿' || it == '〇' }.distinct()
    }
}
