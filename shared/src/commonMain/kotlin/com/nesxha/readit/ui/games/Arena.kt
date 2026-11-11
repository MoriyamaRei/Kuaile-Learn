package com.nesxha.readit.ui.games

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.nesxha.readit.auth.Member
import com.nesxha.readit.auth.RankStatus
import com.nesxha.readit.auth.Res
import com.nesxha.readit.data.RankRules
import com.nesxha.readit.ui.components.BrandCard
import com.nesxha.readit.ui.components.HeaderButton
import com.nesxha.readit.ui.components.PrimaryButton
import com.nesxha.readit.ui.components.ProgressBar
import com.nesxha.readit.ui.components.RankEmblem
import com.nesxha.readit.ui.components.ScreenHeader
import com.nesxha.readit.ui.components.SoftCard
import com.nesxha.readit.ui.components.appear
import com.nesxha.readit.ui.components.c1
import com.nesxha.readit.ui.components.c2
import com.nesxha.readit.ui.member.ProfilePrefs
import com.nesxha.readit.ui.theme.Brand
import com.nesxha.readit.platform.nowMs

/** Rank terakhir yang diketahui (untuk offline). */
internal object RankCache {
    var status: RankStatus? = null
    val p: Int get() = status?.p ?: ProfilePrefs.cachedMe?.rankP ?: 0
}

@Composable
fun ArenaBanner(modifier: Modifier = Modifier, onClick: () -> Unit) {
    val pos = RankRules.pos(RankCache.p)
    BrandCard(modifier.fillMaxWidth(), onClick = onClick,
        brush = Brush.linearGradient(listOf(Color(0xFF0B1E5B), pos.tier.c1(), pos.tier.c2()))) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            RankEmblem(pos, 64.dp)
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text("Arena Rank", style = MaterialTheme.typography.titleLarge, color = Color.White)
                Text("Kuis Kilat · Iron sampai Dragon", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.85f))
                Text(pos.label, style = MaterialTheme.typography.labelLarge, color = Brand.gold)
            }
            Text("›", style = MaterialTheme.typography.headlineMedium, color = Color.White)
        }
    }
}

internal fun countdown(ms: Long): String {
    if (ms <= 0) return "Sebentar Lagi"
    val m = ms / 60000; val d = m / 1440; val h = (m % 1440) / 60
    return if (d > 0) "$d Hari $h Jam" else "$h Jam ${m % 60} Menit"
}

