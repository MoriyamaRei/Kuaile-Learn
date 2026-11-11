package com.nesxha.readit.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.material.icons.rounded.DeleteForever
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Upload
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.nesxha.readit.data.Store
import com.nesxha.readit.ui.auth.AccountCard
import com.nesxha.readit.ui.components.ActionRow
import com.nesxha.readit.ui.components.HeaderButton
import com.nesxha.readit.ui.components.ScreenHeader
import com.nesxha.readit.ui.components.Segmented
import com.nesxha.readit.ui.components.SoftCard
import com.nesxha.readit.ui.components.appear
import com.nesxha.readit.ui.components.bounceClick
import com.nesxha.readit.ui.theme.Brand
import com.nesxha.readit.util.Tts
import com.nesxha.readit.platform.Plat
import com.nesxha.readit.platform.PlatUi
import com.nesxha.readit.platform.epochDayToDate
import com.nesxha.readit.platform.localEpochDay
import com.nesxha.readit.platform.pad2
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(nav: NavHostController) {
    val st by Store.state.collectAsState()
    val s = st.settings
    val scope = rememberCoroutineScope()
    var slider by remember { mutableFloatStateOf(s.newPerDay.toFloat()) }
    var confirmReset by remember { mutableStateOf(false) }
    val cs = MaterialTheme.colorScheme

    val feat = Plat.p.features
    fun reschedule() { scope.launch { delay(300); PlatUi.ui.scheduleReminder() } }
    fun setReminder(on: Boolean) { Store.settings { it.copy(reminderOn = on) }; reschedule() }
    fun askReminder() = scope.launch {
        val granted = PlatUi.ui.requestNotificationPermission()
        setReminder(granted)
        if (!granted) Plat.p.toast("Izin notifikasi ditolak, pengingat tidak bisa tampil.")
    }
    fun backup() = scope.launch {
        val ok = runCatching { Plat.p.saveTextFile("kuaile-learn-cadangan-${epochDayToDate(localEpochDay())}.json", "application/json", Store.exportJson()) }.getOrDefault(false)
        Plat.p.toast(if (ok) "Progres berhasil dicadangkan." else "Gagal menyimpan cadangan.")
    }
    fun restore() = scope.launch {
        val txt = runCatching { Plat.p.pickTextFile("application/json") }.getOrNull() ?: return@launch
        val ok = Store.importJson(txt)
        if (ok) { slider = Store.now.settings.newPerDay.toFloat(); reschedule() }
        Plat.p.toast(if (ok) "Progres berhasil dipulihkan." else "File cadangan tidak dikenali.")
    }

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        ScreenHeader(
            "Pengaturan", "Atur cara belajarmu",
            leading = { HeaderButton(Icons.AutoMirrored.Rounded.ArrowBack, "Kembali") { nav.popBackStack() } },
        )
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            AccountCard(Modifier.appear(0))
            Group("📚", "Belajar", Modifier.appear(0)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Kata Baru per Hari", style = MaterialTheme.typography.titleSmall, color = cs.onSurface, modifier = Modifier.weight(1f))
                    Text("${slider.toInt()}", style = MaterialTheme.typography.titleLarge, color = cs.primary)
                }
                Slider(
                    value = slider, onValueChange = { slider = it },
                    onValueChangeFinished = { Store.settings { it.copy(newPerDay = slider.toInt()) } },
                    valueRange = 5f..50f, steps = 8,
                    colors = SliderDefaults.colors(thumbColor = cs.primary, activeTrackColor = cs.primary, inactiveTrackColor = cs.surfaceVariant),
                )
                Label("Urutan Kata Baru")
                Segmented(listOf("Sesuai Daftar", "Per Tema"), if (s.byTheme) 1 else 0) { i -> Store.settings { it.copy(byTheme = i == 1) } }
                Spacer(Modifier.height(8.dp))
                Text(
                    "Pinyin dan suara otomatis disembunyikan saat berlatih, dan baru muncul setelah kamu mencoba membaca.",
                    style = MaterialTheme.typography.bodySmall, color = cs.onSurfaceVariant,
                )
            }

            Group("🔊", "Suara", Modifier.appear(1)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Kecepatan Suara", style = MaterialTheme.typography.titleSmall, color = cs.onSurface, modifier = Modifier.weight(1f))
                    Text(
                        "Coba 🔊", style = MaterialTheme.typography.labelLarge, color = cs.primary,
                        modifier = Modifier.clip(RoundedCornerShape(50)).background(cs.primaryContainer)
                            .bounceClick { Tts.speak("你好，我在学习汉语。", s.voiceSpeed) }.padding(horizontal = 12.dp, vertical = 6.dp),
                    )
                }
                Spacer(Modifier.height(8.dp))
                Segmented(listOf("Normal", "Lambat", "Sangat Lambat"), s.voiceSpeed) { i ->
                    Store.settings { it.copy(voiceSpeed = i) }
                    Tts.speak("你好", i)
                }
                Spacer(Modifier.height(6.dp))
                Text("Berlaku untuk semua tombol suara, kartu, kuis, dan contoh kalimat.", style = MaterialTheme.typography.bodySmall, color = cs.onSurfaceVariant)
            }

            Group("🎨", "Tampilan", Modifier.appear(1)) {
                Label("Mode Gelap")
                Segmented(listOf("Ikuti Sistem", "Terang", "Gelap"), s.darkMode) { i -> Store.settings { it.copy(darkMode = i) } }
            }

            if (feat.reminder) Group("⏰", "Pengingat Harian", Modifier.appear(2)) {
                ToggleRow("Aktifkan Pengingat", "Notifikasi untuk belajar setiap hari", s.reminderOn) { v ->
                    if (v) askReminder() else setReminder(false)
                }
                Spacer(Modifier.height(6.dp))
                Row(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(cs.surfaceVariant)
                        .bounceClick {
                            PlatUi.ui.pickTime(s.reminderHour, s.reminderMinute) { h, m ->
                                Store.settings { it.copy(reminderHour = h, reminderMinute = m) }
                                reschedule()
                            }
                        }
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("Jam Pengingat", style = MaterialTheme.typography.titleSmall, color = cs.onSurface, modifier = Modifier.weight(1f))
                    Text("${pad2(s.reminderHour)}.${pad2(s.reminderMinute)}", style = MaterialTheme.typography.titleLarge, color = cs.primary)
                }
            }

            Group("💾", "Data", Modifier.appear(3)) {
                ActionRow(Icons.Rounded.Upload, "Cadangkan Progres", "Simpan progres ke file JSON") {
                    backup()
                }
                Spacer(Modifier.height(10.dp))
                ActionRow(Icons.Rounded.Download, "Pulihkan Progres", "Muat progres dari file cadangan") {
                    restore()
                }
                Spacer(Modifier.height(10.dp))
                ActionRow(Icons.Rounded.DeleteForever, "Atur Ulang Semua Progres", "Mulai lagi dari nol") { confirmReset = true }
            }

            Text(
                if (Plat.p.isWeb) "Kuaile Learn · Kosakata HSK 1–4" else "Kuaile Learn · Kosakata HSK 1–4 · Bisa dipakai tanpa internet",
                Modifier.fillMaxWidth().padding(8.dp), style = MaterialTheme.typography.bodySmall,
                color = cs.onSurfaceVariant, textAlign = TextAlign.Center,
            )
            Spacer(Modifier.navigationBarsPadding())
        }
    }

    if (confirmReset) {
        AlertDialog(
            onDismissRequest = { confirmReset = false },
            title = { Text("Atur Ulang Progres?") },
            text = { Text("Semua progres, streak, dan statistik akunmu akan dihapus di semua perangkat (HP dan web). Pengaturan tetap disimpan. Sebaiknya cadangkan dulu.") },
            confirmButton = {
                TextButton(onClick = { Store.resetAll(); confirmReset = false; Plat.p.toast("Progres sudah diatur ulang.") }) {
                    Text("Hapus", color = Brand.error)
                }
            },
            dismissButton = { TextButton(onClick = { confirmReset = false }) { Text("Batal") } },
        )
    }
}

@Composable
private fun Label(text: String) {
    Text(text, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface)
    Spacer(Modifier.height(8.dp))
}

@Composable
private fun Group(emoji: String, title: String, modifier: Modifier, content: @Composable ColumnScope.() -> Unit) {
    SoftCard(modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(emoji, fontSize = 20.sp)
            Spacer(Modifier.width(8.dp))
            Text(title, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
        }
        Spacer(Modifier.height(14.dp))
        content()
    }
}

@Composable
private fun ToggleRow(title: String, sub: String, value: Boolean, onChange: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth().padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface)
            Text(sub, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(
            checked = value, onCheckedChange = onChange,
            colors = SwitchDefaults.colors(checkedTrackColor = MaterialTheme.colorScheme.primary, checkedThumbColor = androidx.compose.ui.graphics.Color.White),
        )
    }
}
