package com.nesxha.readit.ui.games

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.nesxha.readit.auth.AuthError
import com.nesxha.readit.auth.KilatGame
import com.nesxha.readit.auth.KilatSummary
import com.nesxha.readit.auth.Member
import com.nesxha.readit.auth.Res
import com.nesxha.readit.data.KilatLocal
import com.nesxha.readit.data.RankRules
import com.nesxha.readit.ui.components.EmptyState
import com.nesxha.readit.ui.components.HeaderButton
import com.nesxha.readit.ui.components.PrimaryButton
import com.nesxha.readit.ui.components.ScreenHeader
import com.nesxha.readit.ui.components.SoftCard
import com.nesxha.readit.ui.components.appear
import com.nesxha.readit.ui.theme.Brand
import kotlinx.coroutines.launch
import com.nesxha.readit.platform.epochDayToDate
import com.nesxha.readit.platform.formatDateShort
import com.nesxha.readit.platform.localEpochDay
import com.nesxha.readit.platform.localHourMinute
import com.nesxha.readit.platform.pad2
import com.nesxha.readit.platform.roundHalfUp

/** Riwayat Kuis Kilat: hasil setiap permainan, terbaru di atas, dikelompokkan per hari. */
@Composable
fun KilatHistoryScreen(nav: NavHostController) {
    val scope = rememberCoroutineScope()
    val games = remember { mutableStateListOf<KilatGame>() }
    var summary by remember { mutableStateOf<KilatSummary?>(null) }
    var loading by remember { mutableStateOf(true) }
    var more by remember { mutableStateOf(false) }
    var err by remember { mutableStateOf<String?>(null) }
    var reload by remember { mutableIntStateOf(0) }
    val local = remember(reload) { KilatLocal.load() }

    suspend fun load(before: Long?) {
        loading = true
        if (!Member.available) { loading = false; more = false; return }
        when (val r = Member.kilatHistory(before)) {
            is Res.Ok -> {
                if (before == null) games.clear()
                games += r.value.games
                summary = r.value.summary
                more = r.value.games.size >= 30
                err = null
            }
            is Res.Err -> err = if (r.error == AuthError.Network) "Sedang offline. Yang tampil hanya permainan di ${com.nesxha.readit.platform.thisDevice}." else r.error.message
        }
        loading = false
    }
    LaunchedEffect(reload) { load(null) }

    // Permainan offline (di HP ini) digabung dengan riwayat server, diurutkan dari yang terbaru.
    val all = (games + local).sortedByDescending { it.at }
    val sum = summary ?: KilatSummary(
        all.size, all.maxOfOrNull { it.score } ?: 0,
        all.sumOf { it.total }.let { t -> if (t == 0) 0 else roundHalfUp(all.sumOf { it.correct } * 100f / t) }, 0,
    )

    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        ScreenHeader("Riwayat Kilat", "Hasil setiap permainan Kuis Kilat",
            leading = { HeaderButton(Icons.AutoMirrored.Rounded.ArrowBack, "Kembali") { nav.popBackStack() } })
        Column(
            Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(18.dp).navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            SummaryCard(sum, Modifier.appear(0))
            err?.let { Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
            if (all.isEmpty() && !loading) {
                EmptyState("⚡", "Belum Ada Riwayat", "Main Kuis Kilat dulu, nanti hasilnya muncul di sini.")
            }
            var lastDay = ""
            all.forEachIndexed { i, g ->
                val day = dayLabel(g.at)
                if (day != lastDay) {
                    lastDay = day
                    Text(day, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(top = 6.dp))
                }
                GameRow(g, Modifier.appear(minOf(i + 1, 10)))
            }
            if (loading) Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) { CircularProgressIndicator(Modifier.padding(12.dp)) }
            else if (more) PrimaryButton("Muat Lebih Banyak", secondary = true) {
                scope.launch { load(games.lastOrNull()?.id) }
            }
            if (err != null && !loading) PrimaryButton("Coba Lagi", secondary = true) { reload++ }
        }
    }
}

