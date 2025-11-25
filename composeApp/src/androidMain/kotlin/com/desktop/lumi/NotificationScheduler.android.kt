package com.desktop.lumi.db.com.desktop.lumi

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit
import java.util.Calendar


actual class NotificationScheduler(private val context: Context) {

    actual fun scheduleDailyReminder(hour: Int, minute: Int, message: String) {
        // Cancel any existing work first
        cancelDailyReminder()
        
        scheduleNextReminder(hour, minute, message)
    }
    
    private fun scheduleNextReminder(hour: Int, minute: Int, message: String) {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        var delay = target.timeInMillis - now.timeInMillis
        if (delay <= 0) delay += TimeUnit.DAYS.toMillis(1)

        // Pass hour and minute to worker so it can reschedule itself
        val inputData = Data.Builder()
            .putInt("hour", hour)
            .putInt("minute", minute)
            .putString("message", message)
            .build()

        val request = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(inputData)
            .addTag("daily_reminder")
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "daily_reminder",
            ExistingWorkPolicy.REPLACE,
            request
        )
    }
    
    fun rescheduleReminder(hour: Int, minute: Int, message: String) {
        scheduleNextReminder(hour, minute, message)
    }

    actual fun cancelDailyReminder() {
        WorkManager.getInstance(context).cancelAllWorkByTag("daily_reminder")
    }
}
