package com.desktop.lumi.db.com.desktop.lumi

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.desktop.lumi.SmartNotificationWorker
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

    actual fun scheduleOrbitCheckIn(delayMinutes: Long) {
        val inputData = Data.Builder()
            .putString("title", "Orbit Update 🪐")
            .putString("message", "Halfway there. You are doing great.")
            .putString("type", "orbit")
            .build()

        val request = OneTimeWorkRequestBuilder<SmartNotificationWorker>()
            .setInitialDelay(delayMinutes, TimeUnit.MINUTES)
            .setInputData(inputData)
            .addTag("orbit_notification")
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "orbit_checkin",
            ExistingWorkPolicy.REPLACE,
            request
        )
    }

    actual fun scheduleVoidNudge(delayDays: Long) {
        val inputData = Data.Builder()
            .putString("title", "Feeling heavy? ☁️")
            .putString("message", "The Void is open if you need to vent.")
            .putString("type", "void")
            .build()

        val request = OneTimeWorkRequestBuilder<SmartNotificationWorker>()
            .setInitialDelay(delayDays, TimeUnit.DAYS)
            .setInputData(inputData)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "void_nudge",
            ExistingWorkPolicy.KEEP, // Don't replace if already scheduled, we want the original nudge time
            request
        )
    }

    actual fun scheduleWeeklyReport() {
        val now = Calendar.getInstance()
        val sunday = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
            set(Calendar.HOUR_OF_DAY, 9)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }

        // If Sunday 9am has passed, schedule for next Sunday
        if (sunday.before(now)) {
            sunday.add(Calendar.WEEK_OF_YEAR, 1)
        }

        val delay = sunday.timeInMillis - now.timeInMillis

        val inputData = Data.Builder()
            .putString("title", "Weekly Insight 🔮")
            .putString("message", "Your emotional report is ready.")
            .putString("type", "insights")
            .build()

        val request = OneTimeWorkRequestBuilder<SmartNotificationWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(inputData)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "weekly_report",
            ExistingWorkPolicy.REPLACE,
            request
        )
    }

    actual fun scheduleStreakNudge() {
        val inputData = Data.Builder()
            .putString("title", "Keep the momentum 🔥")
            .putString("message", "You're on a streak. Log tomorrow to keep it going.")
            .putString("type", "home") // Navigate to home to log
            .build()

        val request = OneTimeWorkRequestBuilder<SmartNotificationWorker>()
            .setInitialDelay(24, TimeUnit.HOURS) // Nudge them exactly 24h later
            .setInputData(inputData)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "streak_nudge",
            ExistingWorkPolicy.REPLACE,
            request
        )
    }

    actual fun cancelOrbitNotifications() {
        WorkManager.getInstance(context).cancelUniqueWork("orbit_checkin")
    }
}