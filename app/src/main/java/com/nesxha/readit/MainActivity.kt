package com.nesxha.readit

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.lifecycleScope
import com.nesxha.readit.auth.Auth
import com.nesxha.readit.data.Store
import kotlinx.coroutines.launch

/** Layar utama Android. Isi aplikasinya ada di modul bersama ([KuaileApp]). */
class MainActivity : ComponentActivity() {
    private val openRoute = mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT))
        super.onCreate(savedInstanceState)
        AndroidPlatform.install(this)
        openRoute.value = intent?.getStringExtra(EXTRA_OPEN)
        setContent {
            KuaileApp(openRoute.value) { openRoute.value = null }
        }
    }

    override fun onResume() {
        super.onResume()
        if (Auth.phase.value is Auth.Phase.Ready) {
            Store.markOpen()
            lifecycleScope.launch { Auth.quietCheck() }
            com.nesxha.readit.data.ProgressSync.later(800)
        }
    }

    override fun onStop() {
        super.onStop()
        // Kirim progres terakhir sebelum aplikasi ditinggalkan.
        if (Auth.phase.value is Auth.Phase.Ready) com.nesxha.readit.data.ProgressSync.later(0)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        openRoute.value = intent.getStringExtra(EXTRA_OPEN)
    }

    companion object { const val EXTRA_OPEN = "open" }
}
