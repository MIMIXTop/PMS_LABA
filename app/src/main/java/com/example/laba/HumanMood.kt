package com.example.laba

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

@Entity(tableName = "human_mood")
data class HumanMood(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val comment: String,
    val mood: Mood,
    val date: LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault())
)

enum class Mood {
    Bad, Normal, Good
}
