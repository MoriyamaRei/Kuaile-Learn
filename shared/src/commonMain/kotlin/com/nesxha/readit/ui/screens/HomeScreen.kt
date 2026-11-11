package com.nesxha.readit.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.nesxha.readit.data.FlameTiers
import com.nesxha.readit.data.MAX_RESTORES
import com.nesxha.readit.data.Store
import com.nesxha.readit.data.Words
import com.nesxha.readit.data.today
import com.nesxha.readit.go
import com.nesxha.readit.ui.components.AnimatedNumber
import com.nesxha.readit.ui.components.BrandCard
import com.nesxha.readit.ui.components.Bubbles
import com.nesxha.readit.ui.components.Confetti
import com.nesxha.readit.ui.components.Flame
import com.nesxha.readit.ui.components.HanziText
import com.nesxha.readit.ui.components.LevelBadge
import com.nesxha.readit.ui.components.LevelChip
import com.nesxha.readit.ui.components.PinyinText
import com.nesxha.readit.ui.components.ProgressBar
import com.nesxha.readit.ui.components.ProgressRing
import com.nesxha.readit.ui.components.SectionTitle
import com.nesxha.readit.ui.components.SoftCard
import com.nesxha.readit.ui.components.SpeakButton
import com.nesxha.readit.ui.components.appear
import com.nesxha.readit.ui.components.bounceClick
import com.nesxha.readit.ui.theme.Brand
import com.nesxha.readit.ui.theme.HanziFont
import com.nesxha.readit.ui.theme.LevelColor
import kotlinx.coroutines.delay

private fun greeting(): String = when (com.nesxha.readit.platform.localHourMinute().first) {
    in 4..10 -> "Selamat Pagi"
    in 11..14 -> "Selamat Siang"
    in 15..17 -> "Selamat Sore"
    else -> "Selamat Malam"
}

