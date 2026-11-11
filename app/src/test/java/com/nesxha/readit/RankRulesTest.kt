package com.nesxha.readit

import com.nesxha.readit.data.RankRules
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RankRulesTest {
    @Test fun posisi() {
        assertEquals("Iron III", RankRules.pos(0).label)
        assertEquals("Iron II", RankRules.pos(100).label)
        assertEquals("Iron I", RankRules.pos(250).label)
        assertEquals(50, RankRules.pos(250).rp)
        assertEquals("Bronze III", RankRules.pos(300).label)
        assertEquals("Gold II", RankRules.pos(1045).label)
        assertEquals("Phoenix I", RankRules.pos(2399).label)
        assertEquals("Dragon", RankRules.pos(2400).label)
        assertEquals(150, RankRules.pos(2550).rp)
    }

    @Test fun naikDanResetMusim() {
        assertTrue(RankRules.promoted(90, 110))
        assertFalse(RankRules.promoted(110, 190))
        assertTrue(RankRules.tierUp(290, 300))
        assertEquals(1200, RankRules.seasonReset(2600)) // Dragon → Platinum
        assertEquals(1200, RankRules.seasonReset(2000)) // Obsidian → Platinum
        assertEquals(900, RankRules.seasonReset(1600))  // Diamond → Gold
        assertEquals(900, RankRules.seasonReset(1250))  // Platinum → Gold
        assertEquals(600, RankRules.seasonReset(1000))  // Gold → Silver
        assertEquals(600, RankRules.seasonReset(850))   // Silver tetap, divisi III
        assertEquals(0, RankRules.seasonReset(250))     // Iron tetap
    }

    @Test fun kelompokWaktu() {
        assertEquals(0, RankRules.bucket(1499))
        assertEquals(1, RankRules.bucket(1500))
        assertEquals(2, RankRules.bucket(3000))
        assertEquals(3, RankRules.bucket(5000))
    }

    /** Angka yang sama dengan uji server (supabase/bagian-9.sql). */
    @Test fun skorDanRpKilat() {
        val t = RankRules.KilatTally(fast = 20, good = 5, wrong = 1)
        assertEquals(240, RankRules.kilatScore(t, 1))
        assertEquals(138, RankRules.kilatRp(t, 1, 0))                 // (60 + 10) × 2 − 2
        val asal = RankRules.KilatTally(fast = 5, slow = 10, wrong = 15)
        assertEquals(45, RankRules.kilatScore(asal, 3))
        assertEquals(-15, RankRules.kilatRp(asal, 3, 0))              // 15 − 30
        val campur = RankRules.KilatTally(4, 4, 4, 4, 2)
        assertEquals(84, RankRules.kilatScore(campur, 2))
        assertEquals(32, RankRules.kilatRp(campur, 2, 0))             // 24 × 1,5 − 4
        assertEquals(30, RankRules.kilatRp(RankRules.KilatTally(fast = 10), 1, 2500)) // Dragon ×0,5
        assertEquals(45, RankRules.kilatRp(RankRules.KilatTally(fast = 10), 1, 1600)) // Diamond ×0,75
    }

    @Test fun tambahJawaban() {
        val t = RankRules.KilatTally().add(true, 900).add(true, 2000).add(false, 100).add(true, 7000)
        assertEquals(RankRules.KilatTally(fast = 1, good = 1, slow = 1, wrong = 1), t)
        assertEquals(3, t.correct); assertEquals(4, t.total)
    }
}
