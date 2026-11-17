package com.vishalsharma.whatwewear.presentation.home

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import coil.compose.AsyncImage
import com.vishalsharma.whatwewear.domain.model.ClothingItem
import com.vishalsharma.whatwewear.ui.theme.AppColors
import com.vishalsharma.whatwewear.utils.rememberImageModel
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    onAddClothingClick: () -> Unit,
    onClothingClick: (String) -> Unit,
    onProfileClick: () -> Unit,
    onWardrobeClick: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val haptics = LocalHapticFeedback.current

    // The day waiting for the "Unwear?" confirmation
    var confirmUnwearKey by remember { mutableStateOf<String?>(null) }
    val todayKey = state.days.getOrNull(state.todayIndex)?.dateKey

    fun toast(message: String) =
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

    fun saveFailed(ok: Boolean) {
        if (!ok) toast("Couldn't save. Check your connection and try again.")
    }

    // Refresh the date whenever the app comes back
    LaunchedEffect(viewModel, lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            viewModel.onResume()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background)
            .verticalScroll(rememberScrollState())
            .padding(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 24.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = state.dateLine, color = AppColors.Muted, fontSize = 16.sp)
                Text(
                    text = "${state.greeting}, ${state.userName.ifBlank { "there" }}",
                    color = AppColors.Ink,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp,
                    lineHeight = 38.sp
                )
            }
            Box(
                modifier = Modifier
                    .padding(start = 12.dp)
                    .size(56.dp)
                    .clip(CircleShape)
                    .border(1.5.dp, AppColors.Ink, CircleShape)
                    .clickable(onClick = onProfileClick),
                contentAlignment = Alignment.Center
            ) {
                if (state.userName.isNotBlank()) {
                    Text(
                        text = state.userName.first().uppercase(),
                        color = AppColors.Ink,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                } else {
                    Icon(Icons.Outlined.Person, contentDescription = "Profile", tint = AppColors.Ink)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        when {
            state.isLoading -> Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 80.dp),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator(color = AppColors.Ink) }

            state.loadFailed -> Text(
                text = "Couldn't load your wardrobe right now.",
                color = AppColors.Muted,
                modifier = Modifier.padding(top = 40.dp)
            )

            state.totalClothes == 0 -> EmptyHome(onAddClothingClick)

            else -> {
                // Occasion pills
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    state.occasions.forEachIndexed { index, label ->
                        val selected = index == state.selectedOccasion
                        val shape = RoundedCornerShape(50)
                        Text(
                            text = label,
                            color = if (selected) Color.White else AppColors.Ink,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier
                                .clip(shape)
                                .background(if (selected) AppColors.Ink else Color.Transparent)
                                .border(1.dp, if (selected) AppColors.Ink else AppColors.Border, shape)
                                .clickable {
                                    if (state.wornToday) {
                                        toast("Tap Unwear first to choose a different outfit.")
                                    } else {
                                        viewModel.selectOccasion(index)
                                    }
                                }
                                .padding(horizontal = 22.dp, vertical = 14.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                OutfitCollage(items = state.outfit, onClick = onClothingClick)

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = state.outfitTitle,
                    color = AppColors.Ink,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                if (state.outfitColors.isNotBlank()) {
                    Text(text = state.outfitColors, color = AppColors.Muted, fontSize = 17.sp)
                }
                state.occasionHint?.let { hint ->
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = hint, color = AppColors.Muted, fontSize = 13.sp, lineHeight = 18.sp)
                }

                Spacer(modifier = Modifier.height(18.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (state.wornToday) {
                        // The same button turns into Unwear
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(64.dp)
                                .clip(RoundedCornerShape(22.dp))
                                .border(1.5.dp, AppColors.Ink, RoundedCornerShape(22.dp))
                                .clickable { confirmUnwearKey = todayKey },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Unwear",
                                color = AppColors.Ink,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    } else {
                        Button(
                            onClick = {
                                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                viewModel.wearThis(::saveFailed)
                            },
                            enabled = state.outfit.isNotEmpty(),
                            modifier = Modifier
                                .weight(1f)
                                .height(64.dp),
                            shape = RoundedCornerShape(22.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AppColors.Ink,
                                contentColor = Color.White,
                                disabledContainerColor = AppColors.Border,
                                disabledContentColor = AppColors.Muted
                            )
                        ) {
                            Text("Wear this", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    val canShuffle = !state.wornToday && state.outfit.isNotEmpty()
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(22.dp))
                            .border(1.dp, AppColors.Border, RoundedCornerShape(22.dp))
                            .clickable(enabled = canShuffle) { viewModel.shuffle() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Refresh,
                            contentDescription = "Show another outfit",
                            tint = if (canShuffle) AppColors.Ink else AppColors.Border
                        )
                    }
                }

                if (state.wornToday) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Outlined.Check,
                            contentDescription = null,
                            tint = AppColors.Ink,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Worn today. Tap Unwear to choose a different outfit.",
                            color = AppColors.Muted,
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                CalendarSection(
                    days = state.days,
                    todayIndex = state.todayIndex,
                    onSelect = viewModel::selectDate
                )

                state.dayDetail?.let { detail ->
                    Spacer(modifier = Modifier.height(16.dp))
                    DayDetailCard(
                        detail = detail,
                        onItemClick = onClothingClick,
                        onRemove = { confirmUnwearKey = detail.dateKey },
                        onLog = { viewModel.logOutfit(detail.dateKey, ::saveFailed) }
                    )
                }

                state.reminder?.let { reminder ->
                    Spacer(modifier = Modifier.height(28.dp))
                    ReminderCard(reminder = reminder, onClick = { onClothingClick(reminder.item.id) })
                }
            }
        }
    }

    confirmUnwearKey?.let { key ->
        val isToday = key == todayKey
        AlertDialog(
            onDismissRequest = { confirmUnwearKey = null },
            title = { Text(if (isToday) "Unwear today's outfit?" else "Remove this day?") },
            text = {
                Text(
                    "This removes the log for ${if (isToday) "today" else "that day"} and " +
                            "takes one off the wear count of each piece."
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    viewModel.removeWear(key, ::saveFailed)
                    confirmUnwearKey = null
                }) { Text("Yes, unwear", color = AppColors.Danger, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { confirmUnwearKey = null }) {
                    Text("Cancel", color = AppColors.Ink)
                }
            }
        )
    }
}

@Composable
private fun EmptyHome(onAddClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(AppColors.Surface)
            .padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Your closet is empty",
            color = AppColors.Ink,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Add a few pieces and we'll style your day.",
            color = AppColors.Muted,
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = onAddClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AppColors.Ink, contentColor = Color.White)
        ) { Text("Add your first piece", fontWeight = FontWeight.SemiBold) }
    }
}

