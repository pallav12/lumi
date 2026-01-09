package com.desktop.lumi.analytics

/**
 * Analytics interface for tracking user events across platforms
 */
expect class Analytics {
    /**
     * Log an event with optional parameters
     */
    fun logEvent(eventName: String, parameters: Map<String, Any>? = null)
}



