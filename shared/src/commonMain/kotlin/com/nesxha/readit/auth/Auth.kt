package com.nesxha.readit.auth

import com.nesxha.readit.platform.Plat
import com.nesxha.readit.platform.PlatUi
import com.nesxha.readit.platform.Prefs
import com.nesxha.readit.platform.randomUuid
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withTimeoutOrNull
import com.nesxha.readit.platform.nowMs

/**
 * Pengelola akun KMC di HP: menyimpan sesi, mengecek ke server saat aplikasi dibuka,
 * dan menjalankan aturan Mode Offline 3 hari.
 * Data sesi disimpan di prefs "auth" yang dikecualikan dari cadangan Google,
 * supaya sesi tidak ikut pindah ke HP lain lewat pemulihan cadangan.
 */
object Auth {
    sealed interface Phase {
        object Loading : Phase
        data class SignedOut(val notice: String? = null) : Phase
        object NeedOnline : Phase
        /** [offline] = belum terhubung ke server saat ini (progres tidak masuk leaderboard). */
        data class Ready(val session: Session, val offline: Boolean, val serverIssue: Boolean = false, val daysLeft: Int = OfflinePolicy.MAX_DAYS) : Phase
    }

    private val _phase = MutableStateFlow<Phase>(Phase.Loading)
    val phase: StateFlow<Phase> = _phase

    lateinit var api: AuthApi
        private set
    private lateinit var prefs: Prefs
    private var lastCheck = 0L

    /**
     * Penanda perangkat. Di web diawali "web-", supaya server memakai slot web
     * (satu akun boleh dipakai di 1 HP Android dan 1 browser).
     */
    val deviceId: String
        get() = prefs.getString("device", null) ?: ((if (Plat.p.isWeb) "web-" else "") + randomUuid()).also { prefs.edit().putString("device", it).apply() }

    fun init() {
        if (::prefs.isInitialized) return
        prefs = Prefs("auth")
        val url = com.nesxha.readit.AppInfo.SUPABASE_URL
        val key = com.nesxha.readit.AppInfo.SUPABASE_KEY
        api = if (url.isNotBlank() && key.isNotBlank()) SupabaseApi(url, key, ::hasInternet)
        else DemoAuthApi(object : KeyValue {
            override fun get(key: String) = prefs.getString(key, null)
            override fun put(key: String, value: String?) { prefs.edit().putString(key, value).apply() }
        })
    }

    /** True kalau HP tersambung ke jaringan yang benar-benar punya akses internet. */
    fun hasInternet(): Boolean = Plat.p.hasInternet()

    private fun session(): Session? {
        val kmc = prefs.getString("kmc", null) ?: return null
        val token = prefs.getString("token", null) ?: return null
        return Session(kmc, token, prefs.getString("nickname", "") ?: "")
    }

    private fun saveSession(s: Session) {
        prefs.edit().putString("kmc", s.kmc).putString("token", s.token).putString("nickname", s.nickname).apply()
    }

    private fun clearSession() {
        prefs.edit().remove("token").remove("nickname").remove("confirmed").commit()
        PlatUi.ui.onSignedOut()
    }

    val hasSession: Boolean get() = session() != null

    /** Sesi yang sedang dipakai (untuk fitur anggota), atau null. */
    fun currentSession(): Session? = session()

    /** Sesi ditolak server (direset/diblokir admin): keluar tanpa perlu online. */
    fun forceSignOut() {
        clearSession()
        _phase.value = Phase.SignedOut(AuthError.Revoked.message)
    }

    /** Memperbarui nickname yang tampil setelah diganti di Profil. */
    fun updateNickname(n: String) {
        val s = session() ?: return
        saveSession(s.copy(nickname = n))
        val p = _phase.value
        if (p is Phase.Ready) _phase.value = p.copy(session = p.session.copy(nickname = n))
    }

