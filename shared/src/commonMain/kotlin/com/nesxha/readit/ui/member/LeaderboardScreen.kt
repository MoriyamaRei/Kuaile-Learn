package com.nesxha.readit.ui.member

import androidx.compose.animation.AnimatedContent
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
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.nesxha.readit.auth.Auth
import com.nesxha.readit.auth.Board
import com.nesxha.readit.auth.BoardKind
import com.nesxha.readit.auth.Member
import com.nesxha.readit.auth.RankRow
import com.nesxha.readit.auth.Res
import com.nesxha.readit.data.RankRules
import com.nesxha.readit.ui.components.Confetti
import com.nesxha.readit.ui.components.HeaderButton
import com.nesxha.readit.ui.components.PrimaryButton
import com.nesxha.readit.ui.components.RankEmblem
import com.nesxha.readit.ui.components.RankPill
import com.nesxha.readit.ui.components.ScreenHeader
import com.nesxha.readit.ui.components.Segmented
import com.nesxha.readit.ui.components.SoftCard
import com.nesxha.readit.ui.components.appear
import com.nesxha.readit.ui.components.bounceClick
import com.nesxha.readit.ui.components.c2
import com.nesxha.readit.ui.theme.Brand
import kotlinx.coroutines.delay
import kotlin.math.sin
import com.nesxha.readit.platform.nowMs

private val GOLD = Color(0xFFFBBF24)
private val SILVER = Color(0xFFCBD5E1)
private val BRONZE = Color(0xFFCD7F32)

private fun BoardKind.emoji() = when (this) { BoardKind.STREAK -> "🔥"; BoardKind.KILAT -> "⚡"; BoardKind.RANK -> "🏅" }

private fun valueText(k: BoardKind, v: Int) = when (k) {
    BoardKind.RANK -> RankRules.pos(v).let { if (it.isDragon) "Dragon · ${it.rp} RP" else "${it.label} · ${it.rp} RP" }
    else -> "$v ${k.unit}"
}

private fun countdown(ms: Long): String {
    if (ms <= 0) return "Sebentar Lagi"
    val m = ms / 60000; val d = m / 1440; val h = (m % 1440) / 60; val mm = m % 60
    return when { d > 0 -> "$d Hari $h Jam"; h > 0 -> "$h Jam $mm Menit"; else -> "$mm Menit" }
}

@Composable
fun LeaderboardScreen(nav: NavHostController) {
    var tab by rememberSaveable { mutableIntStateOf(0) }
    val kind = BoardKind.entries[tab]
    val cache = remember { mutableStateMapOf<BoardKind, Board>() }
    var error by remember { mutableStateOf<String?>(null) }
    var reload by remember { mutableIntStateOf(0) }
    var loading by remember { mutableStateOf(false) }

    LaunchedEffect(kind, reload) {
        loading = true; error = null
        when (val r = Member.board(kind)) {
            is Res.Ok -> cache[kind] = r.value
            is Res.Err -> error = r.error.message
        }
        loading = false
    }

    Box(Modifier.fillMaxSize()) {
    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        ScreenHeader(
            "Leaderboard", "Hanya aktivitas saat online yang dihitung",
            leading = { HeaderButton(Icons.AutoMirrored.Rounded.ArrowBack, "Kembali") { nav.popBackStack() } },
            trailing = { HeaderButton(Icons.Rounded.Refresh, "Muat Ulang") { reload++ } },
        ) {
            Segmented(BoardKind.entries.map { "${it.emoji()} ${if (it == BoardKind.STREAK) "Streak" else if (it == BoardKind.KILAT) "Kilat" else "Rank"}" }, tab, onBrand = true) { tab = it }
        }
        val board = cache[kind]
        AnimatedContent(Triple(kind, board, error), label = "board", transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(200)) }) { (k, b, err) ->
            Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp).navigationBarsPadding()) {
                BoardInfo(k, b)
                Spacer(Modifier.height(12.dp))
                when {
                    b == null && err != null -> ErrorCard(err) { reload++ }
                    b == null -> Box(Modifier.fillMaxWidth().padding(top = 80.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                    b.top.isEmpty() -> EmptyBoard(k)
                    else -> {
                        if (err != null) { ErrorCard(err) { reload++ }; Spacer(Modifier.height(12.dp)) }
                        Stage(b.top.take(3), k) { nav.navigate("member/$it") }
                        Spacer(Modifier.height(16.dp))
                        b.top.drop(3).forEachIndexed { i, r -> RankRowView(r, k, false, Modifier.appear(i)) { nav.navigate("member/${r.pid}") }; Spacer(Modifier.height(8.dp)) }
                        Spacer(Modifier.height(8.dp))
                        MyRank(b, k)
                    }
                }
                if (loading && b != null) Box(Modifier.fillMaxWidth().padding(top = 12.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(Modifier.size(22.dp), strokeWidth = 2.dp)
                }
            }
        }
    }
    // Konfeti kalau kamu masuk 3 besar.
    val mine = cache[kind]?.myRank ?: 0
    if (mine in 1..3) Confetti(tab * 10 + mine)
    }
}

