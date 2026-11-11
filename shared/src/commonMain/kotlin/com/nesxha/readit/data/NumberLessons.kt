package com.nesxha.readit.data

import com.nesxha.readit.util.ChineseNumber as C
import com.nesxha.readit.util.Spoken

data class LessonRule(
    val title: String,
    val body: String,
    val examples: List<Pair<Spoken, String>> = emptyList(),
    val table: List<Pair<Spoken, String>> = emptyList(),   // ditampilkan sebagai kisi kartu yang bisa diketuk
    val formula: String? = null,                           // rumus, ditampilkan dalam kotak khusus
    val steps: List<String> = emptyList(),                 // contoh pengerjaan langkah demi langkah
    val stepsTitle: String? = null,
)

/** Materi singkat per kategori kuis angka. */
object NumberLessons {
    private fun ex(sp: Spoken, meaning: String) = sp to meaning

    fun of(cat: NumCat): List<LessonRule> = when (cat) {
        NumCat.UMUM, NumCat.CAMPUR -> listOf(
            LessonRule("Angka Dasar", "0–10 adalah dasarnya. Puluhan = angka + 十 + angka.", listOf(
                ex(C.number(10), "10"), ex(C.number(25), "25"), ex(C.number(99), "99"))),
            LessonRule("Hitungan Per 4 Digit", "Bahasa Indonesia mengelompokkan per 3 digit (ribu, juta). Mandarin per 4 digit: 万 = 10.000 dan 亿 = 100.000.000.", listOf(
                ex(C.number(10_000), "10.000"), ex(C.number(100_000), "100.000 (sepuluh 万)"), ex(C.number(1_000_000), "1.000.000 (seratus 万)"), ex(C.number(100_000_000), "100.000.000"))),
            LessonRule("零 untuk Digit Kosong", "Kalau ada digit 0 di tengah, tulis 零 sekali saja, walau nolnya lebih dari satu. Nol di akhir tidak dibaca.", listOf(
                ex(C.number(105), "105"), ex(C.number(1005), "1.005"), ex(C.number(1050), "1.050"), ex(C.number(30508), "30.508"))),
            LessonRule("两 atau 二", "Angka 2 sebelum 千, 万, 亿 (dan 百 di awal) dibaca 两. Di puluhan dan satuan tetap 二.", listOf(
                ex(C.number(2000), "2.000"), ex(C.number(20000), "20.000"), ex(C.number(22), "22"), ex(C.number(12), "12"))),
            LessonRule("十 atau 一十", "Di awal angka cukup 十 (十五 = 15). Di tengah angka harus lengkap 一十.", listOf(
                ex(C.number(15), "15"), ex(C.number(115), "115"), ex(C.number(110_000), "110.000"))),
            LessonRule("Nada 一 Berubah", "一 dibaca yì sebelum 百/千 dan yí sebelum 万/亿. Di akhir angka tetap yī.", listOf(
                ex(C.number(100), "100"), ex(C.number(10_000), "10.000"), ex(C.number(101), "101"))),
        )
        NumCat.HARGA -> listOf(
            LessonRule("Lisan: 块, 毛, 分", "Saat bicara: 块 = yuan, 毛 = 0,1 yuan, 分 = 0,01 yuan. Satuan terakhir boleh dihilangkan.", listOf(
                ex(C.moneySpoken(12, 5, 0), "¥12,50"), ex(C.moneySpoken(12, 3, 5), "¥12,35"), ex(C.moneySpoken(0, 5, 0), "¥0,50"))),
            LessonRule("Tertulis: 元, 角, 分", "Di label harga dan kuitansi dipakai 元 dan 角.", listOf(
                ex(C.moneyWritten(12, 5, 0), "¥12,50"), ex(C.moneyWritten(3, 0, 5), "¥3,05"))),
            LessonRule("Hati-hati: 块五 dan 块零五", "一块五 = ¥1,50, sedangkan 一块零五分 = ¥1,05. 零 menandai 毛 yang kosong.", listOf(
                ex(C.moneySpoken(1, 5, 0), "¥1,50"), ex(C.moneySpoken(1, 0, 5), "¥1,05"))),
            LessonRule("两块, Bukan 二块", "Sebelum kata satuan, angka 2 menjadi 两.", listOf(
                ex(C.moneySpoken(2, 0, 0), "¥2"), ex(C.moneySpoken(0, 2, 0), "¥0,20"), ex(C.moneySpoken(22, 0, 0), "¥22"))),
        )
        NumCat.DISKON -> listOf(
            LessonRule("打折 = Bayar Sekian Persen", "Angka di depan 折 adalah yang DIBAYAR. 打八折 = bayar 80% (diskon 20%), bukan diskon 80%!", listOf(
                ex(Spoken.of("打" to "dǎ", "八" to "bā", "折" to "zhé"), "Bayar 80%"), ex(Spoken.of("打" to "dǎ", "五" to "wǔ", "折" to "zhé"), "Bayar 50%"))),
            LessonRule("Dua Digit: X五折", "八五折 = bayar 85%, 九五折 = bayar 95% (diskon 5%).", listOf(
                ex(Spoken.of("打" to "dǎ", "八" to "bā", "五" to "wǔ", "折" to "zhé"), "Bayar 85%"), ex(Spoken.of("打" to "dǎ", "九" to "jiǔ", "五" to "wǔ", "折" to "zhé"), "Bayar 95%"))),
            LessonRule("半价 dan 原价", "半价 = setengah harga. 原价 = harga asli sebelum diskon.", listOf(
                ex(Spoken.of("半" to "bàn", "价" to "jià"), "Setengah harga"), ex(Spoken.of("原" to "yuán", "价" to "jià"), "Harga asli"))),
            LessonRule("满减", "满200减50 = setiap belanja minimal ¥200, potong ¥50. Kalau belanjanya kurang dari ¥200, tidak ada potongan.", listOf(
                ex(Spoken.of("满" to "mǎn") + C.number(200) + Spoken.of("减" to "jiǎn") + C.number(50), "Belanja ≥ ¥200, potong ¥50"))),
        )
        NumCat.PERSEN -> listOf(
            LessonRule("Persen: 百分之…", "\"Per seratus\" diucapkan dulu, baru angkanya.", listOf(
                ex(C.percent(30), "30%"), ex(C.percent(100), "100%"), ex(C.percent(0, "5"), "0,5%"))),
            LessonRule("Pecahan: Penyebut Dulu", "三分之一 = 1/3: \"dari 3 bagian, ambil 1\". Urutannya kebalikan bahasa Indonesia.", listOf(
                ex(C.fraction(1, 3), "1/3"), ex(C.fraction(3, 4), "3/4"), ex(C.fraction(1, 2), "1/2"))),
            LessonRule("Desimal: 点", "Koma dibaca 点, angka di belakangnya dibaca satu per satu.", listOf(
                ex(C.decimal(3, "14"), "3,14"), ex(C.decimal(0, "5"), "0,5"), ex(C.decimal(12, "05"), "12,05"))),
        )
        NumCat.JAM -> listOf(
            LessonRule("点 = Jam", "Jam bulat cukup angka + 点. Jam 2 dibaca 两点.", listOf(
                ex(C.time(8, 0), "8:00"), ex(C.time(2, 0), "2:00"), ex(C.time(12, 0), "12:00"))),
            LessonRule("半 dan 刻", "半 = :30. 刻 = 15 menit: 一刻 = :15, 三刻 = :45.", listOf(
                ex(C.time(8, 30, 1), "8:30"), ex(C.time(9, 15, 1), "9:15"), ex(C.time(9, 45, 1), "9:45"))),
            LessonRule("分 dan 零", "Menit biasa = angka + 分. Menit 1–9 diberi 零.", listOf(
                ex(C.time(10, 20), "10:20"), ex(C.time(8, 5), "8:05"))),
            LessonRule("差 = Kurang", "差五分九点 = kurang 5 menit jam 9 = 8:55.", listOf(
                ex(C.time(8, 55, 2), "8:55"), ex(C.time(9, 45, 2), "9:45"))),
            LessonRule("Keterangan Waktu", "凌晨 dini hari, 上午 pagi, 中午 siang, 下午 sore, 晚上 malam.", listOf(
                ex(Spoken.of("下" to "xià", "午" to "wǔ") + C.time(3, 0), "15:00"), ex(Spoken.of("晚" to "wǎn", "上" to "shang") + C.time(8, 0), "20:00"))),
        )
        NumCat.TANGGAL -> listOf(
            LessonRule("Urutan: Tahun → Bulan → Tanggal", "Kebalikan bahasa Indonesia. Tahun dibaca per digit.", listOf(
                ex(C.date(2026, 11, 4), "4 November 2026"), ex(C.date(1998, 2, 14, hao = false), "14 Februari 1998"))),
            LessonRule("号 dan 日", "号 dipakai saat bicara, 日 dalam tulisan resmi. Artinya sama.", listOf(
                ex(C.number(4) + Spoken.of("号" to "hào"), "Tanggal 4"))),
            LessonRule("Hari: 星期", "星期 + angka 1–6. Minggu = 星期天 atau 星期日. Bisa juga 周一 atau 礼拜一.", listOf(
                ex(C.weekday(1), "Senin"), ex(C.weekday(6), "Sabtu"), ex(C.weekday(7), "Minggu"))),
            LessonRule("Hari Relatif", "前天 2 hari lalu, 昨天 kemarin, 今天 hari ini, 明天 besok, 后天 lusa, 大后天 3 hari lagi.", listOf(
                ex(Spoken.of("后" to "hòu", "天" to "tiān"), "Lusa"))),
        )
        NumCat.TELEPON -> listOf(
            LessonRule("Dibaca Per Digit", "Nomor telepon, kamar, bus, dan tahun dibaca satu per satu.", listOf(
                ex(C.digits("302"), "Kamar 302"), ex(C.digits("2026"), "Tahun 2026"))),
            LessonRule("1 = 幺 (yāo)", "Di nomor telepon, 1 dibaca yāo supaya tidak tertukar dengan 七 (qī).", listOf(
                ex(C.digits("110", yao = true, zero = "零"), "110 (polisi)"), ex(C.digits("120", yao = true, zero = "零"), "120 (ambulans)"), ex(C.digits("119", yao = true, zero = "零"), "119 (pemadam)"))),
        )
        NumCat.UKURAN -> listOf(
            LessonRule("Umur: 岁", "Umur = angka + 岁. 年 artinya tahun kalender, bukan umur.", listOf(
                ex(C.count(25, "岁", "suì", 4), "25 tahun"), ex(C.count(2, "岁", "suì", 4), "2 tahun"))),
            LessonRule("Kata Satuan: 个", "Benda dan orang dihitung dengan kata satuan. 2 menjadi 两.", listOf(
                ex(C.count(2, "个", "gè", 4) + Spoken.of("人" to "rén"), "2 orang"), ex(C.count(1, "个", "gè", 4) + Spoken.of("人" to "rén"), "1 orang"))),
            LessonRule("Tinggi: 一米七五", "Meter + sisanya per digit. 一米七 = 1,70 m.", listOf(
                ex(Spoken.of("一" to "yì", "米" to "mǐ", "七" to "qī"), "1,70 m"), ex(Spoken.of("一" to "yì", "米" to "mǐ", "六" to "liù", "五" to "wǔ"), "1,65 m"))),
            LessonRule("Berat: 斤 dan 公斤", "公斤 = kilogram. Di pasar dipakai 斤 = ½ kg.", listOf(
                ex(C.count(2, "斤", "jīn", 1), "1 kg"), ex(C.count(1, "公", "gōng", 1) + Spoken.of("斤" to "jīn"), "1 kg"))),
            LessonRule("Lantai: 楼", "Lantai = angka + 楼. Karena urutan, 2 tetap 二楼.", listOf(
                ex(C.number(2) + Spoken.of("楼" to "lóu"), "Lantai 2"))),
        )
        NumCat.KIRA -> listOf(
            LessonRule("多 = Lebih", "Angka + 多 = \"lebih\".", listOf(
                ex(Spoken.of("十" to "shí", "多" to "duō", "个" to "gè"), "Belasan (11–19)"), ex(C.number(100) + Spoken.of("多" to "duō"), "Seratus lebih"))),
            LessonRule("几 = Beberapa", "几 sendiri = beberapa (di bawah 10). 几十 = puluhan, 几百 = ratusan.", listOf(
                ex(Spoken.of("几" to "jǐ", "个" to "gè"), "Beberapa"), ex(Spoken.of("几" to "jǐ", "十" to "shí"), "Puluhan"))),
            LessonRule("Dua Angka Berurutan", "三四个 = 3 atau 4. 二三十 = sekitar 20–30.", listOf(
                ex(Spoken.of("三" to "sān", "四" to "sì", "个" to "gè"), "3 atau 4"), ex(Spoken.of("二" to "èr", "三" to "sān", "十" to "shí"), "20–30"))),
        )
    }

