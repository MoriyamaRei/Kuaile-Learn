package com.nesxha.readit.web

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.platform.Font
import androidx.compose.ui.window.ComposeViewport
import com.nesxha.readit.KuaileApp
import com.nesxha.readit.auth.Auth
import com.nesxha.readit.data.Store
import com.nesxha.readit.data.Words
import com.nesxha.readit.platform.Plat
import com.nesxha.readit.platform.PlatUi
import com.nesxha.readit.platform.WebPlatform
import com.nesxha.readit.platform.WebUi
import com.nesxha.readit.res.Res
import kotlinx.browser.document
import org.jetbrains.compose.resources.ExperimentalResourceApi

@JsFun("(k) => { try { return localStorage.getItem(k); } catch (e) { return null; } }")
private external fun lsGet(k: String): String?

@JsFun("(k, v) => { try { localStorage.setItem(k, v); } catch (e) { globalThis.__kToastFull && globalThis.__kToastFull(); } }")
private external fun lsSet(k: String, v: String)

/** Menutup layar pembuka HTML setelah aplikasi siap. */
@JsFun("() => { const s = document.getElementById('k-splash'); if (s) { s.classList.add('hide'); setTimeout(() => s.remove(), 400); } }")
private external fun hideSplash()

/** Pesan kalau data gagal dimuat. */
@JsFun("(m) => { const s = document.getElementById('k-splash-msg'); if (s) s.textContent = m; }")
private external fun splashMessage(m: String)

@JsFun("() => document.hidden === true")
private external fun pageHidden(): Boolean

private const val PROGRESS_KEY = "kuaile.progress"

@OptIn(ExperimentalComposeUiApi::class, ExperimentalResourceApi::class)
fun main() {
    Plat.p = WebPlatform
    PlatUi.ui = WebUi
    ComposeViewport(document.body!!) {
        var ready by remember { mutableStateOf(false) }
        val fonts = LocalFontFamilyResolver.current
        // Browser tidak memberi akses ke emoji sistem; pakai Noto Color Emoji (hanya emoji yang dipakai aplikasi).
        LaunchedEffect(Unit) {
            runCatching { fonts.preload(FontFamily(Font("KuaileEmoji", Res.readBytes("files/emoji.ttf")))) }
        }
        LaunchedEffect(Unit) {
            val words = runCatching { Res.readBytes("files/words.json").decodeToString() }
            if (words.isFailure) { splashMessage("Data kosakata gagal dimuat. Periksa internet lalu muat ulang halaman."); return@LaunchedEffect }
            Words.init { words.getOrThrow() }
            Store.init(object : Store.StoreIo {
                override fun read(): String? = lsGet(PROGRESS_KEY)
                override fun write(text: String) = lsSet(PROGRESS_KEY, text)
            })
            Auth.init()
            // Tab ditutup/disembunyikan: kirim progres terakhir; dibuka lagi: ambil yang terbaru.
            document.addEventListener("visibilitychange") {
                if (Auth.phase.value is Auth.Phase.Ready) com.nesxha.readit.data.ProgressSync.later(if (pageHidden()) 0 else 500)
            }
            ready = true
            hideSplash()
        }
        if (ready) KuaileApp() else Box(Modifier.fillMaxSize().background(Color(0xFF1D4ED8)))
    }
}
