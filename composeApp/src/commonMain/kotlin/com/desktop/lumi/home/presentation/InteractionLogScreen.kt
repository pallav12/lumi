package com.desktop.lumi.home.presentation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.ChatBubble
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.ui.tooling.preview.Preview

// Models
enum class InteractionType { Text, Call, Meet }
enum class MoodEffect { Better, Same, Worse }

// Consistently using the new palette
private val LumiBackground = Color(0xFFFAFAFA)
private val LumiSurface = Color(0xFFFFFFFF)
private val LumiPrimary = Color(0xFF8E8CD8)
private val TextPrimary = Color(0xFF2D2D39)
private val TextSecondary = Color(0xFF8A8A99)

// Mood Specific Colors
private val ColorBetter = Color(0xFF98D8AA) // Soft Green
private val ColorSame = Color(0xFFE2E2E2)   // Neutral Gray
private val ColorWorse = Color(0xFFFF9E9E)  // Soft Red

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InteractionLogScreen(
    selectedType: InteractionType?,
    selectedMoodEffect: MoodEffect?,
    onSelectType: (InteractionType) -> Unit,
    onSelectMoodEffect: (MoodEffect) -> Unit,
    onSave: (InteractionType?) -> Unit,
    onBack: () -> Unit
) {
    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = LumiBackground,
        topBar = {
            TopAppBar(
                title = {},
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

            Spacer(modifier = Modifier.height(16.dp))

            // 1. Title
            Text(
                text = "Log Interaction",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold
                ),
                color = TextPrimary
            )
            Text(
                text = "Track the moments that matter.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(40.dp))

            // 2. What happened? (Icons Grid)
            Text(
                text = "What happened?",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            InteractionTypeGrid(
                selectedType = selectedType,
                onSelect = onSelectType
            )

            Spacer(modifier = Modifier.height(48.dp))

            // 3. How did it feel? (Pills)
            // Only show this section if type is selected for better flow (optional, but cleaner)
            if (selectedType != null) {
                Text(
                    text = "How do you feel now?",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                MoodEffectRow(
                    selectedEffect = selectedMoodEffect,
                    onSelect = onSelectMoodEffect
                )

                Spacer(modifier = Modifier.height(48.dp))
            }

            // 4. Save Button
            // Pushed to bottom or shown when ready
            SaveButton(
                enabled = selectedType != null && selectedMoodEffect != null,
                onSave = {onSave(selectedType)}
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun InteractionTypeGrid(
    selectedType: InteractionType?,
    onSelect: (InteractionType) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        InteractionTypeItem(
            type = InteractionType.Text,
            icon = Icons.Rounded.ChatBubble,
            label = "Text",
            isSelected = selectedType == InteractionType.Text,
            onSelect = onSelect
        )
        InteractionTypeItem(
            type = InteractionType.Call,
            icon = Icons.Rounded.Call,
            label = "Call",
            isSelected = selectedType == InteractionType.Call,
            onSelect = onSelect
        )
        InteractionTypeItem(
            type = InteractionType.Meet,
            icon = Icons.Rounded.Groups,
            label = "Meet",
            isSelected = selectedType == InteractionType.Meet,
            onSelect = onSelect
        )
    }
}

@Composable
private fun InteractionTypeItem(
    type: InteractionType,
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onSelect: (InteractionType) -> Unit
) {
    val backgroundColor by animateColorAsState(
        if (isSelected) LumiPrimary else LumiSurface
    )
    val contentColor by animateColorAsState(
        if (isSelected) Color.White else TextPrimary
    )
    val elevation by animateDpAsState(
        if (isSelected) 8.dp else 2.dp
    )
    val scale by animateFloatAsState(
        if (isSelected) 1.05f else 1f
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Card(
            modifier = Modifier
                .size(80.dp)
                .scale(scale)
                .clickable { onSelect(type) },
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = backgroundColor),
            elevation = CardDefaults.cardElevation(elevation)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = contentColor,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) LumiPrimary else TextSecondary
        )
    }
}

@Composable
private fun MoodEffectRow(
    selectedEffect: MoodEffect?,
    onSelect: (MoodEffect) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        MoodEffectItem(
            effect = MoodEffect.Better,
            label = "Better / Relieved",
            color = ColorBetter,
            isSelected = selectedEffect == MoodEffect.Better,
            onSelect = onSelect
        )
        MoodEffectItem(
            effect = MoodEffect.Same,
            label = "Same / Neutral",
            color = ColorSame,
            isSelected = selectedEffect == MoodEffect.Same,
            onSelect = onSelect
        )
        MoodEffectItem(
            effect = MoodEffect.Worse,
            label = "Worse / Anxious",
            color = ColorWorse,
            isSelected = selectedEffect == MoodEffect.Worse,
            onSelect = onSelect
        )
    }
}

@Composable
private fun MoodEffectItem(
    effect: MoodEffect,
    label: String,
    color: Color,
    isSelected: Boolean,
    onSelect: (MoodEffect) -> Unit
) {
    val alpha by animateFloatAsState(if (isSelected) 1f else 0.3f)
    val borderWidth by animateDpAsState(if (isSelected) 2.dp else 0.dp)

    // Custom Pill Design
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(color.copy(alpha = 0.15f)) // Always show subtle background
            .border(
                width = borderWidth,
                color = if (isSelected) color else Color.Transparent,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onSelect(effect) }
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = TextPrimary.copy(alpha = if (isSelected) 1f else 0.7f)
            )

            // Checkmark indicator
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) color else Color.Transparent)
                    .border(1.dp, if (isSelected) Color.Transparent else TextSecondary.copy(0.3f), CircleShape)
            )
        }
    }
}

@Composable
private fun SaveButton(
    enabled: Boolean,
    onSave: () -> Unit
) {
    val containerColor = if (enabled) LumiPrimary else Color.Gray.copy(alpha = 0.2f)
    val contentColor = if (enabled) Color.White else TextSecondary

    Button(
        onClick = onSave,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = Color.Gray.copy(alpha = 0.1f),
            disabledContentColor = TextSecondary.copy(alpha = 0.5f)
        ),
        elevation = ButtonDefaults.buttonElevation(0.dp)
    ) {
        Text(
            text = "Save Interaction",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview
@Composable
fun PreviewInteractionNew() {
    MaterialTheme {
        InteractionLogScreen(
            selectedType = InteractionType.Text,
            selectedMoodEffect = null,
            onSelectType = {},
            onSelectMoodEffect = {},
            onSave = {},
            onBack = {}
        )
    }
}