package com.desktop.lumi.db.com.desktop.lumi

expect class NotificationScheduler {
    fun scheduleDailyReminder(hour: Int, minute: Int, message: String)
    fun cancelDailyReminder()
    fun scheduleOrbitCheckIn(delayMinutes: Long)
    fun scheduleVoidNudge(delayDays: Long)
    fun scheduleWeeklyReport()
    fun scheduleStreakNudge()
    fun cancelOrbitNotifications()
}