package com.nesxha.readit.ui.member

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.PersonAdd
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.UploadFile
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
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
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.nesxha.readit.auth.AdminMember
import com.nesxha.readit.auth.Kmc
import com.nesxha.readit.auth.Member
import com.nesxha.readit.auth.MemberCsv
import com.nesxha.readit.auth.Res
import com.nesxha.readit.ui.components.Chip
import com.nesxha.readit.ui.components.HeaderButton
import com.nesxha.readit.ui.components.PrimaryButton
import com.nesxha.readit.ui.components.ScreenHeader
import com.nesxha.readit.ui.components.SoftCard
import com.nesxha.readit.ui.components.bounceClick
import com.nesxha.readit.ui.theme.Brand
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDate
import com.nesxha.readit.platform.Plat
import com.nesxha.readit.platform.epochDayToDate
import com.nesxha.readit.platform.localEpochDay
import com.nesxha.readit.platform.IoDispatcher

private val FILTERS = listOf("Semua", "Aktif", "Terdaftar", "Diblokir", "Terkunci")

private data class Act(val key: String, val label: String, val confirm: String?, val danger: Boolean = false)

private fun actionsFor(m: AdminMember): List<Act> = buildList {
    if (m.status != "diblokir") add(Act("reset_pin", "Reset PIN", "PIN ${m.kmc} akan dihapus dan statusnya kembali Aktif. Anggota perlu Daftar ulang dan membuat PIN baru."))
    if (m.online) add(Act("reset_device", "Reset Perangkat", "HP dan browser yang sedang dipakai ${m.kmc} akan dikeluarkan, supaya anggota bisa masuk di perangkat baru."))
    if (m.lock.isNotEmpty()) add(Act("unlock", "Buka Kunci", null))
    if (m.status == "diblokir") add(Act("unblock", "Buka Blokir", null))
    else add(Act("block", "Blokir", "${m.kmc} tidak akan bisa masuk sampai blokirnya dibuka.", danger = true))
    if (m.photo) add(Act("remove_photo", "Hapus Foto", "Foto profil ${m.kmc} akan dihapus."))
    if (m.nickname != null) add(Act("reset_nick", "Reset Nickname", "Nickname ${m.nickname} akan diganti menjadi nama sementara."))
    add(Act("delete", "Hapus Anggota", "${m.kmc} dan semua datanya (profil, foto, skor) akan dihapus permanen.", danger = true))
}

