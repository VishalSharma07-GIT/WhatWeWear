
package com.vishalsharma.whatwewear.presentation.planner

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Checkroom
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.vishalsharma.whatwewear.data.remote.model.OutfitDto
import com.vishalsharma.whatwewear.domain.model.ClothingItem

private val PlannerBackground = Color(0xFFFAF7F2)
private val PlannerBrown = Color(0xFFB8906D)
private val PlannerText = Color(0xFF211B18)
private val PlannerMuted = Color(0xFF89827B)


@Composable
fun OutfitPlannerScreen(
    outfits: List<OutfitDto>,
    clothingItems: List<ClothingItem>,
    onCreateOutfitClick: () -> Unit,
    onDeleteOutfit: (String) -> Unit,
    onMarkAsWorn: (String, Boolean) -> Unit,
    onEditClick: (OutfitDto) -> Unit
) {

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PlannerBackground)
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        Text(
            text = "PLAN AHEAD, DRESS BETTER",
            style = MaterialTheme.typography.labelMedium,
            color = PlannerBrown
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Outfit Planner",
            style = MaterialTheme.typography.headlineLarge,
            color = PlannerText
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Plan your looks, one day at a time.",
            style = MaterialTheme.typography.bodyLarge,
            color = PlannerMuted
        )

        Spacer(Modifier.height(20.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            color = Color(0xFFF0E8DF)
        ) {
            Row(
                modifier = Modifier.padding(18.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.CalendarMonth,
                    contentDescription = null,
                    tint = PlannerBrown
                )

                Column {
                    Text(
                        text = "${outfits.size} planned outfit(s)",
                        style = MaterialTheme.typography.titleMedium,
                        color = PlannerText
                    )
                    Text(
                        text = "Your looks, all in one place.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = PlannerMuted
                    )
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        if (outfits.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Checkroom,
                        contentDescription = null,
                        tint = PlannerBrown
                    )
                    Text(
                        text = "Your next look starts here",
                        style = MaterialTheme.typography.titleLarge,
                        color = PlannerText
                    )
                    Text(
                        text = "Create an outfit from clothes you own.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = PlannerMuted
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(outfits, key = { it.id }) { outfit ->
                    val selectedClothes = outfit.clothingIds.mapNotNull { id ->
                        clothingItems.find { it.id == id }
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = outfit.name,
                                style = MaterialTheme.typography.titleLarge,
                                color = PlannerText
                            )

                            Text(
                                text = "${outfit.occasion} • ${outfit.plannedDate}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = PlannerMuted
                            )

                            Text(
                                text = "${outfit.clothingIds.size} clothing item(s)",
                                style = MaterialTheme.typography.bodySmall,
                                color = PlannerMuted
                            )

                            if (selectedClothes.isNotEmpty()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    selectedClothes.take(4).forEach { clothing ->
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(100.dp)
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(Color(0xFFF0EBE4)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (!clothing.imageUri.isNullOrBlank()) {
                                                AsyncImage(
                                                    model = ImageRequest.Builder(context)
                                                        .data(clothing.imageUri)
                                                        .crossfade(true)
                                                        .build(),
                                                    contentDescription = clothing.name,
                                                    modifier = Modifier
                                                        .fillMaxSize()
                                                        .padding(6.dp),
                                                    contentScale = ContentScale.Fit
                                                )
                                            } else if (clothing.imageRes != null) {
                                                Text(
                                                    text = clothing.name,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = PlannerMuted
                                                )
                                            } else {
                                                Icon(
                                                    imageVector = Icons.Outlined.Checkroom,
                                                    contentDescription = clothing.name,
                                                    tint = PlannerBrown,
                                                    modifier = Modifier.size(28.dp)
                                                )
                                            }
                                        }
                                    }
                                }

                                if (selectedClothes.size > 4) {
                                    Text(
                                        text = "+${selectedClothes.size - 4} more items",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = PlannerMuted
                                    )
                                }
                            }

                            if (outfit.isWorn) {
                                Text(
                                    text = "✓ Already worn",
                                    color = PlannerBrown,
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        onMarkAsWorn(
                                            outfit.id,
                                            !outfit.isWorn
                                        )
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        if (outfit.isWorn) "Undo worn"
                                        else "Mark worn"
                                    )
                                }

                                OutlinedButton(
                                    onClick = {
                                        onDeleteOutfit(outfit.id)
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Delete")
                                }
                                OutlinedButton(
                                    onClick = {
                                        onEditClick(outfit)
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Edit")
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = onCreateOutfitClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PlannerBrown,
                contentColor = Color.White
            )
        ) {
            Icon(
                imageVector = Icons.Outlined.Add,
                contentDescription = null
            )
            Text(
                text = "Create Outfit",
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}
