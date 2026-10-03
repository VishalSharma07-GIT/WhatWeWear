package com.vishalsharma.whatwewear.presentation.wardrobe

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vishalsharma.whatwewear.data.remote.model.ClothingDto
import com.vishalsharma.whatwewear.data.repository.WardrobeRepository
import com.vishalsharma.whatwewear.domain.model.ClothingItem
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class WardrobeViewModel @Inject constructor(
    private val wardrobeRepository: WardrobeRepository
) : ViewModel() {

    private val _clothingItems = MutableStateFlow(
        sampleClothingItems
    )

    val clothingItems: StateFlow<List<ClothingItem>> =
        _clothingItems.asStateFlow()

    fun addClothing(item: ClothingItem) {

        // Show the item immediately in the UI
        _clothingItems.value += item

        viewModelScope.launch {

            try {

                val clothingDto = ClothingDto(
                    id = item.id,
                    name = item.name,
                    brand = item.brand,
                    category = item.category,
                    color = item.color,
                    size = item.size,
                    imageUrl = item.imageUri ?: ""
                )

                wardrobeRepository.addClothing(clothingDto)

                Log.d(
                    "WardrobeFirebase",
                    "Clothing saved successfully: ${item.name}"
                )

            } catch (e: Exception) {

                Log.e(
                    "WardrobeFirebase",
                    "Failed to save clothing",
                    e
                )
            }
        }
    }
}