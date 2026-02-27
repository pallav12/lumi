package com.desktop.lumi.db.com.desktop.lumi.lovejar

data class AnchorEntry(
    val id: Long = 0,
    val content: String,
    val imageUri: String? = null,
    val timestamp: Long,
    val tags: List<String> = emptyList()
)