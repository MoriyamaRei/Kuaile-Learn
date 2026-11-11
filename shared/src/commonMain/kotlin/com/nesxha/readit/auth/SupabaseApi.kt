package com.nesxha.readit.auth

import kotlinx.coroutines.withContext
import com.nesxha.readit.json.JSONObject
import com.nesxha.readit.platform.Plat
import kotlin.concurrent.Volatile
import com.nesxha.readit.platform.IoDispatcher

/**
 * Klien server Supabase. Aplikasi hanya memanggil fungsi (RPC) di supabase/schema.sql;
 * tabel anggota tidak bisa dibaca atau diubah langsung.
 * [hasInternet] dipakai untuk membedakan "HP tidak ada internet" dan "server bermasalah".
 */
class SupabaseApi(
    private val baseUrl: String,
    private val key: String,
    private val hasInternet: () -> Boolean,
) : AuthApi {
    override val isDemo = false

    companion object {
        /** Keterangan teknis error server terakhir (untuk ditampilkan kecil di layar, membantu pengurus mencari penyebab). */
        @Volatile var lastError: String? = null
    }

    internal suspend fun call(fn: String, body: JSONObject): Res<JSONObject> = withContext(IoDispatcher) {
        val headers = buildMap {
            put("apikey", key)
            // Key lama (JWT anon) juga dikirim sebagai Bearer; key baru sb_publishable_ cukup di header apikey.
            if (!key.startsWith("sb_")) put("Authorization", "Bearer $key")
            put("Content-Type", "application/json")
        }
        val res = try {
            Plat.p.http("POST", "${baseUrl.trimEnd('/')}/rest/v1/rpc/$fn", headers, body.toString())
        } catch (e: Throwable) {
            lastError = "$fn · ${e::class.simpleName} · ${e.message.orEmpty()}".take(300)
            return@withContext Res.Err(if (hasInternet()) AuthError.Server else AuthError.Network)
        }
        try {
            val code = res.code
            val text = res.body
            if (code !in 200..299) {
                val detail = runCatching { JSONObject(text).let { j -> listOf(j.optString("code"), j.optString("message"), j.optString("hint")).filter { it.isNotBlank() }.joinToString(" · ") } }
                    .getOrNull().orEmpty().ifBlank { text.take(160) }
                lastError = "$fn · HTTP $code · $detail".take(300)
                return@withContext Res.Err(AuthError.Server)
            }
            Res.Ok(JSONObject(text))
        } catch (e: Exception) {
            lastError = "$fn · ${e::class.simpleName} · ${e.message.orEmpty()}".take(300)
            Res.Err(AuthError.Server)
        }
    }

    /** Mengubah {ok:false, error:"..."} dari server menjadi [AuthError]. */
    private fun error(j: JSONObject): AuthError = when (j.optString("error")) {
        "NOT_ACTIVE" -> AuthError.NotActive
        "ALREADY_REGISTERED" -> AuthError.AlreadyRegistered
        "NOT_REGISTERED" -> AuthError.NotRegistered
        "WRONG_BIRTH" -> AuthError.WrongBirth(j.optInt("left", 0))
        "WRONG_PIN" -> AuthError.WrongPin(j.optInt("left", 0))
        "LOCKED" -> AuthError.Locked(j.optLong("until", 0))
        "LOCKED_FOREVER" -> AuthError.LockedForever
        "OTHER_DEVICE" -> if (com.nesxha.readit.platform.Plat.ready && com.nesxha.readit.platform.Plat.p.isWeb) AuthError.OtherBrowser else AuthError.OtherDevice
        "BLOCKED" -> AuthError.Blocked
        "NICKNAME_TAKEN" -> AuthError.NicknameTaken
        "WEAK_PIN" -> AuthError.WeakPin
        "REVOKED" -> AuthError.Revoked
        "NICK_WAIT" -> AuthError.NickWait(j.optLong("until", 0))
        else -> AuthError.Invalid(j.optString("message").ifEmpty { "Data tidak valid." })
    }

    /** Memanggil fungsi server dan mengubah {ok:false} menjadi [Res.Err]. */
    internal suspend fun rpc(fn: String, body: JSONObject): Res<JSONObject> = map(call(fn, body)) { it }

    private inline fun <T> map(r: Res<JSONObject>, ok: (JSONObject) -> T): Res<T> = when (r) {
        is Res.Err -> r
        is Res.Ok -> if (r.value.optBoolean("ok")) Res.Ok(ok(r.value)) else Res.Err(error(r.value))
    }

    override suspend fun check(kmc: String): Res<KmcStatus> =
        when (val r = call("kmc_check", JSONObject().put("p_kmc", kmc))) {
            is Res.Err -> r
            is Res.Ok -> Res.Ok(when (r.value.optString("status")) {
                "aktif" -> KmcStatus.AKTIF
                "terdaftar" -> KmcStatus.TERDAFTAR
                "diblokir" -> KmcStatus.DIBLOKIR
                else -> KmcStatus.TIDAK_ADA
            })
        }

    override suspend fun checkBirth(kmc: String, place: String, birth: String): Res<Unit> =
        map(call("kmc_check_birth", JSONObject().put("p_kmc", kmc).put("p_place", place).put("p_birth", birth))) { }

    override suspend fun register(kmc: String, place: String, birth: String, pin: String, nickname: String): Res<Unit> =
        map(call("kmc_register", JSONObject().put("p_kmc", kmc).put("p_place", place).put("p_birth", birth)
            .put("p_pin", pin).put("p_nickname", nickname))) { }

    override suspend fun login(kmc: String, pin: String, device: String): Res<Session> =
        map(call("kmc_login", JSONObject().put("p_kmc", kmc).put("p_pin", pin).put("p_device", device))) {
            Session(kmc, it.getString("token"), it.optString("nickname"))
        }

    override suspend fun verify(token: String, device: String): Res<String> =
        map(call("kmc_verify", JSONObject().put("p_token", token).put("p_device", device))) { it.optString("nickname") }

    override suspend fun logout(token: String, device: String): Res<Unit> =
        map(call("kmc_logout", JSONObject().put("p_token", token).put("p_device", device))) { }
}
