package com.desktop.lumi

import android.app.Activity
import android.content.Context
import android.widget.Toast
import com.desktop.lumi.analytics.Analytics
import com.google.android.play.core.review.ReviewManagerFactory

interface AppReviewManager {
    fun tryRequestReview(activity: Activity)
}

class AndroidReviewManager(private val context: Context, val analytics: Analytics) : AppReviewManager {
    private val manager = ReviewManagerFactory.create(context)

    override fun tryRequestReview(activity: Activity) {
        val request = manager.requestReviewFlow()
        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val reviewInfo = task.result
                val flow = manager.launchReviewFlow(activity, reviewInfo)
                flow.addOnCompleteListener { _ ->
                    analytics.logEvent("review_complete")
                }.addOnFailureListener {
                    analytics.logEvent("review_failed")
                }
            } else {
                analytics.logEvent("task_unsuccessful")
            }
        }
    }
}