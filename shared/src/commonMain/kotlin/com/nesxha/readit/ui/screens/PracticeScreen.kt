package com.nesxha.readit.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Draw
import androidx.compose.material.icons.rounded.Extension
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.Hearing
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material.icons.rounded.RecordVoiceOver
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Translate
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.nesxha.readit.AppChrome
import com.nesxha.readit.data.AppState
import com.nesxha.readit.data.Counter
import com.nesxha.readit.data.NumCat
import com.nesxha.readit.data.NumberQuiz
import com.nesxha.readit.data.PracticeMode
import com.nesxha.readit.data.Store
import com.nesxha.readit.data.WordQuiz
import com.nesxha.readit.data.WordSource
import com.nesxha.readit.ui.components.ActionRow
import com.nesxha.readit.ui.components.AnimatedNumber
import com.nesxha.readit.ui.components.BrandCard
import com.nesxha.readit.ui.components.CountChips
import com.nesxha.readit.ui.components.HINTS_PER_SESSION
import com.nesxha.readit.ui.components.HeaderButton
import com.nesxha.readit.ui.components.IconCircle
import com.nesxha.readit.ui.components.Pill
import com.nesxha.readit.ui.components.PrimaryButton
import com.nesxha.readit.ui.components.ScreenHeader
import com.nesxha.readit.ui.components.SectionTitle
import com.nesxha.readit.ui.components.Segmented
import com.nesxha.readit.ui.components.SoftCard
import com.nesxha.readit.ui.components.appear
import com.nesxha.readit.ui.components.bounceClick
import com.nesxha.readit.ui.theme.Brand
import com.nesxha.readit.platform.BackHandler

/** Pengaturan satu sesi latihan. */
data class PracticeConfig(
    val mode: PracticeMode,
    val level: Int = 0,
    val source: WordSource = WordSource.SEMUA,
    val count: Int = 10,
    val numCat: NumCat = NumCat.UMUM,
    val numDiff: Int = 0,
    val range: LongRange? = null,
    val minutes: Int = 1,
)

fun PracticeMode.icon(): ImageVector = when (this) {
    PracticeMode.HANZI_ARTI -> Icons.Rounded.Translate
    PracticeMode.ARTI_HANZI -> Icons.Rounded.Lightbulb
    PracticeMode.HANZI_PINYIN -> Icons.Rounded.RecordVoiceOver
    PracticeMode.PINYIN_HANZI -> Icons.Rounded.Hearing
    PracticeMode.NADA -> Icons.Rounded.GraphicEq
    PracticeMode.SUSUN -> Icons.Rounded.Extension
    PracticeMode.TULIS -> Icons.Rounded.Draw
    PracticeMode.KILAT -> Icons.Rounded.Bolt
    PracticeMode.UJIAN -> Icons.Rounded.School
    PracticeMode.ANGKA -> Icons.Rounded.Search
}

private sealed class Stage(val depth: Int) {
    data object Menu : Stage(0)
    data class Setup(val mode: PracticeMode) : Stage(1)
    data class Play(val cfg: PracticeConfig, val round: Int) : Stage(2)
}

