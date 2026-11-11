package com.nesxha.readit.ui.member

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.DoneAll
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.nesxha.readit.auth.Kmc
import com.nesxha.readit.auth.Mail
import com.nesxha.readit.auth.Member
import com.nesxha.readit.auth.Res
import com.nesxha.readit.ui.components.Chip
import com.nesxha.readit.ui.components.HeaderButton
import com.nesxha.readit.ui.components.ScreenHeader
import com.nesxha.readit.ui.components.appear
import com.nesxha.readit.ui.components.bounceClick
import com.nesxha.readit.ui.theme.Brand
import com.nesxha.readit.util.Releases
import com.nesxha.readit.platform.Plat
import com.nesxha.readit.platform.witaDate
import kotlinx.coroutines.launch
import com.nesxha.readit.platform.nowMs

private val KINDS = listOf("pengumuman" to "📢 Pengumuman", "acara" to "📅 Acara", "hadiah" to "🏆 Hadiah", "update" to "🆕 Info Update", "akun" to "🔐 Akun")
private fun kindIcon(k: String) = when (k) { "acara" -> "📅"; "hadiah" -> "🏆"; "update" -> "🆕"; "akun" -> "🔐"; else -> "📢" }

private fun ago(ms: Long): String {
    val m = (nowMs() - ms) / 60000
    return when {
        m < 1 -> "Baru Saja"; m < 60 -> "$m Menit Lalu"; m < 1440 -> "${m / 60} Jam Lalu"; m < 43200 -> "${m / 1440} Hari Lalu"
        else -> witaDate(ms).let { "${it.dayOfMonth}/${it.monthNumber}/${it.year}" }
    }
}

private fun cleanNotes(s: String) = s.lines().filterNot { it.startsWith("Co-Authored-By") || it.startsWith("Claude-Session") }.joinToString("\n").trim()

/** Kotak Surat: pengumuman, acara, hadiah, info akun dari server, ditambah Info Update dari rilis aplikasi. */
@Composable
fun MailScreen(nav: NavHostController) {
    val scope = rememberCoroutineScope()
    val cs = MaterialTheme.colorScheme
    var list by remember { mutableStateOf<List<Mail>?>(null) }
    var err by remember { mutableStateOf<String?>(null) }
    var reload by remember { mutableIntStateOf(0) }
    var open by remember { mutableStateOf<Long?>(null) }
    var writing by remember { mutableStateOf(false) }
    var admin by remember { mutableStateOf(ProfilePrefs.cachedMe?.admin == true) }

    LaunchedEffect(reload) {
        err = null
        if (ProfilePrefs.cachedMe == null) (Member.me() as? Res.Ok)?.value?.let { ProfilePrefs.cachedMe = it; admin = it.admin }
        val server = Member.mail()
        val releases = Releases.recent().map {
            Mail(-it.code.toLong(), "update", "Versi ${it.version}" + if (it.code == com.nesxha.readit.AppInfo.VERSION_CODE) (if (Plat.p.isWeb) "" else " (Terpasang)") else "",
                cleanNotes(it.notes), false, it.at, false, true)
        }
        when (server) {
            is Res.Ok -> list = (server.value + releases).sortedWith(compareByDescending<Mail> { it.pinned }.thenByDescending { it.at })
            is Res.Err -> { err = server.error.message; if (releases.isNotEmpty()) list = releases }
        }
        ProfilePrefs.cachedMe = ProfilePrefs.cachedMe?.copy(unread = list?.count { !it.read } ?: 0)
    }

    Column(Modifier.fillMaxSize().background(cs.background)) {
        ScreenHeader(
            "Kotak Surat", list?.let { l -> l.count { !it.read }.let { if (it > 0) "$it surat belum dibaca" else "Semua surat sudah dibaca" } } ?: "Memuat…",
            leading = { HeaderButton(Icons.AutoMirrored.Rounded.ArrowBack, "Kembali") { nav.popBackStack() } },
            trailing = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (admin) HeaderButton(Icons.Rounded.Edit, "Tulis Surat") { writing = true }
                    HeaderButton(Icons.Rounded.DoneAll, "Tandai Semua Dibaca") {
                        scope.launch { Member.mailRead(null); list = list?.map { it.copy(read = true) } }
                    }
                }
            },
        )
        val l = list
        when {
            l == null && err != null -> Text(err!!, Modifier.padding(24.dp), color = cs.onSurfaceVariant)
            l == null -> Box(Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            l.isEmpty() -> Column(Modifier.fillMaxWidth().padding(40.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("📭", fontSize = 56.sp)
                Text("Belum Ada Surat", style = MaterialTheme.typography.titleLarge, color = cs.onSurface)
                Text("Pengumuman dan info dari pengurus KMC akan muncul di sini.", style = MaterialTheme.typography.bodyMedium, color = cs.onSurfaceVariant)
            }
            else -> LazyColumn(
                Modifier.fillMaxSize().navigationBarsPadding(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                if (err != null) item { Text(err!!, style = MaterialTheme.typography.bodySmall, color = cs.error) }
                items(l, key = { it.id }) { m ->
                    MailItem(m, open == m.id, admin, Modifier.appear(0),
                        onTap = {
                            open = if (open == m.id) null else m.id
                            if (!m.read && m.id > 0) {
                                list = list?.map { if (it.id == m.id) it.copy(read = true) else it }
                                scope.launch { Member.mailRead(m.id) }
                            }
                        },
                        onDelete = {
                            scope.launch {
                                when (val r = Member.adminMailDelete(m.id)) { is Res.Ok -> { Plat.p.toast(r.value); reload++ }; is Res.Err -> Plat.p.toast(r.error.message) }
                            }
                        })
                }
            }
        }
    }
    if (writing) ComposeMail(onDone = { sent -> writing = false; if (sent) reload++ })
}

@Composable
private fun MailItem(m: Mail, open: Boolean, admin: Boolean, modifier: Modifier, onTap: () -> Unit, onDelete: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    Column(
        modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(if (m.read) cs.surface else cs.primaryContainer)
            .then(if (m.pinned) Modifier.border(1.5.dp, Brand.gold, RoundedCornerShape(20.dp)) else Modifier)
            .clickable(onClick = onTap).padding(14.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(42.dp).clip(CircleShape).background(cs.background), contentAlignment = Alignment.Center) { Text(kindIcon(m.kind), fontSize = 20.sp) }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text((if (m.pinned) "📌 " else "") + m.title, style = MaterialTheme.typography.titleSmall, color = cs.onSurface,
                    maxLines = if (open) 3 else 1, overflow = TextOverflow.Ellipsis)
                Text(ago(m.at) + if (m.personal) " · Untukmu" else "", style = MaterialTheme.typography.labelSmall, color = cs.onSurfaceVariant)
            }
            if (!m.read) Box(Modifier.size(10.dp).clip(CircleShape).background(Brand.error))
        }
        if (!open && m.body.isNotBlank()) Text(m.body, style = MaterialTheme.typography.bodySmall, color = cs.onSurfaceVariant, maxLines = 1,
            overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(start = 54.dp, top = 4.dp))
        AnimatedVisibility(open, enter = expandVertically() + fadeIn()) {
            Column(Modifier.padding(start = 54.dp, top = 8.dp)) {
                Text(m.body.ifBlank { "(Tanpa Isi)" }, style = MaterialTheme.typography.bodyMedium, color = cs.onSurface)
                if (admin && m.id > 0) Text("Hapus Surat", style = MaterialTheme.typography.labelLarge, color = Brand.error,
                    modifier = Modifier.padding(top = 10.dp).clip(RoundedCornerShape(50)).bounceClick(onClick = onDelete).padding(vertical = 4.dp))
            }
        }
    }
}

