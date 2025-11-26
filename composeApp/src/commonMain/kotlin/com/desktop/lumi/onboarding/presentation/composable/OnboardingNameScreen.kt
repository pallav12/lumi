package com.desktop.lumi.onboarding.presentation.composable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.ui.tooling.preview.Preview

// Consistent Lumi Palette
private val LumiBackground = Color(0xFFFAFAFA)
private val LumiPrimary = Color(0xFF8E8CD8)
private val TextPrimary = Color(0xFF2D2D39)
private val TextSecondary = Color(0xFF8A8A99)
private val InputLineColor = Color(0xFFE0E0E0)

@Composable
fun OnboardingNameScreen(
    name: String,
    onNameChange: (String) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    // Auto-focus the text field when screen opens
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Scaffold(
        containerColor = LumiBackground,
        topBar = {
            // minimal top bar with progress
            Column(modifier = Modifier.fillMaxWidth()) {
                LinearProgressIndicator(
                    progress = { 0.33f }, // Step 1 of 3
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
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.weight(0.2f))

            // 1. The Question (Human & Soft)
            Text(
                text = "First, who is on your mind?",
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
                text = "We'll keep this private. It stays on your device.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            // 2. The Hero Input
            // Minimalist, large text, centered.
            TextField(
                value = name,
                onValueChange = onNameChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                textStyle = MaterialTheme.typography.headlineLarge.copy(
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium,
                    color = LumiPrimary
                ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = LumiPrimary,
                    unfocusedIndicatorColor = InputLineColor,
                    cursorColor = LumiPrimary
                ),
                placeholder = {
                    Text(
                        text = "Enter Name",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            textAlign = TextAlign.Center,
                            color = InputLineColor
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { if (name.isNotBlank()) onNext() }
                )
            )

            Spacer(modifier = Modifier.weight(0.4f))

            // 3. The "Pulse" Action
            // Only show when they start typing
            AnimatedVisibility(
                visible = name.isNotBlank(),
                enter = fadeIn() + slideInVertically(initialOffsetY = { 50 })
            ) {
                Button(
                    onClick = onNext,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp), // Fully rounded
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LumiPrimary,
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 6.dp,
                        pressedElevation = 2.dp
                    )
                ) {
                    Text(
                        text = "Continue",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.AutoMirrored.Rounded.ArrowForward, contentDescription = null)
                }
            }

            // Placeholder space to prevent jumpiness if button is hidden
            if (name.isBlank()) {
                Spacer(modifier = Modifier.height(56.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Preview
@Composable
fun PreviewNewOnboarding() {
    MaterialTheme {
        OnboardingNameScreen(
            name = "Rajeev",
            onNameChange = {},
            onNext = {},
            onBack = {}
        )
    }
}