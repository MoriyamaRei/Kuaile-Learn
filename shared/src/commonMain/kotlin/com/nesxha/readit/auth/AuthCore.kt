package com.nesxha.readit.auth

import kotlin.math.ceil

/** Aturan nomor KMC. Format resmi: KMC-00001-00. */
object Kmc {
    private val FULL = Regex("^KMC-\\d{5}-\\d{2}$")

    /** Mengubah ketikan bebas ("kmc 00001 00", "0000100") menjadi format resmi, atau null kalau tidak valid. */
    fun normalize(input: String): String? {
        val digits = input.uppercase().removePrefix("KMC").filter { it.isDigit() }
        if (digits.length != 7) return null
        val out = "KMC-" + digits.take(5) + "-" + digits.drop(5)
        return out.takeIf { FULL.matches(it) }
    }

    /** Format tampilan saat sedang diketik: hanya angka, tanda hubung disisipkan otomatis. */
    fun pretty(digits: String): String {
        val d = digits.filter { it.isDigit() }.take(7)
        return when {
            d.length <= 5 -> "KMC-$d"
            else -> "KMC-" + d.take(5) + "-" + d.drop(5)
        }
    }

    /** Tempat lahir dibandingkan tanpa peduli huruf besar-kecil, spasi, dan tanda baca. */
    fun normalizePlace(s: String): String = s.lowercase().filter { it.isLetterOrDigit() }
}

/** Aturan PIN 6 angka. */
object PinRules {
    const val LENGTH = 6
    private val COMMON = setOf("123123", "112233", "121212", "123321", "147258", "159753", "111222", "520520", "131313", "010203")

    /** Alasan PIN ditolak, atau null kalau PIN boleh dipakai. */
    fun problem(pin: String): String? {
        if (pin.length != LENGTH || !pin.all { it.isDigit() }) return "PIN harus 6 angka."
        if (pin.toSet().size == 1) return "PIN tidak boleh angka yang sama semua."
        val d = pin.map { it - '0' }
        val asc = (1 until d.size).all { d[it] == (d[it - 1] + 1) % 10 }
        val desc = (1 until d.size).all { d[it] == (d[it - 1] + 9) % 10 }
        if (asc || desc) return "PIN tidak boleh angka berurutan."
        if (pin in COMMON || pin.substring(0, 2).repeat(3) == pin || pin.substring(0, 3).repeat(2) == pin)
            return "PIN terlalu mudah ditebak. Pilih kombinasi lain."
        return null
    }
}

/** Aturan nickname: 1–16 karakter, huruf, angka, spasi, titik, atau garis bawah. */
object Nickname {
    const val MAX = 16
    fun clean(s: String): String = s.trim().replace(Regex("\\s+"), " ")
    fun problem(s: String): String? {
        val c = clean(s)
        if (c.isEmpty()) return "Nickname belum diisi."
        if (c.length > MAX) return "Nickname maksimal $MAX karakter."
        if (!c.all { it.isLetterOrDigit() || it == ' ' || it == '.' || it == '_' }) return "Nickname hanya boleh huruf, angka, spasi, titik, dan garis bawah."
        return null
    }
}

/**
 * Aturan kunci (sama dengan website KMC): setiap 3× salah, KMC terkunci
 * 1 menit → 3 menit → 5 menit → 10 menit → 1 jam → permanen.
 * Server yang menjalankan aturan ini; aplikasi memakai salinannya untuk mode demo.
 */
object Lockout {
    const val TRIES = 3
    private val STEPS = longArrayOf(60, 180, 300, 600, 3600)

    /** Lama kunci (ms) untuk tingkat ke-[level] (1, 2, …), atau null kalau permanen. */
    fun durationMs(level: Int): Long? = STEPS.getOrNull(level - 1)?.times(1000)

    data class State(val fails: Int = 0, val level: Int = 0, val until: Long = 0, val forever: Boolean = false)

