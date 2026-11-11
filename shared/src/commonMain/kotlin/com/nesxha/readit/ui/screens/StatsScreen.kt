package com.nesxha.readit.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MilitaryTech
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.nesxha.readit.ui.components.bounceClick
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.material3.Icon
import com.nesxha.readit.ui.components.Pill
import com.nesxha.readit.ui.components.flameColors
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.nesxha.readit.data.Badges
import com.nesxha.readit.data.Counter
import com.nesxha.readit.data.FlameTiers
import com.nesxha.readit.data.NumCat
import com.nesxha.readit.data.Store
import com.nesxha.readit.data.Words
import com.nesxha.readit.data.today
import com.nesxha.readit.ui.components.ActionRow
import com.nesxha.readit.ui.components.AnimatedNumber
import com.nesxha.readit.ui.components.HeaderButton
import com.nesxha.readit.ui.components.BrandCard
import com.nesxha.readit.ui.components.Flame
import com.nesxha.readit.ui.components.FlameTierStrip
import com.nesxha.readit.ui.components.LevelEmblem
import com.nesxha.readit.ui.components.ProgressBar
import com.nesxha.readit.ui.components.ScreenHeader
import com.nesxha.readit.ui.components.SectionTitle
import com.nesxha.readit.ui.components.Segmented
import com.nesxha.readit.ui.components.SoftCard
import com.nesxha.readit.ui.components.WordRow
import com.nesxha.readit.ui.components.appear
import com.nesxha.readit.ui.theme.Brand
import com.nesxha.readit.platform.epochDayToDate
import com.nesxha.readit.platform.dayName
import com.nesxha.readit.platform.monthName
import com.nesxha.readit.platform.monthShort
import com.nesxha.readit.platform.fixed

private val ranges = listOf(7, 14, 30)

