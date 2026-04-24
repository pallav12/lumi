package com.desktop.lumi

import com.revenuecat.purchases.kmp.Purchases
import com.revenuecat.purchases.kmp.configure

/**
 * Called from Swift AppDelegate at app launch.
 * Keeps platform-sensitive SDK init out of the Compose UI layer.
 */
fun initializeLumi(revenueCatApiKey: String) {
    if (revenueCatApiKey.isNotBlank()) {
        Purchases.configure(apiKey = revenueCatApiKey)
    }
}
