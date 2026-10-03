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
        imageRes = null,
        imageUri = imageUrl
    )
}