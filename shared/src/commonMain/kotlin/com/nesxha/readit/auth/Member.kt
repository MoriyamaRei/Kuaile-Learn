package com.nesxha.readit.auth

import com.nesxha.readit.json.JSONArray
import com.nesxha.readit.json.JSONObject
import com.nesxha.readit.platform.roundHalfUp
import com.nesxha.readit.platform.IoDispatcher

data class Me(
    val kmc: String, val pid: String, val nickname: String, val showKmc: Boolean, val photoVer: Int,
    val streak: Int, val bestKilat: Int, val admin: Boolean, val nickNext: Long,
    val weekKilat: Int = 0, val wins: Int = 0, val title: String? = null, val pins: List<String> = emptyList(),
    val rankP: Int = 0, val peak: Int = 0, val unread: Int = 0,
)

data class RankRow(val rank: Int, val pid: String, val nickname: String, val photoVer: Int, val value: Int, val prev: Int? = null, val title: String? = null)

data class Board(val top: List<RankRow>, val myRank: Int, val myValue: Int, val myPrev: Int? = null, val resetAt: Long = 0)

data class Profile(
    val pid: String, val nickname: String, val kmc: String?, val photoVer: Int,
    val streak: Int, val bestKilat: Int, val since: Long,
    val weekKilat: Int = 0, val wins: Int = 0, val title: String? = null, val pins: List<String> = emptyList(),
    val level: Int = 1, val mastered: Int = 0, val rankP: Int = 0, val peak: Int = 0,
)

/** Status rank sendiri dari server. */
data class RankStatus(
    val p: Int, val protect: Int, val bonusLeft: Int, val seasonEnd: Long,
    val targets: Map<String, Int>, val best: List<Pair<Int, Int>>,
)

/** Hasil Kuis Kilat untuk rank (versi 5: skor dan RP dari kecepatan). */
data class KilatResult(
    val oldP: Int, val newP: Int, val delta: Int, val bonus: Boolean, val score: Int, val minutes: Int,
    val weekBest: Int, val best: Int, val status: RankStatus,
)

/** Satu baris Riwayat Kilat. */
data class KilatGame(
    val id: Long, val at: Long, val minutes: Int, val correct: Int, val wrong: Int, val score: Int,
    val oldP: Int, val newP: Int, val bonus: Boolean, val quit: Boolean, val offline: Boolean = false,
) {
    val total: Int get() = correct + wrong
    val accuracy: Int get() = if (total == 0) 0 else roundHalfUp(correct * 100f / total)
    val delta: Int get() = newP - oldP
}

data class KilatSummary(val games: Int, val best: Int, val accuracy: Int, val weekRp: Int)
data class KilatHistory(val games: List<KilatGame>, val summary: KilatSummary)

/** Coroutine untuk kiriman yang tetap jalan walau layar sudah ditutup (misalnya keluar di tengah Kilat). */
object Background {
    val scope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.SupervisorJob() + com.nesxha.readit.platform.IoDispatcher)
}

/** Hasil satu permainan rank. */
data class GameResult(
    val oldP: Int, val newP: Int, val delta: Int, val base: Int, val bonus: Boolean,
    val verdict: String, val target: Int, val status: RankStatus,
)

data class Mail(
    val id: Long, val kind: String, val title: String, val body: String, val pinned: Boolean,
    val at: Long, val personal: Boolean, val read: Boolean,
)

data class AdminMember(
    val kmc: String, val name: String, val status: String, val nickname: String?, val online: Boolean,
    val admin: Boolean, val lock: String, val photo: Boolean, val lastSeen: Long,
)

enum class BoardKind(val key: String, val title: String, val unit: String) {
    STREAK("streak", "Streak Terpanjang", "Hari"),
    KILAT("kilat", "Kilat Mingguan", "Poin"),
    RANK("rank", "Rank", "RP"),
}

/** Fitur anggota yang butuh server: profil, foto, leaderboard, ganti PIN, dan admin. */
object Member {
    private fun JSONObject.str(k: String): String? = if (isNull(k)) null else optString(k)

    private suspend fun session(block: suspend (SupabaseApi, String, String) -> Res<JSONObject>): Res<JSONObject> {
        val api = Auth.api as? SupabaseApi
            ?: return Res.Err(AuthError.Invalid("Fitur ini butuh server, sedangkan aplikasi masih Mode Demo."))
        val s = Auth.currentSession() ?: return Res.Err(AuthError.Revoked)
        if (!Auth.hasInternet()) return Res.Err(AuthError.Network)
        val r = block(api, s.token, Auth.deviceId)
        if (r is Res.Err && r.error == AuthError.Revoked) Auth.forceSignOut()
        return r
    }

