package com.example.laba

import androidx.compose.material3.DatePickerDialog as M3DatePickerDialog
import android.content.res.Configuration
import android.view.Window
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.datetime.*

@Composable
fun MoodApp() {
    val viewModel: MoodViewModel = viewModel()
    val moodList by viewModel.moodList.collectAsState()

    // Состояние формы
    var comment by remember { mutableStateOf("") }
    var selectedMood by remember { mutableStateOf<Mood?>(null) }
    var selectedDate by remember {
        mutableStateOf(Clock.System.todayIn(TimeZone.currentSystemDefault()))
    }
    var showDatePicker by remember { mutableStateOf(false) }

    // Настройка системных баров (как было в вашем коде)
    val view = LocalView.current
    DisposableEffect(Unit) {
        val window: Window = (view.context as androidx.activity.ComponentActivity).window
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowCompat.getInsetsController(window, view)
        controller.hide(WindowInsetsCompat.Type.statusBars())
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        onDispose {
            controller.show(WindowInsetsCompat.Type.statusBars())
            WindowCompat.setDecorFitsSystemWindows(window, true)
        }
    }

    // Обработчик добавления записи
    val onAddEntry = {
        if (comment.isNotBlank() && selectedMood != null) {
            viewModel.addHumanMood(
                message = comment,
                currentDate = Clock.System.todayIn(TimeZone.currentSystemDefault()),
                humanMood = selectedMood!!
            )
            comment = ""
            selectedMood = null
        }
    }

    // 1. Получаем конфигурацию устройства
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        // Учитываем вырезы экрана и системные отступы
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->

        // Главный контейнер
        Box(modifier = Modifier.padding(innerPadding).padding(16.dp)) {

            if (isLandscape) {
                // === ГОРИЗОНТАЛЬНАЯ ОРИЕНТАЦИЯ (ROW) ===
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Левая часть: Форма ввода (делаем скроллящейся, чтобы влезла клавиатура)
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.Top
                    ) {
                        Text(
                            text = "Дневник настроения",
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        MoodInputForm(
                            selectedDate = selectedDate,
                            onOpenDatePicker = { showDatePicker = true },
                            selectedMood = selectedMood,
                            onMoodSelected = { selectedMood = it },
                            comment = comment,
                            onCommentChange = { comment = it },
                            onAddClick = onAddEntry
                        )
                    }

                    // Правая часть: Список
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                        // В горизонтальном режиме заголовок списка можно добавить сюда или скрыть
                        MoodListContent(
                            moodList = moodList,
                            onDelete = { viewModel.deleteHumanMood(it) }
                        )
                    }
                }
            } else {
                // === ВЕРТИКАЛЬНАЯ ОРИЕНТАЦИЯ (COLUMN) ===
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = "Дневник настроения",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(bottom = 16.dp, top = 16.dp)
                    )

                    MoodInputForm(
                        selectedDate = selectedDate,
                        onOpenDatePicker = { showDatePicker = true },
                        selectedMood = selectedMood,
                        onMoodSelected = { selectedMood = it },
                        comment = comment,
                        onCommentChange = { comment = it },
                        onAddClick = onAddEntry
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Список занимает оставшееся место
                    MoodListContent(
                        moodList = moodList,
                        onDelete = { viewModel.deleteHumanMood(it) },
                        modifier = Modifier.weight(1f) // Важно для вертикальной ориентации
                    )
                }
            }
        }
    }

    // Логика DatePicker осталась прежней
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

// === ВСПОМОГАТЕЛЬНЫЕ COMPOSABLE ===

@Composable
fun MoodInputForm(
    selectedDate: LocalDate,
    onOpenDatePicker: () -> Unit,
    selectedMood: Mood?,
    onMoodSelected: (Mood) -> Unit,
    comment: String,
    onCommentChange: (String) -> Unit,
    onAddClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        DateSelector(
            selectedDate = selectedDate,
            onOpenDatePicker = onOpenDatePicker
        )

        Spacer(modifier = Modifier.height(8.dp))

        MoodSelector(
            selectedMood = selectedMood,
            onMoodSelected = onMoodSelected
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = comment,
            onValueChange = onCommentChange,
            label = { Text("Ваш комментарий") },
            placeholder = { Text("Как прошел день") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onAddClick,
            enabled = comment.isNotBlank() && selectedMood != null,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Добавить запись")
        }
    }
}

@Composable
fun MoodListContent(
    moodList: List<HumanMood>,
    onDelete: (HumanMood) -> Unit,
    modifier: Modifier = Modifier
) {
    if (moodList.isEmpty()) {
        Box(
            modifier = modifier.fillMaxWidth().padding(top = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Пока нет записей \nДобавьте первую!",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    } else {
        LazyColumn(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(moodList, key = { it.id }) { mood -> // Добавил key для производительности
                MoodItem(
                    humanMood = mood,
                    onDelete = { onDelete(mood) }
                )
            }
        }
    }
}