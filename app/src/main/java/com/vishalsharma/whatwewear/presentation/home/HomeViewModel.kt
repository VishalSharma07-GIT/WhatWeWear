package com.vishalsharma.whatwewear.presentation.home

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import androidx.lifecycle.viewModelScope
import com.vishalsharma.whatwewear.data.mapper.toDomain
import com.vishalsharma.whatwewear.data.repository.WardrobeRepository
import kotlinx.coroutines.launch


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val wardrobeRepository: WardrobeRepository
) : ViewModel() {

    private val _uiState= MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHomeData()
        loadClothingItems()
    }


    private fun loadHomeData() {
        _uiState.value = HomeUiState(
            userName = "Vishal",
            greeting = getGreeting(),
            temperature = "31°C",
            weatherDescription = "Perfect day to dress up",
            totalClothes = 0,
            totalTops = 0,
            totalBottoms = 0,
            totalShoes = 0,
            favoriteCount = 0,
            currentLookIndex = 0,
            currentLookTitle = "Casual Weekend"
        )
    }

    private fun getGreeting(): String {
        return when (java.time.LocalTime.now().hour) {
            in 5..11 -> "Good Morning"
            in 12..16 -> "Good Afternoon"
            in 17..20 -> "Good Evening"
            else -> "Good Night"
        }
    }

    fun selectLook(index: Int) {

        val looks = listOf(
            "Casual Weekend",
            "Smart Casual",
            "Evening Style"
        )

        if (index in looks.indices) {

            _uiState.value = _uiState.value.copy(
                currentLookIndex = index,
                currentLookTitle = looks[index]
            )
        }
    }

    private fun loadClothingItems() {
        viewModelScope.launch {
            try {
                val clothingItems = wardrobeRepository.getClothing()
                    .map { it.toDomain() }

                val colorCounts = clothingItems
                    .map { it.color.trim().ifBlank { "Unspecified" } }
                    .groupingBy { it }
                    .eachCount()
                    .toSortedMap()

                _uiState.value = _uiState.value.copy(
                    totalClothes = clothingItems.size,

                    totalTops = clothingItems.count {
                        it.category.equals("Tops", ignoreCase = true)
                    },

                    totalBottoms = clothingItems.count {
                        it.category.equals("Bottoms", ignoreCase = true)
                    },

                    totalShoes = clothingItems.count {
                        it.category.equals("Shoes", ignoreCase = true)
                    },

                    recentClothes = clothingItems.take(4),
                    error = null,
                    colorCounts = colorCounts
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Unable to load wardrobe items"
                )
            }
        }
    }

    fun refreshClothingItems() {
        loadClothingItems()
    }
}




