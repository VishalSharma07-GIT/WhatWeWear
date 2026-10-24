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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import androidx.compose.runtime.LaunchedEffect
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
import com.vishalsharma.whatwewear.presentation.home.components.RecentlyDigitizedSection
import com.vishalsharma.whatwewear.presentation.home.components.StyleAnalyticsCard
import com.vishalsharma.whatwewear.presentation.home.components.StyleAssistantButton
import com.vishalsharma.whatwewear.presentation.home.components.SustainabilityCard
import com.vishalsharma.whatwewear.presentation.home.components.WeatherCard

@Composable
fun HomeScreen(
    onAddClothingClick: () -> Unit,
    onPlannerClick: () -> Unit,
    onStudioClick: () -> Unit,
    onWardrobeClick: () -> Unit,
    onClothingClick: (String) -> Unit,
    onInsightsClick: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
)
 {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(viewModel, lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            viewModel.refreshClothingItems()
            kotlinx.coroutines.awaitCancellation()
        }
    }


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
                text = "${uiState.greeting},\n${uiState.userName}",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 35.sp,
                    lineHeight = 48.sp,
                    letterSpacing = (-1).sp
                ),
                color = Color(0xFF211B18)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Curating your elegance for the day ahead.",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 16.sp,
                    lineHeight = 24.sp
                ),
                color = Color(0xFF77736F)
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
                totalLooks = 3,
                title = uiState.currentLookTitle,
                clothingItems = uiState.currentLookItems,
                onPreviousClick = {
                    viewModel.selectLook(uiState.currentLookIndex - 1)
                },
                onNextClick = {
                    viewModel.selectLook(uiState.currentLookIndex + 1)
                },
                onGetTheLookClick = onStudioClick
            )

            Spacer(modifier = Modifier.height(32.dp))


            Text(
                text = "Style Analytics",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(16.dp))


            StyleAnalyticsCard(
                totalClothes = uiState.totalClothes,
                totalTops = uiState.totalTops,
                totalBottoms = uiState.totalBottoms,
                totalShoes = uiState.totalShoes,
                onViewInsightsClick = onInsightsClick
            )



            Spacer(modifier = Modifier.height(32.dp))

            ProTipCard(
                tip = uiState.proTip
            )

            Spacer(modifier = Modifier.height(32.dp))

            RecentlyDigitizedSection(
                clothes = uiState.recentClothes,
                onViewAllClick = onWardrobeClick,
                onItemClick = { clothing ->
                    onClothingClick(clothing.id)
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Wardrobe: ${uiState.totalClothes} clothes",
                style = MaterialTheme.typography.bodyLarge
            )

            // Extra bottom space so the FAB doesn't cover the last content
            Spacer(modifier = Modifier.height(80.dp))
        }

        StyleAssistantButton(
            onClick = {

            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
        )
    }
}