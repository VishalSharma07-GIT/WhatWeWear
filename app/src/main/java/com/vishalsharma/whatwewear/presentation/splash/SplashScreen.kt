package com.vishalsharma.whatwewear.presentation.splash

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.vishalsharma.whatwewear.R
import com.vishalsharma.whatwewear.ui.theme.Background
import com.vishalsharma.whatwewear.ui.theme.Dimens
import kotlinx.coroutines.delay
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vishalsharma.whatwewear.data.preferences.OnboardingPreferences

@Composable
fun SplashScreen(
    onNavigationDecided: (
        hasCompletedOnboarding: Boolean,
        isLoggedIn: Boolean
    ) -> Unit
){

    val viewModel: SplashViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var startAnimation by remember{
        mutableStateOf(false)
    }
    val context = LocalContext.current
    val onboardingPreferences = remember {
        OnboardingPreferences(context)
    }

    val hasCompletedOnboarding by onboardingPreferences
        .hasCompletedOnboarding
        .collectAsState(initial = false)

    LaunchedEffect(uiState.isLoading) {

        startAnimation = true

        if (!uiState.isLoading) {

            onNavigationDecided(
                hasCompletedOnboarding,
                uiState.isLoggedIn
            )

        }

    }
    val alpha by animateFloatAsState(
        targetValue = if(startAnimation) 1f else 0f,
        tween(durationMillis = 1200),
        label="SplashAlpha"
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Background
    ) {

        Box(
            modifier = Modifier.fillMaxSize()
        ) {

            Image(
                painter = painterResource(id = R.drawable.applogo3),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(Dimens.SplashLogoSize)
                    .graphicsLayer {
                        this.alpha = alpha
                    }
                    .align(Alignment.Center)
            )

            Text(
                text = "DESIGNED FOR YOUR CLOSET",
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = Dimens.SplashBottomPadding),
                style = MaterialTheme.typography.labelLarge,
                letterSpacing = 3.sp,
                textAlign = TextAlign.Center
            )
        }

    }
}