/** [drillCat] ≠ null: dibuka dari materi, langsung ke persiapan Kuis Angka kategori itu. */
@Composable
fun PracticeScreen(nav: NavHostController, drillCat: NumCat? = null, startMode: PracticeMode? = null) {
    val st by Store.state.collectAsState()
    var stage by remember { mutableStateOf<Stage>(if (drillCat != null) Stage.Setup(PracticeMode.ANGKA) else if (startMode != null) Stage.Setup(startMode) else Stage.Menu) }
    var round by remember { mutableIntStateOf(0) }
    // Pilihan terakhir diingat selama aplikasi terbuka.
    var level by rememberSaveable { mutableIntStateOf(0) }
    var sourceIdx by rememberSaveable { mutableIntStateOf(0) }
    var count by rememberSaveable { mutableIntStateOf(10) }
    var numCatIdx by rememberSaveable { mutableIntStateOf(drillCat?.ordinal ?: 0) }
    var numDiff by rememberSaveable { mutableIntStateOf(0) }
    var rangeIdx by rememberSaveable { mutableIntStateOf(1) }
    var customLo by rememberSaveable { mutableStateOf("250") }
    var customHi by rememberSaveable { mutableStateOf("3000") }

    val s = stage
    DisposableEffect(s is Stage.Play) {
        AppChrome.focusMode = s is Stage.Play
        onDispose { AppChrome.focusMode = false }
    }
    fun toMenu() {
        if (drillCat != null || startMode != null) nav.popBackStack() else stage = Stage.Menu
    }
    BackHandler(enabled = s is Stage.Setup) { toMenu() }

    AnimatedContent(
        targetState = stage,
        transitionSpec = {
            val forward = targetState.depth >= initialState.depth
            (slideInHorizontally(tween(350)) { if (forward) it / 3 else -it / 3 } + fadeIn(tween(350))) togetherWith
                (slideOutHorizontally(tween(350)) { if (forward) -it / 3 else it / 3 } + fadeOut(tween(200)))
        },
        label = "practice",
    ) { stg ->
        when (stg) {
            Stage.Menu -> PracticeMenu(st, onArena = { nav.navigate("arena") }) { stage = Stage.Setup(it) }
            is Stage.Setup -> PracticeSetup(
                st, stg.mode,
                level = level, onLevel = { level = it },
                source = WordSource.entries[sourceIdx], onSource = { sourceIdx = it.ordinal },
                count = count, onCount = { count = it },
                numCat = NumCat.entries[numCatIdx], onNumCat = { numCatIdx = it.ordinal },
                numDiff = numDiff, onNumDiff = { numDiff = it },
                rangeIdx = rangeIdx, customLo = customLo, customHi = customHi,
                onRange = { rangeIdx = it }, onCustom = { lo, hi -> customLo = lo; customHi = hi },
                onBack = { toMenu() },
                onLesson = { cat -> nav.navigate("lesson/${cat.name}") },
                onHistory = { nav.navigate("kilathistory") },
                onStart = { cfg -> round++; stage = Stage.Play(cfg, round) },
            )
            is Stage.Play -> {
                val back = { stage = Stage.Setup(stg.cfg.mode) }
                val again = { round++; stage = Stage.Play(stg.cfg, round) }
                when (stg.cfg.mode) {
                    PracticeMode.TULIS -> WritingGame(stg.cfg, stg.round, onExit = back, onAgain = again, onWord = { nav.navigate("word/$it") })
                    PracticeMode.ANGKA -> NumGame(stg.cfg, stg.round, onExit = back, onAgain = again)
                    else -> WordQuizGame(stg.cfg, stg.round, onExit = back, onAgain = again, onWord = { nav.navigate("word/$it") })
                }
            }
        }
    }
}

// ---------------- Menu ----------------

