package com.nesxha.readit.ui.auth

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Backspace
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import org.jetbrains.compose.resources.painterResource
import com.nesxha.readit.res.Res as AppRes
import com.nesxha.readit.res.mascot_head
import com.nesxha.readit.res.mascot_full
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nesxha.readit.ui.theme.HanziFont
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

/** Warna layar akun: selalu biru tua, tidak ikut mode gelap/terang. */
internal object AuthColors {
    val deep = Color(0xFF071A4D)
    val mid = Color(0xFF1238A8)
    val bright = Color(0xFF2563EB)
    val glow = Color(0xFF60A5FA)
    val cyan = Color(0xFF38BDF8)
    val violet = Color(0xFF818CF8)
    val glass = Color(0x1FFFFFFF)
    val glassStrong = Color(0x33FFFFFF)
    val line = Color(0x40FFFFFF)
    val soft = Color(0xCCFFFFFF)
    val faint = Color(0x99FFFFFF)
    val error = Color(0xFFFCA5A5)
    val success = Color(0xFF86EFAC)
    val gold = Color(0xFFFCD34D)
}

private val FLOATING = listOf("读", "学", "写", "听", "说", "字", "书", "文", "汉", "语", "乐", "友")

/** Latar biru dengan cahaya yang bergerak pelan dan hanzi yang melayang. */
@Composable
internal fun AuroraBackground(modifier: Modifier = Modifier, hanzi: Boolean = true, content: @Composable BoxScope.() -> Unit) {
    val tr = rememberInfiniteTransition(label = "aurora")
    val p by tr.animateFloat(0f, 1f, infiniteRepeatable(tween(14000, easing = LinearEasing), RepeatMode.Reverse), label = "p")
    val q by tr.animateFloat(0f, 1f, infiniteRepeatable(tween(20000, easing = LinearEasing)), label = "q")
    Box(modifier.fillMaxSize().background(Brush.verticalGradient(listOf(AuthColors.deep, AuthColors.mid, AuthColors.bright)))) {
        Canvas(Modifier.fillMaxSize()) {
            val w = size.width; val h = size.height
            fun glow(c: Color, x: Float, y: Float, r: Float) =
                drawCircle(Brush.radialGradient(listOf(c, Color.Transparent), Offset(x, y), r), r, Offset(x, y))
            glow(AuthColors.cyan.copy(alpha = 0.35f), w * (0.15f + 0.25f * p), h * (0.18f + 0.06f * p), w * 0.75f)
            glow(AuthColors.violet.copy(alpha = 0.30f), w * (0.95f - 0.3f * p), h * (0.55f - 0.1f * p), w * 0.8f)
            glow(AuthColors.glow.copy(alpha = 0.35f), w * (0.4f + 0.2f * sin(q * 6.28f)), h * 0.95f, w * 0.9f)
        }
        if (hanzi) BoxWithConstraints(Modifier.fillMaxSize()) {
            val wPx = constraints.maxWidth.toFloat(); val hPx = constraints.maxHeight.toFloat()
            FLOATING.forEachIndexed { i, ch ->
                val lane = (i * 0.083f + 0.04f) % 1f
                val speed = 0.6f + (i % 3) * 0.25f
                val phase = ((q * speed + i * 0.17f) % 1f)
                val x = lane * wPx + sin((q + i) * 6.28f) * 24f
                val y = hPx * (1.1f - phase * 1.25f)
                val a = (sin(phase * 3.14f) * 0.10f).coerceAtLeast(0f)
                Text(
                    ch, fontFamily = HanziFont, fontSize = (26 + (i % 4) * 10).sp, color = Color.White.copy(alpha = a),
                    modifier = Modifier.offset { IntOffset(x.roundToInt(), y.roundToInt()) }.graphicsLayer { rotationZ = (i % 5 - 2) * 8f },
                )
            }
        }
        content()
    }
}

