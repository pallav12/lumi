package com.desktop.lumi.domain.repository

import com.desktop.lumi.domain.model.Interaction
import kotlinx.coroutines.flow.Flow

interface InteractionRepository {
    fun getInteractions(): Flow<List<Interaction>>
    suspend fun saveInteraction(interaction: Interaction)
    suspend fun getRecentInteractionsOfType(type: String): List<Interaction>
    suspend fun getAllInteractions(limit: Int): List<Interaction>
}

