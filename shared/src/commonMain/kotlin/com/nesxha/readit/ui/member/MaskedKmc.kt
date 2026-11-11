package com.nesxha.readit.ui.member

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

/** Nomor KMC yang disamarkan (KMC-•••••-••); ketuk ikon mata untuk menampilkan atau menyembunyikan. */
@Composable
fun MaskedKmc(kmc: String, style: TextStyle, color: Color, modifier: Modifier = Modifier) {
    var shown by remember { mutableStateOf(false) }
    Row(modifier.clip(CircleShape).clickable { shown = !shown }.padding(vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
        AnimatedContent(shown, label = "kmc", transitionSpec = { fadeIn() togetherWith fadeOut() }) { s ->
            Text(if (s) kmc else mask(kmc), style = style, color = color, maxLines = 1)
        }
        Spacer(Modifier.width(6.dp))
        Icon(
            if (shown) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
            if (shown) "Sembunyikan Nomor KMC" else "Tampilkan Nomor KMC", tint = color, modifier = Modifier.size(18.dp),
        )
    }
}

fun mask(kmc: String): String = if (kmc.startsWith("KMC-")) "KMC-•••••-••" else "•••••••"
