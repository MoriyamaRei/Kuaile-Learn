package com.nesxha.readit.ui.screens

import com.nesxha.readit.auth.AuthError
import com.nesxha.readit.auth.Background
import com.nesxha.readit.auth.KilatResult
import com.nesxha.readit.data.RankRules
import com.nesxha.readit.ui.components.c1
import com.nesxha.readit.auth.Member
import com.nesxha.readit.auth.Res
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nesxha.readit.data.Counter
import com.nesxha.readit.data.PracticeMode
import com.nesxha.readit.data.QType
import com.nesxha.readit.data.Question
import com.nesxha.readit.data.Store
import com.nesxha.readit.data.Word
import com.nesxha.readit.data.WordQuiz
import com.nesxha.readit.data.Xp
import com.nesxha.readit.ui.components.ChoiceButton
import com.nesxha.readit.ui.components.Confetti
import com.nesxha.readit.ui.components.EmptyState
import com.nesxha.readit.ui.components.ExitDialog
import com.nesxha.readit.ui.components.HanziText
import com.nesxha.readit.ui.components.HeaderButton
import com.nesxha.readit.ui.components.HintRow
import com.nesxha.readit.ui.components.LevelBadge
import com.nesxha.readit.ui.components.Pill
import com.nesxha.readit.ui.components.PinyinText
import com.nesxha.readit.ui.components.PrimaryButton
import com.nesxha.readit.ui.components.ProgressBar
import com.nesxha.readit.ui.components.ProgressRing
import com.nesxha.readit.ui.components.ResultsScreen
import com.nesxha.readit.ui.components.ScreenHeader
import com.nesxha.readit.ui.components.SectionTitle
import com.nesxha.readit.ui.components.SoftCard
import com.nesxha.readit.ui.components.SpeakButton
import com.nesxha.readit.ui.components.WordRow
import com.nesxha.readit.ui.components.appear
import com.nesxha.readit.ui.components.bounceClick
import com.nesxha.readit.ui.components.rememberFeedback
import com.nesxha.readit.ui.components.rememberHints
import com.nesxha.readit.ui.theme.Brand
import com.nesxha.readit.ui.theme.HanziFont
import com.nesxha.readit.util.Sentence
import com.nesxha.readit.util.Tts
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.nesxha.readit.platform.roundHalfUp
import com.nesxha.readit.platform.BackHandler

private fun timeText(s: Int) = if (s >= 60) "${s / 60}:${com.nesxha.readit.platform.pad2(s % 60)}" else "$s"

