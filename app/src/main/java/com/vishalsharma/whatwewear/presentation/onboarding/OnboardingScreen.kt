package com.vishalsharma.whatwewear.presentation.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.vishalsharma.whatwewear.R
import com.vishalsharma.whatwewear.data.preferences.OnboardingPreferences
import com.vishalsharma.whatwewear.presentation.components.buttons.PrimaryButton
import com.vishalsharma.whatwewear.presentation.components.indicator.PageIndicator
import com.vishalsharma.whatwewear.presentation.navigation.NavRoutes
import com.vishalsharma.whatwewear.ui.theme.Dimens
import com.vishalsharma.whatwewear.ui.theme.Primary
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(
    navController: NavHostController
) {

    val pagerState = rememberPagerState(
        pageCount = { onboardingPages.size }
    )

    val scope = rememberCoroutineScope()

    val context = LocalContext.current

    val onboardingPreferences = remember {
        OnboardingPreferences(context)
    }

    val buttonText =
        if (pagerState.currentPage == onboardingPages.lastIndex) {
            "Get Started"
        } else {
            "Next"
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                horizontal = Dimens.PaddingLarge,
                vertical = Dimens.PaddingMedium
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Image(
                painter = painterResource(R.drawable.applogo3),
                contentDescription = "App Logo",
                modifier = Modifier.width(100.dp)
            )

            TextButton(
                onClick = {
                    scope.launch {

                        onboardingPreferences.saveOnboardingCompleted()

                        navController.navigate(NavRoutes.Login) {
                            popUpTo(NavRoutes.Onboarding) {
                                inclusive = true
                            }
                        }
                    }
                }
            ) {

                Text(
                    text = "Skip",
                    color = Primary,
                    style = MaterialTheme.typography.titleMedium
                )

            }
        }

        // Pager
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .padding(top = 12.dp)
        ) { page ->

            OnboardingPageContent(
                page = onboardingPages[page],
                modifier = Modifier.fillMaxSize()
            )

        }

        Spacer(modifier = Modifier.height(16.dp))

        PageIndicator(
            pageCount = onboardingPages.size,
            currentPage = pagerState.currentPage
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (pagerState.currentPage == onboardingPages.lastIndex) {

            PrimaryButton(
                text = "Get Started",
                onClick = {

                    scope.launch {

                        onboardingPreferences.saveOnboardingCompleted()

                        navController.navigate(NavRoutes.Login) {
                            popUpTo(NavRoutes.Onboarding) {
                                inclusive = true
                            }
                        }

                    }

                }
            )

        } else {

            Spacer(
                modifier = Modifier.height(Dimens.ButtonHeight)
            )

        }
    }
}