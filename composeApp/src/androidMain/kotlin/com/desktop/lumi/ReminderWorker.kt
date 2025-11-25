package com.desktop.lumi.db.com.desktop.lumi

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.Worker
import androidx.work.WorkerParameters

class ReminderWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val hour = inputData.getInt("hour", 8)
        val minute = inputData.getInt("minute", 30)
        val message = inputData.getString("message") ?: "Time to reflect today 💭"
        
        showNotification("Lumi ❤️", message)
        
        // Reschedule for next day
        val scheduler = NotificationScheduler(applicationContext)
        scheduler.rescheduleReminder(hour, minute, message)

        return Result.success()
    }

    private fun showNotification(title: String, msg: String) {
        val channelId = "lumi_channel"

        val manager = ContextCompat.getSystemService(
            applicationContext,
            NotificationManager::class.java
        )!!

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Daily Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle(title)
            .setContentText(msg)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setAutoCancel(true)
            .build()

        manager.notify(1234, notification)
        return
    }
}
