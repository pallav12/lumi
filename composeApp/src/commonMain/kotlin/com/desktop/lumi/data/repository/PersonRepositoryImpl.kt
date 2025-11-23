package com.desktop.lumi.data.repository

import com.desktop.lumi.db.AppDatabase
import com.desktop.lumi.domain.model.Person
import com.desktop.lumi.domain.repository.PersonRepository
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToOneOrNull
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PersonRepositoryImpl(
    private val db: AppDatabase
) : PersonRepository {

    override fun getPerson(): Flow<Person?> =
        db.personQueries.selectPerson()
            .asFlow()
            .mapToOneOrNull()
            .map { row ->
                row?.let {
                    Person(
                        id = it.id,
                        name = it.name,
                        relationshipType = it.relationshipType,
                        reminderHour = it.reminderHour,
                        reminderMinute = it.reminderMinute
                    )
                }
            }

    override suspend fun savePerson(person: Person) {
        db.personQueries.deletePerson()
        db.personQueries.insertPerson(
            name = person.name,
            relationshipType = person.relationshipType,
            reminderHour = person.reminderHour,
            reminderMinute = person.reminderMinute
        )
    }
}

