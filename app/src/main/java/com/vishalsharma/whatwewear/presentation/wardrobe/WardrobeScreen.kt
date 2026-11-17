package com.vishalsharma.whatwewear.presentation.wardrobe

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vishalsharma.whatwewear.domain.model.ClothingItem
import com.vishalsharma.whatwewear.presentation.components.ChipGroup
import com.vishalsharma.whatwewear.presentation.components.ClothingOptions
import com.vishalsharma.whatwewear.presentation.components.ColorSwatchPicker
import com.vishalsharma.whatwewear.presentation.wardrobe.components.ClothingCard
import com.vishalsharma.whatwewear.presentation.wardrobe.components.EmptyWardrobeState
import com.vishalsharma.whatwewear.ui.theme.AppColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WardrobeScreen(
    clothingItems: List<ClothingItem>,
    isLoading: Boolean = false,
    loadError: Boolean = false,
    onRetry: () -> Unit = {},
    onAddClothingClick: () -> Unit = {},
    onClothingClick: (String) -> Unit = {}
) {
    var selectedCategory by rememberSaveable { mutableStateOf("All") }
    var query by rememberSaveable { mutableStateOf("") }
    var color by rememberSaveable { mutableStateOf("") }
    var season by rememberSaveable { mutableStateOf("") }
    var occasion by rememberSaveable { mutableStateOf("") }
    var showFilters by rememberSaveable { mutableStateOf(false) }

    // Only categories you actually own are shown, each with its count
    val categoryCounts = ClothingOptions.categories
        .map { category -> category to clothingItems.count { it.category.equals(category, true) } }
        .filter { it.second > 0 }
    val activeCategory =
        if (selectedCategory == "All" || categoryCounts.any { it.first == selectedCategory }) selectedCategory
        else "All"

    val filterCount = listOf(color, season, occasion).count { it.isNotBlank() }
    val q = query.trim()

    val filtered = clothingItems.filter { item ->
        (activeCategory == "All" || item.category.equals(activeCategory, true)) &&
                (color.isBlank() || item.color.equals(color, true)) &&
                (season.isBlank() || item.season.equals(season, true)) &&
                (occasion.isBlank() || item.occasion.equals(occasion, true)) &&
                (q.isBlank() || listOf(item.name, item.brand, item.color, item.type)
                    .any { it.contains(q, true) })
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background)
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = "Closet",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 34.sp
                ),
                color = AppColors.Ink
            )
            Text(
                text = if (clothingItems.size == 1) "1 piece" else "${clothingItems.size} pieces",
                color = AppColors.Muted,
                fontSize = 15.sp,
                modifier = Modifier.padding(bottom = 6.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = { Text("Search name, brand, color", color = AppColors.Muted) },
                leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null, tint = AppColors.Muted) },
                singleLine = true,
                shape = RoundedCornerShape(18.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AppColors.Ink,
                    unfocusedBorderColor = AppColors.Border,
                    cursorColor = AppColors.Ink,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .border(1.dp, AppColors.Border, RoundedCornerShape(18.dp))
                    .clickable { showFilters = true },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.Tune, contentDescription = "Filters", tint = AppColors.Ink)
                if (filterCount > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(6.dp)
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(AppColors.Ink),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("$filterCount", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            item {
                CategoryPill("All", clothingItems.size, activeCategory == "All") { selectedCategory = "All" }
            }
            items(categoryCounts) { (category, count) ->
                CategoryPill(category, count, activeCategory == category) { selectedCategory = category }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        when {
            isLoading -> Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp),
                contentAlignment = Alignment.TopCenter
            ) { CircularProgressIndicator(color = AppColors.Ink) }

            loadError -> Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Couldn't load your closet.", color = AppColors.Muted)
                TextButton(onClick = onRetry) { Text("Try again", color = AppColors.Ink) }
            }

            clothingItems.isEmpty() -> EmptyWardrobeState(onAddClothingClick = onAddClothingClick)

            filtered.isEmpty() -> Text(
                text = "No pieces match your search.",
                color = AppColors.Muted,
                modifier = Modifier.padding(top = 32.dp)
            )

            else -> LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                contentPadding = PaddingValues(bottom = 40.dp)
            ) {
                items(items = filtered, key = { it.id }) { item ->
                    ClothingCard(item = item, onClick = { onClothingClick(item.id) })
                }
                if (activeCategory == "All" && q.isBlank() && filterCount == 0) {
                    item(key = "add_piece") {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(0.85f)
                                .clip(RoundedCornerShape(24.dp))
                                .border(1.dp, AppColors.Border, RoundedCornerShape(24.dp))
                                .clickable(onClick = onAddClothingClick),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Outlined.Add, contentDescription = null, tint = AppColors.Ink)
                                Text("Add piece", color = AppColors.Ink, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showFilters) {
        ModalBottomSheet(
            onDismissRequest = { showFilters = false },
            containerColor = AppColors.Background
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
                    .navigationBarsPadding()
                    .padding(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(22.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Filters",
                        style = MaterialTheme.typography.titleLarge.copy(fontFamily = FontFamily.Serif),
                        color = AppColors.Ink
                    )
                    TextButton(onClick = { color = ""; season = ""; occasion = "" }) {
                        Text("Clear all", color = AppColors.Ink)
                    }
                }

                ColorSwatchPicker("Color", ClothingOptions.colors, color) {
                    color = if (color.equals(it, true)) "" else it
                }
                ChipGroup("Season", ClothingOptions.seasons, season, allowClear = true) { season = it }
                ChipGroup("Occasion", ClothingOptions.occasions, occasion, allowClear = true) { occasion = it }

                Button(
                    onClick = { showFilters = false },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppColors.Ink,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        if (filtered.size == 1) "Show 1 piece" else "Show ${filtered.size} pieces",
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryPill(label: String, count: Int, selected: Boolean, onClick: () -> Unit) {
    val shape = RoundedCornerShape(50)
    Text(
        text = "$label  $count",
        color = if (selected) Color.White else AppColors.Ink,
        fontSize = 15.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier
            .clip(shape)
            .background(if (selected) AppColors.Ink else Color.Transparent)
            .border(1.dp, if (selected) AppColors.Ink else AppColors.Border, shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 10.dp)
    )
}