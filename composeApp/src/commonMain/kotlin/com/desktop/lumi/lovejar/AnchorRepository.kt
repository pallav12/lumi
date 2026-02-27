package com.desktop.lumi.db.com.desktop.lumi.lovejar

import com.desktop.lumi.db.AppDatabase
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface AnchorRepository {
    fun getAllEntries(): Flow<List<AnchorEntry>>
    suspend fun addEntry(entry: AnchorEntry)
    suspend fun deleteEntry(id: Long)
    suspend fun getRandomEntry(): AnchorEntry?
    suspend fun getEntryCount(): Long
}

class AnchorRepositoryImpl(
    private val db: AppDatabase
) : AnchorRepository {

    override fun getAllEntries(): Flow<List<AnchorEntry>> {
        return db.anchorQueries.getAllEntries()
            .asFlow()
            .mapToList()
            .map { list ->
                list.map { entity ->
                    AnchorEntry(
                        id = entity.id,
                        content = entity.content,
                        imageUri = entity.image_uri,
                        timestamp = entity.timestamp,
                        tags = entity.tags?.split(",")?.filter { it.isNotBlank() } ?: emptyList()
                    )
                }
            }
    }

    override suspend fun addEntry(entry: AnchorEntry) {
        db.anchorQueries.insertEntry(
            content = entry.content,
            image_uri = entry.imageUri,
            timestamp = entry.timestamp,
            tags = entry.tags.joinToString(",")
        )
    }

    override suspend fun deleteEntry(id: Long) {
        db.anchorQueries.deleteEntry(id)
    }

    override suspend fun getRandomEntry(): AnchorEntry? {
        return db.anchorQueries.getRandomEntry()
            .executeAsOneOrNull()
            ?.let { entity ->
                AnchorEntry(
                    id = entity.id,
                    content = entity.content,
                    imageUri = entity.image_uri,
                    timestamp = entity.timestamp,
                    tags = entity.tags?.split(",")?.filter { it.isNotBlank() } ?: emptyList()
                )
            }
    }

    override suspend fun getEntryCount(): Long {
        return db.anchorQueries.getEntriesCount().executeAsOne()
    }
}