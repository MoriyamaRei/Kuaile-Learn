package com.nesxha.readit.data

/** Satu lencana: [progress] mengembalikan (nilai sekarang, target). */
class Badge(
    val id: String,
    val emoji: String,
    val title: String,
    val desc: String,
    val progress: (AppState) -> Pair<Int, Int>,
) {
    fun unlocked(st: AppState): Boolean = progress(st).let { it.first >= it.second }
}

object Badges {
    private fun levelDone(lv: Int) = { st: AppState ->
        st.seenCount(lv) to Words.all.count { it.level == lv }
    }

    val all: List<Badge> = listOf(
        Badge("streak3", "🔥", "Mulai Menyala", "Streak 3 hari", { it.bestStreak to 3 }),
        Badge("streak7", "🔥", "Seminggu Penuh", "Streak 7 hari", { it.bestStreak to 7 }),
        Badge("streak30", "☄️", "Sebulan Tanpa Henti", "Streak 30 hari", { it.bestStreak to 30 }),
        Badge("streak100", "🌋", "Api Abadi", "Streak 100 hari", { it.bestStreak to 100 }),
        Badge("kata10", "🌱", "Langkah Pertama", "Pelajari 10 kata", { it.seenCount() to 10 }),
        Badge("kata100", "🌿", "Seratus Kata", "Pelajari 100 kata", { it.seenCount() to 100 }),
        Badge("kata500", "🌳", "Kosakata Lebat", "Pelajari 500 kata", { it.seenCount() to 500 }),
        Badge("kata1200", "🏯", "Penakluk HSK 1–4", "Pelajari semua 1200 kata", { it.seenCount() to Words.all.size }),
        Badge("hafal50", "🧠", "Ingatan Kuat", "Hafal 50 kata", { it.masteredCount() to 50 }),
        Badge("hafal300", "🎓", "Ahli Hafalan", "Hafal 300 kata", { it.masteredCount() to 300 }),
        Badge("hsk1", "🟢", "Lulus Materi HSK 1", "Pelajari semua kata HSK 1", levelDone(1)),
        Badge("hsk2", "🔵", "Lulus Materi HSK 2", "Pelajari semua kata HSK 2", levelDone(2)),
        Badge("hsk3", "🟠", "Lulus Materi HSK 3", "Pelajari semua kata HSK 3", levelDone(3)),
        Badge("hsk4", "🟣", "Lulus Materi HSK 4", "Pelajari semua kata HSK 4", levelDone(4)),
        // Rekor lama (jumlah benar) tetap diakui supaya lencana yang sudah didapat tidak hilang.
        Badge("kilat10", "⚡", "Gerak Cepat", "Skor 50 di Kuis Kilat", { maxOf(it.counter(Counter.BEST_KILAT_SCORE), if (it.counter(Counter.BEST_KILAT) >= 10) 50 else 0) to 50 }),
        Badge("kilat25", "🌩️", "Secepat Kilat", "Skor 150 di Kuis Kilat", { maxOf(it.counter(Counter.BEST_KILAT_SCORE), if (it.counter(Counter.BEST_KILAT) >= 25) 150 else 0) to 150 }),
        Badge("tulis10", "✍️", "Tangan Terampil", "Tulis 10 hanzi dengan benar", { it.counter(Counter.WRITE_OK) to 10 }),
        Badge("tulis100", "🖌️", "Kaligrafer Muda", "Tulis 100 hanzi dengan benar", { it.counter(Counter.WRITE_OK) to 100 }),
        Badge("susun20", "🧩", "Penyusun Kalimat", "Susun 20 kalimat dengan benar", { it.counter(Counter.SENTENCE_OK) to 20 }),
        Badge("ujian1", "📝", "Lulus Ujian", "Lulus satu Ujian HSK", { it.counter(Counter.EXAM_PASS) to 1 }),
        Badge("ujian100", "💯", "Nilai Sempurna", "Skor 100% di Ujian HSK", { it.counter(Counter.EXAM_PERFECT) to 1 }),
        Badge("pulih", "🕯️", "Bangkit Lagi", "Pulihkan streak yang padam", { it.counter(Counter.RESTORES) to 1 }),
        Badge("angka100", "🔢", "Ahli Angka", "Jawab 100 soal Kuis Angka dengan benar", { it.counter(Counter.NUM_OK) to 100 }),
        Badge("diskon20", "🏷️", "Pemburu Diskon", "Jawab 20 soal diskon dengan benar", { it.counter(Counter.numOk(NumCat.DISKON)) to 20 }),
        Badge("jam20", "⏰", "Tepat Waktu", "Jawab 20 soal jam dengan benar", { it.counter(Counter.numOk(NumCat.JAM)) to 20 }),
        Badge("lv5", "⭐", "Bintang Baru", "Capai level 5", { it.levelInfo.level to 5 }),
        Badge("lv10", "🌟", "Bersinar", "Capai level 10", { it.levelInfo.level to 10 }),
        Badge("lv25", "💫", "Makin Terang", "Capai level 25", { it.levelInfo.level to 25 }),
        Badge("lv50", "🏅", "Setengah Abad", "Capai level 50", { it.levelInfo.level to 50 }),
        Badge("lv100", "👑", "Raja Hanzi", "Capai level 100", { it.levelInfo.level to 100 }),
    )

    val byId: Map<String, Badge> = all.associateBy { it.id }
}
