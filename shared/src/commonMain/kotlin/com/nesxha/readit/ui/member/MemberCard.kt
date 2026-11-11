package com.nesxha.readit.ui.member

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.nesxha.readit.auth.Auth
import com.nesxha.readit.auth.Me
import com.nesxha.readit.auth.Member
import com.nesxha.readit.auth.Res
import com.nesxha.readit.ui.components.SoftCard
import com.nesxha.readit.ui.components.bounceClick

/** Kartu akun di tab Profil: foto, nickname, dan pintasan ke Akun Saya dan Leaderboard. */
@Composable
fun MemberCard(nav: NavHostController, modifier: Modifier = Modifier) {
    val phase by Auth.phase.collectAsState()
    val p = phase as? Auth.Phase.Ready ?: return
    var me by remember { mutableStateOf<Me?>(null) }
    LaunchedEffect(p.offline) { if (Member.available && !p.offline) (Member.me() as? Res.Ok)?.let { me = it.value } }
    val cs = MaterialTheme.colorScheme
    SoftCard(modifier.fillMaxWidth(), onClick = { nav.navigate("account") }) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Avatar(me?.pid, me?.photoVer ?: 0, p.session.nickname, 56.dp)
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(p.session.nickname.ifEmpty { "Anggota KMC" }, style = MaterialTheme.typography.titleLarge, color = cs.onSurface, maxLines = 1)
                MaskedKmc(p.session.kmc, MaterialTheme.typography.bodyMedium, cs.onSurfaceVariant)
            }
            Text("Akun Saya", style = MaterialTheme.typography.labelLarge, color = cs.primary)
        }
        Spacer(Modifier.padding(top = 12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Chip("🏆 Leaderboard", Modifier.weight(1f)) { nav.navigate("leaderboard") }
            Chip("🔥 ${me?.streak ?: "–"} · ⚡ ${me?.bestKilat ?: "–"}", Modifier.weight(1f)) { nav.navigate("account") }
        }
    }
}

@Composable
private fun Chip(text: String, modifier: Modifier, onClick: () -> Unit) {
    Text(
        text, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onPrimaryContainer, maxLines = 1,
        modifier = modifier.clip(RoundedCornerShape(50)).background(MaterialTheme.colorScheme.primaryContainer)
            .bounceClick(onClick = onClick).padding(horizontal = 14.dp, vertical = 10.dp),
    )
}
