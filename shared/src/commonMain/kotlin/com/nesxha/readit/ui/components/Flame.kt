package com.nesxha.readit.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
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
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nesxha.readit.data.FlameTiers
import kotlin.math.sin
import kotlin.random.Random

/** Warna api per tingkat: luar, tengah, inti. */
data class FlameColors(val outer: Color, val mid: Color, val core: Color, val glow: Color)

fun flameColors(tier: Int): FlameColors = when (tier) {
    0 -> FlameColors(Color(0xFF7F1D1D), Color(0xFFB91C1C), Color(0xFFF87171), Color(0xFFB91C1C))   // merah redup
    1 -> FlameColors(Color(0xFFDC2626), Color(0xFFEF4444), Color(0xFFFECACA), Color(0xFFEF4444))   // merah terang
    2 -> FlameColors(Color(0xFFEA580C), Color(0xFFF97316), Color(0xFFFED7AA), Color(0xFFF97316))   // oranye
    3 -> FlameColors(Color(0xFFF59E0B), Color(0xFFFACC15), Color(0xFFFEF9C3), Color(0xFFFACC15))   // kuning
    4 -> FlameColors(Color(0xFFFCD34D), Color(0xFFFEF3C7), Color(0xFFFFFFFF), Color(0xFFFDE68A))   // putih kekuningan
    5 -> FlameColors(Color(0xFF1D4ED8), Color(0xFF3B82F6), Color(0xFFBFDBFE), Color(0xFF3B82F6))   // biru
    6 -> FlameColors(Color(0xFFCBD5E1), Color(0xFFF1F5F9), Color(0xFFFFFFFF), Color(0xFFE2E8F0))   // putih terang
    7 -> FlameColors(Color(0xFF0EA5E9), Color(0xFF7DD3FC), Color(0xFFE0F2FE), Color(0xFF38BDF8))   // biru muda
    else -> FlameColors(Color(0xFF6D28D9), Color(0xFF8B5CF6), Color(0xFFDDD6FE), Color(0xFFA78BFA)) // violet
}

private val unlit = FlameColors(Color(0xFF475569), Color(0xFF64748B), Color(0xFF94A3B8), Color.Transparent)

/**
 * Bentuk api dengan tiga lidah (tengah, kiri, kanan). [t] = waktu animasi (radian);
 * tiap lidah bergoyang dengan irama berbeda supaya terlihat hidup.
 */
private fun flamePath(w: Float, h: Float, t: Float, amp: Float): Path {
    val sway = sin(t) * amp
    val lt = sin(t * 1.7f + 1f) * amp
    val rt = sin(t * 1.3f + 2.2f) * amp
    val stretch = 1f + sin(t * 2.3f) * 0.04f * amp
    val top = h * (0.02f + (1f - stretch) * 0.5f)
    val mx = w * (0.5f + sway * 0.06f)
    val lx = w * (0.2f + lt * 0.03f); val ly = h * (0.36f - lt * 0.04f)
    val rx = w * (0.8f + rt * 0.03f); val ry = h * (0.30f - rt * 0.04f)
    return Path().apply {
        moveTo(w * 0.5f, h)
        cubicTo(w * 0.18f, h, w * 0.02f, h * 0.80f, w * 0.08f, h * 0.60f)
        cubicTo(w * 0.11f, h * 0.50f, w * 0.17f, h * 0.44f, lx, ly)
        cubicTo(w * 0.25f, h * 0.48f, w * 0.30f, h * 0.53f, w * 0.34f, h * 0.50f)
        cubicTo(w * 0.35f, h * 0.30f, w * 0.43f, h * 0.16f, mx, top)
        cubicTo(w * 0.60f, h * 0.18f, w * 0.67f, h * 0.33f, w * 0.66f, h * 0.45f)
        cubicTo(w * 0.70f, h * 0.41f, w * 0.74f, h * 0.36f, rx, ry)
        cubicTo(w * 0.88f, h * 0.44f, w * 0.98f, h * 0.62f, w * 0.92f, h * 0.78f)
        cubicTo(w * 0.85f, h * 0.95f, w * 0.70f, h, w * 0.5f, h)
        close()
    }
}

