package com.vishalsharma.whatwewear.presentation.home

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    private val _uiState= MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHomeData()
    }

    private fun loadHomeData() {

        _uiState.value = HomeUiState(
            userName = "Vishal",
            greeting = getGreeting(),
            temperature = "31°C",
            weatherDescription = "Perfect day to dress up",
            totalClothes = 52,
            totalTops = 18,
            totalBottoms = 12,
            totalShoes = 10,
            favoriteCount = 9,
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
}




