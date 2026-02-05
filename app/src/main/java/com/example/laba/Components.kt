package com.example.laba

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDate

@Composable
fun MoodSelector(
    selectedMood: Mood?,
    onMoodSelected: (Mood) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        MoodButton(
            mood = Mood.Bad,
            isSelected = selectedMood == Mood.Bad,
            onClick = { onMoodSelected(Mood.Bad) },
            emoji = "\uD83D\uDE41",
            label = "Плохо",
            modifier = Modifier.weight(1f)
        )

        MoodButton(
            mood = Mood.Normal,
            isSelected = selectedMood == Mood.Normal,
            onClick = { onMoodSelected(Mood.Normal) },
            emoji = "\uD83D\uDE10",
            label = "Нормально",
            modifier = Modifier.weight(1f)
        )

        MoodButton(
            mood = Mood.Good,
            isSelected = selectedMood == Mood.Good,
            onClick = { onMoodSelected(Mood.Good) },
            emoji = "\uD83D\uDE04",
            label = "Хорошо",
            modifier = Modifier.weight(1f)
        )

    }
}

@Composable
fun MoodButton(
    mood: Mood,
    isSelected: Boolean,
    onClick: () -> Unit,
    emoji: String,
    label: String,
    modifier: Modifier = Modifier
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
        modifier = modifier
            .heightIn(min = 70.dp)
            .padding(4.dp),
        shape = MaterialTheme.shapes.medium,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
        ) {
            Text(
                text = emoji,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .size(size = 40.dp)
                    .wrapContentSize(Alignment.Center),
                textAlign = TextAlign.Center
            )
            //Spacer(modifier = Modifier.height(1.dp))
            Text(
                label,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1
            )
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
