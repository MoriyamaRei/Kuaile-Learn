package com.nesxha.readit.ui.screens

import com.nesxha.readit.ui.components.rememberHints
import com.nesxha.readit.ui.components.ResultsScreen
import com.nesxha.readit.ui.components.HintState
import com.nesxha.readit.ui.components.HintRow
import com.nesxha.readit.ui.components.ExitDialog
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Backspace
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.Calculate
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.nesxha.readit.data.Counter
import com.nesxha.readit.data.Keys
import com.nesxha.readit.data.NumCat
import com.nesxha.readit.data.NumQuestion
import com.nesxha.readit.data.NumberLessons
import com.nesxha.readit.data.NumberQuiz
import com.nesxha.readit.data.Store
import com.nesxha.readit.ui.components.ActionRow
import com.nesxha.readit.ui.components.AnimatedNumber
import com.nesxha.readit.ui.components.BrandCard
import com.nesxha.readit.ui.components.Chip
import com.nesxha.readit.ui.components.Confetti
import com.nesxha.readit.ui.components.EmptyState
import com.nesxha.readit.ui.components.HanziText
import com.nesxha.readit.ui.components.HeaderButton
import com.nesxha.readit.ui.components.Pill
import com.nesxha.readit.ui.components.PinyinText
import com.nesxha.readit.ui.components.ProgressBar
import com.nesxha.readit.ui.components.ScreenHeader
import com.nesxha.readit.ui.components.SectionTitle
import com.nesxha.readit.ui.components.Segmented
import com.nesxha.readit.ui.components.SoftCard
import com.nesxha.readit.ui.components.SpeakButton
import com.nesxha.readit.ui.components.appear
import com.nesxha.readit.ui.components.bounceClick
import com.nesxha.readit.ui.theme.Brand
import com.nesxha.readit.ui.theme.HanziFont
import com.nesxha.readit.ui.theme.UiFont
import com.nesxha.readit.util.ChineseNumber
import com.nesxha.readit.util.Spoken
import com.nesxha.readit.util.Tts
import kotlinx.coroutines.delay
import com.nesxha.readit.platform.BackHandler

internal fun diffDesc(cat: NumCat, d: Int): String = when (cat) {
    NumCat.UMUM -> listOf("Angka 0–99", "Angka 100–9.999", "Angka 10.000–100 juta")[d]
    NumCat.HARGA -> listOf("Harga yuan bulat", "Yuan dengan 毛/分 dan soal cerita", "Harga lengkap dan label 元/角/分")[d]
    NumCat.DISKON -> listOf("Membaca 打X折", "X五折 dan menghitung harga bayar", "满减 dan hitungan lebih sulit")[d]
    NumCat.PERSEN -> listOf("Persen puluhan, pecahan sederhana", "Persen 1–100, pecahan sampai 10", "Persen desimal, pecahan sampai 20")[d]
    NumCat.JAM -> listOf("Jam bulat dan 半", "刻 dan menit", "差 dan keterangan waktu (24 jam)")[d]
    NumCat.TANGGAL -> listOf("Nama hari dan bulan", "Tanggal lengkap", "前天, 明天, 后天, dan lainnya")[d]
    NumCat.TELEPON -> listOf("Nomor kamar", "Nomor telepon dan kamar", "Telepon dan nomor darurat")[d]
    NumCat.UKURAN -> listOf("Umur, jumlah orang, lantai", "Umur, tinggi, dan berat", "Tinggi, berat, dan 斤")[d]
    NumCat.KIRA -> listOf("几, 三四, 十多", "几十, 二三十, 一百多", "几百, 两千多, 几万")[d]
    NumCat.CAMPUR -> listOf("Semua kategori, tingkat mudah", "Semua kategori, tingkat sedang", "Semua kategori, tingkat sulit")[d]
}

