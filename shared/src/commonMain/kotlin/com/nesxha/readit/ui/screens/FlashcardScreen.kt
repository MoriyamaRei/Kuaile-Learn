package com.nesxha.readit.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material.icons.rounded.Quiz
import androidx.compose.material.icons.rounded.Repeat
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.nesxha.readit.data.Grade
import com.nesxha.readit.data.Store
import com.nesxha.readit.data.Word
import com.nesxha.readit.data.Words
import com.nesxha.readit.data.posName
import com.nesxha.readit.data.today
import com.nesxha.readit.go
import com.nesxha.readit.ui.components.ActionRow
import com.nesxha.readit.ui.components.Confetti
import com.nesxha.readit.ui.components.EmptyState
import com.nesxha.readit.ui.components.ExitDialog
import com.nesxha.readit.ui.components.HanziText
import com.nesxha.readit.ui.components.HeaderButton
import com.nesxha.readit.ui.components.LevelBadge
import com.nesxha.readit.ui.components.Pill
import com.nesxha.readit.ui.components.PinyinText
import com.nesxha.readit.ui.components.ProgressBar
import com.nesxha.readit.ui.components.ScreenHeader
import com.nesxha.readit.ui.components.SpeakButton
import com.nesxha.readit.ui.components.XpEarned
import com.nesxha.readit.ui.components.appear
import com.nesxha.readit.ui.components.bounceClick
import com.nesxha.readit.ui.theme.Brand
import com.nesxha.readit.ui.theme.HanziFont
import com.nesxha.readit.util.Tts
import com.nesxha.readit.platform.BackHandler

/** Kartu "Belum Hafal" diulang paling banyak sekali lagi di sesi yang sama, lalu dijadwalkan besok. */
private const val MAX_RETRY = 1

private data class Item(val word: Word, val retry: Int = 0)

private class Session(words: List<Word>, val practice: Boolean) {
    val queue = mutableStateListOf<Item>().apply { addAll(words.map { Item(it) }) }
    /** Jumlah kata unik yang sudah selesai (bukan jumlah kartu yang muncul). */
    var done by mutableIntStateOf(0)
    val total = words.size
    var step by mutableIntStateOf(0)
    val startXp = Store.now.xp
}

private fun dailySession(): Session {
    val st = Store.now
    return Session(st.dueWords() + st.newWordsToday(), practice = false)
}

@Composable
fun FlashcardScreen(nav: NavHostController) {
    val st by Store.state.collectAsState()
    var session by remember { mutableStateOf(dailySession()) }
    var confetti by remember { mutableIntStateOf(0) }
    var askExit by remember { mutableStateOf(false) }
    val s = session
    val inProgress = s.queue.isNotEmpty() && s.done > 0

    BackHandler(enabled = inProgress) { askExit = true }
    if (askExit) ExitDialog("Keluar dari Flashcard?", "Kartu yang sudah kamu jawab tetap tersimpan.", onStay = { askExit = false }) {
        askExit = false; nav.popBackStack()
    }

    Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            ScreenHeader(
                "Flashcard",
                if (s.practice && s.queue.isNotEmpty()) "Latihan bebas · tidak mengubah jadwal" else "Kartu hari ini",
                leading = { HeaderButton(Icons.AutoMirrored.Rounded.ArrowBack, "Kembali") { if (inProgress) askExit = true else nav.popBackStack() } },
                trailing = {
                    if (s.queue.isNotEmpty()) Text("${s.done}/${s.total}", style = MaterialTheme.typography.titleMedium, color = Color.White)
                },
            ) {
                if (s.queue.isNotEmpty()) {
                    ProgressBar(if (s.total == 0) 0f else s.done.toFloat() / s.total, color = Color.White, track = Color(0x33FFFFFF), height = 8.dp)
                }
            }
            if (s.queue.isEmpty()) {
                LaunchedEffect(s) {
                    if (s.done > 0 && (s.practice || Store.celebratedDay != today())) {
                        if (!s.practice) Store.celebratedDay = today()
                        confetti++
                    }
                }
                Finished(
                    hadCards = s.done > 0,
                    xp = st.xp - s.startXp,
                    onMoreNew = {
                        val extra = Words.studyOrder(st.settings.byTheme).filter { !st.card(it.id).seen }.take(5)
                        session = Session(extra, practice = false)
                    },
                    onPractice = {
                        val seen = Words.all.filter { st.card(it.id).seen }.shuffled().take(15)
                        session = Session(seen.ifEmpty { Words.all.filter { it.level == 1 }.shuffled().take(10) }, practice = true)
                    },
                    onQuiz = { nav.go("practice") },
                )
            } else {
                val item = s.queue.first()
                FlipCard(
                    word = item.word, retry = item.retry > 0, key = s.step,
                    onAnswer = { knew ->
                        // Hanya jawaban pertama yang dicatat; ulangan di sesi yang sama sekadar pengingat.
                        if (!s.practice && item.retry == 0) Store.grade(item.word.id, if (knew) Grade.BISA else Grade.LUPA)
                        s.queue.removeAt(0)
                        if (!knew && item.retry < MAX_RETRY) s.queue.add(minOf(3, s.queue.size), item.copy(retry = item.retry + 1))
                        else s.done++
                        s.step++
                    },
                    modifier = Modifier.weight(1f),
                )
            }
        }
        Confetti(confetti)
    }
}