    private fun sp(vararg p: Pair<String, String>) = Spoken.of(*p)

    /** Materi lengkap: tabel pengantar + aturan + jebakan yang sering salah. */
    fun full(cat: NumCat): List<LessonRule> {
        val c = if (cat == NumCat.CAMPUR) NumCat.UMUM else cat
        return intro(c) + theory(c) + of(c) + traps(c)
    }

    private fun intro(cat: NumCat): List<LessonRule> = when (cat) {
        NumCat.UMUM, NumCat.CAMPUR -> listOf(
            LessonRule("Angka 0–10", "Hafalkan sebelas angka ini dulu. Semua angka lain dirangkai dari sini. Ketuk kartu untuk mendengar.",
                table = (0..10).map { C.number(it) to "$it" }),
            LessonRule("Satuan Besar", "Perhatikan padanannya dalam bahasa Indonesia, terutama 万 dan 亿.",
                table = listOf(
                    C.number(10) to "10", sp("百" to "bǎi") to "100", sp("千" to "qiān") to "1.000", sp("万" to "wàn") to "10.000",
                    C.number(100_000) to "100.000", sp("百" to "bǎi", "万" to "wàn") to "1 juta", sp("千" to "qiān", "万" to "wàn") to "10 juta",
                    sp("亿" to "yì") to "100 juta", sp("十" to "shí", "亿" to "yì") to "1 miliar",
                )),
            LessonRule("Pola Puluhan", "Puluhan = angka + 十. Puluhan + satuan = angka + 十 + angka.",
                table = listOf(20, 30, 40, 50, 60, 70, 80, 90, 21, 35, 48, 99).map { C.number(it) to "$it" }),
        )
        NumCat.HARGA -> listOf(
            LessonRule("Satuan Uang", "Mata uang Tiongkok adalah 人民币 (rénmínbì, RMB), simbolnya ¥.",
                table = listOf(
                    sp("块" to "kuài") to "Yuan (lisan)", sp("元" to "yuán") to "Yuan (tertulis)", sp("毛" to "máo") to "0,1 yuan (lisan)",
                    sp("角" to "jiǎo") to "0,1 yuan (tertulis)", sp("分" to "fēn") to "0,01 yuan", sp("人" to "rén", "民" to "mín", "币" to "bì") to "Rupiah-nya Tiongkok",
                )),
            LessonRule("Bertanya Harga", "Kalimat yang paling sering dipakai saat belanja.",
                examples = listOf(
                    sp("多" to "duō", "少" to "shǎo", "钱" to "qián") to "Berapa harganya?",
                    sp("太" to "tài", "贵" to "guì", "了" to "le") to "Terlalu mahal!",
                    sp("便" to "pián", "宜" to "yi", "一" to "yì", "点" to "diǎn", "儿" to "r") to "Lebih murah sedikit, dong",
                )),
        )
        NumCat.DISKON -> listOf(
            LessonRule("Tabel 折", "Angka 折 = persen yang dibayar. Sisanya adalah potongan.",
                table = listOf(9, 8, 7, 6, 5).map { x -> sp("打" to "dǎ") + Spoken(listOf(C.digit(x))) + sp("折" to "zhé") to "Bayar ${x * 10}% · hemat ${100 - x * 10}%" } +
                    listOf(95, 85, 75).map { x -> sp("打" to "dǎ") + Spoken(listOf(C.digit(x / 10), C.digit(x % 10))) + sp("折" to "zhé") to "Bayar $x% · hemat ${100 - x}%" } +
                    listOf(sp("半" to "bàn", "价" to "jià") to "Bayar 50%")),
            LessonRule("Kata Promo Lain", "Sering muncul di toko dan aplikasi belanja.",
                table = listOf(
                    sp("优" to "yōu", "惠" to "huì") to "Promo / potongan", sp("包" to "bāo", "邮" to "yóu") to "Gratis ongkir",
                    sp("买" to "mǎi", "一" to "yī", "送" to "sòng", "一" to "yī") to "Beli 1 gratis 1", sp("特" to "tè", "价" to "jià") to "Harga spesial",
                )),
        )
        NumCat.PERSEN -> listOf(
            LessonRule("Yang Sering Dipakai", "Ketuk untuk mendengar.",
                table = listOf(
                    C.fraction(1, 2) to "1/2", C.fraction(1, 3) to "1/3", C.fraction(2, 3) to "2/3", C.fraction(1, 4) to "1/4",
                    C.fraction(3, 4) to "3/4", C.percent(50) to "50%", C.percent(100) to "100%", C.decimal(0, "5") to "0,5",
                )),
        )
        NumCat.JAM -> listOf(
            LessonRule("Kata Penting", "Kosakata dasar untuk menyebut waktu.",
                table = listOf(
                    sp("点" to "diǎn") to "Jam (pukul)", sp("分" to "fēn") to "Menit", sp("一" to "yí", "刻" to "kè") to ":15",
                    sp("半" to "bàn") to ":30", sp("三" to "sān", "刻" to "kè") to ":45", sp("差" to "chà") to "Kurang",
                    sp("钟" to "zhōng") to "Tepat (三点钟 = jam 3 tepat)", sp("几" to "jǐ", "点" to "diǎn") to "Jam berapa?",
                )),
            LessonRule("Bagian Hari", "Diletakkan di depan jam: 下午三点 = jam 3 sore.",
                table = listOf(
                    sp("凌" to "líng", "晨" to "chén") to "Dini hari", sp("早" to "zǎo", "上" to "shang") to "Pagi (awal)",
                    sp("上" to "shàng", "午" to "wǔ") to "Pagi–siang", sp("中" to "zhōng", "午" to "wǔ") to "Tengah hari",
                    sp("下" to "xià", "午" to "wǔ") to "Siang–sore", sp("晚" to "wǎn", "上" to "shang") to "Malam",
                )),
        )
        NumCat.TANGGAL -> listOf(
            LessonRule("Nama Hari", "星期 + angka. Minggu memakai 天 atau 日.",
                table = (1..7).map { C.weekday(it) to listOf("Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu", "Minggu")[it - 1] }),
            LessonRule("Nama Bulan", "Angka + 月.",
                table = (1..12).map { C.number(it) + sp("月" to "yuè") to listOf("Januari", "Februari", "Maret", "April", "Mei", "Juni", "Juli", "Agustus", "September", "Oktober", "November", "Desember")[it - 1] }),
            LessonRule("Hari Relatif", "Untuk membicarakan kemarin, besok, dan seterusnya.",
                table = listOf(
                    sp("前" to "qián", "天" to "tiān") to "2 hari lalu", sp("昨" to "zuó", "天" to "tiān") to "Kemarin", sp("今" to "jīn", "天" to "tiān") to "Hari ini",
                    sp("明" to "míng", "天" to "tiān") to "Besok", sp("后" to "hòu", "天" to "tiān") to "Lusa", sp("大" to "dà", "后" to "hòu", "天" to "tiān") to "3 hari lagi",
                )),
        )
        NumCat.TELEPON -> listOf(
            LessonRule("Digit Saat Menyebut Nomor", "Perhatikan angka 1 yang dibaca 幺 (yāo).",
                table = (0..9).map { d -> C.digits("$d", yao = true, zero = "零") to "$d" }),
            LessonRule("Bertanya Nomor", "Kalimat yang sering dipakai.",
                examples = listOf(
                    sp("你" to "nǐ", "的" to "de", "电" to "diàn", "话" to "huà", "号" to "hào", "码" to "mǎ", "是" to "shì", "多" to "duō", "少" to "shao") to "Nomor teleponmu berapa?",
                    sp("你" to "nǐ", "住" to "zhù", "几" to "jǐ", "号" to "hào", "房" to "fáng", "间" to "jiān") to "Kamu tinggal di kamar nomor berapa?",
                )),
        )
        NumCat.UKURAN -> listOf(
            LessonRule("Kata Satuan Umum", "Di Mandarin, angka hampir selalu diikuti kata satuan sebelum bendanya.",
                table = listOf(
                    sp("个" to "gè") to "Umum (orang, benda)", sp("本" to "běn") to "Buku", sp("张" to "zhāng") to "Kertas, tiket, meja",
                    sp("杯" to "bēi") to "Gelas", sp("瓶" to "píng") to "Botol", sp("件" to "jiàn") to "Pakaian, urusan",
                    sp("只" to "zhī") to "Hewan kecil", sp("辆" to "liàng") to "Kendaraan", sp("双" to "shuāng") to "Pasang (sepatu, sumpit)",
                )),
            LessonRule("Satuan Ukuran", "Ukuran yang sering dipakai sehari-hari.",
                table = listOf(
                    sp("岁" to "suì") to "Tahun (umur)", sp("米" to "mǐ") to "Meter", sp("公" to "gōng", "里" to "lǐ") to "Kilometer",
                    sp("公" to "gōng", "斤" to "jīn") to "Kilogram", sp("斤" to "jīn") to "½ kilogram", sp("楼" to "lóu") to "Lantai",
                )),
        )
        NumCat.KIRA -> listOf(
            LessonRule("Kata Perkiraan", "Untuk jumlah yang tidak pasti.",
                table = listOf(
                    sp("几" to "jǐ") to "Beberapa", sp("十" to "shí", "几" to "jǐ") to "Belasan", sp("几" to "jǐ", "十" to "shí") to "Puluhan",
                    sp("几" to "jǐ", "百" to "bǎi") to "Ratusan", sp("几" to "jǐ", "千" to "qiān") to "Ribuan", sp("多" to "duō") to "Lebih",
                    sp("左" to "zuǒ", "右" to "yòu") to "Sekitar (di belakang angka)", sp("大" to "dà", "概" to "gài") to "Kira-kira (di depan angka)",
                )),
        )
    }