    /** Keadaan setelah satu percobaan salah pada waktu [now]. */
    fun fail(s: State, now: Long): State {
        val f = s.fails + 1
        if (f < TRIES) return s.copy(fails = f)
        val lv = s.level + 1
        val d = durationMs(lv)
        return if (d == null) State(0, lv, 0, true) else State(0, lv, now + d, false)
    }

    fun left(s: State): Int = TRIES - s.fails
}

/** Mode offline: maksimal 3 hari tanpa internet sejak terakhir terhubung. */
object OfflinePolicy {
    const val MAX_DAYS = 3
    const val DAY_MS = 24L * 3600 * 1000
    const val MAX_MS = MAX_DAYS * DAY_MS
    private const val CLOCK_SLACK = 10L * 60 * 1000

    data class Status(val expired: Boolean, val msLeft: Long) {
        val daysLeft: Int get() = ceil(msLeft.toDouble() / DAY_MS).toInt().coerceIn(0, MAX_DAYS)
    }

    /**
     * [lastConfirmed] = terakhir kali HP punya internet (server menjawab, atau server error saat internet ada).
     * [lastSeen] = waktu terbesar yang pernah tercatat; jam HP yang dimundurkan dianggap kedaluwarsa.
     */
    fun status(lastConfirmed: Long, lastSeen: Long, now: Long): Status {
        if (now + CLOCK_SLACK < lastSeen) return Status(true, 0)
        val left = lastConfirmed + MAX_MS - now
        return Status(left <= 0, left.coerceAtLeast(0))
    }
}

enum class KmcStatus { AKTIF, TERDAFTAR, TIDAK_ADA, DIBLOKIR }

data class Session(val kmc: String, val token: String, val nickname: String)

/** Kesalahan yang bisa terjadi saat berurusan dengan server. */
sealed class AuthError(val message: String) {
    object Network : AuthError("Tidak ada koneksi internet. Sambungkan internet, lalu coba lagi.")
    object Server : AuthError("Server sedang bermasalah. Coba lagi beberapa saat lagi.")
    object NotActive : AuthError("KMC ini belum diaktifkan. Hubungi pengurus KMC.")
    object AlreadyRegistered : AuthError("KMC ini sudah terdaftar. Silakan masuk.")
    object NotRegistered : AuthError("KMC ini belum terdaftar di aplikasi. Silakan daftar dulu.")
    class WrongBirth(val left: Int) : AuthError("Tempat atau tanggal lahir tidak cocok. Sisa percobaan: $left.")
    class WrongPin(val left: Int) : AuthError("PIN salah. Sisa percobaan: $left.")
    class Locked(val until: Long) : AuthError("KMC terkunci sementara.")
    object LockedForever : AuthError("KMC terkunci permanen. Hubungi pengurus KMC untuk membukanya.")
    object OtherDevice : AuthError("KMC ini sedang dipakai di HP lain. Keluar dulu dari HP tersebut, atau hubungi pengurus jika HP-nya hilang.")
    object OtherBrowser : AuthError("KMC ini sedang dipakai di browser lain. Keluar dulu dari browser tersebut, atau hubungi pengurus.")
    object Blocked : AuthError("KMC ini diblokir. Hubungi pengurus KMC.")
    object NicknameTaken : AuthError("Nickname ini sudah dipakai. Coba yang lain.")
    object WeakPin : AuthError("PIN terlalu mudah ditebak. Pilih kombinasi lain.")
    class NickWait(val until: Long) : AuthError("Nickname hanya bisa diganti seminggu sekali.")
    object Revoked : AuthError("Sesi berakhir karena KMC-mu direset atau diblokir pengurus. Silakan masuk lagi.")
    class Invalid(msg: String = "Data tidak valid.") : AuthError(msg)
}

sealed class Res<out T> {
    data class Ok<T>(val value: T) : Res<T>()
    data class Err(val error: AuthError) : Res<Nothing>()
}

