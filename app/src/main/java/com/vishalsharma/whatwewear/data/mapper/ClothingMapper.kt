package com.vishalsharma.whatwewear.data.mapper

import com.vishalsharma.whatwewear.data.remote.model.ClothingDto
import com.vishalsharma.whatwewear.domain.model.ClothingItem

fun ClothingDto.toDomain(): ClothingItem {
    return ClothingItem(
        id = id,
        name = name,
        brand = brand,
        category = category,
        color = color,
        size = size,
        imageUri = imageUrl,
        season = season,
        occasion = occasion
    )
}

fun ClothingItem.toDto(): ClothingDto {
    return ClothingDto(
        id = id,
        name = name,
        brand = brand,
        category = category,
        color = color,
        size = size,
        imageUrl = imageUri.orEmpty(),
        season = season,
        occasion = occasion
    )
}