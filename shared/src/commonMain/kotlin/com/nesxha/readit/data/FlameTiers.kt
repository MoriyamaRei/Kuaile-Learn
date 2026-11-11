package com.nesxha.readit.data

/** Tingkat api streak: [from] = streak pertama yang masuk tingkat ini. */
data class FlameTier(val level: Int, val from: Int, val name: String)

object FlameTiers {
    val all = listOf(
        FlameTier(0, 1, "Api Merah Redup"),
        FlameTier(1, 11, "Api Merah Terang"),
        FlameTier(2, 26, "Api Oranye"),
        FlameTier(3, 51, "Api Kuning"),
        FlameTier(4, 76, "Api Putih Kekuningan"),
        FlameTier(5, 101, "Api Biru"),
        FlameTier(6, 151, "Api Putih Terang"),
        FlameTier(7, 201, "Api Biru Muda"),
        FlameTier(8, 301, "Api Violet"),
    )

    fun of(streak: Int): FlameTier = all.last { streak >= it.from || it.level == 0 }

    /** Streak yang dirayakan dengan animasi api besar: awal tiap tingkat baru, lalu tiap 100 hari setelah 300. */
    fun isMilestone(streak: Int): Boolean =
        all.any { it.level > 0 && it.from == streak } || (streak >= 400 && streak % 100 == 0)

    /** Hari menuju tingkat berikutnya (null kalau sudah tingkat tertinggi). */
    fun next(streak: Int): FlameTier? = all.firstOrNull { it.from > streak }
}