@Composable
private fun SummaryCard(s: KilatSummary, modifier: Modifier) {
    val cs = MaterialTheme.colorScheme
    SoftCard(modifier.fillMaxWidth()) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Stat("${s.games}", "Permainan", Modifier.weight(1f))
            Stat("${s.accuracy}%", "Rata-Rata Benar", Modifier.weight(1f))
            Stat("${s.best}", "Skor Terbaik", Modifier.weight(1f))
            Stat((if (s.weekRp > 0) "+" else "") + "${s.weekRp}", "RP Minggu Ini", Modifier.weight(1f),
                if (s.weekRp > 0) Brand.success else if (s.weekRp < 0) Brand.error else cs.onSurface)
        }
    }
}

@Composable
private fun Stat(value: String, label: String, modifier: Modifier, color: Color = MaterialTheme.colorScheme.primary) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleLarge, color = color, maxLines = 1)
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center)
    }
}

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
private fun GameRow(g: KilatGame, modifier: Modifier) {
    val cs = MaterialTheme.colorScheme
    SoftCard(modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(localHourMinute(g.at).let { (h, m) -> "${pad2(h)}.${pad2(m)}" }, style = MaterialTheme.typography.titleSmall, color = cs.onSurface)
                    Spacer(Modifier.width(8.dp))
                    Tag("${g.minutes} Menit", cs.primaryContainer, cs.onPrimaryContainer)
                }
                Spacer(Modifier.height(4.dp))
                Text("${g.correct} Benar · ${g.wrong} Salah · ${g.total} Soal · ${g.accuracy}%",
                    style = MaterialTheme.typography.bodyMedium, color = cs.onSurface)
                Text(if (g.quit) "Skor tidak dihitung" else "Skor ${g.score}", style = MaterialTheme.typography.bodySmall, color = cs.onSurfaceVariant)
            }
            if (!g.offline) {
                val d = g.delta
                Text((if (d > 0) "+" else "") + "$d RP", style = MaterialTheme.typography.titleMedium,
                    color = if (d > 0) Brand.success else if (d < 0) Brand.error else cs.onSurfaceVariant)
            }
        }
        val tags = buildList {
            if (g.offline) add(Triple("📶 Mode Offline · Tidak Dihitung", cs.surfaceVariant, cs.onSurfaceVariant))
            if (g.quit) add(Triple("🚪 Keluar di Tengah", Brand.error.copy(alpha = 0.14f), Brand.error))
            if (g.bonus) add(Triple("🎁 Bonus ×2", Brand.gold.copy(alpha = 0.2f), cs.onSurface))
            if (!g.offline && RankRules.promoted(g.oldP, g.newP)) add(Triple("⬆️ Naik ke ${RankRules.pos(g.newP).label}", Brand.success.copy(alpha = 0.14f), Brand.success))
            else if (!g.offline && RankRules.promoted(g.newP, g.oldP)) add(Triple("⬇️ Turun ke ${RankRules.pos(g.newP).label}", Brand.error.copy(alpha = 0.14f), Brand.error))
        }
        if (tags.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            androidx.compose.foundation.layout.FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                tags.forEach { (t, bg, fg) -> Tag(t, bg, fg) }
            }
        }
    }
}

@Composable
private fun Tag(text: String, bg: Color, fg: Color) {
    Text(text, style = MaterialTheme.typography.labelMedium, color = fg, maxLines = 1,
        modifier = Modifier.clip(RoundedCornerShape(50)).background(bg).padding(horizontal = 10.dp, vertical = 4.dp))
}

/** "Hari Ini", "Kemarin", atau "Kamis, 5 Nov 2026". */
private fun dayLabel(ms: Long): String {
    val d = localEpochDay(ms)
    val today = localEpochDay()
    if (d == today) return "Hari Ini"
    if (d == today - 1) return "Kemarin"
    return formatDateShort(epochDayToDate(d))
}
