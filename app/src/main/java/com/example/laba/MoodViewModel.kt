package com.example.laba

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

class MoodViewModel : ViewModel() {
    private val _moodList = MutableStateFlow<List<HumanMood>>(emptyList())

    val moodList : StateFlow<List<HumanMood>> = _moodList

    private var nextId = 1

    fun addHumanMood(message: String, currentDate: LocalDate, humanMood: Mood) {
        viewModelScope.launch {
            _moodList.value += HumanMood(
                            id = nextId++,
                            comment = message.trim(),
                            mood = humanMood,
                            date = currentDate
                        )
        }
    }

    fun deleteHumanMood(humanMood: HumanMood) {
        viewModelScope.launch {
            _moodList.value = _moodList.value.filter {it.id != humanMood.id}
        }
    }
}