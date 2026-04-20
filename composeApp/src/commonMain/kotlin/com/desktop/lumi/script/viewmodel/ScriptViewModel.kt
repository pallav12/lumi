package com.desktop.lumi.script.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class ScriptCategory(val displayName: String) {
    IGNORED("I feel ignored"),
    REASSURANCE("I need reassurance"),
    BOUNDARIES("Setting a boundary"),
    CONFLICT("After a fight"),
    DATES("Planning/Clarity")
}

data class SafeScript(
    val category: ScriptCategory,
    val title: String, // e.g. "Soft Check-in"
    val content: String,
    val tone: String // "Soft", "Direct", "Vulnerable"
)

class ScriptViewModel : ViewModel() {

    // The Library (Hardcoded for MVP, can be DB later)
    private val allScripts = listOf(
        // IGNORED
        SafeScript(
            ScriptCategory.IGNORED,
            "The Soft Check-in",
            "Hey, just checking in. Feeling a bit disconnected lately and wanted to say hi. Hope your day is going well.",
            "Soft"
        ),
        SafeScript(
            ScriptCategory.IGNORED,
            "The Vulnerable Admit",
            "I'm feeling a bit anxious about the silence. Just need a little reassurance if you have a moment, no rush.",
            "Vulnerable"
        ),
        SafeScript(
            ScriptCategory.IGNORED,
            "The Reality Check",
            "I value our connection, but the lack of communication is hard for me. Can you let me know when you're free to chat?",
            "Direct"
        ),

        // REASSURANCE
        SafeScript(
            ScriptCategory.REASSURANCE,
            "Simple Ask",
            "Could use a little love today. Feeling a bit off.",
            "Soft"
        ),
        SafeScript(
            ScriptCategory.REASSURANCE,
            "The 'Are we good?'",
            "My brain is making up stories that you're mad at me. Just checking—are we good?",
            "Vulnerable"
        ),

        // BOUNDARIES
        SafeScript(
            ScriptCategory.BOUNDARIES,
            "Time out",
            "I'm feeling a bit overwhelmed right now and need to take some space to process. I'll reach out when I'm ready.",
            "Direct"
        ),
        SafeScript(
            ScriptCategory.BOUNDARIES,
            "Pacing",
            "I really like where this is going, but I need to take things a bit slower to feel secure. Hope you understand.",
            "Soft"
        ),

        // CONFLICT
        SafeScript(
            ScriptCategory.CONFLICT,
            "Repair Attempt",
            "I don't want to fight. I want to understand you. Can we restart this conversation?",
            "Soft"
        ),
        SafeScript(
            ScriptCategory.CONFLICT,
            "Owning it",
            "I realized I reacted out of anxiety earlier. I'm sorry. I'm working on regulating better.",
            "Vulnerable"
        ),

        // DATES
        SafeScript(
            ScriptCategory.DATES,
            "Clarifying Plans",
            "Hey! Just want to double check if we are still on for tonight? I like to plan my evening.",
            "Direct"
        )
    )

    companion object {
        /** Indices of the 3 scripts free users can access. */
        val FREE_SCRIPT_TITLES = setOf(
            "The Soft Check-in",  // IGNORED - Soft
            "Simple Ask",         // REASSURANCE - Soft
            "Time out"            // BOUNDARIES - Direct
        )
    }

    data class UiState(
        val selectedCategory: ScriptCategory = ScriptCategory.IGNORED,
        val visibleScripts: List<SafeScript> = emptyList()
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    init {
        // Initial load
        selectCategory(ScriptCategory.IGNORED)
    }

    fun selectCategory(category: ScriptCategory) {
        _uiState.update {
            it.copy(
                selectedCategory = category,
                visibleScripts = allScripts.filter { script -> script.category == category }
            )
        }
    }
}