@Composable
private fun PracticeMenu(st: AppState, onArena: () -> Unit = {}, onPick: (PracticeMode) -> Unit) {
    val cs = MaterialTheme.colorScheme
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        ScreenHeader("Latihan", "Uji kemampuanmu membaca hanzi")
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            if (com.nesxha.readit.platform.Accounts.ON) com.nesxha.readit.ui.games.ArenaBanner(Modifier.appear(0), onArena)
            SoftCard(Modifier.fillMaxWidth().appear(0), radius = 20.dp, padding = PaddingValues(14.dp), color = Brand.gold.copy(alpha = 0.12f)) {
                Text("Aturan Latihan", style = MaterialTheme.typography.titleSmall, color = cs.onSurface)
                Text(
                    "Soal hanya menampilkan hanzi, tanpa pinyin dan arti. Kamu punya $HINTS_PER_SESSION bantuan pinyin per latihan, masing-masing tampil 3 detik. Pinyin, arti, dan suara muncul setelah kamu menjawab.",
                    style = MaterialTheme.typography.bodySmall, color = cs.onSurfaceVariant,
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Featured(PracticeMode.KILAT, "Rekor ${st.counter(Counter.BEST_KILAT_SCORE)}", Modifier.weight(1f).appear(1)) { onPick(PracticeMode.KILAT) }
                val best = (0..4).maxOf { st.counter(Counter.examBest(it)) }
                Featured(PracticeMode.UJIAN, if (best > 0) "Terbaik $best%" else "Belum Pernah", Modifier.weight(1f).appear(2)) { onPick(PracticeMode.UJIAN) }
            }
            SectionTitle("Kosakata", Modifier.appear(3))
            val words = listOf(PracticeMode.HANZI_ARTI, PracticeMode.HANZI_PINYIN, PracticeMode.NADA, PracticeMode.ARTI_HANZI, PracticeMode.PINYIN_HANZI, PracticeMode.SUSUN)
            words.chunked(2).forEachIndexed { r, row ->
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    row.forEachIndexed { c, m -> ModeCard(m, Modifier.weight(1f).appear(4 + r * 2 + c)) { onPick(m) } }
                }
            }
            SectionTitle("Menulis & Angka", Modifier.appear(10))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ModeCard(PracticeMode.TULIS, Modifier.weight(1f).appear(11)) { onPick(PracticeMode.TULIS) }
                ModeCard(PracticeMode.ANGKA, Modifier.weight(1f).appear(12)) { onPick(PracticeMode.ANGKA) }
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun Featured(mode: PracticeMode, note: String, modifier: Modifier, onClick: () -> Unit) {
    val bob = rememberInfiniteTransition(label = "bob")
    val y by bob.animateFloat(-3f, 3f, infiniteRepeatable(tween(1400, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "y")
    BrandCard(modifier.height(176.dp), radius = 24.dp, padding = PaddingValues(16.dp), onClick = onClick) {
        IconCircle(mode.icon(), Modifier.graphicsLayer { translationY = y.dp.toPx() }, onBrand = true)
        Spacer(Modifier.weight(1f))
        Text(mode.title, style = MaterialTheme.typography.titleMedium, color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(mode.sub, style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.88f), minLines = 2, maxLines = 2, overflow = TextOverflow.Ellipsis)
        Spacer(Modifier.height(8.dp))
        Pill(note, color = Color(0x33FFFFFF), textColor = Color.White)
    }
}

@Composable
private fun ModeCard(mode: PracticeMode, modifier: Modifier, onClick: () -> Unit) {
    SoftCard(modifier.height(132.dp), radius = 22.dp, padding = PaddingValues(16.dp), onClick = onClick) {
        IconCircle(mode.icon())
        Spacer(Modifier.weight(1f))
        Text(mode.title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(mode.sub, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, minLines = 2, maxLines = 2, overflow = TextOverflow.Ellipsis)
    }
}

// ---------------- Layar persiapan (sama untuk semua mode) ----------------

@Composable
private fun PracticeSetup(
    st: AppState, mode: PracticeMode,
    level: Int, onLevel: (Int) -> Unit,
    source: WordSource, onSource: (WordSource) -> Unit,
    count: Int, onCount: (Int) -> Unit,
    numCat: NumCat, onNumCat: (NumCat) -> Unit,
    numDiff: Int, onNumDiff: (Int) -> Unit,
    rangeIdx: Int, customLo: String, customHi: String, onRange: (Int) -> Unit, onCustom: (String, String) -> Unit,
    onBack: () -> Unit, onLesson: (NumCat) -> Unit, onStart: (PracticeConfig) -> Unit,
    onHistory: () -> Unit = {},
) {
    val cs = MaterialTheme.colorScheme
    // Kuis Kilat dipakai untuk rank: kosakata dan target mengikuti rank, tidak bisa dipilih.
    val rankP = com.nesxha.readit.ui.games.RankCache.p
    val kilat = mode == PracticeMode.KILAT
    val effLevel = if (kilat) -com.nesxha.readit.data.RankRules.maxHsk(rankP) else level
    val effSource = if (kilat) WordSource.SEMUA else source
    val available = remember(mode, effLevel, effSource, st.cards) { if (mode.usesWords) WordQuiz.available(mode, st, effLevel, effSource) else Int.MAX_VALUE }
    val needed = when (mode) { PracticeMode.ANGKA -> 0; PracticeMode.UJIAN -> 20; PracticeMode.KILAT -> 4; else -> 2 }
    val rangeError = if (mode == PracticeMode.ANGKA && numCat == NumCat.UMUM && rangeIdx < 0) parseRange(customLo, customHi).second else null
    val problem = when {
        mode.usesWords && available < needed -> if (available == 0) "Belum ada kata untuk pilihan ini. Coba ganti level atau sumber kata." else "Kata untuk pilihan ini baru $available, minimal $needed."
        rangeError != null -> rangeError
        else -> null
    }
    val realCount = if (mode.usesWords && !mode.fixedCount) minOf(count, available) else count

    Column(Modifier.fillMaxSize()) {
        ScreenHeader(mode.title, mode.sub, leading = { HeaderButton(Icons.AutoMirrored.Rounded.ArrowBack, "Kembali", onBack) })
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            if (kilat) {
                val pos = com.nesxha.readit.data.RankRules.pos(rankP)
                SoftCard(Modifier.fillMaxWidth().appear(0)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        com.nesxha.readit.ui.components.RankEmblem(pos, 64.dp)
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text("Kilat Rank · ${pos.label}", style = MaterialTheme.typography.titleMedium, color = cs.onSurface)
                            Text("Kosakata HSK 1–${com.nesxha.readit.data.RankRules.maxHsk(rankP)} · Rekor ${st.counter(Counter.BEST_KILAT_SCORE)}",
                                style = MaterialTheme.typography.bodySmall, color = cs.onSurfaceVariant)
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Text("Makin cepat menjawab benar, makin besar skor dan RP-nya. Jawaban salah mengurangi 2 RP. Saat online, hasilnya dihitung untuk rank dan papan Kilat Mingguan.",
                        style = MaterialTheme.typography.bodySmall, color = cs.onSurfaceVariant)
                }
                PrimaryButton("📜 Riwayat Kilat", Modifier.appear(0), secondary = true, onClick = onHistory)
            } else if (mode.usesWords) {
                SoftCard(Modifier.fillMaxWidth().appear(0)) {
                    Text("Level", style = MaterialTheme.typography.titleMedium, color = cs.onSurface)
                    Spacer(Modifier.height(8.dp))
                    Segmented(listOf("HSK 1–4", "HSK 1", "HSK 2", "HSK 3", "HSK 4"), level, onSelect = onLevel)
                    Spacer(Modifier.height(16.dp))
                    Text("Sumber Kata", style = MaterialTheme.typography.titleMedium, color = cs.onSurface)
                    Spacer(Modifier.height(8.dp))
                    WordSource.entries.chunked(2).forEach { row ->
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(vertical = 4.dp)) {
                            row.forEach { src ->
                                val n = remember(mode, level, src, st.cards) { WordQuiz.available(mode, st, level, src) }
                                OptionTile(src.label, "$n kata", src == source, Modifier.weight(1f)) { onSource(src) }
                            }
                        }
                    }
                }
            } else {
                NumberSetup(numCat, onNumCat, numDiff, onNumDiff, rangeIdx, customLo, customHi, rangeError, onRange, onCustom, onLesson)
            }

            SoftCard(Modifier.fillMaxWidth().appear(1)) {
                if (kilat) {
                    val min = st.settings.kilatMinutes
                    Text("Durasi", style = MaterialTheme.typography.titleMedium, color = cs.onSurface)
                    Text("Skor dan RP plus dikali sesuai durasi. Jawab sebanyak mungkin sebelum waktu habis, tanpa bantuan.",
                        style = MaterialTheme.typography.bodySmall, color = cs.onSurfaceVariant)
                    Spacer(Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        com.nesxha.readit.data.RankRules.kilatMinutes.forEach { m ->
                            OptionTile("$m Menit", com.nesxha.readit.data.RankRules.multLabel(m), m == min, Modifier.weight(1f)) {
                                Store.settings { it.copy(kilatMinutes = m) }
                            }
                        }
                    }
                } else if (mode.fixedCount) {
                    Text("20 Soal · 10 Menit", style = MaterialTheme.typography.titleMedium, color = cs.onSurface)
                    Text(
                        "Soal campuran semua jenis. Lulus kalau nilainya minimal 60%. Bantuan pinyin tetap $HINTS_PER_SESSION×.",
                        style = MaterialTheme.typography.bodySmall, color = cs.onSurfaceVariant,
                    )
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text("Jumlah Soal", style = MaterialTheme.typography.titleMedium, color = cs.onSurface)
                            Text("Soal diacak setiap kali main.", style = MaterialTheme.typography.bodySmall, color = cs.onSurfaceVariant)
                        }
                        AnimatedNumber(realCount, MaterialTheme.typography.headlineMedium, cs.primary)
                    }
                    Spacer(Modifier.height(12.dp))
                    CountChips(count, if (mode.usesWords) available else 100, onCount)
                }
            }
            if (problem != null) Text(problem, style = MaterialTheme.typography.bodyMedium, color = Brand.error)
        }
        PrimaryButton(
            if (mode.fixedCount) "Mulai" else "Mulai $realCount Soal",
            Modifier.padding(18.dp).navigationBarsPadding(), enabled = problem == null,
        ) {
            onStart(
                PracticeConfig(
                    mode, effLevel, effSource, if (kilat) st.settings.kilatMinutes * 90 else realCount, numCat, numDiff,
                    range = if (mode == PracticeMode.ANGKA && numCat == NumCat.UMUM) {
                        if (rangeIdx >= 0) NumberQuiz.ranges[rangeIdx].second else parseRange(customLo, customHi).first
                    } else null,
                    minutes = st.settings.kilatMinutes,
                ),
            )
        }
    }
}

