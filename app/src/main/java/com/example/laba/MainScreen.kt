package com.example.laba

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.DatePickerDialog as M3DatePickerDialog
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import kotlinx.datetime.*

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun MoodApp() {
    val context = LocalContext.current
    val viewModel: MoodViewModel = viewModel()
    val moodList by viewModel.moodList.collectAsState()
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val snackbarHostState = remember { SnackbarHostState() }
    val notificationHelper = remember { NotificationHelper(context) }

    var comment by remember { mutableStateOf("") }
    var selectedMood by remember { mutableStateOf<Mood?>(null) }
    var selectedDate by remember {
        mutableStateOf(Clock.System.todayIn(TimeZone.currentSystemDefault()))
    }
    val datePickerState = rememberDatePickerState()
    var showDatePicker by remember { mutableStateOf(false) }

    var showStatsScreen by remember { mutableStateOf(false) }

    val motivationalQuotes = remember {
        listOf(
            "После черной полосы всегда идет белая. Держитесь!",
            "Даже самая темная ночь всегда заканчивается рассветом.",
            "Не унывайте! Завтра будет новый день и новые возможности.",
            "Трудности делают нас сильнее. Вы обязательно со всем справитесь!",
            "Улыбнитесь! Вы прекрасны, и всё обязательно наладится \uD83D\uDC99",
            "Ошибки и сложные дни — это просто опыт. Дальше будет лучше!",
            "Позвольте себе отдохнуть сегодня. Вы заслуживаете заботы о себе."
        )
    }

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

            if (selectedMood == Mood.Bad) {
                val randomQuote = motivationalQuotes.random()
                notificationHelper.showNotification(
                    title = "Поддержка для вас",
                    message = randomQuote
                )
            }

            comment = ""
            selectedMood = null
        }
    }

    if (showStatsScreen) {
        StatsScreen(
            moodList = moodList,
            onBack = { showStatsScreen = false }
        )
    } else {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .systemBarsPadding(),
            contentWindowInsets = WindowInsets.safeDrawing,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            floatingActionButton = {
                FloatingActionButton(onClick = { showStatsScreen = true }) {
                    Icon(imageVector = Icons.Default.Analytics, contentDescription = "Stats")
                }
            }
        ) { innerPadding ->
            Box(modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 10.dp)
                .imePadding()) {
                if (isLandscape) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.Top
                        ) {
                            Text(
                                text = "Дневник настроения",
                                style = MaterialTheme.typography.headlineSmall,
                                modifier = Modifier.padding(bottom = 16.dp),
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
                        Column(modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .imePadding()) {
                            MoodListContent(moodList = moodList, onDelete = { viewModel.deleteHumanMood(it) })
                        }
                    }
                } else {
                    Column(modifier = Modifier.fillMaxSize()) {
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
            DatePicker(state = datePickerState)
        }
    }
}

