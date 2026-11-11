package com.nesxha.readit.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.nesxha.readit.data.RankPos
import com.nesxha.readit.data.RankRules
import com.nesxha.readit.data.RankTier
import com.nesxha.readit.ui.theme.HanziFont
import kotlin.math.cos
import kotlin.math.sin
import com.nesxha.readit.platform.toRadians

fun RankTier.c1() = Color(colors[0])
fun RankTier.c2() = Color(colors[1])

/**
 * Lencana rank berbentuk perisai dengan hanzi tingkatnya.
 * Iron–Gold polos; Platinum–Diamond berkilau halus; Obsidian, Phoenix, Dragon beranimasi penuh.
 */
@Composable
fun RankEmblem(pos: RankPos, size: Dp, modifier: Modifier = Modifier, animate: Boolean = true) {
    val t = pos.tier
    val level = if (!animate) 0 else when (t.index) { in 0..3 -> 0; 4, 5 -> 1; else -> 2 }
    val tr = rememberInfiniteTransition(label = "emblem")
    val spin by tr.animateFloat(0f, 360f, infiniteRepeatable(tween(9000, easing = LinearEasing)), label = "spin")
    val pulse by tr.animateFloat(0f, 1f, infiniteRepeatable(tween(1600), RepeatMode.Reverse), label = "pulse")
    val gleam by tr.animateFloat(-0.6f, 1.6f, infiniteRepeatable(tween(2600, delayMillis = 900, easing = LinearEasing)), label = "gleam")
    Box(modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val w = this.size.width; val h = this.size.height
            val r = w / 2
            if (level == 2) {
                // Aura berdenyut dan sinar berputar.
                drawCircle(Brush.radialGradient(listOf(t.c2().copy(alpha = 0.5f + 0.3f * pulse), Color.Transparent)), r)
                for (i in 0 until 12) {
                    val a = toRadians((spin + i * 30f).toDouble())
                    val p1 = Offset(center.x + cos(a).toFloat() * r * 0.62f, center.y + sin(a).toFloat() * r * 0.62f)
                    val p2 = Offset(center.x + cos(a).toFloat() * r * (0.86f + 0.1f * pulse), center.y + sin(a).toFloat() * r * (0.86f + 0.1f * pulse))
                    drawLine(t.c2().copy(alpha = 0.55f), p1, p2, w * 0.02f, androidx.compose.ui.graphics.StrokeCap.Round)
                }
                for (i in 0 until 6) {
                    val a = toRadians((-spin * 1.6f + i * 60f).toDouble())
                    drawCircle(if (i % 2 == 0) Color.White else t.c2(), r * 0.045f,
                        Offset(center.x + cos(a).toFloat() * r * 0.95f, center.y + sin(a).toFloat() * r * 0.95f))
                }
            } else if (level == 1) {
                drawCircle(Brush.radialGradient(listOf(t.c2().copy(alpha = 0.25f + 0.15f * pulse), Color.Transparent)), r)
            }
            val div = pos.division
            // Divisi I: sayap di kiri dan kanan perisai.
            if (div == 1) for (side in listOf(-1f, 1f)) {
                val cx = w / 2
                val wing = Path().apply {
                    moveTo(cx + side * w * 0.30f, h * 0.30f)
                    quadraticBezierTo(cx + side * w * 0.52f, h * 0.18f, cx + side * w * 0.50f, h * 0.42f)
                    quadraticBezierTo(cx + side * w * 0.46f, h * 0.52f, cx + side * w * 0.38f, h * 0.50f)
                    quadraticBezierTo(cx + side * w * 0.44f, h * 0.62f, cx + side * w * 0.32f, h * 0.66f)
                    close()
                }
                drawPath(wing, Brush.linearGradient(listOf(Color.White.copy(alpha = 0.9f), t.c2()), Offset(cx, 0f), Offset(cx + side * w * 0.5f, h * 0.6f)))
                drawPath(wing, t.c1().copy(alpha = 0.7f), style = Stroke(w * 0.015f))
            }
            // Dragon: mahkota naga di atas perisai.
            val top = if (pos.isDragon) 0.20f else 0.08f
            if (pos.isDragon) {
                val crown = Path().apply {
                    moveTo(w * 0.28f, h * 0.24f); lineTo(w * 0.26f, h * 0.06f); lineTo(w * 0.38f, h * 0.15f)
                    lineTo(w * 0.5f, h * 0.0f); lineTo(w * 0.62f, h * 0.15f); lineTo(w * 0.74f, h * 0.06f); lineTo(w * 0.72f, h * 0.24f); close()
                }
                drawPath(crown, Brush.verticalGradient(listOf(Color(0xFFFDE68A), Color(0xFFF59E0B)), 0f, h * 0.24f))
                drawPath(crown, Color(0xFF7C2D12), style = Stroke(w * 0.015f))
                drawCircle(Color(0xFFDC2626), w * 0.03f, Offset(w * 0.5f, h * 0.12f))
            }
            fun shield(inset: Float) = Path().apply {
                val l = 0.14f + inset; val r = 0.86f - inset; val tp = top + inset * 1.1f; val b = 0.94f - inset * 1.2f
                moveTo(w * 0.5f, h * tp)
                lineTo(w * r, h * (tp + 0.14f))
                lineTo(w * (r - 0.04f), h * (b - 0.32f))
                quadraticBezierTo(w * (r - 0.14f), h * (b - 0.10f), w * 0.5f, h * b)
                quadraticBezierTo(w * (l + 0.14f), h * (b - 0.10f), w * (l + 0.04f), h * (b - 0.32f))
                lineTo(w * l, h * (tp + 0.14f))
                close()
            }
            val path = shield(0f)
            drawPath(path, Brush.linearGradient(listOf(t.c2(), t.c1()), Offset(0f, 0f), Offset(w, h)))
            drawPath(path, Color.White.copy(alpha = 0.55f), style = Stroke(w * 0.035f))
            // Divisi II, I, dan Dragon: bingkai ganda.
            if (div in 1..2 || pos.isDragon) drawPath(shield(0.06f), Color.White.copy(alpha = 0.5f), style = Stroke(w * 0.02f))
            drawPath(Path().apply {
                moveTo(w * 0.5f, h * (top + 0.04f)); lineTo(w * 0.80f, h * (top + 0.17f)); lineTo(w * 0.5f, h * (top + 0.30f)); lineTo(w * 0.20f, h * (top + 0.17f)); close()
            }, Color.White.copy(alpha = 0.18f))
            if (level >= 1) {
                // Kilau cahaya yang menyapu perisai.
                clipPath(path) {
                    val x = w * gleam
                    drawRect(Brush.linearGradient(listOf(Color.Transparent, Color.White.copy(alpha = 0.55f), Color.Transparent),
                        Offset(x - w * 0.3f, 0f), Offset(x, h)))
                }
            }
        }
        Column(Modifier.padding(top = if (pos.isDragon) size * 0.12f else 0.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(t.hanzi, fontFamily = HanziFont, fontWeight = FontWeight.Bold, color = Color.White, fontSize = (size.value * 0.34f).sp)
            // Bintang divisi: III ★, II ★★, I ★★★.
            if (!pos.isDragon) Text("★".repeat(4 - pos.division), color = Color(0xFFFDE68A), fontSize = (size.value * 0.12f).sp, letterSpacing = 0.sp)
        }
    }
}

