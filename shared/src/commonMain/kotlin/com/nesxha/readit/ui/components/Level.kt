package com.nesxha.readit.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nesxha.readit.data.Levels
import com.nesxha.readit.data.Store
import com.nesxha.readit.ui.theme.Brand
import com.nesxha.readit.ui.theme.HanziFont
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin
import com.nesxha.readit.platform.toRadians

/** Warna cincin lambang level: biru, lalu emas di level 100+, violet di 500+. */
private fun ringColors(level: Int): List<Color> = when {
    level >= 500 -> listOf(Color(0xFF7C3AED), Color(0xFFC4B5FD))
    level >= 100 -> listOf(Color(0xFFF59E0B), Color(0xFFFDE68A))
    level >= 50 -> listOf(Color(0xFF0EA5E9), Color(0xFFBAE6FD))
    else -> listOf(Color(0xFF1D4ED8), Color(0xFF60A5FA))
}

/** Ukuran huruf angka mengecil sesuai jumlah digit supaya tidak pernah patah. */
private fun digitScale(n: Int): Float = when (n.toString().length) {
    1, 2 -> 0.42f; 3 -> 0.34f; 4 -> 0.27f; 5 -> 0.22f; else -> 0.18f
}

/** Lencana level kecil berbentuk pil; lebarnya mengikuti angka (Lv 1 sampai Lv 100000 tetap satu baris). */
@Composable
fun LevelChip(level: Int, modifier: Modifier = Modifier, onBrand: Boolean = false) {
    val bg = if (onBrand) Color.White else MaterialTheme.colorScheme.primary
    val fg = if (onBrand) Brand.blue else Color.White
    Row(
        modifier.heightIn(min = 26.dp).widthIn(min = 52.dp).clip(RoundedCornerShape(50)).background(bg)
            .padding(start = 6.dp, end = 10.dp, top = 3.dp, bottom = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Icon(Icons.Rounded.Star, null, tint = if (level >= 100) Brand.gold else fg, modifier = Modifier.size(15.dp))
        Spacer(Modifier.width(3.dp))
        Text("Lv $level", color = fg, style = MaterialTheme.typography.labelLarge, maxLines = 1, softWrap = false)
    }
}

/** Lambang level bulat; angka panjang otomatis mengecil, jadi tidak keluar dari lingkaran. */
@Composable
fun LevelEmblem(level: Int, size: Dp, modifier: Modifier = Modifier, spin: Boolean = false) {
    val ring = ringColors(level)
    val tr = rememberInfiniteTransition(label = "emblem")
    val rot by tr.animateFloat(0f, 360f, infiniteRepeatable(tween(8000, easing = LinearEasing)), label = "rot")
    Box(modifier.size(size), contentAlignment = Alignment.Center) {
        Box(
            Modifier.matchParentSize().rotate(if (spin) rot else 0f).clip(CircleShape)
                .background(Brush.sweepGradient(ring + ring.first())),
        )
        Box(
            Modifier.matchParentSize().padding(size * 0.07f).clip(CircleShape)
                .background(Brush.linearGradient(listOf(Color(0xFF1E3A8A), Color(0xFF2563EB)))),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Level", color = Color.White.copy(alpha = 0.8f), fontSize = (size.value * 0.11f).sp, maxLines = 1, softWrap = false)
                Text(
                    "$level", color = Color.White, fontWeight = FontWeight.ExtraBold,
                    fontSize = (size.value * digitScale(level)).sp, lineHeight = (size.value * digitScale(level) * 1.1f).sp,
                    maxLines = 1, softWrap = false, textAlign = TextAlign.Center,
                )
            }
        }
    }
}

/** Perayaan naik level layar penuh. */
@Composable
fun LevelUpCelebration(level: Int, onDismiss: () -> Unit) {
    val info = Levels.info(Store.now.xp)
    val newTitle = level in Levels.titleStarts && level > 1
    val enter = remember { Animatable(0f) }
    val pop = remember { Animatable(0.2f) }
    var count by remember { mutableStateOf(level - 1) }
    LaunchedEffect(level) {
        enter.animateTo(1f, tween(300))
        pop.animateTo(1f, spring(dampingRatio = 0.4f, stiffness = 200f))
        delay(150); count = level
    }
    val tr = rememberInfiniteTransition(label = "rays")
    val rays by tr.animateFloat(0f, 360f, infiniteRepeatable(tween(14000, easing = LinearEasing)), label = "r")
    val glow by tr.animateFloat(0.6f, 1f, infiniteRepeatable(tween(900), RepeatMode.Reverse), label = "g")

    Box(
        Modifier.fillMaxSize().graphicsLayer { alpha = enter.value }
            .background(Brush.verticalGradient(listOf(Color(0xF20B1530), Color(0xF21E3A8A))))
            .bounceClick(onClick = onDismiss),
        contentAlignment = Alignment.Center,
    ) {
        // Sinar berputar di belakang lambang
        Canvas(Modifier.fillMaxSize()) {
            val c = Offset(size.width / 2, size.height * 0.40f)
            val r = size.maxDimension
            rotate(rays, c) {
                for (i in 0 until 16) {
                    val a1 = toRadians(i * 22.5).toFloat()
                    val a2 = toRadians(i * 22.5 + 9).toFloat()
                    val p = Path().apply {
                        moveTo(c.x, c.y)
                        lineTo(c.x + cos(a1) * r, c.y + sin(a1) * r)
                        lineTo(c.x + cos(a2) * r, c.y + sin(a2) * r)
                        close()
                    }
                    drawPath(p, Color.White.copy(alpha = 0.05f * glow))
                }
            }
        }
        Column(
            Modifier.statusBarsPadding().navigationBarsPadding().padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("Naik Level!", style = MaterialTheme.typography.headlineMedium, color = Color.White)
            Spacer(Modifier.height(20.dp))
            AnimatedNumberEmblem(count, Modifier.graphicsLayer { scaleX = pop.value; scaleY = pop.value })
            Spacer(Modifier.height(20.dp))
            Text("Kamu Sekarang Level $level", style = MaterialTheme.typography.titleLarge, color = Color.White, textAlign = TextAlign.Center)
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(Levels.title(level), style = MaterialTheme.typography.titleMedium, color = Brand.sky)
                Spacer(Modifier.width(6.dp))
                Text(Levels.hanziTitle(level), fontFamily = HanziFont, fontSize = 18.sp, color = Brand.sky)
            }
            if (newTitle) {
                Spacer(Modifier.height(10.dp))
                Box(Modifier.clip(RoundedCornerShape(50)).background(Brand.gold).padding(horizontal = 14.dp, vertical = 6.dp)) {
                    Text("Gelar Baru Terbuka!", color = Color(0xFF3B2600), style = MaterialTheme.typography.labelLarge)
                }
            }
            Spacer(Modifier.height(10.dp))
            Text(
                "Butuh ${info.xpForNext} XP lagi untuk Level ${level + 1}. Terus belajar, ya!",
                style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.8f), textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(24.dp))
            Box(
                Modifier.clip(RoundedCornerShape(50)).background(Color.White).bounceClick(onClick = onDismiss)
                    .padding(horizontal = 32.dp, vertical = 14.dp),
            ) { Text("Mantap!", color = Brand.blue, style = MaterialTheme.typography.labelLarge) }
        }
        Confetti(level)
    }
}

@Composable
private fun AnimatedNumberEmblem(level: Int, modifier: Modifier) {
    val v by androidx.compose.animation.core.animateIntAsState(level, tween(600), label = "lv")
    LevelEmblem(v, 150.dp, modifier, spin = true)
}
