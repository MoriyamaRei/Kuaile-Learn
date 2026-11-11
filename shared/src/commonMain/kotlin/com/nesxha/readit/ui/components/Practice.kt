package com.nesxha.readit.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nesxha.readit.ui.theme.Brand

/** Jumlah bantuan per sesi latihan dan lama pinyin ditampilkan. */
const val HINTS_PER_SESSION = 3
const val HINT_MILLIS = 3000

/** Keadaan bantuan selama satu sesi latihan. */
@Stable
class HintState(limit: Int = HINTS_PER_SESSION) {
    var left by mutableIntStateOf(limit)
        private set
    var used by mutableIntStateOf(0)
        private set

    /** Pakai satu bantuan; false kalau jatah sudah habis. */
    fun use(): Boolean {
        if (left <= 0) return false
        left--; used++
        return true
    }
}

@Composable
fun rememberHints(key: Any?): HintState = remember(key) { HintState() }

/**
 * Tombol bantuan + tampilan pinyin sementara.
 * [available] = soal ini boleh dibantu (mis. tidak untuk soal yang jawabannya pinyin).
 * [onUsed] dipanggil sekali saat bantuan dipakai untuk soal ini.
 */
@Composable
fun HintRow(
    hints: HintState,
    questionKey: Any,
    available: Boolean,
    answered: Boolean,
    pinyin: String?,
    hanzi: String?,
    onUsed: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val cs = MaterialTheme.colorScheme
    var showing by remember(questionKey) { mutableStateOf(false) }
    var usedHere by remember(questionKey) { mutableStateOf(false) }
    val timer = remember(questionKey) { Animatable(1f) }
    LaunchedEffect(showing) {
        if (showing) {
            timer.snapTo(1f)
            timer.animateTo(0f, tween(HINT_MILLIS, easing = LinearEasing))
            showing = false
        }
    }
    if (!available || pinyin == null || answered) return
    Column(modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        AnimatedVisibility(showing, enter = fadeIn() + scaleIn(initialScale = 0.9f), exit = fadeOut()) {
            Column(
                Modifier.clip(RoundedCornerShape(16.dp)).background(Brand.gold.copy(alpha = 0.18f)).padding(horizontal = 14.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(Modifier.horizontalScroll(rememberScrollState())) { PinyinText(pinyin, hanzi ?: "", size = 20.sp) }
                Spacer(Modifier.height(6.dp))
                ProgressBar(timer.value, Modifier.width(120.dp), color = Brand.gold, height = 4.dp)
            }
        }
        if (!showing) {
            val enabled = hints.left > 0
            Row(
                Modifier.clip(RoundedCornerShape(50))
                    .background(if (enabled) Brand.gold.copy(alpha = 0.18f) else cs.surfaceVariant)
                    .bounceClick(enabled = enabled) {
                        if (hints.use()) {
                            showing = true
                            if (!usedHere) { usedHere = true; onUsed() }
                        }
                    }
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Rounded.Search, null, tint = if (enabled) cs.onSurface else cs.onSurfaceVariant, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text(
                    if (enabled) "Bantuan Pinyin · ${hints.left}/$HINTS_PER_SESSION" else "Bantuan Habis",
                    style = MaterialTheme.typography.labelLarge, color = if (enabled) cs.onSurface else cs.onSurfaceVariant,
                )
            }
        }
    }
}

/** Ringkasan XP yang didapat dalam satu sesi (ditampilkan di layar hasil). */
@Composable
fun XpEarned(xp: Int, modifier: Modifier = Modifier) {
    Row(
        modifier.clip(RoundedCornerShape(50)).background(Brand.gold).padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(Icons.Rounded.Star, null, tint = Color(0xFF3B2600), modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(6.dp))
        AnimatedNumber(xp, MaterialTheme.typography.titleMedium, Color(0xFF3B2600), " XP")
    }
}

/** Konfirmasi keluar dari latihan yang sedang berjalan. */
@Composable
fun ExitDialog(title: String, text: String, onStay: () -> Unit, onLeave: () -> Unit) {
    AlertDialog(
        onDismissRequest = onStay,
        title = { Text(title) },
        text = { Text(text) },
        confirmButton = { TextButton(onClick = onLeave) { Text("Keluar", color = Brand.error) } },
        dismissButton = { TextButton(onClick = onStay) { Text("Lanjutkan") } },
    )
}

// ---------------- Komponen latihan yang dipakai bersama ----------------

/** Animasi umpan balik jawaban: [state] 1 = benar (membesar sebentar), 2 = salah (bergetar). */
@Composable
fun rememberFeedback(state: Int): Pair<androidx.compose.animation.core.Animatable<Float, androidx.compose.animation.core.AnimationVector1D>, androidx.compose.animation.core.Animatable<Float, androidx.compose.animation.core.AnimationVector1D>> {
    val shake = remember { Animatable(0f) }
    val pop = remember { Animatable(1f) }
    LaunchedEffect(state) {
        when (state) {
            2 -> shake.animateTo(0f, androidx.compose.animation.core.keyframes {
                durationMillis = 420
                -16f at 60; 14f at 120; -10f at 180; 8f at 240; -4f at 300; 0f at 420
            })
            1 -> { pop.animateTo(1.05f, tween(120)); pop.animateTo(1f, androidx.compose.animation.core.spring(dampingRatio = 0.4f)) }
        }
    }
    return shake to pop
}

/**
 * Kerangka tombol pilihan ganda. [state]: 0 belum dijawab, 1 jawaban benar, 2 pilihan salah, 3 pilihan lain.
 * [content] menerima warna teks yang sesuai.
 */
@Composable
fun ChoiceButton(state: Int, enabled: Boolean, onClick: () -> Unit, content: @Composable (Color) -> Unit) {
    val cs = MaterialTheme.colorScheme
    val bg by androidx.compose.animation.animateColorAsState(
        when (state) { 1 -> Brand.success; 2 -> Brand.error; else -> cs.surface }, tween(250), label = "choice",
    )
    val (shake, pop) = rememberFeedback(state)
    val fg = if (state == 1 || state == 2) Color.White else cs.onSurface
    Column(
        Modifier.fillMaxWidth()
            .graphicsLayerCompat(shake.value, pop.value, if (state == 3) 0.5f else 1f)
            .clip(RoundedCornerShape(20.dp)).background(bg)
            .then(if (state == 0) Modifier.border(1.5.dp, cs.outline, RoundedCornerShape(20.dp)) else Modifier)
            .bounceClick(enabled = enabled, onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) { content(fg) }
}

private fun Modifier.graphicsLayerCompat(shakeDp: Float, scale: Float, alpha: Float): Modifier =
    this.then(Modifier.graphicsLayer { translationX = shakeDp.dp.toPx(); scaleX = scale; scaleY = scale; this.alpha = alpha })

/** Tombol utama selebar layar. */
@Composable
fun PrimaryButton(text: String, modifier: Modifier = Modifier, enabled: Boolean = true, secondary: Boolean = false, onClick: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    val bg = when { !enabled -> cs.surfaceVariant; secondary -> cs.primaryContainer; else -> cs.primary }
    val fg = when { !enabled -> cs.onSurfaceVariant; secondary -> cs.onPrimaryContainer; else -> Color.White }
    Box(
        modifier.fillMaxWidth().clip(RoundedCornerShape(50)).background(bg).bounceClick(enabled = enabled, onClick = onClick).padding(vertical = 15.dp),
        contentAlignment = Alignment.Center,
    ) { Text(text, color = fg, style = MaterialTheme.typography.titleMedium, maxLines = 1) }
}

val COUNT_OPTIONS = listOf(10, 20, 30, 40, 50, 60, 70, 80, 90, 100)

/** Pilihan jumlah soal 10–100; angka di atas [maxAllowed] tidak bisa dipilih. */
@Composable
fun CountChips(selected: Int, maxAllowed: Int, onSelect: (Int) -> Unit) {
    val cs = MaterialTheme.colorScheme
    COUNT_OPTIONS.chunked(5).forEach { row ->
        Row(horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp), modifier = Modifier.padding(vertical = 4.dp)) {
            row.forEach { n ->
                val enabled = n <= maxOf(10, maxAllowed)
                val sel = n == selected
                val bg by androidx.compose.animation.animateColorAsState(
                    when { sel -> cs.primary; enabled -> cs.surfaceVariant; else -> cs.surfaceVariant.copy(alpha = 0.4f) }, label = "cnt",
                )
                Box(
                    Modifier.weight(1f).height(46.dp).clip(RoundedCornerShape(14.dp)).background(bg).bounceClick(enabled = enabled) { onSelect(n) },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        "$n", style = MaterialTheme.typography.titleSmall, maxLines = 1,
                        color = when { sel -> Color.White; enabled -> cs.onSurface; else -> cs.onSurfaceVariant.copy(alpha = 0.5f) },
                    )
                }
            }
        }
    }
}

