package com.vishalsharma.whatwewear.presentation.add

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vishalsharma.whatwewear.ai.AiClothingAnalyzer
import com.vishalsharma.whatwewear.ai.AiClothingResult
import com.vishalsharma.whatwewear.ai.GarmentExtractor
import com.vishalsharma.whatwewear.utils.ImageCompressor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

sealed interface AnalyzeState {
    object Idle : AnalyzeState
    object Loading : AnalyzeState
    data class Done(val result: AiClothingResult, val cleanImage: String?) : AnalyzeState
    object Failed : AnalyzeState
}

@HiltViewModel
class AddClothingViewModel @Inject constructor(
    private val analyzer: AiClothingAnalyzer,
    private val extractor: GarmentExtractor
) : ViewModel() {

    private val _state = MutableStateFlow<AnalyzeState>(AnalyzeState.Idle)
    val state: StateFlow<AnalyzeState> = _state.asStateFlow()

    fun analyze(uri: Uri) {
        viewModelScope.launch {
            _state.value = AnalyzeState.Loading
            _state.value = try {
                val bitmap = withContext(Dispatchers.IO) { analyzer.loadBitmap(uri) }
                val result = analyzer.analyze(bitmap)

                // Cut out only the garment. If this fails, we still keep the AI result.
                val clean = try {
                    ImageCompressor.bitmapToDataUri(extractor.extract(bitmap, result.box))
                } catch (e: Exception) {
                    Log.e("AiAnalyzer", "Cutout failed", e)
                    null
                }
                AnalyzeState.Done(result, clean)
            } catch (e: Exception) {
                Log.e("AiAnalyzer", "Analysis failed", e)
                AnalyzeState.Failed
            }
        }
    }

    fun reset() {
        _state.value = AnalyzeState.Idle
    }
}