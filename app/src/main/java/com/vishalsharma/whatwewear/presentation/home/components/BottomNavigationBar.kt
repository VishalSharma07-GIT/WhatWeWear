package com.vishalsharma.whatwewear.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Checkroom
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vishalsharma.whatwewear.presentation.navigation.NavRoutes

private val Ivory = Color(0xFFFAF7F2)
private val AccentBrown = Color(0xFFB8906D)
private val MutedBrown = Color(0xFF89827B)

@Composable
fun BottomNavigationBar(
    currentRoute: String?,
    onItemClick: (String) -> Unit,
    onAddClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Ivory)
            .navigationBarsPadding()
            .height(72.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        NavItem(
            label = "Home",
            icon = Icons.Outlined.Home,
            selected = currentRoute == NavRoutes.Home,
            onClick = { onItemClick(NavRoutes.Home) },
            modifier = Modifier.weight(1f)
        )

        NavItem(
            label = "Closet",
            icon = Icons.Outlined.Checkroom,
            selected = currentRoute == NavRoutes.Wardrobe,
            onClick = { onItemClick(NavRoutes.Wardrobe) },
            modifier = Modifier.weight(1f)
        )

        // Add button, in the third slot
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .shadow(6.dp, CircleShape)
                    .clip(CircleShape)
                    .background(AccentBrown)
                    .clickable(onClick = onAddClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = "Add clothing",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

@Composable
private fun NavItem(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val color = if (selected) AccentBrown else MutedBrown
    Column(
        modifier = modifier
            .fillMaxHeight()
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(imageVector = icon, contentDescription = label, tint = color)
        Text(text = label, fontSize = 11.sp, color = color, maxLines = 1)
    }
}