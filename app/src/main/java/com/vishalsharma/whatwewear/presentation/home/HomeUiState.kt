package com.vishalsharma.whatwewear.presentation.home

import com.vishalsharma.whatwewear.domain.model.Clothing

data class HomeUiState(
    val userName: String = "",
    val greeting: String = "",
    val temperature: String = "",
    val weatherDescription: String = "",
    val recentClothes: List<Clothing> = emptyList(),
    val totalClothes: Int = 0,
    val totalTops: Int = 0,
    val totalBottoms: Int = 0,
    val totalShoes: Int = 0,
    val favoriteCount: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null
)