/** Keterangan papan dan hitung mundur reset. */
@Composable
private fun BoardInfo(k: BoardKind, b: Board?) {
    var now by remember { mutableLongStateOf(nowMs()) }
    LaunchedEffect(Unit) { while (true) { delay(30_000); now = nowMs() } }
    val cs = MaterialTheme.colorScheme
    val text = when (k) {
        BoardKind.STREAK -> "Hari berturut-turut membuka aplikasi saat online. Putus kalau bolos sehari."
        BoardKind.KILAT -> "Skor Kuis Kilat terbaik minggu ini. Juara 1 dapat lencana Juara Minggu Ini."
        BoardKind.RANK -> "Peringkat dari Kuis Kilat di Arena Rank, dari Dragon sampai Iron."
    }
    Row(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(cs.primaryContainer).padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(k.emoji(), fontSize = 22.sp)
        Spacer(Modifier.width(10.dp))
        Column(Modifier.weight(1f)) {
            Text(k.title, style = MaterialTheme.typography.titleSmall, color = cs.onPrimaryContainer)
            Text(text, style = MaterialTheme.typography.bodySmall, color = cs.onPrimaryContainer.copy(alpha = 0.8f))
            if (b != null && b.resetAt > 0) Text(
                (if (k == BoardKind.KILAT) "⏳ Reset dalam " else "⏳ Musim Berakhir dalam ") + countdown(b.resetAt - now),
                style = MaterialTheme.typography.labelMedium, color = cs.primary,
            )
        }
    }
}

