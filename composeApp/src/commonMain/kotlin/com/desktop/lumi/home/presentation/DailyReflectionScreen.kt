package com.desktop.lumi.home.presentation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.ui.tooling.preview.Preview

// --- Consistently using the new palette ---
private val LumiBackground = Color(0xFFFAFAFA)
private val LumiSurface = Color(0xFFFFFFFF)
private val LumiPrimary = Color(0xFF8E8CD8)
private val LumiSecondary = Color(0xFFFFB7B2)
private val TextPrimary = Color(0xFF2D2D39)
private val TextSecondary = Color(0xFF8A8A99)
private val InputBackground = Color(0xFFF2F2F7) // Softer gray for inputs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyReflectionScreen(
    mood: Int?, // null = not selected yet
    note: String,
    onMoodSelected: (Int) -> Unit,
    onNoteChange: (String) -> Unit,
    onSave: () -> Unit,
    onBack: () -> Unit
) {
    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = LumiBackground,
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = LumiBackground)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(padding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(8.dp))

            // 1. The Big Question
            Text(
                text = "How did today feel?",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold
                ),
                color = TextPrimary,
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "Be honest with yourself.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(40.dp))

            // 2. The Mood "Stage"
            // We center the mood selection and make it the hero
            MoodSelector(
                selectedMood = mood,
                onMoodSelected = onMoodSelected
            )

            Spacer(modifier = Modifier.height(48.dp))

            // 3. The Journal Area
            JournalInput(
                note = note,
                onNoteChange = onNoteChange
            )

            Spacer(modifier = Modifier.height(40.dp))

            // 4. The Action
            // We push this to the bottom of the content flow
            // Require note if mood is neutral (3), otherwise just require mood to be selected
            SaveButton(
                enabled = mood != null && (mood != 3 || note.isNotEmpty()),
                onSave = onSave
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun MoodSelector(
    selectedMood: Int?,
    onMoodSelected: (Int) -> Unit
) {
    val moods = listOf(
        1 to "😫", // Drained
        2 to "😕", // Confused
        3 to "😐", // Neutral
        4 to "🙂", // Good
        5 to "🥰"  // Loved
    )

    // Using a BoxWithConstraints or just Row to distribute
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        moods.forEach { (value, emoji) ->
            MoodItem(
                emoji = emoji,
                isSelected = selectedMood == value,
                isDimmed = selectedMood != null && selectedMood != value,
                onClick = { onMoodSelected(value) }
            )
        }
    }

    // Label under the selected mood (Optional context)
    Box(modifier = Modifier.height(30.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
        if (selectedMood != null) {
            val label = when(selectedMood) {
                1 -> "Drained"
                2 -> "Confused"
                3 -> "Okay"
                4 -> "Good"
                5 -> "Loved"
                else -> ""
            }
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = LumiPrimary
            )
        }
    }
}

@Composable
private fun MoodItem(
    emoji: String,
    isSelected: Boolean,
    isDimmed: Boolean,
    onClick: () -> Unit
) {
    // Spring animations for bouncy feel
    val size by animateDpAsState(
        targetValue = if (isSelected) 64.dp else 48.dp,
        animationSpec = spring(dampingRatio = 0.6f)
    )
    val fontSize by animateFloatAsState(
        targetValue = if (isSelected) 32f else 20f
    )
    val alpha by animateFloatAsState(
        targetValue = if (isDimmed) 0.3f else 1f
    )
    val backgroundAlpha by animateFloatAsState(
        targetValue = if (isSelected) 0.2f else 0f
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(size)
                .scale(scale = if (isSelected) 1.1f else 1f)
                .shadow(
                    elevation = if (isSelected) 8.dp else 0.dp,
                    shape = CircleShape,
                    spotColor = LumiPrimary.copy(alpha = 0.5f)
                )
                .background(
                    color = if (isSelected) LumiSurface else Color.Transparent,
                    shape = CircleShape
                )
                // The ring effect
                .border(
                    width = if (isSelected) 2.dp else 0.dp,
                    color = if (isSelected) LumiPrimary else Color.Transparent,
                    shape = CircleShape
                )
                .clip(CircleShape)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null // Disable default ripple for custom feel
                ) { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = emoji,
                fontSize = fontSize.sp,
                color = Color.Unspecified.copy(alpha = alpha)
            )
        }
    }
}

@Composable
private fun JournalInput(
    note: String,
    onNoteChange: (String) -> Unit
) {
    Column {
        Text(
            text = "Journal (Optional)",
            style = MaterialTheme.typography.labelLarge,
            color = TextSecondary,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(12.dp))

        // A "Paper" like card for typing
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = InputBackground),
            elevation = CardDefaults.cardElevation(0.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            TextField(
                value = note,
                onValueChange = onNoteChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .padding(4.dp),
                placeholder = {
                    Text(
                        "What happened today? Vent here...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary.copy(alpha = 0.7f)
                    )
                },
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = TextPrimary),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
        }
    }
}

@Composable
private fun SaveButton(
    enabled: Boolean,
    onSave: () -> Unit
) {
    // Gradient button for premium feel
    val brush = Brush.horizontalGradient(
        colors = listOf(LumiPrimary, Color(0xFFA5A3E6))
    )

    val alphaa by animateFloatAsState(targetValue = if (enabled) 1f else 0.5f)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .shadow(
                elevation = if (enabled) 8.dp else 0.dp,
                shape = RoundedCornerShape(28.dp),
                spotColor = LumiPrimary.copy(alpha = 0.4f)
            )
            .clip(RoundedCornerShape(28.dp))
            .background(brush = brush) // Only apply alpha to color, not structure? Actually simpler to just use button colors if solid
            .background(if (enabled) LumiPrimary else Color.Gray.copy(alpha = 0.2f)) // Fallback logic
            .clickable(enabled = enabled, onClick = onSave),
        contentAlignment = Alignment.Center
    ) {
        // We use a Box instead of Button to control the gradient background easily
        // But for accessibility, Button is better. Let's wrap content.

        Text(
            text = "Save Entry",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White.copy(alpha = if (enabled) 1f else 0.7f)
        )
    }
}

@Preview
@Composable
fun PreviewNewReflection() {
    MaterialTheme {
        DailyReflectionScreen(
            mood = 4,
            note = "I felt pretty good actually...",
            onMoodSelected = {},
            onNoteChange = {},
            onSave = {},
            onBack = {}
        )
    }
}