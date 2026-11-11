package com.nesxha.readit.util

import com.nesxha.readit.AppInfo
import com.nesxha.readit.json.JSONArray
import com.nesxha.readit.json.JSONObject
import com.nesxha.readit.platform.Plat
import kotlinx.datetime.Instant

/**
 * Daftar rilis dari repo publik "etalase" (GitHub Releases).
 * Dipakai untuk "Info Update" di Kotak Surat dan pengecekan versi baru (APK di Android).
 */
object Releases {
    const val REPO = "MoriyamaRei/Kuaile-Learn-Update"

    /**
     * [code] = nomor build, selalu angka terakhir tag (v1.0.34 → 34, v1.1.1.35 → 35).
     * [version] = nomor versi yang tampil (v1.0.34 → 1.0.34, v1.1.1.35 → 1.1.1).
     * [mandatory] kalau catatan rilis memuat "[WAJIB]".
     */
    data class Release(val code: Int, val version: String, val name: String, val notes: String, val apkUrl: String?, val at: Long, val mandatory: Boolean)

    /** Memecah tag rilis menjadi (nomor build, versi tampil). */
    fun parseTag(tag: String): Pair<Int, String>? {
        val parts = tag.removePrefix("v").split('.')
        val code = parts.lastOrNull()?.toIntOrNull() ?: return null
        val version = if (parts.size >= 4) parts.dropLast(1).joinToString(".") else parts.joinToString(".")
        return code to version
    }

    private fun parse(o: JSONObject): Release? {
        val tag = o.optString("tag_name")
        val (code, version) = parseTag(tag) ?: return null
        val assets = o.optJSONArray("assets") ?: JSONArray()
        val apk = (0 until assets.length()).map { assets.getJSONObject(it) }.firstOrNull { it.optString("name").endsWith(".apk") }
        val body = o.optString("body")
        val at = runCatching { Instant.parse(o.optString("published_at")).toEpochMilliseconds() }.getOrDefault(0L)
        return Release(code, version, o.optString("name").ifEmpty { tag }, body.replace("[WAJIB]", "").trim(),
            apk?.optString("browser_download_url"), at, body.contains("[WAJIB]"))
    }

    /** Beberapa rilis terakhir. Kosong kalau gagal. */
    suspend fun recent(): List<Release> = runCatching {
        val r = Plat.p.http("GET", "https://api.github.com/repos/$REPO/releases?per_page=8", mapOf("Accept" to "application/vnd.github+json"), null)
        if (r.code !in 200..299) return emptyList()
        JSONArray(r.body).let { a -> (0 until a.length()).mapNotNull { parse(a.getJSONObject(it)) } }
    }.getOrDefault(emptyList())

    /** Rilis APK terbaru kalau lebih baru dari versi yang terpasang. */
    suspend fun newer(): Release? {
        // Pilih nomor versi tertinggi (bukan tanda "latest"), karena build bisa selesai tidak berurutan.
        val r = recent().filter { it.apkUrl != null }.maxByOrNull { it.code }
        return r?.takeIf { it.code > AppInfo.VERSION_CODE }
    }
}