@Composable
fun StatsScreen(nav: NavHostController) {
    val st by Store.state.collectAsState()
    var rangeIdx by rememberSaveable { mutableIntStateOf(1) }
    val range = ranges[rangeIdx]
    val t = today()
    val days = (range - 1 downTo 0).map { t - it }
    val logs = days.map { st.days[it] }
    val totalAll = st.days.values.sumOf { it.total }
    val correct = st.days.values.sumOf { it.correct }
    val wrong = st.days.values.sumOf { it.wrong }
    val acc = if (correct + wrong == 0) 0 else correct * 100 / (correct + wrong)
    val cs = MaterialTheme.colorScheme

    val me = com.nesxha.readit.ui.member.rememberMe()
    var pickTitle by remember { mutableStateOf(false) }
    var showAll by rememberSaveable { mutableStateOf(false) }
    if (pickTitle) com.nesxha.readit.ui.member.TitlePicker(me, st) { pickTitle = false }

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        com.nesxha.readit.ui.member.ProfileHero(nav, me, st) { pickTitle = true }
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            if (com.nesxha.readit.platform.Accounts.ON) {
                com.nesxha.readit.ui.member.QuickStats(me, st, Modifier.appear(0))
                com.nesxha.readit.ui.member.ProfileShortcuts(nav, Modifier.appear(1))
            }
            com.nesxha.readit.ui.member.ShowcaseCard(nav, me, st, Modifier.appear(2))
            // Statistik lengkap bisa dibuka-tutup supaya profil tidak terlalu panjang.
            Row(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(cs.primaryContainer)
                    .bounceClick { showAll = !showAll }.padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("📊 Statistik Lengkap", style = MaterialTheme.typography.titleSmall, color = cs.onPrimaryContainer, modifier = Modifier.weight(1f))
                Text(if (showAll) "Tutup ▲" else "Buka ▼", style = MaterialTheme.typography.labelLarge, color = cs.primary)
            }
            if (showAll) {
            // 2. Api streak
            SoftCard(Modifier.fillMaxWidth().appear(1)) {
                val tier = FlameTiers.of(st.streak)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(width = 96.dp, height = 116.dp), contentAlignment = Alignment.BottomCenter) {
                        Flame(st.streak, st.streakLit, Modifier.size(width = 84.dp, height = 108.dp), sparks = st.streakLit)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.Bottom) {
                            AnimatedNumber(st.streak, MaterialTheme.typography.displaySmall, cs.onSurface)
                            Text(" hari", style = MaterialTheme.typography.titleLarge, color = cs.onSurfaceVariant)
                        }
                        Text(if (st.brokenStreak > 0) "Streak Padam" else "Streak", style = MaterialTheme.typography.labelLarge, color = cs.onSurfaceVariant)
                        Spacer(Modifier.height(8.dp))
                        Pill(if (st.streak == 0) "Belum Menyala" else tier.name, color = flameColors(tier.level).glow.copy(alpha = 0.2f), textColor = cs.onSurface)
                        Spacer(Modifier.height(6.dp))
                        Text("Rekor ${st.bestStreak} hari", style = MaterialTheme.typography.labelMedium, color = cs.onSurfaceVariant)
                    }
                }
                Spacer(Modifier.height(14.dp))
                FlameTierStrip(st.streak, st.streakLit, Modifier.fillMaxWidth())
            }

            // 4. Angka-angka
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatTile("🎯", acc, "%", "Akurasi", Modifier.weight(1f).appear(3))
                StatTile("🃏", totalAll, "", "Jawaban", Modifier.weight(1f).appear(3))
                StatTile("📖", st.seenCount(), "", "Dipelajari", Modifier.weight(1f).appear(4))
                StatTile("🏆", st.masteredCount(), "", "Hafal", Modifier.weight(1f).appear(4))
            }

            // Akurasi Kuis Angka per kategori
            val numCats = NumCat.entries.filter { it != NumCat.CAMPUR && st.counter(Counter.numTotal(it)) > 0 }
            if (numCats.isNotEmpty()) {
                SoftCard(Modifier.fillMaxWidth().appear(6)) {
                    Text("Kuis Angka", style = MaterialTheme.typography.titleMedium, color = cs.onSurface)
                    Text("${st.counter(Counter.NUM_OK)} jawaban benar", style = MaterialTheme.typography.bodySmall, color = cs.onSurfaceVariant)
                    numCats.forEach { c ->
                        val total = st.counter(Counter.numTotal(c))
                        val ok = st.counter(Counter.numOk(c))
                        Spacer(Modifier.height(10.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("${c.emoji} ${c.title}", style = MaterialTheme.typography.labelLarge, color = cs.onSurface, modifier = Modifier.weight(1f), maxLines = 1)
                            Text("${ok * 100 / total}% · $ok/$total", style = MaterialTheme.typography.labelMedium, color = cs.primary, maxLines = 1)
                        }
                        Spacer(Modifier.height(4.dp))
                        ProgressBar(ok.toFloat() / total, height = 6.dp)
                    }
                }
            }

            SoftCard(Modifier.fillMaxWidth().appear(6)) {
                Text("Aktivitas Harian", style = MaterialTheme.typography.titleMedium, color = cs.onSurface)
                Spacer(Modifier.height(10.dp))
                Segmented(ranges.map { "$it Hari" }, rangeIdx) { rangeIdx = it }
                Spacer(Modifier.height(16.dp))
                DailyChart(logs.map { it?.reviews ?: 0 }, logs.map { it?.quiz ?: 0 }, range)
                Spacer(Modifier.height(6.dp))
                Row {
                    Text(epochDayToDate(days.first()).let { d -> "${d.dayOfMonth} ${monthShort(d.monthNumber)}" }, style = MaterialTheme.typography.labelSmall, color = cs.onSurfaceVariant)
                    Spacer(Modifier.weight(1f))
                    Text("Hari Ini", style = MaterialTheme.typography.labelSmall, color = cs.onSurfaceVariant)
                }
                Spacer(Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Legend(cs.primary, "Flashcard"); Spacer(Modifier.width(16.dp)); Legend(Brand.blueLight.copy(alpha = 0.55f), "Kuis")
                }
            }

            val finish = st.estimatedFinish()
            BrandCard(Modifier.fillMaxWidth().appear(7)) {
                Text("📅 Perkiraan Tanggal Selesai", color = Color.White.copy(alpha = 0.9f), style = MaterialTheme.typography.titleSmall)
                Spacer(Modifier.height(6.dp))
                if (finish == null) {
                    Text("Semua ${Words.all.size} kata sudah dipelajari. Luar biasa!", color = Color.White, style = MaterialTheme.typography.headlineSmall)
                } else {
                    Text(
                        "${dayName(finish)}, ${finish.dayOfMonth} ${monthName(finish.monthNumber)} ${finish.year}",
                        color = Color.White, style = MaterialTheme.typography.headlineSmall,
                    )
                    val left = Words.all.size - st.seenCount()
                    Text(
                        "$left kata lagi dengan kecepatan ${fixed(st.pacePerDay(), 1).replace('.', ',')} kata baru per hari.",
                        color = Color.White.copy(alpha = 0.88f), style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }

            SectionTitle("20 Kata Paling Sering Salah", Modifier.appear(8))
            val wrongList = st.mostWrong(20)
            if (wrongList.isEmpty()) {
                Text("Belum ada kata yang salah. Pertahankan!", style = MaterialTheme.typography.bodyMedium, color = cs.onSurfaceVariant)
            } else {
                wrongList.forEachIndexed { i, (w, c) ->
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.appear(9 + i)) {
                        Text("${i + 1}", Modifier.width(28.dp), style = MaterialTheme.typography.titleMedium, color = cs.primary, textAlign = TextAlign.Center)
                        Box(Modifier.weight(1f)) { WordRow(w, extra = "${c.wrong}× salah") { nav.navigate("word/${w.id}") } }
                    }
                }
            }
            }
            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
private fun XpBox(label: String, value: Int, modifier: Modifier) {
    Column(
        modifier.clip(androidx.compose.foundation.shape.RoundedCornerShape(16.dp)).background(Color(0x26FFFFFF)).padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AnimatedNumber(value, MaterialTheme.typography.titleMedium, Color.White)
        Text(label, style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.85f))
    }
}

