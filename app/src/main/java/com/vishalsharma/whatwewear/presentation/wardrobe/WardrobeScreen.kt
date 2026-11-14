package com.vishalsharma.whatwewear.presentation.wardrobe

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import com.vishalsharma.whatwewear.presentation.wardrobe.components.ClothingCard
import com.vishalsharma.whatwewear.presentation.wardrobe.components.EmptyWardrobeState

private val Ivory = Color(0xFFFAF7F2)
private val Ink = Color(0xFF211B18)
private val Muted = Color(0xFF89827B)
private val Brown = Color(0xFF59423C)

@Composable
fun WardrobeScreen(
    clothingItems: List<ClothingItem>,
    isLoading: Boolean = false,
    loadError: Boolean = false,
    onRetry: () -> Unit = {},
    onAddClothingClick: () -> Unit = {},
    onClothingClick: (String) -> Unit = {}
) {
    val categories = listOf("All", "Tops", "Bottoms", "Dresses", "Outerwear", "Shoes", "Accessories")

    var selectedCategory by rememberSaveable { mutableStateOf("All") }
    var searching by rememberSaveable { mutableStateOf(false) }
    var query by rememberSaveable { mutableStateOf("") }

    val filtered = clothingItems.filter { item ->
        val matchesCategory =
            selectedCategory == "All" || item.category.equals(selectedCategory, ignoreCase = true)
        val q = query.trim()
        val matchesQuery = q.isBlank() || listOf(item.name, item.brand, item.color, item.type)
            .any { it.contains(q, ignoreCase = true) }
        matchesCategory && matchesQuery
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Ivory)
            .padding(horizontal = 20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Closet",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp
                ),
                color = Ink
            )
            IconButton(onClick = {
                searching = !searching
                if (!searching) query = ""
            }) {
                Icon(
                    imageVector = if (searching) Icons.Outlined.Close else Icons.Outlined.Search,
                    contentDescription = if (searching) "Close search" else "Search",
                    tint = Muted
                )
            }
        }

        if (searching) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = { Text("Search name, brand or color") },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items(categories) { category ->
                val selected = selectedCategory == category
                Text(
                    text = category,
                    color = if (selected) Color.White else Brown,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(if (selected) Brown else Color(0xFFEFE3DE))
                        .clickable { selectedCategory = category }
                        .padding(horizontal = 18.dp, vertical = 10.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (filtered.size == 1) "1 piece" else "${filtered.size} pieces",
            style = MaterialTheme.typography.bodyMedium,
            color = Muted
        )

        Spacer(modifier = Modifier.height(12.dp))

        when {
            isLoading -> Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp),
                contentAlignment = Alignment.TopCenter
            ) { CircularProgressIndicator(color = Brown) }

            loadError -> Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Couldn't load your closet.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Muted
                )
                TextButton(onClick = onRetry) { Text("Try again") }
            }

            clothingItems.isEmpty() -> EmptyWardrobeState(onAddClothingClick = onAddClothingClick)

            filtered.isEmpty() -> Text(
                text = "No pieces match your search.",
                style = MaterialTheme.typography.bodyMedium,
                color = Muted,
                modifier = Modifier.padding(top = 32.dp)
            )

            else -> LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(top = 4.dp, bottom = 40.dp)
            ) {
                items(items = filtered, key = { it.id }) { item ->
                    ClothingCard(item = item, onClick = { onClothingClick(item.id) })
                }
            }
        }
    }
}