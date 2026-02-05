package com.example.laba

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

data class MoodAppState(
    val comment: String = "",
    val selectedMood: Mood? = null,
    val selectedDate: LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault()),
    val showDatePicker: Boolean = false,
    val moodList: List<HumanMood> = emptyList()
)