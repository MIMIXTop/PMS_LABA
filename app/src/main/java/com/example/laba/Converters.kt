package com.example.laba

import androidx.room.TypeConverter
import kotlinx.datetime.LocalDate

class Converters {
    @TypeConverter
    fun fromMood(mood: Mood): String {
        return mood.name
    }

    @TypeConverter
    fun toMood(moodString: String) : Mood {
        return enumValueOf<Mood>(moodString)
    }

    @TypeConverter
    fun fromLocalDate(date: LocalDate) : String {
        return date.toString()
    }

    @TypeConverter
    fun toLocalDate(dateString: String) : LocalDate {
        return LocalDate.parse(dateString)
    }
}