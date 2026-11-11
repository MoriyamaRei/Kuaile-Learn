package com.nesxha.readit.util

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.nesxha.readit.MainActivity
import com.nesxha.readit.R
import com.nesxha.readit.auth.Auth
import com.nesxha.readit.auth.Member
import com.nesxha.readit.auth.Res
import java.util.concurrent.TimeUnit

/** Mengecek surat baru beberapa jam sekali (tanpa Firebase) dan memberi notifikasi. */
class MailWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {
    override suspend fun doWork(): Result {
        com.nesxha.readit.AndroidPlatform.installBase(applicationContext)
        if (Auth.currentSession() == null || !Member.available) return Result.success()
        val me = (Member.me() as? Res.Ok)?.value ?: return Result.success()
        val prefs = applicationContext.getSharedPreferences("profil", Context.MODE_PRIVATE)
        val last = prefs.getInt("notifiedUnread", 0)
        if (me.unread > last) show(applicationContext, me.unread)
        prefs.edit().putInt("notifiedUnread", me.unread).apply()
        return Result.success()
    }

    companion object {
        private const val CHANNEL = "surat"
        private const val WORK = "cek_surat"

        fun schedule(ctx: Context) {
            val ch = NotificationChannel(CHANNEL, "Kotak Surat", NotificationManager.IMPORTANCE_DEFAULT)
            ch.description = "Pengumuman dan info dari pengurus KMC"
            ctx.getSystemService(NotificationManager::class.java).createNotificationChannel(ch)
            val req = PeriodicWorkRequestBuilder<MailWorker>(4, TimeUnit.HOURS)
                .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()).build()
            WorkManager.getInstance(ctx).enqueueUniquePeriodicWork(WORK, ExistingPeriodicWorkPolicy.KEEP, req)
        }

        fun show(ctx: Context, unread: Int) {
            if (Build.VERSION.SDK_INT >= 33 &&
                ContextCompat.checkSelfPermission(ctx, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
            ) return
            val pi = PendingIntent.getActivity(
                ctx, 7, Intent(ctx, MainActivity::class.java).putExtra(MainActivity.EXTRA_OPEN, "mail")
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP),
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
            )
            val n = NotificationCompat.Builder(ctx, CHANNEL)
                .setSmallIcon(R.drawable.ic_notif)
                .setContentTitle("📬 Kamu Punya Surat Baru")
                .setContentText(if (unread == 1) "Ada 1 surat belum dibaca dari Kuaile Learn." else "Ada $unread surat belum dibaca dari Kuaile Learn.")
                .setContentIntent(pi).setAutoCancel(true).build()
            runCatching { NotificationManagerCompat.from(ctx).notify(1002, n) }
        }
    }
}
