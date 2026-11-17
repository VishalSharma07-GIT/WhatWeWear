package com.vishalsharma.whatwewear.presentation.home

import com.vishalsharma.whatwewear.domain.model.ClothingItem

data class DayCell(
    val dateKey: String,
    val weekday: String,
    val dayNumber: Int,
    val monthLabel: String,
    val worn: Boolean,
    val isToday: Boolean,
    val isSelected: Boolean,
    val isFuture: Boolean
)

// What shows under the calendar when a day other than today is selected
data class DayDetail(
    val dateKey: String,
    val title: String,
    val items: List<ClothingItem>,
    val message: String,
    val canRemove: Boolean,
    val canLog: Boolean
)

data class Reminder(
    val item: ClothingItem,
    val message: String
)

data class HomeUiState(
    val isLoading: Boolean = true,
    val loadFailed: Boolean = false,
    val userName: String = "",
    val greeting: String = "",
    val dateLine: String = "",
    val occasions: List<String> = listOf("Casual", "Work", "Going out"),
    val selectedOccasion: Int = 0,
    val occasionHint: String? = null,
    val outfit: List<ClothingItem> = emptyList(),
    val outfitTitle: String = "",
    val outfitColors: String = "",
    val wornToday: Boolean = false,
    val totalClothes: Int = 0,
    val days: List<DayCell> = emptyList(),
    val todayIndex: Int = 0,
    val dayDetail: DayDetail? = null,
    val reminder: Reminder? = null
)