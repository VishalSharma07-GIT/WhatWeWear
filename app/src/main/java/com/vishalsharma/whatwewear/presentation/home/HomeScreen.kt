package com.vishalsharma.whatwewear.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.vishalsharma.whatwewear.presentation.home.components.CuratedLookSection
import com.vishalsharma.whatwewear.presentation.home.components.RecentlyDigitizedSection
import com.vishalsharma.whatwewear.presentation.home.components.StyleAnalyticsCard

@Composable
fun HomeScreen(
    onAddClothingClick: () -> Unit,
    onWardrobeClick: () -> Unit,
    onClothingClick: (String) -> Unit,
    onProfileClick: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(viewModel, lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            viewModel.refreshClothingItems()
            kotlinx.coroutines.awaitCancellation()
        }
    }

    val hasLookBasics = uiState.totalTops > 0 &&
            uiState.totalBottoms > 0 &&
            uiState.totalShoes > 0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.Top
    ) {
        HomeHeader(
            initial = uiState.userName.firstOrNull()?.uppercase() ?: "U",
            onProfileClick = onProfileClick
        )

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

        Spacer(modifier = Modifier.height(24.dp))

        DigitizeCard(onClick = onAddClothingClick)
        uiState.error?.let {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Couldn't load your wardrobe right now.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Today's Curated Looks",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (hasLookBasics) {
            CuratedLookSection(
                currentIndex = uiState.currentLookIndex,
                totalLooks = 3,
                title = uiState.currentLookTitle,
                clothingItems = uiState.currentLookItems,
                onPreviousClick = { viewModel.selectLook(uiState.currentLookIndex - 1) },
                onNextClick = { viewModel.selectLook(uiState.currentLookIndex + 1) },
                onCardClick = onWardrobeClick,
                onGetTheLookClick = onWardrobeClick
            )
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFDFAF7))
            ) {
                Text(
                    text = "Add at least one top, one bottom and one pair of shoes to see your curated looks.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF77736F),
                    modifier = Modifier.padding(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        StyleAnalyticsCard(
            totalClothes = uiState.totalClothes,
            totalTops = uiState.totalTops,
            totalBottoms = uiState.totalBottoms,
            totalShoes = uiState.totalShoes,
            clothingItems = uiState.allClothes
        )

        Spacer(modifier = Modifier.height(32.dp))

        RecentlyDigitizedSection(
            clothes = uiState.recentClothes,
            onViewAllClick = onWardrobeClick,
            onItemClick = { clothing -> onClothingClick(clothing.id) }
        )

        // Space so the bottom bar never covers the last item
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun HomeHeader(
    initial: String,
    onProfileClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxWidth().height(48.dp)) {
        Text(
            text = "WWW",
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            color = Color(0xFF211B18),
            modifier = Modifier.align(Alignment.Center)
        )
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0xFFEFE3DE))
                .clickable(onClick = onProfileClick),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = initial,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color(0xFF493D35)
            )
        }
    }
}

@Composable
private fun DigitizeCard(onClick: () -> Unit)
{
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEFE3DE)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                text = "Digitize",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Medium,
                fontSize = 26.sp,
                color = Color(0xFF29231F)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Take a photo of a piece and we'll add it to your wardrobe.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF77736F)
            )
            Spacer(modifier = Modifier.height(20.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "ADD ITEM  ↗",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = Color(0xFF493D35)
                )
            }
        }
    }
}
