package com.vishalsharma.whatwewear.presentation.clothingdetails

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.vishalsharma.whatwewear.domain.model.ClothingItem
import com.vishalsharma.whatwewear.ui.theme.AppColors
import com.vishalsharma.whatwewear.utils.rememberImageModel

@Composable
fun ClothingDetailsScreen(
    clothingItem: ClothingItem,
    onDeleteClick: () -> Unit = {},
    onEditClick: () -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    val details = listOf(
        "Category" to clothingItem.category,
        "Type" to clothingItem.type,
        "Color" to clothingItem.color,
        "Size" to clothingItem.size,
        "Season" to clothingItem.season,
        "Occasion" to clothingItem.occasion
    ).filter { it.second.isNotBlank() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.padding(start = 8.dp, top = 4.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = AppColors.Ink
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(340.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(AppColors.Surface),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = rememberImageModel(clothingItem.imageUri),
                    contentDescription = clothingItem.name,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(18.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = clothingItem.name,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp
                ),
                color = AppColors.Ink
            )
            if (clothingItem.brand.isNotBlank()) {
                Text(
                    text = clothingItem.brand,
                    fontSize = 18.sp,
                    color = AppColors.Muted
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            details.forEach { (label, value) ->
                HorizontalDivider(color = AppColors.Border)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = label, color = AppColors.Muted, fontSize = 16.sp)
                    Text(
                        text = value,
                        color = AppColors.Ink,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            HorizontalDivider(color = AppColors.Border)

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Edit and delete, pinned at the bottom like the mock
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                onClick = onEditClick,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.5.dp, AppColors.Ink),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = AppColors.Ink)
            ) {
                Text("Edit", fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
            }
            TextButton(
                onClick = { showDeleteDialog = true },
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text(
                    "Delete",
                    color = AppColors.Danger,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete this piece?") },
            text = { Text("\"${clothingItem.name}\" will be removed from your wardrobe.") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    onDeleteClick()
                }) { Text("Delete", color = AppColors.Danger) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel", color = AppColors.Ink) }
            }
        )
    }
}