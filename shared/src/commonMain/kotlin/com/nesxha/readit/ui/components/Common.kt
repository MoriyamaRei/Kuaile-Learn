package com.nesxha.readit.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nesxha.readit.data.Word
import com.nesxha.readit.ui.theme.Brand
import com.nesxha.readit.ui.theme.HanziFont
import com.nesxha.readit.ui.theme.LevelColor
import com.nesxha.readit.ui.theme.LocalDark
import com.nesxha.readit.ui.theme.ToneColor
import com.nesxha.readit.util.Pinyin
import com.nesxha.readit.util.Tts
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * Pinyin dengan warna nada per suku kata. Selalu satu baris supaya tidak patah di tengah kata.
 * [onDark] = null mengikuti tema; true/false memaksa palet untuk latar gelap/terang.
 */
@Composable
fun PinyinText(
    pinyin: String,
    hanzi: String,
    modifier: Modifier = Modifier,
    size: TextUnit = 18.sp,
    weight: FontWeight = FontWeight.Bold,
    align: TextAlign = TextAlign.Center,
    onDark: Boolean? = null,
    wrap: Boolean = false,
) {
    val dark = onDark ?: LocalDark.current
    val segs = remember(pinyin, hanzi) { Pinyin.segments(pinyin, hanzi) }
    val base = if (dark) Color(0xFFCBD5E1) else Color(0xFF64748B)
    val txt = buildAnnotatedString {
        segs.forEach { s ->
            withStyle(SpanStyle(color = if (s.tone < 0) base else ToneColor.of(s.tone, dark))) { append(s.text) }
        }
    }
    // [wrap] = boleh turun ke baris berikutnya (untuk teks panjang seperti angka besar).
    if (wrap) Text(txt, modifier = modifier, fontSize = size, fontWeight = weight, textAlign = align, lineHeight = size * 1.35f)
    else Text(txt, modifier = modifier, fontSize = size, fontWeight = weight, textAlign = align, softWrap = false, maxLines = 1, overflow = TextOverflow.Visible)
}

@Composable
fun HanziText(
    text: String,
    modifier: Modifier = Modifier,
    size: TextUnit = 48.sp,
    color: Color = MaterialTheme.colorScheme.onSurface,
    bold: Boolean = true,
) {
    Text(
        text, modifier = modifier, fontFamily = HanziFont, fontSize = size, color = color,
        fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal, textAlign = TextAlign.Center,
        lineHeight = (size.value * 1.25f).sp,
    )
}

/** Klik dengan efek memantul. */
@Composable
fun Modifier.bounceClick(enabled: Boolean = true, onClick: () -> Unit): Modifier {
    val src = remember { MutableInteractionSource() }
    val pressed by src.collectIsPressedAsState()
    val sc by animateFloatAsState(if (pressed) 0.95f else 1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy), label = "bounce")
    return this.scale(sc).clickable(interactionSource = src, indication = null, enabled = enabled, onClick = onClick)
}

/** Muncul pelan dari bawah; [index] memberi jeda bertahap, negatif = tanpa animasi. */
@Composable
fun Modifier.appear(index: Int = 0): Modifier {
    val a = remember { Animatable(if (index < 0) 1f else 0f) }
    LaunchedEffect(Unit) {
        if (index < 0) return@LaunchedEffect
        delay(index.coerceIn(0, 10) * 55L)
        a.animateTo(1f, tween(450, easing = FastOutSlowInEasing))
    }
    return this.graphicsLayer {
        alpha = a.value
        translationY = (1f - a.value) * 28.dp.toPx()
        val s = 0.96f + 0.04f * a.value
        scaleX = s; scaleY = s
    }
}

/** Angka yang menghitung naik saat berubah. */
@Composable
fun AnimatedNumber(value: Int, style: androidx.compose.ui.text.TextStyle, color: Color, suffix: String = "") {
    val v by animateIntAsState(value, tween(900, easing = FastOutSlowInEasing), label = "num")
    Text("$v$suffix", style = style, color = color)
}

@Composable
fun BrandCard(
    modifier: Modifier = Modifier,
    radius: Dp = 28.dp,
    padding: PaddingValues = PaddingValues(20.dp),
    brush: Brush = Brand.card,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val m = modifier
        .shadow(12.dp, RoundedCornerShape(radius), ambientColor = Color(0x552563EB), spotColor = Color(0x662563EB))
        .clip(RoundedCornerShape(radius))
        .background(brush)
        .let { if (onClick != null) it.bounceClick(onClick = onClick) else it }
    Box(m) {
        Bubbles(Modifier.matchParentSize(), alpha = 0.10f)
        Column(Modifier.padding(padding), content = content)
    }
}

