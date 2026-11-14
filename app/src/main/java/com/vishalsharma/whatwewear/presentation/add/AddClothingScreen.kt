package com.vishalsharma.whatwewear.presentation.add

import android.content.ContentValues
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.vishalsharma.whatwewear.domain.model.ClothingItem
import com.vishalsharma.whatwewear.presentation.components.AppTextField
import com.vishalsharma.whatwewear.presentation.components.ChipGroup
import com.vishalsharma.whatwewear.presentation.components.ClothingOptions
import com.vishalsharma.whatwewear.presentation.components.ColorSwatchPicker
import com.vishalsharma.whatwewear.presentation.components.EditPhotoButton
import com.vishalsharma.whatwewear.presentation.components.ImageEditorDialog
import com.vishalsharma.whatwewear.utils.ImageCompressor
import com.vishalsharma.whatwewear.utils.rememberImageModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private val Brown = Color(0xFF59423C)

// Makes sure an AI answer matches one of our options
private fun match(value: String, options: List<String>): String =
    options.firstOrNull { it.equals(value.trim(), ignoreCase = true) } ?: ""

@Composable
fun AddClothingScreen(
    initialPhoto: String? = null,
    onClothingSaved: (ClothingItem, (Boolean) -> Unit) -> Unit = { _, _ -> },
    viewModel: AddClothingViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()

    var imageUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    var cameraUri by rememberSaveable { mutableStateOf<Uri?>(null) }

    var name by rememberSaveable { mutableStateOf("") }
    var brand by rememberSaveable { mutableStateOf("") }
    var category by rememberSaveable { mutableStateOf("") }
    var color by rememberSaveable { mutableStateOf("") }
    var size by rememberSaveable { mutableStateOf("") }
    var season by rememberSaveable { mutableStateOf("") }
    var occasion by rememberSaveable { mutableStateOf("") }
    var type by rememberSaveable { mutableStateOf("") }
    var showMore by rememberSaveable { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }
    var saveFailed by remember { mutableStateOf(false) }

    var useOriginal by remember { mutableStateOf(false) }
    var editedImage by remember { mutableStateOf<String?>(null) }
    var editSource by remember { mutableStateOf<Bitmap?>(null) }
    val scope = rememberCoroutineScope()

    val cleanImage = (state as? AnalyzeState.Done)?.cleanImage
    val shownImage = editedImage
        ?: if (useOriginal) imageUri?.toString() else (cleanImage ?: imageUri?.toString())
    val shownModel = rememberImageModel(shownImage)

    fun onPhotoReady(uri: Uri) {
        imageUri = uri
        viewModel.analyze(uri)
    }

    LaunchedEffect(initialPhoto) {
        if (initialPhoto != null && imageUri == null) {
            onPhotoReady(Uri.parse(initialPhoto))
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) cameraUri?.let { onPhotoReady(it) }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { onPhotoReady(it) }
    }

    // When the AI finishes, fill the form
    LaunchedEffect(state) {
        (state as? AnalyzeState.Done)?.result?.let { r ->
            name = r.name
            category = match(r.category, ClothingOptions.categories)
            color = match(r.color, ClothingOptions.colors)
            brand = r.brand
            season = match(r.season, ClothingOptions.seasons)
            occasion = match(r.occasion, ClothingOptions.occasions)
            type = r.type
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text(
            text = "Add Clothing",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Medium
            )
        )
        Spacer(Modifier.height(24.dp))

        if (imageUri == null) {
            // STEP 1: choose photo
            Text(
                "Take a photo of your clothing and we'll fill in the details for you.",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = {
                    val values = ContentValues().apply {
                        put(MediaStore.Images.Media.DISPLAY_NAME, "wardrobe_${System.currentTimeMillis()}.jpg")
                        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                    }
                    val uri = context.contentResolver.insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values
                    )
                    if (uri != null) {
                        cameraUri = uri
                        cameraLauncher.launch(uri)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Brown, contentColor = Color.White)
            ) { Text("Take photo") }
            Spacer(Modifier.height(12.dp))
            OutlinedButton(
                onClick = {
                    galleryLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp)
            ) { Text("Choose from gallery") }
        } else {
            // STEP 2: preview + analyzing + review
            Box {
                AsyncImage(
                    model = shownModel,
                    contentDescription = "Selected clothing",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .clip(RoundedCornerShape(16.dp))
                )
                EditPhotoButton(
                    onClick = {
                        shownImage?.let { src ->
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

            TextButton(onClick = {
                editedImage = null
                useOriginal = false
                imageUri = null
                name = ""; brand = ""; category = ""; color = ""
                size = ""; season = ""; occasion = ""; type = ""
                saveFailed = false
                viewModel.reset()
            }) { Text("Retake / choose another") }

            if (cleanImage != null) {
                TextButton(onClick = {
                    editedImage = null
                    useOriginal = !useOriginal
                }) {
                    Text(if (useOriginal) "Use cutout" else "Use original photo")
                }
            }

            when (state) {
                AnalyzeState.Loading -> {
                    Spacer(Modifier.height(24.dp))
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) { CircularProgressIndicator(color = Brown) }
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "Analyzing your piece…",
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                else -> {
                    if (state == AnalyzeState.Failed) {
                        Text(
                            "We couldn't analyze the photo automatically. Please fill in the details.",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        TextButton(onClick = { imageUri?.let { viewModel.analyze(it) } }) {
                            Text("Try again")
                        }
                        Spacer(Modifier.height(12.dp))
                    }

                    AppTextField(value = name, onValueChange = { name = it }, label = "Name")
                    Spacer(Modifier.height(12.dp))
                    AppTextField(value = brand, onValueChange = { brand = it }, label = "Brand")

                    Spacer(Modifier.height(24.dp))
                    ChipGroup("Category", ClothingOptions.categories, category) { category = it }

                    Spacer(Modifier.height(24.dp))
                    ColorSwatchPicker("Color", ClothingOptions.colors, color) { color = it }

                    TextButton(onClick = { showMore = !showMore }) {
                        Text(if (showMore) "Hide details" else "More details")
                    }

                    if (showMore) {
                        Spacer(Modifier.height(8.dp))
                        ChipGroup("Size", ClothingOptions.sizes, size, allowClear = true) { size = it }
                        Spacer(Modifier.height(24.dp))
                        ChipGroup("Season", ClothingOptions.seasons, season, allowClear = true) { season = it }
                        Spacer(Modifier.height(24.dp))
                        ChipGroup("Occasion", ClothingOptions.occasions, occasion, allowClear = true) { occasion = it }
                    }

                    Spacer(Modifier.height(24.dp))
                    Button(
                        enabled = category.isNotBlank() && !isSaving,
                        onClick = {
                            isSaving = true
                            saveFailed = false
                            onClothingSaved(
                                ClothingItem(
                                    id = System.currentTimeMillis().toString(),
                                    name = name.ifBlank { "New piece" },
                                    brand = brand,
                                    category = category,
                                    imageUri = shownImage,
                                    color = color,
                                    size = size,
                                    season = season,
                                    occasion = occasion,
                                    type = type
                                )
                            ) { success ->
                                if (!success) {
                                    isSaving = false
                                    saveFailed = true
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Brown, contentColor = Color.White)
                    ) { Text(if (isSaving) "Saving…" else "Save") }

                    if (category.isBlank()) {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Choose a category to save.",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    if (saveFailed) {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Couldn't save. Check your internet and try again.",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
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
                    editedImage = withContext(Dispatchers.Default) {
                        ImageCompressor.bitmapToDataUri(edited)
                    }
                }
            }
        )
    }
}