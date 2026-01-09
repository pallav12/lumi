package com.desktop.lumi.analytics

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

actual class Analytics {
    private val firebaseAnalytics: FirebaseAnalytics = Firebase.analytics

    actual fun logEvent(eventName: String, parameters: Map<String, Any>?) {
        val bundle = parameters?.let { params ->
            android.os.Bundle().apply {
                params.forEach { (key, value) ->
                    when (value) {
                        is String -> putString(key, value)
                        is Int -> putInt(key, value)
                        is Long -> putLong(key, value)
                        is Double -> putDouble(key, value)
                        is Boolean -> putBoolean(key, value)
                        else -> putString(key, value.toString())
                    }
                }
            }
        }
        firebaseAnalytics.logEvent(eventName, bundle)
    }
}



