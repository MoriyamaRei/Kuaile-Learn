package com.nesxha.readit

import com.nesxha.readit.data.AppState
import com.nesxha.readit.data.NumCat
import com.nesxha.readit.data.NumberLessons
import com.nesxha.readit.data.NumberQuiz
import com.nesxha.readit.data.PracticeMode
import com.nesxha.readit.data.WordQuiz
import com.nesxha.readit.data.WordSource
import com.nesxha.readit.data.Words
import com.nesxha.readit.util.Pinyin
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.BeforeClass
import org.junit.Test

class QuizGeneratorTest {
    companion object {
        @BeforeClass @JvmStatic fun setup() = TestData.load()
    }

    private fun aligned(py: String, hz: String) = Pinyin.aligned(py, hz)

    @Test fun pinyinSemuaKataSelaras() {
        val bad = Words.all.filter { !aligned(it.pinyin, it.hanzi) }.map { it.hanzi }
        assertTrue("Pinyin tidak selaras: $bad", bad.isEmpty())
    }

    @Test fun soalAngkaValid() {
        for (cat in NumCat.entries) for (d in 0..2) {
            val qs = NumberQuiz.build(cat, d, 100)
            assertEquals("$cat d$d", 100, qs.size)
            for (q in qs) {
                if (q.isChoice) {
                    assertEquals(4, q.options.map { it.text }.toSet().size)
                    assertEquals(q.answerText, q.options[q.answer].text)
                } else {
                    val typed = q.answerText.filter { it.isDigit() || it in ",:/%" }
                    assertTrue("$cat: ${q.answerText}", q.check(typed))
                }
            }
        }
    }

    @Test fun jarakAngkaDihormati() {
        for ((_, r) in NumberQuiz.ranges) {
            for (q in NumberQuiz.build(NumCat.UMUM, 0, 50, r)) {
                val n = (if (q.isChoice) q.display!! else q.accepted.first()).filter { it.isDigit() }.toLong()
                assertTrue("$n di luar $r", n in r)
            }
        }
    }

    @Test fun materiSelaras() {
        for (cat in NumCat.entries) for (r in NumberLessons.full(cat)) for ((sp, _) in r.examples + r.table) {
            assertTrue("${sp.hanzi} | ${sp.pinyin}", aligned(sp.pinyin, sp.hanzi))
        }
    }

    @Test fun soalKataValid() {
        val st = AppState()
        for (mode in PracticeMode.entries.filter { it.usesWords && it != PracticeMode.TULIS }) {
            val qs = WordQuiz.build(mode, st, 0, WordSource.SEMUA, 30)
            assertTrue("$mode kosong", qs.size >= 10)
            for (q in qs) if (q.options.size == 4) assertEquals(4, q.options.toSet().size)
        }
    }

    @Test fun gabunganMencakupSemuaLevel() {
        val levels = WordQuiz.pool(AppState(), 0, WordSource.SEMUA, 40).map { it.level }.toSet()
        assertEquals(setOf(1, 2, 3, 4), levels)
    }
}
