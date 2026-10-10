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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.vishalsharma.whatwewear.domain.model.ClothingItem
import com.vishalsharma.whatwewear.presentation.wardrobe.components.ClothingCard
import com.vishalsharma.whatwewear.presentation.wardrobe.components.EmptyWardrobeState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Person
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.vishalsharma.whatwewear.presentation.wardrobe.components.WardrobeFilter
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.ui.unit.sp

@Composable
fun WardrobeScreen(
    clothingItems: List<ClothingItem>,
    onAddClothingClick: () -> Unit = {},
    onClothingClick: (String) -> Unit = {}
) {
    val categories = listOf(
        "All",
        "Tops",
        "Bottoms",
        "Dresses",
        "Outerwear",
        "Shoes",
        "Accessories"
    )

    var selectedCategory by remember {
        mutableStateOf("All")
    }
    var selectedColor by remember {
        mutableStateOf("All Colors")
    }

    var selectedSeason by remember {
        mutableStateOf("All Seasons")
    }

    var selectedOccasion by remember {
        mutableStateOf("All Occasions")
    }

    var expandedFilter by remember {
        mutableStateOf("")
    }


    val filteredItems = clothingItems.filter { item ->
        val matchesCategory =
            selectedCategory == "All" ||
                    item.category.equals(selectedCategory, ignoreCase = true)

        val matchesColor =
            selectedColor == "All Colors" ||
                    item.color.equals(selectedColor, ignoreCase = true)

        val matchesSeason =
            selectedSeason == "All Seasons" ||
                    item.season.equals(selectedSeason, ignoreCase = true)

        val matchesOccasion =
            selectedOccasion == "All Occasions" ||
                    item.occasion.equals(selectedOccasion, ignoreCase = true)

        matchesCategory &&
                matchesColor &&
                matchesSeason &&
                matchesOccasion
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAF7F2))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {


            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(76.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {},
                    modifier = Modifier.size(44.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Menu,
                        contentDescription = "Open menu",
                        tint = Color(0xFF77716C)
                    )
                }

                Text(
                    text = "WWW",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 40.sp,
                        letterSpacing = 1.sp
                    ),
                    color = Color(0xFF211B18)
                )

                IconButton(
                    onClick = {},
                    modifier = Modifier
                        .size(44.dp)
                        .background(
                            color = Color(0xFFE7DED4),
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = "Profile",
                        tint = Color(0xFF77716C)
                    )
                }
            }


            Spacer(
            modifier = Modifier.height(12.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(28.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            categories.forEach { category ->

                val isSelected = selectedCategory == category

                Text(
                    text = category,
                    color = if (isSelected) {
                        Color(0xFF29231F)
                    } else {
                        Color(0xFF89827B)
                    },
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = if (isSelected) {
                            FontWeight.Bold
                        } else {
                            FontWeight.Medium
                        }
                    ),
                    modifier = Modifier.clickable {
                        selectedCategory = category
                    }
                )
            }
        }

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            WardrobeFilter(
                label = "Color",
                options = listOf(
                    "All Colors",
                    "Black",
                    "White",
                    "Blue",
                    "Grey",
                    "Brown",
                    "Green",
                    "Red",
                    "Other"
                ),
                selectedValue = selectedColor,
                expanded = expandedFilter == "color",
                onExpandedChange = {
                    expandedFilter = if (it) "color" else ""
                },
                onOptionSelected = {
                    selectedColor = it
                }
            )

            WardrobeFilter(
                label = "Season",
                options = listOf(
                    "All Seasons",
                    "Spring",
                    "Summer",
                    "Autumn",
                    "Winter"
                ),
                selectedValue = selectedSeason,
                expanded = expandedFilter == "season",
                onExpandedChange = {
                    expandedFilter = if (it) "season" else ""
                },
                onOptionSelected = {
                    selectedSeason = it
                }
            )

            WardrobeFilter(
                label = "Occasion",
                options = listOf(
                    "All Occasions",
                    "Casual",
                    "Formal",
                    "Party",
                    "Work",
                    "Sports"
                ),
                selectedValue = selectedOccasion,
                expanded = expandedFilter == "occasion",
                onExpandedChange = {
                    expandedFilter = if (it) "occasion" else ""
                },
                onOptionSelected = {
                    selectedOccasion = it
                }
            )
        }


        Text(
            text = "${filteredItems.size} items",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        if (filteredItems.isEmpty()) {

            EmptyWardrobeState(
                onAddClothingClick = onAddClothingClick
            )

        } else {

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 96.dp)
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

        FloatingActionButton(
            onClick = onAddClothingClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            containerColor = Color(0xFF29231F),
            contentColor = Color(0xFFFAF7F2),
            shape = CircleShape
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Add clothing"
            )
        }
    }
}