/** Logo Kuaile Learn: maskot enggang dalam kartu putih, dengan kilau yang menyapu. */
@Composable
internal fun LogoMark(size: Dp, modifier: Modifier = Modifier, shine: Boolean = true) {
    val tr = rememberInfiniteTransition(label = "logo")
    val s by tr.animateFloat(-1f, 2f, infiniteRepeatable(tween(3200, delayMillis = 1200, easing = FastOutSlowInEasing)), label = "shine")
    Box(
        modifier.size(size)
            .shadow(24.dp, RoundedCornerShape(size * 0.28f), ambientColor = Color(0xFF60A5FA), spotColor = Color(0xFF1E3A8A))
            .clip(RoundedCornerShape(size * 0.28f))
            .background(Color.White)
            .drawWithContent {
                drawContent()
                if (shine) {
                    val x = this.size.width * s
                    drawRect(Brush.linearGradient(
                        listOf(Color.Transparent, Color.White.copy(alpha = 0.55f), Color.Transparent),
                        start = Offset(x - this.size.width * 0.4f, 0f), end = Offset(x, this.size.height),
                    ))
                }
            },
    ) {
        Image(painterResource(AppRes.drawable.mascot_head), "Logo Kuaile Learn", Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
    }
}

/** Maskot enggang utuh yang melambai pelan, dengan cahaya di belakangnya. */
@Composable
internal fun Mascot(height: Dp, modifier: Modifier = Modifier) {
    val tr = rememberInfiniteTransition(label = "mascot")
    val t by tr.animateFloat(0f, 1f, infiniteRepeatable(tween(2400, easing = LinearEasing)), label = "t")
    Box(modifier, contentAlignment = Alignment.Center) {
        Canvas(Modifier.size(height * 1.05f)) {
            drawCircle(Brush.radialGradient(listOf(Color.White.copy(alpha = 0.55f), Color.Transparent)), size.minDimension / 2)
        }
        Image(
            painterResource(AppRes.drawable.mascot_full), "Maskot Kuaile Learn",
            Modifier.height(height).aspectRatio(640f / 670f).graphicsLayer {
                translationY = sin(t * 6.283f) * 6.dp.toPx()
                rotationZ = sin(t * 6.283f + 1f) * 2.5f
                transformOrigin = androidx.compose.ui.graphics.TransformOrigin(0.5f, 1f)
            },
        )
    }
}

/** Kartu kaca tembus pandang. */
@Composable
internal fun GlassCard(modifier: Modifier = Modifier, padding: Dp = 18.dp, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier.clip(RoundedCornerShape(24.dp)).background(AuthColors.glass)
            .border(BorderStroke(1.dp, AuthColors.line), RoundedCornerShape(24.dp)).padding(padding),
        content = content,
    )
}

@Composable
private fun Modifier.press(enabled: Boolean, onClick: () -> Unit): Modifier {
    val src = remember { MutableInteractionSource() }
    val pressed by src.collectIsPressedAsState()
    val sc by animateFloatAsState(if (pressed) 0.94f else 1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy), label = "press")
    return this.scale(sc).clickable(src, null, enabled = enabled, onClick = onClick)
}

/** Tombol utama putih di atas latar biru. */
@Composable
internal fun AuthButton(text: String, modifier: Modifier = Modifier, enabled: Boolean = true, loading: Boolean = false, ghost: Boolean = false, onClick: () -> Unit) {
    val bg by animateColorAsState(
        when { ghost -> AuthColors.glass; enabled -> Color.White; else -> Color(0x59FFFFFF) }, label = "bg",
    )
    val fg = if (ghost) Color.White else AuthColors.mid
    Box(
        modifier.fillMaxWidth().height(56.dp)
            .shadow(if (ghost || !enabled) 0.dp else 14.dp, RoundedCornerShape(50), spotColor = Color(0xFF0B1E5B))
            .clip(RoundedCornerShape(50)).background(bg)
            .let { if (ghost) it.border(1.dp, AuthColors.line, RoundedCornerShape(50)) else it }
            .press(enabled && !loading, onClick),
        contentAlignment = Alignment.Center,
    ) {
        if (loading) CircularProgressIndicator(Modifier.size(24.dp), color = fg, strokeWidth = 2.5.dp)
        else Text(text, color = fg.copy(alpha = if (enabled) 1f else 0.7f), style = MaterialTheme.typography.titleMedium)
    }
}

