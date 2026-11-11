package com.nesxha.readit.data

import com.nesxha.readit.util.Pinyin
import com.nesxha.readit.util.Sentence
import kotlin.random.Random

/** Semua mode di tab Latihan. */
enum class PracticeMode(val title: String, val sub: String) {
    HANZI_ARTI("Hanzi → Arti", "Baca hanzi, pilih artinya"),
    ARTI_HANZI("Arti → Hanzi", "Lihat arti, pilih hanzinya"),
    HANZI_PINYIN("Hanzi → Pinyin", "Baca hanzi, pilih pinyinnya"),
    PINYIN_HANZI("Pinyin → Hanzi", "Lihat pinyin, pilih hanzinya"),
    NADA("Tebak Nada", "Baca hanzi, pilih nadanya"),
    SUSUN("Susun Kalimat", "Urutkan potongan kalimat"),
    TULIS("Tulis Hanzi", "Lihat pinyin, tulis hanzinya"),
    KILAT("Kuis Kilat", "1–3 menit · tanpa bantuan"),
    UJIAN("Ujian HSK", "20 soal · 10 menit · lulus 60%"),
    ANGKA("Kuis Angka", "Angka, harga, diskon, jam, dan lainnya");

    /** Jumlah soal tetap (tidak bisa dipilih). */
    val fixedCount: Boolean get() = this == KILAT || this == UJIAN
    val usesWords: Boolean get() = this != ANGKA
}

/** Asal kata untuk soal latihan. */
enum class WordSource(val label: String) {
    SEMUA("Semua Kata"), DIPELAJARI("Sudah Dipelajari"), SALAH("Sering Salah"), BINTANG("Berbintang"),
}

enum class QType(val label: String, val hintable: Boolean) {
    HANZI_ARTI("Hanzi → Arti", true),
    ARTI_HANZI("Arti → Hanzi", true),
    HANZI_PINYIN("Hanzi → Pinyin", false),   // jawabannya pinyin, jadi tanpa bantuan
    PINYIN_HANZI("Pinyin → Hanzi", false),   // pinyin sudah jadi soalnya
    NADA("Tebak Nada", false),
    SUSUN("Susun Kalimat", true),
}

data class Question(
    val word: Word,
    val type: QType,
    val options: List<String>,
    val answer: Int,
    val chunks: List<String> = emptyList(),
    val sentence: String = "",
)

object WordQuiz {
    private val MCQ = listOf(QType.HANZI_ARTI, QType.ARTI_HANZI, QType.HANZI_PINYIN, QType.PINYIN_HANZI, QType.NADA)

    fun isPureHanzi(w: Word) = w.hanzi.isNotEmpty() && w.hanzi.all { it in '一'..'鿿' }
    private val sentenceOk = { w: Word -> Sentence.chunks(w.example).size >= 3 }
    val writable = { w: Word -> isPureHanzi(w) && w.hanzi.length <= 4 }

    /**
     * Kumpulan kata. Level 0 = gabungan HSK 1–4; untuk sumber "Semua" diambil bergiliran dari tiap level
     * supaya benar-benar campur. "Sering Salah" mengutamakan kata yang paling sering keliru.
     */
    fun pool(st: AppState, level: Int, source: WordSource, n: Int, accept: (Word) -> Boolean = { true }): List<Word> {
        // level 0 = semua; 1..4 = satu level; negatif = HSK 1 sampai -level (Kuis Kilat mengikuti rank).
        val base = Words.all.filter { (level == 0 || it.level == level || (level < 0 && it.level <= -level)) && accept(it) }
        return when (source) {
            WordSource.SEMUA -> if (level != 0) base.shuffled().take(n) else {
                val per = (1..4).map { lv -> base.filter { it.level == lv }.shuffled() }
                val out = ArrayList<Word>()
                var i = 0
                while (out.size < n && per.any { i < it.size }) {
                    per.shuffled().forEach { l -> if (i < l.size && out.size < n) out += l[i] }
                    i++
                }
                out.shuffled()
            }
            WordSource.DIPELAJARI -> base.filter { st.card(it.id).seen }.shuffled().take(n)
            WordSource.BINTANG -> base.filter { st.card(it.id).star }.shuffled().take(n)
            WordSource.SALAH -> base.filter { st.card(it.id).wrong > 0 }
                .sortedWith(compareByDescending<Word> { st.card(it.id).wrong }.thenBy { st.card(it.id).right })
                .take(n).shuffled()
        }
    }

    fun available(mode: PracticeMode, st: AppState, level: Int, source: WordSource): Int = when (mode) {
        PracticeMode.SUSUN -> pool(st, level, source, Int.MAX_VALUE, sentenceOk).size
        PracticeMode.TULIS -> pool(st, level, source, Int.MAX_VALUE, writable).size
        else -> pool(st, level, source, Int.MAX_VALUE).size
    }

    // ---------- Pembuat soal ----------
    private fun syllables(py: String) = Pinyin.split(py).count { p -> p.any { it.isLetter() } }
    private fun plainKey(py: String) = Pinyin.plain(py).replace(" ", "")

