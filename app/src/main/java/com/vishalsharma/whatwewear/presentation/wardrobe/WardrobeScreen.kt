package com.vishalsharma.whatwewear.presentation.wardrobe

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vishalsharma.whatwewear.presentation.wardrobe.components.ClothingCard
import com.vishalsharma.whatwewear.presentation.wardrobe.components.EmptyWardrobeState

@Composable
fun WardrobeScreen(
    onAddClothingClick: () -> Unit = {},
    onClothingClick: (String) -> Unit = {}
) {

    val categories = listOf(
        "All",
        "Tops",
        "Bottoms",
        "Shoes",
        "Accessories"
    )

    var selectedCategory by remember {
        mutableStateOf("All")
    }

    val filteredItems = if (selectedCategory == "All") {
        sampleClothingItems
    } else {
        sampleClothingItems.filter {
            it.category == selectedCategory
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "My Wardrobe",
                style = MaterialTheme.typography.headlineMedium
            )

            IconButton(
                onClick = onAddClothingClick
            ) {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = "Add clothing"
                )
            }
        }

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            categories.forEach { category ->

                FilterChip(
                    selected = selectedCategory == category,
                    onClick = {
                        selectedCategory = category
                    },
                    label = {
                        Text(text = category)
                    }
                )
            }
        }

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        Text(
            text = "${filteredItems.size} items",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        if (filteredItems.isEmpty()) {

            EmptyWardrobeState(
                onAddClothingClick = onAddClothingClick
            )

        } else {

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                items(
                    items = filteredItems,
                    key = { it.id }
                ) { item ->

                    ClothingCard(
                        item = item,
                        onClick = {
                            onClothingClick(item.id)
                        }
                    )
                }
            }
        }
    }
}