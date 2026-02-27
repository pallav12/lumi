package com.desktop.lumi.db.com.desktop.lumi.lovejar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.desktop.lumi.analytics.Analytics
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.ExperimentalTime

class AnchorViewModel(
    private val repository: AnchorRepository,
    private val analytics: Analytics?
) : ViewModel() {

    // --- Library State (All Entries for the Gallery) ---
    val entries = repository.getAllEntries()
        .stateIn(
            scope = viewModelScope,
            // Eagerly fetch the data so the Anchor gallery is instantly
            // available without delay when the user needs it most.
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

    // --- Random Entry State (Reality Check Overlay) ---
    private val _randomEntry = MutableStateFlow<AnchorEntry?>(null)
    val randomEntry = _randomEntry.asStateFlow()

    // --- Add Screen State ---
    data class AddState(
        val content: String = "",
        val imageUri: String? = null,
        val isSaved: Boolean = false
    )
    private val _addState = MutableStateFlow(AddState())
    val addState = _addState.asStateFlow()

    // --- Library Actions ---
    fun pullRandomAnchor() {
        viewModelScope.launch {
            _randomEntry.value = repository.getRandomEntry()
            analytics?.logEvent("anchor_reality_check_pulled")
        }
    }

    fun clearRandomAnchor() {
        _randomEntry.value = null
    }

    fun deleteEntry(id: Long) {
        viewModelScope.launch {
            repository.deleteEntry(id)
            analytics?.logEvent("anchor_deleted")
        }
    }

    // --- Add Screen Actions ---
    fun onContentChange(text: String) {
        _addState.update { it.copy(content = text) }
    }

    fun onImagePicked(uri: String) {
        _addState.update { it.copy(imageUri = uri) }
    }

    fun removeImage() {
        _addState.update { it.copy(imageUri = null) }
    }

    @OptIn(ExperimentalTime::class)
    fun saveEntry() {
        val state = _addState.value
        if (state.content.isBlank() && state.imageUri == null) return

        viewModelScope.launch {
            repository.addEntry(
                AnchorEntry(
                    content = state.content,
                    imageUri = state.imageUri,
                    timestamp = kotlin.time.Clock.System.now().toEpochMilliseconds(),
                    tags = emptyList()
                )
            )

            analytics?.logEvent(
                "anchor_created",
                mapOf(
                    "has_image" to (state.imageUri != null),
                    "has_text" to state.content.isNotBlank()
                )
            )

            _addState.update { AddState(isSaved = true) }
        }
    }

    fun resetAddState() {
        _addState.update { AddState() }
    }
}