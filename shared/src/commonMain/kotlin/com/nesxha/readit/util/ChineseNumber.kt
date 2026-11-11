package com.nesxha.readit.util

/** Satu hanzi beserta pinyin-nya. */
data class Syl(val h: String, val p: String)

/** Ucapan Mandarin: rangkaian hanzi + pinyin per suku kata. */
class Spoken(val syls: List<Syl>) {
    val hanzi: String get() = syls.joinToString("") { it.h }
    val pinyin: String get() = syls.joinToString(" ") { it.p }
    operator fun plus(o: Spoken) = Spoken(syls + o.syls)
    override fun toString() = "$hanzi ($pinyin)"
    override fun equals(other: Any?) = other is Spoken && other.hanzi == hanzi
    override fun hashCode() = hanzi.hashCode()

    companion object {
        val EMPTY = Spoken(emptyList())
        fun of(vararg pairs: Pair<String, String>) = Spoken(pairs.map { Syl(it.first, it.second) })
    }
}

/**
 * Mengubah angka ke bacaan Mandarin dengan aturan yang benar:
 * 零 untuk digit kosong di tengah, 两 sebelum 千/万/亿 dan kata satuan, 十 (bukan 一十) di awal,
 * pengelompokan 万/亿 per 4 digit, dan perubahan nada 一 (yì/yí).
 */
object ChineseNumber {
    private val D = arrayOf("零", "一", "二", "三", "四", "五", "六", "七", "八", "九")
    private val DP = arrayOf("líng", "yī", "èr", "sān", "sì", "wǔ", "liù", "qī", "bā", "jiǔ")
    private val LIANG = Syl("两", "liǎng")
    private val ZERO = Syl("零", "líng")

    fun digit(d: Int) = Syl(D[d], DP[d])

    /** Bilangan bulat 0 sampai di bawah 10^12. */
    fun number(n: Long): Spoken {
        require(n >= 0 && n < 1_000_000_000_000L)
        if (n == 0L) return Spoken(listOf(ZERO))
        val out = ArrayList<Syl>()
        val parts = listOf(n / 100_000_000L to Syl("亿", "yì"), (n / 10_000L) % 10_000L to Syl("万", "wàn"), n % 10_000L to null)
        var started = false
        var gap = false
        for ((v, big) in parts) {
            val sec = v.toInt()
            if (sec == 0) { if (started) gap = true; continue }
            if (started && (gap || sec < 1000)) out += ZERO
            section(sec, leading = !started, followedByBig = big != null, out = out)
            if (big != null) {
                // 一万 → yíwàn, 一亿 → yíyì (hanya kalau bagiannya tepat 1; 十一万 tetap shíyī)
                if (sec == 1) out[out.size - 1] = Syl("一", "yí")
                out += big
            }
            started = true
            gap = false
        }
        return Spoken(out)
    }

    fun number(n: Int): Spoken = number(n.toLong())

    private fun section(v: Int, leading: Boolean, followedByBig: Boolean, out: MutableList<Syl>) {
        val digits = intArrayOf(v / 1000, (v / 100) % 10, (v / 10) % 10, v % 10)
        val units = arrayOf(Syl("千", "qiān"), Syl("百", "bǎi"), Syl("十", "shí"), null)
        var pendingZero = false
        var any = false
        for (i in 0..3) {
            val d = digits[i]
            if (d == 0) { if (any) pendingZero = true; continue }
            if (pendingZero) { out += ZERO; pendingZero = false }
            val unit = units[i]
            val first = !any
            when {
                // 10–19 di awal bilangan: 十, bukan 一十
                i == 2 && d == 1 && first && leading -> {}
                // 2 sebelum 千 → 两; sebelum 百 di awal bilangan → 两百
                d == 2 && i == 0 -> out += LIANG
                d == 2 && i == 1 && first && leading -> out += LIANG
                // 2 sendirian sebelum 万/亿 → 两万, 两亿
                d == 2 && i == 3 && first && followedByBig -> out += LIANG
                // 一 sebelum 百/千 → yì
                d == 1 && (i == 0 || i == 1) -> out += Syl("一", "yì")
                else -> out += digit(d)
            }
            if (unit != null) out += unit
            any = true
        }
    }

    /** Bilangan diikuti kata satuan: 1 → 一 dengan nada berubah, 2 → 两. [tone] = nada kata satuan. */
    fun count(n: Int, unitH: String, unitP: String, tone: Int): Spoken {
        val unit = Spoken.of(unitH to unitP)
        return when (n) {
            1 -> Spoken.of("一" to if (tone == 4 || tone == 0) "yí" else "yì") + unit
            2 -> Spoken(listOf(LIANG)) + unit
            else -> number(n) + unit
        }
    }

