package com.vishalsharma.whatwewear.domain.model

enum class Occasion(val label: String) {
    CASUAL("Casual"), COLLEGE("College"), WORK("Work"),
    PARTY("Party"), WEDDING("Wedding"), SPORTS("Sports");

    companion object {
        fun fromString(value: String?) =
            entries.firstOrNull { it.name.equals(value, true) || it.label.equals(value, true) } ?: CASUAL
    }
}