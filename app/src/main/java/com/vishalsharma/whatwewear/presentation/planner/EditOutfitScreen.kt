
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
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.vishalsharma.whatwewear.data.remote.model.OutfitDto
import com.vishalsharma.whatwewear.domain.model.ClothingItem
import java.time.LocalDate
import java.time.format.DateTimeParseException

private val EditBackground = Color(0xFFFAF7F2)
private val EditBrown = Color(0xFFB8906D)
private val EditText = Color(0xFF211B18)

@Composable
fun EditOutfitScreen(
    outfit: OutfitDto,
    clothingItems: List<ClothingItem>,
    onBackClick: () -> Unit,
    onUpdateClick: (OutfitDto) -> Unit
) {
    var name by remember(outfit.id) {
        mutableStateOf(outfit.name)
    }
    var occasion by remember(outfit.id) {
        mutableStateOf(outfit.occasion)
    }
    var date by remember(outfit.id) {
        mutableStateOf(outfit.plannedDate)
    }
    var selectedIds by remember(outfit.id) {
        mutableStateOf(outfit.clothingIds.toSet())
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
            .background(EditBackground)
            .padding(20.dp)
    ) {
        Text(
            text = "Edit Outfit",
            style = MaterialTheme.typography.headlineMedium,
            color = EditText
        )

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Outfit name") },
            singleLine = true
        )

        Spacer(Modifier.height(12.dp))

        Text("Occasion", style = MaterialTheme.typography.titleMedium)

        occasions.chunked(3).forEach { group ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                group.forEach { option ->
                    FilterChip(
                        selected = occasion == option,
                        onClick = { occasion = option },
                        label = { Text(option) }
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))


        OutlinedTextField(
            value = date,
            onValueChange = {
                date = it
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
                    date.isBlank() -> Text(
                        "Enter a date in YYYY-MM-DD format."
                    )
                }
            }
        )


        Spacer(Modifier.height(16.dp))

        Text(
            text = "Select clothes (${selectedIds.size} selected)",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(Modifier.height(8.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(clothingItems, key = { it.id }) { item ->
                val selected = item.id in selectedIds

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            selectedIds = if (selected) {
                                selectedIds - item.id
                            } else {
                                selectedIds + item.id
                            }
                        },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (selected) {
                            Color(0xFFE8D5C2)
                        } else {
                            Color.White
                        }
                    )
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text(
                            text = if (selected) "✓ Selected" else "+ Select",
                            color = EditBrown
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(item.name, color = EditText)
                        Text(
                            item.category,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(
                onClick = onBackClick,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE8E0D8),
                    contentColor = EditText
                )
            ) {
                Text("Cancel")
            }


            Button(
                onClick = {
                    val validDate = isValidDate(date)
                    dateError = !validDate

                    if (
                        name.isNotBlank() &&
                        selectedIds.isNotEmpty() &&
                        validDate
                    ) {
                        onUpdateClick(
                            outfit.copy(
                                name = name.trim(),
                                occasion = occasion,
                                plannedDate = date.trim(),
                                clothingIds = selectedIds.toList()
                            )
                        )
                    }
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = EditBrown
                ),
                enabled = name.isNotBlank() &&
                        selectedIds.isNotEmpty() &&
                        isValidDate(date)
            ) {
                Text("Save Changes")
            }

        }
    }
}
