package com.nesxha.readit.ui.member

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AdminPanelSettings
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Key
import androidx.compose.material.icons.rounded.PhotoCamera
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.nesxha.readit.auth.Me
import com.nesxha.readit.auth.Member
import com.nesxha.readit.auth.Nickname
import com.nesxha.readit.auth.Profile
import com.nesxha.readit.auth.Res
import com.nesxha.readit.auth.AuthError
import com.nesxha.readit.ui.auth.AccountCard
import com.nesxha.readit.ui.components.ActionRow
import com.nesxha.readit.ui.components.HeaderButton
import com.nesxha.readit.ui.components.PrimaryButton
import com.nesxha.readit.ui.components.ScreenHeader
import com.nesxha.readit.ui.components.SoftCard
import com.nesxha.readit.ui.components.appear
import com.nesxha.readit.ui.components.bounceClick
import com.nesxha.readit.ui.theme.Brand
import com.nesxha.readit.util.Photos
import kotlinx.coroutines.launch
import com.nesxha.readit.platform.nowMs
import com.nesxha.readit.platform.Plat
import com.nesxha.readit.platform.witaDate

private val MONTHS = listOf("Januari", "Februari", "Maret", "April", "Mei", "Juni", "Juli", "Agustus", "September", "Oktober", "November", "Desember")

private fun fmtDate(ms: Long): String {
    val d = witaDate(ms)
    return "${d.dayOfMonth} ${MONTHS[d.monthNumber - 1]} ${d.year}"
}

private fun fmtMonth(ms: Long): String {
    val d = witaDate(ms)
    return "${MONTHS[d.monthNumber - 1]} ${d.year}"
}

@Composable
private fun StatTile(emoji: String, value: String, label: String, modifier: Modifier) {
    SoftCard(modifier, padding = androidx.compose.foundation.layout.PaddingValues(14.dp)) {
        Text(emoji, fontSize = 24.sp)
        Spacer(Modifier.height(4.dp))
        Text(value, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface, maxLines = 1)
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
    }
}

