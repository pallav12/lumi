package com.desktop.lumi

/**
 * Singleton that Swift wires a closure into at launch.
 * When Compose calls [requestReview], it triggers the Swift closure
 * which invokes SKStoreReviewController.requestReview().
 */
object IOSReviewManager {
    private var handler: (() -> Unit)? = null

    /** Called from Swift AppDelegate at launch to register the review trigger. */
    fun setHandler(handler: () -> Unit) {
        this.handler = handler
    }

    /** Called from Kotlin (Compose UI) when a review prompt is warranted. */
    fun requestReview() {
        handler?.invoke()
    }
}
