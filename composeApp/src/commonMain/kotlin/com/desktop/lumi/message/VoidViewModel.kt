package com.desktop.lumi.db.com.desktop.lumi.message

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.desktop.lumi.analytics.Analytics
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class VoidViewModel(
    private val analytics: Analytics? = null
) : ViewModel() {

    data class VoidState(
        val message: String = "",
        val isLocked: Boolean = false,
        val isBurning: Boolean = false,
        val isBurned: Boolean = false
    )

    private val _uiState = MutableStateFlow(VoidState())
    val uiState = _uiState.asStateFlow()

    fun onMessageChange(text: String) {
        if (!_uiState.value.isLocked) {
            _uiState.update { it.copy(message = text) }
        }
    }

    fun onRelease() {
        if (_uiState.value.message.isBlank()) return

        viewModelScope.launch {
            // Capture message length before clearing
            val messageLength = _uiState.value.message.length
            
            _uiState.update { it.copy(isLocked = true) }

            delay(1500)
            _uiState.update { it.copy(isBurning = true) }

            delay(2000) // Duration of burn animation
            _uiState.update { it.copy(isBurning = false, isBurned = true, message = "") }
            
            // Track analytics event when void is burned
            analytics?.logEvent(
                "void_burned",
                mapOf(
                    "message_length" to messageLength
                )
            )

            delay(3000)
            _uiState.update { it.copy(isLocked = false, isBurned = false) }
        }
    }
}