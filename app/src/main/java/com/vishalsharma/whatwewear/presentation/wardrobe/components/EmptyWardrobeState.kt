package com.vishalsharma.whatwewear.presentation.wardrobe.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun EmptyWardrobeState(
    onAddClothingClick: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text(
            text = "Your wardrobe is empty",
            style = MaterialTheme.typography.titleLarge
        )

        Text(
            text = "Start adding clothes to build your wardrobe.",
            style = MaterialTheme.typography.bodyMedium
        )

        TextButton(
            onClick = onAddClothingClick
        ) {
            Text(
                text = "Add Clothing"
            )
        }
    }
}