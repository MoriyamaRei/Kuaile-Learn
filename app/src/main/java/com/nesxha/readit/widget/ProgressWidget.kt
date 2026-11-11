package com.nesxha.readit.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.ImageProvider
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.LinearProgressIndicator
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.Text
import androidx.glance.unit.ColorProvider
import com.nesxha.readit.R
import com.nesxha.readit.data.Store

class ProgressWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        com.nesxha.readit.AndroidPlatform.installBase(context)
        provideContent { Content(context) }
    }

    @Composable
    private fun Content(context: Context) {
        if (!signedIn(context)) { LockedWidget(context); return }
        val st by Store.state.collectAsState()
        val done = st.doneToday
        val total = done + st.remainingToday
        Column(
            GlanceModifier.fillMaxSize().background(ImageProvider(R.drawable.widget_bg_candy)).padding(14.dp)
                .clickable(actionStartActivity(openIntent(context, "flash"))),
        ) {
            Text("Target Hari Ini", style = wText(12.sp, bold = true, color = WhiteSoft))
            Spacer(GlanceModifier.height(2.dp))
            Text(if (st.goalReached) "Tercapai! 🎉" else "$done / $total", style = wText(24.sp, bold = true), maxLines = 1)
            Spacer(GlanceModifier.height(6.dp))
            LinearProgressIndicator(
                progress = st.goalFraction,
                modifier = GlanceModifier.fillMaxWidth().height(8.dp),
                color = ColorProvider(Color.White),
                backgroundColor = ColorProvider(Color(0x55FFFFFF)),
            )
            Spacer(GlanceModifier.defaultWeight())
            Text(
                if (st.brokenStreak > 0) "🕯️ Streak ${st.brokenStreak} padam" else "🔥 ${st.streak} hari streak",
                style = wText(13.sp, bold = true), maxLines = 1,
            )
            Text("📖 ${st.seenCount()} kata · 🏆 ${st.masteredCount()} hafal", style = wText(11.sp, color = WhiteSoft), maxLines = 1)
        }
    }
}

class ProgressWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = ProgressWidget()
}
