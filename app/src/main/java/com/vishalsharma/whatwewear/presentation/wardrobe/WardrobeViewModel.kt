package com.vishalsharma.whatwewear.presentation.wardrobe

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vishalsharma.whatwewear.data.mapper.toDomain
import com.vishalsharma.whatwewear.data.mapper.toDto
import com.vishalsharma.whatwewear.data.repository.WardrobeRepository
import com.vishalsharma.whatwewear.domain.model.ClothingItem
import com.vishalsharma.whatwewear.utils.ImageCompressor
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull

private const val SAVE_WAIT_MS = 5000L

@HiltViewModel
class WardrobeViewModel @Inject constructor(
    private val wardrobeRepository: WardrobeRepository,
    @param:ApplicationContext private val context: Context
) : ViewModel() {

    private val _clothingItems = MutableStateFlow<List<ClothingItem>>(emptyList())
    val clothingItems: StateFlow<List<ClothingItem>> = _clothingItems.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _loadError = MutableStateFlow(false)
    val loadError: StateFlow<Boolean> = _loadError.asStateFlow()

    private var observeJob: Job? = null

    init {
        observeClothing()
    }

    // Live updates: add, edit and delete refresh the list automatically
    private fun observeClothing() {
        observeJob?.cancel()
        observeJob = viewModelScope.launch {
            wardrobeRepository.observeClothing()
                .catch {
                    Log.e("WardrobeFirebase", "Failed to load clothing", it)
                    _loadError.value = true
                    _isLoading.value = false
                }
                .collect { dtoList ->
                    _clothingItems.value = dtoList.map { it.toDomain() }
                    _loadError.value = false
                    _isLoading.value = false
                }
        }
    }

    fun retry() {
        _isLoading.value = true
        _loadError.value = false
        observeClothing()
    }

    // Uploads nothing: a local phone photo is shrunk and stored as text inside the item
    private suspend fun resolveImageUrl(item: ClothingItem): String {
        val uri = item.imageUri.orEmpty()
        return if (uri.startsWith("content://") || uri.startsWith("file://")) {
            withContext(Dispatchers.IO) { ImageCompressor.toDataUri(context, Uri.parse(uri)) }
        } else uri
    }

    fun addClothing(item: ClothingItem, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val imageUrl = resolveImageUrl(item)
                // Offline, Firestore only answers once the server is reachable.
                // After a few seconds we treat the save as done: it is stored on the phone and syncs later.
                val answered = withTimeoutOrNull(SAVE_WAIT_MS) {
                    wardrobeRepository.saveClothing(item.toDto().copy(imageUrl = imageUrl))
                    true
                }
                if (answered == null) {
                    Log.w("WardrobeFirebase", "No server answer yet (offline?). Saved on the phone, will sync.")
                }
                onResult(true)
            } catch (e: Exception) {
                Log.e("WardrobeFirebase", "Failed to save clothing", e)
                onResult(false)
            }
        }
    }

    fun updateClothing(item: ClothingItem) {
        viewModelScope.launch {
            try {
                val existing = getClothingById(item.id)
                val imageUrl = resolveImageUrl(item)
                wardrobeRepository.saveClothing(
                    item.toDto().copy(
                        imageUrl = imageUrl,
                        createdAt = existing?.createdAt ?: item.createdAt
                    )
                )
            } catch (e: Exception) {
                Log.e("WardrobeFirebase", "Failed to update clothing", e)
            }
        }
    }

    fun getClothingById(id: String): ClothingItem? =
        _clothingItems.value.find { it.id == id }

    fun deleteClothing(clothingId: String) {
        viewModelScope.launch {
            try {
                wardrobeRepository.deleteClothing(clothingId)
            } catch (e: Exception) {
                Log.e("WardrobeFirebase", "Failed to delete clothing", e)
            }
        }
    }
}