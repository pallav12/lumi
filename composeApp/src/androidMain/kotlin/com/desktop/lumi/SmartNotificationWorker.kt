package com.desktop.lumi


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

class SmartNotificationWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val title = inputData.getString("title") ?: "Lumi"
        val message = inputData.getString("message") ?: "Time to check in."
        val type = inputData.getString("type") ?: "general" // streak, orbit, void, weekly

        showNotification(title, message, type)
        return Result.success()
    }

    private fun showNotification(title: String, msg: String, type: String) {
        val channelId = "lumi_smart_notifications"
        val manager = ContextCompat.getSystemService(applicationContext, NotificationManager::class.java)!!

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Lumi Updates",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            manager.createNotificationChannel(channel)
        }

        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigation_route", type) // Pass type to navigate to correct screen
        }

        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            type.hashCode(), // Unique request code per type
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle(title)
            .setContentText(msg)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        manager.notify(type.hashCode(), notification)
    }
}