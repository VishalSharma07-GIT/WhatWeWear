package com.vishalsharma.whatwewear.domain.model

enum class ClothingCategory(val label: String) {
    TOP("Tops"), BOTTOM("Bottoms"), SHOES("Shoes"),
    OUTERWEAR("Outerwear"), DRESS("Dresses"), ACCESSORY("Accessories");

    companion object {
        fun fromString(value: String?) =
            entries.firstOrNull { it.name.equals(value, true) || it.label.equals(value, true) } ?: TOP
    }
}