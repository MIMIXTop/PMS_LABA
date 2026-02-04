package com.example.laba

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDate

@Composable
fun MoodItem(
    humanMood: HumanMood,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = moodToColor(humanMood.mood).copy(alpha = 0.08f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Цветовая индикация настроения
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(MaterialTheme.shapes.small)
                    .background(moodToColor(humanMood.mood))
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Основное содержимое
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Дата
                Text(
                    text = formatDate(humanMood.date),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Комментарий
                Text(
                    text = humanMood.comment.ifEmpty { "Без комментария" },
                    style = MaterialTheme.typography.bodyLarge,
                    fontStyle = if (humanMood.comment.isEmpty()) FontStyle.Italic else FontStyle.Normal,
                    color = if (humanMood.comment.isEmpty())
                        MaterialTheme.colorScheme.onSurfaceVariant
                    else
                        MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Настроение (текстом)
                Text(
                    text = moodToText(humanMood.mood),
                    style = MaterialTheme.typography.labelSmall,
                    color = moodToColor(humanMood.mood)
                )
            }

            // Кнопка удаления
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Удалить запись",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

// Вспомогательные функции
@Composable
fun moodToColor(mood: Mood): Color = when (mood) {
    Mood.Bad -> MaterialTheme.colorScheme.error
    Mood.Normal -> MaterialTheme.colorScheme.secondary
    Mood.Good -> MaterialTheme.colorScheme.tertiary
}

fun moodToText(mood: Mood): String = when (mood) {
    Mood.Bad -> "Плохое настроение"
    Mood.Normal -> "Нормальное настроение"
    Mood.Good -> "Хорошее настроение"
}

fun formatDate(date: LocalDate): String {
    val monthName = when (date.monthNumber) {
        1 -> "января"
        2 -> "февраля"
        3 -> "марта"
        4 -> "апреля"
        5 -> "мая"
        6 -> "июня"
        7 -> "июля"
        8 -> "августа"
        9 -> "сентября"
        10 -> "октября"
        11 -> "ноября"
        12 -> "декабря"
        else -> ""
    }
    return "${date.dayOfMonth} $monthName ${date.year} г."
}