    private suspend fun rpc(fn: String, extra: JSONObject.() -> Unit = {}): Res<JSONObject> = session { api, token, device ->
        api.rpc(fn, JSONObject().put("p_token", token).put("p_device", device).apply(extra))
    }

    private inline fun <T> Res<JSONObject>.to(f: (JSONObject) -> T): Res<T> = when (this) {
        is Res.Err -> this
        is Res.Ok -> Res.Ok(f(value))
    }

    /** Server tersedia (bukan Mode Demo). */
    val available: Boolean get() = com.nesxha.readit.platform.Accounts.ON && Auth.api is SupabaseApi

    private fun pins(j: JSONObject) = j.str("pins")?.split(',')?.map { it.trim() }?.filter { it.isNotEmpty() }.orEmpty()

    suspend fun me(): Res<Me> = rpc("kmc_me").to {
        Me(it.getString("kmc"), it.getString("pid"), it.optString("nickname"), it.optBoolean("show_kmc"), it.optInt("photo_ver"),
            it.optInt("streak"), it.optInt("best_kilat"), it.optBoolean("admin"), it.optLong("nick_next"),
            it.optInt("week_kilat"), it.optInt("wins"), it.str("title"), pins(it), it.optInt("rank_p"), it.optInt("peak"), it.optInt("unread"))
    }

    suspend fun setNickname(nick: String): Res<Unit> =
        rpc("kmc_set_profile") { put("p_nickname", nick); put("p_show_kmc", JSONObject.NULL) }.to { Auth.updateNickname(Nickname.clean(nick)) }

    suspend fun setShowKmc(show: Boolean): Res<Unit> =
        rpc("kmc_set_profile") { put("p_nickname", JSONObject.NULL); put("p_show_kmc", show) }.to { }

    /** Mengunggah foto (base64 JPEG). Kirim null untuk menghapus. */
    suspend fun setPhoto(thumb: String?, full: String?): Res<Int> =
        rpc("kmc_set_photo") { put("p_thumb", thumb ?: JSONObject.NULL); put("p_full", full ?: JSONObject.NULL) }.to { it.optInt("photo_ver") }

    /** Foto anggota: Ok(null) kalau tidak punya foto. */
    suspend fun photo(pid: String, full: Boolean): Res<String?> =
        rpc("kmc_photo") { put("p_pid", pid); put("p_full", full) }.to { it.str("image") }

    suspend fun profile(pid: String): Res<Profile> = rpc("kmc_profile") { put("p_pid", pid) }.to {
        Profile(it.getString("pid"), it.optString("nickname"), it.str("kmc"), it.optInt("photo_ver"),
            it.optInt("streak"), it.optInt("best_kilat"), it.optLong("since"),
            it.optInt("week_kilat"), it.optInt("wins"), it.str("title"), pins(it), it.optInt("level", 1), it.optInt("mastered"),
            it.optInt("rank_p"), it.optInt("peak"))
    }

    suspend fun setStyle(title: String?, pins: List<String>): Res<Unit> =
        rpc("kmc_set_style") { put("p_title", title ?: JSONObject.NULL); put("p_pins", pins.joinToString(",")) }.to { }

    /** Sinkron progres belajar (lihat supabase/bagian-11.sql). */
    suspend fun progressSync(data: String?, gen: Int, reset: Boolean, claim: Boolean, since: Long): Res<JSONObject> =
        rpc("kmc_progress_sync") {
            put("p_data", data ?: JSONObject.NULL); put("p_gen", gen); put("p_reset", reset)
            put("p_claim", claim); put("p_since", since)
        }

    suspend fun syncStats(level: Int, mastered: Int): Res<Unit> =
        rpc("kmc_sync_stats") { put("p_level", level); put("p_mastered", mastered) }.to { }

    private fun rankStatus(j: JSONObject): RankStatus {
        val t = j.optJSONObject("targets") ?: JSONObject()
        val b = j.optJSONArray("best") ?: JSONArray()
        return RankStatus(
            j.optInt("p"), j.optInt("protect"), j.optInt("bonus_left"), j.optLong("season_end"),
            t.keys().asSequence().associateWith { t.optInt(it) },
            (0 until b.length()).map { b.getJSONObject(it).let { o -> o.optInt("season") to o.optInt("peak") } },
        )
    }