/** Layar hasil yang sama untuk semua latihan. [details] = daftar soal yang perlu diulang. */
@Composable
fun ResultsScreen(
    title: String, score: Int, total: Int, xp: Int, hinted: Int,
    onAgain: () -> Unit, onExit: () -> Unit,
    exam: Boolean = false, note: String? = null,
    details: (@Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit)? = null,
) {
    val pct = if (total == 0) 0 else score * 100 / total
    val cs = MaterialTheme.colorScheme
    androidx.compose.foundation.layout.Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
    ) {
        ScreenHeader("Hasil", title, leading = { HeaderButton(Icons.Rounded.Close, "Tutup", onExit) })
        Column(Modifier.padding(18.dp), verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp)) {
            BrandCard(Modifier.fillMaxWidth().appear(0)) {
                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(if (pct >= 80) "🏆" else if (pct >= 60) "👍" else "💪", fontSize = 52.sp)
                    if (exam) {
                        Pill(if (pct >= 60) "Lulus" else "Belum Lulus", color = if (pct >= 60) Brand.success else Color(0x33FFFFFF), textColor = Color.White)
                        Spacer(Modifier.height(6.dp))
                    }
                    Row(verticalAlignment = Alignment.Bottom) {
                        AnimatedNumber(score, MaterialTheme.typography.displaySmall, Color.White)
                        Text(" / $total", style = MaterialTheme.typography.headlineSmall, color = Color.White.copy(alpha = 0.85f))
                    }
                    Text(
                        when { pct >= 80 -> "Luar biasa! Pertahankan."; pct >= 60 -> "Bagus! Sedikit lagi sempurna."; else -> "Tidak apa-apa, ayo coba lagi." },
                        color = Color.White, style = MaterialTheme.typography.titleMedium, textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    )
                    Spacer(Modifier.height(10.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (xp > 0) XpEarned(xp)
                        if (hinted > 0) {
                            Spacer(Modifier.width(8.dp))
                            Pill("🔍 $hinted soal dibantu", color = Color(0x33FFFFFF), textColor = Color.White)
                        }
                    }
                }
            }
            if (note != null) {
                SoftCard(Modifier.fillMaxWidth().appear(1), color = Brand.gold.copy(alpha = 0.14f)) {
                    Text("Tahukah Kamu?", style = MaterialTheme.typography.titleMedium, color = cs.onSurface)
                    Spacer(Modifier.height(6.dp))
                    Text(note, style = MaterialTheme.typography.bodyMedium, color = cs.onSurface)
                }
            }
            Row(horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp), modifier = Modifier.appear(2)) {
                PrimaryButton("Main Lagi", Modifier.weight(1f), onClick = onAgain)
                PrimaryButton("Ganti Pengaturan", Modifier.weight(1f), secondary = true, onClick = onExit)
            }
            if (details != null) details()
            Spacer(Modifier.navigationBarsPadding().height(12.dp))
        }
    }
}
