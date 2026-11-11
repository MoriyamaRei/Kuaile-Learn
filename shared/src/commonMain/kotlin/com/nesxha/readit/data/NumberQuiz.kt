package com.nesxha.readit.data

import com.nesxha.readit.util.ChineseNumber as C
import com.nesxha.readit.util.Spoken
import kotlin.random.Random

enum class NumCat(val title: String, val sub: String, val emoji: String) {
    UMUM("Angka Umum", "0 sampai 100 juta", "🔢"),
    HARGA("Harga", "块, 毛, 分 dan 元, 角", "💴"),
    DISKON("Diskon", "打八折, 半价, 满减", "🏷️"),
    PERSEN("Persen & Pecahan", "百分之, 分之, 点", "➗"),
    JAM("Jam", "点, 刻, 半, 差", "⏰"),
    TANGGAL("Tanggal & Hari", "年, 月, 号, 星期", "📅"),
    TELEPON("Telepon & Kamar", "幺 dan dibaca per digit", "📞"),
    UKURAN("Ukuran Sehari-hari", "岁, 米, 斤, 楼, 个", "📏"),
    KIRA("Kira-kira", "多, 几, 二三十", "🤔"),
    CAMPUR("Campuran", "Semua kategori diacak", "🎲"),
}

/** Papan angka yang ditampilkan untuk soal isian. */
enum class Keys(val extra: String) { NUMBER(""), DECIMAL(","), TIME(":"), DATE("/"), FRACTION("/"), PERCENT(",") }

data class NumOption(val text: String, val pinyin: String? = null, val chinese: Boolean = false)

data class NumQuestion(
    val cat: NumCat,
    val instruction: String,
    val hanzi: String? = null,
    val pinyin: String? = null,
    val display: String? = null,
    val options: List<NumOption> = emptyList(),
    val answer: Int = -1,
    val accepted: Set<String> = emptySet(),
    val answerText: String = "",
    val keys: Keys = Keys.NUMBER,
    val unit: String? = null,
    val explain: String = "",
    val story: Boolean = false,
) {
    val isChoice: Boolean get() = options.isNotEmpty()
    fun check(input: String): Boolean = NumberQuiz.norm(input) in accepted
}

object NumberQuiz {
    private val R = Random.Default

    // ---------- Normalisasi jawaban isian ----------
    fun norm(raw: String): String {
        val s = raw.trim().replace(" ", "").replace("¥", "").replace("%", "").replace("yuan", "")
        return runCatching {
            when {
                ':' in s -> s.split(':').let { (h, m) -> "${h.toInt()}:${m.toInt().toString().padStart(2, '0')}" }
                '/' in s -> s.split('/').joinToString("/") { it.toInt().toString() }
                else -> {
                    val t = s.replace(".", "")
                    if (',' in t) {
                        val a = t.substringBefore(',').ifEmpty { "0" }.toLong()
                        val b = t.substringAfter(',').trimEnd('0')
                        if (b.isEmpty()) "$a" else "$a,$b"
                    } else t.toLong().toString()
                }
            }
        }.getOrDefault(s)
    }

    private fun dec(v: Double): String = norm(C.fmtDec(v).replace(".", ""))

    // ---------- Pembuat soal ----------
    /** Pilihan jarak angka untuk kategori Angka Umum. */
    val ranges: List<Pair<String, LongRange>> = listOf(
        "0 – 10" to 0L..10L,
        "11 – 100" to 11L..100L,
        "101 – 1.000" to 101L..1_000L,
        "1.001 – 10.000" to 1_001L..10_000L,
        "10.001 – 100.000" to 10_001L..100_000L,
        "100.001 – 1 juta" to 100_001L..1_000_000L,
        "1 juta – 100 juta" to 1_000_001L..100_000_000L,
        "100 juta – 1 triliun" to 100_000_001L..999_999_999_999L,
    )

    const val MAX_NUMBER = 999_999_999_999L

    fun build(cat: NumCat, difficulty: Int, n: Int, range: LongRange? = null): List<NumQuestion> {
        val out = ArrayList<NumQuestion>()
        var guard = 0
        while (out.size < n && guard++ < n * 20) {
            val c = if (cat == NumCat.CAMPUR) NumCat.entries.filter { it != NumCat.CAMPUR }.random(R) else cat
            val q = runCatching { if (c == NumCat.UMUM && range != null) umum(difficulty, range) else make(c, difficulty) }.getOrNull() ?: continue
            if (q.isChoice && (q.options.size < 4 || q.options.map { it.text }.toSet().size < 4)) continue
            if (out.takeLast(5).any { it.hanzi == q.hanzi && it.display == q.display && it.hanzi != null }) continue
            out += q
        }
        return out
    }

    fun make(cat: NumCat, d: Int): NumQuestion = when (cat) {
        NumCat.UMUM -> umum(d)
        NumCat.HARGA -> harga(d)
        NumCat.DISKON -> diskon(d)
        NumCat.PERSEN -> persen(d)
        NumCat.JAM -> jam(d)
        NumCat.TANGGAL -> tanggal(d)
        NumCat.TELEPON -> telepon(d)
        NumCat.UKURAN -> ukuran(d)
        NumCat.KIRA -> kira(d)
        NumCat.CAMPUR -> make(NumCat.entries.filter { it != NumCat.CAMPUR }.random(R), d)
    }

    /** Susun pilihan ganda: benar + pengecoh (unik, bukan jawaban). */
    private fun choice(correct: NumOption, wrong: List<NumOption>): Pair<List<NumOption>, Int> {
        val w = wrong.filter { it.text != correct.text }.distinctBy { it.text }.shuffled(R).take(3)
        val all = (w + correct).shuffled(R)
        return all to all.indexOfFirst { it.text == correct.text }
    }

    private fun zh(sp: Spoken) = NumOption(sp.hanzi, sp.pinyin, true)