/** Kontrak dengan server. Lihat docs/RENCANA-AKUN-KMC.md dan supabase/schema.sql. */
interface AuthApi {
    val isDemo: Boolean
    suspend fun check(kmc: String): Res<KmcStatus>
    /** Mencocokkan tempat dan tanggal lahir (yyyy-MM-dd) sebelum anggota membuat PIN. */
    suspend fun checkBirth(kmc: String, place: String, birth: String): Res<Unit>
    /** [birth] berformat yyyy-MM-dd. */
    suspend fun register(kmc: String, place: String, birth: String, pin: String, nickname: String): Res<Unit>
    suspend fun login(kmc: String, pin: String, device: String): Res<Session>
    /** Ok(nickname) kalau sesi masih sah, Err(Revoked) kalau sudah tidak berlaku. */
    suspend fun verify(token: String, device: String): Res<String>
    suspend fun logout(token: String, device: String): Res<Unit>
}

/** Penyimpanan sederhana untuk mode demo (di aplikasi: SharedPreferences, di tes: Map). */
interface KeyValue {
    fun get(key: String): String?
    fun put(key: String, value: String?)
}

internal fun sha256(s: String): String = com.nesxha.readit.platform.Sha256.hex(s)

/**
 * Server tiruan untuk mencoba tampilan sebelum Supabase tersambung.
 * Semua aturan (kunci, satu HP, PIN lemah) ditiru, tapi datanya hanya ada di HP ini.
 */
class DemoAuthApi(private val kv: KeyValue, private val now: () -> Long = { com.nesxha.readit.platform.nowMs() }) : AuthApi {
    override val isDemo = true

    private class M(
        var status: KmcStatus, val place: String, val birth: String,
        var pin: String? = null, var nickname: String? = null, var device: String? = null, var token: String? = null,
        var lock: Lockout.State = Lockout.State(),
    )

    private val members = linkedMapOf(
        "KMC-00001-00" to M(KmcStatus.AKTIF, "batulicin", "2005-08-17"),
        "KMC-00002-00" to M(KmcStatus.TERDAFTAR, "kotabaru", "2004-03-12", sha256("482915"), "Xiao Mei"),
        "KMC-00003-00" to M(KmcStatus.TERDAFTAR, "banjarmasin", "2003-11-02", sha256("482915"), "Ah Long", "hp-lain", "x"),
        "KMC-00004-00" to M(KmcStatus.DIBLOKIR, "pagatan", "2006-01-20"),
    )

    init { members.forEach { (k, m) -> load(k, m) } }

    private fun load(k: String, m: M) {
        val raw = kv.get("demo:$k") ?: return
        val p = raw.split('|')
        if (p.size < 8) return
        m.status = KmcStatus.valueOf(p[0]); m.pin = p[1].ifEmpty { null }; m.nickname = p[2].ifEmpty { null }
        m.device = p[3].ifEmpty { null }; m.token = p[4].ifEmpty { null }
        m.lock = Lockout.State(p[5].toInt(), p[6].toInt(), p[7].toLong(), p.getOrNull(8) == "1")
    }

    private fun save(k: String, m: M) {
        val l = m.lock
        kv.put("demo:$k", listOf(m.status.name, m.pin ?: "", m.nickname ?: "", m.device ?: "", m.token ?: "",
            l.fails, l.level, l.until, if (l.forever) "1" else "0").joinToString("|"))
    }

    private fun lockError(m: M): AuthError? = when {
        m.lock.forever -> AuthError.LockedForever
        m.lock.until > now() -> AuthError.Locked(m.lock.until)
        else -> null
    }

    /** Mencatat satu kesalahan; mengembalikan error yang pas (sisa percobaan atau terkunci). */
    private fun miss(k: String, m: M, pin: Boolean): AuthError {
        m.lock = Lockout.fail(m.lock, now()); save(k, m)
        return lockError(m) ?: if (pin) AuthError.WrongPin(Lockout.left(m.lock)) else AuthError.WrongBirth(Lockout.left(m.lock))
    }

    override suspend fun check(kmc: String): Res<KmcStatus> =
        Res.Ok(members[kmc]?.status ?: KmcStatus.TIDAK_ADA)

