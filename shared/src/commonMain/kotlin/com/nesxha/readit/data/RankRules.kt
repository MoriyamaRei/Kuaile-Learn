package com.nesxha.readit.data

import com.nesxha.readit.platform.roundHalfUp

/** Satu tingkatan rank. Warna dipakai untuk lencana dan bingkai foto. */
data class RankTier(val index: Int, val name: String, val hanzi: String, val pinyin: String, val colors: List<Long>)

/** Posisi rank dari total RP: tingkat, divisi (3 = III … 1 = I, 0 untuk Dragon), dan RP di divisi itu. */
data class RankPos(val tier: RankTier, val division: Int, val rp: Int) {
    val isDragon: Boolean get() = tier.index == RankRules.DRAGON
    val roman: String get() = when (division) { 3 -> "III"; 2 -> "II"; 1 -> "I"; else -> "" }
    val label: String get() = if (isDragon) tier.name else "${tier.name} $roman"
    /** Progres menuju divisi berikutnya (Dragon: selalu penuh). */
    val fraction: Float get() = if (isDragon) 1f else rp / 100f
}

/**
 * Aturan rank (sama dengan server, lihat supabase/v3.sql):
 * 9 tingkat; Iron sampai Phoenix punya divisi III, II, I (100 RP per divisi); Dragon di puncak.
 */
object RankRules {
    const val PER_DIVISION = 100
    const val PER_TIER = 300
    const val DRAGON = 8
    const val DRAGON_AT = DRAGON * PER_TIER

    val tiers = listOf(
        RankTier(0, "Iron", "铁", "tiě", listOf(0xFF6B7280, 0xFF9CA3AF)),
        RankTier(1, "Bronze", "铜", "tóng", listOf(0xFF92400E, 0xFFD97706)),
        RankTier(2, "Silver", "银", "yín", listOf(0xFF64748B, 0xFFE2E8F0)),
        RankTier(3, "Gold", "金", "jīn", listOf(0xFFB45309, 0xFFFCD34D)),
        RankTier(4, "Platinum", "铂", "bó", listOf(0xFF0E7490, 0xFFA5F3FC)),
        RankTier(5, "Diamond", "钻", "zuàn", listOf(0xFF1D4ED8, 0xFF93C5FD)),
        RankTier(6, "Obsidian", "曜", "yào", listOf(0xFF1E1B4B, 0xFF8B5CF6)),
        RankTier(7, "Phoenix", "凤", "fèng", listOf(0xFFB91C1C, 0xFFFB923C)),
        RankTier(8, "Dragon", "龙", "lóng", listOf(0xFF7C2D12, 0xFFFACC15)),
    )

    fun pos(p: Int): RankPos {
        val v = p.coerceAtLeast(0)
        if (v >= DRAGON_AT) return RankPos(tiers[DRAGON], 0, v - DRAGON_AT)
        val t = v / PER_TIER
        val inTier = v % PER_TIER
        return RankPos(tiers[t], 3 - inTier / PER_DIVISION, inTier % PER_DIVISION)
    }

    /** True kalau dari [old] ke [new] naik divisi atau tingkat (untuk animasi Rank Up). */
    fun promoted(old: Int, new: Int): Boolean = minOf(new, DRAGON_AT) / PER_DIVISION > minOf(old, DRAGON_AT) / PER_DIVISION

    fun tierUp(old: Int, new: Int): Boolean = pos(new).tier.index > pos(old).tier.index

    /** Tingkat setelah reset musim. */
    fun seasonReset(p: Int): Int {
        val t = pos(p).tier.index
        val nt = when { t >= 6 -> 4; t >= 4 -> 3; t == 3 -> 2; else -> t }
        return nt * PER_TIER
    }

    /** Kosakata yang dipakai game sesuai tingkat: Iron–Bronze HSK 1, Silver–Gold HSK 1–2, Platinum–Diamond HSK 1–3, ke atas HSK 1–4. */
    fun maxHsk(p: Int): Int = when (pos(p).tier.index) { 0, 1 -> 1; 2, 3 -> 2; 4, 5 -> 3; else -> 4 }

    /** Kecepatan game (1.0 = Iron) naik setiap tingkat. */
    fun speed(p: Int): Float = 1f + pos(p).tier.index * 0.12f

    /** Pilihan durasi Kuis Kilat (menit) dan pengalinya untuk skor dan RP plus. */
    val kilatMinutes = listOf(1, 2, 3)
    fun kilatMult(minutes: Int): Double = when (minutes) { 1 -> 2.0; 2 -> 1.5; else -> 1.0 }
    fun multLabel(minutes: Int): String = when (minutes) { 1 -> "×2"; 2 -> "×1,5"; else -> "×1" }

    /** Pengali RP plus untuk rank tinggi: Diamond dan Obsidian ×0,75; Phoenix dan Dragon ×0,5. */
    fun tierFactor(p: Int): Double = pos(p).tier.index.let { t -> if (t >= 7) 0.5 else if (t >= 5) 0.75 else 1.0 }

    /** Kelompok waktu menjawab: 0 = cepat (< 1,5 detik), 1 = bagus (1,5–3), 2 = cukup (3–5), 3 = lambat (> 5). */
    fun bucket(ms: Long): Int = when { ms < 1500 -> 0; ms < 3000 -> 1; ms < 5000 -> 2; else -> 3 }

    val bucketLabels = listOf("Cepat · < 1,5 Detik", "Bagus · 1,5–3 Detik", "Cukup · 3–5 Detik", "Lambat · > 5 Detik")
    val bucketScore = listOf(5, 4, 3, 2)
    val bucketRp = listOf(3, 2, 1, 0)
    const val WRONG_RP = -2
    const val QUIT_RP = -10

    /** Jumlah jawaban satu permainan Kilat, dikelompokkan menurut kecepatan. */
    data class KilatTally(val fast: Int = 0, val good: Int = 0, val ok: Int = 0, val slow: Int = 0, val wrong: Int = 0) {
        val correct: Int get() = fast + good + ok + slow
        val total: Int get() = correct + wrong
        fun add(correct: Boolean, ms: Long): KilatTally = if (!correct) copy(wrong = wrong + 1) else when (bucket(ms)) {
            0 -> copy(fast = fast + 1); 1 -> copy(good = good + 1); 2 -> copy(ok = ok + 1); else -> copy(slow = slow + 1)
        }
        fun count(b: Int): Int = when (b) { 0 -> fast; 1 -> good; 2 -> ok; else -> slow }
    }

    /** Skor Kilat sebelum pengali durasi. */
    fun kilatRawScore(t: KilatTally): Int = 5 * t.fast + 4 * t.good + 3 * t.ok + 2 * t.slow

    /** Skor Kilat (untuk rekor dan papan Kilat Mingguan). Sama dengan server. */
    fun kilatScore(t: KilatTally, minutes: Int): Int = roundHalfUp(kilatRawScore(t) * kilatMult(minutes)).toInt()

    /** RP plus sebelum pengali. */
    fun kilatRawRp(t: KilatTally): Int = 3 * t.fast + 2 * t.good + t.ok

    /** RP satu permainan sebelum bonus harian dan perlindungan. Sama dengan server. */
    fun kilatRp(t: KilatTally, minutes: Int, p: Int): Int =
        roundHalfUp(kilatRawRp(t) * kilatMult(minutes) * tierFactor(p)).toInt() + WRONG_RP * t.wrong
}
