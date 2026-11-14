package com.vishalsharma.whatwewear.ai

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.ai.GenerativeModel
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import com.vishalsharma.whatwewear.utils.ImageCompressor
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.json.JSONObject
import javax.inject.Inject

data class AiClothingResult(
    val name: String = "",
    val category: String = "Tops",
    val type: String = "",
    val color: String = "Other",
    val brand: String = "",
    val season: String = "",
    val occasion: String = "",
    val box: List<Int> = emptyList()   // [ymin, xmin, ymax, xmax] in 0..1000
)

class AiClothingAnalyzer @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    private val modelNames = listOf(

        "gemini-3.5-flash-lite"

    )

    private fun buildModel(name: String): GenerativeModel =
        Firebase.ai(backend = GenerativeBackend.googleAI())
            .generativeModel(
                modelName = name,
                generationConfig = generationConfig { responseMimeType = "application/json" }
            )

    private val prompt = """
        You are a wardrobe assistant. The photo may show a person wearing clothes, a background, or other objects.
        Pick the ONE most prominent clothing item or accessory in the photo and describe ONLY that item.
        Return ONLY JSON:
        {
          "name": "short descriptive name, e.g. Black acid-wash graphic t-shirt",
          "category": one of "Tops","Bottoms","Dresses","Outerwear","Shoes","Accessories",
          "type": "specific type, e.g. t-shirt, jeans, sneakers, watch",
          "color": one of "Black","White","Blue","Grey","Brown","Green","Red","Other",
          "brand": "brand ONLY if clearly visible, otherwise empty string",
          "season": one of "Spring","Summer","Autumn","Winter",
          "occasion": one of "Casual","Formal","Party","Work","Sports",
          "box_2d": [ymin, xmin, ymax, xmax]
        }
        box_2d is a tight bounding box around ONLY that item (not the person, not the background),
        as integers from 0 to 1000 relative to the image size.
        Never guess the brand. If unsure about a field, use your best estimate.
    """.trimIndent()

    suspend fun analyze(bitmap: Bitmap): AiClothingResult = withContext(Dispatchers.IO) {
        var lastError: Exception? = null

        for (name in modelNames) {
            for (attempt in 1..2) {
                try {
                    val response = buildModel(name).generateContent(
                        content {
                            image(bitmap)
                            text(prompt)
                        }
                    )
                    val json = JSONObject(response.text.orEmpty().trim())
                    val boxArray = json.optJSONArray("box_2d")
                    val box = if (boxArray != null && boxArray.length() == 4)
                        List(4) { boxArray.optInt(it) } else emptyList()
                    Log.d("AiAnalyzer", "Success with model $name, box=$box")
                    return@withContext AiClothingResult(
                        name = json.optString("name"),
                        category = json.optString("category", "Tops"),
                        type = json.optString("type"),
                        color = json.optString("color", "Other"),
                        brand = json.optString("brand"),
                        season = json.optString("season"),
                        occasion = json.optString("occasion"),
                        box = box
                    )
                } catch (e: Exception) {
                    lastError = e
                    Log.w("AiAnalyzer", "Model $name attempt $attempt failed: ${e.message}")
                    delay(1500)
                }
            }
        }
        throw lastError ?: IllegalStateException("AI analysis failed")
    }

    fun loadBitmap(uri: Uri): Bitmap {
        val dataUri = ImageCompressor.toDataUri(context, uri, maxSize = 1024, quality = 80)
        val bytes = Base64.decode(dataUri.substringAfter("base64,"), Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }
}