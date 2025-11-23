package com.desktop.lumi.domain.model

data class Person(
    val id: Long,
    val name: String,
    val relationshipType: String,
    val reminderHour: Long,
    val reminderMinute: Long
)

