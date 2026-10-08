package com.vishalsharma.whatwewear.presentation.editclothing

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
import com.vishalsharma.whatwewear.domain.model.ClothingItem
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.rememberAsyncImagePainter

@Composable
fun EditClothingScreen(
    clothingItem: ClothingItem,
    onClothingUpdated: (ClothingItem) -> Unit = {}
) {

    var clothingName by remember {
        mutableStateOf(clothingItem.name)
    }

    var brand by remember {
        mutableStateOf(clothingItem.brand)
    }

    var category by remember {
        mutableStateOf(clothingItem.category)
    }

    var color by remember {
        mutableStateOf(clothingItem.color)
    }

    var size by remember {
        mutableStateOf(clothingItem.size)
    }
    var categoryExpanded by remember {
        mutableStateOf(false)
    }

    var colorExpanded by remember {
        mutableStateOf(false)
    }

    var sizeExpanded by remember {
        mutableStateOf(false)
    }
    val categories = listOf(
        "Tops",
        "Bottoms",
        "Shoes",
        "Accessories"
    )

    val colors = listOf(
        "Black",
        "White",
        "Blue",
        "Grey",
        "Brown",
        "Green",
        "Red",
        "Other"
    )

    val sizes = listOf(
        "XS",
        "S",
        "M",
        "L",
        "XL",
        "XXL"
    )
    val context = LocalContext.current

    var selectedImageUri by remember {
        mutableStateOf(
            clothingItem.imageUri?.let { Uri.parse(it) }
        )
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {


        Text(
            text = "Edit Clothing"
        )
        selectedImageUri?.let { uri ->

            Image(
                painter = rememberAsyncImagePainter(uri),
                contentDescription = clothingItem.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(
                        RoundedCornerShape(16.dp)
                    ),
                contentScale = ContentScale.Crop
            )
        }
        Button(
            onClick = {
                imagePickerLauncher.launch(
                    arrayOf("image/*")
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Choose New Image")
        }

        OutlinedTextField(
            value = clothingName,
            onValueChange = {
                clothingName = it
            },
            label = {
                Text("Clothing Name")
            },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = brand,
            onValueChange = {
                brand = it
            },
            label = {
                Text("Brand")
            },
            modifier = Modifier.fillMaxWidth()
        )

        Box(
            modifier = Modifier.fillMaxWidth()
        ) {

            OutlinedTextField(
                value = category,
                onValueChange = {},
                readOnly = true,
                label = {
                    Text("Category")
                },
                modifier = Modifier.fillMaxWidth()
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
                categories.forEach { option ->

                    DropdownMenuItem(
                        text = {
                            Text(option)
                        },
                        onClick = {
                            category = option
                            categoryExpanded = false
                        }
                    )
                }
            }
        }

        Box(
            modifier = Modifier.fillMaxWidth()
        ) {

            OutlinedTextField(
                value = color,
                onValueChange = {},
                readOnly = true,
                label = {
                    Text("Color")
                },
                modifier = Modifier.fillMaxWidth()
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
                colors.forEach { option ->

                    DropdownMenuItem(
                        text = {
                            Text(option)
                        },
                        onClick = {
                            color = option
                            colorExpanded = false
                        }
                    )
                }
            }
        }

        Box(
            modifier = Modifier.fillMaxWidth()
        ) {

            OutlinedTextField(
                value = size,
                onValueChange = {},
                readOnly = true,
                label = {
                    Text("Size")
                },
                modifier = Modifier.fillMaxWidth()
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
                sizes.forEach { option ->

                    DropdownMenuItem(
                        text = {
                            Text(option)
                        },
                        onClick = {
                            size = option
                            sizeExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Button(
            onClick = {

                val updatedItem = clothingItem.copy(
                    name = clothingName,
                    brand = brand,
                    category = category,
                    color = color,
                    size = size,
                    imageUri = selectedImageUri?.toString()
                )

                onClothingUpdated(updatedItem)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Changes")
        }
    }
}