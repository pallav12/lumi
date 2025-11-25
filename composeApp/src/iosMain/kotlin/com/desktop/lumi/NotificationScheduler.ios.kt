package com.desktop.lumi.db.com.desktop.lumi


import platform.Foundation.NSDateComponents
import platform.UserNotifications.*

actual class NotificationScheduler {

    actual fun scheduleDailyReminder(hour: Int, minute: Int, message: String) {
        val center = UNUserNotificationCenter.currentNotificationCenter()
        
        // Cancel existing notification first
        cancelDailyReminder()

        val content = UNMutableNotificationContent().apply {
            setTitle("Lumi ❤️")
            setBody(message)
        }

        val trigger = UNCalendarNotificationTrigger.triggerWithDateMatchingComponents(
            NSDateComponents().apply {
                this.hour = hour.toLong()
                this.minute = minute.toLong()
            },
            repeats = true
        )

        val request = UNNotificationRequest.requestWithIdentifier(
            "daily_reminder",
            content,
            trigger
        )

        center.addNotificationRequest(request) { error ->
            if (error != null) {
                println("Notification scheduling error: $error")
            } else {
                println("Notification scheduled successfully")
            }
        }
    }

    actual fun cancelDailyReminder() {
        UNUserNotificationCenter.currentNotificationCenter()
            .removePendingNotificationRequestsWithIdentifiers(listOf("daily_reminder"))
    }
}