private fun DrawScope.drawFlame(c: FlameColors, t: Float, amp: Float, glow: Float) {
    val w = size.width; val h = size.height
    if (glow > 0f && c.glow != Color.Transparent) {
        val center = Offset(w / 2, h * 0.68f)
        drawCircle(Brush.radialGradient(listOf(c.glow.copy(alpha = 0.5f * glow), Color.Transparent), center, w * 0.85f), w * 0.85f, center)
    }
    // Lapisan luar: warna tingkat, makin terang ke bawah.
    drawPath(flamePath(w, h, t, amp), Brush.verticalGradient(listOf(c.outer, c.mid, c.mid), 0f, h))
    // Lapisan tengah: sedikit berlawanan arah.
    val s1 = 0.64f
    val mid = flamePath(w * s1, h * s1, -t + 1.3f, amp * 0.8f)
    mid.translate(Offset(w * (1 - s1) / 2, h * (1 - s1)))
    drawPath(mid, Brush.verticalGradient(listOf(c.mid, c.core), h * (1 - s1), h))
    // Inti putih di bawah.
    val s2 = 0.34f
    val core = flamePath(w * s2, h * s2, t * 1.4f, amp * 0.6f)
    core.translate(Offset(w * (1 - s2) / 2, h * (1 - s2) - h * 0.02f))
    drawPath(core, Brush.verticalGradient(listOf(c.core, Color.White), h * (1 - s2), h))
}

/**
 * Api beranimasi. [lit] = false menggambar api abu-abu tanpa gerak.
 * [ignite] diubah untuk memutar animasi menyala. [sparks] = percikan bara (untuk ukuran besar).
 */
