package com.vishalsharma.whatwewear.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.vishalsharma.whatwewear.domain.model.ClothingItem
import com.vishalsharma.whatwewear.utils.rememberImageModel

private val LookBackground = Color(0xFFFFFAF7)
private val LookText = Color(0xFF211B18)
private val LookMuted = Color(0xFF77736F)

@Composable
fun CuratedLookCard(
    title: String,
    clothingItems: List<ClothingItem>,
    onCardClick: () -> Unit = {},
    onGetTheLookClick: () -> Unit = {} // kept so other files still compile; not used in V0
) {
    Card(
        onClick = onCardClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = LookBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "CURATED FOR YOU",
                style = MaterialTheme.typography.labelMedium.copy(
                    letterSpacing = 1.5.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                color = LookMuted
            )

            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Medium
                ),
                color = LookText
            )

            if (clothingItems.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color(0xFFF0E8E2)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Add clothes to your wardrobe\nfor personalized looks.",
                        color = LookMuted,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(items = clothingItems, key = { it.id }) { clothing ->
                        Column(
                            modifier = Modifier.padding(bottom = 4.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            AsyncImage(
                                model = rememberImageModel(clothing.imageUri) ?: clothing.imageRes,
                                contentDescription = clothing.name,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .width(140.dp)
                                    .height(180.dp)
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(Color(0xFFF0E8E2))
                                    .padding(6.dp)
                            )

                            Text(
                                text = clothing.name,
                                color = LookText,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1,
                                modifier = Modifier.width(140.dp)
                            )

                            Text(
                                text = clothing.category,
                                color = LookMuted,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
            }

            Text(
                text = "Pieces from your personal wardrobe.",
                style = MaterialTheme.typography.bodyMedium,
                color = LookMuted
            )
        }
    }
}