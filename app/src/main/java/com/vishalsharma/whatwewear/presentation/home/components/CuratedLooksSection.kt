
package com.vishalsharma.whatwewear.presentation.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronLeft
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.vishalsharma.whatwewear.domain.model.ClothingItem

@Composable
fun CuratedLookSection(
    currentIndex: Int,
    totalLooks: Int,
    title: String,
    clothingItems: List<ClothingItem>,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
    onCardClick: () -> Unit,
    onGetTheLookClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "LOOK ${currentIndex + 1} OF $totalLooks",
            style = MaterialTheme.typography.labelMedium,
            color = Color(0xFF77736F)
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = onPreviousClick,
                enabled = currentIndex > 0
            ) {
                Icon(
                    imageVector = Icons.Outlined.ChevronLeft,
                    contentDescription = "Previous outfit"
                )
            }

            IconButton(
                onClick = onNextClick,
                enabled = currentIndex < totalLooks - 1
            ) {
                Icon(
                    imageVector = Icons.Outlined.ChevronRight,
                    contentDescription = "Next outfit"
                )
            }
        }
    }

    CuratedLookCard(
        title = title,
        clothingItems = clothingItems,
        onCardClick = onCardClick,
        onGetTheLookClick = onGetTheLookClick
    )
}