/** Teks tautan kecil. */
@Composable
internal fun AuthLink(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Text(
        text, style = MaterialTheme.typography.labelLarge, color = Color.White,
        modifier = modifier.clip(RoundedCornerShape(50)).clickable(onClick = onClick).padding(horizontal = 14.dp, vertical = 10.dp),
    )
}

/** Animasi bergetar untuk kesalahan; ganti [trigger] untuk memutar ulang. */
@Composable
internal fun rememberShake(trigger: Int): Float {
    val a = remember { Animatable(0f) }
    LaunchedEffect(trigger) {
        if (trigger == 0) return@LaunchedEffect
        a.snapTo(0f)
        for (v in listOf(18f, -16f, 12f, -9f, 5f, -2f, 0f)) a.animateTo(v, tween(45))
    }
    return a.value
}

/** Enam titik PIN yang membesar saat diisi; merah dan bergetar saat salah. */
@Composable
internal fun PinDots(count: Int, error: Boolean, shakeKey: Int, modifier: Modifier = Modifier, length: Int = 6) {
    val dx = rememberShake(shakeKey)
    Row(modifier.graphicsLayer { translationX = dx * density }, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        repeat(length) { i ->
            val filled = i < count
            val sc by animateFloatAsState(if (filled) 1f else 0.62f, spring(dampingRatio = 0.4f, stiffness = 600f), label = "dot")
            val c by animateColorAsState(if (error) AuthColors.error else if (filled) Color.White else Color(0x4DFFFFFF), label = "dotc")
            Box(Modifier.size(18.dp).scale(sc).clip(CircleShape).background(c))
        }
    }
}

/** Papan angka besar seperti ATM. */
@Composable
internal fun PinPad(enabled: Boolean, onDigit: (Char) -> Unit, onDelete: () -> Unit, modifier: Modifier = Modifier) {
    val haptic = LocalHapticFeedback.current
    val rows = listOf("123", "456", "789", " 0<")
    Column(modifier, verticalArrangement = Arrangement.spacedBy(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        rows.forEach { r ->
            Row(horizontalArrangement = Arrangement.spacedBy(22.dp)) {
                r.forEach { ch ->
                    when (ch) {
                        ' ' -> Spacer(Modifier.size(72.dp))
                        '<' -> Box(
                            Modifier.size(72.dp).clip(CircleShape).press(enabled) { haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove); onDelete() },
                            contentAlignment = Alignment.Center,
                        ) { Icon(Icons.AutoMirrored.Rounded.Backspace, "Hapus", tint = Color.White) }
                        else -> Box(
                            Modifier.size(72.dp).clip(CircleShape).background(AuthColors.glass)
                                .border(1.dp, AuthColors.line, CircleShape)
                                .press(enabled) { haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove); onDigit(ch) },
                            contentAlignment = Alignment.Center,
                        ) { Text(ch.toString(), color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.SemiBold) }
                    }
                }
            }
        }
    }
}

/** Tiga titik yang memantul bergantian. */
@Composable
internal fun LoadingDots(modifier: Modifier = Modifier, color: Color = Color.White) {
    val tr = rememberInfiniteTransition(label = "dots")
    val t by tr.animateFloat(0f, 1f, infiniteRepeatable(tween(1100, easing = LinearEasing)), label = "t")
    Row(modifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        repeat(3) { i ->
            val ph = ((t - i * 0.16f) % 1f + 1f) % 1f
            val up = if (ph < 0.5f) sin(ph * 2 * kotlin.math.PI).toFloat() else 0f
            Box(Modifier.size(9.dp).graphicsLayer { translationY = -up * 10.dp.toPx() }.clip(CircleShape).background(color.copy(alpha = 0.55f + 0.45f * up)))
        }
    }
}

