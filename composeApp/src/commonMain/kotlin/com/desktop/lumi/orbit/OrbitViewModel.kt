package com.desktop.lumi.orbit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.desktop.lumi.analytics.Analytics
import com.desktop.lumi.db.com.desktop.lumi.NotificationScheduler
import com.desktop.lumi.domain.repository.OrbitRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.minutes
import kotlin.time.ExperimentalTime

class OrbitViewModel(
    private val repository: OrbitRepository,
    private val analytics: Analytics?,
    private val scheduler: NotificationScheduler? = null
) : ViewModel() {

    data class OrbitState(
        val isActive: Boolean = false,
        val startTime: Long = 0L,
        val durationMinutes: Long = 0L,
        val intention: String = "",
        val progress: Float = 0f,
        val timeReclaimed: String = "0m",
        val isCompleted: Boolean = false
    )

    private val _uiState = MutableStateFlow(OrbitState())
    val uiState = _uiState.asStateFlow()

    private val _reviewEvent = MutableStateFlow(false)
    val reviewEvent = _reviewEvent.asStateFlow()

    private var timerJob: Job? = null


    init {
        viewModelScope.launch {
            repository.getOrbitState().collectLatest { dbState ->
                updateLocalStateFromDb(dbState)
                if (dbState.isActive && !dbState.isCompleted) {
                    startTicker()
                } else {
                    timerJob?.cancel()
                }
            }
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun updateLocalStateFromDb(dbState: OrbitState) {
        val now = kotlin.time.Clock.System.now().toEpochMilliseconds()

        if (dbState.isActive) {
            val elapsedMillis = now - dbState.startTime
            val totalMillis = dbState.durationMinutes * 60 * 1000

            if (elapsedMillis >= totalMillis && !dbState.isCompleted) {
                // Completed while away
                viewModelScope.launch {
                    repository.updateOrbitState(
                        dbState.copy(isCompleted = true, isActive = false)
                    )
                }
                // Trigger Review on return
                _reviewEvent.value = true
            } else {
                val progress =
                    if (totalMillis > 0) elapsedMillis.toFloat() / totalMillis.toFloat() else 0f
                _uiState.update {
                    dbState.copy(
                        progress = progress.coerceIn(0f, 1f),
                        timeReclaimed = formatDuration(elapsedMillis / 1000 / 60)
                    )
                }
            }
        } else if (dbState.isCompleted) {
            _uiState.update {
                dbState.copy(
                    progress = 1f,
                    timeReclaimed = formatDuration(dbState.durationMinutes)
                )
            }
        } else {
            _uiState.update { dbState }
        }
    }

    @OptIn(ExperimentalTime::class)
    fun startOrbit(durationHours: Int, intention: String) {
        val now = kotlin.time.Clock.System.now().toEpochMilliseconds()
        val durationMins = durationHours * 60L

        val newState = OrbitState(
            isActive = true,
            startTime = now,
            durationMinutes = durationMins,
            intention = intention,
            isCompleted = false
        )

        viewModelScope.launch {
            repository.updateOrbitState(newState)
            // Schedule check-in
            scheduler?.scheduleOrbitCheckIn((durationMins / 2))
        }

        analytics?.logEvent("orbit_started", mapOf("duration" to durationHours))
    }

    @OptIn(ExperimentalTime::class)
    private fun startTicker() {
        if (timerJob?.isActive == true) return

        timerJob = viewModelScope.launch {
            while (true) {
                val state = _uiState.value
                if (!state.isActive) break

                val now = kotlin.time.Clock.System.now().toEpochMilliseconds()
                val elapsedMillis = now - state.startTime
                val totalMillis = state.durationMinutes * 60 * 1000

                if (elapsedMillis >= totalMillis) {
                    // Complete
                    repository.updateOrbitState(
                        state.copy(isActive = false, isCompleted = true)
                    )
                    analytics?.logEvent("orbit_completed")

                    _reviewEvent.value = true
                    break
                } else {
                    val progress = elapsedMillis.toFloat() / totalMillis.toFloat()
                    _uiState.update {
                        it.copy(
                            progress = progress.coerceIn(0f, 1f),
                            timeReclaimed = formatDuration(elapsedMillis / 1000 / 60)
                        )
                    }
                }
                delay(1.minutes)
            }
        }
    }

    // ... (breakOrbit, finishOrbit, formatDuration remain same) ...
    fun breakOrbit() {
        viewModelScope.launch { repository.resetOrbit() }
        timerJob?.cancel()
        scheduler?.cancelOrbitNotifications()
        analytics?.logEvent("orbit_broken")
    }

    fun finishOrbit() {
        viewModelScope.launch { repository.resetOrbit() }
        timerJob?.cancel()
    }

    private fun formatDuration(minutes: Long): String {
        val h = minutes / 60
        val m = minutes % 60
        return if (h > 0) "${h}h ${m}m" else "${m}m"
    }

    fun onReviewShown() {
        _reviewEvent.value = false
    }
}