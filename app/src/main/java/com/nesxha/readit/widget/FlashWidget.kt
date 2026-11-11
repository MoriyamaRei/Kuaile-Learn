package com.nesxha.readit.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.ImageProvider
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
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
import androidx.glance.layout.width
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.Text
import com.nesxha.readit.R
import com.nesxha.readit.data.AppState
import com.nesxha.readit.data.Grade
import com.nesxha.readit.data.Store
import com.nesxha.readit.data.Word
import com.nesxha.readit.data.Words

private val REVEALED = booleanPreferencesKey("revealed")
private val SKIP = intPreferencesKey("skip")
private val GRADE_KEY = ActionParameters.Key<Int>("grade")
private val WORD_KEY = ActionParameters.Key<Int>("word")

/** Kartu berikutnya untuk widget: ulangan dulu, lalu kata baru, lalu acak. */
internal fun nextFlash(st: AppState, skip: Int): Word {
    val list = st.dueWords() + st.newWordsToday()
    if (list.isNotEmpty()) return list[skip.mod(list.size)]
    val seen = Words.all.filter { st.card(it.id).seen }
    val pool = seen.ifEmpty { Words.all }
    return pool[(skip * 37 + (System.currentTimeMillis() / 3_600_000L).toInt()).mod(pool.size)]
}

class FlashWidget : GlanceAppWidget() {
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
        val revealed = prefs[REVEALED] ?: false
        val w = nextFlash(st, prefs[SKIP] ?: 0)
        val left = st.remainingToday
        Column(
            GlanceModifier.fillMaxSize().background(ImageProvider(R.drawable.widget_bg_grape)).padding(14.dp),
        ) {
            Row(GlanceModifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    if (left > 0) "Flashcard · $left lagi" else "Flashcard · Latihan",
                    style = wText(11.sp, bold = true, color = WhiteSoft), modifier = GlanceModifier.defaultWeight(),
                )
                Text(
                    "Buka ›", style = wText(11.sp, bold = true),
                    modifier = GlanceModifier.clickable(actionStartActivity(openIntent(context, "flash"))),
                )
            }
            Column(
                GlanceModifier.fillMaxWidth().defaultWeight().clickable(actionRunCallback<RevealAction>()),
                horizontalAlignment = Alignment.CenterHorizontally, verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(w.hanzi, style = wText(if (w.hanzi.length > 3) 28.sp else 40.sp, bold = true, center = true), maxLines = 1)
                if (revealed) {
                    Text(w.pinyin, style = wText(14.sp, bold = true, color = WhiteSoft, center = true), maxLines = 1)
                    Text(w.main, style = wText(14.sp, center = true), maxLines = 2)
                } else {
                    Text("Coba baca dulu, lalu ketuk untuk cek", style = wText(12.sp, color = WhiteSoft, center = true))
                }
            }
            if (revealed) {
                Spacer(GlanceModifier.height(6.dp))
                Row(GlanceModifier.fillMaxWidth()) {
                    Box(
                        GlanceModifier.defaultWeight().background(ImageProvider(R.drawable.widget_bg_red)).padding(vertical = 8.dp)
                            .clickable(actionRunCallback<FlashGradeAction>(actionParametersOf(GRADE_KEY to Grade.LUPA.ordinal, WORD_KEY to w.id))),
                        contentAlignment = Alignment.Center,
                    ) { Text("✗ Lupa", style = wText(13.sp, bold = true, center = true)) }
                    Spacer(GlanceModifier.width(8.dp))
                    Box(
                        GlanceModifier.defaultWeight().background(ImageProvider(R.drawable.widget_bg_green)).padding(vertical = 8.dp)
                            .clickable(actionRunCallback<FlashGradeAction>(actionParametersOf(GRADE_KEY to Grade.BISA.ordinal, WORD_KEY to w.id))),
                        contentAlignment = Alignment.Center,
                    ) { Text("✓ Bisa", style = wText(13.sp, bold = true, center = true)) }
                }
            }
        }
    }
}

class RevealAction : ActionCallback {
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        updateAppWidgetState(context, glanceId) { it[REVEALED] = !(it[REVEALED] ?: false) }
        FlashWidget().update(context, glanceId)
    }
}

class FlashGradeAction : ActionCallback {
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        com.nesxha.readit.AndroidPlatform.installBase(context)
        val g = Grade.entries[parameters[GRADE_KEY] ?: Grade.BISA.ordinal]
        val id = parameters[WORD_KEY] ?: return
        val wasDueOrNew = (Store.now.dueWords() + Store.now.newWordsToday()).any { it.id == id }
        Store.grade(id, g)
        updateAppWidgetState(context, glanceId) {
            it[REVEALED] = false
            // Kartu yang dilupakan tetap jatuh tempo hari ini; lewati supaya kartu lain muncul dulu.
            if (g == Grade.LUPA || !wasDueOrNew) it[SKIP] = (it[SKIP] ?: 0) + 1
        }
        FlashWidget().update(context, glanceId)
    }
}

class FlashWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = FlashWidget()
}