@Composable
fun SoftCard(
    modifier: Modifier = Modifier,
    radius: Dp = 24.dp,
    padding: PaddingValues = PaddingValues(18.dp),
    color: Color = MaterialTheme.colorScheme.surface,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val m = modifier
        .shadow(3.dp, RoundedCornerShape(radius), ambientColor = Color(0x1A1E3A8A), spotColor = Color(0x261E3A8A))
        .clip(RoundedCornerShape(radius))
        // Warna tembus pandang diberi alas warna latar: di web (Skia) bayangan tergambar di bawah seluruh kartu.
        .let { if (color.alpha < 1f) it.background(MaterialTheme.colorScheme.background) else it }
        .background(color)
        .let { if (onClick != null) it.bounceClick(onClick = onClick) else it }
        .padding(padding)
    Column(m, content = content)
}

/** Lencana level kecil: titik warna + teks. */
@Composable
fun LevelBadge(level: Int, modifier: Modifier = Modifier, onBrand: Boolean = false) {
    val bg = if (onBrand) Color(0x33FFFFFF) else MaterialTheme.colorScheme.surfaceVariant
    val fg = if (onBrand) Color.White else MaterialTheme.colorScheme.onSurface
    Row(
        modifier.clip(RoundedCornerShape(50)).background(bg).padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(8.dp).clip(CircleShape).background(LevelColor.of(level)))
        Spacer(Modifier.width(6.dp))
        Text("HSK $level", color = fg, style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
fun Pill(text: String, modifier: Modifier = Modifier, color: Color = MaterialTheme.colorScheme.primaryContainer, textColor: Color = MaterialTheme.colorScheme.onPrimaryContainer) {
    Box(modifier.clip(RoundedCornerShape(50)).background(color).padding(horizontal = 10.dp, vertical = 4.dp)) {
        Text(text, color = textColor, style = MaterialTheme.typography.labelMedium, maxLines = 1)
    }
}

@Composable
fun SpeakButton(text: String, modifier: Modifier = Modifier, onBrand: Boolean = true) {
    val bg = if (onBrand) Color(0x33FFFFFF) else MaterialTheme.colorScheme.primaryContainer
    val tint = if (onBrand) Color.White else MaterialTheme.colorScheme.primary
    Box(
        modifier.size(44.dp).clip(CircleShape).background(bg).bounceClick { Tts.speak(text) },
        contentAlignment = Alignment.Center,
    ) { Icon(Icons.AutoMirrored.Rounded.VolumeUp, contentDescription = "Dengarkan", tint = tint) }
}

/** Lingkaran ikon berwarna lembut. */
@Composable
fun IconCircle(icon: ImageVector, modifier: Modifier = Modifier, size: Dp = 44.dp, onBrand: Boolean = false) {
    Box(
        modifier.size(size).clip(CircleShape)
            .background(if (onBrand) Color(0x33FFFFFF) else MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center,
    ) { Icon(icon, null, tint = if (onBrand) Color.White else MaterialTheme.colorScheme.primary, modifier = Modifier.size(size * 0.5f)) }
}

/** Cincin progres beranimasi. */
@Composable
fun ProgressRing(
    fraction: Float,
    modifier: Modifier = Modifier,
    stroke: Dp = 12.dp,
    track: Color = Color(0x33FFFFFF),
    color: Color = Color.White,
    content: @Composable () -> Unit = {},
) {
    val anim by animateFloatAsState(fraction.coerceIn(0f, 1f), tween(1100, easing = FastOutSlowInEasing), label = "ring")
    Box(modifier, contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val s = stroke.toPx()
            val sz = Size(size.width - s, size.height - s)
            val tl = Offset(s / 2, s / 2)
            drawArc(track, 0f, 360f, false, tl, sz, style = Stroke(s, cap = StrokeCap.Round))
            if (anim > 0f) drawArc(color, -90f, 360f * anim, false, tl, sz, style = Stroke(s, cap = StrokeCap.Round))
        }
        content()
    }
}

@Composable
fun ProgressBar(
    fraction: Float,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    track: Color = MaterialTheme.colorScheme.surfaceVariant,
    height: Dp = 10.dp,
) {
    val anim by animateFloatAsState(fraction.coerceIn(0f, 1f), tween(900, easing = FastOutSlowInEasing), label = "bar")
    Box(modifier.fillMaxWidth().height(height).clip(RoundedCornerShape(50)).background(track)) {
        if (anim > 0f) Box(Modifier.fillMaxWidth(anim).fillMaxHeight().clip(RoundedCornerShape(50)).background(color))
    }
}

@Composable
fun SectionTitle(text: String, modifier: Modifier = Modifier, action: String? = null, onAction: (() -> Unit)? = null) {
    Row(modifier.fillMaxWidth().padding(top = 6.dp, bottom = 2.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(text, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.weight(1f))
        if (action != null && onAction != null) {
            Text(action, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, modifier = Modifier.bounceClick(onClick = onAction).padding(4.dp))
        }
    }
}

/** Gelembung dekoratif yang melayang pelan. */
@Composable
fun Bubbles(modifier: Modifier = Modifier, alpha: Float = 0.12f) {
    val tr = rememberInfiniteTransition(label = "bubbles")
    val p by tr.animateFloat(0f, 1f, infiniteRepeatable(tween(9000, easing = LinearEasing), RepeatMode.Reverse), label = "p")
    Canvas(modifier) {
        val w = size.width; val h = size.height
        val c = Color.White.copy(alpha = alpha)
        drawCircle(c, h * 0.55f, Offset(w * (0.88f + 0.04f * p), h * (0.15f + 0.1f * p)))
        drawCircle(c, h * 0.28f, Offset(w * (0.70f - 0.05f * p), h * (0.95f - 0.08f * p)))
        drawCircle(c.copy(alpha = alpha * 0.7f), h * 0.16f, Offset(w * (0.10f + 0.06f * p), h * (0.20f + 0.15f * p)))
    }
}

/** Kepala layar bergradasi biru dengan gelembung beranimasi. */
@Composable
fun ScreenHeader(
    title: String,
    subtitle: String? = null,
    leading: (@Composable () -> Unit)? = null,
    trailing: @Composable () -> Unit = {},
    bottom: (@Composable ColumnScope.() -> Unit)? = null,
) {
    Box(
        Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
            .background(Brand.header),
    ) {
        Bubbles(Modifier.matchParentSize())
        Column(Modifier.statusBarsPadding().padding(start = 20.dp, end = 16.dp, top = 12.dp, bottom = 22.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (leading != null) { leading(); Spacer(Modifier.width(12.dp)) }
                Column(Modifier.weight(1f)) {
                    Text(title, style = MaterialTheme.typography.headlineMedium, color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    if (subtitle != null) Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.85f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                trailing()
            }
            if (bottom != null) { Spacer(Modifier.height(14.dp)); bottom() }
        }
    }
}

@Composable
fun HeaderButton(icon: ImageVector, desc: String, onClick: () -> Unit) {
    Box(
        Modifier.size(44.dp).clip(CircleShape).background(Color(0x2EFFFFFF)).bounceClick(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) { Icon(icon, desc, tint = Color.White) }
}

/** Pemilih bersegmen dengan sorotan yang bergeser. */
@Composable
fun Segmented(options: List<String>, selected: Int, modifier: Modifier = Modifier, onBrand: Boolean = false, onSelect: (Int) -> Unit) {
    val track = if (onBrand) Color(0x2EFFFFFF) else MaterialTheme.colorScheme.surfaceVariant
    val pill = if (onBrand) Color.White else MaterialTheme.colorScheme.primary
    BoxWithConstraints(modifier.fillMaxWidth().clip(RoundedCornerShape(50)).background(track).padding(4.dp)) {
        val w = maxWidth / options.size
        val x by androidx.compose.animation.core.animateDpAsState(w * selected, spring(dampingRatio = 0.75f, stiffness = 400f), label = "seg")
        Box(Modifier.offset(x = x).width(w).height(36.dp).clip(RoundedCornerShape(50)).background(pill))
        Row(Modifier.fillMaxWidth()) {
            options.forEachIndexed { i, o ->
                val sel = i == selected
                val fg by animateColorAsState(
                    when {
                        sel && onBrand -> Brand.blue
                        sel -> Color.White
                        onBrand -> Color.White
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }, label = "segfg",
                )
                Box(Modifier.weight(1f).height(36.dp).clip(RoundedCornerShape(50)).clickable { onSelect(i) }, contentAlignment = Alignment.Center) {
                    Text(o, style = if (options.size >= 5) MaterialTheme.typography.labelMedium else MaterialTheme.typography.labelLarge, color = fg, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
        }
    }
}

/** Chip pilihan berwarna tema. */
@Composable
fun Chip(text: String, selected: Boolean, onClick: () -> Unit) {
    val bg by animateColorAsState(if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface, label = "chip")
    val fg by animateColorAsState(if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant, label = "chipfg")
    Box(
        Modifier.clip(RoundedCornerShape(50)).background(bg)
            .bounceClick(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp),
    ) { Text(text, style = MaterialTheme.typography.labelLarge, color = fg, maxLines = 1) }
}

@Composable
fun WordRow(word: Word, modifier: Modifier = Modifier, extra: String? = null, trailing: (@Composable () -> Unit)? = null, onClick: () -> Unit) {
    SoftCard(modifier.fillMaxWidth(), radius = 20.dp, padding = PaddingValues(12.dp), onClick = onClick) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(54.dp).clip(RoundedCornerShape(16.dp)).background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                HanziText(word.hanzi.take(4), size = if (word.hanzi.length > 2) 17.sp else 25.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                Box(Modifier.align(Alignment.TopEnd).padding(5.dp).size(7.dp).clip(CircleShape).background(LevelColor.of(word.level)))
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                PinyinText(word.pinyin, word.hanzi, size = 15.sp, align = TextAlign.Start)
                Text(word.main, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
            if (extra != null) {
                Spacer(Modifier.width(8.dp))
                Pill(extra)
            }
            if (trailing != null) { Spacer(Modifier.width(4.dp)); trailing() }
        }
    }
}

@Composable
fun EmptyState(emoji: String, title: String, text: String) {
    val tr = rememberInfiniteTransition(label = "float")
    val y by tr.animateFloat(-6f, 6f, infiniteRepeatable(tween(1600, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "y")
    Column(Modifier.fillMaxWidth().padding(28.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text(emoji, fontSize = 56.sp, modifier = Modifier.graphicsLayer { translationY = y.dp.toPx() })
        Spacer(Modifier.height(12.dp))
        Text(title, style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onBackground)
        Spacer(Modifier.height(6.dp))
        Text(text, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

/** Baris aksi: ikon + judul + keterangan. */
@Composable
fun ActionRow(icon: ImageVector, title: String, sub: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    SoftCard(modifier.fillMaxWidth(), radius = 20.dp, padding = PaddingValues(14.dp), onClick = onClick) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconCircle(icon)
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                Text(sub, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text("›", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary)
        }
    }
}

private class Piece(val x: Float, val vx: Float, val vy: Float, val color: Color, val w: Float, val h: Float, val spin: Float, val phase: Float)

/** Hujan konfeti; ganti [trigger] untuk memutar ulang. */
@Composable
fun Confetti(trigger: Int, modifier: Modifier = Modifier) {
    if (trigger == 0) return
    val colors = listOf(Color(0xFF2563EB), Color(0xFF60A5FA), Color(0xFFBFDBFE), Color(0xFFFBBF24), Color.White, Color(0xFF1E40AF))
    val pieces = remember(trigger) {
        val r = Random(trigger * 31L + com.nesxha.readit.platform.nowMs())
        List(150) {
            Piece(
                r.nextFloat(), (r.nextFloat() - 0.5f) * 0.4f, 0.5f + r.nextFloat() * 0.9f, colors[r.nextInt(colors.size)],
                8f + r.nextFloat() * 10f, 5f + r.nextFloat() * 8f, (r.nextFloat() - 0.5f) * 1440f, r.nextFloat() * 6.28f,
            )
        }
    }
    val t = remember(trigger) { Animatable(0f) }
    LaunchedEffect(trigger) { t.snapTo(0f); t.animateTo(1f, tween(3200)) }
    if (t.value >= 1f) return
    Box(modifier.fillMaxSize()) {
        Canvas(Modifier.fillMaxSize()) {
            val p = t.value
            pieces.forEach { pc ->
                val y = -0.1f * size.height + (pc.vy * p * 1.3f) * size.height + 0.6f * p * p * size.height
                val x = (pc.x + pc.vx * p) * size.width + sin(pc.phase + p * 12f) * 18f
                val alpha = if (p > 0.8f) (1f - p) / 0.2f else 1f
                rotate(pc.spin * p, Offset(x, y)) {
                    drawRect(pc.color.copy(alpha = alpha), Offset(x, y), Size(pc.w, pc.h * (0.4f + 0.6f * abs(cos(pc.phase + p * 9f)))))
                }
            }
        }
    }
}
