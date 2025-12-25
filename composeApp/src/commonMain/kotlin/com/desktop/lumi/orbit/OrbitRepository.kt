package com.desktop.lumi.domain.repository

import com.desktop.lumi.db.AppDatabase
import com.desktop.lumi.orbit.OrbitViewModel
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToOne
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface OrbitRepository {
    fun getOrbitState(): Flow<OrbitViewModel.OrbitState>
    suspend fun updateOrbitState(state: OrbitViewModel.OrbitState)
    suspend fun resetOrbit()
}

class OrbitRepositoryImpl(
    private val db: AppDatabase
) : OrbitRepository {

    init {
        // Ensure the singleton row exists
        db.orbitQueries.initOrbit()
    }

    override fun getOrbitState(): Flow<OrbitViewModel.OrbitState> {
        return db.orbitQueries.getOrbit()
            .asFlow()
            .mapToOne()
            .map {
                OrbitViewModel.OrbitState(
                    isActive = it.is_active == 1L,
                    startTime = it.start_time,
                    durationMinutes = it.duration_minutes,
                    intention = it.intention,
                    isCompleted = it.is_completed == 1L,
                    // Calculated fields will be handled in ViewModel based on these
                    progress = 0f,
                    timeReclaimed = "0m"
                )
            }
    }

    override suspend fun updateOrbitState(state: OrbitViewModel.OrbitState) {
        db.orbitQueries.updateOrbit(
            is_active = if (state.isActive) 1L else 0L,
            start_time = state.startTime,
            duration_minutes = state.durationMinutes,
            intention = state.intention,
            is_completed = if (state.isCompleted) 1L else 0L
        )
    }

    override suspend fun resetOrbit() {
        db.orbitQueries.resetOrbit()
    }
}