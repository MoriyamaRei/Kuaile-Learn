package com.nesxha.readit.ui.member

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Mail
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.nesxha.readit.auth.Auth
import com.nesxha.readit.auth.Me
import com.nesxha.readit.auth.Member
import com.nesxha.readit.auth.Res
import com.nesxha.readit.data.AppState
import com.nesxha.readit.data.Badges
import com.nesxha.readit.data.RankRules
import com.nesxha.readit.data.Store
import com.nesxha.readit.data.today
import com.nesxha.readit.ui.auth.FitText
import com.nesxha.readit.ui.components.Bubbles
import com.nesxha.readit.ui.components.HeaderButton
import com.nesxha.readit.ui.components.RankFrame
import com.nesxha.readit.ui.components.RankPill
import com.nesxha.readit.ui.components.SoftCard
import com.nesxha.readit.ui.components.bounceClick
import com.nesxha.readit.ui.theme.Brand
import kotlinx.coroutines.launch

/** Simpanan lokal profil (gelar dan pajangan) supaya tetap tampil saat offline. */
object ProfilePrefs {
    private val p = com.nesxha.readit.platform.Prefs("profil")
    fun title(): String? = p.getString("title", null)
    fun pins(): List<String> = (p.getString("pins", "") ?: "").split(',').filter { it.isNotBlank() }
    fun save(title: String?, pins: List<String>) {
        p.edit().putString("title", title).putString("pins", pins.joinToString(",")).apply()
    }
    var cachedMe: Me? = null
}

/** Memuat profil sendiri dari server (kalau online) dan menyinkronkan level serta kata hafal. */
@Composable
fun rememberMe(): Me? {
    var me by remember { mutableStateOf(ProfilePrefs.cachedMe) }
    LaunchedEffect(Unit) {
        if (!Member.available) return@LaunchedEffect
        (Member.me() as? Res.Ok)?.value?.let { m ->
            me = m; ProfilePrefs.cachedMe = m
            if (m.title != null || m.pins.isNotEmpty()) ProfilePrefs.save(m.title, m.pins)
        }
        val st = Store.now
        Member.syncStats(st.levelInfo.level, st.masteredCount())
    }
    return me
}