@Composable
fun Flame(streak: Int, lit: Boolean, modifier: Modifier = Modifier, ignite: Int = 0, sparks: Boolean = false) {
    val c = if (lit) flameColors(FlameTiers.of(streak).level) else unlit
    val tr = rememberInfiniteTransition(label = "flame")
    val t by tr.animateFloat(0f, (2 * kotlin.math.PI).toFloat(), infiniteRepeatable(tween(1700, easing = LinearEasing)), label = "t")
    val pulse by tr.animateFloat(0.75f, 1f, infiniteRepeatable(tween(900, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "glow")
    val rise by tr.animateFloat(0f, 1f, infiniteRepeatable(tween(1400, easing = LinearEasing)), label = "rise")
    val grow = remember { Animatable(1f) }
    LaunchedEffect(ignite) {
        if (ignite > 0) { grow.snapTo(0.15f); grow.animateTo(1f, spring(dampingRatio = 0.35f, stiffness = 170f)) }
    }
    Canvas(modifier.graphicsLayer { scaleX = grow.value; scaleY = grow.value; transformOrigin = androidx.compose.ui.graphics.TransformOrigin(0.5f, 1f) }) {
        if (!lit) { drawFlame(c, 0f, 0f, 0f); return@Canvas }
        drawFlame(c, t, 1f, pulse)
        if (sparks) {
            val w = size.width; val h = size.height
            for (i in 0 until 7) {
                val p = (rise + i / 7f) % 1f
                val x = w * (0.3f + 0.4f * ((i * 37) % 10) / 10f) + sin((p + i) * 9f) * w * 0.05f
                val y = h * (0.35f - p * 0.45f)
                drawCircle(c.mid.copy(alpha = (1f - p) * 0.9f), w * (0.025f + 0.015f * (i % 3)) * (1f - p * 0.5f), Offset(x, y))
            }
        }
    }
}

private class Ember(val x: Float, val speed: Float, val size: Float, val phase: Float)

/** Perayaan layar penuh: api besar naik, bara beterbangan, angka streak menghitung naik. */
@Composable
fun FlameCelebration(streak: Int, onDismiss: () -> Unit) {
    val tier = FlameTiers.of(streak)
    val c = flameColors(tier.level)
    val enter = remember { Animatable(0f) }
    val rise = remember { Animatable(0f) }
    LaunchedEffect(streak) {
        enter.animateTo(1f, tween(350))
        rise.animateTo(1f, spring(dampingRatio = 0.5f, stiffness = 120f))
    }
    val tr = rememberInfiniteTransition(label = "embers")
    val t by tr.animateFloat(0f, 1f, infiniteRepeatable(tween(2600, easing = LinearEasing)), label = "t")
    val embers = remember { List(40) { Random.nextFloat().let { r -> Ember(Random.nextFloat(), 0.5f + Random.nextFloat(), 3f + r * 6f, Random.nextFloat()) } } }
    val newTier = tier.from == streak && tier.level > 0

    Box(
        Modifier.fillMaxSize()
            .graphicsLayer { alpha = enter.value }
            .background(Brush.verticalGradient(listOf(Color(0xF20B1120), Color(0xF2111827), c.outer.copy(alpha = 0.55f))))
            .bounceClick(onClick = onDismiss),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(Modifier.fillMaxSize()) {
            embers.forEach { e ->
                val p = (t * e.speed + e.phase) % 1f
                val x = e.x * size.width + sin((p + e.phase) * 12f) * 24f
                val y = size.height * (1.05f - p * 1.1f)
                drawCircle(c.mid.copy(alpha = (1f - p) * 0.9f), e.size, Offset(x, y))
            }
        }
        Column(
            Modifier.statusBarsPadding().navigationBarsPadding().padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Box(contentAlignment = Alignment.Center) {
                Flame(streak, true, sparks = true, modifier = Modifier.size(width = 200.dp, height = 260.dp).graphicsLayer {
                    translationY = (1f - rise.value) * 300.dp.toPx()
                    val s = 0.4f + 0.6f * rise.value
                    scaleX = s; scaleY = s
                })
                AnimatedNumber(
                    streak, MaterialTheme.typography.displayMedium.copy(fontSize = 56.sp), Color.White,
                )
            }
            Spacer(Modifier.height(18.dp))
            Text(
                if (streak >= 400) "Luar Biasa! Streak $streak Hari" else "Selamat! Streak $streak Hari",
                style = MaterialTheme.typography.headlineMedium, color = Color.White, textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                when {
                    newTier -> "Apimu naik tingkat menjadi ${tier.name}. Terus jaga nyalanya!"
                    streak >= 1000 -> "Seribu hari lebih tanpa putus. Kamu benar-benar legenda Kuaile Learn!"
                    else -> "$streak hari belajar tanpa putus. ${tier.name} milikmu makin membara!"
                },
                style = MaterialTheme.typography.bodyLarge, color = Color.White.copy(alpha = 0.9f), textAlign = TextAlign.Center,
            )
            FlameTiers.next(streak)?.let { nx ->
                Spacer(Modifier.height(6.dp))
                Text(
                    "Tingkat berikutnya: ${nx.name} di hari ke-${nx.from}.",
                    style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.7f), textAlign = TextAlign.Center,
                )
            }
            Spacer(Modifier.height(24.dp))
            Box(
                Modifier.clip(RoundedCornerShape(50)).background(c.mid).bounceClick(onClick = onDismiss)
                    .padding(horizontal = 32.dp, vertical = 14.dp),
            ) {
                Text("Lanjutkan", color = if (tier.level in setOf(3, 4, 6, 7)) Color(0xFF1F2937) else Color.White, style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

/** Deretan sembilan tingkat api; yang sudah dicapai menyala, yang sekarang diberi lingkaran. */
@Composable
fun FlameTierStrip(current: Int, lit: Boolean, modifier: Modifier = Modifier) {
    val now = if (current > 0) FlameTiers.of(current).level else -1
    androidx.compose.foundation.layout.Row(modifier, horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
        FlameTiers.all.forEach { tier ->
            val reached = current >= tier.from
            val isNow = tier.level == now
            Box(
                Modifier.size(if (isNow) 36.dp else 30.dp).clip(RoundedCornerShape(50))
                    .background(if (isNow) flameColors(tier.level).glow.copy(alpha = 0.18f) else Color.Transparent),
                contentAlignment = Alignment.Center,
            ) {
                Flame(tier.from, reached && (lit || !isNow), Modifier.size(width = if (isNow) 22.dp else 16.dp, height = if (isNow) 28.dp else 21.dp).graphicsLayer { alpha = if (reached) 1f else 0.45f })
            }
        }
    }
}
