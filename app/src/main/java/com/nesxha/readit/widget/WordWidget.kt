package com.nesxha.readit.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.ImageProvider
import androidx.glance.action.ActionParameters
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.Text
import com.nesxha.readit.R
import com.nesxha.readit.data.AppState
import com.nesxha.readit.data.Store
import com.nesxha.readit.data.Word
import com.nesxha.readit.data.Words
import kotlin.random.Random

private val SHIFT = intPreferencesKey("shift")

/** Kata yang tampil: berganti tiap 30 menit, atau saat tombol ganti ditekan. */
internal fun currentWord(st: AppState, shift: Int): Word {
    val seen = Words.all.filter { st.card(it.id).seen }
    val pool = if (seen.size >= 5) seen else Words.all.filter { it.level == st.wordOfDay().level }
    val slot = System.currentTimeMillis() / 1_800_000L
    return pool[Random(slot * 7919 + shift).nextInt(pool.size)]
}

class WordWidget : GlanceAppWidget() {
    override val stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        com.nesxha.readit.AndroidPlatform.installBase(context)
        provideContent { Content(context) }
    }

    @Composable
    private fun Content(context: Context) {
        if (!signedIn(context)) { LockedWidget(context); return }
        val st by Store.state.collectAsState()
        val prefs = currentState<Preferences>()
        val w = currentWord(st, prefs[SHIFT] ?: 0)
        Column(
            GlanceModifier.fillMaxSize().background(ImageProvider(R.drawable.widget_bg_sunset)).padding(14.dp)
                .clickable(actionStartActivity(openIntent(context, "word/${w.id}"))),
        ) {
            Row(GlanceModifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("HSK ${w.level} · Kata Saat Ini", style = wText(11.sp, bold = true, color = WhiteSoft), modifier = GlanceModifier.defaultWeight())
                Box(
                    GlanceModifier.background(ImageProvider(R.drawable.widget_bg_white)).padding(horizontal = 10.dp, vertical = 4.dp)
                        .clickable(actionRunCallback<NextWordAction>()),
                ) { Text("↻", style = wText(14.sp, bold = true)) }
            }
            Spacer(GlanceModifier.defaultWeight())
            Text(w.hanzi, style = wText(if (w.hanzi.length > 3) 30.sp else 44.sp, bold = true), maxLines = 1)
            Text(w.pinyin, style = wText(15.sp, bold = true, color = WhiteSoft), maxLines = 1)
            Spacer(GlanceModifier.height(2.dp))
            Text(w.main, style = wText(14.sp), maxLines = 2)
        }
    }
}

class NextWordAction : ActionCallback {
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        updateAppWidgetState(context, glanceId) { it[SHIFT] = (it[SHIFT] ?: 0) + 1 }
        WordWidget().update(context, glanceId)
    }
}

class WordWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = WordWidget()
}
