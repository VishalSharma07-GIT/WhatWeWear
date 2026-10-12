package com.vishalsharma.whatwewear.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vishalsharma.whatwewear.R
import com.vishalsharma.whatwewear.presentation.home.components.CuratedLookSection
import com.vishalsharma.whatwewear.presentation.home.components.HomeActionCards
import com.vishalsharma.whatwewear.presentation.home.components.HomeTopBar
import com.vishalsharma.whatwewear.presentation.home.components.ProTipCard
import com.vishalsharma.whatwewear.presentation.home.components.StyleAssistantButton
import com.vishalsharma.whatwewear.presentation.home.components.SustainabilityCard
import com.vishalsharma.whatwewear.presentation.home.components.WeatherCard

@Composable
fun HomeScreen(
    onAddClothingClick: () -> Unit,
    onPlannerClick: () -> Unit,
    onStudioClick: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.Top
        ) {

            HomeTopBar()

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "YOUR PERSONAL STYLE, REIMAGINED",
                style = MaterialTheme.typography.labelLarge,
                color = Color(0xFF89827B),
                letterSpacing = 1.5.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "${uiState.greeting}, ${uiState.userName}.",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold
                ),
                color = Color(0xFF211B18)
            )

            Spacer(modifier = Modifier.height(16.dp))

            WeatherCard(
                temperature = uiState.temperature,
                description = uiState.weatherDescription
            )

            Spacer(modifier = Modifier.height(24.dp))

            HomeActionCards(
                onDigitizeClick = onAddClothingClick,
                onPlannerClick = onPlannerClick
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Today's Curated Looks",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(24.dp))

            CuratedLookSection(
                currentIndex = uiState.currentLookIndex,
                totalLooks = 2,
                imageRes = R.drawable.onboarding1,
                title = uiState.currentLookTitle,
                onPreviousClick = {
                    viewModel.selectLook(
                        uiState.currentLookIndex - 1
                    )
                },
                onNextClick = {
                    viewModel.selectLook(
                        uiState.currentLookIndex + 1
                    )
                },
                onGetTheLookClick = onStudioClick

            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Sustainability Score",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            SustainabilityCard(
                score = uiState.sustainabilityScore,
                message = uiState.sustainabilityMessage
            )

            Spacer(modifier = Modifier.height(32.dp))

            ProTipCard(
                tip = uiState.proTip
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Wardrobe: ${uiState.totalClothes} clothes",
                style = MaterialTheme.typography.bodyLarge
            )

            // Extra bottom space so the FAB doesn't cover the last content
            Spacer(modifier = Modifier.height(80.dp))
        }

        StyleAssistantButton(
            onClick = {
                // TODO: OPEN AI STYLE ASSISTANT
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
        )
    }
}