package com.nesxha.readit.data

import com.nesxha.readit.platform.Prefs
import com.nesxha.readit.auth.KilatGame
import com.nesxha.readit.json.JSONArray
import com.nesxha.readit.json.JSONObject
import com.nesxha.readit.platform.nowMs

/**
 * Riwayat Kilat yang dimainkan saat offline (tidak dihitung untuk rank).
 * Disimpan di HP saja, maksimal 30 permainan terakhir, lalu digabung dengan riwayat dari server.
 */
object KilatLocal {
    private const val KEY = "games"
    private val prefs by lazy { Prefs("kilat_lokal") }

    fun load(): List<KilatGame> = runCatching {
        val a = JSONArray(prefs.getString(KEY, "[]") ?: "[]")
        (0 until a.length()).map { i ->
            val o = a.getJSONObject(i)
            KilatGame(-o.optLong("at"), o.optLong("at"), o.optInt("minutes"), o.optInt("correct"), o.optInt("wrong"), o.optInt("score"),
                0, 0, bonus = false, quit = o.optBoolean("quit"), offline = true)
        }
    }.getOrDefault(emptyList())

    fun add(minutes: Int, correct: Int, wrong: Int, score: Int, quit: Boolean) {
        val list = JSONArray()
        list.put(JSONObject().put("at", nowMs()).put("minutes", minutes).put("correct", correct)
            .put("wrong", wrong).put("score", score).put("quit", quit))
        runCatching { JSONArray(prefs.getString(KEY, "[]") ?: "[]") }.getOrNull()?.let { old ->
            for (i in 0 until minOf(old.length(), 29)) list.put(old.getJSONObject(i))
        }
        prefs.edit().putString(KEY, list.toString()).apply()
    }
}