    // ---------- Angka umum ----------
    private fun tricky(lo: Long, hi: Long): Long {
        // Angka acak dengan banyak digit 0 di tengah supaya aturan 零 sering muncul.
        repeat(300) {
            val len = R.nextLong(lo.toString().length.toLong(), hi.toString().length.toLong() + 1).toInt()
            val sb = StringBuilder()
            for (i in 0 until len) sb.append(if (i == 0) R.nextInt(1, 10) else if (R.nextFloat() < 0.4f) 0 else R.nextInt(1, 10))
            val n = sb.toString().toLong()
            if (n in lo..hi) return n
        }
        return R.nextLong(lo, hi + 1)
    }

    /** Angka acak dalam jarak tertentu; jarak lebar memakai angka dengan banyak 0 di tengah. */
    private fun inRange(r: LongRange): Long {
        val lo = r.first.coerceAtLeast(0); val hi = r.last.coerceAtMost(MAX_NUMBER)
        if (hi - lo < 1000 || R.nextInt(3) == 0) return R.nextLong(lo, hi + 1)
        return tricky(maxOf(lo, 1), hi)
    }

    private fun umumRange(d: Int): Long = when (d) {
        0 -> if (R.nextBoolean()) R.nextLong(0, 11) else R.nextLong(11, 100)
        1 -> tricky(100, 9_999)
        else -> tricky(10_000, 100_000_000)
    }

    private fun umumExplain(n: Long, sp: Spoken): String {
        val h = sp.hanzi
        val parts = ArrayList<String>()
        if (n >= 10_000) parts += "Mandarin menghitung per 4 digit: 万 = 10.000 dan 亿 = 100.000.000. Jadi ${C.fmt(n)} = $h."
        if ('零' in h) parts += "零 dipakai untuk digit kosong di tengah angka, cukup satu kali walau nolnya lebih dari satu."
        if ('两' in h) parts += "Angka 2 sebelum 千, 万, 亿 (dan 百 di awal) dibaca 两, bukan 二."
        if (h.startsWith("十")) parts += "10–19 di awal angka cukup ditulis 十…, tanpa 一."
        if (h.contains("一十")) parts += "Di tengah angka, belasan ditulis lengkap: 一十."
        if (parts.isEmpty()) parts += "${C.fmt(n)} dibaca ${sp.pinyin}."
        return parts.joinToString(" ")
    }

    private fun umumWrong(n: Long, sp: Spoken): List<NumOption> {
        val w = ArrayList<NumOption>()
        val h = sp.hanzi
        if ('零' in h) w += NumOption(h.replace("零", ""), chinese = true)
        if (n >= 10_000 && n % 1000 == 0L) w += NumOption(C.number(n / 1000).hanzi + "千", chinese = true)
        if (h.contains("一十")) w += NumOption(h.replaceFirst("一十", "十"), chinese = true)
        if (h.contains("二十")) w += NumOption(h.replaceFirst("二十", "两十"), chinese = true)
        if (n >= 10 && n * 10 <= MAX_NUMBER) w += zh(C.number(n * 10))
        if (n % 10 == 0L && n >= 100) w += zh(C.number(n / 10))
        repeat(6) {
            val s = n.toString().toCharArray()
            val idx = R.nextInt(s.size)
            s[idx] = ('0' + ((s[idx] - '0' + R.nextInt(1, 9)) % 10))
            if (s[0] == '0') s[0] = '1'
            val m = s.concatToString().toLong()
            if (m != n) w += zh(C.number(m))
        }
        return w
    }

    private fun umum(d: Int, range: LongRange? = null): NumQuestion {
        val n = if (range != null) inRange(range) else umumRange(d)
        val sp = C.number(n)
        return if (R.nextBoolean()) {
            NumQuestion(
                NumCat.UMUM, "Tulis angka ini dengan digit", hanzi = sp.hanzi, pinyin = sp.pinyin,
                accepted = setOf(n.toString()), answerText = C.fmt(n), explain = umumExplain(n, sp),
            )
        } else {
            val (opts, ans) = choice(zh(sp), umumWrong(n, sp))
            NumQuestion(NumCat.UMUM, "Pilih tulisan Mandarin yang benar", display = C.fmt(n), options = opts, answer = ans, answerText = sp.hanzi, explain = umumExplain(n, sp))
        }
    }

    // ---------- Harga ----------
    private data class Item(val h: String, val p: String, val id: String, val mw: String, val mwp: String, val mwId: String)

    private val items = listOf(
        Item("苹果", "píng guǒ", "apel", "斤", "jīn", "jin (½ kg)"),
        Item("香蕉", "xiāng jiāo", "pisang", "斤", "jīn", "jin (½ kg)"),
        Item("咖啡", "kā fēi", "kopi", "杯", "bēi", "gelas"),
        Item("面包", "miàn bāo", "roti", "个", "gè", "buah"),
        Item("牛奶", "niú nǎi", "susu", "瓶", "píng", "botol"),
        Item("本子", "běn zi", "buku tulis", "本", "běn", "buah"),
    )

    private fun money(d: Int): Triple<Int, Int, Int> = when (d) {
        0 -> Triple(R.nextInt(1, 100), if (R.nextInt(3) == 0) 5 else 0, 0)
        1 -> if (R.nextInt(3) == 0) Triple(R.nextInt(1, 200), 0, R.nextInt(1, 10)) else Triple(R.nextInt(1, 200), R.nextInt(1, 10), 0)
        else -> Triple(R.nextInt(0, 1000), R.nextInt(0, 10), R.nextInt(0, 10)).let { if (it.first + it.second + it.third == 0) Triple(1, 5, 0) else it }
    }

    private fun yuanText(y: Int, j: Int, f: Int) = "¥" + C.fmtDec(y + j / 10.0 + f / 100.0)

