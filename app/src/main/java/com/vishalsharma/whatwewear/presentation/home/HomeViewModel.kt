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
import com.vishalsharma.whatwewear.data.remote.weather.WeatherService

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val wardrobeRepository: WardrobeRepository
) : ViewModel() {

    private val _uiState= MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHomeData()
        loadClothingItems()
        loadWeather()
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
    private fun loadWeather() {
        viewModelScope.launch {
            try {
                val weather = WeatherService.getCurrentWeather()

                _uiState.value = _uiState.value.copy(
                    temperature = weather.temperature,
                    weatherDescription = weather.description
                )
            } catch (e: Exception) {

            }
        }
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

        if (index !in looks.indices) return

        val allClothes = _uiState.value.allClothes

        val matchingItems = when (index) {
            0 -> allClothes.filter {
                it.occasion.contains("casual", ignoreCase = true) ||
                        it.season.contains("spring", ignoreCase = true) ||
                        it.season.contains("summer", ignoreCase = true)
            }

            1 -> allClothes.filter {
                it.occasion.contains("smart", ignoreCase = true) ||
                        it.occasion.contains("formal", ignoreCase = true) ||
                        it.occasion.contains("office", ignoreCase = true)
            }

            2 -> allClothes.filter {
                it.occasion.contains("evening", ignoreCase = true) ||
                        it.occasion.contains("party", ignoreCase = true) ||
                        it.occasion.contains("date", ignoreCase = true)
            }

            else -> emptyList()
        }

        val selectedItems = (matchingItems.ifEmpty { allClothes })
            .take(3)

        _uiState.value = _uiState.value.copy(
            currentLookIndex = index,
            currentLookTitle = looks[index],
            currentLookItems = selectedItems
        )
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
                    allClothes = clothingItems,
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




