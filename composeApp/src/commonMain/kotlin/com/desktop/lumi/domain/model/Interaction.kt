package com.desktop.lumi.domain.model

data class Interaction(
    val id: Long,
    val type: String,
    val moodEffect: Int,
    val timestamp: Long
)

