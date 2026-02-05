package com.example.laba

import androidx.compose.material3.DatePickerDialog as M3DatePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.datetime.*
import kotlinx.datetime.todayIn
import android.view.Window
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

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

    val view = LocalView.current
    DisposableEffect(Unit) {
        val window: Window = (view.context as androidx.activity.ComponentActivity).window

        WindowCompat.setDecorFitsSystemWindows(window, false)

        val controller = WindowCompat.getInsetsController(window, view)
        controller.hide(WindowInsetsCompat.Type.statusBars())
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        onDispose {
            controller.show(WindowInsetsCompat.Type.statusBars())
            WindowCompat.setDecorFitsSystemWindows(window, true)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Дневник настроения",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp, top = 16.dp)
        )

        DateSelector(
            selectedDate = selectedDate,
            onOpenDatePicker = { showDatePicker = true }
        )

        Spacer(modifier = Modifier.height(8.dp))

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

        Spacer(modifier = Modifier.height(8.dp))

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
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDate.atStartOfDayIn(TimeZone.currentSystemDefault()).toEpochMilliseconds()
        )
        M3DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val newDate = Instant.fromEpochMilliseconds(millis)
                            .toLocalDateTime(TimeZone.currentSystemDefault())
                            .date
                        selectedDate = newDate
                    }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Отмена")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}


private fun MoodAppPortable() {

}