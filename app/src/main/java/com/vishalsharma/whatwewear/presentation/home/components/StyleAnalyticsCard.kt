
package com.vishalsharma.whatwewear.presentation.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun StyleAnalyticsCard(
    totalClothes: Int,
    totalTops: Int,
    totalBottoms: Int,
    totalShoes: Int,
    onViewInsightsClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = Color(0xFFF0E8DF)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "YOUR STYLE, IN NUMBERS",
                style = MaterialTheme.typography.labelMedium,
                color = Color(0xFF89827B)
            )

            Text(
                text = totalClothes.toString(),
                style = MaterialTheme.typography.displaySmall,
                color = Color(0xFF211B18)
            )

            Text(
                text = "Pieces in your wardrobe",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF625950)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                CategoryCount("Tops", totalTops)
                CategoryCount("Bottoms", totalBottoms)
                CategoryCount("Shoes", totalShoes)
            }

            TextButton(
                onClick = onViewInsightsClick
            ) {
                Text("EXPLORE INSIGHTS →")
            }
        }
    }
}

@Composable
private fun CategoryCount(
    label: String,
    count: Int
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.titleLarge,
            color = Color(0xFF211B18)
        )

        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF625950)
        )
    }
}
