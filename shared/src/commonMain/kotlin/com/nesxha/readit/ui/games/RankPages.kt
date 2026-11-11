package com.nesxha.readit.ui.games

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.nesxha.readit.data.RankPos
import com.nesxha.readit.data.RankRules
import com.nesxha.readit.ui.components.HeaderButton
import com.nesxha.readit.ui.components.RankEmblem
import com.nesxha.readit.ui.components.ScreenHeader
import com.nesxha.readit.ui.components.SoftCard
import com.nesxha.readit.ui.components.appear
import com.nesxha.readit.ui.components.bounceClick
import com.nesxha.readit.ui.components.c2
import com.nesxha.readit.ui.theme.Brand

@Composable
private fun RuleCard(title: String, modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    SoftCard(modifier.fillMaxWidth()) {
        Text(title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
        Spacer(Modifier.height(8.dp))
        content()
    }
}

/** Baris tabel dua kolom. */
@Composable
private fun RuleRow(left: String, right: String, color: Color? = null) {
    val cs = MaterialTheme.colorScheme
    Row(Modifier.fillMaxWidth().padding(vertical = 5.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(left, style = MaterialTheme.typography.bodyMedium, color = cs.onSurface, modifier = Modifier.weight(1f))
        Text(right, style = MaterialTheme.typography.titleSmall, color = color ?: cs.primary, textAlign = TextAlign.End)
    }
}

@Composable
private fun Note(text: String) {
    Text(text, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
}

private val PLUS = Color(0xFF16A34A)
private val MINUS = Color(0xFFDC2626)

/** Aturan Rank: kenapa RP bertambah atau berkurang. */
@Composable
fun RankRulesScreen(nav: NavHostController) {
    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).verticalScroll(rememberScrollState())) {
        ScreenHeader("Aturan Rank", "Cara RP dihitung dari Kuis Kilat",
            leading = { HeaderButton(Icons.AutoMirrored.Rounded.ArrowBack, "Kembali") { nav.popBackStack() } })
        Column(Modifier.padding(18.dp).navigationBarsPadding(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            RuleCard("⚡ Dari Mana RP Didapat?", Modifier.appear(0)) {
                Note("RP (Rank Point) didapat dari Kuis Kilat saat online. Satu kali main Kilat dihitung untuk RP sekaligus skor papan Kilat Mingguan. " +
                    "Skor dan RP dihitung per jawaban: makin cepat menjawab benar, makin besar. Setiap divisi butuh 100 RP. " +
                    "Dari Iron III sampai Phoenix I ada 24 divisi, lalu Dragon di puncak.")
            }
            RuleCard("1️⃣ Skor dan RP per Jawaban", Modifier.appear(1)) {
                Note("Waktu dihitung sejak soal tampil sampai kamu memilih jawaban.")
                Spacer(Modifier.height(4.dp))
                (0..3).forEach { b ->
                    RuleRow("Benar, ${RankRules.bucketLabels[b].substringAfter("· ")}",
                        "Skor ${RankRules.bucketScore[b]} · " + (if (RankRules.bucketRp[b] > 0) "+${RankRules.bucketRp[b]} RP" else "0 RP"),
                        if (RankRules.bucketRp[b] > 0) PLUS else null)
                }
                RuleRow("Salah", "Skor 0 · −2 RP", MINUS)
                Note("Jadi asal menebak cepat-cepat justru membuat RP berkurang.")
            }
            RuleCard("2️⃣ Pengali Durasi", Modifier.appear(2)) {
                Note("Pilih durasi sebelum mulai. Pengali berlaku untuk skor dan RP plus (RP minus dari jawaban salah tidak dikali).")
                Spacer(Modifier.height(4.dp))
                RankRules.kilatMinutes.forEach { m -> RuleRow("$m Menit", RankRules.multLabel(m), PLUS) }
            }
            RuleCard("3️⃣ Pengali Rank Tinggi", Modifier.appear(3)) {
                Note("Makin tinggi rank, RP plus makin kecil supaya rank puncak tetap istimewa.")
                Spacer(Modifier.height(4.dp))
                RuleRow("Iron sampai Platinum", "×1")
                RuleRow("Diamond dan Obsidian", "×0,75")
                RuleRow("Phoenix dan Dragon", "×0,5")
            }
            RuleCard("📚 Kosakata per Rank", Modifier.appear(4)) {
                RankRules.tiers.forEach { t ->
                    RuleRow("${t.hanzi} ${t.name}", "HSK 1–${RankRules.maxHsk(t.index * RankRules.PER_TIER)}")
                }
                Note("Makin tinggi rank, kosakatanya makin luas.")
            }
            RuleCard("➕ Aturan Tambahan", Modifier.appear(4)) {
                RuleRow("🎁 3 Permainan Pertama Tiap Hari", "RP Plus ×2", PLUS)
                RuleRow("🛡️ 3 Permainan Setelah Naik Divisi", "Tidak Bisa Turun")
                RuleRow("🚪 Keluar di Tengah Permainan", "−10 RP", MINUS)
                RuleRow("📶 Main Saat Offline", "Tidak Dihitung")
                RuleRow("⬇️ Iron III", "Tidak Bisa Turun Lagi")
            }
            RuleCard("🧮 Contoh (Iron, Tanpa Bonus Harian)", Modifier.appear(5)) {
                RuleRow("1 Menit: 20 Cepat, 5 Bagus, 1 Salah · (60 + 10) ×2 − 2", "+138", PLUS)
                RuleRow("2 Menit: 4 Cepat, 4 Bagus, 4 Cukup, 4 Lambat, 2 Salah · 24 ×1,5 − 4", "+32", PLUS)
                RuleRow("3 Menit: 12 Bagus, 3 Salah · 24 ×1 − 6", "+18", PLUS)
                RuleRow("3 Menit asal ketuk: 5 Cepat, 10 Lambat, 15 Salah · 15 − 30", "−15", MINUS)
            }
            RuleCard("🗓️ Musim dan Reset", Modifier.appear(6)) {
                Note("Satu musim berlangsung 3 bulan (Januari, April, Juli, Oktober). Di awal musim baru, rank turun lalu mulai dari divisi III dengan 0 RP:")
                Spacer(Modifier.height(4.dp))
                RuleRow("Dragon, Phoenix, Obsidian", "→ Platinum")
                RuleRow("Diamond, Platinum", "→ Gold")
                RuleRow("Gold", "→ Silver")
                RuleRow("Silver, Bronze, Iron", "Tetap")
                Note("Rank tertinggimu di setiap musim disimpan di Arena.")
            }
        }
    }
}

/** Daftar Rank: katalog semua rank dari Dragon sampai Iron. Ketuk untuk melihat Divisi III, II, I. */
@Composable
fun RankListScreen(nav: NavHostController) {
    var open by remember { mutableStateOf<Int?>(null) }
    val cs = MaterialTheme.colorScheme
    val myTier = RankRules.pos(RankCache.p).tier.index
    Column(Modifier.fillMaxSize().background(cs.background).verticalScroll(rememberScrollState())) {
        ScreenHeader("Daftar Rank", "9 rank dari Iron sampai Dragon",
            leading = { HeaderButton(Icons.AutoMirrored.Rounded.ArrowBack, "Kembali") { nav.popBackStack() } })
        Column(Modifier.padding(18.dp).navigationBarsPadding(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            RankRules.tiers.reversed().forEachIndexed { i, t ->
                val start = t.index * RankRules.PER_TIER
                val top: RankPos = if (t.index == RankRules.DRAGON) RankRules.pos(start) else RankRules.pos(start + 200)
                val range = if (t.index == RankRules.DRAGON) "2.400 RP ke Atas" else "${com.nesxha.readit.platform.thousands(start)}–${com.nesxha.readit.platform.thousands(start + 299)} RP"
                val effect = when (t.index) { in 0..3 -> null; 4, 5 -> "✨ Kilau"; else -> "🔥 Animasi Penuh" }
                SoftCard(Modifier.fillMaxWidth().appear(i).then(if (t.index == myTier) Modifier.border(2.dp, Brand.gold, RoundedCornerShape(24.dp)) else Modifier),
                    onClick = { open = if (open == t.index) null else t.index }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RankEmblem(top, 72.dp)
                        Spacer(Modifier.width(14.dp))
                        Column(Modifier.weight(1f)) {
                            Text(t.name, style = MaterialTheme.typography.titleLarge, color = cs.onSurface)
                            Text("${t.hanzi} · ${t.pinyin}", style = MaterialTheme.typography.bodyMedium, color = t.c2(), fontWeight = FontWeight.SemiBold)
                            Text(range, style = MaterialTheme.typography.labelMedium, color = cs.onSurfaceVariant)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            if (t.index == myTier) Text("📍 Rank-mu", style = MaterialTheme.typography.labelMedium, color = Brand.gold)
                            if (effect != null) Text(effect, style = MaterialTheme.typography.labelSmall, color = cs.onSurfaceVariant)
                            Text(if (open == t.index) "▲" else "▼", style = MaterialTheme.typography.labelLarge, color = cs.primary)
                        }
                    }
                    AnimatedVisibility(open == t.index, enter = expandVertically() + fadeIn()) {
                        Column(Modifier.padding(top = 12.dp)) {
                            if (t.index == RankRules.DRAGON) {
                                Note("Dragon tidak punya divisi. Pemain Dragon diurutkan berdasarkan jumlah RP di leaderboard Rank.")
                            } else {
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                                    listOf(0, 100, 200).forEach { off ->
                                        val d = RankRules.pos(start + off)
                                        Column(horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier.clip(RoundedCornerShape(16.dp)).bounceClick { }.padding(6.dp)) {
                                            RankEmblem(d, 76.dp)
                                            Text(d.label, style = MaterialTheme.typography.labelLarge, color = cs.onSurface)
                                            Text("${start + off} RP", style = MaterialTheme.typography.labelSmall, color = cs.onSurfaceVariant)
                                        }
                                    }
                                }
                                Spacer(Modifier.height(6.dp))
                                Note("Divisi III ★ polos, Divisi II ★★ bingkai ganda, Divisi I ★★★ dengan sayap.")
                            }
                        }
                    }
                }
            }
        }
    }
}