@Composable
private fun Legend(color: Color, label: String) {
    Box(Modifier.size(12.dp).clip(CircleShape).background(color))
    Spacer(Modifier.width(6.dp))
    Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
}

@Composable
private fun DailyChart(reviews: List<Int>, quiz: List<Int>, key: Int) {
    val anim = remember(key) { Animatable(0f) }
    LaunchedEffect(key) { anim.animateTo(1f, tween(900, easing = FastOutSlowInEasing)) }
    val max = (reviews.indices.maxOfOrNull { reviews[it] + quiz[it] } ?: 0).coerceAtLeast(5)
    val grid = MaterialTheme.colorScheme.outline
    val labelColor = MaterialTheme.colorScheme.onSurfaceVariant
    val main = MaterialTheme.colorScheme.primary
    val second = Brand.blueLight.copy(alpha = 0.55f)
    Row(verticalAlignment = Alignment.Top) {
        Column(Modifier.height(160.dp), verticalArrangement = Arrangement.SpaceBetween) {
            Text("$max", fontSize = 10.sp, color = labelColor)
            Text("${max / 2}", fontSize = 10.sp, color = labelColor)
            Text("0", fontSize = 10.sp, color = labelColor)
        }
        Spacer(Modifier.width(6.dp))
        Canvas(Modifier.weight(1f).height(160.dp)) {
            val n = reviews.size
            val slot = size.width / n
            val bw = slot * 0.62f
            for (g in 0..2) {
                val y = size.height * g / 2f
                drawLine(grid, Offset(0f, y), Offset(size.width, y), strokeWidth = 1.5f)
            }
            for (i in 0 until n) {
                // Batang tumbuh bergiliran dari kiri ke kanan.
                val local = ((anim.value * 1.6f) - i.toFloat() / n * 0.6f).coerceIn(0f, 1f)
                val x = i * slot + (slot - bw) / 2
                val hr = size.height * reviews[i] / max * local
                val hq = size.height * quiz[i] / max * local
                val r = CornerRadius(bw / 3, bw / 3)
                if (hq > 0) drawRoundRect(second, Offset(x, size.height - hr - hq), Size(bw, hq), r)
                if (hr > 0) drawRoundRect(main, Offset(x, size.height - hr), Size(bw, hr), r)
            }
        }
    }
}

@Composable
private fun StatTile(emoji: String, value: Int, suffix: String, label: String, modifier: Modifier) {
    SoftCard(modifier, radius = 22.dp, padding = PaddingValues(12.dp)) {
        Text(emoji, fontSize = 20.sp)
        Spacer(Modifier.height(4.dp))
        AnimatedNumber(value, MaterialTheme.typography.titleLarge, MaterialTheme.colorScheme.onSurface, suffix)
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.labelSmall, maxLines = 1)
    }
}