/** Baca jarak buatan sendiri; mengembalikan (jarak, pesan kesalahan). */
internal fun parseRange(lo: String, hi: String): Pair<LongRange?, String?> {
    val a = lo.filter { it.isDigit() }.toLongOrNull()
    val b = hi.filter { it.isDigit() }.toLongOrNull()
    return when {
        a == null || b == null -> null to "Isi kedua angka dulu."
        b > NumberQuiz.MAX_NUMBER -> null to "Angka terbesar ${ChineseNumber.fmt(NumberQuiz.MAX_NUMBER)}."
        a >= b -> null to "Angka \"sampai\" harus lebih besar dari angka \"dari\"."
        else -> (a..b) to null
    }
}

@Composable
internal fun RangePicker(
    idx: Int, lo: String, hi: String, error: String?,
    onRange: (Int) -> Unit, onCustom: (String, String) -> Unit, modifier: Modifier,
) {
    val cs = MaterialTheme.colorScheme
    SoftCard(modifier.fillMaxWidth()) {
        Text("Jarak Angka", style = MaterialTheme.typography.titleMedium, color = cs.onSurface)
        Text("Pilih jarak angka yang mau dilatih, atau atur sendiri.", style = MaterialTheme.typography.bodySmall, color = cs.onSurfaceVariant)
        Spacer(Modifier.height(12.dp))
        val items = NumberQuiz.ranges.mapIndexed { i, r -> i to r.first } + (-1 to "Atur Sendiri")
        items.chunked(2).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(vertical = 4.dp)) {
                row.forEach { (i, label) ->
                    val sel = i == idx
                    val bg by animateColorAsState(if (sel) cs.primary else cs.surfaceVariant, label = "rg")
                    Box(
                        Modifier.weight(1f).height(46.dp).clip(RoundedCornerShape(14.dp)).background(bg).bounceClick { onRange(i) },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            if (i == -1) "✏️ $label" else label, style = MaterialTheme.typography.labelLarge,
                            color = if (sel) Color.White else cs.onSurface, maxLines = 1, softWrap = false,
                        )
                    }
                }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }
        AnimatedVisibility(idx == -1, enter = expandVertically() + fadeIn()) {
            Column {
                Spacer(Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RangeField("Dari", lo, Modifier.weight(1f)) { onCustom(it, hi) }
                    Text("  –  ", style = MaterialTheme.typography.titleLarge, color = cs.onSurfaceVariant)
                    RangeField("Sampai", hi, Modifier.weight(1f)) { onCustom(lo, it) }
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    error ?: "Soal diambil acak dari ${ChineseNumber.fmt(lo.toLongOrNull() ?: 0)} sampai ${ChineseNumber.fmt(hi.toLongOrNull() ?: 0)}.",
                    style = MaterialTheme.typography.bodySmall, color = if (error != null) Brand.error else cs.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun RangeField(label: String, value: String, modifier: Modifier, onChange: (String) -> Unit) {
    val cs = MaterialTheme.colorScheme
    Column(
        modifier.clip(RoundedCornerShape(14.dp)).background(cs.surfaceVariant).padding(horizontal = 12.dp, vertical = 8.dp),
    ) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = cs.onSurfaceVariant)
        BasicTextField(
            value = value,
            onValueChange = { v -> onChange(v.filter { it.isDigit() }.take(12)) },
            singleLine = true,
            textStyle = MaterialTheme.typography.titleMedium.copy(color = cs.onSurface, fontFamily = UiFont),
            cursorBrush = SolidColor(cs.primary),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
        )
        if (value.length > 3) Text(ChineseNumber.fmt(value.toLongOrNull() ?: 0), style = MaterialTheme.typography.labelSmall, color = cs.primary, maxLines = 1)
    }
}

// ---------------- Permainan ----------------

@Composable
fun NumGame(cfg: PracticeConfig, round: Int, onExit: () -> Unit, onAgain: () -> Unit) {
    val cat = cfg.numCat
    val range = cfg.range
    val questions = remember(round) { NumberQuiz.build(cat, cfg.numDiff, cfg.count, range) }
    val startXp = remember(round) { Store.now.xp }
    val fact = remember(round) { NumberLessons.culture.random() }
    var index by remember(round) { mutableIntStateOf(0) }
    var score by remember(round) { mutableIntStateOf(0) }
    val wrongs = remember(round) { mutableStateListOf<NumQuestion>() }
    val hinted = remember(round) { mutableStateListOf<Int>() }
    var finished by remember(round) { mutableStateOf(false) }
    var confetti by remember { mutableIntStateOf(0) }
    var askExit by remember { mutableStateOf(false) }
    val hints = rememberHints(round)

    BackHandler(enabled = !finished) { if (index > 0) askExit = true else onExit() }
    if (askExit) ExitDialog("Keluar dari Latihan?", "Soal yang sudah dijawab tetap tercatat, tetapi latihan ini tidak selesai.", onStay = { askExit = false }) {
        askExit = false; onExit()
    }

    if (questions.isEmpty()) {
        Column(Modifier.fillMaxSize()) {
            ScreenHeader(cat.title, null, leading = { HeaderButton(Icons.Rounded.Close, "Tutup", onExit) })
            EmptyState("🤔", "Soal Tidak Tersedia", "Coba kategori atau tingkat lain.")
        }
        return
    }

    Box(Modifier.fillMaxSize()) {
        if (finished) {
            ResultsScreen(
                "${cat.emoji} ${cat.title}", score, questions.size, xp = Store.now.xp - startXp, hinted = hinted.size,
                onAgain = onAgain, onExit = onExit, note = fact,
            ) {
                if (wrongs.isNotEmpty()) {
                    SectionTitle("Perlu Diulang", Modifier.appear(3))
                    wrongs.forEachIndexed { i, q -> WrongNumCard(q, Modifier.appear(4 + i)) }
                }
            }
        } else {
            Column(Modifier.fillMaxSize()) {
                ScreenHeader(
                    "${cat.emoji} ${cat.title}",
                    "Soal ${index + 1} dari ${questions.size} · Benar $score" + (range?.let { " · ${ChineseNumber.fmt(it.first)}–${ChineseNumber.fmt(it.last)}" } ?: ""),
                    leading = { HeaderButton(Icons.Rounded.Close, "Tutup") { if (index > 0) askExit = true else onExit() } },
                ) {
                    ProgressBar(index.toFloat() / questions.size, color = Color.White, track = Color(0x33FFFFFF), height = 8.dp)
                }
                AnimatedContent(
                    targetState = index,
                    transitionSpec = {
                        (slideInHorizontally(spring(dampingRatio = 0.8f, stiffness = 300f)) { it } + fadeIn()) togetherWith
                            (slideOutHorizontally(tween(250)) { -it / 2 } + fadeOut(tween(200)))
                    },
                    label = "nq",
                ) { i ->
                    NumQuestionView(questions[i], key = "$round-$i", hints = hints, helped = i in hinted, onHint = { if (i !in hinted) hinted += i }) { ok ->
                        if (ok) score++ else wrongs += questions[i]
                        if (i + 1 >= questions.size) {
                            finished = true
                            if (score * 100 / questions.size >= 80) confetti++
                        } else index = i + 1
                    }
                }
            }
        }
        Confetti(confetti)
    }
}

@Composable
private fun WrongNumCard(q: NumQuestion, modifier: Modifier) {
    val cs = MaterialTheme.colorScheme
    SoftCard(modifier.fillMaxWidth(), radius = 20.dp, padding = PaddingValues(14.dp)) {
        Text(q.hanzi ?: q.display ?: "", fontFamily = if (q.hanzi != null) HanziFont else UiFont, fontSize = 20.sp, color = cs.onSurface)
        if (q.hanzi != null && q.pinyin != null) PinyinText(q.pinyin, q.hanzi, size = 13.sp, align = TextAlign.Start, wrap = true)
        Spacer(Modifier.height(4.dp))
        Text("Jawaban: ${q.answerText}", style = MaterialTheme.typography.titleSmall, color = Brand.success, fontFamily = if (q.answerText.any { it.code > 0x3000 }) HanziFont else UiFont)
    }
}

@Composable
private fun NumQuestionView(q: NumQuestion, key: String, hints: HintState, helped: Boolean, onHint: () -> Unit, onNext: (Boolean) -> Unit) {
    val cs = MaterialTheme.colorScheme
    var picked by remember(key) { mutableStateOf<Int?>(null) }
    var input by remember(key) { mutableStateOf("") }
    var result by remember(key) { mutableStateOf<Boolean?>(null) }
    val shake = remember(key) { Animatable(0f) }

    fun submit(ok: Boolean) {
        if (result != null) return
        result = ok
        Store.numAnswer(q.cat, ok, if (!ok || helped) 1 else if (q.story) 5 else 3)
    }
    LaunchedEffect(result) {
        when (result) {
            false -> shake.animateTo(0f, keyframes { durationMillis = 420; -14f at 60; 12f at 120; -8f at 180; 6f at 240; -3f at 300; 0f at 420 })
            true -> { q.hanzi?.let { Tts.speak(it) }; delay(1500); onNext(true) }
            null -> {}
        }
    }

    Column(Modifier.fillMaxSize()) {
        Column(
            Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 18.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Kartu soal
            SoftCard(Modifier.fillMaxWidth().graphicsLayer { translationX = shake.value.dp.toPx() }, radius = 28.dp) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Pill(q.cat.title)
                    if (q.story) { Spacer(Modifier.width(6.dp)); Pill("Soal Cerita", color = Brand.gold.copy(alpha = 0.25f), textColor = cs.onSurface) }
                    Spacer(Modifier.weight(1f))
                    if (q.hanzi != null && result != null) SpeakButton(q.hanzi, onBrand = false)
                }
                Spacer(Modifier.height(10.dp))
                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    if (q.hanzi != null) {
                        val len = q.hanzi.length
                        HanziText(q.hanzi, size = when { len <= 3 -> 52.sp; len <= 6 -> 40.sp; len <= 10 -> 30.sp; else -> 24.sp })
                        if (q.pinyin != null && result != null) {
                            Spacer(Modifier.height(4.dp))
                            PinyinText(q.pinyin, q.hanzi, size = 17.sp, wrap = true)
                        }
                    }
                    if (q.display != null) {
                        Text(q.display, style = MaterialTheme.typography.displaySmall, color = cs.onSurface, textAlign = TextAlign.Center)
                    }
                    Spacer(Modifier.height(10.dp))
                    Text(q.instruction, style = MaterialTheme.typography.bodyMedium, color = cs.onSurfaceVariant, textAlign = TextAlign.Center)
                }
            }
            HintRow(hints, questionKey = key, available = q.hanzi != null && q.pinyin != null, answered = result != null, pinyin = q.pinyin, hanzi = q.hanzi, onUsed = onHint)

            if (q.isChoice) {
                q.options.forEachIndexed { i, o ->
                    val state = when { picked == null -> 0; i == q.answer -> 1; picked == i -> 2; else -> 3 }
                    val bg by animateColorAsState(when (state) { 1 -> Brand.success; 2 -> Brand.error; else -> cs.surface }, tween(250), label = "no")
                    val fg = if (state == 1 || state == 2) Color.White else cs.onSurface
                    Column(
                        Modifier.fillMaxWidth().graphicsLayer { alpha = if (state == 3) 0.5f else 1f }
                            .clip(RoundedCornerShape(20.dp)).background(bg)
                            .border(1.5.dp, if (state == 0) cs.outline else Color.Transparent, RoundedCornerShape(20.dp))
                            .bounceClick(enabled = picked == null) { picked = i; submit(i == q.answer) }
                            .padding(horizontal = 16.dp, vertical = 13.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        if (o.chinese) Text(o.text, fontFamily = HanziFont, fontSize = if (o.text.length > 10) 20.sp else 26.sp, fontWeight = FontWeight.Bold, color = fg, textAlign = TextAlign.Center)
                        else Text(o.text, style = MaterialTheme.typography.titleMedium, color = fg, textAlign = TextAlign.Center)
                        if (state == 1 && o.pinyin != null) Text(o.pinyin, style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.9f), textAlign = TextAlign.Center)
                    }
                }
            } else {
                // Kotak jawaban
                val frame by animateColorAsState(when (result) { true -> Brand.success; false -> Brand.error; null -> cs.primary }, label = "nf")
                Row(
                    Modifier.fillMaxWidth().heightIn(min = 64.dp).clip(RoundedCornerShape(20.dp)).background(cs.surface)
                        .border(2.dp, frame, RoundedCornerShape(20.dp)).padding(horizontal = 18.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (q.unit == "yuan") Text("¥ ", style = MaterialTheme.typography.headlineSmall, color = cs.onSurfaceVariant)
                    Text(
                        input.ifEmpty { "Ketik jawaban…" }, modifier = Modifier.weight(1f),
                        style = if (input.isEmpty()) MaterialTheme.typography.titleMedium else MaterialTheme.typography.headlineMedium,
                        color = if (input.isEmpty()) cs.onSurfaceVariant else cs.onSurface, maxLines = 1,
                    )
                    if (q.unit != null && q.unit != "yuan") Text(q.unit, style = MaterialTheme.typography.titleMedium, color = cs.onSurfaceVariant)
                }
            }

            // Penjelasan setelah menjawab
            AnimatedVisibility(result != null, enter = expandVertically() + fadeIn() + slideInVertically { it / 3 }) {
                val ok = result == true
                SoftCard(Modifier.fillMaxWidth(), color = (if (ok) Brand.success else Brand.error).copy(alpha = 0.12f)) {
                    Text(
                        if (ok) "Benar! +${if (helped) 1 else if (q.story) 5 else 3} XP" else "Belum Tepat",
                        style = MaterialTheme.typography.titleMedium, color = if (ok) Brand.success else Brand.error,
                    )
                    if (!ok) {
                        Spacer(Modifier.height(4.dp))
                        Text("Jawaban: ${q.answerText}", style = MaterialTheme.typography.titleSmall, color = cs.onSurface, fontFamily = if (q.answerText.any { it.code > 0x3000 }) HanziFont else UiFont)
                    }
                    if (q.explain.isNotBlank()) {
                        Spacer(Modifier.height(6.dp))
                        Text(q.explain, style = MaterialTheme.typography.bodyMedium, color = cs.onSurface)
                    }
                }
            }
        }

        // Papan angka / tombol lanjut
        Column(Modifier.navigationBarsPadding().padding(horizontal = 18.dp, vertical = 10.dp)) {
            if (result == false) {
                Box(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(50)).background(cs.primary).bounceClick { onNext(false) }.padding(vertical = 15.dp),
                    contentAlignment = Alignment.Center,
                ) { Text("Lanjut", color = Color.White, style = MaterialTheme.typography.titleMedium) }
            } else if (!q.isChoice && result == null) {
                Keypad(q.keys, onKey = { k -> if (input.length < 16) input += k }, onDelete = { input = input.dropLast(1) })
                Spacer(Modifier.height(8.dp))
                val ready = input.isNotEmpty()
                Box(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(50)).background(if (ready) cs.primary else cs.surfaceVariant)
                        .bounceClick(enabled = ready) { submit(q.check(input)) }.padding(vertical = 15.dp),
                    contentAlignment = Alignment.Center,
                ) { Text("Periksa", color = if (ready) Color.White else cs.onSurfaceVariant, style = MaterialTheme.typography.titleMedium) }
            }
        }
    }
}

