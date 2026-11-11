package com.nesxha.readit.data

import com.nesxha.readit.util.Pinyin
import com.nesxha.readit.json.JSONObject
import kotlin.concurrent.Volatile
import com.nesxha.readit.platform.syncOn

data class Sense(val pos: String, val mean: String, val example: String, val examplePinyin: String = "")

data class Theme(val id: String, val name: String, val emoji: String)

data class Word(
    val id: Int,
    val hanzi: String,
    val pinyin: String,
    val level: Int,
    val theme: String,
    val book: Int,          // 0 = tidak ada di HSK 4下; 11..20 = bab di buku
    val senses: List<Sense>,
) {
    /** Arti utama, huruf pertama kapital supaya rapi. */
    val main: String get() = senses.first().mean.capFirst()
    val others: List<String> get() = senses.drop(1).map { it.mean.capFirst() }.distinct().filter { it != main }
    val pos: String get() = senses.first().pos
    val example: String get() = senses.first().example
    val pinyinPlain: String = Pinyin.plain(pinyin)
    val searchMeaning: String = senses.joinToString(" ") { it.mean }.lowercase()
}

fun String.capFirst(): String = if (isEmpty()) this else this[0].uppercaseChar() + substring(1)

/** Nama jenis kata dalam bahasa Indonesia. */
fun posName(p: String): String = when (p.trim().trimEnd('.').lowercase()) {
    "n" -> "Kata Benda"
    "v" -> "Kata Kerja"
    "adj" -> "Kata Sifat"
    "adv" -> "Kata Keterangan"
    "pron" -> "Kata Ganti"
    "num" -> "Kata Bilangan"
    "nm", "vm", "m", "nm./vm" -> "Kata Satuan"
    "conj" -> "Kata Sambung"
    "prep" -> "Kata Depan"
    "aux" -> "Kata Bantu"
    "sa", "mp" -> "Partikel"
    "int" -> "Kata Seru"
    "sv" -> "Verba + Objek"
    "" -> "Ungkapan"
    else -> p
}

object Words {
    lateinit var all: List<Word>; private set
    lateinit var themes: Map<String, Theme>; private set
    lateinit var byId: Map<Int, Word>; private set
    /** Urutan tema di layar (kelompok makna dulu, lalu kelompok jenis kata). */
    val themeOrder = listOf(
        "keluarga", "profesi", "tubuh", "makanan", "waktu", "tempat", "sekolah", "uang", "rumah", "transport",
        "alam", "hobi", "perasaan", "komunikasi", "masyarakat", "konsep", "angka", "ganti",
        "kerja", "sifat", "keterangan", "depan", "sambung", "bantu", "partikel",
    )
    @Volatile private var loaded = false

    val isLoaded: Boolean get() = loaded

    /** Muat sekali dari teks words.json (Android: dari assets; web: dari berkas situs). */
    fun init(readText: () -> String) {
        if (loaded) return
        syncOn(this) {
            if (loaded) return
            parse(readText())
        }
    }

    /** Muat data dari teks JSON (dipakai juga oleh tes otomatis tanpa Android). */
    fun parse(txt: String) {
        syncOn(this) {
            val root = JSONObject(txt)
            val th = root.getJSONObject("themes")
            val tm = mutableMapOf<String, Theme>()
            th.keys().forEach { k ->
                val o = th.getJSONObject(k)
                tm[k] = Theme(k, o.getString("name"), o.getString("emoji"))
            }
            val arr = root.getJSONArray("words")
            val list = ArrayList<Word>(arr.length())
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                val ss = o.getJSONArray("s")
                val senses = (0 until ss.length()).map {
                    val s = ss.getJSONObject(it)
                    Sense(s.getString("p"), s.getString("m"), s.getString("e"), s.optString("ep"))
                }
                list += Word(o.getInt("id"), o.getString("h"), o.getString("py"), o.getInt("lv"), o.getString("t"), o.optInt("b", 0), senses)
            }
            all = list
            byId = list.associateBy { it.id }
            themes = tm
            loaded = true
        }
    }

    fun theme(id: String): Theme = themes[id] ?: Theme(id, id, "📘")

    /** Urutan belajar kata baru. */
    fun studyOrder(byTheme: Boolean): List<Word> =
        if (!byTheme) all.sortedBy { it.id }
        else all.sortedWith(compareBy<Word>({ it.level }, { themeOrder.indexOf(it.theme).let { i -> if (i < 0) 99 else i } }, { it.id }))
}
