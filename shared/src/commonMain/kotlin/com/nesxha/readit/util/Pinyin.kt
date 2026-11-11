package com.nesxha.readit.util

/** Potongan pinyin beserta nadanya. tone: 1..4, 0 = netral, -1 = tanda baca/spasi. */
data class PySeg(val text: String, val tone: Int)

object Pinyin {
    private const val VOWELS = "aeiouüv"
    private const val INITIALS = "bpmfdtnlgkhjqxrzcsyw"

    // Huruf bernada → (huruf dasar, nada). Tanpa java.text.Normalizer supaya bisa dipakai juga di versi web.
    private val TONED: Map<Char, Pair<Char, Int>> = buildMap {
        val rows = listOf("aāáǎà", "eēéěè", "iīíǐì", "oōóǒò", "uūúǔù", "üǖǘǚǜ", "AĀÁǍÀ", "EĒÉĚÈ", "IĪÍǏÌ", "OŌÓǑÒ", "UŪÚǓÙ", "ÜǕǗǙǛ")
        for (r in rows) {
            val b = r[0].lowercaseChar()
            for (i in 1 until r.length) put(r[i], b to i)
        }
        put('Ü', 'ü' to 0)
        put('ń', 'n' to 2); put('ň', 'n' to 3); put('ǹ', 'n' to 4); put('ḿ', 'm' to 2)
    }

    private fun base(c: Char): Char = TONED[c]?.first ?: c.lowercaseChar()

    fun toneOf(c: Char): Int = TONED[c]?.second ?: 0

    private fun isVowel(c: Char) = c.isLetter() && base(c) in VOWELS

    /** Pinyin tanpa tanda nada, huruf kecil, ü → u. */
    fun plain(py: String): String = buildString {
        for (c in py) {
            if (c.isLetter()) {
                val b = base(c)
                append(if (b == 'ü') 'u' else b)
            } else if (c == ' ') append(' ')
        }
    }

    fun toneOfSyllable(s: String): Int = s.map { toneOf(it) }.firstOrNull { it > 0 } ?: 0

    /** Pecah pinyin jadi suku kata (tanda baca menjadi potongan sendiri). */
    fun split(py: String): List<String> {
        val out = ArrayList<String>()
        val cur = StringBuilder()
        var hasV = false
        var i = 0
        while (i < py.length) {
            val c = py[i]
            if (!c.isLetter()) {
                if (cur.isNotEmpty()) out += cur.toString()
                out += c.toString()
                cur.clear(); hasV = false; i++; continue
            }
            val b = base(c)
            if (hasV && b in INITIALS) {
                val len = if (b in "zcs" && i + 1 < py.length && base(py[i + 1]) == 'h') 2 else 1
                val nxt = py.getOrNull(i + len)
                if (nxt != null && isVowel(nxt)) {
                    out += cur.toString(); cur.clear(); hasV = false
                }
            }
            cur.append(c)
            if (isVowel(c)) hasV = true
            i++
        }
        if (cur.isNotEmpty()) out += cur.toString()
        return out
    }

    private fun hanziCount(h: String): Int = h.count { it in '一'..'鿿' || it == '〇' }

    /** Potongan berwarna untuk ditampilkan. */
    /** Jumlah suku kata pinyin cocok dengan jumlah hanzi (儿 boleh melebur, misalnya 哪儿 = nǎr). */
    fun aligned(py: String, hanzi: String): Boolean {
        val syl = split(py).count { p -> p.any { it.isLetter() } }
        val n = hanziCount(hanzi)
        val er = hanzi.count { it == '儿' }
        return n == 0 || syl == n || (er > 0 && syl == n - er)
    }

    fun segments(py: String, hanzi: String): List<PySeg> {
        val parts = split(py)
        if (!aligned(py, hanzi)) {
            val t = py.map { toneOf(it) }.firstOrNull { it > 0 } ?: 0
            return listOf(PySeg(py, t))
        }
        return parts.map { p -> if (p.any { it.isLetter() }) PySeg(p, toneOfSyllable(p)) else PySeg(p, -1) }
    }

    /** Ganti nada satu suku kata (untuk pilihan pengecoh kuis). */
    fun withTone(syllable: String, tone: Int): String {
        val plain = buildString { for (c in syllable) append(base(c)) }
        if (tone == 0) return plain
        val idx = run {
            val a = plain.indexOf('a'); if (a >= 0) return@run a
            val e = plain.indexOf('e'); if (e >= 0) return@run e
            val ou = plain.indexOf("ou"); if (ou >= 0) return@run ou
            plain.indexOfLast { it in VOWELS }
        }
        if (idx < 0) return plain
        val ch = plain[idx]
        val marked = (TONED.entries.firstOrNull { it.value.first == ch && it.value.second == tone.coerceIn(1, 4) && it.key.isLowerCase() }?.key ?: ch).toString()
        val res = plain.substring(0, idx) + marked + plain.substring(idx + 1)
        return if (syllable.firstOrNull()?.isUpperCase() == true) res.replaceFirstChar { it.uppercaseChar() } else res
    }
}
