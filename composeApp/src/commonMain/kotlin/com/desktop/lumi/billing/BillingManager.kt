package com.desktop.lumi.billing

import com.revenuecat.purchases.kmp.Purchases
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

sealed class PurchaseOutcome {
    data object Success : PurchaseOutcome()
    data class Error(val message: String) : PurchaseOutcome()
    data object Cancelled : PurchaseOutcome()
}

data class BillingState(
    val isPremium: Boolean = false,
    val isLoading: Boolean = true,
    val priceString: String = "",
    val errorMessage: String? = null
)

class BillingManager {

    companion object {
        const val ENTITLEMENT_ID = "lumi_lifetime"
        const val FREE_ANCHOR_LIMIT = 3
    }

    private val _state = MutableStateFlow(BillingState())
    val state: StateFlow<BillingState> = _state.asStateFlow()

    val isPremium: Boolean get() = _state.value.isPremium

    /**
     * Fetch the current entitlement status from RevenueCat.
     * Call once on app launch after Purchases.configure().
     */
    suspend fun refreshEntitlementStatus() {
        _state.update { it.copy(isLoading = true, errorMessage = null) }

        val active = suspendCancellableCoroutine { cont ->
            Purchases.sharedInstance.getCustomerInfo(
                onError = { _ -> cont.resume(false) },
                onSuccess = { customerInfo ->
                    val entitled = customerInfo.entitlements[ENTITLEMENT_ID]?.isActive == true
                    cont.resume(entitled)
                }
            )
        }

        _state.update { it.copy(isPremium = active, isLoading = false) }
    }

    /**
     * Fetch the lifetime package price for the paywall UI.
     */
    suspend fun fetchOfferings() {
        val price = suspendCancellableCoroutine { cont ->
            Purchases.sharedInstance.getOfferings(
                onError = { _ -> cont.resume("") },
                onSuccess = { offerings ->
                    val formatted = offerings.current
                        ?.lifetime
                        ?.storeProduct
                        ?.price
                        ?.formatted ?: ""
                    cont.resume(formatted)
                }
            )
        }
        _state.update { it.copy(priceString = price) }
    }

    /**
     * Trigger the one-time lifetime purchase.
     */
    suspend fun purchaseLifetime(): PurchaseOutcome {
        _state.update { it.copy(isLoading = true, errorMessage = null) }

        // 1. Fetch the lifetime package
        val lifetimePackage = suspendCancellableCoroutine { cont ->
            Purchases.sharedInstance.getOfferings(
                onError = { _ -> cont.resume(null) },
                onSuccess = { offerings -> cont.resume(offerings.current?.lifetime) }
            )
        }

        if (lifetimePackage == null) {
            _state.update { it.copy(isLoading = false, errorMessage = "Could not load offerings.") }
            return PurchaseOutcome.Error("Could not load offerings. Check your connection.")
        }

        // 2. Initiate the purchase
        return suspendCancellableCoroutine { cont ->
            Purchases.sharedInstance.purchase(
                packageToPurchase = lifetimePackage,
                onError = { error, userCancelled ->
                    _state.update { it.copy(isLoading = false) }
                    if (userCancelled) {
                        cont.resume(PurchaseOutcome.Cancelled)
                    } else {
                        val msg = error.message
                        _state.update { it.copy(errorMessage = msg) }
                        cont.resume(PurchaseOutcome.Error(msg))
                    }
                },
                onSuccess = { _, customerInfo ->
                    val active = customerInfo.entitlements[ENTITLEMENT_ID]?.isActive == true
                    _state.update { it.copy(isPremium = active, isLoading = false) }
                    if (active) cont.resume(PurchaseOutcome.Success)
                    else cont.resume(PurchaseOutcome.Error("Purchase completed but entitlement not granted."))
                }
            )
        }
    }

    /**
     * Restore previous purchases (e.g. after reinstall or device switch).
     */
    suspend fun restorePurchases(): PurchaseOutcome {
        _state.update { it.copy(isLoading = true, errorMessage = null) }

        return suspendCancellableCoroutine { cont ->
            Purchases.sharedInstance.restorePurchases(
                onError = { error ->
                    val msg = error.message
                    _state.update { it.copy(isLoading = false, errorMessage = msg) }
                    cont.resume(PurchaseOutcome.Error(msg))
                },
                onSuccess = { customerInfo ->
                    val active = customerInfo.entitlements[ENTITLEMENT_ID]?.isActive == true
                    _state.update { it.copy(isPremium = active, isLoading = false) }
                    if (active) cont.resume(PurchaseOutcome.Success)
                    else cont.resume(PurchaseOutcome.Error("No previous purchase found."))
                }
            )
        }
    }

    fun canAddAnchor(currentCount: Long): Boolean {
        return _state.value.isPremium || currentCount < FREE_ANCHOR_LIMIT
    }

    fun canViewMirrorInsights(): Boolean = _state.value.isPremium

    fun canViewAllScripts(): Boolean = _state.value.isPremium

    fun canViewDetailedInsights(): Boolean = _state.value.isPremium
}