    private fun theory(cat: NumCat): List<LessonRule> = when (cat) {
        NumCat.UMUM, NumCat.CAMPUR -> listOf(
            LessonRule(
                "Rumus Angka 1–9.999",
                "Setiap posisi (ribuan, ratusan, puluhan, satuan) dibaca sebagai angka + nama posisinya. Posisi yang nol dilewati. Kalau ada posisi nol di antara posisi lain, tulis 零 satu kali saja. Nol di akhir tidak dibaca sama sekali.",
                formula = "[A]千 [B]百 [C]十 [D]\nA = ribuan · B = ratusan · C = puluhan · D = satuan",
                stepsTitle = "Contoh: 3.407",
                steps = listOf("3 ribuan → 三千", "4 ratusan → 四百", "0 puluhan → kosong di tengah, jadi 零", "7 satuan → 七", "Hasil: 三千四百零七 (sān qiān sì bǎi líng qī)"),
            ),
            LessonRule(
                "Rumus Angka Besar",
                "Bahasa Indonesia memberi nama setiap 3 digit (ribu, juta, miliar). Mandarin memberi nama setiap 4 digit: 万 untuk kelompok kedua dan 亿 untuk kelompok ketiga. Setiap kelompok dibaca dengan rumus 1–9.999 di atas, lalu diberi namanya.",
                formula = "[A]亿 [B]万 [C]\nA, B, C = kelompok 4 digit dari kanan",
                stepsTitle = "Contoh: 30.508",
                steps = C.steps(30_508),
            ),
            LessonRule(
                "Contoh Angka Sangat Besar",
                "Cara yang sama berlaku sampai ratusan miliar.",
                stepsTitle = "Contoh: 2.304.050.607",
                steps = C.steps(2_304_050_607),
            ),
            LessonRule(
                "Mengubah Juta dan Miliar",
                "Karena 1 juta = 100 × 10.000, maka 1 juta = 一百万. Jadi jumlah juta dikali 100, lalu diberi 万. Untuk miliar: 1 miliar = 10 × 100.000.000 = 十亿.",
                formula = "N juta = (N × 100)万\nN miliar = (N × 10)亿",
                table = listOf(
                    C.number(1_000_000) to "1 juta", C.number(5_000_000) to "5 juta", C.number(10_000_000) to "10 juta",
                    C.number(50_000_000) to "50 juta", C.number(1_000_000_000) to "1 miliar", C.number(20_000_000_000) to "20 miliar",
                ),
                stepsTitle = "Contoh: 5 juta",
                steps = listOf("5 juta = 5 × 100 = 500 kelompok 万", "500 dibaca 五百", "Hasil: 五百万 (wǔ bǎi wàn)"),
            ),
        )
        NumCat.HARGA -> listOf(
            LessonRule(
                "Rumus Harga Lisan",
                "Yuan dibaca dulu dengan 块, lalu 毛, lalu 分. Satuan paling akhir boleh tidak diucapkan. Kalau 毛 kosong tetapi ada 分, tulis 零 di tengah. Angka 2 sebelum 块/毛 menjadi 两.",
                formula = "[yuan]块 [jiao]毛 [fen]分\n1 块 = 10 毛 = 100 分",
                stepsTitle = "Contoh: ¥36,05",
                steps = listOf("36 yuan → 三十六块", "0 毛 → kosong, jadi 零", "5 分 → 五分", "Hasil: ${C.moneySpoken(36, 0, 5).hanzi} (${C.moneySpoken(36, 0, 5).pinyin})"),
            ),
            LessonRule(
                "Rumus Harga Tertulis",
                "Di label harga, kuitansi, dan bank dipakai satuan resmi 元, 角, 分. Semua satuan ditulis lengkap.",
                formula = "[yuan]元 [jiao]角 [fen]分",
                stepsTitle = "Contoh: ¥12,50",
                steps = listOf("12 yuan → 十二元", "5 jiao → 五角", "Hasil: ${C.moneyWritten(12, 5, 0).hanzi}"),
            ),
            LessonRule(
                "Menghitung Total dan Kembalian",
                "Baca dulu harga satuannya, ubah ke angka, baru dihitung.",
                formula = "Total = harga satuan × jumlah\nKembalian = uang dibayar − total",
                stepsTitle = "Contoh: 一斤苹果两块五, beli 4 斤, bayar ¥20",
                steps = listOf("两块五 = ¥2,50 per 斤", "Total = 2,50 × 4 = ¥10 → 十块", "Kembalian = 20 − 10 = ¥10 → 找你十块"),
            ),
        )
        NumCat.DISKON -> listOf(
            LessonRule(
                "Rumus 打折",
                "Angka di depan 折 menunjukkan berapa persepuluh yang dibayar. Dua digit (X五折) berarti per seratus.",
                formula = "打X折 → Bayar = Harga × X ÷ 10\n打XY折 → Bayar = Harga × XY ÷ 100\nPotongan = Harga − Bayar",
                stepsTitle = "Contoh: 原价三百块，打八折",
                steps = listOf("原价 = harga asli ¥300", "打八折 → bayar 8/10", "300 × 8 ÷ 10 = ¥240", "Potongan = 300 − 240 = ¥60"),
            ),
            LessonRule(
                "Mengubah Diskon Persen ke 折",
                "Kalau toko bilang diskon sekian persen, kurangkan dari 100 dulu.",
                formula = "折 = (100 − diskon%) ÷ 10",
                stepsTitle = "Contoh: diskon 25%",
                steps = listOf("100 − 25 = 75 (persen yang dibayar)", "75 → dua digit, dibaca 七五", "Hasil: 打七五折"),
            ),
            LessonRule(
                "Rumus 满减",
                "Potongan hanya berlaku kalau total belanja mencapai batas minimal.",
                formula = "满A减B:\nKalau Total ≥ A → Bayar = Total − B\nKalau Total < A → Bayar = Total",
                stepsTitle = "Contoh: 满两百减三十, belanja ¥250",
                steps = listOf("Batas minimal A = ¥200, potongan B = ¥30", "250 ≥ 200, jadi dapat potongan", "Bayar = 250 − 30 = ¥220"),
            ),
        )
        NumCat.PERSEN -> listOf(
            LessonRule(
                "Rumus Persen",
                "\"Per seratus\" (百分之) diucapkan lebih dulu, angkanya di belakang.",
                formula = "百分之 N = N%",
                stepsTitle = "Contoh: 百分之四十五",
                steps = listOf("百分之 → persen", "四十五 → 45", "Hasil: 45%"),
            ),
            LessonRule(
                "Rumus Pecahan",
                "分之 berarti \"dari bagian\". Yang di depan 分之 adalah penyebut (jumlah bagian), yang di belakang adalah pembilang.",
                formula = "B 分之 A = A/B\nB = penyebut · A = pembilang",
                stepsTitle = "Contoh: 五分之二",
                steps = listOf("Di depan 分之: 五 → penyebut 5", "Di belakang 分之: 二 → pembilang 2", "Hasil: 2/5"),
            ),
            LessonRule(
                "Rumus Desimal",
                "Bagian bulat dibaca seperti angka biasa. Setelah 点 (koma), setiap digit dibaca satu per satu, termasuk nol.",
                formula = "A 点 B C D = A,BCD\nB, C, D dibaca per digit",
                stepsTitle = "Contoh: 十二点零五",
                steps = listOf("十二 → 12", "点 → koma", "零五 → digit 0 lalu 5", "Hasil: 12,05"),
            ),
        )
        NumCat.JAM -> listOf(
            LessonRule(
                "Rumus Jam",
                "Jam disebut dulu dengan 点, lalu menitnya dengan 分. Menit 1–9 diberi 零. Menit :15, :30, :45 punya sebutan khusus.",
                formula = "[jam]点 [menit]分\n:15 = 一刻 · :30 = 半 · :45 = 三刻",
                stepsTitle = "Contoh: 7:05",
                steps = listOf("Jam 7 → 七点", "Menit 5 < 10 → 零五分", "Hasil: ${C.time(7, 5).hanzi}"),
            ),
            LessonRule(
                "Rumus 差 (Kurang)",
                "差 menyebut berapa menit lagi menuju jam berikutnya. Untuk mengubahnya, kurangi jamnya 1 dan menitnya dari 60.",
                formula = "差 N 分 H 点 = (H − 1) : (60 − N)\n差一刻 H 点 = (H − 1) : 45",
                stepsTitle = "Contoh: 差十分八点",
                steps = listOf("H = 8, N = 10", "Jam = 8 − 1 = 7", "Menit = 60 − 10 = 50", "Hasil: 7:50"),
            ),
            LessonRule(
                "Rumus Format 24 Jam",
                "Bahasa Mandarin sehari-hari memakai jam 1–12 dengan keterangan waktu. Untuk siang/sore/malam, tambahkan 12.",
                formula = "上午 / 凌晨 H点 = H:00\n下午 / 晚上 H点 = (H + 12):00\n中午十二点 = 12:00",
                stepsTitle = "Contoh: 晚上八点半",
                steps = listOf("晚上 → malam, jadi tambah 12", "八点半 → 8:30", "8 + 12 = 20", "Hasil: 20:30"),
            ),
        )
        NumCat.TANGGAL -> listOf(
            LessonRule(
                "Rumus Tanggal",
                "Urutannya dari satuan terbesar ke terkecil: tahun, bulan, tanggal. Tahun dibaca per digit, bulan dan tanggal dibaca sebagai angka biasa.",
                formula = "[tahun per digit]年 [bulan]月 [tanggal]号",
                stepsTitle = "Contoh: 17 Agustus 1945",
                steps = listOf("1945 → per digit: 一九四五年", "Agustus = bulan ke-8 → 八月", "Tanggal 17 → 十七号", "Hasil: ${C.date(1945, 8, 17).hanzi}"),
            ),
            LessonRule(
                "Rumus Hari",
                "Hari dihitung mulai Senin sebagai hari ke-1.",
                formula = "星期 N = hari ke-N (Senin = 1 … Sabtu = 6)\nMinggu = 星期天 / 星期日",
            ),
            LessonRule(
                "Menghitung Hari",
                "Untuk pertanyaan seperti \"后天是星期几？\", tambahkan selisih harinya ke hari ini.",
                formula = "前天 = −2 · 昨天 = −1 · 明天 = +1 · 后天 = +2 · 大后天 = +3",
                stepsTitle = "Contoh: 今天星期五，后天是星期几？",
                steps = listOf("Hari ini 星期五 = Jumat (hari ke-5)", "后天 = +2 hari", "5 + 2 = 7 → Minggu", "Hasil: 星期天"),
            ),
        )
        NumCat.TELEPON -> listOf(
            LessonRule(
                "Rumus Nomor",
                "Setiap digit dibaca sendiri-sendiri. Angka 1 dibaca 幺 (yāo). Nomor ponsel 11 digit biasanya dijeda 3-4-4.",
                formula = "Digit demi digit · 1 = 幺 (yāo) · 0 = 零",
                stepsTitle = "Contoh: 138 0013 8000",
                steps = listOf("Jeda: 138 | 0013 | 8000", "138 → ${C.digits("138", yao = true, zero = "零").pinyin}", "0013 → ${C.digits("0013", yao = true, zero = "零").pinyin}", "8000 → ${C.digits("8000", yao = true, zero = "零").pinyin}"),
            ),
        )
        NumCat.UKURAN -> listOf(
            LessonRule(
                "Rumus Kata Satuan",
                "Di antara angka dan benda harus ada kata satuan. Angka 2 berubah menjadi 两.",
                formula = "[angka] + [kata satuan] + [benda]",
                stepsTitle = "Contoh: 2 buku",
                steps = listOf("2 sebelum kata satuan → 两", "Kata satuan buku → 本", "Buku → 书", "Hasil: 两本书 (liǎng běn shū)"),
            ),
            LessonRule(
                "Rumus Tinggi Badan",
                "Sebut meternya dengan 米, lalu angka sentimeternya per digit tanpa satuan.",
                formula = "一米 [d1][d2] = 1,d1d2 m",
                stepsTitle = "Contoh: 1,68 m",
                steps = listOf("1 meter → 一米", "68 → per digit: 六八", "Hasil: 一米六八 (yì mǐ liù bā)"),
            ),
            LessonRule(
                "Rumus 斤 dan Kilogram",
                "1 斤 = 500 gram, jadi setengah kilogram.",
                formula = "kg = 斤 ÷ 2\n斤 = kg × 2",
                stepsTitle = "Contoh: 六斤",
                steps = listOf("六斤 = 6 斤", "6 ÷ 2 = 3", "Hasil: 3 kg"),
            ),
        )
        NumCat.KIRA -> listOf(
            LessonRule(
                "Rumus Perkiraan",
                "Ada tiga pola utama untuk jumlah yang tidak pasti.",
                formula = "N多 = sedikit lebih dari N\nA B (berurutan) = A atau B\n几 + satuan = beberapa satuan",
                stepsTitle = "Contoh: 二十多个人",
                steps = listOf("二十 = 20", "多 = lebih", "Hasil: lebih dari 20 orang (biasanya 21–29)"),
            ),
        )
    }

