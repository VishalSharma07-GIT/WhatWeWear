package com.vishalsharma.whatwewear.presentation.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.vishalsharma.whatwewear.domain.model.ClothingItem

private val AnalyticsBackground = Color(0xFFF7EEE8)
private val AnalyticsText = Color(0xFF211B18)
private val AnalyticsMuted = Color(0xFF817A73)
private val AnalyticsAccent = Color(0xFFB8906D)

@Composable
fun StyleAnalyticsCard(
    totalClothes: Int,
    totalTops: Int,
    totalBottoms: Int,
    totalShoes: Int,
    clothingItems: List<ClothingItem>,
    onViewInsightsClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        color = AnalyticsBackground
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = "Style Analytics",
                style = MaterialTheme.typography.headlineSmall,
                color = AnalyticsText
            )

            Text(
                text = "Your wardrobe at a glance",
                style = MaterialTheme.typography.bodyLarge,
                color = AnalyticsMuted
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = totalClothes.toString(),
                        style = MaterialTheme.typography.displayMedium,
                        color = AnalyticsText
                    )

                    Text(
                        text = "Pieces in your wardrobe",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AnalyticsMuted
                    )
                }

                Surface(
                    shape = RoundedCornerShape(50),
                    color = Color(0xFFE8EEDC)
                ) {
                    Text(
                        text = "$totalClothes PIECES",
                        modifier = Modifier.padding(
                            horizontal = 12.dp,
                            vertical = 8.dp
                        ),
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFF68705D)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                CategoryCount("Tops", totalTops)
                CategoryCount("Bottoms", totalBottoms)
                CategoryCount("Shoes", totalShoes)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "YOUR WARDROBE EDIT",
                style = MaterialTheme.typography.labelMedium,
                color = AnalyticsMuted
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                clothingItems
                    .distinctBy { it.id }
                    .take(3)
                    .forEach { item ->
                        ClothingThumbnail(item = item)
                    }
            }

            TextButton(
                onClick = onViewInsightsClick
            ) {
                Text(
                    text = "EXPLORE INSIGHTS  →",
                    color = AnalyticsAccent
                )
            }
        }
    }
}

@Composable
private fun ClothingThumbnail(item: ClothingItem) {
    Box(
        modifier = Modifier
            .size(76.dp)
            .clip(CircleShape)
            .background(Color(0xFFE9DED4))
            .border(1.dp, Color(0xFFD9C9BC), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        when {
            !item.imageUri.isNullOrBlank() -> {
                AsyncImage(
                    model = item.imageUri,
                    contentDescription = item.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(76.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }

            item.imageRes != null -> {
                Image(
                    painter = androidx.compose.ui.res.painterResource(
                        id = item.imageRes
                    ),
                    contentDescription = item.name,
                    modifier = Modifier.size(76.dp),
                    contentScale = ContentScale.Crop
                )
            }

            else -> {
                Text(
                    text = item.name.take(1).uppercase(),
                    style = MaterialTheme.typography.titleLarge,
                    color = AnalyticsText
                )
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
            color = AnalyticsText
        )

        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = AnalyticsMuted
        )
    }
}