
package com.vishalsharma.whatwewear.presentation.planner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vishalsharma.whatwewear.data.remote.model.OutfitDto
import com.vishalsharma.whatwewear.data.repository.OutfitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class OutfitPlannerViewModel @Inject constructor(
    private val outfitRepository: OutfitRepository
) : ViewModel() {

    private val _outfits =
        MutableStateFlow<List<OutfitDto>>(emptyList())

    val outfits: StateFlow<List<OutfitDto>> =
        _outfits.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> =
        _errorMessage.asStateFlow()

    init {
        loadOutfits()
    }

    fun loadOutfits() {
        viewModelScope.launch {
            try {
                _errorMessage.value = null
                _outfits.value = outfitRepository.getOutfits()
            } catch (e: Exception) {
                _errorMessage.value =
                    e.message ?: "Failed to load outfits"
            }
        }
    }

    fun saveOutfit(
        name: String,
        occasion: String,
        clothingIds: List<String>,
        plannedDate: String,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            _isSaving.value = true
            _errorMessage.value = null

            try {
                val outfit = OutfitDto(
                    id = UUID.randomUUID().toString(),
                    name = name,
                    occasion = occasion,
                    clothingIds = clothingIds,
                    plannedDate = plannedDate
                )

                outfitRepository.saveOutfit(outfit)
                _outfits.value += outfit
                onResult(true)
            } catch (e: Exception) {
                _errorMessage.value =
                    e.message ?: "Failed to save outfit"
                onResult(false)
            } finally {
                _isSaving.value = false
            }
        }
    }

    fun deleteOutfit(outfitId: String) {
        viewModelScope.launch {
            try {
                outfitRepository.deleteOutfit(outfitId)
                loadOutfits()
            } catch (e: Exception) {
                _errorMessage.value =
                    e.message ?: "Failed to delete outfit"
            }
        }
    }

    fun markOutfitAsWorn(
        outfitId: String,
        isWorn: Boolean
    ) {
        viewModelScope.launch {
            try {
                outfitRepository.markOutfitAsWorn(
                    outfitId,
                    isWorn
                )
                loadOutfits()
            } catch (e: Exception) {
                _errorMessage.value =
                    e.message ?: "Failed to update outfit"
            }
        }
    }


    fun updateOutfit(
        outfit: OutfitDto,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            _isSaving.value = true
            _errorMessage.value = null

            try {
                outfitRepository.updateOutfit(outfit)

                _outfits.value = _outfits.value.map { existing ->
                    if (existing.id == outfit.id) outfit else existing
                }

                onResult(true)
            } catch (e: Exception) {
                _errorMessage.value =
                    e.message ?: "Failed to update outfit"
                onResult(false)
            } finally {
                _isSaving.value = false
            }
        }
    }

}