    private fun harga(d: Int): NumQuestion {
        if (d >= 1 && R.nextInt(3) == 0) return hargaStory(d)
        val (y, j, f) = money(d)
        val written = d == 2 && R.nextBoolean()
        val sp = if (written) C.moneyWritten(y, j, f) else C.moneySpoken(y, j, f)
        val value = y + j / 10.0 + f / 100.0
        val explain = buildString {
            append(if (written) "Di label harga tertulis: 元 = yuan, 角 = 0,1 yuan, 分 = 0,01 yuan. " else "Lisan: 块 = yuan, 毛 = 0,1 yuan, 分 = 0,01 yuan. ")
            if (!written && j > 0 && f == 0 && y > 0) append("Satuan terakhir (毛) boleh dihilangkan: ${sp.hanzi} = ${yuanText(y, j, f)}. ")
            if (!written && j == 0 && f > 0 && y > 0) append("Ada 零 karena 毛-nya kosong. ")
            if (sp.hanzi.startsWith("两")) append("2 sebelum kata satuan dibaca 两, bukan 二. ")
        }.trim()
        return if (R.nextBoolean()) {
            NumQuestion(
                NumCat.HARGA, "Berapa harganya dalam yuan?", hanzi = sp.hanzi, pinyin = sp.pinyin, accepted = setOf(dec(value)),
                answerText = yuanText(y, j, f), keys = Keys.DECIMAL, unit = "yuan", explain = explain,
            )
        } else {
            val wrong = ArrayList<NumOption>()
            if (j > 0 && f == 0 && y > 0) wrong += zh(C.moneySpoken(y, 0, j))          // 块五 vs 块零五分
            if (j == 0 && f > 0 && y > 0) wrong += zh(C.moneySpoken(y, f, 0))
            if (y == 2) wrong += NumOption(sp.hanzi.replaceFirst("两", "二"), chinese = true)
            wrong += zh(C.moneySpoken(y + R.nextInt(1, 5), j, f))
            wrong += zh(C.moneySpoken(maxOf(1, y * 10 % 1000), j, f))
            wrong += zh(C.moneySpoken(y, (j + R.nextInt(1, 9)) % 10, f))
            wrong += zh(C.moneySpoken(y, j, (f + R.nextInt(1, 9)) % 10))
            val (opts, ans) = choice(zh(C.moneySpoken(y, j, f)), wrong)
            NumQuestion(NumCat.HARGA, "Bagaimana harga ini diucapkan?", display = yuanText(y, j, f), options = opts, answer = ans, answerText = C.moneySpoken(y, j, f).hanzi, explain = explain)
        }
    }

    private fun hargaStory(d: Int): NumQuestion {
        val it = items.random(R)
        val prices = listOf(Triple(1, 5, 0), Triple(2, 0, 0), Triple(2, 5, 0), Triple(3, 5, 0), Triple(4, 5, 0), Triple(6, 0, 0), Triple(8, 0, 0), Triple(12, 5, 0), Triple(18, 0, 0), Triple(25, 0, 0))
        val (y, j, f) = prices.random(R)
        val price = y + j / 10.0
        return if (R.nextBoolean()) {
            val k = R.nextInt(2, 7)
            val sp = Spoken.of("一" to if (it.mw == "个") "yí" else "yì", it.mw to it.mwp) + Spoken(it.h.chunked(1).zip(it.p.split(" ")).map { (h, p) -> com.nesxha.readit.util.Syl(h, p) }) + C.moneySpoken(y, j, f)
            NumQuestion(
                NumCat.HARGA, "Harga per ${it.mwId} ${it.id} seperti di atas. Kamu beli $k ${it.mwId}. Total bayar berapa yuan?",
                hanzi = sp.hanzi, pinyin = sp.pinyin, accepted = setOf(dec(price * k)), answerText = "¥" + C.fmtDec(price * k),
                keys = Keys.DECIMAL, unit = "yuan", story = true,
                explain = "${sp.hanzi} = ¥${C.fmtDec(price)} per ${it.mwId}. ¥${C.fmtDec(price)} × $k = ¥${C.fmtDec(price * k)}.",
            )
        } else {
            val priceCents = y * 100 + j * 10 + f
            val pay = listOf(10, 20, 50, 100).first { it * 100 > priceCents }
            val changeCents = pay * 100 - priceCents
            val change = changeCents / 100.0
            val prompt = C.moneySpoken(y, j, f)
            val say = Spoken.of("找" to "zhǎo", "你" to "nǐ") + C.moneySpoken(changeCents / 100, (changeCents / 10) % 10, changeCents % 10)
            NumQuestion(
                NumCat.HARGA, "Harga ${it.id} tertulis di atas. Kamu membayar dengan uang ¥$pay. Berapa kembaliannya (yuan)?",
                hanzi = prompt.hanzi, pinyin = prompt.pinyin, accepted = setOf(dec(change)), answerText = "¥" + C.fmtDec(change),
                keys = Keys.DECIMAL, unit = "yuan", story = true,
                explain = "${prompt.hanzi} = ¥${C.fmtDec(price)}. ¥$pay − ¥${C.fmtDec(price)} = ¥${C.fmtDec(change)}. Penjual akan bilang: ${say.hanzi} (${say.pinyin}).",
            )
        }
    }

    // ---------- Diskon ----------
    private fun zhe(x: Int): Spoken = // x = 1..9 atau 15..95 (dua digit = X五折)
        if (x < 10) Spoken.of("打" to "dǎ") + Spoken(listOf(C.digit(x))) + Spoken.of("折" to "zhé")
        else Spoken.of("打" to "dǎ") + Spoken(listOf(C.digit(x / 10), C.digit(x % 10))) + Spoken.of("折" to "zhé")

    private fun payPct(x: Int) = if (x < 10) x * 10 else x