@Composable
private fun Keypad(keys: Keys, onKey: (String) -> Unit, onDelete: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    val rows = listOf(listOf("1", "2", "3"), listOf("4", "5", "6"), listOf("7", "8", "9"), listOf(keys.extra, "0", "⌫"))
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        rows.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { k ->
                    val empty = k.isEmpty()
                    Box(
                        Modifier.weight(1f).height(52.dp).clip(RoundedCornerShape(16.dp))
                            .background(if (empty) Color.Transparent else if (k == "⌫") cs.primaryContainer else cs.surface)
                            .then(if (empty) Modifier else Modifier.border(1.dp, cs.outline, RoundedCornerShape(16.dp)))
                            .bounceClick(enabled = !empty) { if (k == "⌫") onDelete() else onKey(k) },
                        contentAlignment = Alignment.Center,
                    ) {
                        if (k == "⌫") Icon(Icons.AutoMirrored.Rounded.Backspace, "Hapus", tint = cs.onPrimaryContainer)
                        else if (!empty) Text(k, style = MaterialTheme.typography.headlineSmall, color = cs.onSurface)
                    }
                }
            }
        }
    }
}

// ---------------- Materi ----------------

/** Layar materi Belajar Angka; bisa berpindah bab lewat chip di atas. */
@Composable
fun NumberLessonScreen(nav: NavHostController, startCat: NumCat) {
    var cat by rememberSaveable { mutableStateOf(startCat) }
    NumLesson(cat, onCat = { cat = it }, onBack = { nav.popBackStack() }, onPractice = { nav.navigate("drill/${it.name}") })
}