@Composable
private fun OutfitCollage(items: List<ClothingItem>, onClick: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(380.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutfitTile(items.getOrNull(0), Modifier.weight(1f).fillMaxHeight(), onClick)
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutfitTile(items.getOrNull(1), Modifier.weight(1f).fillMaxWidth(), onClick)
            OutfitTile(items.getOrNull(2), Modifier.weight(1f).fillMaxWidth(), onClick)
        }
    }
}

@Composable
private fun OutfitTile(item: ClothingItem?, modifier: Modifier, onClick: (String) -> Unit) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(28.dp))
            .background(AppColors.Surface)
            .then(if (item != null) Modifier.clickable { onClick(item.id) } else Modifier),
        contentAlignment = Alignment.Center
    ) {
        if (item != null) {
            AsyncImage(
                model = rememberImageModel(item.imageUri),
                contentDescription = item.name,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp)
            )
        }
    }
}

// Month heading, a "Today" chip, and a day strip that scrolls through the last 12 weeks
@Composable
private fun CalendarSection(
    days: List<DayCell>,
    todayIndex: Int,
    onSelect: (String) -> Unit
) {
    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = (todayIndex - 3).coerceAtLeast(0)
    )
    val scope = rememberCoroutineScope()
    val firstVisible by remember { derivedStateOf { listState.firstVisibleItemIndex } }
    val monthLabel = days.getOrNull(firstVisible + 3)?.monthLabel
        ?: days.getOrNull(todayIndex)?.monthLabel.orEmpty()

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = monthLabel,
            color = AppColors.Ink,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        val chipShape = RoundedCornerShape(50)
        Text(
            text = "Today",
            color = AppColors.Ink,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .clip(chipShape)
                .border(1.dp, AppColors.Border, chipShape)
                .clickable {
                    days.getOrNull(todayIndex)?.let { onSelect(it.dateKey) }
                    scope.launch { listState.animateScrollToItem((todayIndex - 3).coerceAtLeast(0)) }
                }
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }

    Spacer(modifier = Modifier.height(14.dp))

    LazyRow(
        state = listState,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        items(days, key = { it.dateKey }) { day ->
            DayColumn(day = day, onClick = { onSelect(day.dateKey) })
        }
    }
}