@Composable
private fun ErrorCard(msg: String, onRetry: () -> Unit) {
    SoftCard(Modifier.fillMaxWidth()) {
        Text("Leaderboard Belum Bisa Dimuat", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
        Spacer(Modifier.height(4.dp))
        Text(msg, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        // Keterangan teknis dari server, supaya penyebabnya bisa dicek dari screenshot.
        com.nesxha.readit.auth.SupabaseApi.lastError?.let { d ->
            Spacer(Modifier.height(8.dp))
            Text("Detail: $d", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f))
        }
        Spacer(Modifier.height(14.dp))
        PrimaryButton("Coba Lagi", onClick = onRetry)
    }
}

@Composable
private fun EmptyBoard(k: BoardKind) {
    Column(Modifier.fillMaxWidth().padding(top = 30.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(k.emoji(), fontSize = 56.sp)
        Spacer(Modifier.height(10.dp))
        Text("Belum Ada Peringkat", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
        Text(
            when (k) {
                BoardKind.STREAK -> "Buka aplikasi setiap hari saat online untuk mulai masuk peringkat."
                BoardKind.KILAT -> "Main Kuis Kilat saat online minggu ini, dan skormu muncul di sini."
                BoardKind.RANK -> "Main Kuis Kilat saat online untuk mulai mengumpulkan RP."
            },
            style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center,
        )
    }
}

/** Panggung juara 1–3 dengan efek api (Streak), kilat (Kilat), atau cahaya rank (Rank). */
@Composable
private fun Stage(top: List<RankRow>, k: BoardKind, onTap: (String) -> Unit) {
    val order = listOfNotNull(top.getOrNull(1), top.getOrNull(0), top.getOrNull(2))
    val tr = rememberInfiniteTransition(label = "stage")
    val t by tr.animateFloat(0f, 1f, infiniteRepeatable(tween(2200, easing = LinearEasing)), label = "t")
    Box(Modifier.fillMaxWidth()) {
        // Efek latar panggung.
        Canvas(Modifier.matchParentSize()) {
            val w = size.width; val h = size.height
            when (k) {
                BoardKind.STREAK -> for (i in 0 until 9) {
                    val x = w * (i + 0.5f) / 9; val ph = (t + i * 0.13f) % 1f
                    drawCircle(Color(0xFFF97316).copy(alpha = 0.18f * (1f - ph)), 10f + 18f * ph, Offset(x + sin(ph * 6f) * 8f, h * (1f - ph * 0.8f)))
                }
                BoardKind.KILAT -> if (t in 0.45f..0.52f || t in 0.58f..0.62f) {
                    val x = w * 0.5f
                    drawLine(Color(0xFFFDE047), Offset(x, 0f), Offset(x - 30f, h * 0.3f), 5f)
                    drawLine(Color(0xFFFDE047), Offset(x - 30f, h * 0.3f), Offset(x + 10f, h * 0.32f), 5f)
                    drawLine(Color(0xFFFDE047), Offset(x + 10f, h * 0.32f), Offset(x - 20f, h * 0.6f), 5f)
                }
                BoardKind.RANK -> drawCircle(Brush.radialGradient(listOf(GOLD.copy(alpha = 0.25f), Color.Transparent), Offset(w / 2, h * 0.3f), w * 0.45f), w * 0.45f, Offset(w / 2, h * 0.3f))
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.Bottom) {
            order.forEach { r ->
                val place = top.indexOf(r)
                val h = when (place) { 0 -> 150.dp; 1 -> 118.dp; else -> 96.dp }
                val color = when (place) { 0 -> GOLD; 1 -> SILVER; else -> BRONZE }
                PodiumColumn(r, k, h, color, place, Modifier.weight(1f)) { onTap(r.pid) }
            }
        }
    }
}

@Composable
private fun PodiumColumn(r: RankRow, k: BoardKind, height: Dp, color: Color, place: Int, modifier: Modifier, onTap: () -> Unit) {
    val rise = remember { Animatable(0f) }
    LaunchedEffect(r.pid) { delay(140L * (2 - place)); rise.animateTo(1f, spring(dampingRatio = 0.55f, stiffness = 220f)) }
    val me = Auth.currentSession()?.nickname == r.nickname
    Column(modifier.bounceClick(onClick = onTap), horizontalAlignment = Alignment.CenterHorizontally) {
        if (place == 0) Text("👑", fontSize = 26.sp, modifier = Modifier.graphicsLayer { alpha = rise.value; translationY = (1f - rise.value) * -30f })
        Box(contentAlignment = Alignment.BottomEnd) {
            Avatar(r.pid, r.photoVer, r.nickname, if (place == 0) 74.dp else 60.dp, ring = color,
                modifier = Modifier.graphicsLayer { scaleX = rise.value; scaleY = rise.value })
            if (k == BoardKind.RANK) RankEmblem(RankRules.pos(r.value), 28.dp, animate = false)
        }
        Spacer(Modifier.height(6.dp))
        Text(r.nickname, style = MaterialTheme.typography.titleSmall, color = if (me) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
            maxLines = 1, overflow = TextOverflow.Ellipsis)
        if (r.title != null) Text(r.title, style = MaterialTheme.typography.labelSmall, color = Brand.gold, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Spacer(Modifier.height(6.dp))
        Box(
            Modifier.fillMaxWidth().height(height * rise.value.coerceAtLeast(0.05f))
                .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
                .background(Brush.verticalGradient(listOf(color, color.copy(alpha = 0.3f)))),
            contentAlignment = Alignment.TopCenter,
        ) {
            Column(Modifier.padding(top = 10.dp, start = 4.dp, end = 4.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("${r.rank}", style = MaterialTheme.typography.headlineMedium, color = Color.White)
                Text(if (k == BoardKind.RANK) RankRules.pos(r.value).label else "${r.value} ${k.unit}",
                    style = MaterialTheme.typography.labelMedium, color = Color.White, maxLines = 1, textAlign = TextAlign.Center)
                Trend(r.rank, r.prev, Color.White)
            }
        }
    }
}

/** Panah perubahan peringkat dibanding hari sebelumnya. */
@Composable
private fun Trend(rank: Int, prev: Int?, neutral: Color) {
    when {
        prev == null -> Text("Baru", style = MaterialTheme.typography.labelSmall, color = neutral.copy(alpha = 0.8f))
        prev > rank -> Text("▲${prev - rank}", style = MaterialTheme.typography.labelSmall, color = Color(0xFF16A34A))
        prev < rank -> Text("▼${rank - prev}", style = MaterialTheme.typography.labelSmall, color = Color(0xFFDC2626))
        else -> Text("–", style = MaterialTheme.typography.labelSmall, color = neutral.copy(alpha = 0.7f))
    }
}

@Composable
private fun RankRowView(r: RankRow, k: BoardKind, self: Boolean, modifier: Modifier, onTap: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    val me = self || Auth.currentSession()?.nickname == r.nickname
    Row(
        modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(if (me) cs.primaryContainer else cs.surface)
            .then(if (me) Modifier.border(2.dp, cs.primary, RoundedCornerShape(20.dp)) else Modifier)
            .bounceClick(onClick = onTap).padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.width(36.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("${r.rank}", style = MaterialTheme.typography.titleMedium, color = cs.onSurfaceVariant)
            Trend(r.rank, r.prev, cs.onSurfaceVariant)
        }
        Avatar(r.pid, r.photoVer, r.nickname, 42.dp)
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(r.nickname, style = MaterialTheme.typography.titleSmall, color = cs.onSurface, maxLines = 1, overflow = TextOverflow.Ellipsis)
            if (r.title != null) Text(r.title, style = MaterialTheme.typography.labelSmall, color = Brand.gold, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        if (k == BoardKind.RANK) RankPill(RankRules.pos(r.value))
        else Text("${r.value} ${k.unit}", style = MaterialTheme.typography.titleSmall, color = cs.primary)
    }
}

@Composable
private fun MyRank(b: Board, k: BoardKind) {
    val nick = Auth.currentSession()?.nickname ?: ""
    Row(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(22.dp)).background(Brand.card).padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (k == BoardKind.RANK) { RankEmblem(RankRules.pos(b.myValue), 48.dp); Spacer(Modifier.width(12.dp)) }
        Column(Modifier.weight(1f)) {
            Text("Posisimu · $nick", style = MaterialTheme.typography.labelMedium, color = Brand.sky, maxLines = 1)
            Text(if (b.myRank > 0) "Peringkat ${b.myRank}" else "Belum Masuk Peringkat", style = MaterialTheme.typography.titleLarge, color = Color.White, maxLines = 1)
            if (b.myRank > 0) Trend(b.myRank, b.myPrev, Color.White)
        }
        Text(valueText(k, b.myValue), style = MaterialTheme.typography.titleSmall, color = Color.White, textAlign = TextAlign.End)
    }
}