@Composable
private fun NumLesson(cat: NumCat, onCat: (NumCat) -> Unit, onBack: () -> Unit, onPractice: (NumCat) -> Unit) {
    val cs = MaterialTheme.colorScheme
    val st by Store.state.collectAsState()
    LaunchedEffect(cat) { Store.maxCounter(Counter.lessonRead(cat), 1) }
    Column(Modifier.fillMaxSize()) {
        ScreenHeader("Belajar Angka", "${cat.emoji} ${cat.title}", leading = { HeaderButton(Icons.AutoMirrored.Rounded.ArrowBack, "Kembali", onBack) })
        Row(
            Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            NumCat.entries.filter { it != NumCat.CAMPUR }.forEach { c ->
                val done = st.counter(Counter.lessonRead(c)) > 0
                Chip("${if (done && c != cat) "✓ " else ""}${c.emoji} ${c.title}", c == cat) { onCat(c) }
            }
        }
        AnimatedContent(cat, label = "lesson") { c ->
            Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                NumberLessons.full(c).forEachIndexed { i, rule ->
                    SoftCard(Modifier.fillMaxWidth().appear(i)) {
                        Text(rule.title, style = MaterialTheme.typography.titleMedium, color = cs.onSurface)
                        Spacer(Modifier.height(4.dp))
                        Text(rule.body, style = MaterialTheme.typography.bodyMedium, color = cs.onSurfaceVariant)
                        rule.formula?.let { f ->
                            Spacer(Modifier.height(10.dp))
                            FormulaBox(f)
                        }
                        if (rule.steps.isNotEmpty()) {
                            Spacer(Modifier.height(10.dp))
                            StepsBox(rule.stepsTitle, rule.steps)
                        }
                        if (rule.table.isNotEmpty()) {
                            Spacer(Modifier.height(10.dp))
                            rule.table.chunked(2).forEach { row ->
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(vertical = 4.dp)) {
                                    row.forEach { (sp, meaning) -> TableCard(sp, meaning, Modifier.weight(1f)) }
                                    if (row.size == 1) Spacer(Modifier.weight(1f))
                                }
                            }
                        }
                        rule.examples.forEach { (sp, meaning) ->
                            Spacer(Modifier.height(8.dp))
                            ExampleRow(sp, meaning)
                        }
                    }
                }
                Box(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(50)).background(cs.primary).bounceClick { onPractice(c) }.padding(vertical = 15.dp),
                    contentAlignment = Alignment.Center,
                ) { Text("Latihan Bab Ini", color = Color.White, style = MaterialTheme.typography.titleMedium) }
                Spacer(Modifier.navigationBarsPadding().height(12.dp))
            }
        }
    }
}

