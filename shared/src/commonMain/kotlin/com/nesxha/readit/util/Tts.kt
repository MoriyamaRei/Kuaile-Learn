package com.nesxha.readit.util

import com.nesxha.readit.platform.Plat

/** Suara Mandarin lewat mesin suara bawaan perangkat (TextToSpeech di Android, Web Speech di browser). */
object Tts {
    val available: Boolean get() = Plat.ready && Plat.p.ttsAvailable

    /** Kecepatan bicara sesuai pengaturan: normal, lambat, sangat lambat. */
    fun rateFor(speed: Int): Float = when (speed) { 1 -> 0.6f; 2 -> 0.4f; else -> 0.9f }

    fun speak(text: String, speed: Int = com.nesxha.readit.data.Store.now.settings.voiceSpeed) {
        if (!Plat.ready) return
        val clean = text.replace("…", "").replace("（", " ").replace("）", " ")
        Plat.p.speak(clean, rateFor(speed))
    }
}
