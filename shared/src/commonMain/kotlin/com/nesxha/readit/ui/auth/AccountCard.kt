package com.nesxha.readit.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.nesxha.readit.auth.Auth
import com.nesxha.readit.auth.Res
import com.nesxha.readit.ui.components.SoftCard
import com.nesxha.readit.ui.components.bounceClick
import com.nesxha.readit.ui.theme.Brand
import kotlinx.coroutines.launch

/** Kartu akun KMC di Pengaturan: nickname, nomor KMC, status koneksi, dan tombol keluar. */
@Composable
fun AccountCard(modifier: Modifier = Modifier) {
    val phase by Auth.phase.collectAsState()
    val p = phase as? Auth.Phase.Ready ?: return
    val scope = rememberCoroutineScope()
    val cs = MaterialTheme.colorScheme
    var ask by remember { mutableStateOf(false) }
    var busy by remember { mutableStateOf(false) }
    val nick = p.session.nickname.ifEmpty { "Anggota KMC" }

    SoftCard(modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(56.dp).clip(CircleShape).background(Brush.linearGradient(listOf(Brand.blueDeep, Brand.blueLight))),
                contentAlignment = Alignment.Center,
            ) { Text(nick.take(1).uppercase(), style = MaterialTheme.typography.headlineSmall, color = Color.White) }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(nick, style = MaterialTheme.typography.titleLarge, color = cs.onSurface, maxLines = 1)
                com.nesxha.readit.ui.member.MaskedKmc(p.session.kmc, MaterialTheme.typography.bodyMedium, cs.onSurfaceVariant)
            }
        }
        Spacer(Modifier.height(14.dp))
        val (label, color) = when {
            Auth.api.isDemo -> "Mode Demo · Server belum tersambung" to Brand.warning
            p.serverIssue -> "Server sedang bermasalah · Mode Offline" to Brand.warning
            p.offline -> "Mode Offline · Sisa ${p.daysLeft} hari" to Brand.warning
            else -> "Tersambung ke Server" to Brand.success
        }
        Row(
            Modifier.clip(RoundedCornerShape(50)).background(color.copy(alpha = 0.14f)).padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(Modifier.size(8.dp).clip(CircleShape).background(color))
            Spacer(Modifier.width(8.dp))
            Text(label, style = MaterialTheme.typography.labelMedium, color = cs.onSurface)
        }
        Spacer(Modifier.height(14.dp))
        Row(
            Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(Brand.error.copy(alpha = 0.10f))
                .bounceClick(enabled = !busy) { ask = true }.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (busy) CircularProgressIndicator(Modifier.size(20.dp), color = Brand.error, strokeWidth = 2.dp)
            else Icon(Icons.AutoMirrored.Rounded.Logout, null, tint = Brand.error)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text("Keluar dari Akun", style = MaterialTheme.typography.titleSmall, color = Brand.error)
                Text("Wajib online. Setelah keluar, KMC-mu bisa dipakai di perangkat lain.", style = MaterialTheme.typography.bodySmall, color = cs.onSurfaceVariant)
            }
        }
    }

    if (ask) AlertDialog(
        onDismissRequest = { ask = false },
        title = { Text("Keluar dari Akun?") },
        text = { Text("Progres belajar di ${com.nesxha.readit.platform.thisDevice} tetap tersimpan. Untuk memakai aplikasi lagi, kamu perlu masuk dengan nomor KMC dan PIN.") },
        confirmButton = {
            TextButton(onClick = {
                ask = false; busy = true
                scope.launch {
                    val r = Auth.logout()
                    busy = false
                    if (r is Res.Err) com.nesxha.readit.platform.Plat.p.toast(r.error.message)
                }
            }) { Text("Keluar", color = Brand.error) }
        },
        dismissButton = { TextButton(onClick = { ask = false }) { Text("Batal") } },
    )
}
