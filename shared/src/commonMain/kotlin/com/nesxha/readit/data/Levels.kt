package com.nesxha.readit.data

/** Keadaan level dari total XP. */
data class LevelInfo(
    val level: Int,
    val xpInLevel: Int,     // XP yang sudah dikumpulkan di level ini
    val xpForNext: Int,     // XP yang dibutuhkan untuk naik ke level berikutnya
    val total: Int,
) {
    val fraction: Float get() = if (xpForNext == 0) 0f else xpInLevel.toFloat() / xpForNext
    val toNext: Int get() = xpForNext - xpInLevel
    val title: String get() = Levels.title(level)
    val hanzi: String get() = Levels.hanziTitle(level)
}

/** Sumber dan besar XP. */
object Xp {
    const val DAILY = 10
    const val STREAK_BONUS_MAX = 30
    const val NEW_WORD = 5
    fun grade(g: Grade) = when (g) { Grade.LUPA -> 2; Grade.SUSAH -> 4; Grade.BISA -> 5; Grade.GAMPANG -> 6 }
    const val QUIZ_RIGHT = 3
    const val QUIZ_WRONG = 1
    const val SENTENCE_RIGHT = 8
    const val SENTENCE_WRONG = 2
    const val WRITE_CHAR = 6
    const val WRITE_WRONG = 2
    const val GOAL = 30
    const val EXAM_PASS = 50
    const val EXAM_PERFECT = 100
    const val BADGE = 50
    const val FLAME_TIER = 100
}

object Levels {
    /** XP untuk naik dari level [level] ke level berikutnya. */
    fun need(level: Int): Int = 60 + 15 * level

    fun info(totalXp: Int): LevelInfo {
        var lv = 1
        var rest = totalXp.coerceAtLeast(0)
        while (rest >= need(lv)) { rest -= need(lv); lv++ }
        return LevelInfo(lv, rest, need(lv), totalXp)
    }

    fun title(level: Int): String = when {
        level < 5 -> "Pemula"
        level < 10 -> "Pelajar"
        level < 20 -> "Penjelajah"
        level < 35 -> "Mahir"
        level < 50 -> "Cendekia"
        level < 75 -> "Ahli"
        level < 100 -> "Master"
        level < 200 -> "Grandmaster"
        level < 500 -> "Legenda"
        else -> "Legenda Abadi"
    }

    fun hanziTitle(level: Int): String = when {
        level < 5 -> "新手"
        level < 10 -> "学生"
        level < 20 -> "探索者"
        level < 35 -> "熟练"
        level < 50 -> "学者"
        level < 75 -> "专家"
        level < 100 -> "大师"
        level < 200 -> "宗师"
        level < 500 -> "传奇"
        else -> "不朽传奇"
    }

    /** Level pertama tiap gelar (untuk mengumumkan gelar baru). */
    val titleStarts = listOf(1, 5, 10, 20, 35, 50, 75, 100, 200, 500)
}
