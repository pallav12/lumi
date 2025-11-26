package com.desktop.lumi.db.com.desktop.lumi.sos

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SosViewModel : ViewModel() {

    enum class SosStep { BREATHING, REALITY_CHECK, RESOLUTION }

    private val _step = MutableStateFlow(SosStep.BREATHING)
    val step = _step.asStateFlow()

    // Reality Check Questions
    val questions = listOf(
        "Do I have actual proof they are upset with me?",
        "Is this feeling permanent, or just a wave?",
        "If I do nothing for 1 hour, will the world end?"
    )

    fun onBreathingDone() {
        _step.value = SosStep.REALITY_CHECK
    }

    fun onRealityCheckComplete() {
        _step.value = SosStep.RESOLUTION
    }

    fun reset() {
        _step.value = SosStep.BREATHING
    }
}