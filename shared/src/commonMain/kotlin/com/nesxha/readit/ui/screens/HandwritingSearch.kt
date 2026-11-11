package com.nesxha.readit.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Undo
import androidx.compose.material.icons.rounded.DeleteSweep
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.nesxha.readit.ui.components.bounceClick
import com.nesxha.readit.ui.theme.HanziFont
import com.nesxha.readit.util.Handwriting
import com.nesxha.readit.util.InkRecognizer
import com.nesxha.readit.platform.InkEngine
import androidx.compose.ui.layout.onSizeChanged
import com.nesxha.readit.util.Pt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Cari hanzi lewat tulisan: tulis satu hanzi dengan urutan goresan bebas,
 * lalu pilih dari kandidat yang paling mirip. Kandidat yang dipilih ditambahkan ke pencarian.
 */
@Composable
fun HandwritingSearchDialog(onPick: (Char) -> Unit, onClose: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    val strokes = remember { mutableStateListOf<List<Pt>>() }
    val current = remember { mutableStateListOf<Pt>() }
    var ready by remember { mutableStateOf(false) }
    var progress by remember { mutableFloatStateOf(0f) }
    var candidates by remember { mutableStateOf<List<Char>>(emptyList()) }
    var picked by remember { mutableStateOf("") }
    var ink by remember { mutableStateOf(InkRecognizer.state) }
    var padSize by remember { mutableStateOf(androidx.compose.ui.geometry.Size(1f, 1f)) }
    val dict = remember { Handwriting.allChars.toHashSet() }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.Default) { Handwriting.prepare() { progress = it } }
        ready = true
    }
    LaunchedEffect(Unit) { InkRecognizer.ensure { ink = it } }
    LaunchedEffect(strokes.size, ready, ink) {
        val snap = strokes.toList()
        if (snap.isEmpty()) { candidates = emptyList(); return@LaunchedEffect }
        // Utama: pengenal Google (hanya hanzi yang ada di kamus); sisanya diisi pengenal lama.
        val ml = InkRecognizer.recognize(snap, padSize.width, padSize.height)?.filter { it in dict }.orEmpty()
        val old = if (ready) withContext(Dispatchers.Default) { Handwriting.rank(snap, 10) } else emptyList()
        candidates = (ml + old).distinct().take(10)
    }

    Dialog(onDismissRequest = onClose, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Column(
            Modifier.fillMaxWidth().padding(16.dp).clip(RoundedCornerShape(28.dp)).background(cs.surface).padding(18.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Cari Lewat Tulisan", style = MaterialTheme.typography.titleLarge, color = cs.onSurface)
                    Text(
                        when {
                            !InkRecognizer.available -> "Tulis satu hanzi dengan rapi. Urutan goresan bebas."
                            ink == InkEngine.State.MENGUNDUH -> "Mengunduh pengenal tulisan Google (±20 MB, sekali saja)…"
                            ink == InkEngine.State.GAGAL -> "Pengenal Google belum tersedia (butuh internet sekali). Memakai pengenal dasar."
                            else -> "Tulis satu hanzi. Urutan goresan bebas."
                        },
                        style = MaterialTheme.typography.bodySmall, color = cs.onSurfaceVariant,
                    )
                }
                if (picked.isNotEmpty()) Text(picked, fontFamily = HanziFont, fontSize = 26.sp, color = cs.primary)
            }
            Spacer(Modifier.height(12.dp))

            // Kandidat (2 baris × 5, semua terlihat tanpa digeser)
            Box(Modifier.fillMaxWidth().height(124.dp), contentAlignment = Alignment.CenterStart) {
                when {
                    !ready && ink != InkEngine.State.SIAP -> Column(Modifier.fillMaxWidth()) {
                        Text("Menyiapkan pengenal tulisan…", style = MaterialTheme.typography.bodySmall, color = cs.onSurfaceVariant)
                        Spacer(Modifier.height(6.dp))
                        LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth())
                    }
                    candidates.isEmpty() -> Text("Kandidat hanzi akan muncul di sini.", style = MaterialTheme.typography.bodyMedium, color = cs.onSurfaceVariant)
                    else -> AnimatedContent(candidates, label = "cand", transitionSpec = { (fadeIn() + scaleIn(initialScale = 0.9f)) togetherWith fadeOut() }) { list ->
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) { list.chunked(5).forEachIndexed { row, part -> Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            part.forEachIndexed { j, c ->
                                val i = row * 5 + j
                                Box(
                                    Modifier.weight(1f).height(56.dp).clip(RoundedCornerShape(16.dp))
                                        .background(if (i == 0) cs.primary else cs.primaryContainer)
                                        .bounceClick {
                                            picked += c; onPick(c)
                                            strokes.clear(); current.clear(); candidates = emptyList()
                                        },
                                    contentAlignment = Alignment.Center,
                                ) { Text(c.toString(), fontFamily = HanziFont, fontSize = 30.sp, color = if (i == 0) androidx.compose.ui.graphics.Color.White else cs.onPrimaryContainer) }
                            }
                            repeat(5 - part.size) { Spacer(Modifier.weight(1f)) }
                        } } }
                    }
                }
            }
            Spacer(Modifier.height(12.dp))

            // Kotak tulis
            val ink = cs.onSurface
            val guide = cs.outline
            Box(
                Modifier.fillMaxWidth().aspectRatio(1f).clip(RoundedCornerShape(24.dp)).background(cs.background)
                    .border(2.dp, cs.outline, RoundedCornerShape(24.dp))
                    .onSizeChanged { padSize = androidx.compose.ui.geometry.Size(it.width.toFloat(), it.height.toFloat()) }
                    .pointerInput(Unit) {
                        awaitEachGesture {
                            val down = awaitFirstDown(); down.consume()
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
            ) {
                Canvas(Modifier.fillMaxSize()) {
                    val dash = PathEffect.dashPathEffect(floatArrayOf(12f, 12f))
                    val w = size.width; val h = size.height
                    drawLine(guide, Offset(w / 2, 0f), Offset(w / 2, h), 2f, pathEffect = dash)
                    drawLine(guide, Offset(0f, h / 2), Offset(w, h / 2), 2f, pathEffect = dash)
                    val stroke = Stroke(width = 9.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                    (strokes + listOf(current.toList())).forEach { s ->
                        if (s.isEmpty()) return@forEach
                        if (s.size == 1) { drawCircle(ink, stroke.width / 2, Offset(s[0].x, s[0].y)); return@forEach }
                        val p = Path().apply { moveTo(s[0].x, s[0].y); s.drop(1).forEach { lineTo(it.x, it.y) } }
                        drawPath(p, ink, style = stroke)
                    }
                }
                if (!ready) CircularProgressIndicator(Modifier.align(Alignment.Center))
            }
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SmallButton("Urungkan", Icons.AutoMirrored.Rounded.Undo, strokes.isNotEmpty(), Modifier.weight(1f)) { strokes.removeAt(strokes.size - 1) }
                SmallButton("Bersihkan", Icons.Rounded.DeleteSweep, strokes.isNotEmpty(), Modifier.weight(1f)) { strokes.clear() }
                SmallButton("Selesai", null, true, Modifier.weight(1f), primary = true, onClick = onClose)
            }
        }
    }
}

@Composable
private fun SmallButton(text: String, icon: androidx.compose.ui.graphics.vector.ImageVector?, enabled: Boolean, modifier: Modifier, primary: Boolean = false, onClick: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    val a by animateFloatAsState(if (enabled) 1f else 0.4f, label = "a")
    Row(
        modifier.clip(RoundedCornerShape(50)).background(if (primary) cs.primary else cs.primaryContainer)
            .bounceClick(enabled = enabled, onClick = onClick).padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically,
    ) {
        val fg = (if (primary) androidx.compose.ui.graphics.Color.White else cs.onPrimaryContainer).copy(alpha = a)
        if (icon != null) { Icon(icon, null, tint = fg, modifier = Modifier.size(18.dp)); Spacer(Modifier.size(6.dp)) }
        Text(text, style = MaterialTheme.typography.labelLarge, color = fg, maxLines = 1)
    }
}