@Composable
private fun Finished(hadCards: Boolean, xp: Int, onMoreNew: () -> Unit, onPractice: () -> Unit, onQuiz: () -> Unit) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(Modifier.appear(0)) {
            EmptyState(
                if (hadCards) "🎉" else "😎",
                if (hadCards) "Sesi Selesai!" else "Kartu Hari Ini Sudah Habis",
                if (hadCards) "Kerja bagus! Kata yang belum hafal akan muncul lagi besok." else "Kamu bisa tambah kata baru atau latihan bebas.",
            )
        }
        if (xp > 0) XpEarned(xp, Modifier.align(Alignment.CenterHorizontally).appear(1))
        ActionRow(Icons.Rounded.AddCircle, "Pelajari 5 Kata Baru Lagi", "Di luar target harian", Modifier.appear(2), onMoreNew)
        ActionRow(Icons.Rounded.Repeat, "Latihan Bebas", "Acak dari kata yang sudah dipelajari", Modifier.appear(3), onPractice)
        ActionRow(Icons.Rounded.Quiz, "Uji di Latihan", "Kuis, Ujian HSK, dan lainnya", Modifier.appear(4), onQuiz)
    }
}

/**
 * Satu kartu: depan hanzi (di atas warna merek), belakang pinyin dan arti.
 * Ketuk kartu atau tombol untuk membalik; setelah dibalik pilih Belum Hafal atau Sudah Hafal.
 */
@Composable
private fun FlipCard(word: Word, retry: Boolean, key: Int, onAnswer: (Boolean) -> Unit, modifier: Modifier) {
    val cs = MaterialTheme.colorScheme
    var flipped by remember(key) { mutableStateOf(false) }
    val rot by animateFloatAsState(if (flipped) 180f else 0f, spring(dampingRatio = 0.75f, stiffness = 240f), label = "flip")
    val enter = remember(key) { Animatable(0.92f) }
    val density = LocalDensity.current
    LaunchedEffect(key) { enter.animateTo(1f, spring(dampingRatio = 0.65f, stiffness = 320f)) }
    LaunchedEffect(flipped) { if (flipped) Tts.speak(word.hanzi) }

    Column(modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 14.dp)) {
        Box(
            Modifier.weight(1f).fillMaxWidth()
                .graphicsLayer { scaleX = enter.value; scaleY = enter.value; alpha = ((enter.value - 0.92f) / 0.08f).coerceIn(0f, 1f) }
                .bounceClick(enabled = !flipped) { flipped = true },
        ) {
            val showBack = rot > 90f
            Box(Modifier.fillMaxSize().graphicsLayer { rotationY = rot; cameraDistance = 14f * density.density }) {
                Box(Modifier.fillMaxSize().graphicsLayer { if (showBack) rotationY = 180f }) {
                    if (!showBack) CardFront(word, retry) else CardBack(word)
                }
            }
        }
        Spacer(Modifier.height(14.dp))
        if (!flipped) {
            BigButton("Balik Kartu", cs.primary, Color.White, Modifier.fillMaxWidth()) { flipped = true }
        } else {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                BigButton("Belum Hafal", Brand.error.copy(alpha = 0.14f), Brand.error, Modifier.weight(1f)) { onAnswer(false) }
                BigButton("Sudah Hafal", Brand.success, Color.White, Modifier.weight(1f)) { onAnswer(true) }
            }
        }
    }
}

