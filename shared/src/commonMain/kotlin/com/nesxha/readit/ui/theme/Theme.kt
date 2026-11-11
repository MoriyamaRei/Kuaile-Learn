package com.nesxha.readit.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.nesxha.readit.res.Res
import com.nesxha.readit.res.hanzi_bold
import com.nesxha.readit.res.hanzi_regular
import com.nesxha.readit.res.ui_bold
import com.nesxha.readit.res.ui_extrabold
import com.nesxha.readit.res.ui_medium
import com.nesxha.readit.res.ui_regular
import com.nesxha.readit.res.ui_semibold
import org.jetbrains.compose.resources.Font

/** Font antarmuka (Plus Jakarta Sans). Dimuat lewat Compose Resources, jadi berupa getter @Composable. */
val UiFont: FontFamily
    @Composable get() = FontFamily(
        Font(Res.font.ui_regular, FontWeight.Normal),
        Font(Res.font.ui_medium, FontWeight.Medium),
        Font(Res.font.ui_semibold, FontWeight.SemiBold),
        Font(Res.font.ui_bold, FontWeight.Bold),
        Font(Res.font.ui_extrabold, FontWeight.ExtraBold),
    )

/** Font hanzi (LXGW WenKai). */
val HanziFont: FontFamily
    @Composable get() = FontFamily(
        Font(Res.font.hanzi_regular, FontWeight.Normal),
        Font(Res.font.hanzi_bold, FontWeight.Bold),
    )

/** Palet utama: biru-putih. */
object Brand {
    val blue = Color(0xFF2563EB)
    val blueLight = Color(0xFF60A5FA)
    val blueDeep = Color(0xFF1E40AF)
    val navy = Color(0xFF1E3A8A)
    val sky = Color(0xFFBFDBFE)
    val success = Color(0xFF16A34A)
    val error = Color(0xFFDC2626)
    val warning = Color(0xFFF59E0B)
    val gold = Color(0xFFFBBF24)

    /** Gradasi kepala layar. */
    val header: Brush
        @Composable @ReadOnlyComposable get() = if (LocalDark.current)
            Brush.linearGradient(listOf(Color(0xFF172554), Color(0xFF1E40AF)))
        else Brush.linearGradient(listOf(Color(0xFF1D4ED8), Color(0xFF3B82F6)))

    /** Gradasi kartu unggulan. */
    val card: Brush
        @Composable @ReadOnlyComposable get() = if (LocalDark.current)
            Brush.linearGradient(listOf(Color(0xFF1E40AF), Color(0xFF2563EB)))
        else Brush.linearGradient(listOf(Color(0xFF2563EB), Color(0xFF60A5FA)))

    val solid: Brush get() = Brush.linearGradient(listOf(blue, blue))
}

/** Warna per level HSK (dipakai sebagai aksen kecil saja). */
object LevelColor {
    fun of(level: Int): Color = when (level) {
        1 -> Color(0xFF22C55E); 2 -> Color(0xFF3B82F6); 3 -> Color(0xFFF97316); else -> Color(0xFF8B5CF6)
    }
}

/** Warna nada: 1 merah, 2 hijau, 3 biru, 4 ungu, netral abu-abu. [dark] = latar gelap. */
object ToneColor {
    fun of(tone: Int, dark: Boolean): Color = when (tone) {
        1 -> if (dark) Color(0xFFF87171) else Color(0xFFDC2626)
        2 -> if (dark) Color(0xFF4ADE80) else Color(0xFF16A34A)
        3 -> if (dark) Color(0xFF60A5FA) else Color(0xFF2563EB)
        4 -> if (dark) Color(0xFFC084FC) else Color(0xFF9333EA)
        else -> if (dark) Color(0xFF94A3B8) else Color(0xFF64748B)
    }
}

val LocalDark = staticCompositionLocalOf { false }

private val Light = lightColorScheme(
    primary = Color(0xFF2563EB),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDBEAFE),
    onPrimaryContainer = Color(0xFF1E3A8A),
    secondary = Color(0xFF3B82F6),
    tertiary = Color(0xFF0EA5E9),
    background = Color(0xFFF3F6FC),
    onBackground = Color(0xFF0F1B33),
    surface = Color.White,
    onSurface = Color(0xFF0F1B33),
    surfaceVariant = Color(0xFFEAF0FA),
    onSurfaceVariant = Color(0xFF5B6B85),
    outline = Color(0xFFD9E2F1),
    error = Color(0xFFDC2626),
)

private val Dark = darkColorScheme(
    primary = Color(0xFF3B82F6),
    onPrimary = Color.White,
    primaryContainer = Color(0xFF1E3A5F),
    onPrimaryContainer = Color(0xFFBFDBFE),
    secondary = Color(0xFF60A5FA),
    tertiary = Color(0xFF38BDF8),
    background = Color(0xFF0D131D),
    onBackground = Color(0xFFE5ECF6),
    surface = Color(0xFF172030),
    onSurface = Color(0xFFE5ECF6),
    surfaceVariant = Color(0xFF212C3E),
    onSurfaceVariant = Color(0xFF94A3B8),
    outline = Color(0xFF2B384D),
    error = Color(0xFFF87171),
)

private fun t(ui: FontFamily, size: Int, w: FontWeight, lh: Int = (size * 1.35).toInt()) =
    TextStyle(fontFamily = ui, fontWeight = w, fontSize = size.sp, lineHeight = lh.sp)

private fun type(ui: FontFamily) = Typography(
    displayLarge = t(ui, 48, FontWeight.ExtraBold),
    displayMedium = t(ui, 40, FontWeight.ExtraBold),
    displaySmall = t(ui, 32, FontWeight.ExtraBold),
    headlineLarge = t(ui, 30, FontWeight.ExtraBold),
    headlineMedium = t(ui, 26, FontWeight.ExtraBold),
    headlineSmall = t(ui, 22, FontWeight.Bold),
    titleLarge = t(ui, 19, FontWeight.Bold),
    titleMedium = t(ui, 16, FontWeight.Bold),
    titleSmall = t(ui, 14, FontWeight.SemiBold),
    bodyLarge = t(ui, 16, FontWeight.Normal),
    bodyMedium = t(ui, 14, FontWeight.Normal),
    bodySmall = t(ui, 12, FontWeight.Normal),
    labelLarge = t(ui, 14, FontWeight.Bold),
    labelMedium = t(ui, 12, FontWeight.SemiBold),
    labelSmall = t(ui, 11, FontWeight.SemiBold),
)

@Composable
fun ReadItTheme(darkMode: Int, content: @Composable () -> Unit) {
    val dark = when (darkMode) { 1 -> false; 2 -> true; else -> isSystemInDarkTheme() }
    CompositionLocalProvider(LocalDark provides dark) {
        val ui = UiFont
        val typography = androidx.compose.runtime.remember(ui) { type(ui) }
        MaterialTheme(colorScheme = if (dark) Dark else Light, typography = typography, content = content)
    }
}
