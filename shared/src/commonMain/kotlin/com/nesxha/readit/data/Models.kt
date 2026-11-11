package com.nesxha.readit.data

import com.nesxha.readit.json.JSONObject
import com.nesxha.readit.platform.localEpochDay

/** Nomor hari lokal hari ini (sejak 1970-01-01). */
fun today(): Long = localEpochDay()

/** Nilai jawaban flashcard. */
enum class Grade(val label: String) { LUPA("Lupa"), SUSAH("Susah"), BISA("Bisa"), GAMPANG("Gampang") }

enum class Status(val label: String) { BARU("Baru"), BELAJAR("Belajar"), HAFAL("Hafal") }

/** Keadaan ulangan berjarak satu kata. */
data class Card(
    val reps: Int = 0,
    val interval: Int = 0,      // hari
    val ease: Double = 2.5,
    val due: Long = 0,          // epoch day
    val lapses: Int = 0,
    val wrong: Int = 0,         // total salah (flashcard + kuis)
    val right: Int = 0,
    val learnedOn: Long = -1,   // hari pertama kali dipelajari, -1 = belum
    val star: Boolean = false,
    val t: Long = 0,            // waktu terakhir diubah (ms), untuk sinkron antarperangkat
) {
    val seen: Boolean get() = learnedOn >= 0
    val status: Status get() = when {
        !seen -> Status.BARU
        interval >= 21 -> Status.HAFAL
        else -> Status.BELAJAR
    }

    fun toJson(): JSONObject = JSONObject().apply {
        put("r", reps); put("i", interval); put("e", ease); put("d", due); put("l", lapses)
        put("w", wrong); put("ok", right); put("lo", learnedOn); put("s", star)
        if (t > 0) put("t", t)
    }

    companion object {
        fun fromJson(o: JSONObject) = Card(
            o.optInt("r"), o.optInt("i"), o.optDouble("e", 2.5), o.optLong("d"), o.optInt("l"),
            o.optInt("w"), o.optInt("ok"), o.optLong("lo", -1), o.optBoolean("s"), o.optLong("t", 0),
        )
    }
}

/** Catatan aktivitas satu hari. */
data class DayLog(
    val reviews: Int = 0,
    val newWords: Int = 0,
    val correct: Int = 0,
    val wrong: Int = 0,
    val quiz: Int = 0,
    val opened: Boolean = false,  // aplikasi dibuka hari itu (hadir)
    val xp: Int = 0,
) {
    val total: Int get() = reviews + quiz
    val attended: Boolean get() = opened || total > 0

    fun toJson(): JSONObject = JSONObject().apply {
        put("r", reviews); put("n", newWords); put("c", correct); put("w", wrong); put("q", quiz); put("o", opened); put("x", xp)
    }

    companion object {
        fun fromJson(o: JSONObject) = DayLog(o.optInt("r"), o.optInt("n"), o.optInt("c"), o.optInt("w"), o.optInt("q"), o.optBoolean("o"), o.optInt("x"))
    }
}

/** Arah kartu flashcard. */
enum class Direction(val label: String) { HANZI("Hanzi → Arti"), ARTI("Arti → Hanzi"), ACAK("Acak") }

data class Settings(
    val newPerDay: Int = 10,
    val reminderOn: Boolean = false,
    val reminderHour: Int = 19,
    val reminderMinute: Int = 0,
    val darkMode: Int = 0,           // 0 ikuti sistem, 1 terang, 2 gelap
    val hidePinyin: Boolean = false,
    val direction: Direction = Direction.HANZI,
    val byTheme: Boolean = false,
    val autoSpeak: Boolean = true,
    val voiceSpeed: Int = 0,         // 0 normal, 1 lambat, 2 sangat lambat
    val kilatMinutes: Int = 1,       // durasi Kuis Kilat terakhir yang dipilih (1–3 menit)
    val examplePinyin: Boolean = true, // pinyin di bawah contoh kalimat (tombol mata di Detail Kata)
) {
    fun toJson(): JSONObject = JSONObject().apply {
        put("npd", newPerDay); put("ron", reminderOn); put("rh", reminderHour); put("rm", reminderMinute)
        put("dark", darkMode); put("hp", hidePinyin); put("dir", direction.name); put("bt", byTheme); put("as", autoSpeak); put("vs", voiceSpeed)
        put("km", kilatMinutes); put("ep", examplePinyin)
    }

    companion object {
        fun fromJson(o: JSONObject) = Settings(
            o.optInt("npd", 10), o.optBoolean("ron"), o.optInt("rh", 19), o.optInt("rm", 0),
            o.optInt("dark", 0), o.optBoolean("hp"),
            runCatching { Direction.valueOf(o.optString("dir", "HANZI")) }.getOrDefault(Direction.HANZI),
            o.optBoolean("bt"), o.optBoolean("as", true), o.optInt("vs", 0),
            o.optInt("km", 1).coerceIn(1, 3), o.optBoolean("ep", true),
        )
    }
}
