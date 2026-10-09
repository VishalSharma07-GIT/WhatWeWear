package com.vishalsharma.whatwewear.presentation.add

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import coil.compose.rememberAsyncImagePainter
import com.vishalsharma.whatwewear.domain.model.ClothingItem

@Composable
fun AddClothingScreen(
    onClothingSaved: (ClothingItem) -> Unit ={}
) {

    val context = LocalContext.current

    var selectedImageUri by remember {
        mutableStateOf<Uri?>(null)
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->

        uri?.let {

            val contentResolver = context.contentResolver

            contentResolver.takePersistableUriPermission(
                it,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )

            selectedImageUri = it
        }
    }
    var clothingName by remember {
        mutableStateOf("")
    }

    var brand by remember {
        mutableStateOf("")
    }

    var category by remember {
        mutableStateOf("")
    }

    var categoryExpanded by remember {
        mutableStateOf(false)
    }



    var color by remember {
        mutableStateOf("")
    }
    var colorExpanded by remember {
        mutableStateOf(false)
    }

    var size by remember {
        mutableStateOf("")
    }
    var sizeExpanded by remember {
        mutableStateOf(false)
    }
    var season by remember {
        mutableStateOf("")
    }

    var seasonExpanded by remember {
        mutableStateOf(false)
    }

    var occasion by remember {
        mutableStateOf("")
    }

    var occasionExpanded by remember {
        mutableStateOf(false)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.Top
    ) {

        Text(
            text = "Add Clothing"
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                imagePickerLauncher.launch(arrayOf("image/*"))
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Choose Clothing Image"
            )
        }
        selectedImageUri?.let { uri ->

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Image(
                painter = rememberAsyncImagePainter(uri),
                contentDescription = "Selected clothing image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(
                        RoundedCornerShape(16.dp)
                    ),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = clothingName,
            onValueChange = {
                clothingName = it
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Clothing Name")
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = brand,
            onValueChange = {
                brand = it
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Brand")
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier.fillMaxWidth()
        ) {

            OutlinedTextField(
                value = category,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text("Category")
                },
                trailingIcon = {
                    Text("▼")
                }
            )

            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable {
                        categoryExpanded = true
                    }
            )

            DropdownMenu(
                expanded = categoryExpanded,
                onDismissRequest = {
                    categoryExpanded = false
                }
            ) {
                listOf(
                    "Tops",
                    "Bottoms",
                    "Dresses",
                    "Outerwear",
                    "Shoes",
                    "Accessories"
                ).forEach { item ->

                    DropdownMenuItem(
                        text = {
                            Text(item)
                        },
                        onClick = {
                            category = item
                            categoryExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier.fillMaxWidth()
        ) {

            OutlinedTextField(
                value = color,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text("Color")
                },
                trailingIcon = {
                    Text("▼")
                }
            )

            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable {
                        colorExpanded = true
                    }
            )

            DropdownMenu(
                expanded = colorExpanded,
                onDismissRequest = {
                    colorExpanded = false
                }
            ) {
                listOf(
                    "Black",
                    "White",
                    "Blue",
                    "Grey",
                    "Brown",
                    "Green",
                    "Red",
                    "Other"
                ).forEach { item ->

                    DropdownMenuItem(
                        text = {
                            Text(item)
                        },
                        onClick = {
                            color = item
                            colorExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier.fillMaxWidth()
        ) {

            OutlinedTextField(
                value = size,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text("Size")
                },
                trailingIcon = {
                    Text("▼")
                }
            )

            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable {
                        sizeExpanded = true
                    }
            )

            DropdownMenu(
                expanded = sizeExpanded,
                onDismissRequest = {
                    sizeExpanded = false
                }
            ) {
                listOf(
                    "XS",
                    "S",
                    "M",
                    "L",
                    "XL",
                    "XXL"
                ).forEach { item ->

                    DropdownMenuItem(
                        text = {
                            Text(item)
                        },
                        onClick = {
                            size = item
                            sizeExpanded = false
                        }
                    )
                }
            }

        }
        Spacer(modifier = Modifier.height(12.dp))

        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = season,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Season") },
                trailingIcon = { Text("▼") }
            )

            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable { seasonExpanded = true }
            )

            DropdownMenu(
                expanded = seasonExpanded,
                onDismissRequest = { seasonExpanded = false }
            ) {
                listOf("Spring", "Summer", "Autumn", "Winter").forEach { item ->
                    DropdownMenuItem(
                        text = { Text(item) },
                        onClick = {
                            season = item
                            seasonExpanded = false
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))

        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = occasion,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Occasion") },
                trailingIcon = { Text("▼") }
            )

            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable { occasionExpanded = true }
            )

            DropdownMenu(
                expanded = occasionExpanded,
                onDismissRequest = { occasionExpanded = false }
            ) {
                listOf("Casual", "Formal", "Party", "Work", "Sports").forEach { item ->
                    DropdownMenuItem(
                        text = { Text(item) },
                        onClick = {
                            occasion = item
                            occasionExpanded = false
                        }
                    )
                }
            }
        }


        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {

                val clothingItem = ClothingItem(
                    id = System.currentTimeMillis().toString(),
                    name = clothingName,
                    brand = brand,
                    category = category,
                    imageUri = selectedImageUri?.toString(),
                    color = color,
                    size = size,
                    season = season,
                    occasion = occasion
                )

                onClothingSaved(clothingItem)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Save Clothing"
            )
        }
    }
}