/** Kepala profil: foto dengan bingkai rank dan cincin XP, nickname, gelar, KMC tersamar, dan pil level/rank. */
@Composable
fun ProfileHero(nav: NavHostController, me: Me?, st: AppState, onTitle: () -> Unit) {
    val phase by Auth.phase.collectAsState()
    val session = (phase as? Auth.Phase.Ready)?.session
    val li = st.levelInfo
    val pos = RankRules.pos(me?.rankP ?: 0)
    val title = me?.title ?: ProfilePrefs.title()
    val xp by animateFloatAsState(li.fraction, tween(1200), label = "xp")
    Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(bottomStart = 36.dp, bottomEnd = 36.dp)).background(Brand.header)) {
        Bubbles(Modifier.matchParentSize())
        Column(Modifier.statusBarsPadding().padding(start = 18.dp, end = 14.dp, top = 8.dp, bottom = 22.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("Profil", style = MaterialTheme.typography.headlineMedium, color = Color.White, modifier = Modifier.weight(1f))
                Box {
                    if (com.nesxha.readit.platform.Accounts.ON) HeaderButton(Icons.Rounded.Mail, "Kotak Surat") { nav.navigate("mail") }
                    val unread = me?.unread ?: 0
                    if (unread > 0) Box(
                        Modifier.align(Alignment.TopEnd).size(18.dp).clip(CircleShape).background(Brand.error),
                        contentAlignment = Alignment.Center,
                    ) { Text(if (unread > 9) "9+" else "$unread", color = Color.White, fontSize = 10.sp) }
                }
                Spacer(Modifier.width(8.dp))
                HeaderButton(Icons.Rounded.Settings, "Pengaturan") { nav.navigate("settings") }
            }
            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                RankFrame(if (me != null) pos else null, 122.dp, modifier = Modifier.bounceClick { nav.navigate("account") }) {
                    Avatar(me?.pid, me?.photoVer ?: 0, session?.nickname ?: "?", 92.dp)
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    FitText(session?.nickname?.ifEmpty { "Pelajar" } ?: "Pelajar",
                        MaterialTheme.typography.headlineSmall.copy(color = Color.White), minSize = 16f)
                    if (com.nesxha.readit.platform.Accounts.ON) Text(
                        title ?: "Pilih Gelar ›", style = MaterialTheme.typography.labelLarge,
                        color = if (title != null) Brand.gold else Color.White.copy(alpha = 0.7f), maxLines = 1, overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.clip(RoundedCornerShape(50)).clickable(onClick = onTitle).padding(vertical = 2.dp),
                    )
                    if (session != null) MaskedKmc(session.kmc, MaterialTheme.typography.bodySmall, Color.White.copy(alpha = 0.85f))
                }
            }
            Spacer(Modifier.height(12.dp))
            // Streak, level, dan rank disatukan di dekat foto.
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Row(
                    Modifier.clip(RoundedCornerShape(50)).background(Color(0x33FFFFFF)).padding(start = 8.dp, end = 12.dp, top = 3.dp, bottom = 3.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    com.nesxha.readit.ui.components.Flame(st.streak, st.streakLit, Modifier.size(width = 18.dp, height = 22.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("${st.streak}", style = MaterialTheme.typography.labelLarge, color = Color.White)
                }
                Text("⭐ Lv ${li.level} · ${li.title}", style = MaterialTheme.typography.labelLarge, color = Color.White, maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false).clip(RoundedCornerShape(50)).background(Color(0x33FFFFFF)).padding(horizontal = 12.dp, vertical = 6.dp))
                if (me != null) RankPill(pos, Modifier.bounceClick { nav.navigate("arena") })
            }
            Spacer(Modifier.height(10.dp))
            com.nesxha.readit.ui.components.ProgressBar(xp, color = Brand.gold, track = Color(0x33FFFFFF), height = 8.dp)
            Spacer(Modifier.height(4.dp))
            Row {
                Text("${li.xpInLevel}/${li.xpForNext} XP", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.9f), modifier = Modifier.weight(1f))
                Text("${li.toNext} XP Lagi Menuju Level ${li.level + 1}", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.9f))
            }
        }
    }
}

/** Empat angka utama. */
@Composable
fun QuickStats(me: Me?, st: AppState, modifier: Modifier = Modifier) {
    Row(modifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Quick("📚", "${st.masteredCount()}", "Kata Hafal", Modifier.weight(1f))
        Quick("⚡", "${me?.weekKilat ?: "–"}", "Kilat Minggu Ini", Modifier.weight(1f))
        Quick("👑", "${me?.wins ?: "–"}", "Juara Mingguan", Modifier.weight(1f))
    }
}

@Composable
private fun Quick(emoji: String, value: String, label: String, modifier: Modifier) {
    SoftCard(modifier, radius = 20.dp, padding = androidx.compose.foundation.layout.PaddingValues(10.dp)) {
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(emoji, fontSize = 20.sp)
            Text(value, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface, maxLines = 1)
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2, minLines = 2, textAlign = TextAlign.Center)
        }
    }
}

/** Tiga tombol pintasan: kartu anggota, leaderboard, arena rank. */
@Composable
fun ProfileShortcuts(nav: NavHostController, modifier: Modifier = Modifier) {
    Row(modifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Shortcut("🎴", "Kartu Anggota", Modifier.weight(1f)) { nav.navigate("card") }
        Shortcut("🏆", "Leaderboard", Modifier.weight(1f)) { nav.navigate("leaderboard") }
        Shortcut("🏅", "Arena Rank", Modifier.weight(1f)) { nav.navigate("arena") }
    }
}

