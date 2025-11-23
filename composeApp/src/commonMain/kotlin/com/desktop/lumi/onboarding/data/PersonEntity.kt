package com.desktop.lumi.onboarding.data

data class PersonEntity(
    val id: Long = 1L,
    val name: String,
    val relationshipType: String,
    val reminderHour: Int,
    val reminderMinute: Int
)