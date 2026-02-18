// StatisticsActivity.kt
package com.example.laba

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBar
import com.example.laba.ui.theme.LABATheme

@OptIn(ExperimentalMaterial3Api::class)
class StatisticsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LABATheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("Статистика настроения") },
                            navigationIcon = {
                                IconButton(onClick = {finish()}) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Назад"
                                    )
                                }
                            }
                        )
                    }
                ) { padding ->
                    Column(Modifier.padding(padding).padding(16.dp)) {
                        Text("График за последние 7 записей", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(20.dp))
                        MoodChart()
                        Spacer(Modifier.height(40.dp))
                        Button(onClick = { finish() }) {
                            Text("Вернуться назад")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MoodChart() {
    // Демо-данные (в реальном приложении брать из ViewModel)
    val data = listOf(3, 5, 2, 4, 5, 1, 4) // Уровни счастья от 1 до 5

    Canvas(modifier = Modifier
        .fillMaxWidth()
        .height(200.dp)
    ) {
        val width = size.width
        val height = size.height
        val spacing = width / (data.size - 1)
        val maxVal = 5f

        // Рисуем линии графика
        for (i in 0 until data.size - 1) {
            val x1 = i * spacing
            val y1 = height - (data[i] / maxVal * height)
            val x2 = (i + 1) * spacing
            val y2 = height - (data[i + 1] / maxVal * height)

            drawLine(
                color = Color.Magenta,
                start = Offset(x1, y1),
                end = Offset(x2, y2),
                strokeWidth = 5f
            )

            drawCircle(
                color = Color.Blue,
                center = Offset(x1, y1),
                radius = 8f
            )
        }
        // Последняя точка
        drawCircle(
            color = Color.Blue,
            center = Offset(width, height - (data.last() / maxVal * height)),
            radius = 8f
        )
    }
}