/** Lingkaran centang yang tergambar, untuk layar sukses. */
@Composable
internal fun SuccessBadge(modifier: Modifier = Modifier, size: Dp = 116.dp) {
    val ring = remember { Animatable(0f) }
    val check = remember { Animatable(0f) }
    val pop = remember { Animatable(0.4f) }
    LaunchedEffect(Unit) {
        pop.animateTo(1f, spring(dampingRatio = 0.45f, stiffness = 300f))
    }
    LaunchedEffect(Unit) {
        ring.animateTo(1f, tween(520, easing = FastOutSlowInEasing))
        check.animateTo(1f, tween(380, easing = FastOutSlowInEasing))
    }
    val tr = rememberInfiniteTransition(label = "halo")
    val h by tr.animateFloat(0f, 1f, infiniteRepeatable(tween(1800, easing = LinearEasing)), label = "h")
    Canvas(modifier.size(size).scale(pop.value)) {
        val r = this.size.minDimension / 2
        val c = center
        drawCircle(Color.White.copy(alpha = 0.25f * (1f - h)), r * (0.8f + 0.25f * h), c)
        drawCircle(Color.White.copy(alpha = 0.16f), r * 0.8f, c)
        drawArc(Color.White, -90f, 360f * ring.value, false, Offset(c.x - r * 0.8f, c.y - r * 0.8f),
            androidx.compose.ui.geometry.Size(r * 1.6f, r * 1.6f), style = androidx.compose.ui.graphics.drawscope.Stroke(r * 0.09f))
        val p1 = Offset(c.x - r * 0.34f, c.y + r * 0.02f)
        val p2 = Offset(c.x - r * 0.08f, c.y + r * 0.28f)
        val p3 = Offset(c.x + r * 0.38f, c.y - r * 0.26f)
        val k = check.value
        if (k > 0f) {
            val a = (k / 0.4f).coerceAtMost(1f)
            drawLine(Color.White, p1, Offset(p1.x + (p2.x - p1.x) * a, p1.y + (p2.y - p1.y) * a), r * 0.11f, androidx.compose.ui.graphics.StrokeCap.Round)
            if (k > 0.4f) {
                val b = (k - 0.4f) / 0.6f
                drawLine(Color.White, p2, Offset(p2.x + (p3.x - p2.x) * b, p2.y + (p3.y - p2.y) * b), r * 0.11f, androidx.compose.ui.graphics.StrokeCap.Round)
            }
        }
    }
}

/** Indikator langkah pendaftaran. */
@Composable
internal fun StepBar(step: Int, total: Int, modifier: Modifier = Modifier) {
    Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        repeat(total) { i ->
            val f by animateFloatAsState(if (i < step) 1f else 0f, tween(420, easing = FastOutSlowInEasing), label = "step")
            Box(Modifier.weight(1f).height(5.dp).clip(RoundedCornerShape(50)).background(Color(0x33FFFFFF))) {
                Box(Modifier.fillMaxWidth(f).height(5.dp).clip(RoundedCornerShape(50)).background(Color.White))
            }
        }
    }
}

/** Judul layar akun dengan animasi masuk. */
@Composable
internal fun AuthTitle(title: String, sub: String?, modifier: Modifier = Modifier) {
    Column(modifier) {
        Text(title, style = MaterialTheme.typography.headlineMedium, color = Color.White)
        if (sub != null) {
            Spacer(Modifier.height(6.dp))
            Text(sub, style = MaterialTheme.typography.bodyLarge, color = AuthColors.soft)
        }
    }
}

/** Pesan kesalahan/info di bawah isian. */
@Composable
internal fun AuthMessage(text: String?, modifier: Modifier = Modifier, color: Color = AuthColors.error) {
    if (text == null) return
    Text(text, style = MaterialTheme.typography.bodyMedium, color = color, textAlign = TextAlign.Center, modifier = modifier.fillMaxWidth())
}

