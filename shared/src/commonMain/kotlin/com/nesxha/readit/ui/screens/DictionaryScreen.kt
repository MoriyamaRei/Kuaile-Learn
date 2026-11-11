package com.nesxha.readit.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Calculate
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Draw
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.nesxha.readit.data.Status
import com.nesxha.readit.data.Store
import com.nesxha.readit.data.Words
import com.nesxha.readit.ui.components.Chip
import com.nesxha.readit.ui.components.EmptyState
import com.nesxha.readit.ui.components.HeaderButton
import com.nesxha.readit.ui.components.ScreenHeader
import com.nesxha.readit.ui.components.Segmented
import com.nesxha.readit.ui.components.WordRow
import com.nesxha.readit.ui.components.appear
import com.nesxha.readit.ui.components.bounceClick
import com.nesxha.readit.ui.theme.Brand
import com.nesxha.readit.ui.theme.UiFont
import com.nesxha.readit.util.Pinyin

@Composable
fun DictionaryScreen(nav: NavHostController) {
    val st by Store.state.collectAsState()
    var query by rememberSaveable { mutableStateOf("") }
    var level by rememberSaveable { mutableIntStateOf(0) }
    var status by rememberSaveable { mutableIntStateOf(-1) }
    var theme by rememberSaveable { mutableStateOf<String?>(null) }
    var starOnly by rememberSaveable { mutableStateOf(false) }
    var book by rememberSaveable { mutableIntStateOf(-1) }   // -1 mati, 0 semua bab, 11..20 bab tertentu
    var showFilters by rememberSaveable { mutableStateOf(false) }
    var writing by remember { mutableStateOf(false) }
    if (writing) HandwritingSearchDialog(onPick = { query += it }, onClose = { writing = false })

    val results = remember(query, level, status, theme, starOnly, book, st.cards) {
        val q = query.trim().lowercase()
        val qPlain = Pinyin.plain(q).replace(" ", "")
        Words.all.filter { w ->
            val c = st.card(w.id)
            (level == 0 || w.level == level) &&
                (status < 0 || c.status.ordinal == status) &&
                (theme == null || w.theme == theme) &&
                (!starOnly || c.star) &&
                (book < 0 || (w.book > 0 && (book == 0 || w.book == book))) &&
                (q.isEmpty() || w.hanzi.contains(q) ||
                    (qPlain.isNotEmpty() && w.pinyinPlain.replace(" ", "").contains(qPlain)) ||
                    w.searchMeaning.contains(q))
        }.let { list ->
            if (q.isEmpty()) list
            else list.sortedBy { w ->
                when {
                    w.hanzi == q || w.pinyinPlain.replace(" ", "") == qPlain || w.main.lowercase() == q -> 0
                    w.pinyinPlain.replace(" ", "").startsWith(qPlain) || w.main.lowercase().startsWith(q) -> 1
                    else -> 2
                }
            }
        }
    }
    val activeFilters = listOf(status >= 0, theme != null, starOnly, book >= 0).count { it }
    val tuneRot by animateFloatAsState(if (showFilters) 90f else 0f, label = "tune")

    Column(Modifier.fillMaxSize()) {
        ScreenHeader(
            "Kamus", "${Words.all.size} kosakata HSK 1–4",
            trailing = { HeaderButton(Icons.Rounded.Calculate, "Pengubah Angka") { nav.navigate("converter") } },
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Row(
                    Modifier.weight(1f).clip(RoundedCornerShape(50)).background(Color.White).padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.Rounded.Search, null, tint = Brand.blue)
                    Spacer(Modifier.width(8.dp))
                    Box(Modifier.weight(1f)) {
                        if (query.isEmpty()) Text("Cari hanzi, pinyin, atau arti", style = MaterialTheme.typography.bodyLarge, color = Color(0xFF94A3B8), maxLines = 1)
                        BasicTextField(
                            value = query, onValueChange = { query = it }, singleLine = true,
                            textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color(0xFF0F1B33), fontFamily = UiFont),
                            cursorBrush = SolidColor(Brand.blue), modifier = Modifier.fillMaxWidth(),
                        )
                    }
                    if (query.isNotEmpty()) Icon(Icons.Rounded.Close, "Hapus", tint = Color(0xFF64748B), modifier = Modifier.size(20.dp).bounceClick { query = "" })
                }
                Spacer(Modifier.width(8.dp))
                Box(
                    Modifier.size(48.dp).clip(RoundedCornerShape(50)).background(Color(0x2EFFFFFF)).bounceClick { writing = true },
                    contentAlignment = Alignment.Center,
                ) { Icon(Icons.Rounded.Draw, "Cari Lewat Tulisan", tint = Color.White) }
                Spacer(Modifier.width(8.dp))
                Box(
                    Modifier.size(48.dp).clip(RoundedCornerShape(50))
                        .background(if (showFilters || activeFilters > 0) Color.White else Color(0x2EFFFFFF))
                        .bounceClick { showFilters = !showFilters },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Rounded.Tune, "Filter", tint = if (showFilters || activeFilters > 0) Brand.blue else Color.White, modifier = Modifier.rotate(tuneRot))
                    if (activeFilters > 0) {
                        Box(Modifier.align(Alignment.TopEnd).padding(6.dp).size(16.dp).clip(RoundedCornerShape(50)).background(Brand.error), contentAlignment = Alignment.Center) {
                            Text("$activeFilters", color = Color.White, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            Segmented(listOf("Semua", "HSK 1", "HSK 2", "HSK 3", "HSK 4"), level, onBrand = true) { level = it }
        }

        LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(top = 12.dp, bottom = 20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            item {
                AnimatedVisibility(showFilters, enter = expandVertically() + fadeIn(), exit = shrinkVertically() + fadeOut()) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        FilterLabel("Status")
                        ChipRow {
                            Chip("Semua", status < 0) { status = -1 }
                            Status.entries.forEach { s -> Chip(s.label, status == s.ordinal) { status = if (status == s.ordinal) -1 else s.ordinal } }
                            Chip("⭐ Berbintang", starOnly) { starOnly = !starOnly }
                        }
                        FilterLabel("Tema")
                        ChipRow {
                            Chip("Semua Tema", theme == null) { theme = null }
                            Words.themeOrder.filter { Words.themes.containsKey(it) }.forEach { id ->
                                val t = Words.theme(id)
                                Chip("${t.emoji} ${t.name}", theme == id) { theme = if (theme == id) null else id }
                            }
                        }
                        FilterLabel("Buku HSK 4下")
                        ChipRow {
                            Chip("Mati", book < 0) { book = -1 }
                            Chip("Semua Bab", book == 0) { book = 0 }
                            (11..20).forEach { b -> Chip("Bab $b", book == b) { book = b } }
                        }
                        if (activeFilters > 0) {
                            Text(
                                "Hapus Semua Filter",
                                Modifier.padding(horizontal = 18.dp).bounceClick { status = -1; theme = null; starOnly = false; book = -1 },
                                style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }
                }
            }
            item {
                Text(
                    "${results.size} kata ditemukan", Modifier.padding(horizontal = 18.dp),
                    style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            if (results.isEmpty()) {
                item { EmptyState("🔍", "Tidak Ditemukan", "Coba kata kunci lain atau longgarkan filternya.") }
            }
            itemsIndexed(results, key = { _, w -> w.id }) { i, w ->
                val c = st.card(w.id)
                WordRow(
                    w, Modifier.padding(horizontal = 16.dp).appear(if (i < 12) i else -1),
                    extra = when (c.status) { Status.BARU -> null; Status.BELAJAR -> "Belajar"; Status.HAFAL -> "Hafal" },
                    trailing = {
                        Icon(
                            if (c.star) Icons.Rounded.Star else Icons.Rounded.StarBorder, "Bintang",
                            tint = if (c.star) Brand.gold else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.bounceClick { Store.toggleStar(w.id) }.padding(4.dp),
                        )
                    },
                ) { nav.navigate("word/${w.id}") }
            }
        }
    }
}

@Composable
private fun FilterLabel(text: String) {
    Text(text, Modifier.padding(horizontal = 18.dp), style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onBackground)
}

@Composable
private fun ChipRow(content: @Composable () -> Unit) {
    Row(
        Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) { content() }
}
