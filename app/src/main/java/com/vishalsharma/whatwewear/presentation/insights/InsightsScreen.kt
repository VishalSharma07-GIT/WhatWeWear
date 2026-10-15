
package com.vishalsharma.whatwewear.presentation.insights

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import com.vishalsharma.whatwewear.presentation.home.HomeViewModel

@Composable
fun InsightsScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = "Your Style Insights",
            style = MaterialTheme.typography.headlineLarge,
            color = Color(0xFF211B18)
        )

        Text(
            text = "A clearer picture of what you own.",
            style = MaterialTheme.typography.bodyLarge,
            color = Color(0xFF89827B)
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFFF0E8DF)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "TOTAL WARDROBE",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFF89827B)
                )

                Text(
                    text = "${uiState.totalClothes}",
                    style = MaterialTheme.typography.displayMedium,
                    color = Color(0xFF211B18)
                )

                Text(
                    text = "Clothing items in your collection",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF625950)
                )
            }
        }

        Text(
            text = "Category Breakdown",
            style = MaterialTheme.typography.titleLarge,
            color = Color(0xFF211B18)
        )

        CategoryRow("Tops", uiState.totalTops, uiState.totalClothes)
        CategoryRow("Bottoms", uiState.totalBottoms, uiState.totalClothes)
        CategoryRow("Shoes", uiState.totalShoes, uiState.totalClothes)


        Text(
            text = "Color Distribution",
            style = MaterialTheme.typography.titleLarge,
            color = Color(0xFF211B18)
        )

        if (uiState.colorCounts.isEmpty()) {
            Text(
                text = "Add clothing items with color details to see your wardrobe palette.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF89827B)
            )
        } else {
            val totalColoredItems = uiState.colorCounts.values.sum()

            uiState.colorCounts.entries
                .sortedByDescending { it.value }
                .forEach { (color, count) ->
                    ColorDistributionRow(
                        colorName = color,
                        count = count,
                        total = totalColoredItems
                    )
                }
        }


        Text(
            text = "Next, we'll add color distribution and insights based on the clothes you actually own.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF89827B)
        )

        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
private fun CategoryRow(
    label: String,
    count: Int,
    total: Int
) {
    val fraction = if (total > 0) {
        (count.toFloat() / total).coerceIn(0f, 1f)
    } else {
        0f
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = Color(0xFFF7F2EC)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(label, style = MaterialTheme.typography.titleMedium)
                Text("$count", style = MaterialTheme.typography.titleMedium)
            }

            androidx.compose.material3.LinearProgressIndicator(
                progress = { fraction },
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFFB8906D),
                trackColor = Color(0xFFE7DDD3)
            )

            Text(
                text = "${(fraction * 100).toInt()}% of your wardrobe",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF89827B)
            )
        }
    }
}