/** Cincin cahaya yang berdenyut di belakang ikon. */
@Composable
internal fun PulseIcon(content: @Composable BoxScope.() -> Unit) {
    val tr = rememberInfiniteTransition(label = "pulse")
    val t by tr.animateFloat(0f, 1f, infiniteRepeatable(tween(2000, easing = LinearEasing)), label = "t")
    Box(Modifier.size(140.dp), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            for (k in 0..1) {
                val ph = (t + k * 0.5f) % 1f
                drawCircle(Color.White.copy(alpha = 0.22f * (1f - ph)), size.minDimension / 2 * (0.45f + 0.55f * ph))
            }
        }
        Box(Modifier.size(84.dp).clip(CircleShape).background(AuthColors.glassStrong), contentAlignment = Alignment.Center, content = content)
    }
}

/** Orbit titik kecil mengelilingi logo saat loading. */
@Composable
internal fun Orbit(modifier: Modifier = Modifier) {
    val tr = rememberInfiniteTransition(label = "orbit")
    val t by tr.animateFloat(0f, 1f, infiniteRepeatable(tween(2600, easing = LinearEasing)), label = "t")
    Canvas(modifier.aspectRatio(1f)) {
        val r = size.minDimension / 2
        for (i in 0 until 3) {
            val a = (t + i / 3f) * 6.283f
            val c = Offset(center.x + cos(a) * r * 0.92f, center.y + sin(a) * r * 0.92f)
            drawCircle(listOf(AuthColors.cyan, Color.White, AuthColors.gold)[i], r * 0.035f, c)
        }
        drawCircle(Color.White.copy(alpha = 0.10f), r * 0.92f, style = androidx.compose.ui.graphics.drawscope.Stroke(1.5f))
    }
}

private class Drop(val x: Float, val y0: Float, val speed: Float, val color: Color, val w: Float, val h: Float, val spin: Float, val phase: Float)

/** Konfeti yang turun memenuhi layar dari atas sampai bawah. Ganti [trigger] untuk memutar ulang. */
@Composable
internal fun RainConfetti(trigger: Int, modifier: Modifier = Modifier) {
    if (trigger == 0) return
    val colors = listOf(Color.White, AuthColors.gold, AuthColors.glow, AuthColors.cyan, Color(0xFFF472B6), Color(0xFF86EFAC))
    val drops = remember(trigger) {
        val r = kotlin.random.Random(trigger * 17L + com.nesxha.readit.platform.nowMs())
        List(170) {
            Drop(r.nextFloat(), -r.nextFloat() * 1.2f, 0.55f + r.nextFloat() * 0.6f, colors[r.nextInt(colors.size)],
                7f + r.nextFloat() * 9f, 5f + r.nextFloat() * 8f, (r.nextFloat() - 0.5f) * 1080f, r.nextFloat() * 6.28f)
        }
    }
    val t = remember(trigger) { Animatable(0f) }
    LaunchedEffect(trigger) { t.snapTo(0f); t.animateTo(1f, tween(4200, easing = LinearEasing)) }
    if (t.value >= 1f) return
    Canvas(modifier.fillMaxSize()) {
        val p = t.value
        drops.forEach { d ->
            val y = (d.y0 + d.speed * p * 2.4f) * size.height
            if (y < -20f || y > size.height + 20f) return@forEach
            val x = d.x * size.width + sin(d.phase + p * 10f) * 22f
            val fade = if (p > 0.85f) (1f - p) / 0.15f else 1f
            val flip = 0.35f + 0.65f * kotlin.math.abs(cos(d.phase + p * 8f))
            rotate(d.spin * p, Offset(x, y)) {
                drawRect(d.color.copy(alpha = fade), Offset(x, y), androidx.compose.ui.geometry.Size(d.w, d.h * flip))
            }
        }
    }
}
