package com.nesxha.readit.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.nesxha.readit.data.Status
import com.nesxha.readit.data.Store
import com.nesxha.readit.data.Words
import com.nesxha.readit.data.posName
import com.nesxha.readit.data.today
import com.nesxha.readit.ui.components.Bubbles
import com.nesxha.readit.ui.components.HanziText
import com.nesxha.readit.ui.components.HeaderButton
import com.nesxha.readit.ui.components.LevelBadge
import com.nesxha.readit.ui.components.Pill
import com.nesxha.readit.ui.components.PinyinText
import com.nesxha.readit.ui.components.SoftCard
import com.nesxha.readit.ui.components.SpeakButton
import com.nesxha.readit.ui.components.appear
import com.nesxha.readit.ui.components.bounceClick
import com.nesxha.readit.ui.theme.Brand
import com.nesxha.readit.ui.theme.HanziFont
import com.nesxha.readit.util.Tts

@Composable
fun WordDetailScreen(nav: NavHostController, id: Int) {
    val st by Store.state.collectAsState()
    val w = Words.byId[id] ?: Words.all.first()
    val c = st.card(w.id)
    val cs = MaterialTheme.colorScheme
    val starScale = remember { Animatable(1f) }
    val hanziIn = remember { Animatable(0.6f) }
    LaunchedEffect(id) { Tts.speak(w.hanzi); hanziIn.animateTo(1f, spring(dampingRatio = 0.45f, stiffness = 260f)) }
    LaunchedEffect(c.star) { if (c.star) { starScale.snapTo(1.5f); starScale.animateTo(1f, spring(dampingRatio = 0.35f)) } }

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(bottomStart = 36.dp, bottomEnd = 36.dp)).background(Brand.header)) {
            Bubbles(Modifier.matchParentSize())
            Column(Modifier.statusBarsPadding().padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    HeaderButton(Icons.AutoMirrored.Rounded.ArrowBack, "Kembali") { nav.popBackStack() }
                    Spacer(Modifier.weight(1f))
                    Box(
                        Modifier.size(44.dp).clip(CircleShape).background(Color(0x2EFFFFFF)).bounceClick { Store.toggleStar(w.id) },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            if (c.star) Icons.Rounded.Star else Icons.Rounded.StarBorder, "Bintang",
                            tint = if (c.star) Brand.gold else Color.White,
                            modifier = Modifier.graphicsLayer { scaleX = starScale.value; scaleY = starScale.value },
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    SpeakButton(w.hanzi)
                }
                Column(Modifier.fillMaxWidth().padding(vertical = 14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    HanziText(
                        w.hanzi, size = if (w.hanzi.length > 4) 48.sp else 84.sp, color = Color.White,
                        modifier = Modifier.graphicsLayer { scaleX = hanziIn.value; scaleY = hanziIn.value },
                    )
                    Box(Modifier.clip(RoundedCornerShape(14.dp)).background(Color.White).padding(horizontal = 14.dp, vertical = 4.dp)) {
                        PinyinText(w.pinyin, w.hanzi, size = 24.sp, onDark = false)
                    }
                    Spacer(Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        LevelBadge(w.level, onBrand = true)
                        Pill("${Words.theme(w.theme).emoji} ${Words.theme(w.theme).name}", color = Color(0x33FFFFFF), textColor = Color.White)
                        if (w.book > 0) Pill("4下 Bab ${w.book}", color = Color(0x33FFFFFF), textColor = Color.White)
                    }
                }
            }
        }

        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // Tombol mata: tampilkan atau sembunyikan pinyin di semua contoh kalimat (pilihan diingat).
            val showPy = st.settings.examplePinyin
            if (w.senses.any { it.examplePinyin.isNotBlank() }) {
                Row(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(cs.surfaceVariant)
                        .bounceClick { Store.settings { it.copy(examplePinyin = !it.examplePinyin) } }.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(if (showPy) Icons.Rounded.Visibility else Icons.Rounded.VisibilityOff, null, tint = cs.primary)
                    Spacer(Modifier.width(10.dp))
                    Text("Pinyin Contoh Kalimat", style = MaterialTheme.typography.titleSmall, color = cs.onSurface, modifier = Modifier.weight(1f))
                    Text(if (showPy) "Tampil" else "Disembunyikan", style = MaterialTheme.typography.labelLarge, color = cs.primary)
                }
            }
            w.senses.forEachIndexed { i, s ->
                SoftCard(Modifier.fillMaxWidth().appear(i)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(28.dp).clip(CircleShape).background(cs.primary), contentAlignment = Alignment.Center) {
                            Text("${i + 1}", color = Color.White, style = MaterialTheme.typography.labelLarge)
                        }
                        Spacer(Modifier.width(10.dp))
                        Text(posName(s.pos), style = MaterialTheme.typography.labelLarge, color = cs.primary)
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(s.mean.replaceFirstChar { it.uppercaseChar() }, style = MaterialTheme.typography.titleLarge, color = cs.onSurface)
                    if (s.example.isNotBlank()) {
                        Spacer(Modifier.height(8.dp))
                        Row(
                            Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(cs.primaryContainer)
                                .bounceClick { Tts.speak(s.example) }.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text(s.example, fontFamily = HanziFont, fontSize = 20.sp, color = cs.onSurface)
                                if (s.examplePinyin.isNotBlank()) {
                                    // Saat disembunyikan, satu kalimat bisa diintip dengan mengetuk "Lihat Pinyin".
                                    var peek by remember(w.id, i) { mutableStateOf(false) }
                                    if (showPy || peek) {
                                        Text(s.examplePinyin, style = MaterialTheme.typography.bodyMedium, color = cs.onSurfaceVariant,
                                            modifier = Modifier.padding(top = 2.dp))
                                    } else {
                                        Text("👁 Lihat Pinyin", style = MaterialTheme.typography.labelMedium, color = cs.primary,
                                            modifier = Modifier.padding(top = 4.dp).clip(RoundedCornerShape(50)).bounceClick { peek = true }
                                                .padding(horizontal = 4.dp, vertical = 2.dp))
                                    }
                                }
                            }
                            Text("🔊", fontSize = 18.sp)
                        }
                    }
                }
            }

            SoftCard(Modifier.fillMaxWidth().appear(w.senses.size)) {
                Text("Progres Kata Ini", style = MaterialTheme.typography.titleMedium, color = cs.onSurface)
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    InfoBox("Status", c.status.label, cs.primary, Modifier.weight(1f))
                    InfoBox("Benar", "${c.right}", Brand.success, Modifier.weight(1f))
                    InfoBox("Salah", "${c.wrong}", Brand.error, Modifier.weight(1f))
                }
                Spacer(Modifier.height(10.dp))
                val dueText = when {
                    !c.seen -> "Belum pernah dipelajari."
                    c.due <= today() -> "Waktunya diulang hari ini."
                    else -> "Ulangan berikutnya ${c.due - today()} hari lagi (jarak ${c.interval} hari)."
                }
                Text(dueText, style = MaterialTheme.typography.bodyMedium, color = cs.onSurfaceVariant)
                if (c.status != Status.BARU) {
                    Spacer(Modifier.height(10.dp))
                    Text(
                        "Atur Ulang Progres", Modifier.clip(RoundedCornerShape(50)).background(Brand.error.copy(alpha = 0.12f))
                            .bounceClick { Store.resetWord(w.id) }.padding(horizontal = 14.dp, vertical = 8.dp),
                        color = Brand.error, style = MaterialTheme.typography.labelLarge, textAlign = TextAlign.Center,
                    )
                }
            }
            Spacer(Modifier.navigationBarsPadding().height(16.dp))
        }
    }
}

@Composable
private fun InfoBox(label: String, value: String, color: Color, modifier: Modifier) {
    Column(
        modifier.clip(RoundedCornerShape(16.dp)).background(color.copy(alpha = 0.12f)).padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(value, style = MaterialTheme.typography.titleMedium, color = color)
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