    private fun diskon(d: Int): NumQuestion {
        val pick = R.nextInt(if (d == 0) 2 else 4)
        val x = if (d == 0) R.nextInt(5, 10) else if (R.nextBoolean()) R.nextInt(5, 10) else listOf(75, 85, 95, 65).random(R)
        val sp = zhe(x)
        val pct = payPct(x)
        val explain = "${sp.hanzi} artinya kamu membayar $pct% dari harga asli, jadi potongannya ${100 - pct}%. Angka di depan 折 adalah yang DIBAYAR, bukan potongannya."
        return when {
            d >= 2 && R.nextInt(3) == 0 -> manJian()
            pick == 0 -> NumQuestion(
                NumCat.DISKON, "Dengan diskon ini, kamu membayar berapa persen dari harga asli?", hanzi = sp.hanzi, pinyin = sp.pinyin,
                accepted = setOf(pct.toString()), answerText = "$pct%", keys = Keys.PERCENT, unit = "%", explain = explain,
            )
            pick == 1 -> {
                val wrong = listOf(
                    NumOption(zhe(if (x < 10) 10 - x else (100 - x)).hanzi, chinese = true),
                    NumOption("打${C.number(pct).hanzi}折", chinese = true),
                    NumOption(zhe(if (x < 10) (x % 9) + 1 else x - 10).hanzi, chinese = true),
                    NumOption("${C.number(100 - pct).hanzi}折打", chinese = true),
                )
                val (opts, ans) = choice(zh(sp), wrong)
                NumQuestion(NumCat.DISKON, "Toko memberi potongan ${100 - pct}%. Tulisan di toko yang benar?", display = "−${100 - pct}%", options = opts, answer = ans, answerText = sp.hanzi, explain = explain)
            }
            else -> {
                val price = (R.nextInt(2, 21) * 20)
                val paid = price * pct / 100
                val tag = Spoken.of("原" to "yuán", "价" to "jià") + C.count(price, "块", "kuài", 4) + Spoken.of("，" to ",") + sp
                NumQuestion(
                    NumCat.DISKON, "Berapa yang harus kamu bayar (yuan)?", hanzi = tag.hanzi, pinyin = tag.pinyin.replace(" , ", ", "),
                    accepted = setOf(paid.toString()), answerText = "¥$paid", keys = Keys.NUMBER, unit = "yuan", story = true,
                    explain = "原价 = harga asli ¥$price. ${sp.hanzi} = bayar $pct%. ¥$price × $pct% = ¥$paid.",
                )
            }
        }
    }

    private fun manJian(): NumQuestion {
        val min = listOf(100, 200, 300, 500).random(R)
        val cut = min / listOf(5, 4, 10).random(R)
        val spend = if (R.nextInt(4) == 0) min - R.nextInt(1, 5) * 10 else min + R.nextInt(0, 9) * 10
        val pay = if (spend >= min) spend - cut else spend
        val sp = Spoken.of("满" to "mǎn") + C.number(min) + Spoken.of("减" to "jiǎn") + C.number(cut)
        return NumQuestion(
            NumCat.DISKON, "Promo ini berlaku di toko. Kamu belanja ¥$spend. Berapa yang dibayar (yuan)?", hanzi = sp.hanzi, pinyin = sp.pinyin,
            accepted = setOf(pay.toString()), answerText = "¥$pay", unit = "yuan", story = true,
            explain = "${sp.hanzi}: belanja minimal ¥$min dapat potongan ¥$cut. " +
                if (spend >= min) "¥$spend ≥ ¥$min, jadi bayar ¥$spend − ¥$cut = ¥$pay." else "¥$spend belum mencapai ¥$min, jadi tidak dapat potongan: bayar ¥$spend.",
        )
    }

    // ---------- Persen, pecahan, desimal ----------
    private fun persen(d: Int): NumQuestion = when (R.nextInt(3)) {
        0 -> {
            val (i, fr) = when (d) {
                0 -> (R.nextInt(1, 10) * 10).toLong() to ""
                1 -> R.nextLong(1, 101) to ""
                else -> if (R.nextBoolean()) 0L to "${R.nextInt(1, 10)}" else R.nextLong(1, 100) to "5"
            }
            val sp = C.percent(i, fr)
            val shown = if (fr.isEmpty()) "$i%" else "$i,$fr%"
            val explain = "百分之 = \"per seratus\". Urutannya terbalik dari bahasa Indonesia: 百分之 dulu, baru angkanya. ${sp.hanzi} = $shown."
            if (R.nextBoolean()) NumQuestion(NumCat.PERSEN, "Tulis sebagai persen", hanzi = sp.hanzi, pinyin = sp.pinyin, accepted = setOf(norm(shown)), answerText = shown, keys = Keys.PERCENT, unit = "%", explain = explain)
            else {
                val wrong = listOf(
                    NumOption(C.decimal(i, fr).hanzi + "分之百", chinese = true),
                    NumOption("百分" + C.decimal(i, fr).hanzi, chinese = true),
                    zh(C.percent(if (i > 1) i / 10 + 1 else i + 1, fr)),
                    zh(C.percent((i * 10).coerceAtMost(999), "")),
                )
                val (opts, ans) = choice(zh(sp), wrong)
                NumQuestion(NumCat.PERSEN, "Bagaimana persen ini dibaca?", display = shown, options = opts, answer = ans, answerText = sp.hanzi, explain = explain)
            }
        }
        1 -> {
            val den = when (d) { 0 -> listOf(2, 3, 4).random(R); 1 -> R.nextInt(2, 11); else -> R.nextInt(5, 21) }
            val num = R.nextInt(1, den)
            val sp = C.fraction(num, den)
            val explain = "Pecahan dibaca PENYEBUT dulu: ${C.number(den).hanzi}分之${C.number(num).hanzi} = $num/$den (\"$num dari $den bagian\")."
            if (R.nextBoolean()) NumQuestion(NumCat.PERSEN, "Tulis sebagai pecahan (pembilang/penyebut)", hanzi = sp.hanzi, pinyin = sp.pinyin, accepted = setOf("$num/$den"), answerText = "$num/$den", keys = Keys.FRACTION, explain = explain)
            else {
                val wrong = listOf(
                    zh(C.fraction(den, num)),
                    zh(C.fraction(num, den + 1)),
                    zh(C.fraction(maxOf(1, (num + 1) % den), den)),
                    NumOption(C.number(num).hanzi + "分之" + C.number(den).hanzi, chinese = true),
                )
                val (opts, ans) = choice(zh(sp), wrong)
                NumQuestion(NumCat.PERSEN, "Bagaimana pecahan ini dibaca?", display = "$num/$den", options = opts, answer = ans, answerText = sp.hanzi, explain = explain)
            }
        }
        else -> {
            val i = when (d) { 0 -> R.nextLong(0, 10); 1 -> R.nextLong(0, 100); else -> R.nextLong(0, 1000) }
            val fr = when (d) {
                0 -> "${R.nextInt(1, 10)}"
                1 -> "${R.nextInt(1, 10)}${R.nextInt(1, 10)}"
                else -> listOf("0${R.nextInt(1, 10)}", "${R.nextInt(1, 10)}${R.nextInt(0, 10)}${R.nextInt(1, 10)}").random(R)
            }
            val sp = C.decimal(i, fr)
            val shown = "${C.fmt(i)},$fr"
            val explain = "Koma desimal dibaca 点. Angka di belakang 点 dibaca satu per satu: ${sp.hanzi} = $shown."
            if (R.nextBoolean()) NumQuestion(NumCat.PERSEN, "Tulis sebagai bilangan desimal", hanzi = sp.hanzi, pinyin = sp.pinyin, accepted = setOf(norm(shown)), answerText = shown, keys = Keys.DECIMAL, explain = explain)
            else {
                val frNum = fr.trimStart('0').toLongOrNull() ?: 0L
                val wrong = listOf(
                    NumOption(C.number(i).hanzi + "点" + C.number(frNum).hanzi, chinese = true),
                    zh(C.decimal(i, fr.reversed())),
                    zh(C.decimal(i + 1, fr)),
                    NumOption(C.number(frNum).hanzi + "点" + C.number(i).hanzi, chinese = true),
                )
                val (opts, ans) = choice(zh(sp), wrong)
                NumQuestion(NumCat.PERSEN, "Bagaimana bilangan ini dibaca?", display = shown, options = opts, answer = ans, answerText = sp.hanzi, explain = explain)
            }
        }
    }

