package com.vishalsharma.whatwewear.data.remote.model

data class WearLogDto(
    val date: String = "",
    val itemIds: List<String> = emptyList(),
    val occasion: String = "",
    val wornAt: Long = 0L
)