@Composable
private fun OptionTile(title: String, sub: String?, selected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    val bg by animateColorAsState(if (selected) cs.primary else cs.surfaceVariant, label = "opt")
    Column(
        modifier.clip(RoundedCornerShape(14.dp)).background(bg).bounceClick(onClick = onClick).padding(horizontal = 12.dp, vertical = 10.dp),
    ) {
        Text(title, style = MaterialTheme.typography.labelLarge, color = if (selected) Color.White else cs.onSurface, maxLines = 1)
        if (sub != null) Text(sub, style = MaterialTheme.typography.labelSmall, color = if (selected) Color.White.copy(alpha = 0.85f) else cs.onSurfaceVariant, maxLines = 1)
    }
}

/** Bagian persiapan khusus Kuis Angka: kategori, lalu jarak angka atau tingkat kesulitan. */
@Composable
private fun NumberSetup(
    cat: NumCat, onCat: (NumCat) -> Unit, diff: Int, onDiff: (Int) -> Unit,
    rangeIdx: Int, lo: String, hi: String, rangeError: String?, onRange: (Int) -> Unit, onCustom: (String, String) -> Unit,
    onLesson: (NumCat) -> Unit,
) {
    val cs = MaterialTheme.colorScheme
    SoftCard(Modifier.fillMaxWidth().appear(0)) {
        Text("Kategori", style = MaterialTheme.typography.titleMedium, color = cs.onSurface)
        Spacer(Modifier.height(8.dp))
        NumCat.entries.chunked(2).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(vertical = 4.dp)) {
                row.forEach { c -> OptionTile("${c.emoji} ${c.title}", c.sub, c == cat, Modifier.weight(1f)) { onCat(c) } }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
    if (cat == NumCat.UMUM) {
        RangePicker(rangeIdx, lo, hi, rangeError, onRange, onCustom, Modifier.appear(1))
    } else {
        SoftCard(Modifier.fillMaxWidth().appear(1)) {
            Text("Tingkat Kesulitan", style = MaterialTheme.typography.titleMedium, color = cs.onSurface)
            Spacer(Modifier.height(10.dp))
            Segmented(listOf("Mudah", "Sedang", "Sulit"), diff, onSelect = onDiff)
            Spacer(Modifier.height(8.dp))
            Text(diffDesc(cat, diff), style = MaterialTheme.typography.bodyMedium, color = cs.onSurfaceVariant)
        }
    }
    if (cat != NumCat.CAMPUR) {
        ActionRow(Icons.AutoMirrored.Rounded.MenuBook, "Pelajari Materinya Dulu", "Rumus dan contoh untuk ${cat.title}", Modifier.appear(2)) { onLesson(cat) }
    }
}