    // ---------- Jam ----------
    private fun periodOf(h24: Int): Pair<String, String> = when (h24) {
        in 1..5 -> "凌晨" to "líng chén"
        in 6..11 -> "上午" to "shàng wǔ"
        12 -> "中午" to "zhōng wǔ"
        in 13..17 -> "下午" to "xià wǔ"
        else -> "晚上" to "wǎn shang"
    }

    /** Gaya bacaan yang sah untuk menit [m]; [prefer] dipakai kalau sah. */
    private fun styleFor(m: Int, prefer: Int): Int = when {
        prefer == 1 && m in setOf(15, 30, 45) -> 1
        prefer == 2 && (m == 45 || m >= 50) -> 2
        else -> 0
    }

    private fun jam(d: Int): NumQuestion {
        val h = R.nextInt(1, 13)
        val m = when (d) {
            0 -> listOf(0, 30).random(R)
            1 -> listOf(15, 45, R.nextInt(1, 10), R.nextInt(10, 60)).random(R)
            else -> listOf(45, R.nextInt(50, 60), R.nextInt(1, 60)).random(R)
        }
        val style = when (d) { 0 -> styleFor(m, 1); 1 -> styleFor(m, 1); else -> styleFor(m, 2) }
        val base = C.time(h, m, style)
        // Tingkat sulit: tambah keterangan waktu (上午/下午/…) dan jawab dalam format 24 jam.
        val h24 = if (d == 2 && style != 2 && R.nextBoolean()) {
            if (h == 12) 12 else if (R.nextBoolean()) h else h + 12
        } else null
        val sp = if (h24 != null) periodOf(h24).let { (ph, pp) -> Spoken(ph.chunked(1).zip(pp.split(" ")).map { (a, b) -> com.nesxha.readit.util.Syl(a, b) }) } + base else base
        val mm = m.toString().padStart(2, '0')
        val text = "${h24 ?: h}:$mm"
        val explain = buildString {
            append("点 = jam. ")
            if (base.hanzi.contains("刻")) append("刻 = 15 menit (一刻 = :15, 三刻 = :45). ")
            if (base.hanzi.contains("半")) append("半 = setengah, :30. ")
            if (base.hanzi.startsWith("差")) append("差 = kurang: ${base.hanzi} artinya kurang ${60 - m} menit menuju jam ${h % 12 + 1}, jadi $h:$mm. ")
            if (style == 0 && m in 1..9) append("Menit 1–9 diberi 零 di depannya. ")
            if (base.hanzi.contains("两点")) append("Jam 2 dibaca 两点, bukan 二点. ")
            if (h24 != null) append("${periodOf(h24).first} = ${periodName(periodOf(h24).first)}, jadi dalam format 24 jam = $text.")
        }.trim()
        return if (R.nextBoolean()) {
            NumQuestion(
                NumCat.JAM, if (h24 != null) "Tulis jamnya dalam format 24 jam (jam:menit)" else "Tulis jamnya (jam:menit)",
                hanzi = sp.hanzi, pinyin = sp.pinyin, accepted = setOf(norm(text)), answerText = text, keys = Keys.TIME, explain = explain,
            )
        } else {
            // Pengecoh = bacaan untuk jam lain (pasti salah), plus 二点 kalau jamnya 2.
            val others = listOf(h % 12 + 1 to m, (if (h == 1) 12 else h - 1) to m, h to (m + 15) % 60, h to (m + 30) % 60)
            val wrong = others.filter { it != (h to m) }.map { (oh, om) -> zh(C.time(oh, om, styleFor(om, style))) }.toMutableList()
            if (h == 2) wrong += NumOption(base.hanzi.replace("两点", "二点"), chinese = true)
            val (opts, ans) = choice(zh(base), wrong)
            NumQuestion(NumCat.JAM, "Bagaimana jam ini diucapkan?", display = "$h:$mm", options = opts, answer = ans, answerText = base.hanzi, explain = explain)
        }
    }

    private fun periodName(p: String) = when (p) {
        "凌晨" -> "dini hari"; "上午" -> "pagi"; "中午" -> "siang (sekitar jam 12)"; "下午" -> "sore/siang"; else -> "malam"
    }

    // ---------- Tanggal & hari ----------
    private val monthsId = listOf("Januari", "Februari", "Maret", "April", "Mei", "Juni", "Juli", "Agustus", "September", "Oktober", "November", "Desember")
    private val daysId = listOf("Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu", "Minggu")

