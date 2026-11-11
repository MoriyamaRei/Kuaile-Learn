package com.nesxha.readit.data

/**
 * Nama penghitung di AppState.counters. Semua kunci dibuat di sini supaya tidak ada
 * teks kunci yang ditulis langsung di tempat lain (rawan salah ketik).
 */
object Counter {
    const val BEST_KILAT = "bestKilat"
    /** Rekor skor Kilat versi 5 (skor dari kecepatan; skala berbeda dari rekor lama). */
    const val BEST_KILAT_SCORE = "bestKilatScore"
    const val WRITE_OK = "writeOk"
    const val SENTENCE_OK = "sentenceOk"
    const val EXAM_PASS = "examPass"
    const val EXAM_PERFECT = "examPerfect"
    const val RESTORES = "restores"
    const val FLAME_SHOWN = "flameShown"
    const val FLAME_DAY = "flameDay"
    const val XP = "xp"
    const val GOAL_DAY = "goalDay"
    fun examBest(level: Int) = "examBest$level"
    const val NUM_OK = "numOk"
    fun numOk(cat: NumCat) = "numOk_${cat.name}"
    fun numTotal(cat: NumCat) = "numTotal_${cat.name}"
    fun lessonRead(cat: NumCat) = "lessonRead_${cat.name}"
}
