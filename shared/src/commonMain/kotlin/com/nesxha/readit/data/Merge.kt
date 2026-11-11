package com.nesxha.readit.data

import kotlin.math.max
import kotlin.math.min

/**
 * Penggabungan progres antarperangkat (HP + web) untuk satu akun.
 *
 * Tiap perangkat menyimpan "bagiannya sendiri" (aktivitas yang terjadi di perangkat itu).
 * Yang tampil di layar = [combine] bagian sendiri + bagian perangkat lain:
 * - Aktivitas harian, XP, dan penghitung jumlah: dijumlah.
 * - Kartu kata: dipakai yang paling baru diubah.
 * - Rekor, penanda hari, dan "sudah dibaca": diambil yang terbesar.
 * - Lencana dan hari yang dipulihkan: digabung (tanggal lencana yang paling awal).
 * - Pengaturan: milik perangkat ini.
 */
object Merge {
    /** Penghitung yang dijumlah antarperangkat; yang lain diambil nilai terbesarnya. */
    fun additive(key: String): Boolean = key == Counter.XP || key == Counter.WRITE_OK || key == Counter.SENTENCE_OK ||
        key == Counter.EXAM_PASS || key == Counter.EXAM_PERFECT || key == Counter.RESTORES || key == Counter.NUM_OK ||
        key.startsWith("numOk_") || key.startsWith("numTotal_")

    /** Kartu yang lebih baru: waktu ubah, lalu jumlah ulangan, lalu jadwal. */
    fun newer(a: Card, b: Card): Card = when {
        a.t != b.t -> if (a.t > b.t) a else b
        a.reps != b.reps -> if (a.reps > b.reps) a else b
        a.due != b.due -> if (a.due > b.due) a else b
        else -> if (a.right + a.wrong >= b.right + b.wrong) a else b
    }

    private fun plus(a: DayLog, b: DayLog) = DayLog(
        a.reviews + b.reviews, a.newWords + b.newWords, a.correct + b.correct, a.wrong + b.wrong,
        a.quiz + b.quiz, a.opened || b.opened, a.xp + b.xp,
    )

    private fun maxOf(a: DayLog, b: DayLog) = DayLog(
        max(a.reviews, b.reviews), max(a.newWords, b.newWords), max(a.correct, b.correct), max(a.wrong, b.wrong),
        max(a.quiz, b.quiz), a.opened || b.opened, max(a.xp, b.xp),
    )

    private fun <K> mergeMaps(a: Map<K, DayLog>, b: Map<K, DayLog>, f: (DayLog, DayLog) -> DayLog): Map<K, DayLog> {
        if (b.isEmpty()) return a
        val out = HashMap(a)
        for ((k, v) in b) out[k] = out[k]?.let { f(it, v) } ?: v
        return out
    }

    private fun mergeCards(a: Map<Int, Card>, b: Map<Int, Card>): Map<Int, Card> {
        if (b.isEmpty()) return a
        val out = HashMap(a)
        for ((k, v) in b) out[k] = out[k]?.let { newer(it, v) } ?: v
        return out
    }

    private fun mergeBadges(a: Map<String, Long>, b: Map<String, Long>): Map<String, Long> {
        if (b.isEmpty()) return a
        val out = HashMap(a)
        for ((k, v) in b) out[k] = out[k]?.let { min(it, v) } ?: v
        return out
    }

    /** Yang tampil: bagian perangkat ini + bagian perangkat lain. */
    fun combine(own: AppState, other: AppState): AppState {
        val ct = HashMap(own.counters)
        for ((k, v) in other.counters) {
            val o = ct[k]
            ct[k] = if (o == null) v else if (additive(k)) o + v else max(o, v)
        }
        return AppState(
            cards = mergeCards(own.cards, other.cards),
            days = mergeMaps(own.days, other.days, ::plus),
            settings = own.settings,
            restored = own.restored + other.restored,
            badges = mergeBadges(own.badges, other.badges),
            counters = ct,
        )
    }

    /**
     * Gabungan "terbesar" (bukan jumlah) — dipakai untuk memulihkan cadangan dan mengambil alih data
     * perangkat lama, supaya data yang sama tidak terhitung dua kali.
     */
    fun union(a: AppState, b: AppState): AppState {
        val ct = HashMap(a.counters)
        for ((k, v) in b.counters) ct[k] = max(ct[k] ?: v, v)
        return AppState(
            cards = mergeCards(a.cards, b.cards),
            days = mergeMaps(a.days, b.days, ::maxOf),
            settings = a.settings,
            restored = a.restored + b.restored,
            badges = mergeBadges(a.badges, b.badges),
            counters = ct,
        )
    }

    /**
     * Perubahan dari [prev] ke [next] (keduanya tampilan gabungan) diterapkan ke bagian perangkat ini.
     * Kartu yang berubah diberi waktu [stamp] supaya menang saat digabung.
     */
    fun applyChange(own: AppState, prev: AppState, next: AppState, stamp: Long): AppState {
        // Kartu
        var cards = own.cards
        if (next.cards !== prev.cards) {
            val m = HashMap(own.cards)
            for ((k, v) in next.cards) if (prev.cards[k] != v) m[k] = v.copy(t = stamp)
            cards = m
        }
        // Hari: tambahkan selisihnya.
        var days = own.days
        if (next.days !== prev.days) {
            val m = HashMap(own.days)
            for ((d, n) in next.days) {
                val p = prev.days[d] ?: DayLog()
                if (p == n) continue
                val o = m[d] ?: DayLog()
                m[d] = DayLog(
                    o.reviews + (n.reviews - p.reviews), o.newWords + (n.newWords - p.newWords),
                    o.correct + (n.correct - p.correct), o.wrong + (n.wrong - p.wrong), o.quiz + (n.quiz - p.quiz),
                    o.opened || (n.opened && !p.opened), o.xp + (n.xp - p.xp),
                )
            }
            days = m
        }
        // Penghitung
        var counters = own.counters
        if (next.counters !== prev.counters) {
            val m = HashMap(own.counters)
            for ((k, n) in next.counters) {
                val p = prev.counters[k] ?: 0
                if (p == n && k in prev.counters) continue
                m[k] = if (additive(k)) (m[k] ?: 0) + (n - p) else n
            }
            counters = m
        }
        val newBadges = next.badges.filterKeys { it !in prev.badges }
        return AppState(
            cards = cards,
            days = days,
            settings = next.settings,
            restored = own.restored + (next.restored - prev.restored),
            badges = if (newBadges.isEmpty()) own.badges else own.badges + newBadges,
            counters = counters,
        )
    }
}