    private fun tanggal(d: Int): NumQuestion {
        when (d) {
            0 -> return if (R.nextBoolean()) {
                val w = R.nextInt(1, 8)
                val sp = C.weekday(w, if (R.nextBoolean()) "天" else "日")
                val (opts, ans) = choice(NumOption(daysId[w - 1]), daysId.filterIndexed { i, _ -> i != w - 1 }.map { NumOption(it) })
                NumQuestion(NumCat.TANGGAL, "Hari apa ini?", hanzi = sp.hanzi, pinyin = sp.pinyin, options = opts, answer = ans, answerText = daysId[w - 1],
                    explain = "星期 + angka: 星期一 = Senin … 星期六 = Sabtu. Minggu bukan 星期七, tetapi 星期天 atau 星期日.")
            } else {
                val mo = R.nextInt(1, 13)
                val sp = C.number(mo) + Spoken.of("月" to "yuè")
                val (opts, ans) = choice(NumOption(monthsId[mo - 1]), monthsId.filterIndexed { i, _ -> i != mo - 1 }.map { NumOption(it) })
                NumQuestion(NumCat.TANGGAL, "Bulan apa ini?", hanzi = sp.hanzi, pinyin = sp.pinyin, options = opts, answer = ans, answerText = monthsId[mo - 1],
                    explain = "Nama bulan = angka + 月: 一月 = Januari, 十二月 = Desember.")
            }
            1 -> {
                val y = R.nextInt(1990, 2036); val mo = R.nextInt(1, 13); val day = R.nextInt(1, 29)
                val sp = C.date(y, mo, day, hao = R.nextBoolean())
                val text = "$day/$mo/$y"
                val explain = "Urutan tanggal Mandarin: tahun → bulan → tanggal. Tahun dibaca per digit (${C.digits(y.toString()).hanzi}年). 号 dipakai saat bicara, 日 saat menulis."
                return if (R.nextBoolean()) NumQuestion(NumCat.TANGGAL, "Tulis tanggalnya (tanggal/bulan/tahun)", hanzi = sp.hanzi, pinyin = sp.pinyin, accepted = setOf(norm(text)), answerText = text, keys = Keys.DATE, explain = explain)
                else {
                    val wrong = listOf(
                        zh(C.date(y, day.coerceAtMost(12), mo)).takeIf { day <= 12 && day != mo } ?: zh(C.date(y, mo % 12 + 1, day)),
                        NumOption(C.number(y).hanzi + "年" + C.number(mo).hanzi + "月" + C.number(day).hanzi + "号", chinese = true),
                        NumOption(C.number(mo).hanzi + "月" + C.number(day).hanzi + "号" + C.digits(y.toString()).hanzi + "年", chinese = true),
                        zh(C.date(y + 1, mo, day)),
                    )
                    val (opts, ans) = choice(zh(C.date(y, mo, day)), wrong)
                    NumQuestion(NumCat.TANGGAL, "Bagaimana tanggal ini diucapkan?", display = "$day ${monthsId[mo - 1]} $y", options = opts, answer = ans, answerText = C.date(y, mo, day).hanzi, explain = explain)
                }
            }
            else -> {
                val rel = listOf(Triple("前天", "qián tiān", -2), Triple("昨天", "zuó tiān", -1), Triple("明天", "míng tiān", 1), Triple("后天", "hòu tiān", 2), Triple("大后天", "dà hòu tiān", 3)).random(R)
                val today = R.nextInt(1, 8)
                val target = ((today - 1 + rel.third) % 7 + 7) % 7 + 1
                val sp = Spoken.of("今" to "jīn", "天" to "tiān", "是" to "shì") + C.weekday(today) + Spoken.of("，" to ",") +
                    Spoken(rel.first.chunked(1).zip(rel.second.split(" ")).map { (h, p) -> com.nesxha.readit.util.Syl(h, p) }) + Spoken.of("是" to "shì", "星" to "xīng", "期" to "qī", "几" to "jǐ", "？" to "?")
                val correct = zh(C.weekday(target))
                val wrong = (1..7).filter { it != target }.map { zh(C.weekday(it)) }
                val (opts, ans) = choice(correct, wrong)
                return NumQuestion(
                    NumCat.TANGGAL, "Jawab pertanyaannya", hanzi = sp.hanzi, pinyin = sp.pinyin.replace(" , ", ", ").replace(" ?", "?"), options = opts, answer = ans, answerText = correct.text,
                    explain = "前天 = 2 hari lalu, 昨天 = kemarin, 明天 = besok, 后天 = lusa, 大后天 = 3 hari lagi. Hari ini ${daysId[today - 1]}, jadi ${rel.first} = ${daysId[target - 1]} (${correct.text}).",
                )
            }
        }
    }

