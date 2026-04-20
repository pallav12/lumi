package com.desktop.lumi.db.com.desktop.lumi


import platform.Foundation.NSCalendar
import platform.Foundation.NSDate
import platform.Foundation.NSDateComponents
import platform.UserNotifications.*

actual class NotificationScheduler {

    private val center = UNUserNotificationCenter.currentNotificationCenter()

    actual fun scheduleDailyReminder(hour: Int, minute: Int, message: String) {
        // Cancel existing notification first
        cancelDailyReminder()

        val content = UNMutableNotificationContent().apply {
            setTitle("Lumi ❤️")
            setBody(message)
            setSound(UNNotificationSound.defaultSound)
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
                println("Daily reminder scheduled at $hour:$minute")
            }
        }
    }

    actual fun cancelDailyReminder() {
        center.removePendingNotificationRequestsWithIdentifiers(listOf("daily_reminder"))
    }

    actual fun scheduleOrbitCheckIn(delayMinutes: Long) {
        // Cancel any existing orbit check-in first
        cancelOrbitNotifications()

        val content = UNMutableNotificationContent().apply {
            setTitle("Orbit Update \uD83E\uDE90")
            setBody("Halfway there. You are doing great.")
            setSound(UNNotificationSound.defaultSound)
        }

        val trigger = UNTimeIntervalNotificationTrigger.triggerWithTimeInterval(
            delayMinutes * 60.0,
            repeats = false
        )

        val request = UNNotificationRequest.requestWithIdentifier(
            "orbit_checkin",
            content,
            trigger
        )

        center.addNotificationRequest(request) { error ->
            if (error != null) {
                println("Orbit check-in scheduling error: $error")
            } else {
                println("Orbit check-in scheduled in $delayMinutes minutes")
            }
        }
    }

    actual fun scheduleVoidNudge(delayDays: Long) {
        val content = UNMutableNotificationContent().apply {
            setTitle("Feeling heavy? \u2601\uFE0F")
            setBody("The Void is open if you need to vent.")
            setSound(UNNotificationSound.defaultSound)
        }

        val delaySeconds = delayDays * 24 * 60 * 60
        val trigger = UNTimeIntervalNotificationTrigger.triggerWithTimeInterval(
            delaySeconds.toDouble(),
            repeats = false
        )

        val request = UNNotificationRequest.requestWithIdentifier(
            "void_nudge",
            content,
            trigger
        )

        // Check if already scheduled before adding (mirrors Android's KEEP policy)
        center.getPendingNotificationRequestsWithCompletionHandler { requests ->
            val alreadyScheduled = (requests as? List<*>)?.any {
                (it as? UNNotificationRequest)?.identifier == "void_nudge"
            } ?: false

            if (!alreadyScheduled) {
                center.addNotificationRequest(request) { error ->
                    if (error != null) {
                        println("Void nudge scheduling error: $error")
                    } else {
                        println("Void nudge scheduled in $delayDays days")
                    }
                }
            }
        }
    }

    actual fun scheduleWeeklyReport() {
        // Remove existing weekly report first
        center.removePendingNotificationRequestsWithIdentifiers(listOf("weekly_report"))

        val content = UNMutableNotificationContent().apply {
            setTitle("Weekly Insight \uD83D\uDD2E")
            setBody("Your emotional report is ready.")
            setSound(UNNotificationSound.defaultSound)
        }

        // Schedule for Sunday at 9:00 AM, repeating weekly
        val trigger = UNCalendarNotificationTrigger.triggerWithDateMatchingComponents(
            NSDateComponents().apply {
                this.weekday = 1 // Sunday in NSCalendar (1 = Sunday)
                this.hour = 9
                this.minute = 0
            },
            repeats = true
        )

        val request = UNNotificationRequest.requestWithIdentifier(
            "weekly_report",
            content,
            trigger
        )

        center.addNotificationRequest(request) { error ->
            if (error != null) {
                println("Weekly report scheduling error: $error")
            } else {
                println("Weekly report scheduled for Sundays at 9:00 AM")
            }
        }
    }

    actual fun scheduleStreakNudge() {
        // Remove existing streak nudge first
        center.removePendingNotificationRequestsWithIdentifiers(listOf("streak_nudge"))

        val content = UNMutableNotificationContent().apply {
            setTitle("Keep the momentum \uD83D\uDD25")
            setBody("You're on a streak. Log tomorrow to keep it going.")
            setSound(UNNotificationSound.defaultSound)
        }

        // 24 hours from now
        val trigger = UNTimeIntervalNotificationTrigger.triggerWithTimeInterval(
            24.0 * 60 * 60,
            repeats = false
        )

        val request = UNNotificationRequest.requestWithIdentifier(
            "streak_nudge",
            content,
            trigger
        )

        center.addNotificationRequest(request) { error ->
            if (error != null) {
                println("Streak nudge scheduling error: $error")
            } else {
                println("Streak nudge scheduled in 24 hours")
            }
        }
    }

    actual fun cancelOrbitNotifications() {
        center.removePendingNotificationRequestsWithIdentifiers(listOf("orbit_checkin"))
    }
}