@Composable
private fun Notice(title: String, text: String, action: String? = null, onAction: () -> Unit = {}) {
    SoftCard(Modifier.fillMaxWidth()) {
        Text(title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
        Spacer(Modifier.height(4.dp))
        Text(text, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        if (action != null) { Spacer(Modifier.height(14.dp)); PrimaryButton(action, onClick = onAction) }
    }
}

// ---------------- Profil anggota lain ----------------

@Composable
fun MemberProfileScreen(nav: NavHostController, pid: String) {
    var p by remember { mutableStateOf<Profile?>(null) }
    var err by remember { mutableStateOf<String?>(null) }
    var reload by remember { mutableIntStateOf(0) }
    LaunchedEffect(pid, reload) {
        err = null
        when (val r = Member.profile(pid)) { is Res.Ok -> p = r.value; is Res.Err -> err = r.error.message }
    }
    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).verticalScroll(rememberScrollState())) {
        ScreenHeader("Profil Anggota", null, leading = { HeaderButton(Icons.AutoMirrored.Rounded.ArrowBack, "Kembali") { nav.popBackStack() } }) {
            val pr = p
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                com.nesxha.readit.ui.components.RankFrame(pr?.let { com.nesxha.readit.data.RankRules.pos(it.rankP) }, 150.dp, modifier = Modifier.appear(0)) {
                    Avatar(pid, pr?.photoVer ?: 0, pr?.nickname ?: "", 116.dp, full = true)
                }
                Spacer(Modifier.height(8.dp))
                Text(pr?.nickname ?: "…", style = MaterialTheme.typography.headlineSmall, color = Color.White)
                if (pr?.title != null) Text(pr.title, style = MaterialTheme.typography.labelLarge, color = Brand.gold)
                if (pr?.kmc != null) {
                    Spacer(Modifier.height(6.dp))
                    Text(pr.kmc, style = MaterialTheme.typography.labelLarge, color = Color.White,
                        modifier = Modifier.clip(RoundedCornerShape(50)).background(Color(0x33FFFFFF)).padding(horizontal = 12.dp, vertical = 5.dp))
                }
            }
        }
        Column(Modifier.padding(18.dp).navigationBarsPadding(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            val pr = p
            if (pr == null && err != null) Notice("Profil Belum Bisa Dimuat", err!!, "Coba Lagi") { reload++ }
            else if (pr == null) Box(Modifier.fillMaxWidth().padding(30.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            else {
                val rp = com.nesxha.readit.data.RankRules.pos(pr.rankP)
                SoftCard(Modifier.fillMaxWidth().appear(1)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        com.nesxha.readit.ui.components.RankEmblem(rp, 64.dp)
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text("Rank Musim Ini", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(rp.label, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
                            if (pr.peak > 0) Text("Tertinggi: ${com.nesxha.readit.data.RankRules.pos(pr.peak).label}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    StatTile("⭐", "Lv ${pr.level}", "Level", Modifier.weight(1f).appear(2))
                    StatTile("🔥", "${pr.streak}", "Streak Online", Modifier.weight(1f).appear(2))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    StatTile("📚", "${pr.mastered}", "Kata Hafal", Modifier.weight(1f).appear(3))
                    StatTile("⚡", "${pr.bestKilat}", "Rekor Kilat", Modifier.weight(1f).appear(3))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    StatTile("👑", "${pr.wins}×", "Juara Mingguan", Modifier.weight(1f).appear(4))
                    if (pr.since > 0) StatTile("📅", fmtMonth(pr.since), "Bergabung Sejak", Modifier.weight(1f).appear(4))
                }
                val pins = pr.pins.mapNotNull { com.nesxha.readit.data.Badges.byId[it] }
                if (pins.isNotEmpty()) SoftCard(Modifier.fillMaxWidth().appear(5)) {
                    Text("Pajangan Lencana", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        pins.forEach { b ->
                            Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(b.emoji, fontSize = 30.sp)
                                Text(b.title, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface, maxLines = 2, textAlign = TextAlign.Center)
                            }
                        }
                        repeat(3 - pins.size) { Spacer(Modifier.weight(1f)) }
                    }
                }
            }
        }
    }
}

// ---------------- Profil sendiri ----------------

@Composable
fun AccountScreen(nav: NavHostController) {
    val scope = rememberCoroutineScope()
    val cs = MaterialTheme.colorScheme
    var me by remember { mutableStateOf<Me?>(null) }
    var err by remember { mutableStateOf<String?>(null) }
    var reload by remember { mutableIntStateOf(0) }
    var nick by remember { mutableStateOf("") }
    var saving by remember { mutableStateOf(false) }
    var uploading by remember { mutableStateOf(false) }
    var preview by remember { mutableStateOf<ImageBitmap?>(null) }
    var photoMenu by remember { mutableStateOf(false) }

    LaunchedEffect(reload) {
        err = null
        when (val r = Member.me()) {
            is Res.Ok -> { me = r.value; nick = r.value.nickname }
            is Res.Err -> err = r.error.message
        }
    }

    fun toast(s: String) = Plat.p.toast(s)

    fun pickPhoto() {
        val m = me ?: return
        scope.launch {
            val picked = runCatching { Photos.pick() }
            val p = picked.getOrNull()
            if (picked.isFailure) { toast("Foto tidak bisa dibaca atau lebih dari 1 MB setelah dikecilkan."); return@launch }
            if (p == null) return@launch
            uploading = true
            preview = Photos.previewOf(p)
            when (val r = Member.setPhoto(p.thumb, p.full)) {
                is Res.Ok -> { Photos.remember(m.pid, r.value, p); me = m.copy(photoVer = r.value); toast("Foto profil diperbarui.") }
                is Res.Err -> { preview = null; toast(r.error.message) }
            }
            uploading = false
        }
    }

    Column(Modifier.fillMaxSize().background(cs.background).verticalScroll(rememberScrollState())) {
        ScreenHeader("Akun Saya", "Profil yang dilihat anggota lain", leading = { HeaderButton(Icons.AutoMirrored.Rounded.ArrowBack, "Kembali") { nav.popBackStack() } }) {
            val m = me
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(Modifier.bounceClick(enabled = m != null && !uploading) { photoMenu = true }) {
                    Avatar(m?.pid, m?.photoVer ?: 0, m?.nickname ?: nick, 116.dp, ring = Color.White, override = preview)
                    Box(
                        Modifier.align(Alignment.BottomEnd).size(38.dp).clip(CircleShape).background(Color.White).border(2.dp, Brand.blue, CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (uploading) CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
                        else Icon(Icons.Rounded.PhotoCamera, "Ganti Foto", tint = Brand.blue, modifier = Modifier.size(20.dp))
                    }
                }
                Spacer(Modifier.height(10.dp))
                Text(m?.nickname ?: "…", style = MaterialTheme.typography.headlineSmall, color = Color.White)
                if (m != null) MaskedKmc(m.kmc, MaterialTheme.typography.bodyMedium, Brand.sky)
            }
        }
        Column(Modifier.padding(18.dp).navigationBarsPadding(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            val m = me
            when {
                !Member.available -> Notice("Mode Demo", "Profil, foto, dan leaderboard aktif setelah aplikasi tersambung ke server KMC.")
                m == null && err != null -> Notice("Profil Belum Bisa Dimuat", err!!, "Coba Lagi") { reload++ }
                m == null -> Box(Modifier.fillMaxWidth().padding(30.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                else -> {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        StatTile("🔥", "${m.streak}", "Streak Online", Modifier.weight(1f).appear(0))
                        StatTile("⚡", "${m.bestKilat}", "Rekor Kilat", Modifier.weight(1f).appear(1))
                    }
                    SoftCard(Modifier.fillMaxWidth().appear(2)) {
                        Text("Nickname", style = MaterialTheme.typography.titleMedium, color = cs.onSurface)
                        Spacer(Modifier.height(8.dp))
                        val waiting = m.nickNext > nowMs()
                        val problem = if (nick == m.nickname) null else Nickname.problem(nick)
                        OutlinedTextField(
                            nick, { nick = it.take(Nickname.MAX) }, Modifier.fillMaxWidth(), singleLine = true, enabled = !waiting && !saving,
                            isError = problem != null, supportingText = {
                                Text(problem ?: if (waiting) "Bisa diganti lagi mulai ${fmtDate(m.nickNext)}." else "1–16 karakter. Bisa diganti seminggu sekali.")
                            },
                            trailingIcon = { Text("${Nickname.clean(nick).length}/${Nickname.MAX}", style = MaterialTheme.typography.labelSmall, color = cs.onSurfaceVariant) },
                            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                        )
                        Spacer(Modifier.height(6.dp))
                        PrimaryButton(if (saving) "Menyimpan…" else "Simpan Nickname", enabled = !waiting && !saving && problem == null && Nickname.clean(nick) != m.nickname) {
                            saving = true
                            scope.launch {
                                when (val r = Member.setNickname(nick)) {
                                    is Res.Ok -> { toast("Nickname diperbarui."); reload++ }
                                    is Res.Err -> toast(if (r.error is AuthError.NickWait) "Nickname bisa diganti lagi mulai ${fmtDate((r.error as AuthError.NickWait).until)}." else r.error.message)
                                }
                                saving = false
                            }
                        }
                    }
                    SoftCard(Modifier.fillMaxWidth().appear(3)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text("Tampilkan Nomor KMC", style = MaterialTheme.typography.titleSmall, color = cs.onSurface)
                                Text("Kalau aktif, anggota lain bisa melihat nomor KMC di profilmu. Di leaderboard nomor KMC tidak pernah tampil.",
                                    style = MaterialTheme.typography.bodySmall, color = cs.onSurfaceVariant)
                            }
                            Spacer(Modifier.width(10.dp))
                            Switch(m.showKmc, { v ->
                                me = m.copy(showKmc = v)
                                scope.launch { val r = Member.setShowKmc(v); if (r is Res.Err) { me = m; toast(r.error.message) } }
                            }, colors = SwitchDefaults.colors(checkedTrackColor = cs.primary, checkedThumbColor = Color.White))
                        }
                    }
                    ActionRow(Icons.Rounded.EmojiEvents, "Leaderboard", "Peringkat streak dan Kuis Kilat", Modifier.appear(4)) { nav.navigate("leaderboard") }
                    ActionRow(Icons.Rounded.Key, "Ganti PIN", "Wajib online · butuh PIN lama", Modifier.appear(5)) { nav.navigate("changepin") }
                    if (m.admin) ActionRow(Icons.Rounded.AdminPanelSettings, "Menu Admin", "Kelola anggota KMC", Modifier.appear(6)) { nav.navigate("admin") }
                }
            }
            AccountCard(Modifier.appear(7))
        }
    }

    if (photoMenu) AlertDialog(
        onDismissRequest = { photoMenu = false },
        title = { Text("Foto Profil") },
        text = { Text("Pilih foto dari galeri. Foto akan dipotong kotak dan dikecilkan otomatis (maksimal 1 MB).", textAlign = TextAlign.Start) },
        confirmButton = {
            TextButton(onClick = { photoMenu = false; pickPhoto() }) { Text("Pilih dari Galeri") }
        },
        dismissButton = {
            if ((me?.photoVer ?: 0) > 0) TextButton(onClick = {
                photoMenu = false; uploading = true
                scope.launch {
                    when (val r = Member.setPhoto(null, null)) {
                        is Res.Ok -> { preview = null; me = me?.copy(photoVer = 0); toast("Foto profil dihapus.") }
                        is Res.Err -> toast(r.error.message)
                    }
                    uploading = false
                }
            }) { Text("Hapus Foto", color = Brand.error) }
            else TextButton(onClick = { photoMenu = false }) { Text("Batal") }
        },
    )
}