    override suspend fun checkBirth(kmc: String, place: String, birth: String): Res<Unit> {
        val m = members[kmc] ?: return Res.Err(AuthError.NotActive)
        if (m.status == KmcStatus.TERDAFTAR) return Res.Err(AuthError.AlreadyRegistered)
        if (m.status != KmcStatus.AKTIF) return Res.Err(if (m.status == KmcStatus.DIBLOKIR) AuthError.Blocked else AuthError.NotActive)
        lockError(m)?.let { return Res.Err(it) }
        if (Kmc.normalizePlace(place) != m.place || birth != m.birth) return Res.Err(miss(kmc, m, pin = false))
        m.lock = Lockout.State(); save(kmc, m)
        return Res.Ok(Unit)
    }

    override suspend fun register(kmc: String, place: String, birth: String, pin: String, nickname: String): Res<Unit> {
        val m = members[kmc] ?: return Res.Err(AuthError.NotActive)
        when (m.status) {
            KmcStatus.TERDAFTAR -> return Res.Err(AuthError.AlreadyRegistered)
            KmcStatus.DIBLOKIR -> return Res.Err(AuthError.Blocked)
            KmcStatus.TIDAK_ADA -> return Res.Err(AuthError.NotActive)
            KmcStatus.AKTIF -> {}
        }
        lockError(m)?.let { return Res.Err(it) }
        if (Kmc.normalizePlace(place) != m.place || birth != m.birth) return Res.Err(miss(kmc, m, pin = false))
        if (PinRules.problem(pin) != null) return Res.Err(AuthError.WeakPin)
        Nickname.problem(nickname)?.let { return Res.Err(AuthError.Invalid(it)) }
        val nick = Nickname.clean(nickname)
        if (members.any { (k, o) -> k != kmc && o.nickname.equals(nick, ignoreCase = true) }) return Res.Err(AuthError.NicknameTaken)
        m.status = KmcStatus.TERDAFTAR; m.pin = sha256(pin); m.nickname = nick; m.lock = Lockout.State()
        save(kmc, m)
        return Res.Ok(Unit)
    }

    override suspend fun login(kmc: String, pin: String, device: String): Res<Session> {
        val m = members[kmc] ?: return Res.Err(AuthError.NotActive)
        when (m.status) {
            KmcStatus.AKTIF -> return Res.Err(AuthError.NotRegistered)
            KmcStatus.DIBLOKIR -> return Res.Err(AuthError.Blocked)
            KmcStatus.TIDAK_ADA -> return Res.Err(AuthError.NotActive)
            KmcStatus.TERDAFTAR -> {}
        }
        lockError(m)?.let { return Res.Err(it) }
        if (m.pin != sha256(pin)) return Res.Err(miss(kmc, m, pin = true))
        m.lock = Lockout.State()
        if (m.device != null && m.device != device) { save(kmc, m); return Res.Err(AuthError.OtherDevice) }
        val token = sha256("$kmc:$device:${now()}")
        m.device = device; m.token = token; save(kmc, m)
        return Res.Ok(Session(kmc, token, m.nickname ?: ""))
    }

    override suspend fun verify(token: String, device: String): Res<String> {
        val m = members.values.firstOrNull { it.token == token && it.device == device && it.status == KmcStatus.TERDAFTAR }
        return if (m != null) Res.Ok(m.nickname ?: "") else Res.Err(AuthError.Revoked)
    }

    override suspend fun logout(token: String, device: String): Res<Unit> {
        members.forEach { (k, m) -> if (m.token == token && m.device == device) { m.device = null; m.token = null; save(k, m) } }
        return Res.Ok(Unit)
    }

    companion object {
        /** Akun contoh yang ditampilkan di layar saat mode demo. */
        val SAMPLES = listOf(
            "KMC-00001-00" to "Siap daftar · Batulicin, 17 Agustus 2005",
            "KMC-00002-00" to "Sudah terdaftar · PIN 482915",
            "KMC-00003-00" to "Sedang dipakai di HP lain · PIN 482915",
            "KMC-00004-00" to "Diblokir",
        )
    }
}