@Composable
fun AdminScreen(nav: NavHostController) {
    val scope = rememberCoroutineScope()
    val cs = MaterialTheme.colorScheme
    var list by remember { mutableStateOf<List<AdminMember>?>(null) }
    var err by remember { mutableStateOf<String?>(null) }
    var reload by remember { mutableIntStateOf(0) }
    var query by remember { mutableStateOf("") }
    var filter by remember { mutableIntStateOf(0) }
    var selected by remember { mutableStateOf<AdminMember?>(null) }
    var confirm by remember { mutableStateOf<Pair<AdminMember, Act>?>(null) }
    var adding by remember { mutableStateOf(false) }
    var importRows by remember { mutableStateOf<Pair<List<MemberCsv.Row>, List<String>>?>(null) }
    var busy by remember { mutableStateOf<String?>(null) }

    fun toast(s: String) = Plat.p.toast(s)

    LaunchedEffect(reload) {
        err = null
        when (val r = Member.adminList()) { is Res.Ok -> list = r.value; is Res.Err -> err = r.error.message }
    }

    fun doAction(m: AdminMember, a: Act) {
        busy = "${a.label}…"
        scope.launch {
            when (val r = Member.admin(a.key, m.kmc)) { is Res.Ok -> toast(r.value); is Res.Err -> toast(r.error.message) }
            busy = null; reload++
        }
    }

    fun importCsv() = scope.launch {
        val text = runCatching { Plat.p.pickTextFile("text/csv") }.getOrNull() ?: return@launch
        importRows = MemberCsv.parse(text)
    }

    Column(Modifier.fillMaxSize().background(cs.background)) {
        ScreenHeader(
            "Menu Admin", list?.let { "${it.size} anggota" } ?: "Kelola anggota KMC",
            leading = { HeaderButton(Icons.AutoMirrored.Rounded.ArrowBack, "Kembali") { nav.popBackStack() } },
            trailing = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    HeaderButton(Icons.Rounded.UploadFile, "Impor CSV") { importCsv() }
                    HeaderButton(Icons.Rounded.PersonAdd, "Tambah Anggota") { adding = true }
                }
            },
        ) {
            Row(
                Modifier.fillMaxWidth().height(48.dp).clip(RoundedCornerShape(50)).background(Color(0x2EFFFFFF)).padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Rounded.Search, null, tint = Color.White)
                Spacer(Modifier.width(8.dp))
                androidx.compose.foundation.text.BasicTextField(
                    query, { query = it }, singleLine = true, modifier = Modifier.weight(1f),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color.White),
                    cursorBrush = androidx.compose.ui.graphics.SolidColor(Color.White),
                    decorationBox = { inner -> if (query.isEmpty()) Text("Cari KMC, nama, atau nickname", color = Color.White.copy(alpha = 0.7f)); inner() },
                )
            }
        }
        Row(Modifier.horizontalScroll(rememberScrollState()).padding(horizontal = 18.dp, vertical = 12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FILTERS.forEachIndexed { i, f -> Chip(f, filter == i) { filter = i } }
        }
        val all = list
        when {
            all == null && err != null -> Box(Modifier.padding(18.dp)) {
                SoftCard(Modifier.fillMaxWidth()) {
                    Text(err!!, style = MaterialTheme.typography.bodyMedium, color = cs.onSurface)
                    Spacer(Modifier.height(12.dp)); PrimaryButton("Coba Lagi") { reload++ }
                }
            }
            all == null -> Box(Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            else -> {
                val q = query.trim().lowercase()
                val shown = all.filter { m ->
                    (q.isEmpty() || m.kmc.lowercase().contains(q) || m.name.lowercase().contains(q) || (m.nickname ?: "").lowercase().contains(q)) &&
                        when (filter) { 1 -> m.status == "aktif"; 2 -> m.status == "terdaftar"; 3 -> m.status == "diblokir"; 4 -> m.lock.isNotEmpty(); else -> true }
                }
                LazyColumn(Modifier.fillMaxSize().navigationBarsPadding(), contentPadding = androidx.compose.foundation.layout.PaddingValues(18.dp, 0.dp, 18.dp, 24.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (shown.isEmpty()) item { Text("Tidak ada anggota yang cocok.", style = MaterialTheme.typography.bodyMedium, color = cs.onSurfaceVariant, modifier = Modifier.padding(8.dp)) }
                    items(shown, key = { it.kmc }) { m -> MemberRow(m) { selected = m } }
                    item { HelpCard() }
                }
            }
        }
    }

    selected?.let { m ->
        AlertDialog(
            onDismissRequest = { selected = null },
            title = { Text(m.kmc) },
            text = {
                Column {
                    Text(m.name, style = MaterialTheme.typography.titleSmall)
                    Text("Nickname: ${m.nickname ?: "–"} · Status: ${m.status.replaceFirstChar { it.uppercase() }}", style = MaterialTheme.typography.bodySmall)
                    Spacer(Modifier.height(10.dp))
                    actionsFor(m).forEach { a ->
                        Text(
                            a.label, style = MaterialTheme.typography.titleSmall, color = if (a.danger) Brand.error else cs.primary,
                            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).clickable {
                                selected = null
                                if (a.confirm == null) doAction(m, a) else confirm = m to a
                            }.padding(vertical = 12.dp, horizontal = 4.dp),
                        )
                    }
                }
            },
            confirmButton = { TextButton(onClick = { selected = null }) { Text("Tutup") } },
        )
    }

    confirm?.let { (m, a) ->
        AlertDialog(
            onDismissRequest = { confirm = null },
            title = { Text("${a.label}?") },
            text = { Text(a.confirm ?: "") },
            confirmButton = { TextButton(onClick = { confirm = null; doAction(m, a) }) { Text(a.label, color = if (a.danger) Brand.error else cs.primary) } },
            dismissButton = { TextButton(onClick = { confirm = null }) { Text("Batal") } },
        )
    }

    if (adding) AddMemberDialog(onDismiss = { adding = false }) { kmc, name, place, birth ->
        busy = "Menambah anggota…"
        scope.launch {
            when (val r = Member.admin("add", kmc, name, place, birth)) {
                is Res.Ok -> { toast(r.value); adding = false; reload++ }
                is Res.Err -> toast(r.error.message)
            }
            busy = null
        }
    }

    importRows?.let { (rows, bad) ->
        AlertDialog(
            onDismissRequest = { importRows = null },
            title = { Text("Impor Anggota") },
            text = {
                Column {
                    Text("${rows.size} baris siap diimpor sebagai anggota Aktif.")
                    if (bad.isNotEmpty()) {
                        Spacer(Modifier.height(8.dp))
                        Text("${bad.size} baris dilewati karena formatnya salah:", color = Brand.error)
                        bad.take(5).forEach { Text(it, style = MaterialTheme.typography.bodySmall, maxLines = 1, overflow = TextOverflow.Ellipsis) }
                    }
                }
            },
            confirmButton = {
                TextButton(enabled = rows.isNotEmpty(), onClick = {
                    importRows = null
                    scope.launch {
                        var ok = 0; var fail = 0
                        rows.forEachIndexed { i, r ->
                            busy = "Mengimpor ${i + 1} dari ${rows.size}…"
                            if (Member.admin("add", r.kmc, r.name, r.place, r.birth) is Res.Ok) ok++ else fail++
                        }
                        busy = null
                        toast("$ok anggota ditambahkan" + if (fail > 0) ", $fail gagal (mungkin sudah ada)." else ".")
                        reload++
                    }
                }) { Text("Impor") }
            },
            dismissButton = { TextButton(onClick = { importRows = null }) { Text("Batal") } },
        )
    }

    busy?.let { b ->
        AlertDialog(onDismissRequest = {}, confirmButton = {}, text = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                CircularProgressIndicator(Modifier.size(24.dp), strokeWidth = 2.5.dp)
                Spacer(Modifier.width(16.dp)); Text(b)
            }
        })
    }
}

