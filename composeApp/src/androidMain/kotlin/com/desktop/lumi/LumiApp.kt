package com.desktop.lumi

import android.app.Application
import com.google.firebase.FirebaseApp
import com.revenuecat.purchases.kmp.Purchases
import com.revenuecat.purchases.kmp.configure
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class LumiApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)

            Purchases.configure(apiKey = BuildConfig.REVENUECAT_KEY)
    }
}

