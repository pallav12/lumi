package com.desktop.lumi.onboarding.presentation.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.desktop.lumi.onboarding.presentation.model.RelationshipType
import org.jetbrains.compose.ui.tooling.preview.Preview

private val SoftPink = Color(0xFFFFE5F1) // Very light pink
private val SoftBlue = Color(0xFFE5F0FF) // Very light blue
private val PrimarySoft = Color(0xFFB8A4D9) // Soft lavender/pastel purple

@Composable
fun OnboardingRelationshipTypeScreen(
    selectedType: RelationshipType?,
    onSelectType: (RelationshipType) -> Unit,
    onNext: () -> Unit,
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
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier.align(Alignment.Start)
        ) {
            Text("←", fontSize = 24.sp, color = MaterialTheme.colorScheme.onSurface)
        }

        Spacer(modifier = Modifier.height(16.dp))
        
        // Title
        Text(
            text = "What best describes your relationship?",
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Subtitle
        Text(
            text = "This helps me tailor your reflections.",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF777777),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Relationship type options
        RelationshipTypeOption(
            type = RelationshipType.Dating,
            displayText = "Dating",
            isSelected = selectedType == RelationshipType.Dating,
            onSelect = onSelectType
        )

        Spacer(modifier = Modifier.height(12.dp))

        RelationshipTypeOption(
            type = RelationshipType.Situationship,
            displayText = "Situationship",
            isSelected = selectedType == RelationshipType.Situationship,
            onSelect = onSelectType
        )

        Spacer(modifier = Modifier.height(12.dp))

        RelationshipTypeOption(
            type = RelationshipType.Partner,
            displayText = "Partner",
            isSelected = selectedType == RelationshipType.Partner,
            onSelect = onSelectType
        )

        Spacer(modifier = Modifier.height(12.dp))

        RelationshipTypeOption(
            type = RelationshipType.LongDistance,
            displayText = "Long-distance",
            isSelected = selectedType == RelationshipType.LongDistance,
            onSelect = onSelectType
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Next Button
        Button(
            onClick = onNext,
            enabled = selectedType != null,
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
                text = "Next",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }

        // Bottom padding for scroll
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun RelationshipTypeOption(
    type: RelationshipType,
    displayText: String,
    isSelected: Boolean,
    onSelect: (RelationshipType) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(
                color = if (isSelected) {
                    PrimarySoft.copy(alpha = 0.12f)
                } else {
                    SoftPink.copy(alpha = 0.3f)
                },
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                width = if (isSelected) 2.dp else 0.dp,
                color = if (isSelected) PrimarySoft else Color.Transparent,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onSelect(type) }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = displayText,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Preview
@Composable
fun PreviewOnboardingRelationshipTypeScreen() {
    MaterialTheme {
        OnboardingRelationshipTypeScreen(
            selectedType = null,
            onSelectType = {},
            onNext = {},
            onBack = {}
        )
    }
}
