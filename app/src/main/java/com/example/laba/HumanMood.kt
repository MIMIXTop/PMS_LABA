package com.example.laba

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

data class HumanMood(
    val id: Int,
    val comment: String,
    val mood: Mood,
    val date: LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault())
)

enum class Mood {
    Bad, Normal, Good
}
