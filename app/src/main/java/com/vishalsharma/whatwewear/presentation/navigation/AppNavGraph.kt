package com.vishalsharma.whatwewear.presentation.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.vishalsharma.whatwewear.presentation.add.AddClothingScreen
import com.vishalsharma.whatwewear.presentation.add.CameraCaptureSheet
import com.vishalsharma.whatwewear.presentation.auth.LoginScreen
import com.vishalsharma.whatwewear.presentation.clothingdetails.ClothingDetailsScreen
import com.vishalsharma.whatwewear.presentation.editclothing.EditClothingScreen
import com.vishalsharma.whatwewear.presentation.forgotpassword.ForgotPasswordScreen
import com.vishalsharma.whatwewear.presentation.home.HomeScreen
import com.vishalsharma.whatwewear.presentation.onboarding.OnboardingScreen
import com.vishalsharma.whatwewear.presentation.profile.ProfileScreen
import com.vishalsharma.whatwewear.presentation.signup.SignupScreen
import com.vishalsharma.whatwewear.presentation.splash.SplashScreen
import com.vishalsharma.whatwewear.presentation.wardrobe.WardrobeScreen
import com.vishalsharma.whatwewear.presentation.wardrobe.WardrobeViewModel

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    val wardrobeViewModel: WardrobeViewModel = hiltViewModel()
    var showCamera by remember { mutableStateOf(false) }

    NavHost(
        navController = navController,
        startDestination = NavRoutes.Splash
    ) {

        composable(NavRoutes.Splash) {
            SplashScreen(
                onNavigationDecided = { hasCompletedOnboarding, isLoggedIn ->
                    when {
                        !hasCompletedOnboarding -> {
                            navController.navigate(NavRoutes.Onboarding) {
                                popUpTo(NavRoutes.Splash) { inclusive = true }
                            }
                        }
                        isLoggedIn -> {
                            navController.navigate(NavRoutes.Home) {
                                popUpTo(NavRoutes.Splash) { inclusive = true }
                            }
                        }
                        else -> {
                            navController.navigate(NavRoutes.Login) {
                                popUpTo(NavRoutes.Splash) { inclusive = true }
                            }
                        }
                    }
                }
            )
        }

        composable(NavRoutes.Onboarding) {
            OnboardingScreen(navController = navController)
        }

        composable(NavRoutes.Login) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(NavRoutes.Home) {
                        popUpTo(NavRoutes.Login) { inclusive = true }
                    }
                },
                onSignupClick = { navController.navigate(NavRoutes.Signup) },
                onForgotPasswordClick = { navController.navigate(NavRoutes.ForgotPassword) }
            )
        }

        composable(NavRoutes.Signup) {
            SignupScreen(
                onLoginClick = { navController.popBackStack() },
                onSignupSuccess = {
                    navController.navigate(NavRoutes.Home) {
                        popUpTo(NavRoutes.Login) { inclusive = true }
                    }
                }
            )
        }

        composable(NavRoutes.ForgotPassword) {
            ForgotPasswordScreen(onEmailSent = { navController.popBackStack() })
        }

        composable(NavRoutes.Home) {
            MainAppScaffold(
                navController = navController,
                onAddClick = { showCamera = true }
            ) {
                HomeScreen(
                    onAddClothingClick = { showCamera = true },
                    onWardrobeClick = { navController.navigate(NavRoutes.Wardrobe) },
                    onClothingClick = { id ->
                        navController.navigate("${NavRoutes.ClothingDetails}/$id")
                    },
                    onProfileClick = { navController.navigate(NavRoutes.Profile) }
                )
            }
        }

        composable(NavRoutes.Wardrobe) {
            val clothingItems by wardrobeViewModel.clothingItems.collectAsStateWithLifecycle()
            val isLoading by wardrobeViewModel.isLoading.collectAsStateWithLifecycle()
            val loadError by wardrobeViewModel.loadError.collectAsStateWithLifecycle()

            MainAppScaffold(
                navController = navController,
                onAddClick = { showCamera = true }
            ) {
                WardrobeScreen(
                    clothingItems = clothingItems,
                    isLoading = isLoading,
                    loadError = loadError,
                    onRetry = { wardrobeViewModel.retry() },
                    onAddClothingClick = { showCamera = true },
                    onClothingClick = { clothingId ->
                        navController.navigate("${NavRoutes.ClothingDetails}/$clothingId")
                    }
                )
            }
        }

        composable(route = "${NavRoutes.ClothingDetails}/{clothingId}") { backStackEntry ->
            val clothingId = backStackEntry.arguments?.getString("clothingId")
            val clothingItem = clothingId?.let { wardrobeViewModel.getClothingById(it) }

            if (clothingItem != null) {
                ClothingDetailsScreen(
                    clothingItem = clothingItem,
                    onEditClick = {
                        navController.navigate("${NavRoutes.EditClothing}/${clothingItem.id}")
                    },

                    onDeleteClick = {
                        wardrobeViewModel.deleteClothing(clothingItem.id)
                        navController.popBackStack()
                    },
                    onBackClick = { navController.popBackStack() },

                )
            }
        }

        composable(route = "${NavRoutes.EditClothing}/{clothingId}") { backStackEntry ->
            val clothingId = backStackEntry.arguments?.getString("clothingId")
            val clothingItem = clothingId?.let { wardrobeViewModel.getClothingById(it) }

            if (clothingItem != null) {
                EditClothingScreen(
                    clothingItem = clothingItem,
                    onClothingUpdated = { updatedItem ->
                        wardrobeViewModel.updateClothing(updatedItem)
                        navController.popBackStack()
                    },
                    onBackClick = { navController.popBackStack() }
                )
            }
        }

        // The photo from the camera sheet is passed in as an optional argument
        composable(
            route = "${NavRoutes.AddClothing}?photo={photo}",
            arguments = listOf(
                navArgument("photo") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            AddClothingScreen(
                initialPhoto = backStackEntry.arguments?.getString("photo"),
                onClothingSaved = { clothingItem, onResult ->
                    wardrobeViewModel.addClothing(clothingItem) { success ->
                        onResult(success)
                        if (success) {
                            navController.popBackStack()
                        }
                    }
                }
            )
        }

        composable(NavRoutes.Profile) {
            MainAppScaffold(
                navController = navController,
                onAddClick = { showCamera = true }
            ) {
                ProfileScreen(
                    onLoggedOut = {
                        navController.navigate(NavRoutes.Login) {
                            popUpTo(navController.graph.id) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    }

    if (showCamera) {
        CameraCaptureSheet(
            onDismiss = { showCamera = false },
            onPhotoReady = { uri ->
                showCamera = false
                navController.navigate(
                    "${NavRoutes.AddClothing}?photo=${Uri.encode(uri.toString())}"
                )
            }
        )
    }

}