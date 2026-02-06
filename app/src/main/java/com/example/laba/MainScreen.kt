package com.example.laba

import android.annotation.SuppressLint
import androidx.compose.material3.DatePickerDialog as M3DatePickerDialog
import android.content.res.Configuration
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.laba.ui.theme.LABATheme
import kotlinx.datetime.*

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun MoodApp() {
    val viewModel: MoodViewModel = viewModel()
    val moodList by viewModel.moodList.collectAsState()
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    var comment by remember { mutableStateOf("") }
    var selectedMood by remember { mutableStateOf<Mood?>(null) }
    var selectedDate by remember {
        mutableStateOf(Clock.System.todayIn(TimeZone.currentSystemDefault()))
    }
    val datePickerState = rememberDatePickerState()
    var showDatePicker by remember { mutableStateOf(false) }


    LaunchedEffect(showDatePicker) {
        if (showDatePicker) {
            datePickerState.selectedDateMillis = selectedDate.atStartOfDayIn(TimeZone.currentSystemDefault()).toEpochMilliseconds()
            if (isLandscape) {
                datePickerState.displayMode = DisplayMode.Input
            } else {
                datePickerState.displayMode = DisplayMode.Picker
            }
        }
    }

    val onAddEntry = {
        if (comment.isNotBlank() && selectedMood != null) {
            viewModel.addHumanMood(
                message = comment,
                currentDate = selectedDate,
                humanMood = selectedMood!!
            )
            comment = ""
            selectedMood = null
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize().imePadding().systemBarsPadding(),
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->

        Box(modifier = Modifier.padding(innerPadding).padding(0.dp).imePadding()) {

            if (isLandscape) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                            //.heightIn(min = 0.dp, max = LocalConfiguration.current.screenHeightDp.dp)
                            ,
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

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .imePadding()
                    ) {
                        MoodListContent(
                            moodList = moodList,
                            onDelete = { viewModel.deleteHumanMood(it) }
                        )
                    }
                }
            } else {
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

                    MoodListContent(
                        moodList = moodList,
                        onDelete = { viewModel.deleteHumanMood(it) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }

    if (showDatePicker) {
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
            DatePicker(state = datePickerState, )
        }
    }
}
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

@Preview(showBackground = true)
@Composable
fun PreviewNew() {
    LABATheme {
        MoodApp()
    }
}