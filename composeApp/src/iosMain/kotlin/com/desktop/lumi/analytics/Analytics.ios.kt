package com.desktop.lumi.analytics

import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSNotificationName
import platform.Foundation.NSDictionary
import platform.Foundation.create

actual class Analytics {

    actual fun logEvent(eventName: String, parameters: Map<String, Any>?) {
        // Post analytics event via NSNotificationCenter so the Swift layer
        // can observe and forward to Firebase Analytics (or any other SDK).
        // Notification name: "LumiAnalyticsEvent"
        // userInfo: { "event_name": eventName, "parameters": parameters }
        val userInfo = mutableMapOf<Any?, Any?>()
        userInfo["event_name"] = eventName
        if (parameters != null) {
            userInfo["parameters"] = parameters
        }

        NSNotificationCenter.defaultCenter.postNotificationName(
            aName = ANALYTICS_NOTIFICATION_NAME,
            `object` = null,
            userInfo = userInfo as Map<Any?, *>
        )

        // Also log to console for debugging
        val paramsStr = parameters?.entries?.joinToString(", ") { "${it.key}=${it.value}" } ?: ""
        println("Analytics Event: $eventName${if (paramsStr.isNotEmpty()) " | $paramsStr" else ""}")
    }

    companion object {
        /** Swift code should observe this notification name to receive analytics events. */
        val ANALYTICS_NOTIFICATION_NAME: NSNotificationName = "LumiAnalyticsEvent"
    }
}