    // ---------- Telepon & kamar ----------
    private fun telepon(d: Int): NumQuestion {
        if (d == 2 && R.nextInt(3) == 0) {
            val (num, what) = listOf("110" to "Polisi", "119" to "Pemadam kebakaran", "120" to "Ambulans").random(R)
            val sp = C.digits(num, yao = true, zero = "零")
            val (opts, ans) = choice(NumOption(what), listOf("Polisi", "Pemadam kebakaran", "Ambulans", "Informasi telepon (114)").map { NumOption(it) })
            return NumQuestion(NumCat.TELEPON, "Nomor darurat ini untuk apa?", hanzi = sp.hanzi, pinyin = sp.pinyin, options = opts, answer = ans, answerText = what,
                explain = "Nomor darurat di Tiongkok: 110 (幺幺零) polisi, 119 (幺幺九) pemadam kebakaran, 120 (幺二零) ambulans.")
        }
        val phone = d >= 1 && R.nextBoolean()
        val number = if (phone) "1" + listOf("3", "5", "8").random(R) + (1..9).map { R.nextInt(0, 10) }.joinToString("") else "${R.nextInt(1, 10)}0${R.nextInt(1, 10)}"
        val sp = if (phone) C.digits(number, yao = true, zero = "零") else C.digits(number, zero = "〇")
        val shown = if (phone) "${number.substring(0, 3)} ${number.substring(3, 7)} ${number.substring(7)}" else number
        val explain = if (phone) "Nomor telepon dibaca per digit, dan angka 1 dibaca 幺 (yāo) supaya tidak tertukar dengan 七 (qī)."
        else "Nomor kamar, bus, dan tahun dibaca per digit: ${sp.hanzi} = $number, bukan ${C.number(number.toLong()).hanzi}."
        return if (R.nextBoolean()) {
            NumQuestion(NumCat.TELEPON, if (phone) "Tulis nomor teleponnya" else "Tulis nomor kamarnya", hanzi = sp.hanzi + if (phone) "" else "房间", pinyin = sp.pinyin + if (phone) "" else " fáng jiān",
                accepted = setOf(number.trimStart('0').ifEmpty { "0" }), answerText = shown, explain = explain)
        } else if (phone) {
            val plainYi = number.map { if (it == '1') "yī" else C.digits(it.toString(), zero = "零").pinyin }.joinToString(" ")
            val swapped = number.toCharArray().also { a -> val i = R.nextInt(3, a.size - 1); val t = a[i]; a[i] = a[i + 1]; a[i + 1] = t }.concatToString()
            val other = number.toCharArray().also { a -> val i = R.nextInt(3, a.size); a[i] = ('0' + (a[i] - '0' + R.nextInt(1, 9)) % 10) }.concatToString()
            val wrong = listOf(NumOption(plainYi), NumOption(C.digits(swapped, yao = true, zero = "零").pinyin), NumOption(C.digits(other, yao = true, zero = "零").pinyin))
            val (opts, ans) = choice(NumOption(sp.pinyin), wrong)
            NumQuestion(NumCat.TELEPON, "Bagaimana nomor telepon ini dibaca?", display = shown, options = opts, answer = ans, answerText = sp.pinyin, explain = explain)
        } else {
            val r = number
            val wrong = listOf(zh(C.digits(r.reversed())), zh(C.number(r.toLong())), zh(C.digits(r.substring(0, 1) + r.substring(2) + r.substring(1, 2))), zh(C.digits("${r[0]}00${r[2]}")))
            val (opts, ans) = choice(zh(C.digits(r)), wrong)
            NumQuestion(NumCat.TELEPON, "Nomor kamar ini dibaca bagaimana?", display = "Kamar $r", options = opts, answer = ans, answerText = C.digits(r).hanzi, explain = explain)
        }
    }

    // ---------- Ukuran sehari-hari ----------
    private fun ukuran(d: Int): NumQuestion {
        val kind = when (d) { 0 -> listOf(0, 1, 2).random(R); 1 -> listOf(0, 2, 3, 4).random(R); else -> listOf(3, 4, 5).random(R) }
        return when (kind) {
            0 -> { // umur
                val a = R.nextInt(2, 90)
                val sp = C.count(a, "岁", "suì", 4)
                val ex = "岁 = tahun (untuk umur). ${sp.hanzi} = $a tahun." + if (a == 2) " Umur 2 = 两岁." else ""
                if (R.nextBoolean()) NumQuestion(NumCat.UKURAN, "Berapa umurnya (tahun)?", hanzi = sp.hanzi, pinyin = sp.pinyin, accepted = setOf("$a"), answerText = "$a tahun", unit = "tahun", explain = ex)
                else {
                    val wrong = listOf(NumOption(C.number(a).hanzi + "年", chinese = true), zh(C.count(a + 10, "岁", "suì", 4)), NumOption(a.toString().map { C.digit(it - '0').h }.joinToString("") + "岁", chinese = true), zh(C.count(maxOf(2, a - 1), "岁", "suì", 4)))
                    val (opts, ans) = choice(zh(sp), wrong)
                    NumQuestion(NumCat.UKURAN, "Umur ini dalam bahasa Mandarin?", display = "$a tahun", options = opts, answer = ans, answerText = sp.hanzi, explain = ex)
                }
            }
            1 -> { // jumlah orang dengan 个
                val k = R.nextInt(1, 21)
                val sp = C.count(k, "个", "gè", 4) + Spoken.of("人" to "rén")
                val ex = "Benda/orang dihitung dengan kata satuan, misalnya 个. Angka 2 sebelum kata satuan menjadi 两: 两个人."
                val wrong = listOf(NumOption(C.number(k).hanzi + "个人", chinese = true).takeIf { k == 2 } ?: zh(C.count(k + 1, "个", "gè", 4) + Spoken.of("人" to "rén")),
                    NumOption(C.number(k).hanzi + "人个", chinese = true), zh(C.count(k + 2, "个", "gè", 4) + Spoken.of("人" to "rén")),
                    zh(C.count(maxOf(1, k - 1), "个", "gè", 4) + Spoken.of("人" to "rén")))
                val (opts, ans) = choice(zh(sp), wrong)
                NumQuestion(NumCat.UKURAN, "Bagaimana mengatakan jumlah orang ini?", display = "$k orang", options = opts, answer = ans, answerText = sp.hanzi, explain = ex)
            }
            2 -> { // lantai
                val f = R.nextInt(1, 31)
                val sp = C.number(f) + Spoken.of("楼" to "lóu")
                val ex = "Lantai = angka + 楼. Untuk urutan seperti lantai, 2 tetap dibaca 二: 二楼, bukan 两楼."
                if (R.nextBoolean()) NumQuestion(NumCat.UKURAN, "Lantai berapa?", hanzi = sp.hanzi, pinyin = sp.pinyin, accepted = setOf("$f"), answerText = "Lantai $f", explain = ex)
                else {
                    val wrong = listOf(if (f == 2) NumOption("两楼", chinese = true) else zh(C.number(f + 1) + Spoken.of("楼" to "lóu")), NumOption("楼" + C.number(f).hanzi, chinese = true), zh(C.number(f * 10 % 100 + 1) + Spoken.of("楼" to "lóu")), zh(C.number(maxOf(1, f - 1)) + Spoken.of("楼" to "lóu")))
                    val (opts, ans) = choice(zh(sp), wrong)
                    NumQuestion(NumCat.UKURAN, "Bagaimana mengatakan lantai ini?", display = "Lantai $f", options = opts, answer = ans, answerText = sp.hanzi, explain = ex)
                }
            }
            3 -> { // tinggi badan 一米七五
                val cm = R.nextInt(140, 200).let { if (R.nextBoolean()) it / 10 * 10 else it }
                val rest = cm - 100
                val sp = Spoken.of("一" to "yì", "米" to "mǐ") + Spoken(rest.toString().trimEnd('0').ifEmpty { "0" }.map { C.digit(it - '0') })
                val m = cm / 100.0
                val ex = "Tinggi badan diucapkan \"meter + sisa\": ${sp.hanzi} = ${C.fmtDec(m)} m. Digit setelah 米 dibaca satu per satu."
                if (R.nextBoolean()) NumQuestion(NumCat.UKURAN, "Berapa tingginya (meter)?", hanzi = sp.hanzi, pinyin = sp.pinyin, accepted = setOf(dec(m)), answerText = "${C.fmtDec(m)} m", keys = Keys.DECIMAL, unit = "m", explain = ex)
                else {
                    val wrongNum = if (rest % 10 != 0) Spoken.of("一" to "yì", "米" to "mǐ") + C.number(rest) else Spoken.of("一" to "yì", "米" to "mǐ") + C.number(rest)
                    val wrong = listOf(
                        NumOption(wrongNum.hanzi.takeIf { it != sp.hanzi } ?: ("一米" + C.number(rest / 10).hanzi + "十"), chinese = true),
                        zh(Spoken.of("一" to "yì", "米" to "mǐ") + Spoken((rest + 5).toString().trimEnd('0').map { C.digit(it - '0') })),
                        NumOption(C.number(rest).hanzi + "米一", chinese = true),
                        zh(Spoken.of("一" to "yì", "米" to "mǐ") + Spoken(rest.toString().reversed().trimEnd('0').map { C.digit(it - '0') })),
                    )
                    val (opts, ans) = choice(zh(sp), wrong)
                    NumQuestion(NumCat.UKURAN, "Bagaimana tinggi ini diucapkan?", display = "${C.fmtDec(m)} m", options = opts, answer = ans, answerText = sp.hanzi, explain = ex)
                }
            }
            4 -> { // berat 公斤
                val kg = R.nextInt(2, 120)
                val sp = C.count(kg, "公", "gōng", 1) + Spoken.of("斤" to "jīn")
                val ex = "公斤 = kilogram. ${sp.hanzi} = $kg kg."
                NumQuestion(NumCat.UKURAN, "Berapa beratnya (kg)?", hanzi = sp.hanzi, pinyin = sp.pinyin, accepted = setOf("$kg"), answerText = "$kg kg", unit = "kg", explain = ex)
            }
            else -> { // 斤 → kg
                val jin = R.nextInt(1, 21)
                val sp = C.count(jin, "斤", "jīn", 1)
                val kg = jin / 2.0
                NumQuestion(
                    NumCat.UKURAN, "Di pasar Tiongkok berat dihitung dengan 斤. Berapa kilogram ini?", hanzi = sp.hanzi, pinyin = sp.pinyin,
                    accepted = setOf(dec(kg)), answerText = "${C.fmtDec(kg)} kg", keys = Keys.DECIMAL, unit = "kg", story = true,
                    explain = "1 斤 = 0,5 kg (500 gram). ${sp.hanzi} = $jin × 0,5 = ${C.fmtDec(kg)} kg.",
                )
            }
        }
    }

