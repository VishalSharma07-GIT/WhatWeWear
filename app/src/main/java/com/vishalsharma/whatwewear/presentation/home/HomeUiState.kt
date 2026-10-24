package com.vishalsharma.whatwewear.presentation.home

import com.vishalsharma.whatwewear.domain.model.ClothingItem

data class HomeUiState(
    val userName: String = "",
    val greeting: String = "",
    val temperature: String = "",
    val weatherDescription: String = "",
    val recentClothes: List<ClothingItem> = emptyList(),
    val allClothes: List<ClothingItem> = emptyList(),
    val colorCounts: Map<String, Int> = emptyMap(),
    val totalClothes: Int = 0,
    val totalTops: Int = 0,
    val totalBottoms: Int = 0,
    val totalShoes: Int = 0,
    val favoriteCount: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentLookIndex: Int = 0,
    val currentLookTitle: String = "Casual Weekend",
    val currentLookItems: List<ClothingItem> = emptyList(),
    val sustainabilityScore: Int = 78,
    val sustainabilityMessage: String =" You're making more sustainable choices",
    val proTip: String =" Try pairing neutral colors with one statement piece"

)