// === ЭКРАН СТОЛБЧАТОГО ГРАФИКА (ЦВЕТ ЗАВИСИТ ОТ ВЫСОТЫ) ===
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(moodList: List<HumanMood>, onBack: () -> Unit) {
    var isMonthSelected by remember { mutableStateOf(false) }
    val textMeasurer = rememberTextMeasurer()
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())

    // Подготовка данных
    val chartData = remember(moodList, isMonthSelected) {
        val daysToSubtract = if (isMonthSelected) 30 else 7
        val startDate = today.minus(DatePeriod(days = daysToSubtract))

        // Группируем и усредняем
        val groupedData = moodList
            .filter { it.date >= startDate }
            .groupBy { it.date }
            .mapValues { entry ->
                entry.value.map {
                    when (it.mood) {
                        Mood.Bad -> 1f
                        Mood.Normal -> 2f
                        Mood.Good -> 3f
                    }
                }.average().toFloat()
            }

        // Заполняем пропуски нулями, чтобы график был полноценным
        val fullRangeList = mutableListOf<Pair<LocalDate, Float>>()
        var currentDateIterator = startDate.plus(DatePeriod(days = 1))
        val daysCount = if (isMonthSelected) 30 else 7

        repeat(daysCount) {
            val value = groupedData[currentDateIterator] ?: 0f
            fullRangeList.add(currentDateIterator to value)
            currentDateIterator = currentDateIterator.plus(DatePeriod(days = 1))
        }
        fullRangeList
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Моя энергия", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Назад") }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Переключатель Неделя/Месяц
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                FilterChip(
                    selected = !isMonthSelected,
                    onClick = { isMonthSelected = false },
                    label = { Text("За неделю") }
                )
                Spacer(modifier = Modifier.width(16.dp))
                FilterChip(
                    selected = isMonthSelected,
                    onClick = { isMonthSelected = true },
                    label = { Text("За месяц") }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "График за ${if(isMonthSelected) "месяц" else "неделю"}",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            ) {
                Box(modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)) {
                    if (chartData.all { it.second == 0f }) {
                        Text(
                            "Нет данных за этот период",
                            modifier = Modifier.align(Alignment.Center),
                            color = Color.Gray
                        )
                    } else {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val bottomPadding = 20.dp.toPx()
                            val w = size.width
                            val h = size.height - bottomPadding

                            val count = chartData.size
                            val spacing = w / (count * 4f)
                            val barWidth = (w - (spacing * (count - 1))) / count

                            chartData.forEachIndexed { index, dataPoint ->
                                val date = dataPoint.first
                                val moodValue = dataPoint.second // 0..3

                                if (moodValue > 0) {
                                    val barHeight = (moodValue / 3f) * h
                                    val x = index * (barWidth + spacing)
                                    val y = h - barHeight

                                    // === ЛОГИКА ВЫБОРА ЦВЕТА ===
                                    // 1 = Плохо (Красный/Розовый)
                                    // 2 = Нормально (Желтый/Оранжевый)
                                    // 3 = Хорошо (Зеленый)
                                    val dynamicColor = when {
                                        moodValue <= 1.5f -> Color(0xFFEF5350) // Красный (Bad)
                                        moodValue <= 2.5f -> Color(0xFFFFCA28) // Желтый (Normal)
                                        else -> Color(0xFF66BB6A)              // Зеленый (Good)
                                    }

                                    drawRoundRect(
                                        color = dynamicColor,
                                        topLeft = Offset(x, y),
                                        size = Size(barWidth, barHeight),
                                        cornerRadius = CornerRadius(6.dp.toPx())
                                    )
                                }

                                // Подписи дат
                                val shouldDrawText = if (isMonthSelected) {
                                    index == 0 || index == count - 1 || index % 5 == 0
                                } else {
                                    true
                                }

                                if (shouldDrawText) {
                                    val dateStr = "${date.dayOfMonth}.${date.monthNumber.toString().padStart(2,'0')}"
                                    val textLayout = textMeasurer.measure(
                                        text = dateStr,
                                        style = TextStyle(color = Color.Gray, fontSize = 10.sp)
                                    )

                                    val xPos = (index * (barWidth + spacing)) + (barWidth / 2) - (textLayout.size.width / 2)

                                    drawText(
                                        textLayoutResult = textLayout,
                                        topLeft = Offset(xPos, h + 4.dp.toPx())
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Легенда цветов (подсказка)
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                LegendItem(color = Color(0xFFEF5350), text = "Плохо")
                LegendItem(color = Color(0xFFFFCA28), text = "Норм")
                LegendItem(color = Color(0xFF66BB6A), text = "Отлично")
            }
        }
    }
}

@Composable
fun LegendItem(color: Color, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier
            .size(12.dp)
            .background(color, MaterialTheme.shapes.small))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = text, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
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
        DateSelector(selectedDate = selectedDate, onOpenDatePicker = onOpenDatePicker)
        Spacer(modifier = Modifier.height(8.dp))
        MoodSelector(selectedMood = selectedMood, onMoodSelected = onMoodSelected)
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
        Box(modifier = modifier
            .fillMaxWidth()
            .padding(top = 20.dp), contentAlignment = Alignment.Center) {
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
            items(moodList, key = { it.id }) { mood ->
                MoodItem(humanMood = mood, onDelete = { onDelete(mood) })
            }
        }
    }
}