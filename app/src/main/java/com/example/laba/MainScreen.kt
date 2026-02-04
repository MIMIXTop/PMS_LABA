package com.example.laba

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.datetime.*
import kotlinx.datetime.todayIn


@Composable
fun MoodApp() {
    val viewModel: MoodViewModel = viewModel()
    val moodList by viewModel.moodList.collectAsState()
    var comment by remember { mutableStateOf("") }
    var selectedMood by remember { mutableStateOf<Mood?>(null) }
    var selectedDate by remember {
        mutableStateOf(Clock.System.todayIn(TimeZone.currentSystemDefault()))
    }
    var showDatePicker by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Дневник настроения",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        DateSelector(
            selectedDate = selectedDate,
            onOpenDatePicker = { showDatePicker = true }
        )

        MoodSelector(
            selectedMood = selectedMood,
            onMoodSelected = { selectedMood = it }
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = comment,
            onValueChange = { comment = it },
            label = { Text("Ваш комментарий") },
            placeholder = { Text("Как прошел день") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                if (comment.isNotBlank() && selectedMood != null) {
                    viewModel.addHumanMood(
                        message = comment,
                        currentDate = Clock.System.todayIn(TimeZone.currentSystemDefault()),
                        humanMood = selectedMood!!
                    )
                    comment = ""
                    selectedMood = null
                }
            },
            enabled = comment.isNotBlank() && selectedMood != null,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Добавить запись")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Список записей
        if (moodList.isEmpty()) {
            Text(
                text = "Пока нет записей \nДобавьте первую!",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(moodList) { mood ->
                    MoodItem(
                        humanMood = mood,
                        onDelete = { viewModel.deleteHumanMood(mood) }
                    )
                }
            }
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            selectedDate = selectedDate,
            onDateSelected = { date ->
                selectedDate = date
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }


}

@Composable
fun MoodSelector(
    selectedMood: Mood?,
    onMoodSelected: (Mood) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(modifier = Modifier.weight(1f)) {
            MoodButton(
                mood = Mood.Bad,
                isSelected = selectedMood == Mood.Bad,
                onClick = { onMoodSelected(Mood.Bad) },
                emoji = "\uD83D\uDE41",
                label = "Плохо"
            )
        }

        Box(modifier = Modifier.weight(1f)) {
            MoodButton(
                mood = Mood.Normal,
                isSelected = selectedMood == Mood.Normal,
                onClick = { onMoodSelected(Mood.Normal) },
                emoji = "\uD83D\uDE10",
                label = "Нормально"
            )
        }

        Box(modifier = Modifier.weight(1f)) {
            MoodButton(
                mood = Mood.Good,
                isSelected = selectedMood == Mood.Good,
                onClick = { onMoodSelected(Mood.Good) },
                emoji = "\uD83D\uDE04",
                label = "Хорошо"
            )
        }
    }
}

@Composable
fun MoodButton(
    mood: Mood,
    isSelected: Boolean,
    onClick: () -> Unit,
    emoji: String,
    label: String
) {
    val containerColor = when (mood) {
        Mood.Bad -> if (isSelected) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surfaceVariant
        Mood.Normal -> if (isSelected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant
        Mood.Good -> if (isSelected) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.surfaceVariant
    }

    val contentColor = when (mood) {
        Mood.Bad -> if (isSelected) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onSurfaceVariant
        Mood.Normal -> if (isSelected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
        Mood.Good -> if (isSelected) MaterialTheme.colorScheme.onTertiaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
    }

    OutlinedButton(
        onClick = onClick,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        modifier = Modifier.fillMaxWidth(0.3f),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text(
                text = emoji,
                style = MaterialTheme.typography.displaySmall,
                modifier = Modifier.size(size = 24.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(label, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
fun DateSelector(
    selectedDate: LocalDate,
    onOpenDatePicker: () -> Unit
) {
    OutlinedButton(
        onClick = onOpenDatePicker,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium
    ) {
        Text(
            text = "Дата: ${formatDate(selectedDate)}",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun DatePickerDialog(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    val today = Clock.System.todayAt(TimeZone.currentSystemDefault())
    var date by remember { mutableStateOf(selectedDate) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Выбери дату") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { date = today },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Сегодня")
                    }
                    Button(
                        onClick = {
                            date = today.minus(DatePeriod(1))
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Вчера")
                    }

                    Text(
                        text = "Выбрано: ${formatDate(date)}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(date)
            }) {
                Text("Выбрать")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}