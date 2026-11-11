package com.nesxha.readit.util

import com.nesxha.readit.data.Words

/** Memecah contoh kalimat menjadi potongan untuk latihan susun kalimat. */
object Sentence {
    private const val END = "。！？!?"
    private const val ATTACH = "，、：；“”‘’,"

    private val dict: Set<String> by lazy {
        Words.all.map { it.hanzi }.filter { h -> h.isNotEmpty() && h.all { it in '一'..'鿿' } }.toSet()
    }

    /** Tanda baca akhir kalimat (dipertahankan di ujung saat ditampilkan). */
    fun ending(s: String): String = s.trim().takeLastWhile { it in END }

    /** Potongan kalimat, 3–6 buah; daftar kosong kalau kalimat tidak cocok. */
    fun chunks(sentence: String): List<String> {
        var s = sentence.trim()
        while (s.isNotEmpty() && s.last() in END) s = s.dropLast(1)
        if (s.isEmpty()) return emptyList()
        val out = ArrayList<String>()
        var i = 0
        while (i < s.length) {
            val c = s[i]
            if (c in ATTACH) {
                if (out.isNotEmpty()) out[out.size - 1] = out.last() + c else out += c.toString()
                i++; continue
            }
            var len = 1
            for (l in 4 downTo 2) {
                if (i + l <= s.length && s.substring(i, i + l) in dict) { len = l; break }
            }
            out += s.substring(i, i + len)
            i += len
        }
        // Gabungkan potongan sampai paling banyak 6; utamakan gabungan yang membentuk kata.
        while (out.size > 6) {
            var best = 0
            var bestScore = Int.MAX_VALUE
            for (k in 0 until out.size - 1) {
                val joined = out[k] + out[k + 1]
                val score = joined.length - if (joined in dict) 10 else 0
                if (score < bestScore) { bestScore = score; best = k }
            }
            out[best] = out[best] + out[best + 1]
            out.removeAt(best + 1)
        }
        return if (out.size in 3..6) out else emptyList()
    }
}