@Composable
private fun DayColumn(day: DayCell, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .width(54.dp)
            .clip(RoundedCornerShape(27.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = day.weekday,
            color = if (day.isToday) AppColors.Ink else AppColors.Muted,
            fontWeight = if (day.isToday) FontWeight.Bold else FontWeight.Normal,
            fontSize = 13.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .then(
                    if (day.isSelected) Modifier.border(2.dp, AppColors.Ink, CircleShape)
                    else Modifier
                )
                .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .then(
                        when {
                            day.worn -> Modifier.background(AppColors.Ink)
                            else -> Modifier.border(1.dp, AppColors.Border, CircleShape)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (day.worn) {
                    Icon(
                        Icons.Outlined.Check,
                        contentDescription = "Worn",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text(
                        text = "${day.dayNumber}",
                        color = if (day.isFuture) AppColors.Muted else AppColors.Ink,
                        fontSize = 15.sp,
                        fontWeight = if (day.isToday) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        // Small dot under today
        Box(
            modifier = Modifier
                .size(5.dp)
                .clip(CircleShape)
                .background(if (day.isToday) AppColors.Ink else Color.Transparent)
        )
    }
}

@Composable
private fun DayDetailCard(
    detail: DayDetail,
    onItemClick: (String) -> Unit,
    onRemove: () -> Unit,
    onLog: () -> Unit
) {
    val shape = RoundedCornerShape(24.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .border(1.dp, AppColors.Border, shape)
            .padding(16.dp)
    ) {
        Text(
            text = detail.title,
            color = AppColors.Ink,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(10.dp))

        if (detail.items.isNotEmpty()) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                detail.items.take(4).forEach { item ->
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(AppColors.Surface)
                            .clickable { onItemClick(item.id) },
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = rememberImageModel(item.imageUri),
                            contentDescription = item.name,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(6.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        Text(text = detail.message, color = AppColors.Muted, fontSize = 15.sp)

        if (detail.canRemove) {
            TextButton(onClick = onRemove) {
                Text("Unwear this day", color = AppColors.Danger, fontWeight = FontWeight.SemiBold)
            }
        }
        if (detail.canLog) {
            TextButton(onClick = onLog) {
                Text(
                    "Log the outfit above for this day",
                    color = AppColors.Ink,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun ReminderCard(reminder: Reminder, onClick: () -> Unit) {
    val shape = RoundedCornerShape(24.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .border(1.dp, AppColors.Border, shape)
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(76.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(AppColors.Surface),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = rememberImageModel(reminder.item.imageUri),
                contentDescription = reminder.item.name,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = reminder.item.name,
                color = AppColors.Ink,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1
            )
            Text(text = reminder.message, color = AppColors.Muted, fontSize = 15.sp)
        }
        Text(
            text = "View",
            color = AppColors.Ink,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold
        )
    }
}