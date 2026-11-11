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
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.nesxha.readit.MainActivity
import com.nesxha.readit.R
import com.nesxha.readit.data.Store
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

object Reminder {
    const val CHANNEL = "pengingat"
    private const val WORK = "pengingat_harian"

    fun createChannel(ctx: Context) {
        val ch = NotificationChannel(CHANNEL, "Pengingat Harian", NotificationManager.IMPORTANCE_DEFAULT)
        ch.description = "Pengingat untuk belajar kosakata setiap hari"
        ctx.getSystemService(NotificationManager::class.java).createNotificationChannel(ch)
    }

    /** Jadwalkan (atau batalkan) pengingat berikutnya sesuai pengaturan. */
    fun schedule(ctx: Context) {
        val s = Store.now.settings
        val wm = WorkManager.getInstance(ctx)
        if (!s.reminderOn) { wm.cancelUniqueWork(WORK); return }
        val now = LocalDateTime.now()
        var next = now.withHour(s.reminderHour).withMinute(s.reminderMinute).withSecond(0).withNano(0)
        if (!next.isAfter(now.plusSeconds(30))) next = next.plusDays(1)
        val delay = Duration.between(now, next).toMillis()
        val req = OneTimeWorkRequestBuilder<ReminderWorker>().setInitialDelay(delay, TimeUnit.MILLISECONDS).build()
        wm.enqueueUniqueWork(WORK, ExistingWorkPolicy.REPLACE, req)
    }

    fun show(ctx: Context) {
        if (Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(ctx, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) return
        val st = Store.now
        val left = st.remainingToday
        val text = when {
            st.goalReached -> "Target hari ini sudah tercapai. Mau tambah satu Kuis Kilat?"
            left > 0 -> "Ada $left kartu menunggumu hari ini. Yuk, 5 menit saja!"
            else -> "Yuk, buka Kuaile Learn dan pelajari kata baru hari ini."
        }
        val streak = st.streak
        val title = when {
            st.brokenStreak > 0 -> "🕯️ Streak Padam! Pulihkan Hari Ini"
            streak > 0 -> "🔥 Jaga Streak $streak Hari!"
            else -> "📚 Waktunya Belajar Hanzi"
        }
        val pi = PendingIntent.getActivity(
            ctx, 0, Intent(ctx, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )
        val n = NotificationCompat.Builder(ctx, CHANNEL)
            .setSmallIcon(R.drawable.ic_notif)
            .setContentTitle(title)
            .setContentText(text)
            .setContentIntent(pi)
            .setAutoCancel(true)
            .build()
        runCatching { NotificationManagerCompat.from(ctx).notify(1001, n) }
    }
}

class ReminderWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {
    override suspend fun doWork(): Result {
        com.nesxha.readit.AndroidPlatform.installBase(applicationContext)
        Reminder.show(applicationContext)
        Reminder.schedule(applicationContext)
        return Result.success()
    }
}
