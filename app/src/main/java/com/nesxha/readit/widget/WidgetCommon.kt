package com.nesxha.readit.widget

import android.content.Context
import android.content.Intent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import androidx.glance.appwidget.updateAll
import androidx.glance.text.FontWeight
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import androidx.compose.runtime.Composable
import androidx.glance.GlanceModifier
import androidx.glance.ImageProvider
import androidx.glance.action.clickable
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.text.Text
import androidx.compose.ui.unit.dp
import com.nesxha.readit.MainActivity
import com.nesxha.readit.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

object WidgetSync {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    fun refresh(ctx: Context) {
        scope.launch {
            runCatching { WordWidget().updateAll(ctx) }
            runCatching { FlashWidget().updateAll(ctx) }
            runCatching { ProgressWidget().updateAll(ctx) }
        }
    }
}

internal val White = ColorProvider(Color.White)
internal val WhiteSoft = ColorProvider(Color(0xE6FFFFFF))

internal fun wText(size: TextUnit = 14.sp, bold: Boolean = false, color: ColorProvider = White, center: Boolean = false) = TextStyle(
    color = color, fontSize = size, fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
    textAlign = if (center) TextAlign.Center else TextAlign.Start,
)

internal fun openIntent(ctx: Context, route: String?): Intent =
    Intent(ctx, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        if (route != null) putExtra(MainActivity.EXTRA_OPEN, route)
        // Data unik supaya PendingIntent dengan rute berbeda tidak saling menimpa.
        data = android.net.Uri.parse("readit://open/" + (route ?: "home"))
    }

/** Widget hanya tampil untuk anggota yang sudah masuk dengan KMC. */
internal fun signedIn(ctx: Context): Boolean =
    ctx.getSharedPreferences("auth", Context.MODE_PRIVATE).getString("token", null) != null

@Composable
internal fun LockedWidget(ctx: Context) {
    Column(
        GlanceModifier.fillMaxSize().background(ImageProvider(R.drawable.widget_bg_sunset)).padding(14.dp)
            .clickable(actionStartActivity(openIntent(ctx, null))),
        verticalAlignment = Alignment.CenterVertically, horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("🔒 Kuaile Learn", style = wText(15.sp, bold = true, center = true))
        Text("Masuk dengan nomor KMC untuk melihat widget ini.", style = wText(12.sp, color = WhiteSoft, center = true))
    }
}
