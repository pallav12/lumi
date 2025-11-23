package com.desktop.lumi.domain.repository

import com.desktop.lumi.domain.model.Person
import kotlinx.coroutines.flow.Flow

interface PersonRepository {
    fun getPerson(): Flow<Person?>
    suspend fun savePerson(person: Person)
}

