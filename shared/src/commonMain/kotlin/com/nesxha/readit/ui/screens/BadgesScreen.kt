package com.nesxha.readit.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.runtime.setValue
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.MaterialTheme
import com.nesxha.readit.ui.theme.Brand
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.nesxha.readit.data.Badge
import com.nesxha.readit.data.Badges
import com.nesxha.readit.data.Store
import com.nesxha.readit.ui.components.BrandCard
import com.nesxha.readit.ui.components.Confetti
import com.nesxha.readit.ui.components.HeaderButton
import com.nesxha.readit.ui.components.ProgressBar
import com.nesxha.readit.ui.components.ScreenHeader
import com.nesxha.readit.ui.components.SoftCard
import com.nesxha.readit.ui.components.appear
import com.nesxha.readit.ui.components.bounceClick
import com.nesxha.readit.platform.epochDayToDate
import com.nesxha.readit.platform.monthShort

@Composable
fun BadgesScreen(nav: NavHostController) {
    val st by Store.state.collectAsState()
    val got = Badges.all.count { it.id in st.badges }
    var filter by androidx.compose.runtime.saveable.rememberSaveable { androidx.compose.runtime.mutableIntStateOf(0) }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        ScreenHeader(
            "Lencana", "$got dari ${Badges.all.size} Lencana",
            leading = { HeaderButton(Icons.AutoMirrored.Rounded.ArrowBack, "Kembali") { nav.popBackStack() } },
        ) {
            ProgressBar(got.toFloat() / Badges.all.size, color = Brand.gold, track = Color(0x33FFFFFF), height = 8.dp)
            Spacer(Modifier.height(12.dp))
            com.nesxha.readit.ui.components.Segmented(listOf("Semua", "Sudah Didapat", "Belum"), filter, onBrand = true) { filter = it }
        }
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            val list = Badges.all
                .filter { when (filter) { 1 -> it.id in st.badges; 2 -> it.id !in st.badges; else -> true } }
                .sortedWith(compareBy({ if (it.id in st.badges) 0 else 1 }, { -(st.badges[it.id] ?: 0L) }))
            if (list.isEmpty()) Text(
                if (filter == 1) "Belum ada lencana. Ayo mulai belajar!" else "Semua lencana sudah kamu dapatkan. Hebat!",
                style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth().padding(24.dp), textAlign = TextAlign.Center,
            )
            list.chunked(2).forEachIndexed { r, row ->
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    row.forEachIndexed { c, b ->
                        val day = st.badges[b.id]
                        BadgeCard(b, day?.let { epochDayToDate(it).let { d -> "${d.dayOfMonth} ${monthShort(d.monthNumber)} ${d.year}" } }, b.progress(st), Modifier.weight(1f).appear(r * 2 + c))
                    }
                    if (row.size == 1) Spacer(Modifier.weight(1f))
                }
            }
            Spacer(Modifier.navigationBarsPadding().height(8.dp))
        }
    }
}

