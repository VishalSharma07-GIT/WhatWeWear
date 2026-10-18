
package com.vishalsharma.whatwewear.data.remote.model

data class OutfitDto(
    val id: String = "",
    val name: String = "",
    val occasion: String = "",
    val clothingIds: List<String> = emptyList(),
    val plannedDate: String = "",
    val createdAt: Long = 0L,
    val isWorn: Boolean = false
)