@Composable
private fun BigButton(label: String, bg: Color, fg: Color, modifier: Modifier, onClick: () -> Unit) {
    Box(
        modifier.navigationBarsPadding().clip(RoundedCornerShape(50)).background(bg).bounceClick(onClick = onClick).padding(vertical = 16.dp),
        contentAlignment = Alignment.Center,
    ) { Text(label, color = fg, style = MaterialTheme.typography.titleMedium) }
}

/** Depan: kartu biru merek dengan hanzi putih besar. */
@Composable
private fun CardFront(word: Word, retry: Boolean) {
    Box(
        Modifier.fillMaxSize()
            .shadow(16.dp, RoundedCornerShape(32.dp), ambientColor = Color(0x552563EB), spotColor = Color(0x772563EB))
            .clip(RoundedCornerShape(32.dp)).background(Brand.card),
    ) {
        Column(Modifier.fillMaxSize().padding(22.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                LevelBadge(word.level, onBrand = true)
                Spacer(Modifier.weight(1f))
                if (retry) Pill("Ulang Sekali Lagi", color = Color(0x33FFFFFF), textColor = Color.White)
            }
            Spacer(Modifier.weight(1f))
            HanziText(word.hanzi, size = if (word.hanzi.length > 4) 52.sp else 100.sp, color = Color.White)
            Spacer(Modifier.weight(1f))
            Text("Ketuk untuk membalik", style = MaterialTheme.typography.labelLarge, color = Color.White.copy(alpha = 0.8f))
        }
    }
}

/** Belakang: pinyin dan arti, ditambah contoh kalimat. */
@Composable
private fun CardBack(word: Word) {
    val cs = MaterialTheme.colorScheme
    Box(
        Modifier.fillMaxSize()
            .shadow(14.dp, RoundedCornerShape(32.dp), ambientColor = Color(0x332563EB), spotColor = Color(0x552563EB))
            .clip(RoundedCornerShape(32.dp)).background(cs.surface),
    ) {
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                LevelBadge(word.level)
                Spacer(Modifier.width(6.dp))
                if (word.book > 0) Pill("4下 Bab ${word.book}")
                Spacer(Modifier.weight(1f))
                SpeakButton(word.hanzi, onBrand = false)
            }
            Spacer(Modifier.height(18.dp))
            HanziText(word.hanzi, size = if (word.hanzi.length > 4) 40.sp else 60.sp, color = cs.primary)
            Spacer(Modifier.height(4.dp))
            PinyinText(word.pinyin, word.hanzi, size = 30.sp)
            Spacer(Modifier.height(14.dp))
            Text(word.main, style = MaterialTheme.typography.headlineSmall, textAlign = TextAlign.Center, color = cs.onSurface)
            Text(posName(word.pos), style = MaterialTheme.typography.labelLarge, color = cs.onSurfaceVariant)
            if (word.others.isNotEmpty()) {
                Spacer(Modifier.height(6.dp))
                Text("Arti lain: " + word.others.joinToString(" · "), style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center, color = cs.onSurfaceVariant)
            }
            Spacer(Modifier.height(16.dp))
            Column(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(cs.primaryContainer)
                    .bounceClick { Tts.speak(word.example) }.padding(14.dp),
            ) {
                Text("Contoh 🔊", style = MaterialTheme.typography.labelMedium, color = cs.primary)
                Text(word.example, fontFamily = HanziFont, fontSize = 20.sp, color = cs.onSurface)
            }
        }
    }
}