    suspend fun rank(): Res<RankStatus> = rpc("kmc_rank").to { rankStatus(it) }

    /** Hasil Kuis Kilat: menghitung skor dan RP dari kecepatan, mengisi papan Kilat Mingguan dan Riwayat. */
    suspend fun kilatPlay(minutes: Int, t: com.nesxha.readit.data.RankRules.KilatTally, quit: Boolean): Res<KilatResult> =
        rpc("kmc_kilat_play") {
            put("p_minutes", minutes); put("p_fast", t.fast); put("p_good", t.good); put("p_ok", t.ok); put("p_slow", t.slow)
            put("p_wrong", t.wrong); put("p_quit", quit)
        }.to {
            KilatResult(it.optInt("old_p"), it.optInt("new_p"), it.optInt("delta"), it.optBoolean("bonus"), it.optInt("score"),
                it.optInt("minutes"), it.optInt("week_best"), it.optInt("best"), rankStatus(it))
        }

    /** Riwayat Kilat: 30 permainan per halaman; [before] = id terakhir yang sudah dimuat. */
    suspend fun kilatHistory(before: Long?): Res<KilatHistory> =
        rpc("kmc_kilat_history") { put("p_before", before ?: JSONObject.NULL) }.to {
            val a = it.optJSONArray("games") ?: JSONArray()
            val games = (0 until a.length()).map { i ->
                val o = a.getJSONObject(i)
                KilatGame(o.optLong("id"), o.optLong("at"), o.optInt("minutes"), o.optInt("correct"), o.optInt("wrong"), o.optInt("score"),
                    o.optInt("old_p"), o.optInt("new_p"), o.optBoolean("bonus"), o.optBoolean("quit"))
            }
            val s = it.optJSONObject("summary") ?: JSONObject()
            KilatHistory(games, KilatSummary(s.optInt("games"), s.optInt("best"), s.optInt("accuracy"), s.optInt("week_rp")))
        }

    /** Mengirim hasil permainan rank lama (tidak dipakai lagi). */
    suspend fun game(game: String, score: Int, seconds: Int, quit: Boolean): Res<GameResult> =
        rpc("kmc_game") { put("p_game", game); put("p_score", score); put("p_seconds", seconds); put("p_quit", quit) }.to {
            GameResult(it.optInt("old_p"), it.optInt("new_p"), it.optInt("delta"), it.optInt("base"), it.optBoolean("bonus"),
                it.optString("verdict"), it.optInt("target"), rankStatus(it))
        }

    suspend fun mail(): Res<List<Mail>> = rpc("kmc_mail").to {
        val a = it.optJSONArray("mail") ?: JSONArray()
        (0 until a.length()).map { i ->
            val o = a.getJSONObject(i)
            Mail(o.optLong("id"), o.optString("kind"), o.optString("title"), o.optString("body"), o.optBoolean("pinned"),
                o.optLong("at"), o.optBoolean("personal"), o.optBoolean("read"))
        }
    }

    /** Tandai surat dibaca; [id] null = semua. */
    suspend fun mailRead(id: Long?): Res<Unit> = rpc("kmc_mail_read") { put("p_id", id ?: JSONObject.NULL) }.to { }

    suspend fun adminMail(kind: String, title: String, body: String, target: String?, pinned: Boolean): Res<String> =
        rpc("kmc_admin_mail") {
            put("p_kind", kind); put("p_title", title); put("p_body", body)
            put("p_target", target ?: JSONObject.NULL); put("p_pinned", pinned)
        }.to { it.optString("message") }

    suspend fun adminMailDelete(id: Long): Res<String> = rpc("kmc_admin_mail_delete") { put("p_id", id) }.to { it.optString("message") }

    suspend fun board(kind: BoardKind): Res<Board> = rpc("kmc_leaderboard") { put("p_board", kind.key) }.to {
        val arr = it.optJSONArray("top") ?: JSONArray()
        val rows = (0 until arr.length()).map { i ->
            val o = arr.getJSONObject(i)
            RankRow(o.optInt("rank"), o.getString("pid"), o.optString("nickname"), o.optInt("photo_ver"), o.optInt("value"),
                if (o.isNull("prev")) null else o.optInt("prev"), o.str("title"))
        }
        val me = it.optJSONObject("me")
        Board(rows, me?.optInt("rank") ?: 0, me?.optInt("value") ?: 0,
            me?.let { m -> if (m.isNull("prev") || !m.has("prev")) null else m.optInt("prev") }, if (it.isNull("reset_at")) 0 else it.optLong("reset_at"))
    }

