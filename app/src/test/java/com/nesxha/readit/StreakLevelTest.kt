package com.nesxha.readit

import com.nesxha.readit.data.AppState
import com.nesxha.readit.data.DayLog
import com.nesxha.readit.data.FlameTiers
import com.nesxha.readit.data.Levels
import com.nesxha.readit.data.today
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class StreakLevelTest {
    private val t = today()
    private fun state(attended: List<Long>, restored: Set<Long> = emptySet()) =
        AppState(days = attended.associateWith { DayLog(opened = true) }, restored = restored)

    @Test fun streakBiasa() {
        assertEquals(5, state((t - 4..t).toList()).streak)
    }

    @Test fun bolosSehariBisaDipulihkan() {
        // Hadir 5 hari, bolos kemarin, hari ini buka.
        val st = state((t - 6..t - 2).toList() + t)
        assertEquals(5, st.brokenStreak)
        assertFalse(st.streakLit)
        assertTrue(st.canRestore)
        val restored = state((t - 6..t - 2).toList() + t, setOf(t - 1))
        assertEquals(6, restored.streak)
        assertTrue(restored.streakLit)
    }

    @Test fun tidakDipulihkanLaluLenyap() {
        // Bolos 2 hari lalu, kemarin dan hari ini hadir tanpa pemulihan: streak lama hilang.
        val st = state((t - 7..t - 3).toList() + listOf(t - 1, t))
        assertEquals(0, st.brokenStreak)
        assertEquals(2, st.streak)
    }

    @Test fun bolosDuaHariTidakBisaDipulihkan() {
        val st = state((t - 7..t - 3).toList() + t)
        assertEquals(0, st.brokenStreak)
        assertEquals(1, st.streak)
    }

    @Test fun tingkatApi() {
        assertEquals(0, FlameTiers.of(1).level); assertEquals(1, FlameTiers.of(11).level)
        assertEquals(4, FlameTiers.of(100).level); assertEquals(8, FlameTiers.of(999).level)
        assertTrue(FlameTiers.isMilestone(26)); assertTrue(FlameTiers.isMilestone(400)); assertFalse(FlameTiers.isMilestone(350))
    }

    @Test fun level() {
        assertEquals(1, Levels.info(0).level); assertEquals(2, Levels.info(75).level)
        assertEquals(10, Levels.info(1215).level); assertEquals(100, Levels.info(80190).level)
    }
}
