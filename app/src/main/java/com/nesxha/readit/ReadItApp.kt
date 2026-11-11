package com.nesxha.readit

import android.app.Application
import com.nesxha.readit.util.MailWorker
import com.nesxha.readit.util.Reminder

class ReadItApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AndroidPlatform.installBase(this)
        Reminder.createChannel(this)
        Reminder.schedule(this)
        MailWorker.schedule(this)
    }
}
