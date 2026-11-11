package com.nesxha.readit

import com.nesxha.readit.util.ChineseNumber as C
import org.junit.Assert.assertEquals
import org.junit.Test

class ChineseNumberTest {
    private fun h(n: Long) = C.number(n).hanzi

    @Test fun angkaDasar() {
        assertEquals("零", h(0)); assertEquals("十", h(10)); assertEquals("十五", h(15)); assertEquals("二十二", h(22))
    }

    @Test fun aturanLing() {
        assertEquals("一百零五", h(105)); assertEquals("一千零五", h(1005)); assertEquals("一千零五十", h(1050))
        assertEquals("三万零五百零八", h(30508)); assertEquals("十万零五", h(100005)); assertEquals("一亿零五", h(100000005))
    }

    @Test fun liangDanYiShi() {
        assertEquals("两百", h(200)); assertEquals("两千", h(2000)); assertEquals("两万", h(20000)); assertEquals("两万两千", h(22000))
        assertEquals("一百一十", h(110)); assertEquals("十一万", h(110000)); assertEquals("一百万零一十", h(1000010))
    }

    @Test fun nadaYi() {
        assertEquals("yì bǎi", C.number(100).pinyin); assertEquals("yí wàn", C.number(10000).pinyin)
        assertEquals("shí yī wàn", C.number(110000).pinyin); assertEquals("yí kuài", C.moneySpoken(1, 0, 0).pinyin)
    }

    @Test fun uangJamTanggal() {
        assertEquals("十二块五", C.moneySpoken(12, 5, 0).hanzi); assertEquals("三块零五分", C.moneySpoken(3, 0, 5).hanzi)
        assertEquals("两块", C.moneySpoken(2, 0, 0).hanzi); assertEquals("十二元五角", C.moneyWritten(12, 5, 0).hanzi)
        assertEquals("两点", C.time(2, 0).hanzi); assertEquals("差一刻十点", C.time(9, 45, 2).hanzi); assertEquals("八点零五分", C.time(8, 5).hanzi)
        assertEquals("二〇二六年十一月四号", C.date(2026, 11, 4).hanzi)
        assertEquals("三分之一", C.fraction(1, 3).hanzi); assertEquals("百分之三十", C.percent(30).hanzi)
    }

    @Test fun langkahMembaca() {
        val steps = C.steps(30508)
        assertEquals("Gabungkan: 三万零五百零八 (sān wàn líng wǔ bǎi líng bā).", steps.last())
    }
}
