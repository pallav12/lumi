package com.desktop.lumi.data.repository

import com.desktop.lumi.db.AppDatabase
import com.desktop.lumi.domain.model.Reflection
import com.desktop.lumi.domain.repository.ReflectionRepository
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.squareup.sqldelight.runtime.coroutines.mapToOneOrNull
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ReflectionRepositoryImpl(
    private val db: AppDatabase
) : ReflectionRepository {

    override fun getTodayReflection(): Flow<Reflection?> =
        db.reflectionQueries.selectToday()
            .asFlow()
            .mapToOneOrNull()
            .map { row ->
                row?.let {
                    Reflection(
                        id = it.id,
                        mood = it.mood.toInt(),
                        note = it.note,
                        timestamp = it.timestamp
                    )
                }
            }

    override suspend fun saveReflection(reflection: Reflection) {
        db.reflectionQueries.insertReflection(
            mood = reflection.mood.toLong(),
            note = reflection.note,
            timestamp = reflection.timestamp
        )
    }

    override fun getLast7Reflections(): Flow<List<Reflection>> =
        db.reflectionQueries.selectLast7()
            .asFlow()
            .mapToList()
            .map { list ->
                list.map {
                    Reflection(
                        id = it.id,
                        mood = it.mood.toInt(),
                        note = it.note,
                        timestamp = it.timestamp
                    )
                }
            }
}

