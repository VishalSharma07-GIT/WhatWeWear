package com.vishalsharma.whatwewear.domain.model

data class Clothing(

    val id: String = "",

    val userId: String = "",

    val imageUrl: String = "",

    val category: ClothingCategory = ClothingCategory.TOP,

    val color: String = "",

    val brand: String = "",

    val size: String = "",

    val season: Season = Season.ALL,

    val occasion: Occasion = Occasion.CASUAL,

    val favorite: Boolean = false,

    val createdAt: Long = System.currentTimeMillis()
)