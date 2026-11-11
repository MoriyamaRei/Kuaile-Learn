package com.nesxha.readit.util

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Pembaruan aplikasi lewat repo publik "etalase" (GitHub Releases).
 * Android tidak mengizinkan pemasangan diam-diam, jadi anggota tetap menekan "Pasang".
 */
typealias Release = Releases.Release

object Updater {
    /** Rilis disimpan di kode bersama ([Releases]); di sini hanya bagian unduh & pasang APK. */
    fun parseTag(tag: String): Pair<Int, String>? = Releases.parseTag(tag)
    suspend fun newer(): Release? = Releases.newer()

    fun canInstall(ctx: Context): Boolean = Build.VERSION.SDK_INT < 26 || ctx.packageManager.canRequestPackageInstalls()

    fun openInstallSettings(ctx: Context) {
        ctx.startActivity(Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:" + ctx.packageName)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    /** Mengunduh APK (melaporkan progres 0..1), lalu membuka layar pemasangan Android. */
    suspend fun downloadAndInstall(ctx: Context, r: Release, onProgress: (Float) -> Unit): Boolean {
        val url = r.apkUrl ?: return false
        val dir = ctx.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) ?: return false
        val file = File(dir, "KuaileLearn-${r.code}.apk")
        dir.listFiles()?.filter { it.name.endsWith(".apk") && it != file }?.forEach { it.delete() }
        if (!file.exists() || file.length() == 0L) {
            val dm = ctx.getSystemService(DownloadManager::class.java) ?: return false
            val id = dm.enqueue(
                DownloadManager.Request(Uri.parse(url)).setTitle("Pembaruan Kuaile Learn").setDescription(r.name)
                    .setDestinationUri(Uri.fromFile(file)).setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE),
            )
            while (true) {
                delay(400)
                val c = dm.query(DownloadManager.Query().setFilterById(id)) ?: return false
                val (status, done, total) = c.use {
                    if (!it.moveToFirst()) return false
                    Triple(
                        it.getInt(it.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS)),
                        it.getLong(it.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)),
                        it.getLong(it.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)),
                    )
                }
                if (total > 0) onProgress(done.toFloat() / total)
                if (status == DownloadManager.STATUS_SUCCESSFUL) break
                if (status == DownloadManager.STATUS_FAILED) { file.delete(); return false }
            }
        }
        onProgress(1f)
        val uri = FileProvider.getUriForFile(ctx, ctx.packageName + ".files", file)
        ctx.startActivity(
            Intent(Intent.ACTION_VIEW).setDataAndType(uri, "application/vnd.android.package-archive")
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK),
        )
        return true
    }
}
