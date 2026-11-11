package com.nesxha.readit.ui.member

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import org.jetbrains.compose.resources.painterResource
import com.nesxha.readit.res.Res as AppRes
import com.nesxha.readit.res.mascot_head
import com.nesxha.readit.res.mascot_full
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.nesxha.readit.auth.Auth
import com.nesxha.readit.data.RankRules
import com.nesxha.readit.data.Store
import com.nesxha.readit.ui.components.HeaderButton
import com.nesxha.readit.ui.components.RankFrame
import com.nesxha.readit.ui.components.RankPill
import com.nesxha.readit.ui.components.ScreenHeader
import com.nesxha.readit.ui.components.bounceClick
import com.nesxha.readit.ui.components.c1
import com.nesxha.readit.ui.components.c2
import com.nesxha.readit.ui.theme.Brand
import com.nesxha.readit.ui.theme.HanziFont

/** Gambar kode QR dari [Qr], dengan tepi putih satu modul. */
@Composable
private fun QrImage(m: Array<BooleanArray>, modifier: Modifier) {
    androidx.compose.foundation.Canvas(modifier) {
        val n = m.size + 2
        val cell = size.minDimension / n
        for (y in m.indices) for (x in m.indices) if (m[y][x])
            drawRect(Color(0xFF0B1E5B), androidx.compose.ui.geometry.Offset((x + 1) * cell, (y + 1) * cell), androidx.compose.ui.geometry.Size(cell + 0.5f, cell + 0.5f))
    }
}

/** Kartu Anggota Digital: depan berisi foto, nickname, rank, level; ketuk untuk membalik ke QR nomor KMC. */
@Composable
fun MemberCardScreen(nav: NavHostController) {
    val phase by Auth.phase.collectAsState()
    val session = (phase as? Auth.Phase.Ready)?.session
    val st by Store.state.collectAsState()
    val me = rememberMe()
    var back by remember { mutableStateOf(false) }
    val rot by animateFloatAsState(if (back) 180f else 0f, spring(dampingRatio = 0.7f, stiffness = 180f), label = "flip")
    val density = LocalDensity.current
    val pos = RankRules.pos(me?.rankP ?: 0)
    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        ScreenHeader("Kartu Anggota", "Ketuk kartu untuk membalik",
            leading = { HeaderButton(Icons.AutoMirrored.Rounded.ArrowBack, "Kembali") { nav.popBackStack() } })
        Spacer(Modifier.height(28.dp))
        Box(
            Modifier.padding(horizontal = 22.dp).fillMaxWidth().aspectRatio(0.63f)
                .graphicsLayer { rotationY = rot; cameraDistance = 16f * density.density }
                .bounceClick { back = !back },
        ) {
            val showBack = rot > 90f
            Box(
                Modifier.fillMaxSize().graphicsLayer { if (showBack) rotationY = 180f }
                    .shadow(20.dp, RoundedCornerShape(28.dp), spotColor = pos.tier.c1())
                    .clip(RoundedCornerShape(28.dp))
                    .background(Brush.linearGradient(listOf(Color(0xFF0B1E5B), Color(0xFF1D4ED8), pos.tier.c1()))),
            ) {
                com.nesxha.readit.ui.components.Bubbles(Modifier.fillMaxSize(), alpha = 0.10f)
                if (!showBack) Column(Modifier.fillMaxSize().padding(22.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(painterResource(AppRes.drawable.mascot_head), null, Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)), contentScale = ContentScale.Crop)
                        Spacer(Modifier.width(10.dp))
                        Column(Modifier.weight(1f)) {
                            Text("Kuaile Mandarin Club", style = MaterialTheme.typography.titleSmall, color = Color.White)
                            Text("快乐中文俱乐部", fontFamily = HanziFont, color = Color.White.copy(alpha = 0.8f), fontSize = 13.sp)
                        }
                        Text("KARTU ANGGOTA", style = MaterialTheme.typography.labelSmall, color = Brand.gold)
                    }
                    Spacer(Modifier.weight(1f))
                    RankFrame(if (me != null) pos else null, 170.dp) { Avatar(me?.pid, me?.photoVer ?: 0, session?.nickname ?: "?", 132.dp, full = true) }
                    Spacer(Modifier.height(14.dp))
                    Text(session?.nickname ?: "", style = MaterialTheme.typography.headlineMedium, color = Color.White, maxLines = 1)
                    val title = me?.title ?: ProfilePrefs.title()
                    if (title != null) Text(title, style = MaterialTheme.typography.labelLarge, color = Brand.gold)
                    Spacer(Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("⭐ Level ${st.levelInfo.level}", style = MaterialTheme.typography.labelLarge, color = Color.White,
                            modifier = Modifier.clip(RoundedCornerShape(50)).background(Color(0x33FFFFFF)).padding(horizontal = 12.dp, vertical = 5.dp))
                        if (me != null) RankPill(pos)
                    }
                    Spacer(Modifier.weight(1f))
                    if (session != null) MaskedKmc(session.kmc, MaterialTheme.typography.titleMedium, Color.White)
                } else Column(Modifier.fillMaxSize().padding(26.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    val kmc = session?.kmc ?: ""
                    val bmp = remember(kmc) { if (kmc.isNotEmpty()) com.nesxha.readit.util.Qr.encode(kmc) else null }
                    Text("Nomor KMC", style = MaterialTheme.typography.titleMedium, color = Color.White)
                    Spacer(Modifier.height(14.dp))
                    if (bmp != null) Box(Modifier.fillMaxWidth(0.8f).aspectRatio(1f).clip(RoundedCornerShape(20.dp)).background(Color.White).padding(12.dp)) {
                        QrImage(bmp, Modifier.fillMaxSize())
                    }
                    Spacer(Modifier.height(14.dp))
                    MaskedKmc(kmc, MaterialTheme.typography.titleLarge, Color.White)
                    Spacer(Modifier.height(10.dp))
                    Text("Tunjukkan QR ini ke pengurus KMC, atau pindai di layar Masuk.", style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f), textAlign = TextAlign.Center)
                }
            }
        }
    }
}
