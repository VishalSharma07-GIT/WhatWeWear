package com.vishalsharma.whatwewear.presentation.wardrobe

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vishalsharma.whatwewear.data.mapper.toDomain
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

    private val _clothingItems =
        MutableStateFlow<List<ClothingItem>>(emptyList())

    val clothingItems: StateFlow<List<ClothingItem>> =
        _clothingItems.asStateFlow()

    init {
        loadClothing()
    }

    private fun loadClothing() {

        viewModelScope.launch {

            try {

                val clothingDtoList =
                    wardrobeRepository.getClothing()

                val clothingItems =
                    clothingDtoList.map { dto ->
                        dto.toDomain()
                    }

                _clothingItems.value = clothingItems

                Log.d(
                    "WardrobeFirebase",
                    "Loaded ${clothingItems.size} clothing items"
                )

            } catch (e: Exception) {

                Log.e(
                    "WardrobeFirebase",
                    "Failed to load clothing",
                    e
                )
            }
        }
    }

    fun addClothing(item: ClothingItem) {

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

                _clothingItems.value += item

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
    fun updateClothing(item: ClothingItem) {

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

                wardrobeRepository.updateClothing(clothingDto)

                _clothingItems.value =
                    _clothingItems.value.map { existingItem ->
                        if (existingItem.id == item.id) {
                            item
                        } else {
                            existingItem
                        }
                    }

                Log.d(
                    "WardrobeFirebase",
                    "Clothing updated successfully: ${item.name}"
                )

            } catch (e: Exception) {

                Log.e(
                    "WardrobeFirebase",
                    "Failed to update clothing",
                    e
                )
            }
        }
    }

    fun getClothingById(id: String): ClothingItem? {
        return _clothingItems.value.find {
            it.id == id
        }
    }
    fun deleteClothing(clothingId: String) {

        viewModelScope.launch {

            try {

                wardrobeRepository.deleteClothing(clothingId)

                _clothingItems.value =
                    _clothingItems.value.filter {
                        it.id != clothingId
                    }

                Log.d(
                    "WardrobeFirebase",
                    "Clothing deleted successfully: $clothingId"
                )

            } catch (e: Exception) {

                Log.e(
                    "WardrobeFirebase",
                    "Failed to delete clothing",
                    e
                )
            }
        }
    }
}