/** Admin menulis surat ke semua anggota atau ke satu KMC. */
@Composable
private fun ComposeMail(onDone: (Boolean) -> Unit) {
    val scope = rememberCoroutineScope()
    var kind by remember { mutableStateOf("pengumuman") }
    var title by remember { mutableStateOf("") }
    var body by remember { mutableStateOf("") }
    var target by remember { mutableStateOf("") }
    var pinned by remember { mutableStateOf(false) }
    var busy by remember { mutableStateOf(false) }
    val kmc = if (target.isBlank()) null else Kmc.normalize(target)
    AlertDialog(
        onDismissRequest = { if (!busy) onDone(false) },
        title = { Text("Tulis Surat") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    KINDS.filter { it.first != "update" }.forEach { (k, label) -> Chip(label, kind == k) { kind = k } }
                }
                OutlinedTextField(title, { title = it.take(120) }, label = { Text("Judul") }, singleLine = true)
                OutlinedTextField(body, { body = it.take(4000) }, label = { Text("Isi Surat") }, minLines = 4, maxLines = 8)
                OutlinedTextField(target, { target = it }, label = { Text("Untuk KMC (Kosongkan untuk Semua Anggota)") }, singleLine = true,
                    isError = target.isNotBlank() && kmc == null, supportingText = { Text(if (target.isBlank()) "Dikirim ke semua anggota" else kmc ?: "Format KMC belum benar") })
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Sematkan di Atas", style = MaterialTheme.typography.titleSmall, modifier = Modifier.weight(1f))
                    Switch(pinned, { pinned = it })
                }
            }
        },
        confirmButton = {
            TextButton(enabled = !busy && title.isNotBlank() && (target.isBlank() || kmc != null), onClick = {
                busy = true
                scope.launch {
                    when (val r = Member.adminMail(kind, title.trim(), body.trim(), kmc, pinned)) {
                        is Res.Ok -> { Plat.p.toast(r.value); onDone(true) }
                        is Res.Err -> Plat.p.toast(r.error.message)
                    }
                    busy = false
                }
            }) { Text(if (busy) "Mengirim…" else "Kirim") }
        },
        dismissButton = { TextButton(enabled = !busy, onClick = { onDone(false) }) { Text("Batal") } },
    )
}
