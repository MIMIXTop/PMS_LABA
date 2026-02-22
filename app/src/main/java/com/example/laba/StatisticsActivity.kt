package com.example.laba

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.example.laba.ui.theme.LABATheme
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.todayIn

data class MoodRecord(val date: LocalDate, val moodType: Int)

class StatisticsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mood = intent.getIntArrayExtra("MOOD_DATA") ?: IntArray(0)
        val date = intent.getIntArrayExtra("DATE_DATA") ?: IntArray(0)

        val notificationHelper = NotificationHelper(this)

        setContent {
            LABATheme {
                ChartActivity(mood, date, {
                    finish()
                })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChartActivity(
    moodArray: IntArray,
    dateArray: IntArray,
    onBack: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val records = moodArray.zip(dateArray.toTypedArray()) { mood, dateInt ->
        MoodRecord(
            date = LocalDate.fromEpochDays(dateInt),
            moodType = mood
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Статистика настроения") },
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                }
            )
        }
    ) { padding ->

        if (isLandscape) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                MoodChart(
                    data = records,
                    modifier = Modifier
                        .weight(2f)
                        .fillMaxHeight()
                )
            }

        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                Spacer(Modifier.height(20.dp))
                MoodChart(data = records)
                Spacer(Modifier.height(40.dp))
            }
        }
    }
}


@Composable
fun MoodChart(
    data: List<MoodRecord>,
    modifier: Modifier = Modifier
) {
    val modelProducer = remember { CartesianChartModelProducer() }
    var isMonths by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth()) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = !isMonths,
                onClick = { isMonths = false },
                label = { Text("За последние 7 дней") }
            )
            FilterChip(
                selected = isMonths,
                onClick = { isMonths = true },
                label = { Text("За последний месяц") }
            )
        }

        val counts = remember(data, isMonths) {
            val calculatedCounts = IntArray(3) { 0 }

            val today = Clock.System.todayIn(TimeZone.currentSystemDefault())

            val startDate = if (isMonths) {
                today.minus(DatePeriod(months = 1))
            } else {
                today.minus(DatePeriod(days = 7))
            }

            for (record in data) {
                if (record.date in startDate..today) {
                    when (record.moodType) {
                        1 -> calculatedCounts[0]++
                        2 -> calculatedCounts[1]++
                        3 -> calculatedCounts[2]++
                    }
                }
            }

            calculatedCounts.toList()
        }

        LaunchedEffect(counts) {
            modelProducer.runTransaction {
                columnSeries {
                    series(x = listOf(0), y = listOf(counts[0]))
                    series(x = listOf(1), y = listOf(counts[1]))
                    series(x = listOf(2), y = listOf(counts[2]))
                }
            }
        }

        CartesianChartHost(
            chart = rememberCartesianChart(
                rememberColumnCartesianLayer(),
                startAxis = VerticalAxis.rememberStart(),
                bottomAxis = HorizontalAxis.rememberBottom(
                    valueFormatter = { _, value, _ ->
                        when (value.toInt()) {
                            0 -> "Плохое"
                            1 -> "Нормальное"
                            2 -> "Хорошее"
                            else -> ""
                        }
                    }
                ),
            ),
            modelProducer = modelProducer,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        )
    }
}