@Composable
private fun Shortcut(emoji: String, label: String, modifier: Modifier, onClick: () -> Unit) {
    Column(
        modifier.clip(RoundedCornerShape(20.dp)).background(MaterialTheme.colorScheme.primaryContainer).bounceClick(onClick = onClick).padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(emoji, fontSize = 24.sp)
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onPrimaryContainer, maxLines = 1)
    }
}

/** Pajangan 3 lencana favorit. */
@Composable
fun ShowcaseCard(nav: NavHostController, me: Me?, st: AppState, modifier: Modifier = Modifier) {
    val scope = rememberCoroutineScope()
    var pins by remember(me) { mutableStateOf(me?.pins?.takeIf { it.isNotEmpty() } ?: ProfilePrefs.pins()) }
    var editing by remember { mutableStateOf(false) }
    val earned = Badges.all.filter { it.id in st.badges }
    val shown = pins.mapNotNull { Badges.byId[it] }.filter { it.id in st.badges }.ifEmpty {
        earned.sortedByDescending { st.badges[it.id] ?: 0L }.take(3)
    }
    val cs = MaterialTheme.colorScheme
    SoftCard(modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Pajangan Lencana", style = MaterialTheme.typography.titleMedium, color = cs.onSurface, modifier = Modifier.weight(1f))
            Text("Atur", style = MaterialTheme.typography.labelLarge, color = cs.primary,
                modifier = Modifier.clip(RoundedCornerShape(50)).clickable { editing = true }.padding(horizontal = 10.dp, vertical = 4.dp))
            Text("Semua ›", style = MaterialTheme.typography.labelLarge, color = cs.primary,
                modifier = Modifier.clip(RoundedCornerShape(50)).clickable { nav.navigate("badges") }.padding(horizontal = 10.dp, vertical = 4.dp))
        }
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            for (i in 0 until 3) {
                val b = shown.getOrNull(i)
                Column(
                    Modifier.weight(1f).clip(RoundedCornerShape(18.dp)).background(if (b != null) Brand.gold.copy(alpha = 0.12f) else cs.surfaceVariant)
                        .border(1.5.dp, if (b != null) Brand.gold.copy(alpha = 0.6f) else Color.Transparent, RoundedCornerShape(18.dp)).padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(b?.emoji ?: "➕", fontSize = 30.sp)
                    Text(b?.title ?: "Kosong", style = MaterialTheme.typography.labelSmall, color = cs.onSurface, maxLines = 2, minLines = 2,
                        textAlign = TextAlign.Center, overflow = TextOverflow.Ellipsis)
                }
            }
        }
    }
    if (editing) {
        val chosen = remember { mutableStateListOf<String>().apply { addAll(shown.map { it.id }) } }
        AlertDialog(
            onDismissRequest = { editing = false },
            title = { Text("Pilih Maksimal 3 Lencana") },
            text = {
                Column(Modifier.height(360.dp).verticalScroll(rememberScrollState())) {
                    if (earned.isEmpty()) Text("Belum ada lencana yang didapat.")
                    earned.forEach { b ->
                        val on = b.id in chosen
                        Row(
                            Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).clickable {
                                if (on) chosen.remove(b.id) else if (chosen.size < 3) chosen.add(b.id)
                            }.background(if (on) cs.primaryContainer else Color.Transparent).padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(b.emoji, fontSize = 22.sp); Spacer(Modifier.width(10.dp))
                            Text(b.title, style = MaterialTheme.typography.titleSmall, modifier = Modifier.weight(1f))
                            if (on) Text("✓", color = cs.primary)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    editing = false; pins = chosen.toList()
                    ProfilePrefs.save(me?.title ?: ProfilePrefs.title(), pins)
                    scope.launch { Member.setStyle(me?.title ?: ProfilePrefs.title(), pins) }
                }) { Text("Simpan") }
            },
            dismissButton = { TextButton(onClick = { editing = false }) { Text("Batal") } },
        )
    }
}

