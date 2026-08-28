package com.vishalsharma.whatwewear.presentation.navigation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Checkroom
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.vishalsharma.whatwewear.presentation.navigation.NavRoutes

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
            route = NavRoutes.Home,
            label = "Home",
            icon = Icons.Outlined.Home
        ),
        BottomNavItem(
            route = NavRoutes.Wardrobe,
            label = "Wardrobe",
            icon = Icons.Outlined.Checkroom
        ),
        BottomNavItem(
            route = NavRoutes.Profile,
            label = "Profile",
            icon = Icons.Outlined.Person
        )
    )

    NavigationBar {

        items.forEach { item ->

            NavigationBarItem(
                selected = currentRoute == item.route,
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
                    Text(text = item.label)
                }
            )
        }
    }
}