    // ---------- Kira-kira ----------
    private data class Approx(val sp: Spoken, val meaning: String)

    private fun approxPool(d: Int): List<Approx> {
        val ge = Spoken.of("个" to "gè")
        val list = ArrayList<Approx>()
        list += Approx(Spoken.of("几" to "jǐ") + ge, "Beberapa (biasanya kurang dari 10)")
        list += Approx(Spoken.of("三" to "sān", "四" to "sì") + ge, "Tiga atau empat")
        list += Approx(Spoken.of("十" to "shí", "多" to "duō") + ge, "Lebih dari 10 (antara 11–19)")
        list += Approx(Spoken.of("五" to "wǔ", "六" to "liù") + ge, "Lima atau enam")
        list += Approx(Spoken.of("二" to "èr", "十" to "shí", "多" to "duō") + ge, "Dua puluh lebih")
        if (d >= 1) {
            list += Approx(Spoken.of("几" to "jǐ", "十" to "shí") + ge, "Puluhan (beberapa puluh)")
            list += Approx(Spoken.of("二" to "èr", "三" to "sān", "十" to "shí") + ge, "Sekitar 20 sampai 30")
            list += Approx(C.number(100) + Spoken.of("多" to "duō") + ge, "Seratus lebih")
            list += Approx(Spoken.of("七" to "qī", "八" to "bā") + ge, "Tujuh atau delapan")
        }
        if (d >= 2) {
            list += Approx(Spoken.of("几" to "jǐ", "百" to "bǎi") + ge, "Ratusan")
            list += Approx(C.number(2000) + Spoken.of("多" to "duō"), "Dua ribu lebih")
            list += Approx(Spoken.of("五" to "wǔ", "六" to "liù", "十" to "shí") + ge, "Sekitar 50 sampai 60")
            list += Approx(C.number(10_000) + Spoken.of("多" to "duō"), "Sepuluh ribu lebih")
            list += Approx(Spoken.of("几" to "jǐ", "万" to "wàn"), "Puluhan ribu")
        }
        return list
    }

    private fun kira(d: Int): NumQuestion {
        val pool = approxPool(d)
        val a = pool.random(R)
        val ex = "多 setelah angka = \"lebih\" (十多 = belasan, 一百多 = seratus lebih). 几 = \"beberapa\" (几十 = puluhan). Dua angka berurutan = perkiraan (三四 = 3 atau 4, 二三十 = 20–30)."
        return if (R.nextBoolean()) {
            val (opts, ans) = choice(NumOption(a.meaning), (pool - a).map { NumOption(it.meaning) })
            NumQuestion(NumCat.KIRA, "Apa artinya?", hanzi = a.sp.hanzi, pinyin = a.sp.pinyin, options = opts, answer = ans, answerText = a.meaning, explain = ex)
        } else {
            val (opts, ans) = choice(zh(a.sp), (pool - a).map { zh(it.sp) })
            NumQuestion(NumCat.KIRA, "Mana yang artinya \"${a.meaning.replaceFirstChar { it.lowercaseChar() }}\"?", display = a.meaning, options = opts, answer = ans, answerText = a.sp.hanzi, explain = ex)
        }
    }
}