    private fun traps(cat: NumCat): List<LessonRule> = when (cat) {
        NumCat.UMUM, NumCat.CAMPUR -> listOf(LessonRule("Jebakan yang Sering Salah", "Bandingkan pasangan berikut baik-baik.", examples = listOf(
            C.number(105) to "105 (pakai 零)",
            sp("一" to "yì", "百" to "bǎi", "五" to "wǔ") to "150 dalam bahasa lisan!",
            C.number(10_000) to "10.000 (bukan 十千)",
            C.number(2000) to "2.000 (bukan 二千)",
        )))
        NumCat.HARGA -> listOf(LessonRule("Jebakan yang Sering Salah", "Satu huruf bisa mengubah harga.", examples = listOf(
            C.moneySpoken(1, 5, 0) to "¥1,50", C.moneySpoken(1, 0, 5) to "¥1,05", C.moneySpoken(2, 0, 0) to "¥2 (bukan 二块)",
        )))
        NumCat.DISKON -> listOf(LessonRule("Jebakan yang Sering Salah", "Jangan tertukar antara yang dibayar dan potongannya.", examples = listOf(
            sp("打" to "dǎ", "三" to "sān", "折" to "zhé") to "Bayar 30% (diskon 70%)", sp("打" to "dǎ", "七" to "qī", "折" to "zhé") to "Bayar 70% (diskon 30%)",
        )))
        NumCat.JAM -> listOf(LessonRule("Jebakan yang Sering Salah", "差 menghitung mundur dari jam berikutnya.", examples = listOf(
            C.time(9, 45, 2) to "9:45 (bukan 10:45)", C.time(2, 0) to "2:00 (bukan 二点)",
        )))
        else -> emptyList()
    }

