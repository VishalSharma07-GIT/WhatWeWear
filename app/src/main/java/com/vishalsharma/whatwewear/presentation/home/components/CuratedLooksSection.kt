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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CuratedLookSection(
    currentIndex: Int,
    totalLooks: Int,
    imageRes: Int,
    title: String,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit
){
    Row(
        modifier = Modifier.fillMaxWidth()
            .padding(bottom = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

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
                contentDescription = "Next Outfit"
            )
        }
    }
    CuratedLookCard(
        imageRes = imageRes,
        title = title
    )
    }
