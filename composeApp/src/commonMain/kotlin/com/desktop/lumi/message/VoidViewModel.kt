package com.desktop.lumi.void

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.desktop.lumi.analytics.Analytics
import com.desktop.lumi.db.com.desktop.lumi.NotificationScheduler
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class VoidViewModel(
    private val analytics: Analytics?,
    private val scheduler: NotificationScheduler? = null
) : ViewModel() {

    data class VoidState(
        val message: String = "",
        val isLocked: Boolean = false,
        val isBurning: Boolean = false,
        val isBurned: Boolean = false
    )

    private val _uiState = MutableStateFlow(VoidState())
    val uiState = _uiState.asStateFlow()

    private val _reviewEvent = MutableStateFlow(false)
    val reviewEvent = _reviewEvent.asStateFlow()

    fun onMessageChange(text: String) {
        if (!_uiState.value.isLocked) {
            _uiState.update { it.copy(message = text) }
        }
    }

    fun onRelease() {
        if (_uiState.value.message.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLocked = true) }
            analytics?.logEvent("void_burn_started")

            delay(1500)
            _uiState.update { it.copy(isBurning = true) }

            delay(2000)
            _uiState.update { it.copy(isBurning = false, isBurned = true, message = "") }
            analytics?.logEvent("void_burn_completed")

            // ⬅ NEW: Trigger Review
            _reviewEvent.value = true

            // Schedule nudge for later
            scheduler?.scheduleVoidNudge(3)

            delay(3000)
            _uiState.update { it.copy(isLocked = false, isBurned = false) }
        }
    }

    fun onReviewShown() {
        _reviewEvent.value = false
    }
}