    /** Dibaca per digit (tahun, nomor kamar). [zero] = 〇 atau 零; [yao] = angka 1 dibaca yāo (幺). */
    fun digits(s: String, yao: Boolean = false, zero: String = "〇"): Spoken = Spoken(
        s.filter { it.isDigit() }.map { ch ->
            val d = ch - '0'
            when {
                d == 0 -> Syl(zero, "líng")
                d == 1 && yao -> Syl("幺", "yāo")
                else -> digit(d)
            }
        },
    )

    /** Desimal "3.14" → 三点一四. */
    fun decimal(intPart: Long, frac: String): Spoken {
        var sp = number(intPart)
        if (frac.isNotEmpty()) sp = sp + Spoken.of("点" to "diǎn") + Spoken(frac.map { digit(it - '0') })
        return sp
    }

    /** Persen: 百分之三十, 百分之零点五. */
    fun percent(intPart: Long, frac: String = ""): Spoken = Spoken.of("百" to "bǎi", "分" to "fēn", "之" to "zhī") + decimal(intPart, frac)

    /** Pecahan: 三分之一 (penyebut dulu). */
    fun fraction(num: Int, den: Int): Spoken = number(den) + Spoken.of("分" to "fēn", "之" to "zhī") + number(num)

    /** Harga lisan: 十二块五, 三块零五分, 十二块三毛五, 五毛. */
    fun moneySpoken(yuan: Int, jiao: Int, fen: Int): Spoken {
        var sp = Spoken.EMPTY
        if (yuan > 0) sp = sp + count(yuan, "块", "kuài", 4)
        when {
            yuan > 0 && jiao > 0 && fen == 0 -> sp = sp + number(jiao)
            jiao > 0 && fen > 0 -> sp = sp + count(jiao, "毛", "máo", 2) + number(fen)
            jiao > 0 -> sp = sp + count(jiao, "毛", "máo", 2)
            yuan > 0 && fen > 0 -> sp = sp + Spoken(listOf(ZERO)) + count(fen, "分", "fēn", 1)
            fen > 0 -> sp = sp + count(fen, "分", "fēn", 1)
        }
        if (yuan == 0 && jiao == 0 && fen == 0) sp = Spoken(listOf(ZERO)) + Spoken.of("块" to "kuài")
        return sp
    }

    /** Harga tertulis: 十二元五角, 三元零五分. */
    fun moneyWritten(yuan: Int, jiao: Int, fen: Int): Spoken {
        var sp = if (yuan > 0) number(yuan) + Spoken.of("元" to "yuán") else Spoken.EMPTY
        if (jiao > 0) sp = sp + number(jiao) + Spoken.of("角" to "jiǎo")
        if (fen > 0) {
            if (yuan > 0 && jiao == 0) sp = sp + Spoken(listOf(ZERO))
            sp = sp + number(fen) + Spoken.of("分" to "fēn")
        }
        return sp
    }

    /** Jam: 两点, 八点半, 九点一刻, 九点三刻, 八点零五分, 差五分九点. [style]: 0 menit, 1 刻/半, 2 差. */
    fun time(h: Int, m: Int, style: Int = 0): Spoken {
        fun hour(x: Int) = (if (x == 2) Spoken(listOf(LIANG)) else number(x)) + Spoken.of("点" to "diǎn")
        return when {
            m == 0 -> hour(h)
            style == 1 && m == 30 -> hour(h) + Spoken.of("半" to "bàn")
            style == 1 && m == 15 -> hour(h) + Spoken.of("一" to "yí", "刻" to "kè")
            style == 1 && m == 45 -> hour(h) + Spoken.of("三" to "sān", "刻" to "kè")
            style == 2 && m == 45 -> Spoken.of("差" to "chà", "一" to "yí", "刻" to "kè") + hour(h % 12 + 1)
            style == 2 && m >= 50 -> Spoken.of("差" to "chà") + number(60 - m) + Spoken.of("分" to "fēn") + hour(h % 12 + 1)
            m < 10 -> hour(h) + Spoken(listOf(ZERO)) + number(m) + Spoken.of("分" to "fēn")
            else -> hour(h) + number(m) + Spoken.of("分" to "fēn")
        }
    }

    private val WEEK = arrayOf("一", "二", "三", "四", "五", "六")

    /** 1 = Senin … 7 = Minggu. */
    fun weekday(d: Int, sunday: String = "天"): Spoken {
        val base = Spoken.of("星" to "xīng", "期" to "qī")
        return if (d == 7) base + Spoken.of(sunday to if (sunday == "天") "tiān" else "rì")
        else base + Spoken(listOf(digit(d)))
    }