    /** Kartu budaya acak yang muncul setelah kuis selesai. */
    val culture = listOf(
        "🍀 Angka 8 (八 bā) dianggap paling beruntung karena bunyinya mirip 发 (fā, \"makmur\"). Pembukaan Olimpiade Beijing dimulai 08-08-2008 pukul 8:08.",
        "🚫 Angka 4 (四 sì) sering dihindari karena bunyinya mirip 死 (sǐ, \"mati\"). Banyak gedung tidak punya lantai 4.",
        "💕 520 (wǔ èr líng) terdengar seperti 我爱你 (\"aku cinta kamu\"). Tanggal 20 Mei jadi \"hari kasih sayang\" versi internet Tiongkok.",
        "✋ Orang Tiongkok bisa menunjukkan angka 6–10 dengan satu tangan: 6 = jempol dan kelingking, 8 = jempol dan telunjuk, 10 = jari menyilang.",
        "😄 666 (liù liù liù) di internet Tiongkok artinya \"keren banget\", karena 溜 (liù) berarti lancar atau jago.",
        "🧧 Angpao biasanya diisi jumlah genap seperti 200 atau 888. Jumlah dengan angka 4 dihindari.",
        "📱 Saat menyebut nomor telepon, orang Tiongkok membaca 1 sebagai 幺 (yāo) agar tidak tertukar dengan 七 (qī).",
        "🎂 Umur 9 (九 jiǔ) bunyinya mirip 久 (jiǔ, \"lama/abadi\"), jadi 9 juga dianggap angka baik, terutama di pernikahan.",
        "🛒 Tanggal 11 November (双十一, \"Double 11\") adalah festival belanja daring terbesar di dunia, penuh promo 满减 dan 打折.",
    )
}
