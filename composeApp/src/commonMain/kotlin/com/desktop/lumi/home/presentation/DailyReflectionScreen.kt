package com.desktop.lumi.home.presentation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.ui.tooling.preview.Preview

private val SoftPink = Color(0xFFFFE5F1) // Very light pink
private val SoftBlue = Color(0xFFE5F0FF) // Very light blue
private val PrimarySoft = Color(0xFFB8A4D9) // Soft lavender/pastel purple
private val TextFieldBackground = Color(0xFFF8F8F8) // Very light gray

@Composable
fun DailyReflectionScreen(
    mood: Int?,                       // null = not selected yet
    note: String,
    onMoodSelected: (Int) -> Unit,
    onNoteChange: (String) -> Unit,
    onSave: () -> Unit,
    onBack: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .verticalScroll(scrollState)
            .padding(horizontal = 24.dp)
            .padding(top = 60.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        // Header Section
        ReflectionHeader(onBack = onBack)

        Spacer(modifier = Modifier.height(32.dp))

        // Mood Selection Section
        MoodSelectionSection(
            selectedMood = mood,
            onMoodSelected = onMoodSelected
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Optional Note Section
        NoteInputSection(
            note = note,
            onNoteChange = onNoteChange
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Save Button
        SaveButton(
            enabled = mood != null,
            onSave = onSave
        )

        // Bottom padding for scroll
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun ReflectionHeader(
    onBack: () -> Unit
) {
    Column {
        // Back button and title row
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = PrimarySoft.copy(alpha = 0.12f),
                        shape = CircleShape
                    )
                    .clickable { onBack() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "←",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = PrimarySoft
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Reflect on Today",
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Subtitle
        Text(
            text = "How did today feel with them?",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF777777),
            modifier = Modifier.padding(start = 48.dp) // Align with title text
        )
    }
}

@Composable
private fun MoodSelectionSection(
    selectedMood: Int?,
    onMoodSelected: (Int) -> Unit
) {
    Column {
        Text(
            text = "How are you feeling?",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Mood emoji row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val moods = listOf(
                1 to "😞",
                2 to "😐",
                3 to "🙂",
                4 to "😊",
                5 to "😍"
            )

            moods.forEach { (moodValue, emoji) ->
                MoodEmojiButton(
                    emoji = emoji,
                    isSelected = selectedMood == moodValue,
                    onClick = { onMoodSelected(moodValue) }
                )
            }
        }
    }
}

@Composable
private fun MoodEmojiButton(
    emoji: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    // Scale animation for selected state
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1.0f,
        animationSpec = tween(durationMillis = 200),
        label = "mood_scale"
    )

    Box(
        modifier = Modifier
            .size(64.dp)
            .scale(scale)
            .background(
                color = if (isSelected) {
                    PrimarySoft.copy(alpha = 0.2f)
                } else {
                    Color.Transparent
                },
                shape = CircleShape
            )
            .border(
                width = if (isSelected) 2.dp else 0.dp,
                color = if (isSelected) PrimarySoft else Color.Transparent,
                shape = CircleShape
            )
            .clip(CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = emoji,
            fontSize = 28.sp,
            color = if (isSelected) {
                Color.Unspecified
            } else {
                Color.Unspecified.copy(alpha = 0.7f)
            }
        )
    }
}

@Composable
private fun NoteInputSection(
    note: String,
    onNoteChange: (String) -> Unit
) {
    Column {
        Text(
            text = "Add a note (optional)",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = note,
            onValueChange = onNoteChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            placeholder = {
                Text(
                    text = "Write how today felt…",
                    fontSize = 14.sp,
                    color = Color(0xFF999999)
                )
            },
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = TextFieldBackground,
                unfocusedContainerColor = TextFieldBackground,
                focusedBorderColor = PrimarySoft.copy(alpha = 0.5f),
                unfocusedBorderColor = Color.Transparent,
                cursorColor = PrimarySoft
            ),
            maxLines = 5,
            singleLine = false
        )
    }
}

@Composable
private fun SaveButton(
    enabled: Boolean,
    onSave: () -> Unit
) {
    Button(
        onClick = onSave,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = PrimarySoft,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
            contentColor = Color.White,
            disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 0.dp,
            pressedElevation = 2.dp,
            disabledElevation = 0.dp
        )
    ) {
        Text(
            text = "Save Reflection",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}



@Preview
@Composable
fun PreviewDailyReflectionScreenEmpty() {
    MaterialTheme {
        DailyReflectionScreen(
            mood = null,
            note = "",
            onMoodSelected = {},
            onNoteChange = {},
            onSave = {},
            onBack = {}
        )
    }
}

@Preview
@Composable
fun PreviewDailyReflectionScreenFilled() {
    MaterialTheme {
        DailyReflectionScreen(
            mood = 4,
            note = "Had a wonderful day together. We went for a walk and talked about our future plans.",
            onMoodSelected = {},
            onNoteChange = {},
            onSave = {},
            onBack = {}
        )
    }
}