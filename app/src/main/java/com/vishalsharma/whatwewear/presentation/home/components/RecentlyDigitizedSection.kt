
package com.vishalsharma.whatwewear.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.vishalsharma.whatwewear.domain.model.ClothingItem

@Composable
fun RecentlyDigitizedSection(
    clothes: List<ClothingItem>,
    onViewAllClick: () -> Unit,
    onItemClick: (ClothingItem) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Recently Digitized",
                style = MaterialTheme.typography.headlineSmall
            )

            Text(
                text = "VIEW ALL",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier
                    .clickable(onClick = onViewAllClick)
                    .padding(4.dp)
            )
        }

        if (clothes.isEmpty()) {
            Text(
                text = "Your wardrobe is waiting. Add your first clothing item!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(
                    items = clothes,
                    key = { it.id }
                ) { clothing ->
                    Column(
                        modifier = Modifier
                            .fillParentMaxWidth(0.46f)
                            .clickable { onItemClick(clothing) }
                    ) {
                        AsyncImage(
                            model = clothing.imageUri,
                            contentDescription = clothing.name,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(190.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(
                                    MaterialTheme.colorScheme.surfaceVariant
                                )
                                .padding(8.dp)
                        )

                        Text(
                            text = clothing.brand.ifBlank { "UNBRANDED" }
                                .uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(top = 12.dp),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Text(
                            text = clothing.name,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(top = 4.dp),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}
