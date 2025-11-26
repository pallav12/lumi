package com.desktop.lumi.db.com.desktop.lumi

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.desktop.lumi.MainActivity // Ensure this import is correct for your project

class ReminderWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val hour = inputData.getInt("hour", 20)
        val minute = inputData.getInt("minute", 0)
        val message = inputData.getString("message") ?: "Time to reflect today 💭"

        showNotification("Lumi ❤️", message)

        // Reschedule for next day
        val scheduler = NotificationScheduler(applicationContext)
        scheduler.rescheduleReminder(hour, minute, message)

        return Result.success()
    }

    private fun showNotification(title: String, msg: String) {
        val channelId = "lumi_daily_reminder" // Consistent Channel ID

        val manager = ContextCompat.getSystemService(
            applicationContext,
            NotificationManager::class.java
        )!!

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Daily Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Daily reminders to reflect on your relationship"
            }
            manager.createNotificationChannel(channel)
        }

        // Create an Intent to open the main activity
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle(title)
            .setContentText(msg)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Replace with your app icon e.g. R.drawable.ic_notification
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent) // Set the intent to fire when clicked
            .setAutoCancel(true) // Remove notification when clicked
            .build()

        manager.notify(1001, notification) // Use a consistent ID for daily reminders
    }
}