/** Pil kecil "Gold II" dengan warna tingkat. */
@Composable
fun RankPill(pos: RankPos, modifier: Modifier = Modifier) {
    Row(
        modifier.clip(RoundedCornerShape(50)).background(Brush.horizontalGradient(listOf(pos.tier.c1(), pos.tier.c2())))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(pos.tier.hanzi, fontFamily = HanziFont, color = Color.White, fontSize = 13.sp)
        Spacer(Modifier.size(4.dp))
        Text(pos.label, style = MaterialTheme.typography.labelMedium, color = Color.White, maxLines = 1)
    }
}

/**
 * Bingkai foto profil mengikuti rank: Iron–Gold polos, Platinum–Diamond berkilau halus,
 * Obsidian berpercik ungu, Phoenix berapi, Dragon beraura emas dan api.
 */
@Composable
fun RankFrame(pos: RankPos?, size: Dp, xp: Float? = null, modifier: Modifier = Modifier, content: @Composable BoxScope.() -> Unit) {
    val tr = rememberInfiniteTransition(label = "frame")
    val spin by tr.animateFloat(0f, 360f, infiniteRepeatable(tween(6000, easing = LinearEasing)), label = "spin")
    val flick by tr.animateFloat(0f, 1f, infiniteRepeatable(tween(700), RepeatMode.Reverse), label = "flick")
    val idx = pos?.tier?.index ?: -1
    Box(modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val r = this.size.minDimension / 2
            val stroke = r * 0.07f
            if (pos != null) {
                val t = pos.tier
                val ringR = r - stroke * 1.4f
                when {
                    idx == 6 -> for (i in 0 until 10) {
                        // Obsidian: percikan ungu mengorbit.
                        val a = toRadians((spin * 1.3f + i * 36f).toDouble())
                        val rr = r - stroke * (0.6f + 0.5f * ((i % 2) * flick))
                        drawCircle(if (i % 2 == 0) t.c2() else Color.White, stroke * 0.45f,
                            Offset(center.x + cos(a).toFloat() * rr, center.y + sin(a).toFloat() * rr))
                    }
                    idx >= 7 -> {
                        if (idx == 8) drawCircle(Brush.radialGradient(listOf(t.c2().copy(alpha = 0.45f + 0.2f * flick), Color.Transparent)), r)
                        for (i in 0 until 14) {
                            // Phoenix/Dragon: lidah api berdenyut di luar bingkai.
                            val a = toRadians((spin + i * (360.0 / 14)).toDouble())
                            val len = r * (0.10f + 0.06f * ((i % 3) / 2f) + 0.05f * flick)
                            val p1 = Offset(center.x + cos(a).toFloat() * (r - stroke), center.y + sin(a).toFloat() * (r - stroke))
                            val p2 = Offset(center.x + cos(a).toFloat() * (r - stroke + len), center.y + sin(a).toFloat() * (r - stroke + len))
                            drawLine(if (i % 2 == 0) t.c2() else t.c1(), p1, p2, stroke * 0.9f, androidx.compose.ui.graphics.StrokeCap.Round)
                        }
                    }
                }
                if (idx <= 3) {
                    drawCircle(Brush.linearGradient(listOf(t.c2(), t.c1())), ringR, style = Stroke(stroke * 1.6f))
                } else {
                    // Platinum ke atas: cahaya bergerak mengelilingi bingkai.
                    rotate(if (idx >= 4) spin else 0f) {
                        drawCircle(Brush.sweepGradient(listOf(t.c1(), t.c2(), Color.White, t.c2(), t.c1())), ringR, style = Stroke(stroke * 1.6f))
                    }
                }
            }
            if (xp != null) {
                val rr = r - stroke * 3.2f
                drawCircle(Color.White.copy(alpha = 0.25f), rr, style = Stroke(stroke * 0.7f))
                drawArc(Color(0xFFFBBF24), -90f, 360f * xp.coerceIn(0f, 1f), false,
                    Offset(center.x - rr, center.y - rr), androidx.compose.ui.geometry.Size(rr * 2, rr * 2),
                    style = Stroke(stroke * 0.7f, cap = androidx.compose.ui.graphics.StrokeCap.Round))
            }
        }
        Box(Modifier.padding(size * 0.14f), contentAlignment = Alignment.Center, content = content)
    }
}

