package com.desktop.lumi.db.com.desktop.lumi.message

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.desktop.lumi.void.VoidViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview

// The "Void" Palette
private val VoidDark = Color(0xFF121212) // Deepest Black
private val StarWhite = Color(0xFFF0F0F0)
private val BurnOrange = Color(0xFFFF7043)
private val VoidPurple = Color(0xFF2D1B4E)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoidScreen(
    state: VoidViewModel.VoidState,
    onMessageChange: (String) -> Unit,
    onRelease: () -> Unit,
    onBack: () -> Unit
) {
    // Animation for Burn Phase
    val burnProgress by animateFloatAsState(
        targetValue = if (state.isBurning) 1f else 0f,
        animationSpec = tween(durationMillis = 3000) // Slow burn
    )

    Scaffold(
        containerColor = VoidDark,
        topBar = {
            TopAppBar(
                title = { }, // minimalist header
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, "Back", tint = StarWhite.copy(alpha = 0.5f))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = VoidDark)
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    // Subtle radial gradient to give depth
                    brush = Brush.radialGradient(
                        colors = listOf(VoidPurple.copy(alpha = 0.2f), VoidDark),
                        radius = 800f
                    )
                )
                .padding(24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {

                if (state.isBurned) {
                    // ------------------------------------------------
                    // STATE: SUCCESS (Empty Void)
                    // ------------------------------------------------
                    Icon(
                        imageVector = Icons.Rounded.AutoAwesome,
                        contentDescription = null,
                        tint = StarWhite.copy(alpha = 0.3f),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        "It is gone.",
                        color = StarWhite.copy(alpha = 0.6f),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = FontFamily.Serif
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "You released it into the void.\nYou are safe.",
                        color = StarWhite.copy(alpha = 0.4f),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )

                } else {
                    // ------------------------------------------------
                    // STATE: WRITING / BURNING
                    // ------------------------------------------------

                    // Prompt
                    Text(
                        if (state.isLocked) "Releasing..." else "Speak to the void.",
                        color = StarWhite.copy(alpha = 0.5f),
                        fontSize = 14.sp,
                        letterSpacing = 2.sp
                    )

                    Spacer(modifier = Modifier.height(40.dp))

                    // The "Cosmic Paper" Input
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            // The Disintegration Effect
                            .alpha(1f - burnProgress)
                            .scale(1f + (burnProgress * 0.2f)) // Expands as it dissipates
                            .blur(if (state.isBurning) (burnProgress * 20).dp else 0.dp), // Blurs into nothing
                        contentAlignment = Alignment.Center
                    ) {
                        if (state.message.isEmpty()) {
                            Text(
                                "Type the text you want to send,\nbut shouldn't.",
                                color = StarWhite.copy(alpha = 0.2f),
                                textAlign = TextAlign.Center,
                                fontSize = 18.sp
                            )
                        }

                        BasicTextField(
                            value = state.message,
                            onValueChange = onMessageChange,
                            enabled = !state.isLocked,
                            textStyle = TextStyle(
                                color = StarWhite,
                                fontSize = 22.sp,
                                textAlign = TextAlign.Center,
                                fontFamily = FontFamily.Serif,
                                lineHeight = 32.sp
                            ),
                            cursorBrush = SolidColor(StarWhite),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(48.dp))

                    // The Release Button
                    // Only visible if there is text and not already burning
                    if (state.message.isNotBlank() && !state.isBurning) {
                        Button(
                            onClick = onRelease,
                            enabled = !state.isLocked,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (state.isLocked) Color.Gray.copy(0.2f) else BurnOrange,
                                contentColor = Color.White
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(28.dp) // Fully rounded
                        ) {
                            if (state.isLocked) {
                                Icon(Icons.Rounded.Lock, null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(12.dp))
                                Text("Dissolving...", fontSize = 16.sp)
                            } else {
                                Text("Burn into the Void", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewVoidScreen() {
    VoidScreen(VoidViewModel.VoidState(), {}, {}, {})
}