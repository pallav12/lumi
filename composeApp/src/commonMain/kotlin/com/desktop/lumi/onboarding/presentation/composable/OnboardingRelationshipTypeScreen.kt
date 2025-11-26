package com.desktop.lumi.onboarding.presentation.composable

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.desktop.lumi.onboarding.presentation.model.RelationshipType
import org.jetbrains.compose.ui.tooling.preview.Preview

// Consistent Lumi Palette
private val LumiBackground = Color(0xFFFAFAFA)
private val LumiPrimary = Color(0xFF8E8CD8)
private val LumiSurface = Color(0xFFFFFFFF)
private val TextPrimary = Color(0xFF2D2D39)
private val TextSecondary = Color(0xFF8A8A99)

@Composable
fun OnboardingRelationshipTypeScreen(
    selectedType: RelationshipType?,
    onSelectType: (RelationshipType) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        containerColor = LumiBackground,
        topBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                LinearProgressIndicator(
                    progress = { 0.66f }, // Step 2 of 3
                    modifier = Modifier.fillMaxWidth(),
                    color = LumiPrimary,
                    trackColor = LumiPrimary.copy(alpha = 0.1f),
                )
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back",
                            tint = TextSecondary
                        )
                    }
                }
            }
        },
        bottomBar = {
            // Floating Next Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .padding(bottom = 16.dp)
            ) {
                Button(
                    onClick = onNext,
                    enabled = selectedType != null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LumiPrimary,
                        disabledContainerColor = Color.Gray.copy(alpha = 0.1f),
                        contentColor = Color.White,
                        disabledContentColor = TextSecondary.copy(alpha = 0.4f)
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 0.dp,
                        pressedElevation = 2.dp
                    )
                ) {
                    Text(
                        text = "Continue",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    if (selectedType != null) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowForward, contentDescription = null)
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(16.dp))

            // 1. The Header
            Text(
                text = "How would you describe this bond?",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 40.sp
                ),
                color = TextPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "No labels? No problem. Just pick what feels closest so we can tailor the insights.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))

            // 2. The Grid Selection
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(RelationshipType.entries) { type ->
                    RelationshipTypeCard(
                        type = type,
                        isSelected = selectedType == type,
                        onSelect = onSelectType
                    )
                }
            }
        }
    }
}

@Composable
private fun RelationshipTypeCard(
    type: RelationshipType,
    isSelected: Boolean,
    onSelect: (RelationshipType) -> Unit
) {
    // Animations for selection
    val backgroundColor by animateColorAsState(
        if (isSelected) LumiPrimary.copy(alpha = 0.1f) else LumiSurface
    )
    val borderColor by animateColorAsState(
        if (isSelected) LumiPrimary else Color.Transparent
    )
    val scale by animateFloatAsState(
        if (isSelected) 1.02f else 1f
    )
    val elevation by animateDpAsState(
        if (isSelected) 4.dp else 1.dp
    )

    // Data Mapping for Visuals
    val (emoji, label) = when (type) {
        RelationshipType.Dating -> "🥂" to "Dating"
        RelationshipType.Situationship -> "🌀" to "Situationship"
        RelationshipType.Partner -> "🏡" to "Partner"
        RelationshipType.LongDistance -> "✈️" to "Long Distance"
    }

    Card(
        modifier = Modifier
            .aspectRatio(1f) // Square cards
            .scale(scale)
            .clickable { onSelect(type) },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(elevation),
        border = androidx.compose.foundation.BorderStroke(2.dp, borderColor)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = emoji,
                fontSize = 48.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) LumiPrimary else TextPrimary
            )
        }
    }
}

@Preview
@Composable
fun PreviewRelationshipTypeScreen() {
    MaterialTheme {
        OnboardingRelationshipTypeScreen(
            selectedType = RelationshipType.Situationship,
            onSelectType = {},
            onNext = {},
            onBack = {}
        )
    }
}