@file:OptIn(ExperimentalJsExport::class)

package com.nesxha.readit.web

import com.nesxha.readit.data.NumCat
import com.nesxha.readit.data.NumQuestion
import com.nesxha.readit.data.NumberLessons
import com.nesxha.readit.data.NumberQuiz
import com.nesxha.readit.util.ChineseNumber
import com.nesxha.readit.util.Pinyin
import com.nesxha.readit.util.Spoken

/*
 * Jembatan logika Kuaile Learn (Kotlin) ke versi web (JavaScript).
 * Kode angka, materi, dan pinyin dipakai persis sama dengan aplikasi Android.
 * Semua fungsi mengembalikan teks JSON supaya mudah dibaca di JavaScript.
 */

private fun q(s: String?): String {
    if (s == null) return "null"
    val sb = StringBuilder("\"")
    for (c in s) when (c) {
        '"' -> sb.append("\\\""); '\\' -> sb.append("\\\\"); '\n' -> sb.append("\\n"); '\r' -> sb.append("\\r"); '\t' -> sb.append("\\t")
        else -> if (c.code < 0x20) sb.append("\\u" + c.code.toString(16).padStart(4, '0')) else sb.append(c)
    }
    return sb.append('"').toString()
}

private fun arr(items: List<String>) = items.joinToString(",", "[", "]")
private fun obj(vararg kv: Pair<String, String>) = kv.joinToString(",", "{", "}") { "${q(it.first)}:${it.second}" }

private fun spoken(s: Spoken) = obj("h" to q(s.hanzi), "p" to q(s.pinyin), "syls" to arr(s.syls.map { obj("h" to q(it.h), "p" to q(it.p)) }))

private fun cat(id: String): NumCat = NumCat.entries.firstOrNull { it.name == id } ?: NumCat.UMUM

@JsExport
fun numCats(): String = arr(NumCat.entries.map { obj("id" to q(it.name), "title" to q(it.title), "sub" to q(it.sub), "emoji" to q(it.emoji)) })

@JsExport
fun numRanges(): String = arr(NumberQuiz.ranges.map { obj("label" to q(it.first), "lo" to "${it.second.first}", "hi" to "${it.second.last}") })

private fun question(x: NumQuestion) = obj(
    "cat" to q(x.cat.name), "instruction" to q(x.instruction), "hanzi" to q(x.hanzi), "pinyin" to q(x.pinyin), "display" to q(x.display),
    "options" to arr(x.options.map { obj("text" to q(it.text), "pinyin" to q(it.pinyin), "chinese" to "${it.chinese}") }),
    "answer" to "${x.answer}", "accepted" to arr(x.accepted.map { q(it) }), "answerText" to q(x.answerText),
    "keys" to q(x.keys.name), "extra" to q(x.keys.extra), "unit" to q(x.unit), "explain" to q(x.explain), "story" to "${x.story}",
)

/** Membuat [n] soal kuis angka. [lo]/[hi] < 0 = jarak bawaan tingkat kesulitan. */
@JsExport
fun numBuild(catId: String, difficulty: Int, n: Int, lo: Double, hi: Double): String {
    val range = if (lo >= 0 && hi >= lo) lo.toLong()..hi.toLong() else null
    return arr(NumberQuiz.build(cat(catId), difficulty, n, range).map { question(it) })
}

@JsExport
fun numNorm(raw: String): String = NumberQuiz.norm(raw)

@JsExport
fun lesson(catId: String): String = arr(NumberLessons.full(cat(catId)).map { r ->
    obj(
        "title" to q(r.title), "body" to q(r.body),
        "examples" to arr(r.examples.map { obj("s" to spoken(it.first), "m" to q(it.second)) }),
        "table" to arr(r.table.map { obj("s" to spoken(it.first), "m" to q(it.second)) }),
        "formula" to q(r.formula), "steps" to arr(r.steps.map { q(it) }), "stepsTitle" to q(r.stepsTitle),
    )
})

@JsExport
fun culture(): String = arr(NumberLessons.culture.map { q(it) })

/** Pengubah angka: bacaan Mandarin dan langkah-langkahnya. */
@JsExport
fun convert(n: Double): String {
    val v = n.toLong()
    return obj("s" to spoken(ChineseNumber.number(v)), "steps" to arr(ChineseNumber.steps(v).map { q(it) }), "fmt" to q(ChineseNumber.fmt(v)))
}

@JsExport
fun fmt(n: Double): String = ChineseNumber.fmt(n.toLong())

/** Potongan pinyin berwarna nada (aturan sama dengan aplikasi). */
@JsExport
fun pySegments(py: String, hanzi: String): String = arr(Pinyin.segments(py, hanzi).map { obj("t" to q(it.text), "tone" to "${it.tone}") })

@JsExport
fun pyPlain(py: String): String = Pinyin.plain(py)

@JsExport
fun pyAligned(py: String, hanzi: String): Boolean = Pinyin.aligned(py, hanzi)

@JsExport
fun pySplit(py: String): String = arr(Pinyin.split(py).map { q(it) })

@JsExport
fun pyWithTone(syllable: String, tone: Int): String = Pinyin.withTone(syllable, tone)

@JsExport
fun pyToneOf(syllable: String): Int = Pinyin.toneOfSyllable(syllable)
