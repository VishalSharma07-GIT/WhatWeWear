package com.vishalsharma.whatwewear.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.vishalsharma.whatwewear.data.mapper.toDomain
import com.vishalsharma.whatwewear.data.remote.model.WearLogDto
import com.vishalsharma.whatwewear.data.repository.WardrobeRepository
import com.vishalsharma.whatwewear.domain.model.ClothingItem
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.TemporalAdjusters
import java.util.Locale
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.time.Duration.Companion.milliseconds

// What each pill looks for. Pieces get these tags from the AI, and you can change them in Edit.
private data class OccasionRule(
    val label: String,
    val primary: List<String>,
    val secondary: List<String>
)

private val OCCASIONS = listOf(
    OccasionRule("Casual", primary = listOf("Casual"), secondary = listOf("Sports")),
    OccasionRule("Work", primary = listOf("Work"), secondary = listOf("Formal")),
    OccasionRule("Going out", primary = listOf("Party"), secondary = listOf("Formal", "Casual"))
)

// Set both to 0 if you want to see the reminder while testing
private const val UNWORN_MIN_DAYS = 3
private const val STALE_MIN_DAYS = 30

private const val DAY_MS = 24L * 60 * 60 * 1000
private const val SAVE_WAIT_MS = 5000L
private const val WEEKS_BACK = 12

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val wardrobeRepository: WardrobeRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val clothes = MutableStateFlow<List<ClothingItem>?>(null) // null = still loading
    private val wearLogs = MutableStateFlow<List<WearLogDto>>(emptyList())
    private val loadFailed = MutableStateFlow(false)
    private val occasionIndex = MutableStateFlow(0)
    private val shuffleCount = MutableStateFlow(0)
    private val today = MutableStateFlow(LocalDate.now())
    private val selectedDate = MutableStateFlow<LocalDate?>(null) // null = today

    private val data = combine(clothes, wearLogs, loadFailed) { c, l, f -> Triple(c, l, f) }

    val uiState: StateFlow<HomeUiState> =
        combine(data, occasionIndex, shuffleCount, today, selectedDate) { d, occasion, shuffle, day, selected ->
            buildState(d.first, d.second, d.third, occasion, shuffle, day, selected)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())

    init {
        viewModelScope.launch {
            wardrobeRepository.observeClothing()
                .catch {
                    loadFailed.value = true
                    clothes.value = emptyList()
                }
                .collect { dtos ->
                    clothes.value = dtos.map { it.toDomain() }
                    loadFailed.value = false
                }
        }
        viewModelScope.launch {
            wardrobeRepository.observeWearLog()
                .catch { /* the wear log is optional: Home still works without it */ }
                .collect { wearLogs.value = it }
        }
    }

    // Called whenever the app comes back to the screen
    fun onResume() {
        val now = LocalDate.now()
        if (now != today.value) {
            today.value = now
            selectedDate.value = null
        }
    }

    fun selectOccasion(index: Int) {
        occasionIndex.value = index
        shuffleCount.value = 0
    }

    fun shuffle() {
        shuffleCount.value += 1
    }

    fun selectDate(dateKey: String) {
        selectedDate.value = runCatching { LocalDate.parse(dateKey) }.getOrNull()
    }

    fun wearThis(onResult: (Boolean) -> Unit) = logOutfit(today.value.toString(), onResult)

    // Saves the outfit shown on screen as the outfit worn on that day
    fun logOutfit(dateKey: String, onResult: (Boolean) -> Unit) {
        val state = uiState.value
        if (state.outfit.isEmpty()) return
        if (wearLogs.value.any { it.date == dateKey }) return

        val day = runCatching { LocalDate.parse(dateKey) }.getOrNull() ?: return
        val items = clothes.value.orEmpty()
        val ids = state.outfit.map { it.id }.filter { id -> items.any { it.id == id } }
        if (ids.isEmpty()) return

        val wornAt = if (day == today.value) {
            System.currentTimeMillis()
        } else {
            day.atTime(12, 0).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        }
        // lastWornAt never goes backwards when an older day is logged
        val lastWornById = ids.associateWith { id ->
            maxOf(items.first { it.id == id }.lastWornAt, wornAt)
        }
        val label = OCCASIONS[state.selectedOccasion].label

        viewModelScope.launch {
            try {
                // Offline, the server answers late. The change is kept on the phone and syncs later.
                withTimeoutOrNull(SAVE_WAIT_MS.milliseconds) {
                    wardrobeRepository.markWorn(ids, label, dateKey, wornAt, lastWornById)
                    true
                }
                onResult(true)
            } catch (e: Exception) {
                onResult(false)
            }
        }
    }

    // Removes a day's log and takes one off each piece's wear count
    fun removeWear(dateKey: String, onResult: (Boolean) -> Unit) {
        val log = wearLogs.value.firstOrNull { it.date == dateKey } ?: return
        val items = clothes.value.orEmpty()
        val remaining = wearLogs.value.filter { it.date != dateKey }

        val updates = log.itemIds.mapNotNull { id ->
            val item = items.firstOrNull { it.id == id } ?: return@mapNotNull null // piece was deleted
            val lastWorn = remaining.filter { id in it.itemIds }.maxOfOrNull { it.wornAt } ?: 0L
            id to Pair(maxOf(0, item.wearCount - 1), lastWorn)
        }.toMap()

        viewModelScope.launch {
            try {
                withTimeoutOrNull(SAVE_WAIT_MS.milliseconds) {
                    wardrobeRepository.unmarkWorn(dateKey, updates)
                    true
                }
                onResult(true)
            } catch (e: Exception) {
                onResult(false)
            }
        }
    }

    private fun buildState(
        items: List<ClothingItem>?,
        logs: List<WearLogDto>,
        failed: Boolean,
        occasion: Int,
        shuffle: Int,
        day: LocalDate,
        selected: LocalDate?
    ): HomeUiState {
        val all = items.orEmpty()
        val todayKey = day.toString()
        val todayLog = logs.firstOrNull { it.date == todayKey }

        val wornOutfit = todayLog?.itemIds.orEmpty()
            .mapNotNull { id -> all.firstOrNull { it.id == id } }
        val wornToday = wornOutfit.isNotEmpty()

        val season = seasonFor(day.monthValue)
        val outfit = if (wornToday) wornOutfit
        else suggestOutfit(all, occasion, day.dayOfYear + shuffle * 5 + occasion * 11, season)

        val shownOccasion = if (wornToday) {
            OCCASIONS.indexOfFirst { it.label == todayLog?.occasion }.takeIf { it >= 0 } ?: occasion
        } else occasion

        // If nothing in the closet carries this pill's tag, say so
        val rule = OCCASIONS[shownOccasion]
        val hasTagged = all.any { item -> rule.primary.any { it.equals(item.occasion, true) } }
        val hint = if (!wornToday && all.isNotEmpty() && !hasTagged) {
            "No pieces are tagged ${rule.label} yet, so these are your closest matches. " +
                    "Change a piece's occasion under Edit to improve this."
        } else null

        val selectedDay = selected ?: day
        val thisMonday = day.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val start = thisMonday.minusWeeks(WEEKS_BACK.toLong())
        val end = thisMonday.plusDays(6)
        val loggedDates = logs.map { it.date }.toSet()
        val monthFormat = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault())

        val days = generateSequence(start) { it.plusDays(1) }
            .takeWhile { !it.isAfter(end) }
            .map { d ->
                DayCell(
                    dateKey = d.toString(),
                    weekday = d.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                    dayNumber = d.dayOfMonth,
                    monthLabel = d.format(monthFormat),
                    worn = d.toString() in loggedDates,
                    isToday = d == day,
                    isSelected = d == selectedDay,
                    isFuture = d.isAfter(day)
                )
            }
            .toList()
        val todayIndex = days.indexOfFirst { it.isToday }.coerceAtLeast(0)

        val dayDetail = if (selectedDay == day) null else {
            val log = logs.firstOrNull { it.date == selectedDay.toString() }
            val wornItems = log?.itemIds.orEmpty().mapNotNull { id -> all.firstOrNull { it.id == id } }
            DayDetail(
                dateKey = selectedDay.toString(),
                title = selectedDay.format(DateTimeFormatter.ofPattern("EEEE, d MMM", Locale.getDefault())),
                items = wornItems,
                message = when {
                    log != null && wornItems.isNotEmpty() -> wornItems.joinToString(" + ") { it.name }
                    log != null -> "These pieces are no longer in your closet."
                    selectedDay.isAfter(day) -> "Nothing planned yet."
                    else -> "Nothing logged for this day."
                },
                canRemove = log != null,
                canLog = log == null && !selectedDay.isAfter(day) && !wornToday && outfit.isNotEmpty()
            )
        }

        return HomeUiState(
            isLoading = items == null,
            loadFailed = failed,
            userName = getUserName(),
            greeting = getGreeting(),
            dateLine = "${day.format(DateTimeFormatter.ofPattern("EEE, d MMM", Locale.getDefault()))} · $season",
            selectedOccasion = shownOccasion,
            occasionHint = hint,
            outfit = outfit,
            outfitTitle = outfit.take(2).joinToString(" + ") { it.name },
            outfitColors = outfit.map { it.color.trim() }.filter { it.isNotBlank() }
                .distinct().joinToString(" · "),
            wornToday = wornToday,
            totalClothes = all.size,
            days = days,
            todayIndex = todayIndex,
            dayDetail = dayDetail,
            reminder = findReminder(all)
        )
    }

    // Scores every piece for the chosen occasion and the season, then picks from the best ones.
    // Shuffle rotates through the best few, so you see a different outfit each time.
    private fun suggestOutfit(
        clothes: List<ClothingItem>,
        occasion: Int,
        seed: Int,
        season: String
    ): List<ClothingItem> {
        if (clothes.isEmpty()) return emptyList()
        val rule = OCCASIONS[occasion]

        fun score(item: ClothingItem): Int {
            var points = 0
            if (rule.primary.any { it.equals(item.occasion, true) }) points += 6
            else if (rule.secondary.any { it.equals(item.occasion, true) }) points += 3
            if (item.season.equals(season, true)) points += 2
            return points
        }

        fun candidates(category: String): List<ClothingItem> {
            val ranked = clothes
                .filter { it.category.equals(category, ignoreCase = true) }
                .sortedWith(compareBy<ClothingItem>({ -score(it) }, { it.lastWornAt }, { it.id }))
            if (ranked.isEmpty()) return ranked
            val best = score(ranked.first())
            return ranked.filter { score(it) >= best - 2 }.take(4)
        }

        fun List<ClothingItem>.pick(index: Int): ClothingItem? =
            if (isEmpty()) null else this[Math.floorMod(index, size)]

        val tops = candidates("Tops")
        val bottoms = candidates("Bottoms")
        val dresses = candidates("Dresses")
        val shoes = candidates("Shoes")
        val outerwear = candidates("Outerwear")
        val accessories = candidates("Accessories")

        val result = mutableListOf<ClothingItem>()
        val useDress = dresses.isNotEmpty() &&
                (tops.isEmpty() || bottoms.isEmpty() || Math.floorMod(seed, 3) == 2)

        if (useDress) {
            dresses.pick(seed)?.let { result.add(it) }
        } else {
            tops.pick(seed)?.let { result.add(it) }
            bottoms.pick(seed + 1)?.let { result.add(it) }
        }
        shoes.pick(seed)?.let { result.add(it) }
        if (result.size < 3) {
            (accessories.pick(seed) ?: outerwear.pick(seed))?.let { result.add(it) }
        }
        return result.distinctBy { it.id }
    }

    private fun findReminder(all: List<ClothingItem>): Reminder? {
        val now = System.currentTimeMillis()

        val neverWorn = all
            .filter { it.wearCount == 0 && (now - it.createdAt) / DAY_MS >= UNWORN_MIN_DAYS }
            .minByOrNull { it.createdAt }
        if (neverWorn != null) return Reminder(neverWorn, "Not worn since you added it")

        val stale = all
            .filter { it.wearCount > 0 && it.lastWornAt > 0 && (now - it.lastWornAt) / DAY_MS >= STALE_MIN_DAYS }
            .minByOrNull { it.lastWornAt }
        return stale?.let {
            Reminder(it, "Not worn in ${(now - it.lastWornAt) / DAY_MS} days")
        }
    }

    private fun seasonFor(month: Int) = when (month) {
        in 3..5 -> "Spring"
        in 6..8 -> "Summer"
        in 9..11 -> "Autumn"
        else -> "Winter"
    }

    private fun getGreeting(): String = when (LocalTime.now().hour) {
        in 5..11 -> "Good morning"
        in 12..16 -> "Good afternoon"
        in 17..20 -> "Good evening"
        else -> "Good night"
    }

    private fun getUserName(): String {
        val user = auth.currentUser
        val fromName = user?.displayName?.trim()?.substringBefore(" ").orEmpty()
        val fromEmail = user?.email?.substringBefore("@").orEmpty()
        return fromName.ifBlank { fromEmail.replaceFirstChar { it.uppercase() } }
    }
}