    /** Mengirim skor Kuis Kilat. Hanya terkirim kalau sedang online. */
    suspend fun submitKilat(score: Int): Res<Int> = rpc("kmc_submit_kilat") { put("p_score", score) }.to { it.optInt("best") }

    suspend fun changePin(old: String, new: String): Res<Unit> =
        rpc("kmc_change_pin") { put("p_old", old); put("p_new", new) }.to { }

    // ---------------- Admin ----------------

    suspend fun adminList(): Res<List<AdminMember>> = rpc("kmc_admin") { put("p_action", "list"); put("p_kmc", JSONObject.NULL) }.to {
        val arr = it.optJSONArray("members") ?: JSONArray()
        (0 until arr.length()).map { i ->
            val o = arr.getJSONObject(i)
            AdminMember(o.getString("kmc"), o.optString("name"), o.optString("status"), o.str("nickname"), o.optBoolean("online"),
                o.optBoolean("admin"), o.optString("lock"), o.optBoolean("photo"), if (o.isNull("last_seen")) 0 else o.optLong("last_seen"))
        }
    }

    /** [action]: add, reset_pin, reset_device, unlock, block, unblock, remove_photo, reset_nick, delete. */
    suspend fun admin(action: String, kmc: String, name: String? = null, place: String? = null, birth: String? = null): Res<String> =
        rpc("kmc_admin") {
            put("p_action", action); put("p_kmc", kmc)
            put("p_name", name ?: JSONObject.NULL); put("p_place", place ?: JSONObject.NULL); put("p_birth", birth ?: JSONObject.NULL)
        }.to { it.optString("message") }
}

/** Membaca CSV anggota: kmc, nama, tempat_lahir, tanggal_lahir (yyyy-MM-dd atau dd/MM/yyyy). Baris judul dilewati. */
object MemberCsv {
    data class Row(val kmc: String, val name: String, val place: String, val birth: String)

    fun parse(text: String): Pair<List<Row>, List<String>> {
        val rows = ArrayList<Row>(); val bad = ArrayList<String>()
        text.lineSequence().map { it.trim().removePrefix("﻿") }.filter { it.isNotEmpty() }.forEachIndexed { i, line ->
            val sep = if (line.count { it == ';' } > line.count { it == ',' }) ';' else ','
            val c = split(line, sep)
            val kmc = c.getOrNull(0)?.let { Kmc.normalize(it) }
            val birth = c.getOrNull(3)?.let { date(it) }
            if (kmc == null || birth == null || c.size < 4 || c[1].isBlank() || c[2].isBlank()) {
                if (i > 0 || !line.lowercase().contains("kmc")) bad.add("Baris ${i + 1}: $line")
            } else rows.add(Row(kmc, c[1].trim(), c[2].trim(), birth))
        }
        return rows to bad
    }

    private fun split(line: String, sep: Char): List<String> {
        val out = ArrayList<String>(); val sb = StringBuilder(); var q = false
        for (ch in line) when {
            ch == '"' -> q = !q
            ch == sep && !q -> { out.add(sb.toString().trim()); sb.clear() }
            else -> sb.append(ch)
        }
        out.add(sb.toString().trim())
        return out
    }

    /** "2005-08-17", "17/08/2005", "17-08-2005" → "2005-08-17". */
    fun date(s: String): String? {
        val t = s.trim()
        Regex("^(\\d{4})-(\\d{1,2})-(\\d{1,2})$").find(t)?.let { m ->
            val (y, mo, d) = m.destructured; return valid(y.toInt(), mo.toInt(), d.toInt())
        }
        Regex("^(\\d{1,2})[/.-](\\d{1,2})[/.-](\\d{4})$").find(t)?.let { m ->
            val (d, mo, y) = m.destructured; return valid(y.toInt(), mo.toInt(), d.toInt())
        }
        return null
    }

    private fun valid(y: Int, m: Int, d: Int): String? = runCatching { kotlinx.datetime.LocalDate(y, m, d).toString() }.getOrNull()
}
