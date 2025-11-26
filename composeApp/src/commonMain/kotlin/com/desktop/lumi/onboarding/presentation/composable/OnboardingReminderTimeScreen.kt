package com.desktop.lumi.onboarding.presentation.composable

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.ui.tooling.preview.Preview

// Consistent Lumi Palette
private val LumiBackground = Color(0xFFFAFAFA)
private val LumiPrimary = Color(0xFF8E8CD8)
private val LumiSurface = Color(0xFFFFFFFF)
private val TextPrimary = Color(0xFF2D2D39)
private val TextSecondary = Color(0xFF8A8A99)
private val NotificationHighlight = Color(0xFFFFF3CD) // Soft yellow background for highlight
private val NotificationText = Color(0xFF856404) // Darker yellow/brown text

@Composable
fun OnboardingReminderTimeScreen(
    hour: Int,
    minute: Int,
    onTimeChange: (Int, Int) -> Unit,
    onFinish: () -> Unit,
    onBack: () -> Unit
) {
    // Determine context (Morning/Night)
    val isNight = hour < 6 || hour >= 18
    val icon = if (isNight) Icons.Rounded.DarkMode else Icons.Rounded.WbSunny
    val contextMessage = if (isNight) "A quiet moment to end the day." else "Start your day with clarity."
    val iconColor = if (isNight) Color(0xFF5E548E) else Color(0xFFFFA726)

    Scaffold(
        containerColor = LumiBackground,
        topBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                LinearProgressIndicator(
                    progress = { 1.0f },
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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .padding(bottom = 16.dp)
            ) {
                Button(
                    onClick = onFinish,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LumiPrimary,
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(6.dp)
                ) {
                    Text(
                        text = "Enable & Finish",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Rounded.Check, contentDescription = null)
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

            Text(
                text = "When should we check in?",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold
                ),
                color = TextPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            AnimatedContent(
                targetState = icon,
                transitionSpec = { fadeIn(tween(500)) togetherWith fadeOut(tween(500)) }
            ) { targetIcon ->
                Icon(
                    imageVector = targetIcon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = contextMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            Card(
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = LumiSurface),
                elevation = CardDefaults.cardElevation(0.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.Black.copy(0.05f))
            ) {
                Row(
                    modifier = Modifier.padding(32.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    WheelPicker(
                        value = hour,
                        range = 0..23,
                        onValueChange = { onTimeChange(it, minute) },
                        format = { it.toString().padStart(2, '0') }
                    )

                    Text(
                        text = ":",
                        style = MaterialTheme.typography.displayMedium,
                        color = TextSecondary.copy(alpha = 0.5f),
                        modifier = Modifier.padding(horizontal = 12.dp).padding(bottom = 8.dp)
                    )

                    WheelPicker(
                        value = minute,
                        range = 0..59,
                        step = 5, // Increments by 5 minutes now
                        onValueChange = { onTimeChange(hour, it) },
                        format = { it.toString().padStart(2, '0') }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Recommendation Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = NotificationHighlight),
                elevation = CardDefaults.cardElevation(0.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Rounded.NotificationsActive,
                        contentDescription = null,
                        tint = NotificationText,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Highly Recommended",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = NotificationText
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Consistent reflection is key to spotting patterns. We promise not to spam you.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = NotificationText.copy(alpha = 0.9f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WheelPicker(
    value: Int,
    range: IntRange,
    step: Int = 1,
    onValueChange: (Int) -> Unit,
    format: (Int) -> String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(
            onClick = {
                // Logic to wrap around range correctly with step
                val span = range.last - range.first + 1
                val next = ((value - range.first + step) % span) + range.first
                onValueChange(next)
            }
        ) {
            Icon(Icons.Rounded.KeyboardArrowUp, null, tint = LumiPrimary)
        }

        Text(
            text = format(value),
            style = MaterialTheme.typography.displayMedium.copy(
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            ),
            color = TextPrimary,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        IconButton(
            onClick = {
                // Logic to wrap around range correctly with step (handling negatives)
                val span = range.last - range.first + 1
                val prev = ((value - range.first - step).rem(span) + span) % span + range.first
                onValueChange(prev)
            }
        ) {
            Icon(Icons.Rounded.KeyboardArrowDown, null, tint = LumiPrimary)
        }
    }
}
@Preview
@Composable
fun PreviewNewTimePicker() {
    MaterialTheme {
        OnboardingReminderTimeScreen(
            hour = 21,
            minute = 30,
            onTimeChange = { _, _ -> },
            onFinish = {},
            onBack = {},
        )
    }
}