/** Dialog memilih gelar dari lencana yang sudah didapat. */
@Composable
fun TitlePicker(me: Me?, st: AppState, onDone: () -> Unit) {
    val scope = rememberCoroutineScope()
    val earned = Badges.all.filter { it.id in st.badges }
    val options = earned.map { it.title } + listOfNotNull(if ((me?.wins ?: 0) > 0) "Juara Kilat Mingguan" else null)
    val cs = MaterialTheme.colorScheme
    AlertDialog(
        onDismissRequest = onDone,
        title = { Text("Pilih Gelar") },
        text = {
            Column(Modifier.height(360.dp).verticalScroll(rememberScrollState())) {
                Text("Gelar tampil di bawah nickname, juga di leaderboard. Gelar baru terbuka dari lencana yang kamu dapat.",
                    style = MaterialTheme.typography.bodySmall, color = cs.onSurfaceVariant)
                Spacer(Modifier.height(8.dp))
                (listOf("Tanpa Gelar") + options).forEach { t ->
                    Text(t, style = MaterialTheme.typography.titleSmall, color = cs.onSurface,
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).clickable {
                            val v = if (t == "Tanpa Gelar") null else t
                            val pins = me?.pins ?: ProfilePrefs.pins()
                            ProfilePrefs.save(v, pins)
                            ProfilePrefs.cachedMe = ProfilePrefs.cachedMe?.copy(title = v)
                            scope.launch { Member.setStyle(v, pins) }
                            onDone()
                        }.padding(vertical = 12.dp, horizontal = 6.dp))
                }
            }
        },
        confirmButton = { TextButton(onClick = onDone) { Text("Tutup") } },
    )
}

/** Kalender aktivitas 12 minggu terakhir: kotak menyala sesuai banyaknya belajar hari itu. */
@Composable
fun ActivityCalendar(st: AppState, modifier: Modifier = Modifier) {
    val cs = MaterialTheme.colorScheme
    val t = today()
    val weeks = 12
    // Kolom = minggu (Senin–Minggu), baris = hari.
    val dow = com.nesxha.readit.platform.epochDayToDate(t).dayOfWeek.ordinal
    val start = t - dow - (weeks - 1) * 7
    val activeDays = (0 until weeks * 7).count { st.days[start + it]?.let { d -> d.opened || d.total > 0 } == true && start + it <= t }
    SoftCard(modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Kalender Aktivitas", style = MaterialTheme.typography.titleMedium, color = cs.onSurface, modifier = Modifier.weight(1f))
            Text("$activeDays Hari Aktif", style = MaterialTheme.typography.labelMedium, color = cs.onSurfaceVariant)
        }
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            for (w in 0 until weeks) {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    for (d in 0 until 7) {
                        val day = start + w * 7 + d
                        val log = st.days[day]
                        val n = log?.total ?: 0
                        val c = when {
                            day > t -> Color.Transparent
                            n >= 40 -> cs.primary
                            n >= 15 -> cs.primary.copy(alpha = 0.7f)
                            n > 0 -> cs.primary.copy(alpha = 0.45f)
                            log?.opened == true -> cs.primary.copy(alpha = 0.22f)
                            else -> cs.surfaceVariant
                        }
                        Box(Modifier.fillMaxWidth().aspectRatio(1f).clip(RoundedCornerShape(3.dp)).background(c)
                            .then(if (day == t) Modifier.border(1.5.dp, Brand.gold, RoundedCornerShape(3.dp)) else Modifier))
                    }
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Sedikit", style = MaterialTheme.typography.labelSmall, color = cs.onSurfaceVariant)
            listOf(0.22f, 0.45f, 0.7f, 1f).forEach { a ->
                Spacer(Modifier.width(4.dp)); Box(Modifier.size(10.dp).clip(RoundedCornerShape(2.dp)).background(cs.primary.copy(alpha = a)))
            }
            Spacer(Modifier.width(4.dp))
            Text("Banyak", style = MaterialTheme.typography.labelSmall, color = cs.onSurfaceVariant)
        }
    }
}
