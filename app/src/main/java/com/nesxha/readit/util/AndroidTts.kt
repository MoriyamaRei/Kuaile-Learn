package com.nesxha.readit.util

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

/** Mesin TTS Android (dipakai oleh platform Android untuk Tts bersama). */
object AndroidTts {
    private var tts: TextToSpeech? = null
    private var ready = false
    private var pending: String? = null
    var available: Boolean = true; private set

    fun init(ctx: Context) {
        if (tts != null) return
        tts = TextToSpeech(ctx.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val r = tts?.setLanguage(Locale.SIMPLIFIED_CHINESE) ?: TextToSpeech.LANG_NOT_SUPPORTED
                available = r != TextToSpeech.LANG_MISSING_DATA && r != TextToSpeech.LANG_NOT_SUPPORTED
                tts?.setSpeechRate(0.85f)
                ready = true
                pending?.let { speakAt(it, pendingRate) }
                pending = null
            } else available = false
        }
    }

    private var pendingRate = 0.9f

    /** Bicara dengan kecepatan [rate] (1.0 = normal). */
    fun speakAt(text: String, rate: Float) {
        val clean = text.replace("…", "").replace("（", " ").replace("）", " ")
        if (!ready) { pending = clean; pendingRate = rate; return }
        tts?.setSpeechRate(rate)
        tts?.speak(clean, TextToSpeech.QUEUE_FLUSH, null, "readit")
    }
}
