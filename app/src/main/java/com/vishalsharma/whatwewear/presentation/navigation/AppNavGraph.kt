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
import com.vishalsharma.whatwewear.presentation.clothingdetails.ClothingDetailsScreen
import com.vishalsharma.whatwewear.presentation.editclothing.EditClothingScreen
import com.vishalsharma.whatwewear.presentation.wardrobe.WardrobeViewModel
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.vishalsharma.whatwewear.presentation.insights.InsightsScreen
import com.vishalsharma.whatwewear.presentation.planner.CreateOutfitScreen
import com.vishalsharma.whatwewear.presentation.planner.EditOutfitScreen
import com.vishalsharma.whatwewear.presentation.planner.OutfitPlannerScreen
import com.vishalsharma.whatwewear.presentation.planner.OutfitPlannerViewModel


@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    val wardrobeViewModel: WardrobeViewModel = hiltViewModel()
    val outfitPlannerViewModel: OutfitPlannerViewModel = hiltViewModel()
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
                HomeScreen(
                    onAddClothingClick = {
                        navController.navigate(NavRoutes.AddClothing)
                    },
                    onPlannerClick = {
                        navController.navigate(NavRoutes.Planner)
                    },
                    onStudioClick = {
                        navController.navigate(NavRoutes.Studio)
                    },
                    onWardrobeClick = {
                        navController.navigate(NavRoutes.Wardrobe)
                    },
                    onClothingClick = { clothingId ->
                        navController.navigate(
                            "${NavRoutes.ClothingDetails}/$clothingId"
                        )
                    }
                    ,
                    onInsightsClick = {
                        navController.navigate(NavRoutes.Insights)
                    }
                )
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
                    },
                    onClothingClick = { clothingId ->
                        navController.navigate(
                            "${NavRoutes.ClothingDetails}/$clothingId"
                        )
                    }
                )
            }
        }

        composable(
            route = "${NavRoutes.ClothingDetails}/{clothingId}"
        ) { backStackEntry ->

            val clothingId =
                backStackEntry.arguments?.getString("clothingId")

            val clothingItem =
                clothingId?.let {
                    wardrobeViewModel.getClothingById(it)
                }

            if (clothingItem != null) {

                ClothingDetailsScreen(
                    clothingItem = clothingItem,

                    onEditClick = {
                        navController.navigate(
                            "${NavRoutes.EditClothing}/${clothingItem.id}"
                        )
                    },

                    onDeleteClick = {
                        wardrobeViewModel.deleteClothing(
                            clothingItem.id
                        )

                        navController.popBackStack()
                    }
                )
            }
        }
        composable(
            route = "${NavRoutes.EditClothing}/{clothingId}"
        ) { backStackEntry ->

            val clothingId =
                backStackEntry.arguments?.getString("clothingId")

            val clothingItem =
                clothingId?.let {
                    wardrobeViewModel.getClothingById(it)
                }

            if (clothingItem != null) {

                EditClothingScreen(
                    clothingItem = clothingItem,

                    onClothingUpdated = { updatedItem ->

                        wardrobeViewModel.updateClothing(
                            updatedItem
                        )

                        navController.popBackStack()
                    }
                )
            }
        }

        composable(NavRoutes.AddClothing) {
            AddClothingScreen(
                onClothingSaved = { clothingItem ->
                    wardrobeViewModel.addClothing(clothingItem) { success ->
                        if (success) {
                            navController.popBackStack()
                        }
                    }
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
        composable(NavRoutes.Studio) {
            MainAppScaffold(navController = navController) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Studio — Coming Soon")
                }
            }
        }

        composable(NavRoutes.Inspire) {
            MainAppScaffold(navController = navController) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Inspire — Coming Soon")
                }
            }
        }


        composable(NavRoutes.Planner) {
//            val outfitPlannerViewModel: OutfitPlannerViewModel =
//                hiltViewModel()

            val outfits by outfitPlannerViewModel.outfits
                .collectAsStateWithLifecycle()
            val clothingItems by wardrobeViewModel.clothingItems
                .collectAsStateWithLifecycle()

            MainAppScaffold(navController = navController) {
                OutfitPlannerScreen(
                    outfits = outfits,
                    clothingItems=clothingItems,
                    onCreateOutfitClick = {
                        navController.navigate(NavRoutes.CreateOutfit)
                    },
                    onDeleteOutfit = { outfitId ->
                        outfitPlannerViewModel.deleteOutfit(outfitId)
                    },
                    onMarkAsWorn = { outfitId, isWorn ->
                        outfitPlannerViewModel.markOutfitAsWorn(
                            outfitId,
                            isWorn
                        )
                    }
                    ,
                    onEditClick = { outfit ->
                        navController.navigate(
                            "${NavRoutes.EditOutfit}/${outfit.id}"
                        )
                    }
                )
            }
        }


        composable(NavRoutes.CreateOutfit) {
            val clothingItems by wardrobeViewModel.clothingItems
                .collectAsStateWithLifecycle()

//            val outfitPlannerViewModel: OutfitPlannerViewModel =
//                hiltViewModel()

            val isSaving by outfitPlannerViewModel.isSaving
                .collectAsStateWithLifecycle()

            val errorMessage by outfitPlannerViewModel.errorMessage
                .collectAsStateWithLifecycle()

            CreateOutfitScreen(
                clothingItems = clothingItems,
                onBackClick = {
                    navController.popBackStack()
                },
                onSaveClick = { name, occasion, clothingIds, date ->
                    outfitPlannerViewModel.saveOutfit(
                        name = name,
                        occasion = occasion,
                        clothingIds = clothingIds,
                        plannedDate = date
                    ) { success ->
                        if (success) {
                            navController.popBackStack()
                        }
                    }
                }
            )

            errorMessage?.let { message ->
                androidx.compose.material3.Text(
                    text = message,
                    color = androidx.compose.ui.graphics.Color.Red
                )
            }
        }

        composable(
            route = "${NavRoutes.EditOutfit}/{outfitId}"
        ) { backStackEntry ->

            val outfitId = backStackEntry.arguments
                ?.getString("outfitId")

            val outfits by outfitPlannerViewModel.outfits
                .collectAsStateWithLifecycle()

            val clothingItems by wardrobeViewModel.clothingItems
                .collectAsStateWithLifecycle()

            val outfit = outfits.find { it.id == outfitId }

            if (outfit != null) {
                EditOutfitScreen(
                    outfit = outfit,
                    clothingItems = clothingItems,
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onUpdateClick = { updatedOutfit ->
                        outfitPlannerViewModel.updateOutfit(updatedOutfit) { success ->
                            if (success) {
                                navController.popBackStack()
                            }
                        }
                    }
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Loading outfit...")
                }
            }
        }


        composable(NavRoutes.Insights) {
            MainAppScaffold(navController = navController) {
                InsightsScreen()
            }
        }



    }
}