@Composable
private fun FormulaBox(text: String) {
    val cs = MaterialTheme.colorScheme
    Column(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(cs.primary.copy(alpha = 0.10f))
            .border(1.5.dp, cs.primary.copy(alpha = 0.35f), RoundedCornerShape(16.dp)).padding(12.dp),
    ) {
        Text("Rumus", style = MaterialTheme.typography.labelMedium, color = cs.primary)
        Spacer(Modifier.height(4.dp))
        text.split("\n").forEachIndexed { i, line ->
            Text(
                line, color = cs.onSurface, fontFamily = HanziFont,
                fontSize = if (i == 0) 18.sp else 14.sp, fontWeight = if (i == 0) FontWeight.Bold else FontWeight.Normal,
            )
        }
    }
}

@Composable
private fun StepsBox(title: String?, steps: List<String>) {
    val cs = MaterialTheme.colorScheme
    Column(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(cs.surfaceVariant).padding(12.dp),
    ) {
        Text(title ?: "Langkah demi Langkah", style = MaterialTheme.typography.labelLarge, color = cs.onSurface)
        steps.forEachIndexed { i, st ->
            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.Top) {
                Box(
                    Modifier.size(22.dp).clip(RoundedCornerShape(50)).background(if (i == steps.lastIndex) Brand.success else cs.primary),
                    contentAlignment = Alignment.Center,
                ) { Text(if (i == steps.lastIndex) "✓" else "${i + 1}", color = Color.White, style = MaterialTheme.typography.labelSmall) }
                Spacer(Modifier.width(8.dp))
                Text(st, style = MaterialTheme.typography.bodyMedium, color = cs.onSurface, fontFamily = if (st.any { it.code > 0x3000 }) HanziFont else UiFont, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun TableCard(sp: Spoken, meaning: String, modifier: Modifier) {
    val cs = MaterialTheme.colorScheme
    Column(
        modifier.heightIn(min = 92.dp).clip(RoundedCornerShape(16.dp)).background(cs.primaryContainer.copy(alpha = 0.6f))
            .bounceClick { Tts.speak(sp.hanzi) }.padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(sp.hanzi, fontFamily = HanziFont, fontSize = if (sp.hanzi.length > 3) 20.sp else 26.sp, fontWeight = FontWeight.Bold, color = cs.onSurface, maxLines = 1, softWrap = false)
        PinyinText(sp.pinyin, sp.hanzi, size = 13.sp, wrap = true)
        Spacer(Modifier.height(2.dp))
        Text(meaning, style = MaterialTheme.typography.labelMedium, color = cs.primary, textAlign = TextAlign.Center, maxLines = 2)
    }
}

@Composable
private fun ExampleRow(sp: Spoken, meaning: String) {
    val cs = MaterialTheme.colorScheme
    Row(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(cs.primaryContainer.copy(alpha = 0.6f))
            .bounceClick { Tts.speak(sp.hanzi) }.padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(sp.hanzi, fontFamily = HanziFont, fontSize = 20.sp, color = cs.onSurface, fontWeight = FontWeight.Bold)
            PinyinText(sp.pinyin, sp.hanzi, size = 13.sp, align = TextAlign.Start, wrap = true)
        }
        Spacer(Modifier.width(8.dp))
        Text(meaning, style = MaterialTheme.typography.labelLarge, color = cs.primary, textAlign = TextAlign.End)
        Spacer(Modifier.width(6.dp))
        Text("🔊", fontSize = 14.sp)
    }
}

// ---------------- Pengubah angka ----------------

@Composable
fun ConverterScreen(nav: NavHostController) = NumConverter(onBack = { nav.popBackStack() })

@Composable
private fun NumConverter(onBack: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    var mode by rememberSaveable { mutableIntStateOf(0) }
    var text by rememberSaveable { mutableStateOf("30508") }
    val result: Pair<Spoken?, String?> = remember(text, mode) {
        runCatching {
            val clean = text.replace(".", "")
            if (clean.isEmpty()) return@runCatching null to null
            when (mode) {
                0 -> {
                    val ip = clean.substringBefore(',').ifEmpty { "0" }.toLong()
                    val fp = if (',' in clean) clean.substringAfter(',') else ""
                    if (ip >= 1_000_000_000_000L) null to "Angka terlalu besar (maksimal 12 digit)."
                    else {
                        val groups = ip.toString().reversed().chunked(4).map { it.reversed() }.reversed()
                        val units = listOf("", "万", "亿").take(groups.size).reversed()
                        val hint = if (groups.size > 1) "Kelompok 4 digit: " + groups.zip(units).joinToString(" · ") { (g, u) -> "$g$u" } else null
                        ChineseNumber.decimal(ip, fp) to hint
                    }
                }
                1 -> {
                    val y = clean.substringBefore(',').ifEmpty { "0" }.toInt()
                    val f = (if (',' in clean) clean.substringAfter(',') else "").padEnd(2, '0').take(2)
                    ChineseNumber.moneySpoken(y, f[0] - '0', f[1] - '0') to "Tertulis: ${ChineseNumber.moneyWritten(y, f[0] - '0', f[1] - '0').hanzi}"
                }
                else -> ChineseNumber.digits(clean.filter { it.isDigit() }, yao = true, zero = "零") to "Dibaca per digit; angka 1 dibaca 幺 (yāo) seperti saat menyebut nomor telepon."
            }
        }.getOrElse { null to "Format angka belum benar." }
    }

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        ScreenHeader("Pengubah Angka", "Ketik angka, lihat cara bacanya", leading = { HeaderButton(Icons.AutoMirrored.Rounded.ArrowBack, "Kembali", onBack) }) {
            Segmented(listOf("Angka", "Harga (¥)", "Per Digit"), mode, onBrand = true) { mode = it }
        }
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            SoftCard(Modifier.fillMaxWidth().appear(0)) {
                Text(
                    when (mode) { 0 -> "Angka (boleh pakai koma desimal)"; 1 -> "Harga dalam yuan, misalnya 12,5"; else -> "Nomor telepon, kamar, atau tahun" },
                    style = MaterialTheme.typography.labelMedium, color = cs.onSurfaceVariant,
                )
                Spacer(Modifier.height(6.dp))
                BasicTextField(
                    value = text,
                    onValueChange = { v -> text = v.filter { it.isDigit() || (it == ',' && mode != 2) }.take(if (mode == 2) 15 else 16) },
                    singleLine = true,
                    textStyle = MaterialTheme.typography.headlineMedium.copy(color = cs.onSurface, fontFamily = UiFont),
                    cursorBrush = SolidColor(cs.primary),
                    keyboardOptions = KeyboardOptions(keyboardType = if (mode == 2) KeyboardType.Number else KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(6.dp))
                ProgressBar(1f, height = 3.dp)
            }
            val sp = result.first
            AnimatedContent(sp?.hanzi ?: "", label = "conv") { _ ->
                if (sp != null && sp.syls.isNotEmpty()) {
                    BrandCard(Modifier.fillMaxWidth()) {
                        Row(verticalAlignment = Alignment.Top) {
                            Column(Modifier.weight(1f)) {
                                // Angka panjang: hanzi mengecil dan pinyin turun ke baris berikutnya (tidak terpotong).
                                val hz = when { sp.hanzi.length > 20 -> 22.sp; sp.hanzi.length > 12 -> 26.sp; else -> 36.sp }
                                Text(sp.hanzi, fontFamily = HanziFont, fontSize = hz, fontWeight = FontWeight.Bold, color = Color.White, lineHeight = hz * 1.3f)
                                Spacer(Modifier.height(8.dp))
                                Box(Modifier.clip(RoundedCornerShape(12.dp)).background(Color.White).padding(horizontal = 10.dp, vertical = 6.dp)) {
                                    PinyinText(sp.pinyin, sp.hanzi, size = 16.sp, onDark = false, align = TextAlign.Start, wrap = true)
                                }
                            }
                            Spacer(Modifier.width(8.dp))
                            SpeakButton(sp.hanzi)
                        }
                    }
                } else {
                    EmptyState("⌨️", "Ketik Angka", "Hasilnya muncul di sini.")
                }
            }
            val intOnly = mode == 0 && ',' !in text && text.isNotEmpty() && (text.toLongOrNull() ?: -1) in 0 until 1_000_000_000_000L
            if (intOnly) {
                val steps = remember(text) { ChineseNumber.steps(text.toLong()) }
                if (steps.size > 1) StepsBox("Cara Membaca", steps)
            }
            result.second?.let { hint ->
                SoftCard(Modifier.fillMaxWidth(), color = cs.primaryContainer.copy(alpha = 0.6f)) {
                    Text(hint, style = MaterialTheme.typography.bodyMedium, color = cs.onSurface, fontFamily = if (hint.any { it.code > 0x3000 }) HanziFont else UiFont)
                }
            }
            Spacer(Modifier.navigationBarsPadding().height(8.dp))
        }
    }
}
