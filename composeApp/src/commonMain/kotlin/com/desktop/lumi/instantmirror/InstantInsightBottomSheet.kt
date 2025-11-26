package com.desktop.lumi.instantmirror

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccessTime
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.HeartBroken
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material.icons.rounded.Timeline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.ui.tooling.preview.Preview

// --- Lumi Palette ---
private val LumiPrimary = Color(0xFF8E8CD8)
private val TextPrimary = Color(0xFF2D2D39)
private val TextSecondary = Color(0xFF5A5A66)

@Composable
fun InstantInsightBottomSheet(
    insight: InstantInsight?,
    onDismiss: () -> Unit
) {
    // Backdrop Fade Animation
    AnimatedVisibility(
        visible = insight != null,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.6f))
                // Consume clicks so they don't pass through
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onDismiss() }
        )
    }

    // Sheet Slide Animation
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        AnimatedVisibility(
            visible = insight != null,
            enter = slideInVertically(
                initialOffsetY = { it }, // Start fully below screen
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessMediumLow
                )
            ),
            exit = slideOutVertically(
                targetOffsetY = { it },
                animationSpec = spring(stiffness = Spring.StiffnessMedium)
            )
        ) {
            if (insight != null) {
                InsightCardContent(
                    insight = insight,
                    onDismiss = onDismiss
                )
            }
        }
    }
}

@Composable
private fun InsightCardContent(
    insight: InstantInsight,
    onDismiss: () -> Unit
) {
    val theme = getInsightTheme(insight)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp) // Float slightly above bottom
            .padding(bottom = 16.dp) // Extra padding for navigation bar
            .shadow(
                elevation = 24.dp,
                shape = RoundedCornerShape(32.dp),
                spotColor = theme.accentColor.copy(alpha = 0.5f)
            )
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.White, theme.backgroundColor)
                ),
                shape = RoundedCornerShape(32.dp)
            )
            .border(
                width = 1.dp,
                color = theme.accentColor.copy(alpha = 0.2f),
                shape = RoundedCornerShape(32.dp)
            )
            .clickable(enabled = false) {} // Prevent click-through on the card itself
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // 1. Drag Handle / Top Indicator
        Box(
            modifier = Modifier
                .width(48.dp)
                .height(4.dp)
                .background(Color.Gray.copy(alpha = 0.2f), RoundedCornerShape(2.dp))
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 2. Hero Icon
        Box(
            modifier = Modifier
                .size(72.dp)
                .background(theme.accentColor.copy(alpha = 0.15f), CircleShape)
                .border(1.dp, theme.accentColor.copy(alpha = 0.3f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = theme.icon,
                contentDescription = null,
                tint = theme.accentColor,
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 3. Title
        Text(
            text = insight.title,
            style = MaterialTheme.typography.headlineSmall.copy(
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold
            ),
            color = TextPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 4. Message
        Text(
            text = insight.message,
            style = MaterialTheme.typography.bodyLarge.copy(
                lineHeight = 24.sp
            ),
            color = TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 5. Action Button
        Button(
            onClick = onDismiss,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = theme.accentColor,
                contentColor = Color.White
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 4.dp,
                pressedElevation = 1.dp
            )
        ) {
            Text(
                text = "Got it",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// --- Theme Helper ---

private data class InsightTheme(
    val backgroundColor: Color,
    val accentColor: Color,
    val icon: ImageVector
)

private fun getInsightTheme(insight: InstantInsight): InsightTheme {
    return when (insight) {
        is InstantInsight.NegativePattern -> InsightTheme(
            backgroundColor = Color(0xFFFFEEEE),
            accentColor = Color(0xFFFF6B6B), // Soft Red
            icon = Icons.Rounded.HeartBroken
        )
        is InstantInsight.PositivePattern -> InsightTheme(
            backgroundColor = Color(0xFFF0F9FF),
            accentColor = Color(0xFF4DABF7), // Soft Blue
            icon = Icons.Rounded.AutoAwesome
        )
        is InstantInsight.FirstTime -> InsightTheme(
            backgroundColor = Color(0xFFF3F0FF),
            accentColor = LumiPrimary, // Lavender
            icon = Icons.Rounded.Lightbulb
        )
        is InstantInsight.ContrastPattern -> InsightTheme(
            backgroundColor = Color(0xFFFFF9DB),
            accentColor = Color(0xFFFFD43B), // Yellow/Gold
            icon = Icons.Rounded.Timeline
        )
        is InstantInsight.TimePattern -> InsightTheme(
            backgroundColor = Color(0xFFF8F0FC),
            accentColor = Color(0xFFBE4BDB), // Grape
            icon = Icons.Rounded.AccessTime
        )
        is InstantInsight.StabilityPattern -> InsightTheme(
            backgroundColor = Color(0xFFE6FCF5),
            accentColor = Color(0xFF20C997), // Teal
            icon = Icons.Rounded.CheckCircle
        )
        is InstantInsight.GentleSupport -> InsightTheme(
            backgroundColor = Color(0xFFFFF0F6),
            accentColor = Color(0xFFFAA2C1), // Pink
            icon = Icons.Rounded.Favorite
        )
    }
}


@Preview
@Composable
fun PreviewNegativeInsight() {
    MaterialTheme {
        InstantInsightBottomSheet(
            insight = InstantInsight.NegativePattern(
                "Noticed a Pattern?",
                "That's the 3rd time texting has left you feeling anxious. Maybe try a call next time?"
            ),
            onDismiss = {}
        )
    }
}

@Preview
@Composable
fun PreviewPositiveInsight() {
    MaterialTheme {
        InstantInsightBottomSheet(
            insight = InstantInsight.StabilityPattern(
                "Noticed a Pattern?",
                "You've been feeling consistently good for 3 days straight. Keep this momentum!"
            ),
            onDismiss = {}
        )
    }
}