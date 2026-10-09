package com.vishalsharma.whatwewear.presentation.wardrobe.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun WardrobeFilter(
    label: String,
    options: List<String>,
    selectedValue: String,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    androidx.compose.foundation.layout.Box(
        modifier = modifier
    ) {
        OutlinedButton(
            onClick = {
                onExpandedChange(true)
            },
            shape = RoundedCornerShape(50.dp)
        ) {
            Text(
                text = if (
                    selectedValue.startsWith("All ")
                ) {
                    "$label  ⌄"
                } else {
                    "$selectedValue  ⌄"
                },
                color = Color(0xFF3D3631),
                maxLines = 1
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                onExpandedChange(false)
            }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(option)
                    },
                    onClick = {
                        onOptionSelected(option)
                        onExpandedChange(false)
                    }
                )
            }
        }
    }
}