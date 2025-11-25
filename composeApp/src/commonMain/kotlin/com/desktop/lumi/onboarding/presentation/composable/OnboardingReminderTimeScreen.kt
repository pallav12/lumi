package com.desktop.lumi.onboarding.presentation.composable

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
import org.jetbrains.compose.ui.tooling.preview.Preview

private val SoftPink = Color(0xFFFFE5F1) // Very light pink
private val SoftBlue = Color(0xFFE5F0FF) // Very light blue
private val PrimarySoft = Color(0xFFB8A4D9) // Soft lavender/pastel purple

@Composable
fun OnboardingReminderTimeScreen(
    hour: Int,                // 0–23
    minute: Int,              // 0–59
    onTimeChange: (Int, Int) -> Unit,
    onFinish: () -> Unit,
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
            text = "When should I remind you to reflect?",
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Subtitle
        Text(
            text = "I'll gently remind you once a day.",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF777777),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Time Selector
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Hour Picker
            TimePicker(
                value = hour,
                range = 0..23,
                onValueChange = { newHour -> onTimeChange(newHour, minute) },
                formatValue = { if (it < 10) "0$it" else "$it" }
            )

            // Colon separator
            Text(
                text = ":",
                fontSize = 22.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            // Minute Picker
            TimePicker(
                value = minute,
                range = 0..59,
                onValueChange = { newMinute -> onTimeChange(hour, newMinute) },
                formatValue = { if (it < 10) "0$it" else "$it" }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Finish Button
        Button(
            onClick = onFinish,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimarySoft,
                contentColor = Color.White
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 0.dp,
                pressedElevation = 2.dp
            )
        ) {
            Text(
                text = "Finish",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }

        // Bottom padding for scroll
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun TimePicker(
    value: Int,
    range: IntRange,
    onValueChange: (Int) -> Unit,
    formatValue: (Int) -> String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Up Arrow Button
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(
                    color = PrimarySoft.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(12.dp)
                )
                .border(
                    width = 1.dp,
                    color = PrimarySoft.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(12.dp)
                )
                .clickable {
                    val newValue = if (value == range.last) range.first else value + 1
                    onValueChange(newValue)
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "▲",
                fontSize = 18.sp,
                color = PrimarySoft,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Current Value Display
        Box(
            modifier = Modifier
                .width(80.dp)
                .height(60.dp)
                .background(
                    color = PrimarySoft.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(16.dp)
                )
                .border(
                    width = 2.dp,
                    color = PrimarySoft,
                    shape = RoundedCornerShape(16.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = formatValue(value),
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Down Arrow Button
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(
                    color = PrimarySoft.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(12.dp)
                )
                .border(
                    width = 1.dp,
                    color = PrimarySoft.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(12.dp)
                )
                .clickable {
                    val newValue = if (value == range.first) range.last else value - 1
                    onValueChange(newValue)
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "▼",
                fontSize = 18.sp,
                color = PrimarySoft,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Preview
@Composable
fun PreviewOnboardingReminderTimeScreen() {
    MaterialTheme {
        OnboardingReminderTimeScreen(
            hour = 9,
            minute = 30,
            onTimeChange = { _, _ -> },
            onFinish = {},
            onBack = {}
        )
    }
}