@Composable
private fun MemberRow(m: AdminMember, onClick: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    val (label, color) = when (m.status) {
        "terdaftar" -> "Terdaftar" to Brand.success
        "diblokir" -> "Diblokir" to Brand.error
        else -> "Aktif" to Brand.warning
    }
    Row(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(cs.surface).bounceClick(onClick = onClick).padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(10.dp).clip(CircleShape).background(if (m.online) Brand.success else cs.outline))
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(m.kmc + if (m.admin) " · Admin" else "", style = MaterialTheme.typography.titleSmall, color = cs.onSurface)
            Text(listOfNotNull(m.name.ifEmpty { null }, m.nickname?.let { "@$it" }).joinToString(" · "), style = MaterialTheme.typography.bodySmall,
                color = cs.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        if (m.lock.isNotEmpty()) {
            Text("🔒", modifier = Modifier.padding(end = 6.dp))
        }
        Text(label, style = MaterialTheme.typography.labelMedium, color = color,
            modifier = Modifier.clip(RoundedCornerShape(50)).background(color.copy(alpha = 0.14f)).padding(horizontal = 10.dp, vertical = 4.dp))
    }
}

@Composable
private fun HelpCard() {
    val cs = MaterialTheme.colorScheme
    SoftCard(Modifier.fillMaxWidth().padding(top = 8.dp)) {
        Text("Impor dari Google Sheets", style = MaterialTheme.typography.titleSmall, color = cs.onSurface)
        Spacer(Modifier.height(4.dp))
        Text(
            "Siapkan 4 kolom: KMC, Nama, Tempat Lahir, Tanggal Lahir (contoh: KMC-00001-00, Budi, Batulicin, 17/08/2005). " +
                "Lalu File → Download → CSV, dan pilih file itu lewat tombol impor di atas.",
            style = MaterialTheme.typography.bodySmall, color = cs.onSurfaceVariant,
        )
        Spacer(Modifier.height(8.dp))
        Text("Titik hijau = sedang masuk di HP · 🔒 = terkunci", style = MaterialTheme.typography.bodySmall, color = cs.onSurfaceVariant)
    }
}

private val MONTHS = listOf("Januari", "Februari", "Maret", "April", "Mei", "Juni", "Juli", "Agustus", "September", "Oktober", "November", "Desember")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddMemberDialog(onDismiss: () -> Unit, onSave: (String, String, String, String) -> Unit) {
    var digits by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var place by remember { mutableStateOf("") }
    var birth by remember { mutableStateOf<LocalDate?>(null) }
    var pick by remember { mutableStateOf(false) }
    val kmc = Kmc.normalize(digits)
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tambah Anggota") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(digits, { digits = it.filter(Char::isDigit).take(7) }, label = { Text("Nomor KMC (7 angka)") },
                    supportingText = { Text(kmc ?: "Contoh: 0000100 → KMC-00001-00") }, singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                OutlinedTextField(name, { name = it }, label = { Text("Nama Lengkap") }, singleLine = true,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words))
                OutlinedTextField(place, { place = it }, label = { Text("Tempat Lahir") }, singleLine = true,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words))
                Text(
                    birth?.let { "Tanggal Lahir: ${it.dayOfMonth} ${MONTHS[it.monthNumber - 1]} ${it.year}" } ?: "Pilih Tanggal Lahir",
                    style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).clickable { pick = true }.padding(vertical = 12.dp),
                )
            }
        },
        confirmButton = {
            TextButton(enabled = kmc != null && name.isNotBlank() && place.isNotBlank() && birth != null, onClick = {
                onSave(kmc!!, name.trim(), place.trim(), birth.toString())
            }) { Text("Simpan") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Batal") } },
    )
    if (pick) {
        val st = rememberDatePickerState(
            initialDisplayedMonthMillis = LocalDate(2005, 1, 1).toEpochDays() * 86_400_000L,
            yearRange = 1940..epochDayToDate(localEpochDay()).year,
        )
        DatePickerDialog(
            onDismissRequest = { pick = false },
            confirmButton = { TextButton(onClick = { st.selectedDateMillis?.let { birth = LocalDate.fromEpochDays(it.floorDiv(86_400_000L).toInt()) }; pick = false }) { Text("Pilih") } },
            dismissButton = { TextButton(onClick = { pick = false }) { Text("Batal") } },
        ) { DatePicker(st) }
    }
}
