package com.example.laba

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.material3.DatePickerDialog as M3DatePickerDialog
import kotlinx.datetime.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditMoodDialog(
    originalMood: HumanMood,
    onDismiss: () -> Unit,
    onConfirm: (String, Mood, LocalDate) -> Unit
) {
    var comment by remember { mutableStateOf(originalMood.comment) }
    var selectedMood by remember { mutableStateOf(originalMood.mood) }
    var selectedDate by remember { mutableStateOf(originalMood.date) }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = originalMood.date
            .atStartOfDayIn(TimeZone.currentSystemDefault())
            .toEpochMilliseconds()
    )
    var showDatePicker by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Редактировать запись") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // === Выбор даты ===
                OutlinedTextField(
                    value = formatDate(selectedDate),
                    onValueChange = {},
                    label = { Text("Дата") },
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(Icons.Default.CalendarToday, contentDescription = "Выбрать дату")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                // === Выбор настроения ===
                Text("Настроение", style = MaterialTheme.typography.labelMedium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Mood.values().forEach { moodOption ->
                        val isSelected = selectedMood == moodOption
                        val chipColor = moodToColor(moodOption)

                        // Простой чип с ручной обводкой — работает везде
                        Surface(
                            onClick = { selectedMood = moodOption },
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSelected) chipColor else Color.Transparent,
                            contentColor = if (isSelected) Color.White else chipColor,
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .then(
                                    if (!isSelected) Modifier.border(
                                        width = 1.dp,
                                        color = chipColor,
                                        shape = RoundedCornerShape(16.dp)
                                    ) else Modifier
                                )
                        ) {
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 12.dp, vertical = 8.dp)

                            ) {
                                Text(
                                    text = moodToText(moodOption).split(" ").first(),
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        }
                    }
                }

                // === Комментарий ===
                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    label = { Text("Комментарий") },
                    placeholder = {
                        Text(
                            "Введите заметку...",
                            fontStyle = FontStyle.Italic,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 4
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (comment.isNotBlank()) {
                        onConfirm(comment, selectedMood, selectedDate)
                    }
                },
                enabled = comment.isNotBlank() &&
                        (comment != originalMood.comment ||
                                selectedMood != originalMood.mood ||
                                selectedDate != originalMood.date)
            ) {
                Text("Сохранить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )

    // === DatePicker ===
    if (showDatePicker) {
        M3DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        selectedDate = Instant.fromEpochMilliseconds(millis)
                            .toLocalDateTime(TimeZone.currentSystemDefault())
                            .date
                    }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Отмена") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

// Extension для красивого формата даты
private fun LocalDate.format(): String =
    "${this.dayOfMonth}.${this.monthNumber.toString().padStart(2, '0')}.${this.year}"

// Extension для emoji настроений
private val Mood.emoji: String
    get() = when (this) {
        Mood.Bad -> "😔"
        Mood.Normal -> "😐"
        Mood.Good -> "😊"
    }