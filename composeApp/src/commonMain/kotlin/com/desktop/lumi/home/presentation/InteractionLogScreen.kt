package com.desktop.lumi.home.presentation

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.desktop.lumi.home.presentation.InteractionViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview

// Enums
enum class InteractionType { Text, Call, Meet }
enum class MoodEffect { Better, Same, Worse }

// Color constants
private val SoftPink = Color(0xFFFFE5F1) // Very light pink
private val SoftBlue = Color(0xFFE5F0FF) // Very light blue
private val PrimarySoft = Color(0xFFB8A4D9) // Soft lavender/pastel purple
private val SoftGreen = Color(0xFFE8F5E8) // Very light green
private val SoftRed = Color(0xFFFFE5E5) // Very light red/pink
private val SoftGray = Color(0xFFF0F0F0) // Very light gray

@Composable
fun InteractionLogScreen(
    selectedType: InteractionViewModel.InteractionUiState?,              // TEXT, CALL, MEET or null
    selectedMoodEffect: MoodEffect?,            // BETTER, SAME, WORSE or null
    onSelectType: (InteractionType) -> Unit,
    onSelectMoodEffect: (MoodEffect) -> Unit,
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
        InteractionLogHeader(onBack = onBack)

        Spacer(modifier = Modifier.height(32.dp))

        // Interaction Type Section
        InteractionTypeSection(
            selectedType = selectedType?.type,
            onSelectType = onSelectType
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Mood Effect Section
        MoodEffectSection(
            selectedMoodEffect = selectedMoodEffect,
            onSelectMoodEffect = onSelectMoodEffect
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Save Button
        SaveButton(
            enabled = selectedType != null && selectedMoodEffect != null,
            onSave = onSave
        )

        // Bottom padding for scroll
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun InteractionLogHeader(
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
                text = "Log Interaction",
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Subtitle
        Text(
            text = "What happened, and how did it make you feel?",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF777777),
            modifier = Modifier.padding(start = 48.dp) // Align with title text
        )
    }
}

@Composable
private fun InteractionTypeSection(
    selectedType: InteractionType?,
    onSelectType: (InteractionType) -> Unit
) {
    Column {
        Text(
            text = "What kind of interaction was it?",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Interaction type cards
        InteractionTypeCard(
            type = InteractionType.Text,
            displayText = "Text message",
            isSelected = selectedType == InteractionType.Text,
            onSelect = onSelectType
        )

        Spacer(modifier = Modifier.height(16.dp))

        InteractionTypeCard(
            type = InteractionType.Call,
            displayText = "Call",
            isSelected = selectedType == InteractionType.Call,
            onSelect = onSelectType
        )

        Spacer(modifier = Modifier.height(16.dp))

        InteractionTypeCard(
            type = InteractionType.Meet,
            displayText = "Met in person",
            isSelected = selectedType == InteractionType.Meet,
            onSelect = onSelectType
        )
    }
}

@Composable
private fun InteractionTypeCard(
    type: InteractionType,
    displayText: String,
    isSelected: Boolean,
    onSelect: (InteractionType) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect(type) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                PrimarySoft.copy(alpha = 0.12f)
            } else {
                SoftBlue.copy(alpha = 0.3f)
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 2.dp else 0.dp
        ),
        border = if (isSelected) {
            BorderStroke(
                width = 1.dp,
                color = PrimarySoft.copy(alpha = 0.5f)
            )
        } else null
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = displayText,
                fontSize = 16.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun MoodEffectSection(
    selectedMoodEffect: MoodEffect?,
    onSelectMoodEffect: (MoodEffect) -> Unit
) {
    Column {
        Text(
            text = "How did it make you feel?",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Mood effect chips row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MoodEffectChip(
                effect = MoodEffect.Better,
                displayText = "Better",
                isSelected = selectedMoodEffect == MoodEffect.Better,
                backgroundColor = SoftGreen,
                onSelect = onSelectMoodEffect,
                modifier = Modifier.weight(1f)
            )

            MoodEffectChip(
                effect = MoodEffect.Same,
                displayText = "Same",
                isSelected = selectedMoodEffect == MoodEffect.Same,
                backgroundColor = SoftGray,
                onSelect = onSelectMoodEffect,
                modifier = Modifier.weight(1f)
            )

            MoodEffectChip(
                effect = MoodEffect.Worse,
                displayText = "Worse",
                isSelected = selectedMoodEffect == MoodEffect.Worse,
                backgroundColor = SoftRed,
                onSelect = onSelectMoodEffect,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun MoodEffectChip(
    effect: MoodEffect,
    displayText: String,
    isSelected: Boolean,
    backgroundColor: Color,
    onSelect: (MoodEffect) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(44.dp)
            .background(
                color = if (isSelected) {
                    backgroundColor
                } else {
                    backgroundColor.copy(alpha = 0.3f)
                },
                shape = RoundedCornerShape(22.dp) // 50% radius for pill shape
            )
            .border(
                width = if (isSelected) 1.dp else 0.dp,
                color = if (isSelected) {
                    when (effect) {
                        MoodEffect.Better -> Color(0xFF4CAF50).copy(alpha = 0.5f)
                        MoodEffect.Same -> Color(0xFF9E9E9E).copy(alpha = 0.5f)
                        MoodEffect.Worse -> Color(0xFFE57373).copy(alpha = 0.5f)
                    }
                } else {
                    Color.Transparent
                },
                shape = RoundedCornerShape(22.dp)
            )
            .clip(RoundedCornerShape(22.dp))
            .clickable { onSelect(effect) },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = displayText,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) {
                MaterialTheme.colorScheme.onSurface
            } else {
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            }
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
            text = "Save Entry",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Preview
@Composable
fun PreviewInteractionLogScreenEmpty() {
    MaterialTheme {
        InteractionLogScreen(
            selectedType = null,
            selectedMoodEffect = null,
            onSelectType = {},
            onSelectMoodEffect = {},
            onSave = {},
            onBack = {}
        )
    }
}

@Preview
@Composable
fun PreviewInteractionLogScreenSelected() {
    MaterialTheme {
        InteractionLogScreen(
            selectedType = InteractionViewModel.InteractionUiState(),
            selectedMoodEffect = MoodEffect.Better,
            onSelectType = {},
            onSelectMoodEffect = {},
            onSave = {},
            onBack = {}
        )
    }
}