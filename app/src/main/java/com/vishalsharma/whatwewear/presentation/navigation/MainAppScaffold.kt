package com.vishalsharma.whatwewear.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.vishalsharma.whatwewear.presentation.home.components.BottomNavigationBar

@Composable
fun MainAppScaffold(
    navController: NavHostController,
    onAddClick: () -> Unit,
    content: @Composable () -> Unit
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                currentRoute = currentRoute,
                onItemClick = { route ->
                    if (currentRoute != route) {
                        if (route == NavRoutes.Home) {
                            // Home is always at the bottom of the history, so just go back to it
                            val reachedHome = navController.popBackStack(
                                route = NavRoutes.Home,
                                inclusive = false
                            )
                            if (!reachedHome) {
                                navController.navigate(NavRoutes.Home) {
                                    launchSingleTop = true
                                }
                            }
                        } else {
                            navController.navigate(route) {
                                launchSingleTop = true
                                popUpTo(NavRoutes.Home) { inclusive = false }
                            }
                        }
                    }
                },
                onAddClick = onAddClick
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            content()
        }
    }
}