    private fun toneVariants(w: Word): List<String> {
        val parts = Pinyin.split(w.pinyin)
        val idx = parts.indices.filter { i -> parts[i].any { it.isLetter() } }
        val out = linkedSetOf(w.pinyin)
        var guard = 0
        while (out.size < 4 && guard++ < 40 && idx.isNotEmpty()) {
            val p = parts.toMutableList()
            val n = if (idx.size > 1 && guard % 2 == 0) 2 else 1
            idx.shuffled().take(n).forEach { i ->
                val cur = Pinyin.toneOfSyllable(p[i])
                p[i] = Pinyin.withTone(p[i], (1..4).filter { it != cur }.random())
            }
            out += p.joinToString("")
        }
        if (out.size < 4) Words.all.shuffled().forEach { if (out.size < 4) out += it.pinyin }
        return out.toList()
    }

    /** Pengecoh yang mirip (huruf awal / jumlah suku kata / level sama) tetapi tidak ambigu. */
    private fun similarWords(w: Word, valid: (Word) -> Boolean): List<Word> {
        val key = plainKey(w.pinyin)
        val syl = syllables(w.pinyin)
        return Words.all.asSequence()
            .filter { it.id != w.id && valid(it) }
            .map { o ->
                var s = 0
                if (plainKey(o.pinyin).firstOrNull() == key.firstOrNull()) s += 2
                if (syllables(o.pinyin) == syl) s += 2
                if (o.hanzi.length == w.hanzi.length) s += 2
                if (o.level == w.level) s += 1
                o to s + Random.nextInt(3)
            }
            .sortedByDescending { it.second }
            .map { it.first }
            .take(30).toList().shuffled()
    }

    fun makeQuestion(w: Word, type: QType): Question? {
        val opts: List<String> = when (type) {
            QType.HANZI_ARTI -> {
                val d = Words.all.filter { it.level == w.level && it.id != w.id }.shuffled() + Words.all.shuffled()
                listOf(w.main) + d.map { it.main }.filter { it != w.main }.distinct().take(3)
            }
            QType.ARTI_HANZI -> {
                val d = Words.all.filter { it.level == w.level && it.id != w.id }.shuffled() + Words.all.shuffled()
                listOf(w.hanzi) + d.filter { it.main != w.main }.map { it.hanzi }.filter { it != w.hanzi }.distinct().take(3)
            }
            QType.HANZI_PINYIN -> {
                val key = plainKey(w.pinyin)
                listOf(w.pinyin) + similarWords(w) { plainKey(it.pinyin) != key }.map { it.pinyin }.distinct().take(3)
            }
            QType.PINYIN_HANZI -> {
                val py = w.pinyin.lowercase()
                listOf(w.hanzi) + similarWords(w) { it.pinyin.lowercase() != py && it.hanzi != w.hanzi }.map { it.hanzi }.distinct().take(3)
            }
            QType.NADA -> toneVariants(w)
            QType.SUSUN -> {
                val ch = Sentence.chunks(w.example)
                if (ch.size < 3) return null
                var sh = ch.shuffled()
                var guard = 0
                while (sh == ch && guard++ < 10) sh = ch.shuffled()
                return Question(w, type, sh, 0, ch, w.example)
            }
        }
        if (opts.size < 4) return null
        val correct = opts[0]
        val sh = opts.shuffled()
        return Question(w, type, sh, sh.indexOf(correct))
    }

    fun build(mode: PracticeMode, st: AppState, level: Int, source: WordSource, count: Int): List<Question> = when (mode) {
        // Kilat: cukup soal untuk durasi terpanjang ([count] soal); kosakata diulang kalau habis.
        PracticeMode.KILAT -> buildList {
            while (size < count) {
                val batch = pool(st, level, source, count - size).mapNotNull { makeQuestion(it, MCQ.random()) }
                if (batch.isEmpty()) break
                addAll(batch)
            }
        }
        PracticeMode.SUSUN -> pool(st, level, source, count, sentenceOk).mapNotNull { makeQuestion(it, QType.SUSUN) }
        PracticeMode.UJIAN -> {
            val plan = listOf(
                QType.HANZI_ARTI to 4, QType.ARTI_HANZI to 3, QType.HANZI_PINYIN to 3,
                QType.PINYIN_HANZI to 3, QType.NADA to 3, QType.SUSUN to 4,
            )
            val words = pool(st, level, source, 60).toMutableList()
            val out = ArrayList<Question>()
            for ((t, n) in plan) {
                var made = 0
                val iter = words.iterator()
                while (iter.hasNext() && made < n) {
                    val q = makeQuestion(iter.next(), t) ?: continue
                    out += q; iter.remove(); made++
                }
            }
            out.shuffled()
        }
        else -> {
            val t = when (mode) {
                PracticeMode.HANZI_ARTI -> QType.HANZI_ARTI
                PracticeMode.ARTI_HANZI -> QType.ARTI_HANZI
                PracticeMode.HANZI_PINYIN -> QType.HANZI_PINYIN
                PracticeMode.PINYIN_HANZI -> QType.PINYIN_HANZI
                else -> QType.NADA
            }
            pool(st, level, source, count).mapNotNull { makeQuestion(it, t) }
        }
    }

    /** Pinyin yang dipakai lebih dari satu kata (perlu arti supaya soal Tulis Hanzi tidak ambigu). */
    private fun pyKey(py: String) = py.lowercase().replace(" ", "")
    private val ambiguous: Set<String> by lazy {
        Words.all.groupBy { pyKey(it.pinyin) }.filterValues { it.size > 1 }.keys
    }
    fun isAmbiguous(w: Word): Boolean = pyKey(w.pinyin) in ambiguous
}
