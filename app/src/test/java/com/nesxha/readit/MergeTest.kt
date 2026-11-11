package com.nesxha.readit

import com.nesxha.readit.data.AppState
import com.nesxha.readit.data.Card
import com.nesxha.readit.data.Counter
import com.nesxha.readit.data.DayLog
import com.nesxha.readit.data.Merge
import com.nesxha.readit.data.Settings
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** Penggabungan progres HP + web untuk satu akun. */
class MergeTest {
    private val hp = AppState(
        cards = mapOf(1 to Card(reps = 2, interval = 3, due = 105, learnedOn = 100, t = 1000), 2 to Card(reps = 1, learnedOn = 101, t = 500)),
        days = mapOf(100L to DayLog(reviews = 10, newWords = 5, opened = true, xp = 50), 101L to DayLog(reviews = 3, xp = 9)),
        settings = Settings(darkMode = 2),
        badges = mapOf("a" to 100L),
        counters = mapOf(Counter.XP to 59, Counter.BEST_KILAT_SCORE to 40, Counter.WRITE_OK to 3),
    )
    private val web = AppState(
        cards = mapOf(2 to Card(reps = 4, interval = 9, learnedOn = 101, t = 900), 3 to Card(reps = 1, learnedOn = 102, t = 950)),
        days = mapOf(101L to DayLog(reviews = 7, opened = true, xp = 20), 102L to DayLog(quiz = 4, opened = true, xp = 8)),
        settings = Settings(darkMode = 1),
        badges = mapOf("a" to 99L, "b" to 102L),
        counters = mapOf(Counter.XP to 28, Counter.BEST_KILAT_SCORE to 55, Counter.WRITE_OK to 2),
    )

    @Test fun gabunganMenjumlahAktivitas() {
        val m = Merge.combine(hp, web)
        assertEquals(10, m.days[100L]!!.reviews)
        assertEquals(10, m.days[101L]!!.reviews)           // 3 di HP + 7 di web
        assertTrue(m.days[101L]!!.opened)                    // hadir di salah satu perangkat
        assertEquals(29, m.days[101L]!!.xp)
        assertEquals(87, m.counter(Counter.XP))              // XP dijumlah
        assertEquals(5, m.counter(Counter.WRITE_OK))
        assertEquals(55, m.counter(Counter.BEST_KILAT_SCORE)) // rekor = terbesar
        assertEquals(setOf(1, 2, 3), m.cards.keys)
        assertEquals(4, m.cards[2]!!.reps)                   // kartu yang paling baru diubah
        assertEquals(99L, m.badges["a"])                     // tanggal lencana paling awal
        assertEquals(2, m.settings.darkMode)                 // pengaturan milik perangkat ini
    }

    @Test fun sisiLainMelihatHasilSama() {
        val a = Merge.combine(hp, web)
        val b = Merge.combine(web, hp)
        assertEquals(a.copy(settings = Settings()), b.copy(settings = Settings()))
    }

    @Test fun perubahanMasukKeBagianSendiriTanpaDobel() {
        val shown = Merge.combine(hp, web)
        // Belajar 2 kartu di HP hari 101 (+6 XP), kartu 3 diulang.
        val next = shown.copy(
            days = shown.days + (101L to shown.days[101L]!!.let { it.copy(reviews = it.reviews + 2, xp = it.xp + 6) }),
            cards = shown.cards + (3 to shown.cards[3]!!.copy(reps = 2)),
            counters = shown.counters + (Counter.XP to shown.counter(Counter.XP) + 6) + (Counter.BEST_KILAT_SCORE to 70),
        )
        val own = Merge.applyChange(hp, shown, next, stamp = 2000)
        assertEquals(5, own.days[101L]!!.reviews)            // 3 + 2, bukan 12
        assertEquals(65, own.counter(Counter.XP))            // 59 + 6
        assertEquals(70, own.counter(Counter.BEST_KILAT_SCORE))
        assertEquals(2000, own.cards[3]!!.t)
        val again = Merge.combine(own, web)
        assertEquals(next.days, again.days)
        assertEquals(next.counter(Counter.XP), again.counter(Counter.XP))
        assertEquals(2, again.cards[3]!!.reps)
    }

    @Test fun pulihkanCadanganTidakDobel() {
        val shown = Merge.combine(hp, web)
        val backup = shown // cadangan berisi tampilan gabungan
        val next = Merge.union(shown, backup)
        assertEquals(shown.days, next.days)
        assertEquals(shown.counter(Counter.XP), next.counter(Counter.XP))
        val own = Merge.applyChange(hp, shown, next, 3000)
        assertEquals(hp.days, own.days)
    }

    @Test fun jsonMenyimpanWaktuKartu() {
        val c = Card(reps = 3, t = 123456789L)
        assertEquals(c, Card.fromJson(c.toJson()))
        assertEquals(0L, Card.fromJson(Card(reps = 1).toJson()).t)
    }
}
