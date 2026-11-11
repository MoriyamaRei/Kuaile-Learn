package com.nesxha.readit.data

import com.nesxha.readit.auth.Auth
import com.nesxha.readit.auth.Member
import com.nesxha.readit.auth.Res
import com.nesxha.readit.json.JSONObject
import com.nesxha.readit.platform.IoDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Sinkron progres belajar satu akun antara HP dan web lewat server.
 * Dipanggil saat aplikasi dibuka, beberapa detik setelah belajar, dan berkala selama aplikasi terbuka.
 * Kalau offline, progres tetap tersimpan di perangkat dan dikirim saat online lagi.
 */
object ProgressSync {
    private val scope = CoroutineScope(SupervisorJob() + IoDispatcher)
    private val lock = Mutex()
    private var soon: Job? = null

    /** Sinkron sekarang. True kalau berhasil. */
    suspend fun now(): Boolean = lock.withLock { runOnce() }

    /** Sinkron sebentar lagi (beberapa perubahan berturut-turut dikirim sekali). */
    fun later(delayMs: Long = 6000) {
        soon?.cancel()
        soon = scope.launch { delay(delayMs); now() }
    }

    private suspend fun runOnce(): Boolean {
        if (!Member.available) return false
        val s = Auth.currentSession() ?: return false
        if (!Auth.hasInternet()) return false
        Store.switchOwner(s.kmc)
        var claim = false
        repeat(3) {
            val out = Store.syncOut()
            val r = Member.progressSync(out.own, out.gen, out.reset, claim, out.otherAt)
            val j = (r as? Res.Ok)?.value ?: return false
            val gen = j.optInt("gen", out.gen)
            when {
                j.optBoolean("reset") -> { Store.serverReset(gen); claim = false }
                j.has("mine") && !j.isNull("mine") -> {
                    parse(j.optString("mine"))?.let { Store.adopt(it) }
                    claim = true
                }
                else -> {
                    val other = if (j.has("other") && !j.isNull("other")) parse(j.optString("other")) else null
                    Store.synced(out.own, gen, other, j.optLong("other_at", 0))
                    return true
                }
            }
        }
        return false
    }

    private fun parse(txt: String): AppState? = runCatching { AppState.fromJson(JSONObject(txt)) }.getOrNull()
}
