package com.vishalsharma.whatwewear.domain.model

enum class Season(val label: String) {
    SUMMER("Summer"), WINTER("Winter"), SPRING("Spring"),
    AUTUMN("Autumn"), RAINY("Rainy"), ALL("All seasons");

    companion object {
        fun fromString(value: String?) =
            entries.firstOrNull { it.name.equals(value, true) || it.label.equals(value, true) } ?: ALL
    }
}