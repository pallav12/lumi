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
        // Cancel any existing work first to avoid duplicates
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

        // If target time is in the past, schedule for tomorrow
        if (target.before(now)) {
            target.add(Calendar.DAY_OF_YEAR, 1)
        }

        val delay = target.timeInMillis - now.timeInMillis

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

        // Use UNIQUE work to prevent duplicate alarms
        WorkManager.getInstance(context).enqueueUniqueWork(
            "daily_reminder_work",
            ExistingWorkPolicy.REPLACE,
            request
        )
    }

    // Helper for the Worker to call
    fun rescheduleReminder(hour: Int, minute: Int, message: String) {
        scheduleNextReminder(hour, minute, message)
    }

    actual fun cancelDailyReminder() {
        WorkManager.getInstance(context).cancelUniqueWork("daily_reminder_work")
    }
}