package com.nesxha.readit.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Undo
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.DeleteSweep
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nesxha.readit.data.Counter
import com.nesxha.readit.data.Store
import com.nesxha.readit.data.Word
import com.nesxha.readit.data.WordQuiz
import com.nesxha.readit.data.Xp
import com.nesxha.readit.ui.components.ExitDialog
import com.nesxha.readit.ui.components.ResultsScreen
import com.nesxha.readit.ui.components.SectionTitle
import com.nesxha.readit.ui.components.WordRow
import com.nesxha.readit.ui.components.appear
import com.nesxha.readit.ui.components.Confetti
import com.nesxha.readit.ui.components.EmptyState
import com.nesxha.readit.ui.components.HeaderButton
import com.nesxha.readit.ui.components.LevelBadge
import com.nesxha.readit.ui.components.PinyinText
import com.nesxha.readit.ui.components.ProgressBar
import com.nesxha.readit.ui.components.ScreenHeader
import com.nesxha.readit.ui.components.SpeakButton
import com.nesxha.readit.ui.components.bounceClick
import com.nesxha.readit.ui.theme.Brand
import com.nesxha.readit.ui.theme.HanziFont
import com.nesxha.readit.util.Handwriting
import com.nesxha.readit.util.InkRecognizer
import com.nesxha.readit.platform.InkEngine
import androidx.compose.ui.layout.onSizeChanged
import com.nesxha.readit.util.Pt
import com.nesxha.readit.util.Tts
import com.nesxha.readit.util.Verdict
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.nesxha.readit.platform.BackHandler

/** Latihan menulis: lihat pinyin (dan arti), tulis hanzinya satu per satu. */
@Composable
fun WritingGame(cfg: PracticeConfig, round: Int, onExit: () -> Unit, onAgain: () -> Unit, onWord: (Int) -> Unit) {
    val words = remember(round) { WordQuiz.pool(Store.now, cfg.level, cfg.source, cfg.count, WordQuiz.writable) }
    val startXp = remember(round) { Store.now.xp }
    var wi by remember(round) { mutableIntStateOf(0) }
    var ci by remember(round) { mutableIntStateOf(0) }
    var score by remember(round) { mutableIntStateOf(0) }
    val wrongs = remember(round) { mutableStateListOf<Word>() }
    var failedWord by remember(round) { mutableStateOf(false) }
    var finished by remember(round) { mutableStateOf(false) }
    var confetti by remember { mutableIntStateOf(0) }
    var askExit by remember { mutableStateOf(false) }
    val started = wi > 0 || ci > 0

    BackHandler(enabled = !finished) { if (started) askExit = true else onExit() }
    if (askExit) ExitDialog("Keluar dari Latihan?", "Kata yang sudah ditulis tetap tercatat, tetapi latihan ini tidak selesai.", onStay = { askExit = false }) {
        askExit = false; onExit()
    }

    if (words.isEmpty()) {
        Column(Modifier.fillMaxSize()) {
            ScreenHeader("Tulis Hanzi", null, leading = { HeaderButton(Icons.Rounded.Close, "Tutup", onExit) })
            EmptyState("✍️", "Belum Ada Kata", "Kata untuk pilihan ini belum ada. Coba ganti level atau sumber kata.")
        }
        return
    }

    fun nextChar(ok: Boolean) {
        val w = words[wi]
        if (!ok) failedWord = true
        if (ci + 1 < w.hanzi.length) { ci++; return }
        val wordOk = !failedWord
        Store.quizAnswer(w.id, wordOk, if (wordOk) Xp.WRITE_CHAR * w.hanzi.length else Xp.WRITE_WRONG)
        if (wordOk) score++ else wrongs += w
        Tts.speak(w.hanzi)
        if (wi + 1 >= words.size) {
            finished = true
            if (score * 100 / words.size >= 80) confetti++
        } else { wi++; ci = 0; failedWord = false }
    }

    Box(Modifier.fillMaxSize()) {
        if (finished) {
            ResultsScreen("Tulis Hanzi", score, words.size, xp = Store.now.xp - startXp, hinted = 0, onAgain = onAgain, onExit = onExit) {
                val list = wrongs.distinct()
                if (list.isNotEmpty()) {
                    SectionTitle("Perlu Diulang", Modifier.appear(3))
                    list.forEachIndexed { i, w -> WordRow(w, Modifier.appear(4 + i)) { onWord(w.id) } }
                }
            }
        } else {
            val w = words[wi]
            Column(Modifier.fillMaxSize()) {
                ScreenHeader(
                    "Tulis Hanzi", "Kata ${wi + 1} dari ${words.size} · Benar $score",
                    leading = { HeaderButton(Icons.Rounded.Close, "Tutup") { if (started) askExit = true else onExit() } },
                ) {
                    ProgressBar(wi.toFloat() / words.size, color = Color.White, track = Color(0x33FFFFFF), height = 8.dp)
                }
                AnimatedContent(
                    targetState = wi,
                    transitionSpec = { (fadeIn(tween(300)) + scaleIn(tween(300), initialScale = 0.94f)) togetherWith fadeOut(tween(150)) },
                    label = "word",
                ) { idx ->
                    WritePad(words[idx], ci.takeIf { idx == wi } ?: 0, WordQuiz.isAmbiguous(words[idx]), key = "$round-$idx") { ok -> nextChar(ok) }
                }
            }
        }
        Confetti(confetti)
    }
}

