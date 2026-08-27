package com.vishalsharma.whatwewear.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vishalsharma.whatwewear.presentation.home.components.WeatherCard
import com.vishalsharma.whatwewear.presentation.home.components.HomeTopBar
import com.vishalsharma.whatwewear.presentation.home.components.CuratedLookCard
import com.vishalsharma.whatwewear.R

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.Top
    ) {

        // Header
        HomeTopBar()

        Spacer(modifier = Modifier.height(32.dp))

        // Daily Style & Weather
        Text(
            text = "DAILY STYLE & WEATHER",
            style = MaterialTheme.typography.labelLarge
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "${uiState.greeting}, ${uiState.userName}.",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Weather section
        WeatherCard(
            temperature = uiState.temperature,
            description = uiState.weatherDescription
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Curated Looks
        Text(
            text = "Today's Curated Looks",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Temporary placeholder
        CuratedLookCard(
            imageRes = R.drawable.onboarding1,
            title = "Casual Weekend"
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Sustainability
        Text(
            text = "Sustainability Score",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        DashboardPlaceholder(
            text = "Your wardrobe sustainability score"
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Pro Tip
        Text(
            text = "PRO TIP",
            style = MaterialTheme.typography.labelLarge
        )

        Spacer(modifier = Modifier.height(12.dp))

        DashboardPlaceholder(
            text = "Personalized styling tip"
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Wardrobe information
        Text(
            text = "Wardrobe: ${uiState.totalClothes} clothes",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}


@Composable
private fun DashboardPlaceholder(
    text: String
) {
    androidx.compose.foundation.layout.Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}