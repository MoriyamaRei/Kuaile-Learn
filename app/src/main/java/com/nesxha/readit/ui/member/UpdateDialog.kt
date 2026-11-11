package com.nesxha.readit.ui.member

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.nesxha.readit.BuildConfig
import com.nesxha.readit.auth.Auth
import com.nesxha.readit.util.Updater
import kotlinx.coroutines.launch

private var checkedThisRun = false

/** Mengecek versi baru sekali setiap aplikasi dibuka; update wajib tidak bisa ditutup. */
@Composable
fun UpdateCheck() {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    var rel by remember { mutableStateOf<com.nesxha.readit.util.Releases.Release?>(null) }
    var progress by remember { mutableStateOf<Float?>(null) }
    LaunchedEffect(Unit) {
        if (checkedThisRun || !Auth.hasInternet()) return@LaunchedEffect
        checkedThisRun = true
        rel = Updater.newer()
    }
    val r = rel ?: return
    AlertDialog(
        onDismissRequest = { if (!r.mandatory && progress == null) rel = null },
        properties = DialogProperties(dismissOnBackPress = !r.mandatory, dismissOnClickOutside = !r.mandatory),
        title = { Text(if (r.mandatory) "Pembaruan Wajib" else "Pembaruan Tersedia") },
        text = {
            Column {
                Text("Versi ${r.version} sudah tersedia (versimu ${BuildConfig.VERSION_NAME}).", style = MaterialTheme.typography.bodyMedium)
                if (r.mandatory) Text("Pembaruan ini wajib dipasang untuk lanjut memakai aplikasi.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                if (r.notes.isNotBlank()) {
                    Spacer(Modifier.height(10.dp))
                    Text("Yang Baru", style = MaterialTheme.typography.titleSmall)
                    Text(r.notes.lines().filterNot { it.startsWith("Co-Authored-By") || it.startsWith("Claude-Session") }.joinToString("\n").trim(),
                        style = MaterialTheme.typography.bodySmall, modifier = Modifier.heightIn(max = 240.dp).verticalScroll(rememberScrollState()))
                }
                progress?.let {
                    Spacer(Modifier.height(12.dp))
                    Text("Mengunduh… ${(it * 100).toInt()}%", style = MaterialTheme.typography.labelMedium)
                    LinearProgressIndicator(progress = { it }, modifier = Modifier.fillMaxWidth())
                }
            }
        },
        confirmButton = {
            TextButton(enabled = progress == null || progress == 1f, onClick = {
                if (!Updater.canInstall(ctx)) {
                    Toast.makeText(ctx, "Izinkan \"Pasang aplikasi tidak dikenal\" untuk Kuaile Learn, lalu tekan Perbarui lagi.", Toast.LENGTH_LONG).show()
                    Updater.openInstallSettings(ctx); return@TextButton
                }
                progress = 0f
                scope.launch {
                    val ok = Updater.downloadAndInstall(ctx, r) { progress = it }
                    if (!ok) { progress = null; Toast.makeText(ctx, "Unduhan gagal. Periksa internet, lalu coba lagi.", Toast.LENGTH_LONG).show() }
                }
            }) { Text(if (progress == 1f) "Pasang" else "Perbarui") }
        },
        dismissButton = { if (!r.mandatory) TextButton(enabled = progress == null, onClick = { rel = null }) { Text("Nanti") } },
    )
}