    /** True kalau aplikasi sedang tersambung ke server (progres masuk leaderboard). */
    val isOnline: Boolean get() = (_phase.value as? Phase.Ready)?.offline == false && !api.isDemo

    /** Nomor KMC terakhir yang dipakai, untuk mengisi otomatis layar Masuk. */
    val lastKmc: String? get() = prefs.getString("kmc", null)

    private fun confirm(now: Long) {
        prefs.edit().putLong("confirmed", now).putLong("seen", maxOf(now, prefs.getLong("seen", 0))).apply()
    }

    private fun touch(now: Long) {
        prefs.edit().putLong("seen", maxOf(now, prefs.getLong("seen", 0))).apply()
    }

    /** Mengecek ulang dan langsung menerapkan hasilnya (tombol "Coba Lagi"). */
    suspend fun boot() {
        _phase.value = evaluate()
    }

    /** Menerapkan hasil [evaluate] (dipakai layar loading setelah animasinya selesai). */
    fun apply(p: Phase) {
        _phase.value = p
    }

    /** Menentukan keadaan saat aplikasi dibuka: belum masuk, perlu online, atau siap (online/offline). */
    suspend fun evaluate(): Phase {
        val s = session() ?: return Phase.SignedOut()
        val now = nowMs()
        lastCheck = now
        if (hasInternet()) {
            val r = withTimeoutOrNull(9000) { api.verify(s.token, deviceId) }
            if (r is Res.Ok) {
                val fresh = s.copy(nickname = r.value.ifEmpty { s.nickname })
                saveSession(fresh); confirm(now)
                return Phase.Ready(fresh, offline = false)
            }
            if (r is Res.Err && r.error == AuthError.Revoked) {
                clearSession()
                return Phase.SignedOut(AuthError.Revoked.message)
            }
            // Internet ada tetapi server tidak menjawab: bukan salah anggota, hitungan offline tidak berjalan.
            confirm(now)
            return Phase.Ready(s, offline = true, serverIssue = true)
        }
        val st = OfflinePolicy.status(prefs.getLong("confirmed", 0), prefs.getLong("seen", 0), now)
        if (st.expired) return Phase.NeedOnline
        touch(now)
        return Phase.Ready(s, offline = true, daysLeft = st.daysLeft)
    }

    /** Cek diam-diam saat aplikasi kembali dibuka dari latar belakang (paling sering 30 menit sekali). */
    suspend fun quietCheck() {
        if (_phase.value !is Phase.Ready) return
        if (nowMs() - lastCheck < 30 * 60 * 1000L) return
        _phase.value = evaluate()
    }

    /** Login menyimpan sesi; layar memanggil [enter] setelah animasi sukses selesai. */
    suspend fun login(kmc: String, pin: String): Res<Session> {
        if (!api.isDemo && !hasInternet()) return Res.Err(AuthError.Network)
        val r = api.login(kmc, pin, deviceId)
        if (r is Res.Ok) {
            saveSession(r.value)
            confirm(nowMs())
            lastCheck = nowMs()
        }
        return r
    }

    fun enter(s: Session) {
        _phase.value = Phase.Ready(s, offline = false)
        PlatUi.ui.onDataChanged()
    }

    /** Logout wajib online supaya server tahu HP ini sudah dilepas. */
    suspend fun logout(): Res<Unit> {
        val s = session() ?: run { _phase.value = Phase.SignedOut(); return Res.Ok(Unit) }
        if (!api.isDemo && !hasInternet()) return Res.Err(AuthError.Network)
        val r = api.logout(s.token, deviceId)
        if (r is Res.Ok || (r is Res.Err && r.error == AuthError.Revoked)) {
            clearSession()
            _phase.value = Phase.SignedOut("Kamu sudah keluar. KMC-mu sekarang bisa dipakai di perangkat lain.")
            return Res.Ok(Unit)
        }
        return r
    }

    fun clearNotice() {
        val p = _phase.value
        if (p is Phase.SignedOut && p.notice != null) _phase.value = Phase.SignedOut()
    }
}
