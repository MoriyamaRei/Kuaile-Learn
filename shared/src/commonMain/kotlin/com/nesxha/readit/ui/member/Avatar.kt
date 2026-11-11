package com.nesxha.readit.ui.member

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nesxha.readit.ui.theme.UiFont
import com.nesxha.readit.util.Photos

private val AVATAR_COLORS = listOf(
    listOf(Color(0xFF1E40AF), Color(0xFF60A5FA)),
    listOf(Color(0xFF0E7490), Color(0xFF22D3EE)),
    listOf(Color(0xFF6D28D9), Color(0xFFA78BFA)),
    listOf(Color(0xFFB45309), Color(0xFFFBBF24)),
    listOf(Color(0xFF047857), Color(0xFF34D399)),
    listOf(Color(0xFFBE123C), Color(0xFFFB7185)),
)

/** Foto profil bulat; tanpa foto tampil inisial nickname dengan warna tetap per anggota. */
@Composable
fun Avatar(
    pid: String?, ver: Int, nickname: String, size: Dp, modifier: Modifier = Modifier,
    full: Boolean = false, ring: Color? = null, override: ImageBitmap? = null,
) {
    var img by remember(pid, ver, full) { mutableStateOf<ImageBitmap?>(null) }
    LaunchedEffect(pid, ver, full) {
        if (pid != null && ver > 0) img = Photos.load(pid, ver, full)
    }
    val shown = override ?: img
    val pal = AVATAR_COLORS[((pid ?: nickname).hashCode() and 0x7fffffff) % AVATAR_COLORS.size]
    Box(
        modifier.size(size).clip(CircleShape)
            .let { if (ring != null) it.border(size * 0.05f, ring, CircleShape) else it }
            .background(Brush.linearGradient(pal)),
        contentAlignment = Alignment.Center,
    ) {
        Crossfade(shown, label = "avatar") { b ->
            if (b != null) Image(b, nickname, Modifier.size(size), contentScale = ContentScale.Crop)
            else Box(Modifier.size(size), contentAlignment = Alignment.Center) {
                Text(
                    nickname.trim().take(1).uppercase().ifEmpty { "?" }, color = Color.White,
                    fontFamily = UiFont, fontWeight = FontWeight.ExtraBold, fontSize = (size.value * 0.42f).sp,
                )
            }
        }
    }
}
