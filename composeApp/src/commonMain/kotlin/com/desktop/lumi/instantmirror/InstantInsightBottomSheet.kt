package com.desktop.lumi.instantmirror

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Bottom sheet for Instant Mirror insights.
 * Appears after logging an interaction.
 */

private val SoftLavender = Color(0xFFEDE6F7)  // Light purple
private val SoftPink = Color(0xFFFFEAF3)      // Light pink
private val SoftBlue = Color(0xFFE5F0FF)      // Light blue

@Composable
fun InstantInsightBottomSheet(
    insight: InstantInsight?,
    onDismiss: () -> Unit
) {
    AnimatedVisibility(
        visible = insight != null,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        if (insight != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.4f))
                    .clickable(onClick = onDismiss),
                contentAlignment = Alignment.BottomCenter
            ) {
                InsightCard(
                    insight = insight,
                    onDismiss = onDismiss
                )
            }
        }
    }
}

@Composable
private fun InsightCard(
    insight: InstantInsight,
    onDismiss: () -> Unit
) {
    val bgColor = when (insight) {
        is InstantInsight.NegativePattern -> SoftPink
        is InstantInsight.PositivePattern -> SoftBlue
        is InstantInsight.FirstTime -> SoftLavender
        is InstantInsight.ContrastPattern -> SoftLavender
        is InstantInsight.TimePattern -> SoftLavender
        is InstantInsight.StabilityPattern -> SoftBlue
        is InstantInsight.GentleSupport -> SoftLavender
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = bgColor,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            )
            .padding(horizontal = 24.dp, vertical = 28.dp)
            .navigationBarsPadding()
    ) {
        // TITLE
        Text(
            text = insight.title,
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // MESSAGE
        Text(
            text = insight.message,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // DISMISS BUTTON
        Button(
            onClick = onDismiss,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            )
        ) {
            Text("Okay", fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
@Preview
fun InstantInsightPreview() {
    InstantInsightBottomSheet(InstantInsight.FirstTime(message = "Hi"), {})
}