@Composable
private fun WritePad(word: Word, charIndex: Int, showMeaning: Boolean, key: String, onDone: (Boolean) -> Unit) {
    val cs = MaterialTheme.colorScheme
    val scope = rememberCoroutineScope()
    val strokes = remember(key, charIndex) { mutableStateListOf<List<Pt>>() }
    var padSize by remember { mutableStateOf(androidx.compose.ui.geometry.Size(1f, 1f)) }
    LaunchedEffect(Unit) { InkRecognizer.ensure() }
    val current = remember(key, charIndex) { mutableStateListOf<Pt>() }
    var verdict by remember(key, charIndex) { mutableStateOf<Verdict?>(null) }
    var revealed by remember(key, charIndex) { mutableStateOf(false) }
    var checking by remember(key, charIndex) { mutableStateOf(false) }
    val target = word.hanzi[charIndex]
    val locked = verdict == Verdict.BENAR || revealed || checking
    val shake = remember { Animatable(0f) }
    val hintAlpha by animateFloatAsState(if (revealed || verdict == Verdict.BENAR) 1f else 0f, tween(250), label = "hint")
    val frame by animateColorAsState(
        when (verdict) { Verdict.BENAR -> Brand.success; Verdict.SALAH -> Brand.error; Verdict.HAMPIR -> Brand.warning; null -> cs.outline },
        label = "frame",
    )
    val ink = cs.primary
    val guide = cs.outline

    LaunchedEffect(verdict) {
        when (verdict) {
            Verdict.BENAR -> {
                Store.addCounter(Counter.WRITE_OK)
                delay(900); onDone(true)
            }
            Verdict.SALAH, Verdict.HAMPIR -> shake.animateTo(0f, keyframes {
                durationMillis = 420
                -14f at 60; 12f at 120; -8f at 180; 6f at 240; -3f at 300; 0f at 420
            })
            null -> {}
        }
    }

    Column(Modifier.fillMaxSize().padding(horizontal = 18.dp, vertical = 12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        // Soal: pinyin + arti + kotak per huruf
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            LevelBadge(word.level)
            Spacer(Modifier.weight(1f))
            SpeakButton(word.hanzi, onBrand = false)
        }
        PinyinText(word.pinyin, word.hanzi, size = 32.sp)
        // Arti hanya ditampilkan kalau ada kata lain dengan bacaan yang sama, supaya soalnya tidak ambigu.
        if (showMeaning) Text("Arti: ${word.main} (ada kata lain yang bacaannya sama)", style = MaterialTheme.typography.bodyMedium, color = cs.onSurfaceVariant, textAlign = TextAlign.Center, maxLines = 2)
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            word.hanzi.forEachIndexed { i, ch ->
                val done = i < charIndex
                val now = i == charIndex
                Box(
                    Modifier.size(42.dp).clip(RoundedCornerShape(12.dp))
                        .background(if (done) cs.primaryContainer else cs.surface)
                        .border(2.dp, if (now) cs.primary else cs.outline, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(if (done) ch.toString() else "?", fontFamily = HanziFont, fontSize = 22.sp, color = if (done) cs.onPrimaryContainer else cs.onSurfaceVariant, fontWeight = FontWeight.Bold)
                }
            }
        }
        Spacer(Modifier.height(10.dp))

        // Kotak tulis
        Box(
            Modifier.weight(1f).aspectRatio(1f, matchHeightConstraintsFirst = true)
                .graphicsLayer { translationX = shake.value.dp.toPx() }
                .clip(RoundedCornerShape(28.dp)).background(cs.surface)
                .border(3.dp, frame, RoundedCornerShape(28.dp))
                .onSizeChanged { padSize = androidx.compose.ui.geometry.Size(it.width.toFloat(), it.height.toFloat()) }
                .pointerInput(key, charIndex, locked) {
                    if (locked) return@pointerInput
                    awaitEachGesture {
                        val down = awaitFirstDown()
                        down.consume()
                        if (verdict != null) verdict = null
                        current.clear(); current.add(Pt(down.position.x, down.position.y))
                        while (true) {
                            val ev = awaitPointerEvent()
                            val ch = ev.changes.firstOrNull { it.id == down.id } ?: break
                            if (!ch.pressed) break
                            current.add(Pt(ch.position.x, ch.position.y)); ch.consume()
                        }
                        strokes.add(current.toList()); current.clear()
                    }
                },
            contentAlignment = Alignment.Center,
        ) {
            // Petunjuk / jawaban samar di belakang tulisan
            Text(
                target.toString(), fontFamily = HanziFont, fontSize = 200.sp,
                color = (if (verdict == Verdict.BENAR) Brand.success else cs.primary).copy(alpha = 0.16f * hintAlpha),
                textAlign = TextAlign.Center,
            )
            Canvas(Modifier.fillMaxSize()) {
                val dash = PathEffect.dashPathEffect(floatArrayOf(12f, 12f))
                val w = size.width; val h = size.height
                drawLine(guide, Offset(w / 2, 0f), Offset(w / 2, h), 2f, pathEffect = dash)
                drawLine(guide, Offset(0f, h / 2), Offset(w, h / 2), 2f, pathEffect = dash)
                drawLine(guide, Offset(0f, 0f), Offset(w, h), 1.5f, pathEffect = dash)
                drawLine(guide, Offset(w, 0f), Offset(0f, h), 1.5f, pathEffect = dash)
                val stroke = Stroke(width = 9.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                val color = when (verdict) { Verdict.BENAR -> Brand.success; Verdict.SALAH -> Brand.error; else -> ink }
                (strokes + listOf(current.toList())).forEach { s ->
                    if (s.isEmpty()) return@forEach
                    if (s.size == 1) { drawCircle(color, stroke.width / 2, Offset(s[0].x, s[0].y)); return@forEach }
                    val p = Path().apply { moveTo(s[0].x, s[0].y); s.drop(1).forEach { lineTo(it.x, it.y) } }
                    drawPath(p, color, style = stroke)
                }
            }
        }

        Spacer(Modifier.height(10.dp))
        val msg = when {
            checking -> "Memeriksa…"
            revealed -> "Jawabannya: $target"
            verdict == Verdict.BENAR -> "Benar! 🎉"
            verdict == Verdict.HAMPIR -> "Hampir! Coba tulis lebih rapi."
            verdict == Verdict.SALAH -> "Belum tepat. Coba lagi atau lihat jawaban."
            else -> "Tulis huruf ke-${charIndex + 1} di kotak"
        }
        Text(msg, style = MaterialTheme.typography.titleSmall, color = when (verdict) {
            Verdict.BENAR -> Brand.success; Verdict.SALAH -> Brand.error; Verdict.HAMPIR -> Brand.warning; null -> cs.onSurfaceVariant
        })
        Spacer(Modifier.height(10.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            if (revealed) {
                PadButton("Lanjut", null, primary = true) { onDone(false) }
            } else {
                PadButton("Urungkan", Icons.AutoMirrored.Rounded.Undo, enabled = !locked && strokes.isNotEmpty()) { strokes.removeAt(strokes.size - 1); verdict = null }
                PadButton("Hapus", Icons.Rounded.DeleteSweep, enabled = !locked && strokes.isNotEmpty()) { strokes.clear(); verdict = null }
                if (verdict == Verdict.SALAH || verdict == Verdict.HAMPIR) {
                    PadButton("Jawaban", Icons.Rounded.Visibility, enabled = true) { revealed = true }
                }
                PadButton("Periksa", null, primary = true, enabled = !locked && strokes.isNotEmpty()) {
                    checking = true
                    val snapshot = strokes.toList()
                    scope.launch {
                        // Pengenal Google lebih toleran: benar kalau hanzi target masuk 5 kandidat teratas.
                        val ml = if (InkRecognizer.state == InkEngine.State.SIAP) InkRecognizer.recognize(snapshot, padSize.width, padSize.height) else null
                        val pos = ml?.indexOf(target) ?: -1
                        val mlVerdict = when { pos in 0..4 -> Verdict.BENAR; pos in 5..9 -> Verdict.HAMPIR; else -> null }
                        val v = if (mlVerdict == Verdict.BENAR) mlVerdict
                        else withContext(Dispatchers.Default) { Handwriting.judge(target, snapshot) }.let { old ->
                            if (mlVerdict == Verdict.HAMPIR && old == Verdict.SALAH) Verdict.HAMPIR else old
                        }
                        checking = false
                        verdict = v
                    }
                }
            }
        }
    }
}

@Composable
private fun RowScope.PadButton(label: String, icon: ImageVector?, primary: Boolean = false, enabled: Boolean = true, onClick: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    val bg = when { !enabled -> cs.surfaceVariant; primary -> cs.primary; else -> cs.primaryContainer }
    val fg = when { !enabled -> cs.onSurfaceVariant.copy(alpha = 0.6f); primary -> Color.White; else -> cs.onPrimaryContainer }
    Column(
        Modifier.weight(if (primary) 1.4f else 1f).clip(RoundedCornerShape(18.dp)).background(bg)
            .bounceClick(enabled = enabled, onClick = onClick).padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (icon != null) Icon(icon, null, tint = fg, modifier = Modifier.size(20.dp))
        Text(label, color = fg, style = MaterialTheme.typography.labelMedium, maxLines = 1)
    }
}
