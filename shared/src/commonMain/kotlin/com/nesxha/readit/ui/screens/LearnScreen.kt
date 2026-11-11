package com.nesxha.readit.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.Style
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.nesxha.readit.data.Counter
import com.nesxha.readit.data.NumCat
import com.nesxha.readit.data.Store
import com.nesxha.readit.ui.components.BrandCard
import com.nesxha.readit.ui.components.Pill
import com.nesxha.readit.ui.components.ProgressBar
import com.nesxha.readit.ui.components.ScreenHeader
import com.nesxha.readit.ui.components.SectionTitle
import com.nesxha.readit.ui.components.SoftCard
import com.nesxha.readit.ui.components.appear
import com.nesxha.readit.ui.theme.Brand

@Composable
fun LearnScreen(nav: NavHostController) {
    val st by Store.state.collectAsState()
    val cs = MaterialTheme.colorScheme
    val fresh = st.newWordsToday().size
    val due = st.dueWords().size

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        ScreenHeader("Belajar", "Kenali kata baru dan pelajari materinya")
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            BrandCard(Modifier.fillMaxWidth().appear(0), onClick = { nav.navigate("flash") }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(52.dp).clip(RoundedCornerShape(16.dp)).background(Color(0x33FFFFFF)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Rounded.Style, null, tint = Color.White)
                    }
                    Spacer(Modifier.width(14.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Flashcard Hari Ini", style = MaterialTheme.typography.titleLarge, color = Color.White)
                        Text(
                            if (fresh + due > 0) "$fresh kata baru · $due ulangan" else "Semua kartu hari ini beres",
                            style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.9f),
                        )
                    }
                    Text("›", style = MaterialTheme.typography.headlineLarge, color = Color.White)
                }
            }

            SectionTitle("Materi", Modifier.appear(1))
            val read = NumCat.entries.count { it != NumCat.CAMPUR && st.counter(Counter.lessonRead(it)) > 0 }
            SoftCard(Modifier.fillMaxWidth().appear(2), onClick = { nav.navigate("lesson/${NumCat.UMUM.name}") }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(48.dp).clip(RoundedCornerShape(16.dp)).background(cs.primaryContainer), contentAlignment = Alignment.Center) {
                        Icon(Icons.AutoMirrored.Rounded.MenuBook, null, tint = cs.primary)
                    }
                    Spacer(Modifier.width(14.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Belajar Angka", style = MaterialTheme.typography.titleMedium, color = cs.onSurface)
                        Text("Rumus, tabel, dan contoh: angka, harga, diskon, jam, tanggal, dan lainnya", style = MaterialTheme.typography.bodySmall, color = cs.onSurfaceVariant, maxLines = 2)
                    }
                    Text("›", style = MaterialTheme.typography.headlineSmall, color = cs.primary)
                }
                Spacer(Modifier.height(12.dp))
                ProgressBar(read / 9f, height = 6.dp)
                Spacer(Modifier.height(4.dp))
                Text("$read dari 9 bab sudah dibaca", style = MaterialTheme.typography.labelSmall, color = cs.onSurfaceVariant)
            }

            SectionTitle("Cara Belajar yang Disarankan", Modifier.appear(3))
            SoftCard(Modifier.fillMaxWidth().appear(4), color = Brand.gold.copy(alpha = 0.10f)) {
                listOf(
                    "Kenali" to "Kata baru muncul lengkap dengan pinyin dan arti di Flashcard.",
                    "Ulangi" to "Flashcard menampilkan hanzi saja. Coba baca dulu, baru balik kartunya.",
                    "Uji" to "Di Latihan, soal hanya berupa hanzi. Bantuan pinyin hanya 3× per latihan.",
                ).forEachIndexed { i, (title, text) ->
                    if (i > 0) Spacer(Modifier.height(10.dp))
                    Row(verticalAlignment = Alignment.Top) {
                        Pill("${i + 1}")
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text(title, style = MaterialTheme.typography.titleSmall, color = cs.onSurface)
                            Text(text, style = MaterialTheme.typography.bodySmall, color = cs.onSurfaceVariant)
                        }
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}
