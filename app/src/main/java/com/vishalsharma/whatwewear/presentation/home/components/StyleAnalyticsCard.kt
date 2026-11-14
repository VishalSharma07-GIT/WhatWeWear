package com.vishalsharma.whatwewear.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vishalsharma.whatwewear.domain.model.ClothingItem

private val AnalyticsBackground = Color(0xFFF7EEE8)
private val AnalyticsText = Color(0xFF211B18)
private val AnalyticsMuted = Color(0xFF817A73)

// One soft tone per category, from dark to light
private val categoryTones = listOf(
    "Tops" to Color(0xFF59423C),
    "Bottoms" to Color(0xFFB8906D),
    "Shoes" to Color(0xFF8C6E5D),
    "Dresses" to Color(0xFFD9C3AE),
    "Outerwear" to Color(0xFFC9A98C),
    "Accessories" to Color(0xFFE5D6C8)
)

@Composable
fun StyleAnalyticsCard(
    totalClothes: Int,
    totalTops: Int,
    totalBottoms: Int,
    totalShoes: Int,
    clothingItems: List<ClothingItem>
) {
    val counts = categoryTones.map { (name, tone) ->
        Triple(
            name,
            tone,
            clothingItems.count { it.category.equals(name, ignoreCase = true) }
        )
    }.filter { it.third > 0 }

    val topCategory = counts.maxByOrNull { it.third }?.first

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        color = AnalyticsBackground
    ) {
        Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 28.dp)) {
            Text(
                text = "STYLE ANALYTICS",
                style = MaterialTheme.typography.labelMedium.copy(
                    letterSpacing = 2.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                color = AnalyticsMuted
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = totalClothes.toString(),
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Medium,
                        fontSize = 64.sp,
                        lineHeight = 68.sp
                    ),
                    color = AnalyticsText
                )
                Text(
                    text = if (totalClothes == 1) "piece" else "pieces",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = FontFamily.Serif
                    ),
                    color = AnalyticsMuted,
                    modifier = Modifier.padding(start = 10.dp, bottom = 12.dp)
                )
            }

            Text(
                text = "in your wardrobe",
                style = MaterialTheme.typography.bodyMedium,
                color = AnalyticsMuted
            )

            if (counts.isNotEmpty()) {
                Spacer(modifier = Modifier.height(24.dp))

                // One thin bar: each color is a category, sized by how much of it you own
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    counts.forEach { (_, tone, count) ->
                        Box(
                            modifier = Modifier
                                .weight(count.toFloat())
                                .height(8.dp)
                                .clip(RoundedCornerShape(50))
                                .background(tone)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Your wardrobe leans towards ${topCategory?.lowercase()}.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = FontFamily.Serif
                    ),
                    color = AnalyticsText
                )
            } else {
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Add your first piece to see your style at a glance.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AnalyticsMuted
                )
            }
        }
    }
}