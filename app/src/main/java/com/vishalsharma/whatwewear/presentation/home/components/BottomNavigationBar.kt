package com.vishalsharma.whatwewear.presentation.navigation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Checkroom
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Insights
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vishalsharma.whatwewear.presentation.navigation.NavRoutes

private val Ivory = Color(0xFFFAF7F2)
private val AccentBrown = Color(0xFFB8906D)
private val MutedBrown = Color(0xFF89827B)

private data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

@Composable
fun BottomNavigationBar(
    currentRoute: String?,
    onItemClick: (String) -> Unit
) {
    val items = listOf(
        BottomNavItem(
            route = NavRoutes.Wardrobe,
            label = "Wardrobe",
            icon = Icons.Outlined.Checkroom
        ),
        BottomNavItem(
            route = NavRoutes.Studio,
            label = "Studio",
            icon = Icons.Outlined.AutoAwesome
        ),
        BottomNavItem(
            route = NavRoutes.Inspire,
            label = "Inspire",
            icon = Icons.Outlined.FavoriteBorder
        ),
        BottomNavItem(
            route = NavRoutes.Planner,
            label = "Planner",
            icon = Icons.Outlined.CalendarMonth
        ),
        BottomNavItem(
            route = NavRoutes.Insights,
            label = "Insights",
            icon = Icons.Outlined.Insights
        )
    )

    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .height(76.dp),
        containerColor = Ivory,
        contentColor = MutedBrown,
        tonalElevation = 0.dp
    ) {
        items.forEach { item ->
            val selected = currentRoute == item.route

            NavigationBarItem(
                selected = selected,
                onClick = {
                    onItemClick(item.route)
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        fontSize = 10.sp,
                        maxLines = 1
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = AccentBrown,
                    selectedTextColor = AccentBrown,
                    unselectedIconColor = MutedBrown,
                    unselectedTextColor = MutedBrown,
                    indicatorColor = Color(0xFFEDE3D8)
                )
            )
        }
    }
}