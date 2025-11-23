package com.desktop.lumi.domain.repository

import com.desktop.lumi.domain.model.Reflection
import kotlinx.coroutines.flow.Flow

interface ReflectionRepository {
    fun getTodayReflection(): Flow<Reflection?>
    suspend fun saveReflection(reflection: Reflection)
    fun getLast7Reflections(): Flow<List<Reflection>>
}

