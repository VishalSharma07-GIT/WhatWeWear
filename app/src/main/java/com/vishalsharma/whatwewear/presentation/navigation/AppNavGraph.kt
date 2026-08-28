package com.vishalsharma.whatwewear.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.vishalsharma.whatwewear.presentation.auth.LoginScreen
import com.vishalsharma.whatwewear.presentation.forgotpassword.ForgotPasswordScreen
import com.vishalsharma.whatwewear.presentation.home.HomeScreen
import com.vishalsharma.whatwewear.presentation.onboarding.OnboardingScreen
import com.vishalsharma.whatwewear.presentation.profile.ProfileScreen
import com.vishalsharma.whatwewear.presentation.signup.SignupScreen
import com.vishalsharma.whatwewear.presentation.splash.SplashScreen
import com.vishalsharma.whatwewear.presentation.wardrobe.WardrobeScreen

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

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

            MainAppScaffold(
                navController = navController
            ) {
                WardrobeScreen()
            }

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
