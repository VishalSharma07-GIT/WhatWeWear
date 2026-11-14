package com.vishalsharma.whatwewear.data.mapper

import com.vishalsharma.whatwewear.data.remote.model.ClothingDto
import com.vishalsharma.whatwewear.domain.model.ClothingCategory
import com.vishalsharma.whatwewear.domain.model.ClothingItem
import com.vishalsharma.whatwewear.domain.model.Occasion
import com.vishalsharma.whatwewear.domain.model.Season

fun ClothingDto.toDomain() = ClothingItem(
    id = id, name = name, brand = brand, category = category,
    imageUri = imageUrl, color = color, size = size,
    season = season, occasion = occasion,
    type = type, createdAt = createdAt
)

fun ClothingItem.toDto() = ClothingDto(
    id = id, name = name, brand = brand, category = category,
    type = type, color = color, size = size,
    imageUrl = imageUri.orEmpty(),
    season = season, occasion = occasion,
    createdAt = createdAt
)