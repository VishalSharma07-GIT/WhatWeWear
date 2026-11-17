package com.vishalsharma.whatwewear.presentation.editclothing

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.vishalsharma.whatwewear.domain.model.ClothingItem
import com.vishalsharma.whatwewear.presentation.components.AppTextField
import com.vishalsharma.whatwewear.presentation.components.ChipGroup
import com.vishalsharma.whatwewear.presentation.components.ClothingOptions
import com.vishalsharma.whatwewear.presentation.components.ColorSwatchPicker
import com.vishalsharma.whatwewear.presentation.components.EditPhotoButton
import com.vishalsharma.whatwewear.presentation.components.ImageEditorDialog
import com.vishalsharma.whatwewear.presentation.components.PinnedActionBar
import com.vishalsharma.whatwewear.presentation.components.PrimaryActionButton
import com.vishalsharma.whatwewear.utils.ImageCompressor
import com.vishalsharma.whatwewear.utils.rememberImageModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private val Ivory = Color(0xFFF4F0EB)
private val Ink = Color(0xFF211B18)

@Composable
fun EditClothingScreen(
    clothingItem: ClothingItem,
    onClothingUpdated: (ClothingItem) -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    var name by rememberSaveable { mutableStateOf(clothingItem.name) }
    var brand by rememberSaveable { mutableStateOf(clothingItem.brand) }
    var category by rememberSaveable { mutableStateOf(clothingItem.category) }
    var color by rememberSaveable { mutableStateOf(clothingItem.color) }
    var size by rememberSaveable { mutableStateOf(clothingItem.size) }
    var season by rememberSaveable { mutableStateOf(clothingItem.season) }
    var occasion by rememberSaveable { mutableStateOf(clothingItem.occasion) }
    // not rememberSaveable: the saved photo can be a large piece of text
    var imageUri by remember { mutableStateOf(clothingItem.imageUri) }
    var isSaving by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var editSource by remember { mutableStateOf<Bitmap?>(null) }

    val photoPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri -> uri?.let { imageUri = it.toString() } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Ivory)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, top = 4.dp, end = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Ink)
            }
            Text(
                text = "Edit piece",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Medium
                ),
                color = Ink
            )
        }

        Box(modifier = Modifier.weight(1f)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(start = 20.dp, top = 8.dp, end = 20.dp, bottom = 130.dp),
                verticalArrangement = Arrangement.spacedBy(22.dp)
            ) {
                Box {
                    AsyncImage(
                        model = rememberImageModel(imageUri),
                        contentDescription = name,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(Color(0xFFF2EBE4))
                            .padding(12.dp)
                    )
                    EditPhotoButton(
                        onClick = {
                            imageUri?.let { src ->
                                scope.launch {
                                    editSource = withContext(Dispatchers.IO) {
                                        runCatching { ImageCompressor.toBitmap(context, src) }.getOrNull()
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(10.dp)
                    )
                }

                TextButton(
                    onClick = {
                        photoPicker.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) { Text("Choose a new photo") }

                AppTextField(value = name, onValueChange = { name = it }, label = "Name")
                AppTextField(value = brand, onValueChange = { brand = it }, label = "Brand")

                ChipGroup("Category", ClothingOptions.categories, category) { category = it }
                ColorSwatchPicker("Color", ClothingOptions.colors, color) { color = it }
                ChipGroup("Size", ClothingOptions.sizes, size, allowClear = true) { size = it }
                ChipGroup("Season", ClothingOptions.seasons, season, allowClear = true) { season = it }
                ChipGroup("Occasion", ClothingOptions.occasions, occasion, allowClear = true) { occasion = it }
            }

            PinnedActionBar(
                modifier = Modifier.align(Alignment.BottomCenter),
                backgroundColor = Ivory
            ) {
                PrimaryActionButton(
                    label = "Save changes",
                    enabled = category.isNotBlank(),
                    loading = isSaving,
                    disabledLabel = "Choose a category",
                    onClick = {
                        isSaving = true
                        onClothingUpdated(
                            clothingItem.copy(
                                name = name.ifBlank { clothingItem.name },
                                brand = brand,
                                category = category,
                                color = color,
                                size = size,
                                season = season,
                                occasion = occasion,
                                imageUri = imageUri
                            )
                        )
                    }
                )
            }
        }
    }

    editSource?.let { bitmap ->
        ImageEditorDialog(
            source = bitmap,
            onDismiss = { editSource = null },
            onDone = { edited ->
                editSource = null
                scope.launch {
                    imageUri = withContext(Dispatchers.Default) {
                        ImageCompressor.bitmapToDataUri(edited)
                    }
                }
            }
        )
    }
}