/** Kuis kosakata (semua mode kecuali Tulis Hanzi dan Kuis Angka). */
@Composable
fun WordQuizGame(cfg: PracticeConfig, round: Int, onExit: () -> Unit, onAgain: () -> Unit, onWord: (Int) -> Unit) {
    val mode = cfg.mode
    val questions = remember(round) { WordQuiz.build(mode, Store.now, cfg.level, cfg.source, cfg.count) }
    val startXp = remember(round) { Store.now.xp }
    val kilat = mode == PracticeMode.KILAT
    val minutes = cfg.minutes.coerceIn(1, 3)
    val limit = when (mode) { PracticeMode.KILAT -> minutes * 60; PracticeMode.UJIAN -> 600; else -> 0 }
    // Kilat: jawaban dikelompokkan menurut kecepatan (dihitung sejak soal tampil).
    var tally by remember(round) { mutableStateOf(RankRules.KilatTally()) }
    var shownAt by remember(round) { androidx.compose.runtime.mutableLongStateOf(0L) }
    var index by remember(round) { mutableIntStateOf(0) }
    var picked by remember(round) { mutableStateOf<Int?>(null) }
    var score by remember(round) { mutableIntStateOf(0) }
    var answered by remember(round) { mutableIntStateOf(0) }
    val wrongs = remember(round) { mutableStateListOf<Word>() }
    val hintedIdx = remember(round) { mutableStateListOf<Int>() }
    var timeLeft by remember(round) { mutableIntStateOf(limit) }
    var finished by remember(round) { mutableStateOf(false) }
    var askExit by remember { mutableStateOf(false) }
    var confetti by remember { mutableIntStateOf(0) }
    val hints = rememberHints(round)

    BackHandler(enabled = !finished) { if (answered > 0) askExit = true else onExit() }
    if (askExit) ExitDialog(
        if (mode == PracticeMode.KILAT) "Keluar dari Kuis Kilat?" else "Keluar dari Latihan?",
        if (kilat && Member.available) "Kalau keluar sekarang, hasilnya dihitung −10 RP (saat online) dan skornya tidak dicatat."
        else "Soal yang sudah dijawab tetap tercatat, tetapi latihan ini tidak selesai.",
        onStay = { askExit = false },
    ) {
        askExit = false
        if (kilat) {
            val t = tally
            if (Member.available) Background.scope.launch {
                val r = Member.kilatPlay(minutes, t, quit = true)
                if (r is Res.Err && r.error == AuthError.Network) com.nesxha.readit.data.KilatLocal.add(minutes, t.correct, t.wrong, 0, quit = true)
            } else com.nesxha.readit.data.KilatLocal.add(minutes, t.correct, t.wrong, 0, quit = true)
        }
        onExit()
    }

    if (questions.size < 2) {
        Column(Modifier.fillMaxSize()) {
            ScreenHeader(mode.title, null, leading = { HeaderButton(Icons.Rounded.Close, "Tutup", onExit) })
            EmptyState("🌱", "Belum Ada Soal", "Kata untuk pilihan ini belum cukup. Coba ganti level atau sumber kata.")
        }
        return
    }

    if (limit > 0) {
        LaunchedEffect(round) {
            while (timeLeft > 0 && !finished) { delay(1000); timeLeft-- }
            finished = true
        }
    }
    LaunchedEffect(index, round) { shownAt = com.nesxha.readit.platform.nowMs() }
    LaunchedEffect(picked) {
        val p = picked ?: return@LaunchedEffect
        val ms = com.nesxha.readit.platform.nowMs() - shownAt
        val q = questions[index]
        val ok = p == q.answer
        val helped = index in hintedIdx
        val xp = when {
            q.type == QType.SUSUN -> if (!ok) Xp.SENTENCE_WRONG else if (helped) Xp.SENTENCE_WRONG * 2 else Xp.SENTENCE_RIGHT
            !ok -> Xp.QUIZ_WRONG
            helped -> Xp.QUIZ_WRONG
            else -> Xp.QUIZ_RIGHT
        }
        Store.quizAnswer(q.word.id, ok, xp)
        if (q.type == QType.SUSUN && ok) Store.addCounter(Counter.SENTENCE_OK)
        answered++
        if (ok) score++ else wrongs += q.word
        if (kilat) tally = tally.add(ok, ms)
        Tts.speak(if (q.type == QType.SUSUN) q.sentence else q.word.hanzi)
        delay(
            when {
                mode == PracticeMode.KILAT -> if (ok) 450 else 1300
                q.type == QType.SUSUN -> if (ok) 1800 else 3200
                ok -> 1400
                else -> 2400
            },
        )
        if (index + 1 >= questions.size) finished = true else { index++; picked = null }
    }
    val total = if (mode == PracticeMode.KILAT) answered else questions.size
    var kilatNote by remember(round) { mutableStateOf<String?>(null) }
    var kilatRes by remember(round) { mutableStateOf<KilatResult?>(null) }
    var rankUp by remember(round) { mutableStateOf(false) }
    LaunchedEffect(finished) {
        if (!finished) return@LaunchedEffect
        val pct = if (total == 0) 0 else score * 100 / total
        when (mode) {
            PracticeMode.KILAT -> {
                val t = tally
                val sc = RankRules.kilatScore(t, minutes)
                Store.maxCounter(Counter.BEST_KILAT_SCORE, sc)
                if (Member.available) {
                    kilatNote = "Menghitung RP…"
                    when (val r = Member.kilatPlay(minutes, t, quit = false)) {
                        is Res.Ok -> {
                            kilatRes = r.value; kilatNote = null
                            com.nesxha.readit.ui.games.RankCache.status = r.value.status
                            com.nesxha.readit.ui.member.ProfilePrefs.cachedMe = com.nesxha.readit.ui.member.ProfilePrefs.cachedMe?.copy(
                                rankP = r.value.newP, weekKilat = r.value.weekBest, bestKilat = r.value.best)
                            if (RankRules.promoted(r.value.oldP, r.value.newP)) { delay(900); rankUp = true }
                        }
                        is Res.Err -> {
                            kilatNote = if (r.error == AuthError.Network) "Mode Offline: skor ini tidak dihitung untuk rank dan leaderboard."
                                else "Skor tidak dihitung: ${r.error.message}"
                            if (r.error == AuthError.Network) com.nesxha.readit.data.KilatLocal.add(minutes, t.correct, t.wrong, sc, quit = false)
                        }
                    }
                } else com.nesxha.readit.data.KilatLocal.add(minutes, t.correct, t.wrong, sc, quit = false)
            }
            PracticeMode.UJIAN -> {
                Store.maxCounter(Counter.examBest(cfg.level), pct)
                if (pct >= 60) { Store.addCounter(Counter.EXAM_PASS); Store.addXp(Xp.EXAM_PASS) }
                if (pct == 100) { Store.addCounter(Counter.EXAM_PERFECT); Store.addXp(Xp.EXAM_PERFECT) }
            }
            else -> {}
        }
        if (total > 0 && (pct >= 80 || (kilat && score >= 10))) confetti++
    }

    Box(Modifier.fillMaxSize()) {
        if (finished) {
            ResultsScreen(
                mode.title, score, total, xp = Store.now.xp - startXp, hinted = hintedIdx.size,
                onAgain = onAgain, onExit = onExit, exam = mode == PracticeMode.UJIAN, note = kilatNote,
            ) {
                if (kilat) KilatScoreCard(tally, minutes, Modifier.appear(1))
                kilatRes?.let { KilatRankCard(it, tally, minutes, Modifier.appear(2)) }
                val list = wrongs.distinct()
                if (list.isNotEmpty()) {
                    SectionTitle("Perlu Diulang", Modifier.appear(3))
                    list.forEachIndexed { i, w -> WordRow(w, Modifier.appear(4 + i)) { onWord(w.id) } }
                }
            }
        } else {
            Column(Modifier.fillMaxSize()) {
                ScreenHeader(
                    mode.title,
                    if (kilat) "Skor ${RankRules.kilatScore(tally, minutes)} · Benar ${tally.correct} · Salah ${tally.wrong}" else "Soal ${index + 1} dari ${questions.size} · Benar $score",
                    leading = { HeaderButton(Icons.Rounded.Close, "Tutup") { if (answered > 0) askExit = true else onExit() } },
                    trailing = {
                        if (limit > 0) {
                            ProgressRing(timeLeft / limit.toFloat(), Modifier.size(54.dp), stroke = 5.dp, color = if (timeLeft <= 10) Color(0xFFFCA5A5) else Color.White) {
                                Text(timeText(timeLeft), color = Color.White, style = MaterialTheme.typography.labelLarge)
                            }
                        }
                    },
                ) {
                    if (mode != PracticeMode.KILAT) ProgressBar((index + if (picked != null) 1 else 0).toFloat() / questions.size, color = Color.White, track = Color(0x33FFFFFF), height = 8.dp)
                }
                AnimatedContent(
                    targetState = index,
                    transitionSpec = {
                        (slideInHorizontally(spring(dampingRatio = 0.8f, stiffness = 300f)) { it } + fadeIn()) togetherWith
                            (slideOutHorizontally(tween(250)) { -it / 2 } + fadeOut(tween(200)))
                    },
                    label = "q",
                ) { i ->
                    val qq = questions[i]
                    val myPick = if (i == index) picked else null
                    Column(
                        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        if (mode == PracticeMode.UJIAN || mode == PracticeMode.KILAT) Pill(qq.type.label)
                        Prompt(qq, answered = myPick != null)
                        HintRow(
                            hints, questionKey = "$round-$i",
                            available = qq.type.hintable && mode != PracticeMode.KILAT,
                            answered = myPick != null, pinyin = qq.word.pinyin, hanzi = qq.word.hanzi,
                            onUsed = { if (i !in hintedIdx) hintedIdx += i },
                        )
                        if (qq.type == QType.SUSUN) {
                            SentenceTask(qq, myPick) { ok -> if (picked == null) picked = if (ok) 0 else 1 }
                        } else {
                            qq.options.forEachIndexed { oi, opt ->
                                val state = when { myPick == null -> 0; oi == qq.answer -> 1; myPick == oi -> 2; else -> 3 }
                                ChoiceButton(state, enabled = myPick == null, onClick = { if (picked == null) picked = oi }) { fg ->
                                    when (qq.type) {
                                        QType.ARTI_HANZI, QType.PINYIN_HANZI -> Text(opt, fontFamily = HanziFont, fontSize = 30.sp, fontWeight = FontWeight.Bold, color = fg)
                                        QType.NADA, QType.HANZI_PINYIN -> if (state == 0 || state == 3) PinyinText(opt, qq.word.hanzi, size = 22.sp)
                                            else Text(opt, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = fg, softWrap = false, maxLines = 1)
                                        else -> Text(opt, style = MaterialTheme.typography.titleMedium, color = fg, textAlign = TextAlign.Center)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        Confetti(confetti)
        if (rankUp) kilatRes?.let { com.nesxha.readit.ui.components.RankUpCelebration(it.oldP, it.newP) { rankUp = false } }
    }
}

/**
 * Soal: hanzi saja (kecuali soal yang memang berupa arti atau pinyin).
 * Setelah dijawab, pinyin, arti, dan tombol suara baru muncul.
 */
@Composable
private fun Prompt(q: Question, answered: Boolean) {
    val cs = MaterialTheme.colorScheme
    SoftCard(Modifier.fillMaxWidth(), radius = 28.dp) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            LevelBadge(q.word.level)
            Spacer(Modifier.weight(1f))
            if (answered) SpeakButton(if (q.type == QType.SUSUN) q.sentence else q.word.hanzi, onBrand = false)
        }
        Column(Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            val big = if (q.word.hanzi.length > 4) 42.sp else 64.sp
            when (q.type) {
                QType.HANZI_ARTI -> { HanziText(q.word.hanzi, size = big, color = cs.onSurface); Ask("Apa artinya?") }
                QType.ARTI_HANZI -> {
                    Text(q.word.main, color = cs.onSurface, style = MaterialTheme.typography.headlineSmall, textAlign = TextAlign.Center)
                    Ask("Pilih hanzi yang tepat")
                }
                QType.HANZI_PINYIN -> { HanziText(q.word.hanzi, size = big, color = cs.onSurface); Ask("Bagaimana cara membacanya?") }
                QType.PINYIN_HANZI -> { PinyinText(q.word.pinyin, q.word.hanzi, size = 34.sp); Ask("Hanzi mana yang dibaca seperti ini?") }
                QType.NADA -> { HanziText(q.word.hanzi, size = big, color = cs.onSurface); Ask("Pilih pinyin dengan nada yang benar") }
                QType.SUSUN -> {
                    Ask("Susun potongan menjadi kalimat yang benar")
                    Spacer(Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Kata kunci: ", style = MaterialTheme.typography.bodyMedium, color = cs.onSurfaceVariant)
                        Text(q.word.hanzi, fontFamily = HanziFont, fontSize = 22.sp, color = cs.primary, fontWeight = FontWeight.Bold)
                    }
                }
            }
            // Umpan balik: bacaan dan arti baru ditampilkan setelah menjawab.
            AnimatedVisibility(answered, enter = expandVertically() + fadeIn()) {
                Column(Modifier.padding(top = 10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    if (q.type != QType.HANZI_ARTI && q.type != QType.HANZI_PINYIN && q.type != QType.NADA) {
                        HanziText(q.word.hanzi, size = 30.sp, color = cs.primary)
                    }
                    PinyinText(q.word.pinyin, q.word.hanzi, size = 20.sp)
                    Text(q.word.main, style = MaterialTheme.typography.titleMedium, color = cs.onSurface, textAlign = TextAlign.Center)
                }
            }
        }
    }
}

@Composable
private fun Ask(text: String) {
    Spacer(Modifier.height(6.dp))
    Text(text, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)
}

/** Susun kalimat: ketuk potongan untuk menaruh, ketuk lagi untuk mengembalikan. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SentenceTask(q: Question, picked: Int?, onSubmit: (Boolean) -> Unit) {
    val cs = MaterialTheme.colorScheme
    val placed = remember(q) { mutableStateListOf<Int>() }
    val state = when (picked) { null -> 0; 0 -> 1; else -> 2 }
    val (shake, pop) = rememberFeedback(state)
    val border by animateColorAsState(when (state) { 1 -> Brand.success; 2 -> Brand.error; else -> cs.outline }, label = "sb")
    val end = Sentence.ending(q.sentence)

    Column(
        Modifier.fillMaxWidth()
            .graphicsLayer { translationX = shake.value.dp.toPx(); scaleX = pop.value; scaleY = pop.value }
            .clip(RoundedCornerShape(22.dp)).background(cs.surface)
            .border(2.dp, border, RoundedCornerShape(22.dp))
            .heightIn(min = 96.dp).padding(14.dp)
            .animateContentSize(),
    ) {
        if (placed.isEmpty()) Text("Ketuk potongan di bawah sesuai urutan…", style = MaterialTheme.typography.bodyMedium, color = cs.onSurfaceVariant)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            placed.forEach { i -> ChunkChip(q.options[i], filled = true, enabled = picked == null) { placed.remove(i) } }
            if (placed.size == q.options.size && end.isNotEmpty()) {
                Text(end, fontFamily = HanziFont, fontSize = 22.sp, color = cs.onSurface, modifier = Modifier.align(Alignment.CenterVertically))
            }
        }
    }
    if (state == 2) {
        SoftCard(Modifier.fillMaxWidth(), color = Brand.success.copy(alpha = 0.12f)) {
            Text("Kalimat yang Benar", style = MaterialTheme.typography.labelMedium, color = Brand.success)
            Text(q.sentence, fontFamily = HanziFont, fontSize = 22.sp, color = cs.onSurface)
        }
    }
    if (picked == null) {
        FlowRow(
            Modifier.fillMaxWidth().animateContentSize(),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            q.options.indices.filter { it !in placed }.forEach { i -> ChunkChip(q.options[i], filled = false, enabled = true) { placed.add(i) } }
        }
        val ready = placed.size == q.options.size
        PrimaryButton("Periksa", enabled = ready) { onSubmit(placed.joinToString("") { q.options[it] } == q.chunks.joinToString("")) }
    }
}

@Composable
private fun ChunkChip(text: String, filled: Boolean, enabled: Boolean, onClick: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    val appear = remember { Animatable(0.7f) }
    LaunchedEffect(Unit) { appear.animateTo(1f, spring(dampingRatio = 0.5f, stiffness = 500f)) }
    Box(
        Modifier.graphicsLayer { scaleX = appear.value; scaleY = appear.value }
            .clip(RoundedCornerShape(14.dp))
            .background(if (filled) cs.primaryContainer else cs.surface)
            .border(1.5.dp, if (filled) Color.Transparent else cs.outline, RoundedCornerShape(14.dp))
            .bounceClick(enabled = enabled, onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp),
    ) { Text(text, fontFamily = HanziFont, fontSize = 22.sp, color = if (filled) cs.onPrimaryContainer else cs.onSurface) }
}

/** Rincian skor Kilat: jumlah jawaban per kecepatan, lalu pengali durasi. */
@Composable
private fun KilatScoreCard(t: RankRules.KilatTally, minutes: Int, modifier: Modifier) {
    val cs = MaterialTheme.colorScheme
    SoftCard(modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("Skor Kilat", style = MaterialTheme.typography.titleMedium, color = cs.onSurface)
                Text("$minutes Menit · Pengali ${RankRules.multLabel(minutes)}", style = MaterialTheme.typography.bodySmall, color = cs.onSurfaceVariant)
            }
            Text("${RankRules.kilatScore(t, minutes)}", style = MaterialTheme.typography.headlineMedium, color = cs.primary)
        }
        Spacer(Modifier.height(8.dp))
        (0..3).forEach { b ->
            val n = t.count(b)
            Row(Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
                Text(RankRules.bucketLabels[b], style = MaterialTheme.typography.bodyMedium, color = cs.onSurface, modifier = Modifier.weight(1f))
                Text("$n × ${RankRules.bucketScore[b]} = ${n * RankRules.bucketScore[b]}", style = MaterialTheme.typography.bodyMedium, color = cs.onSurfaceVariant)
            }
        }
        Row(Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
            Text("Salah", style = MaterialTheme.typography.bodyMedium, color = Brand.error, modifier = Modifier.weight(1f))
            Text("${t.wrong} × 0 = 0", style = MaterialTheme.typography.bodyMedium, color = cs.onSurfaceVariant)
        }
        Spacer(Modifier.height(6.dp))
        Text("${RankRules.kilatRawScore(t)} ${RankRules.multLabel(minutes)} = ${RankRules.kilatScore(t, minutes)}",
            style = MaterialTheme.typography.labelLarge, color = cs.primary)
    }
}

/** Kartu RP Kilat di layar hasil: perhitungan, bonus, perlindungan, dan rank baru. */
@Composable
private fun KilatRankCard(r: KilatResult, t: RankRules.KilatTally, minutes: Int, modifier: Modifier) {
    val pos = RankRules.pos(r.newP)
    val factor = RankRules.tierFactor(r.oldP)
    val plus = roundHalfUp(RankRules.kilatRawRp(t) * RankRules.kilatMult(minutes) * factor).toInt()
    val base = plus + RankRules.WRONG_RP * t.wrong
    com.nesxha.readit.ui.components.BrandCard(
        modifier.fillMaxWidth(),
        brush = androidx.compose.ui.graphics.Brush.linearGradient(listOf(Color(0xFF0B1E5B), pos.tier.c1())),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            com.nesxha.readit.ui.components.RankEmblem(pos, 64.dp)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text("Rank Point", style = MaterialTheme.typography.titleSmall, color = Color.White)
                Text((if (r.delta >= 0) "+${r.delta}" else "${r.delta}") + " RP", style = MaterialTheme.typography.headlineSmall,
                    color = if (r.delta >= 0) Color(0xFF86EFAC) else Color(0xFFFCA5A5))
                Text(pos.label, style = MaterialTheme.typography.labelLarge, color = Color.White)
            }
        }
        Spacer(Modifier.height(10.dp))
        val note = Color.White.copy(alpha = 0.9f)
        Text("RP Plus: ${RankRules.kilatRawRp(t)} ${RankRules.multLabel(minutes)}" + (if (factor < 1.0) " ×${if (factor == 0.5) "0,5" else "0,75"} (Rank Tinggi)" else "") + " = +$plus",
            style = MaterialTheme.typography.bodySmall, color = note)
        if (t.wrong > 0) Text("Salah ${t.wrong} × 2 = −${t.wrong * 2}", style = MaterialTheme.typography.bodySmall, color = note)
        if (r.bonus) Text("🎁 Bonus Harian ×2: $base → ${base * 2} RP", style = MaterialTheme.typography.bodySmall, color = Color(0xFFFCD34D))
        if (base < 0 && r.delta > base) Text("🛡️ Terlindung setelah naik divisi, RP tidak turun penuh.", style = MaterialTheme.typography.bodySmall, color = Color.White)
        Text("⚡ Skor Terbaik Minggu Ini: ${r.weekBest}", style = MaterialTheme.typography.bodySmall, color = note)
    }
}