@Composable
fun ArenaScreen(nav: NavHostController) {
    var status by remember { mutableStateOf(RankCache.status) }
    var offline by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        when (val r = Member.rank()) {
            is Res.Ok -> { status = r.value; RankCache.status = r.value; offline = false }
            is Res.Err -> offline = true
        }
    }
    val p = status?.p ?: RankCache.p
    val pos = RankRules.pos(p)
    val cs = MaterialTheme.colorScheme
    Column(Modifier.fillMaxSize().background(cs.background).verticalScroll(rememberScrollState())) {
        ScreenHeader("Arena Rank", "Main Kilat, kumpulkan RP, naik rank",
            leading = { HeaderButton(Icons.AutoMirrored.Rounded.ArrowBack, "Kembali") { nav.popBackStack() } })
        Column(Modifier.padding(18.dp).navigationBarsPadding(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            BrandCard(Modifier.fillMaxWidth().appear(0), brush = Brush.linearGradient(listOf(Color(0xFF0B1E5B), pos.tier.c1()))) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RankEmblem(pos, 96.dp)
                    Spacer(Modifier.width(14.dp))
                    Column(Modifier.weight(1f)) {
                        Text(pos.label, style = MaterialTheme.typography.headlineSmall, color = Color.White)
                        Text("${pos.tier.hanzi} ${pos.tier.pinyin}", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.85f))
                        Spacer(Modifier.height(8.dp))
                        ProgressBar(pos.fraction, color = Brand.gold, track = Color(0x33FFFFFF), height = 8.dp)
                        Spacer(Modifier.height(4.dp))
                        Text(if (pos.isDragon) "${pos.rp} RP di Dragon" else "${pos.rp}/100 RP Menuju Divisi Berikutnya",
                            style = MaterialTheme.typography.labelMedium, color = Color.White)
                    }
                }
                Spacer(Modifier.height(12.dp))
                val s = status
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Chip2(if (s != null) "🎁 Bonus ×2: ${s.bonusLeft} Lagi" else "🎁 Bonus ×2 Harian", Modifier.weight(1f))
                    if (s != null && s.protect > 0) Chip2("🛡️ Terlindung ${s.protect}×", Modifier.weight(1f))
                }
                if (s != null) Text("⏳ Musim Berakhir dalam ${countdown(s.seasonEnd - nowMs())}",
                    style = MaterialTheme.typography.labelMedium, color = Color.White.copy(alpha = 0.9f), modifier = Modifier.padding(top = 8.dp))
            }
            if (offline) SoftCard(Modifier.fillMaxWidth()) {
                Text("Mode Latihan", style = MaterialTheme.typography.titleSmall, color = cs.onSurface)
                Text("Kamu sedang offline atau server belum tersambung. Kilat tetap bisa dimainkan, tapi RP tidak dihitung.",
                    style = MaterialTheme.typography.bodySmall, color = cs.onSurfaceVariant)
            }
            SoftCard(Modifier.fillMaxWidth().appear(1), onClick = { nav.navigate("kilat") }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(58.dp).clip(RoundedCornerShape(18.dp)).background(Brush.linearGradient(listOf(pos.tier.c1(), pos.tier.c2()))),
                        contentAlignment = Alignment.Center) { Text("⚡", fontSize = 30.sp) }
                    Spacer(Modifier.width(14.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Kuis Kilat Rank", style = MaterialTheme.typography.titleMedium, color = cs.onSurface)
                        Text("1, 2, atau 3 menit. Makin cepat dan tepat menjawab, makin besar RP-nya.", style = MaterialTheme.typography.bodySmall, color = cs.onSurfaceVariant)
                        Text("📚 HSK 1–${RankRules.maxHsk(p)} · Pengali Rank ${RankRules.tierFactor(p).let { if (it == 1.0) "×1" else if (it == 0.75) "×0,75" else "×0,5" }}",
                            style = MaterialTheme.typography.labelMedium, color = cs.primary)
                    }
                    Text("Main", style = MaterialTheme.typography.labelLarge, color = Color.White,
                        modifier = Modifier.clip(RoundedCornerShape(50)).background(cs.primary).padding(horizontal = 14.dp, vertical = 8.dp))
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                PrimaryButton("📜 Aturan Rank", Modifier.weight(1f), secondary = true) { nav.navigate("rankrules") }
                PrimaryButton("🏅 Daftar Rank", Modifier.weight(1f), secondary = true) { nav.navigate("ranklist") }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                PrimaryButton("📜 Riwayat Kilat", Modifier.weight(1f), secondary = true) { nav.navigate("kilathistory") }
                PrimaryButton("🏆 Leaderboard", Modifier.weight(1f), secondary = true) { nav.navigate("leaderboard") }
            }
            status?.best?.takeIf { it.isNotEmpty() }?.let { best ->
                SoftCard(Modifier.fillMaxWidth()) {
                    Text("Rank Tertinggi Musim Lalu", style = MaterialTheme.typography.titleSmall, color = cs.onSurface)
                    best.take(4).forEach { (season, peak) ->
                        Text("Musim ${season % 4 + 1} · ${season / 4}: ${RankRules.pos(peak).label}", style = MaterialTheme.typography.bodySmall, color = cs.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
private fun Chip2(text: String, modifier: Modifier) {
    Text(text, style = MaterialTheme.typography.labelMedium, color = Color.White, maxLines = 1, textAlign = TextAlign.Center,
        modifier = modifier.clip(RoundedCornerShape(50)).background(Color(0x33FFFFFF)).padding(horizontal = 10.dp, vertical = 6.dp))
}
