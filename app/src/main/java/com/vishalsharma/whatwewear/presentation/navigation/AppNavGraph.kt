package com.vishalsharma.whatwewear.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.vishalsharma.whatwewear.presentation.add.AddClothingScreen
import com.vishalsharma.whatwewear.presentation.auth.LoginScreen
import com.vishalsharma.whatwewear.presentation.forgotpassword.ForgotPasswordScreen
import com.vishalsharma.whatwewear.presentation.home.HomeScreen
import com.vishalsharma.whatwewear.presentation.onboarding.OnboardingScreen
import com.vishalsharma.whatwewear.presentation.profile.ProfileScreen
import com.vishalsharma.whatwewear.presentation.signup.SignupScreen
import com.vishalsharma.whatwewear.presentation.splash.SplashScreen
import com.vishalsharma.whatwewear.presentation.wardrobe.WardrobeScreen
import androidx.hilt.navigation.compose.hiltViewModel
import com.vishalsharma.whatwewear.presentation.wardrobe.WardrobeViewModel


@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    val wardrobeViewModel: WardrobeViewModel = hiltViewModel()
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
                                popUpTo(NavRoutes.Splash) {
                                    inclusive = true
                                }
                            }

                        }

                        isLoggedIn -> {

                            navController.navigate(NavRoutes.Home) {
                                popUpTo(NavRoutes.Splash) {
                                    inclusive = true
                                }
                            }

                        }

                        else -> {

                            navController.navigate(NavRoutes.Login) {
                                popUpTo(NavRoutes.Splash) {
                                    inclusive = true
                                }
                            }

                        }

                    }

                }
            )

        }

        composable(NavRoutes.Onboarding) {
            OnboardingScreen(
                navController = navController
            )
        }

        composable(NavRoutes.Login) {

            LoginScreen(

                onLoginSuccess = {
                    navController.navigate(NavRoutes.Home) {
                        popUpTo(NavRoutes.Login) {
                            inclusive = true
                        }
                    }
                },

                onSignupClick = {
                    navController.navigate(NavRoutes.Signup)
                },

                onForgotPasswordClick = {
                    navController.navigate(NavRoutes.ForgotPassword)
                }

            )

        }
        composable(NavRoutes.Home) {

            MainAppScaffold(
                navController = navController
            ) {
                HomeScreen()
            }

        }

        composable(NavRoutes.Wardrobe) {

            val clothingItems by wardrobeViewModel.clothingItems
                .collectAsStateWithLifecycle()

            MainAppScaffold(navController = navController) {

                WardrobeScreen(
                    clothingItems = clothingItems,
                    onAddClothingClick = {
                        navController.navigate(NavRoutes.AddClothing)
                    }
                )
            }
        }
        composable(NavRoutes.AddClothing) {

            AddClothingScreen(
                onClothingSaved = { clothingItem ->

                    wardrobeViewModel.addClothing(clothingItem)

                    navController.popBackStack()
                }
            )
        }
        composable(NavRoutes.Profile) {

            MainAppScaffold(
                navController = navController
            ) {
                ProfileScreen()
            }

        }
        composable(NavRoutes.Signup) {

            SignupScreen(

                onLoginClick = {
                    navController.popBackStack()
                },

                onSignupSuccess = {

                    navController.navigate(NavRoutes.Home) {

                        popUpTo(NavRoutes.Login) {
                            inclusive = true
                        }

                    }

                }

            )

        }
        composable(NavRoutes.ForgotPassword) {

            ForgotPasswordScreen(

                onEmailSent = {

                    navController.popBackStack()

                }

            )

        }


    }
}
