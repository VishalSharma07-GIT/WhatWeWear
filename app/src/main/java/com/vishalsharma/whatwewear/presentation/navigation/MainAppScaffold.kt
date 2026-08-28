package com.vishalsharma.whatwewear.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.vishalsharma.whatwewear.presentation.navigation.components.BottomNavigationBar

@Composable
fun MainAppScaffold(
    navController: NavHostController,
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

                        navController.navigate(route) {

                            launchSingleTop = true

                            restoreState = true

                            popUpTo(NavRoutes.Home) {
                                saveState = true
                            }
                        }
                    }
                }
            )
        }
    ) { innerPadding ->

        androidx.compose.foundation.layout.Box(
            modifier = androidx.compose.ui.Modifier
                .padding(innerPadding)
        ) {
            content()
        }
    }
}