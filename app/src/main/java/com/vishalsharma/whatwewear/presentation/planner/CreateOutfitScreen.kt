
package com.vishalsharma.whatwewear.presentation.planner

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.FilterChip
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.vishalsharma.whatwewear.domain.model.ClothingItem
import java.time.LocalDate
import java.time.format.DateTimeParseException

private val PlannerBackground = Color(0xFFFAF7F2)
private val PlannerBrown = Color(0xFFB8906D)
private val PlannerText = Color(0xFF211B18)

@Composable
fun CreateOutfitScreen(
    clothingItems: List<ClothingItem>,
    onBackClick: () -> Unit,
    onSaveClick: (
        String,
        String,
        List<String>,
        String
    ) -> Unit
) {
    var outfitName by remember { mutableStateOf("") }
    var selectedOccasion by remember { mutableStateOf("Casual") }
    var selectedDate by remember { mutableStateOf("") }
    var selectedClothingIds by remember {
        mutableStateOf(setOf<String>())
    }
    var dateError by remember { mutableStateOf(false) }

    val occasions = listOf(
        "Casual", "College", "Work",
        "Party", "Wedding", "Sports"
    )
    fun isValidDate(date: String): Boolean {
        return try {
            LocalDate.parse(date.trim())
            true
        } catch (e: DateTimeParseException) {
            false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PlannerBackground)
            .padding(20.dp)
    ) {
        Text(
            text = "Create Outfit",
            style = MaterialTheme.typography.headlineMedium,
            color = PlannerText
        )

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = outfitName,
            onValueChange = { outfitName = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Outfit name") },
            placeholder = { Text("e.g. Monday college look") },
            singleLine = true
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = "Occasion",
            style = MaterialTheme.typography.titleMedium,
            color = PlannerText
        )

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            occasions.take(3).forEach { occasion ->
                FilterChip(
                    selected = selectedOccasion == occasion,
                    onClick = { selectedOccasion = occasion },
                    label = { Text(occasion) }
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            occasions.drop(3).forEach { occasion ->
                FilterChip(
                    selected = selectedOccasion == occasion,
                    onClick = { selectedOccasion = occasion },
                    label = { Text(occasion) }
                )
            }
        }

        Spacer(Modifier.height(12.dp))


        OutlinedTextField(
            value = selectedDate,
            onValueChange = {
                selectedDate = it
                dateError = it.isNotBlank() && !isValidDate(it)
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Planned date (YYYY-MM-DD)") },
            placeholder = { Text("2026-10-20") },
            singleLine = true,
            isError = dateError,
            supportingText = {
                when {
                    dateError -> Text(
                        "Invalid date. Enter a real date like 2026-10-20."
                    )
                    selectedDate.isBlank() -> Text(
                        "Enter a date in YYYY-MM-DD format."
                    )
                }
            }
        )


        Spacer(Modifier.height(20.dp))

        Text(
            text = "Select clothes (${selectedClothingIds.size} selected)",
            style = MaterialTheme.typography.titleMedium,
            color = PlannerText
        )

        Spacer(Modifier.height(10.dp))

        if (clothingItems.isEmpty()) {
            Text("No wardrobe items found. Add clothes to your wardrobe first.")
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(
                    items = clothingItems,
                    key = { it.id }
                ) { item ->
                    val isSelected =
                        item.id in selectedClothingIds

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedClothingIds =
                                    if (isSelected) {
                                        selectedClothingIds - item.id
                                    } else {
                                        selectedClothingIds + item.id
                                    }
                            },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) {
                                Color(0xFFE8D5C2)
                            } else {
                                Color.White
                            }
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 2.dp
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp)
                        ) {
                            Text(
                                text = if (isSelected) {
                                    "✓ Selected"
                                } else {
                                    "＋ Select"
                                },
                                color = PlannerBrown,
                                style = MaterialTheme.typography.labelMedium
                            )

                            Spacer(Modifier.height(6.dp))

                            Text(
                                text = item.name,
                                color = PlannerText,
                                style = MaterialTheme.typography.titleSmall
                            )

                            Text(
                                text = item.category,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onBackClick,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE8E0D8),
                    contentColor = PlannerText
                )
            ) {
                Text("Back")
            }

            Button(
                onClick = {
                    val validDate = isValidDate(selectedDate)

                    dateError = !validDate

                    if (
                        outfitName.trim().isNotEmpty() &&
                        selectedClothingIds.isNotEmpty() &&
                        validDate
                    ) {
                        onSaveClick(
                            outfitName.trim(),
                            selectedOccasion,
                            selectedClothingIds.toList(),
                            selectedDate.trim()
                        )
                    }
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PlannerBrown
                ),
                enabled = outfitName.trim().isNotEmpty() &&
                        selectedClothingIds.isNotEmpty() &&
                       isValidDate(selectedDate)
            ) {
                Text("Continue")
            }
        }
    }
}
