package com.vishalsharma.whatwewear.presentation.home

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import androidx.lifecycle.viewModelScope
import com.vishalsharma.whatwewear.data.mapper.toDomain
import com.vishalsharma.whatwewear.data.repository.WardrobeRepository
import kotlinx.coroutines.launch
import com.vishalsharma.whatwewear.data.remote.weather.WeatherService
import com.vishalsharma.whatwewear.domain.model.ClothingItem

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val wardrobeRepository: WardrobeRepository
) : ViewModel() {

    private val _uiState= MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHomeData()
        loadClothingItems()
        loadWeather()
    }


    private fun loadHomeData() {
        _uiState.value = HomeUiState(
            userName = "Vishal",
            greeting = getGreeting(),
            temperature = "31°C",
            weatherDescription = "Perfect day to dress up",
            totalClothes = 0,
            totalTops = 0,
            totalBottoms = 0,
            totalShoes = 0,
            favoriteCount = 0,
            currentLookIndex = 0,
            currentLookTitle = "Casual Weekend"
        )
    }
    private fun loadWeather() {
        viewModelScope.launch {
            try {
                val weather = WeatherService.getCurrentWeather()

                _uiState.value = _uiState.value.copy(
                    temperature = weather.temperature,
                    weatherDescription = weather.description
                )
            } catch (e: Exception) {

            }
        }
    }

    private fun getGreeting(): String {
        return when (java.time.LocalTime.now().hour) {
            in 5..11 -> "Good Morning"
            in 12..16 -> "Good Afternoon"
            in 17..20 -> "Good Evening"
            else -> "Good Night"
        }
    }



    private fun getItemsForLook(
        index: Int,
        allClothes: List<ClothingItem>
    ): List<ClothingItem> {

        if (index !in 0..2 || allClothes.isEmpty()) {
            return emptyList()
        }

        val occasionKeywords = when (index) {
            0 -> listOf("casual", "weekend", "everyday")
            1 -> listOf("smart", "formal", "office")
            else -> listOf("evening", "party", "date")
        }

        fun matchesOccasion(item: ClothingItem): Boolean {
            return occasionKeywords.any { keyword ->
                item.occasion.contains(keyword, ignoreCase = true)
            }
        }

        // Match keywords against both the category and item name.
        fun matchesItem(
            item: ClothingItem,
            keywords: List<String>
        ): Boolean {
            return keywords.any { keyword ->
                item.category.contains(keyword, ignoreCase = true) ||
                        item.name.contains(keyword, ignoreCase = true)
            }
        }

        // Choose different items for different looks whenever possible.
        fun pickItem(
            candidates: List<ClothingItem>
        ): ClothingItem? {

            val pool = candidates
                .distinctBy { it.id }
                .sortedBy { it.id }

            if (pool.isEmpty()) return null

            val occasionMatches = pool.filter { matchesOccasion(it) }
            val available = occasionMatches.ifEmpty { pool }

            return available[index % available.size]
        }

        val tops = allClothes.filter {
            matchesItem(
                it,
                listOf(
                    "top", "shirt", "t-shirt", "tshirt",
                    "tee", "blouse", "polo", "sweater",
                    "hoodie", "sweatshirt", "kurta"
                )
            )
        }

        val bottoms = allClothes.filter {
            matchesItem(
                it,
                listOf(
                    "bottom", "jeans", "trouser", "pants",
                    "shorts", "skirt", "leggings", "chinos"
                )
            )
        }

        val shoes = allClothes.filter {
            matchesItem(
                it,
                listOf(
                    "shoe", "sneaker", "boot", "sandal",
                    "footwear", "loafer", "heels"
                )
            )
        }

        val dresses = allClothes.filter {
            matchesItem(
                it,
                listOf("dress", "jumpsuit", "romper")
            )
        }

        val accessories = allClothes.filter {
            matchesItem(
                it,
                listOf(
                    "accessory", "accessories",
                    "watch", "jewelry", "jewellery",
                    "belt", "bag", "sunglasses",
                    "hat", "cap", "scarf",
                    "bracelet", "necklace", "ring", "wallet"
                )
            )
        }

        val outfit = mutableListOf<ClothingItem>()

        // Prefer a dress; otherwise combine a top and bottom.
        val dress = pickItem(dresses)

        if (dress != null) {
            outfit.add(dress)
        } else {
            val top = pickItem(tops)
            val bottom = pickItem(bottoms)

            if (top != null) outfit.add(top)
            if (bottom != null) outfit.add(bottom)
        }

        // Add shoes if available.
        val shoe = pickItem(shoes)

        if (shoe != null && outfit.none { it.id == shoe.id }) {
            outfit.add(shoe)
        }

        // Add an accessory, such as a watch, if available.
        val accessory = pickItem(
            accessories.filter { candidate ->
                outfit.none { it.id == candidate.id }
            }
        )

        if (accessory != null) {
            outfit.add(accessory)
        }

        return outfit.distinctBy { it.id }
    }
    fun selectLook(index: Int) {
        val looks = listOf(
            "Casual Weekend",
            "Smart Casual",
            "Evening Style"
        )

        if (index !in looks.indices) return

        val allClothes = _uiState.value.allClothes

        _uiState.value = _uiState.value.copy(
            currentLookIndex = index,
            currentLookTitle = looks[index],
            currentLookItems = getItemsForLook(index, allClothes)
        )
    }


    private fun loadClothingItems() {
        viewModelScope.launch {
            try {
                val clothingItems = wardrobeRepository.getClothing()
                    .map { it.toDomain() }

                val colorCounts = clothingItems
                    .map { it.color.trim().ifBlank { "Unspecified" } }
                    .groupingBy { it }
                    .eachCount()
                    .toSortedMap()

                _uiState.value = _uiState.value.copy(
                    allClothes = clothingItems,
                    totalClothes = clothingItems.size,

                    totalTops = clothingItems.count {
                        it.category.equals("Tops", ignoreCase = true)
                    },

                    totalBottoms = clothingItems.count {
                        it.category.equals("Bottoms", ignoreCase = true)
                    },

                    totalShoes = clothingItems.count {
                        it.category.equals("Shoes", ignoreCase = true)
                    },

                    recentClothes = clothingItems.take(4),
                    error = null,
                    colorCounts = colorCounts,
                    currentLookItems = getItemsForLook(
                        _uiState.value.currentLookIndex,
                        clothingItems
                    )
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Unable to load wardrobe items"
                )
            }
        }
    }

    fun refreshClothingItems() {
        loadClothingItems()
    }
}




