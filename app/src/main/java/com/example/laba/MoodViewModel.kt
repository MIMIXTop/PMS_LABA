package com.example.laba

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

class MoodViewModel(private val moodRepository: Repository) : ViewModel() {
    val moodList : StateFlow<List<HumanMood>> = moodRepository.getAllItemsStream()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private var nextId = 1

    fun addHumanMood(message: String, currentDate: LocalDate, humanMood: Mood) {
        viewModelScope.launch {
           val newMood = HumanMood(
               id = 0,
               comment = message.trim(),
               mood = humanMood,
               date = currentDate
           )

            moodRepository.insertItem(newMood)
        }
    }

    fun deleteHumanMood(humanMood: HumanMood) {
        viewModelScope.launch {
            moodRepository.deleteItem(humanMood)
        }
    }

    fun updateHumanMood(humanMood: HumanMood) {
        viewModelScope.launch {
            moodRepository.editeItem(humanMood)
        }
    }
}