@Composable
fun HomeScreen(nav: NavHostController) {
    val st by Store.state.collectAsState()
    val cs = MaterialTheme.colorScheme
    var confetti by remember { mutableIntStateOf(0) }
    var ignite by remember { mutableIntStateOf(0) }
    var igniteBanner by remember { mutableStateOf(false) }
    val reached = st.goalReached
    LaunchedEffect(reached) {
        if (reached && Store.celebratedDay != today()) { Store.celebratedDay = today(); confetti++ }
    }
    LaunchedEffect(st.streakLit) {
        if (st.streakLit && Store.ignitedDay != today()) {
            Store.ignitedDay = today()
            delay(450); ignite++; igniteBanner = true
            delay(2600); igniteBanner = false
        }
    }
    val floatAnim = rememberInfiniteTransition(label = "float")
    val floatY by floatAnim.animateFloat(-4f, 4f, infiniteRepeatable(tween(1800, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "fy")

    Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
            // Kepala: salam + level
            Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(bottomStart = 36.dp, bottomEnd = 36.dp)).background(Brand.header)) {
                Bubbles(Modifier.matchParentSize())
                Column(Modifier.statusBarsPadding().padding(start = 22.dp, end = 20.dp, top = 14.dp, bottom = 92.dp)) {
                    Text("你好！${greeting()} 👋", style = MaterialTheme.typography.titleSmall, color = Color.White.copy(alpha = 0.9f))
                    Text("Ayo Belajar Hanzi", style = MaterialTheme.typography.headlineMedium, color = Color.White)
                    Spacer(Modifier.height(10.dp))
                    val li = st.levelInfo
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.bounceClick { nav.go("profile") }) {
                        LevelChip(li.level, onBrand = true)
                        Spacer(Modifier.width(8.dp))
                        Column(Modifier.weight(1f)) {
                            Row {
                                Text(li.title, style = MaterialTheme.typography.labelLarge, color = Color.White, modifier = Modifier.weight(1f), maxLines = 1)
                                Text("${li.xpInLevel}/${li.xpForNext} XP", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.85f), maxLines = 1, softWrap = false)
                            }
                            Spacer(Modifier.height(4.dp))
                            ProgressBar(li.fraction, color = Brand.gold, track = Color(0x33FFFFFF), height = 6.dp)
                        }
                    }
                }
            }

            Column(Modifier.padding(horizontal = 18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                // 1. Target hari ini + streak
                SoftCard(Modifier.fillMaxWidth().pullUp(76.dp).appear(0), radius = 28.dp, padding = PaddingValues(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        ProgressRing(st.goalFraction, Modifier.size(96.dp), stroke = 11.dp, track = cs.surfaceVariant, color = cs.primary) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                if (reached) Text("🎉", fontSize = 28.sp)
                                else AnimatedNumber((st.goalFraction * 100).toInt(), MaterialTheme.typography.headlineSmall, cs.onSurface, "%")
                                Text("${st.doneToday} Kartu", color = cs.onSurfaceVariant, style = MaterialTheme.typography.labelSmall)
                            }
                        }
                        Spacer(Modifier.width(16.dp))
                        Column(Modifier.weight(1f)) {
                            Text("Target Hari Ini", color = cs.onSurface, style = MaterialTheme.typography.titleLarge)
                            Text(
                                when {
                                    reached -> "Hebat! Target hari ini sudah tercapai."
                                    st.doneToday == 0 -> "${st.newWordsToday().size} Kata Baru · ${st.dueWords().size} Ulangan"
                                    else -> "Tinggal ${st.remainingToday} kartu lagi. Semangat!"
                                },
                                color = cs.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium,
                            )
                            Spacer(Modifier.height(10.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Flame(st.streak, st.streakLit, Modifier.size(width = 24.dp, height = 30.dp), ignite = ignite)
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    "${st.streak}",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = if (st.brokenStreak > 0) cs.onSurfaceVariant else cs.onSurface,
                                )
                            }
                        }
                    }
                    // Streak padam: tombol pemulihan di dalam kartu yang sama
                    AnimatedVisibility(st.brokenStreak > 0, enter = expandVertically() + fadeIn(), exit = shrinkVertically() + fadeOut()) {
                        Column(Modifier.padding(top = 14.dp).fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(cs.surfaceVariant).padding(12.dp)) {
                            Text(
                                if (st.restoresLeft > 0) "Kemarin kamu tidak membuka Kuaile Learn. Pulihkan hari ini supaya streak ${st.brokenStreak} hari tidak hilang."
                                else "Jatah pemulihan bulan ini sudah habis. Streak ${st.brokenStreak} hari akan hilang besok.",
                                style = MaterialTheme.typography.bodySmall, color = cs.onSurface,
                            )
                            Spacer(Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Sisa ${st.restoresLeft}/$MAX_RESTORES bulan ini", style = MaterialTheme.typography.labelMedium, color = cs.onSurfaceVariant, modifier = Modifier.weight(1f))
                                if (st.restoresLeft > 0) {
                                    Box(
                                        Modifier.clip(RoundedCornerShape(50)).background(Brand.gold)
                                            .bounceClick { if (Store.restoreStreak()) confetti++ }.padding(horizontal = 14.dp, vertical = 8.dp),
                                    ) { Text("🔥 Pulihkan", color = Color(0xFF3B2600), style = MaterialTheme.typography.labelLarge) }
                                }
                            }
                        }
                    }
                }

                // 2. Lanjutkan belajar
                val left = st.remainingToday
                BrandCard(Modifier.fillMaxWidth().appear(1), onClick = { if (left > 0) nav.navigate("flash") else nav.go("practice") }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(if (left > 0) "Lanjutkan Flashcard" else "Uji Bacaanmu", style = MaterialTheme.typography.titleLarge, color = Color.White)
                            Text(
                                if (left > 0) "$left kartu menunggu hari ini" else "Kartu hari ini beres. Coba latihan membaca hanzi.",
                                style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.9f),
                            )
                        }
                        Text("›", style = MaterialTheme.typography.headlineLarge, color = Color.White)
                    }
                }

                // 3. Kata hari ini (kartu kenalan: lengkap dengan pinyin dan arti)
                val wod = st.wordOfDay()
                SectionTitle("Kata Hari Ini", Modifier.appear(2), action = "Detail", onAction = { nav.navigate("word/${wod.id}") })
                SoftCard(Modifier.fillMaxWidth().appear(2), onClick = { nav.navigate("word/${wod.id}") }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        LevelBadge(wod.level)
                        Spacer(Modifier.width(8.dp))
                        Text("${Words.theme(wod.theme).emoji} ${Words.theme(wod.theme).name}", color = cs.onSurfaceVariant, style = MaterialTheme.typography.labelMedium, modifier = Modifier.weight(1f), maxLines = 1)
                        SpeakButton(wod.hanzi, onBrand = false)
                    }
                    Spacer(Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        HanziText(wod.hanzi, size = if (wod.hanzi.length > 3) 44.sp else 62.sp, color = cs.primary, modifier = Modifier.graphicsLayer { translationY = floatY.dp.toPx() })
                        Spacer(Modifier.width(18.dp))
                        Column(Modifier.weight(1f)) {
                            PinyinText(wod.pinyin, wod.hanzi, size = 20.sp, align = androidx.compose.ui.text.style.TextAlign.Start)
                            Text(wod.main, color = cs.onSurface, style = MaterialTheme.typography.titleLarge)
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(wod.example, fontFamily = HanziFont, color = cs.onSurface, fontSize = 17.sp)
                }

                // 4. Progres per level
                SectionTitle("Progres Per Level", Modifier.appear(3))
                SoftCard(Modifier.fillMaxWidth().appear(3)) {
                    (1..4).forEach { lv ->
                        val total = Words.all.count { it.level == lv }
                        val seen = st.seenCount(lv)
                        val mastered = st.masteredCount(lv)
                        Column(Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(Modifier.size(10.dp).clip(CircleShape).background(LevelColor.of(lv)))
                                Spacer(Modifier.width(8.dp))
                                Text("HSK $lv", style = MaterialTheme.typography.titleMedium, color = cs.onSurface)
                                Spacer(Modifier.width(8.dp))
                                Text("$mastered Hafal", style = MaterialTheme.typography.bodySmall, color = cs.onSurfaceVariant, modifier = Modifier.weight(1f))
                                Text("$seen / $total", style = MaterialTheme.typography.labelLarge, color = cs.primary)
                            }
                            Spacer(Modifier.height(8.dp))
                            ProgressBar(seen.toFloat() / total, height = 8.dp)
                        }
                    }
                }
                Spacer(Modifier.height(20.dp))
            }
        }

        // Kabar kecil saat api menyala
        AnimatedVisibility(
            igniteBanner, Modifier.align(Alignment.TopCenter).statusBarsPadding().padding(top = 8.dp),
            enter = slideInVertically { -it } + fadeIn(), exit = slideOutVertically { -it } + fadeOut(),
        ) {
            Row(
                Modifier.shadow(10.dp, RoundedCornerShape(50)).clip(RoundedCornerShape(50)).background(cs.surface)
                    .padding(start = 8.dp, end = 16.dp, top = 6.dp, bottom = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Flame(st.streak, true, Modifier.size(width = 24.dp, height = 30.dp))
                Spacer(Modifier.width(8.dp))
                Column {
                    Text("Api Menyala! Streak ${st.streak} Hari", style = MaterialTheme.typography.labelLarge, color = cs.onSurface)
                    Text(FlameTiers.of(st.streak).name, style = MaterialTheme.typography.labelSmall, color = cs.onSurfaceVariant)
                }
            }
        }
        Confetti(confetti)
    }
}

/** Tarik kartu ke atas supaya menumpuk di kepala layar. */
private fun Modifier.pullUp(dy: Dp): Modifier = this.layout { m, c ->
    val p = m.measure(c)
    val px = dy.roundToPx()
    layout(p.width, (p.height - px).coerceAtLeast(0)) { p.place(0, -px) }
}