    fun date(y: Int, mo: Int, d: Int, hao: Boolean = true): Spoken =
        digits(y.toString()) + Spoken.of("年" to "nián") + number(mo) + Spoken.of("月" to "yuè") +
            number(d) + Spoken.of((if (hao) "号" else "日") to if (hao) "hào" else "rì")

    /** Penjelasan langkah demi langkah cara membaca bilangan bulat [n]. */
    fun steps(n: Long): List<String> {
        if (n < 0 || n >= 1_000_000_000_000L) return emptyList()
        if (n == 0L) return listOf("0 dibaca 零 (líng).")
        val out = ArrayList<String>()
        val a = n / 100_000_000L
        val b = (n / 10_000L) % 10_000L
        val c = n % 10_000L
        val groups = buildList {
            if (a > 0) add(a.toString())
            if (a > 0 || b > 0) add(if (a > 0) b.toString().padStart(4, '0') else b.toString())
            add(if (a > 0 || b > 0) c.toString().padStart(4, '0') else c.toString())
        }
        if (n >= 10_000) {
            out += "Pisahkan per 4 digit dari kanan: ${groups.joinToString(" | ")}."
            val names = when (groups.size) { 3 -> listOf("亿", "万", "sisa"); else -> listOf("万", "sisa") }
            out += "Setiap kelompok punya nama: " + groups.zip(names).joinToString(", ") { (g, nm) -> if (nm == "sisa") "$g (tanpa nama)" else "$g → $nm" } + "."
        }
        fun part(v: Long, label: String, leading: Boolean) {
            if (v == 0L) { out += "Kelompok $label = 0000, dilewati (tidak dibaca)."; return }
            val sp = number(v)
            val sec = v.toInt()
            val digits = v.toString().padStart(4, '0')
            val notes = ArrayList<String>()
            if (!leading && sec < 1000) notes += "diawali nol, jadi didahului 零"
            if (digits.trimStart('0').trimEnd('0').contains('0')) notes += "ada nol di tengah, ditulis 零 sekali"
            if (sec % 10 == 0 && sec >= 10) notes += "nol di akhir tidak dibaca"
            if (leading && sec in 10..19) notes += "10–19 di awal cukup 十, tanpa 一"
            if (sp.hanzi.contains("两")) notes += "angka 2 di sini dibaca 两"
            out += "Baca $label ${fmt(v)}: ${sp.hanzi}" + (if (notes.isNotEmpty()) " (${notes.joinToString("; ")})" else "") + "."
        }
        if (a > 0) { part(a, "kelompok 亿", true); out += "Tambahkan 亿: ${number(a).hanzi}亿." }
        if (b > 0 || (a > 0 && b == 0L)) {
            if (b > 0) { part(b, "kelompok 万", a == 0L); out += "Tambahkan 万: ${number(b).hanzi}万." }
            else if (c > 0) out += "Kelompok 万 = 0000, dilewati; nanti cukup satu 零 sebelum kelompok terakhir."
        }
        if (c > 0) part(c, if (n >= 10_000) "kelompok terakhir" else "angka", n < 10_000)
        else if (n >= 10_000) out += "Kelompok terakhir = 0000, tidak dibaca."
        out += "Gabungkan: ${number(n).hanzi} (${number(n).pinyin})."
        return out
    }

    /** Format angka gaya Indonesia: 30.508 dan 1,5. */
    fun fmt(n: Long): String {
        // Pemisah ribuan titik (gaya Indonesia), tanpa java.util supaya bisa dipakai juga di versi web.
        val neg = n < 0
        val d = (if (neg) -n else n).toString()
        val sb = StringBuilder()
        for (i in d.indices) {
            if (i > 0 && (d.length - i) % 3 == 0) sb.append('.')
            sb.append(d[i])
        }
        return (if (neg) "-" else "") + sb.toString()
    }

    fun fmtDec(v: Double, maxFrac: Int = 2): String {
        var mul = 1L
        repeat(maxFrac) { mul *= 10 }
        val scaled = kotlin.math.floor(kotlin.math.abs(v) * mul + 0.5).toLong()
        val whole = scaled / mul
        val frac = (scaled % mul).toString().padStart(maxFrac, '0')
        val s0 = (if (v < 0 && scaled != 0L) "-" else "") + whole + (if (maxFrac > 0) ".$frac" else "")
        val s = s0.let { if ('.' in it) it.trimEnd('0').trimEnd('.') else it }
        val (a, b) = if ('.' in s) s.substringBefore('.') to s.substringAfter('.') else s to ""
        return fmt(a.toLong()) + if (b.isNotEmpty()) ",$b" else ""
    }
}