/** Perayaan Rank Up: lencana lama berganti lencana baru dengan cahaya dan konfeti. */
@Composable
fun RankUpCelebration(old: Int, new: Int, onDone: () -> Unit) {
    val a = remember { Animatable(0f) }
    LaunchedEffect(Unit) { a.animateTo(1f, spring(dampingRatio = 0.5f, stiffness = 120f)) }
    val pn = RankRules.pos(new)
    val tierUp = RankRules.tierUp(old, new)
    Dialog(onDismissRequest = onDone) {
        Box(contentAlignment = Alignment.Center) {
            Column(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(32.dp))
                    .background(Brush.verticalGradient(listOf(Color(0xFF0B1E5B), pn.tier.c1()))).padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center,
            ) {
                Text(if (tierUp) "RANK UP!" else "Naik Divisi!", style = MaterialTheme.typography.headlineMedium, color = Color.White)
                Spacer(Modifier.height(16.dp))
                RankEmblem(pn, 150.dp, Modifier.graphicsLayer { scaleX = a.value; scaleY = a.value; rotationZ = (1f - a.value) * 180f })
                Spacer(Modifier.height(14.dp))
                Text(pn.label, style = MaterialTheme.typography.headlineSmall, color = Color.White)
                Text("${pn.tier.hanzi} · ${pn.tier.pinyin}", style = MaterialTheme.typography.bodyLarge, color = Color.White.copy(alpha = 0.85f), textAlign = TextAlign.Center)
                Spacer(Modifier.height(18.dp))
                Box(
                    Modifier.clip(RoundedCornerShape(50)).background(Color.White).bounceClick(onClick = onDone).padding(horizontal = 30.dp, vertical = 12.dp),
                ) { Text("Mantap!", style = MaterialTheme.typography.labelLarge, color = pn.tier.c1()) }
            }
            Confetti(1)
        }
    }
}
