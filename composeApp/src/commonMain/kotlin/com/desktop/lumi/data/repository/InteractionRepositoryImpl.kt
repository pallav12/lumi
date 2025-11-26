package com.desktop.lumi.data.repository

import com.desktop.lumi.db.AppDatabase
import com.desktop.lumi.domain.model.Interaction
import com.desktop.lumi.domain.repository.InteractionRepository
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class InteractionRepositoryImpl(
    private val db: AppDatabase
) : InteractionRepository {

    override fun getInteractions(): Flow<List<Interaction>> =
        db.interactionQueries.selectAllInteractions()
            .asFlow()
            .mapToList()
            .map { rows ->
                rows.map {
                    Interaction(
                        id = it.id,
                        type = it.type,
                        moodEffect = it.moodEffect.toInt(),
                        timestamp = it.timestamp
                    )
                }
            }

    override suspend fun saveInteraction(interaction: Interaction) {
        db.interactionQueries.insertInteraction(
            type = interaction.type,
            moodEffect = interaction.moodEffect.toLong(),
            timestamp = interaction.timestamp
        )
    }

    override suspend fun getRecentInteractionsOfType(type: String): List<Interaction> {
        return db.interactionQueries
            .getRecentInteractionsByType(type)
            .executeAsList()
            .map {
                Interaction(
                    id = it.id,
                    type = it.type,
                    moodEffect = it.moodEffect.toInt(),
                    timestamp = it.timestamp
                )
            }
    }
}