/** Kartu lencana: ukuran seragam, judul dan deskripsi dua baris, progres selalu di dasar kartu. */
@Composable
private fun BadgeCard(b: Badge, date: String?, progress: Pair<Int, Int>, modifier: Modifier) {
    val cs = MaterialTheme.colorScheme
    val unlocked = date != null
    val shine = rememberInfiniteTransition(label = "shine")
    val glow by shine.animateFloat(0.6f, 1f, infiniteRepeatable(tween(1400), RepeatMode.Reverse), label = "glow")
    val content: @Composable () -> Unit = {
        Column(Modifier.fillMaxWidth().height(168.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(Modifier.size(58.dp), contentAlignment = Alignment.Center) {
                Box(
                    Modifier.size(58.dp).clip(CircleShape)
                        .background(if (unlocked) Color(0x33FFFFFF) else cs.surfaceVariant)
                        .then(if (unlocked) Modifier.border(2.dp, Brand.gold.copy(alpha = glow), CircleShape) else Modifier),
                    contentAlignment = Alignment.Center,
                ) { Text(b.emoji, fontSize = 28.sp, modifier = Modifier.graphicsLayer { alpha = if (unlocked) 1f else 0.28f }) }
                if (!unlocked) Text("🔒", fontSize = 14.sp, modifier = Modifier.align(Alignment.BottomEnd))
            }
            Spacer(Modifier.height(8.dp))
            Text(b.title, style = MaterialTheme.typography.titleSmall, color = if (unlocked) Color.White else cs.onSurface,
                textAlign = TextAlign.Center, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
            Text(b.desc, style = MaterialTheme.typography.labelSmall, color = if (unlocked) Color.White.copy(alpha = 0.85f) else cs.onSurfaceVariant,
                textAlign = TextAlign.Center, minLines = 2, maxLines = 2, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
            Spacer(Modifier.weight(1f))
            if (unlocked) {
                Text("Didapat $date", style = MaterialTheme.typography.labelSmall, color = Brand.gold, maxLines = 1)
            } else {
                ProgressBar(progress.first.toFloat() / progress.second, height = 6.dp)
                Spacer(Modifier.height(4.dp))
                Text("${minOf(progress.first, progress.second)}/${progress.second}", style = MaterialTheme.typography.labelSmall, color = cs.onSurfaceVariant)
            }
        }
    }
    if (unlocked) BrandCard(modifier, radius = 22.dp, padding = PaddingValues(12.dp)) { content() }
    else SoftCard(modifier, radius = 22.dp, padding = PaddingValues(12.dp)) { content() }
}

/** Dialog perayaan saat lencana baru didapat. */
@Composable
fun NewBadgeDialog(ids: List<String>, onDismiss: () -> Unit) {
    val badges = ids.mapNotNull { Badges.byId[it] }
    if (badges.isEmpty()) { LaunchedEffect(Unit) { onDismiss() }; return }
    val first = badges.first()
    val pop = remember(ids) { Animatable(0.3f) }
    LaunchedEffect(ids) { pop.animateTo(1f, spring(dampingRatio = 0.45f, stiffness = 260f)) }
    val wobble = rememberInfiniteTransition(label = "badge")
    val rot by wobble.animateFloat(-8f, 8f, infiniteRepeatable(tween(900), RepeatMode.Reverse), label = "rot")
    Dialog(onDismissRequest = onDismiss) {
        Box {
            BrandCard(Modifier.fillMaxWidth().graphicsLayer { scaleX = pop.value; scaleY = pop.value }, radius = 32.dp) {
                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Lencana Baru!", style = MaterialTheme.typography.titleMedium, color = Color.White.copy(alpha = 0.9f))
                    Spacer(Modifier.height(12.dp))
                    Box(
                        Modifier.size(96.dp).clip(CircleShape).background(Color(0x33FFFFFF)),
                        contentAlignment = Alignment.Center,
                    ) { Text(first.emoji, fontSize = 50.sp, modifier = Modifier.graphicsLayer { rotationZ = rot }) }
                    Spacer(Modifier.height(12.dp))
                    Text(first.title, style = MaterialTheme.typography.headlineSmall, color = Color.White, textAlign = TextAlign.Center)
                    Text(first.desc, style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.9f), textAlign = TextAlign.Center)
                    if (badges.size > 1) {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Ditambah ${badges.size - 1} lencana lainnya: " + badges.drop(1).joinToString(" ") { it.emoji },
                            style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.9f), textAlign = TextAlign.Center,
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                    Box(
                        Modifier.clip(RoundedCornerShape(50)).background(Color.White).bounceClick(onClick = onDismiss)
                            .padding(horizontal = 28.dp, vertical = 12.dp),
                    ) { Text("Mantap!", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelLarge) }
                }
            }
            Confetti(ids.hashCode().let { if (it == 0) 1 else it })
        }
    }
}
