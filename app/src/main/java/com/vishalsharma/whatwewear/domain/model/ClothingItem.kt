package com.vishalsharma.whatwewear.domain.model

data class ClothingItem(
    val id: String,
    val name: String,
    val brand: String,
    val category: String,
    val imageRes: Int? = null,
    val imageUri: String? = null,
    val color: String = "",
    val size: String = "",
    val season: String = "",
    val occasion: String = ""
)