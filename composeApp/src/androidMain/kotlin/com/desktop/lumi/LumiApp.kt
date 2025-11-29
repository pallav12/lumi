package com.desktop.lumi

import android.app.Application
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class LumiApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize Firebase early in the application lifecycle
        FirebaseApp.initializeApp(this)
    }
}

