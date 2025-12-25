package com.desktop.lumi.orbit

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Fingerprint
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

// Orbit Palette
private val SpaceDark = Color(0xFF0F172A)
private val StarGold = Color(0xFFFFD700)
private val CalmBlue = Color(0xFF38BDF8)
private val TextLight = Color(0xFFF1F5F9)
private val ChipBackground = Color(0xFF1E293B)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrbitScreen(
    state: OrbitViewModel.OrbitState,
    userName: String = "Me",
    onStart: (Int, String) -> Unit,
    onBreak: () -> Unit,
    onFinish: () -> Unit,
    onBack: () -> Unit,
    onGoToSOS: () -> Unit // ⬅ NEW: Navigation to SOS
) {
    Scaffold(
        containerColor = SpaceDark,
        topBar = {
            TopAppBar(
                title = { Text("Detox Orbit", color = TextLight) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, "Back", tint = TextLight)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SpaceDark)
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            SanctuaryHalo(isActive = true)

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                if (state.isCompleted) {
                    OrbitSuccessView(state, onFinish)
                } else if (state.isActive) {
                    OrbitActiveView(state, onBreak, onBack, onGoToSOS) // Pass SOS handler
                } else {
                    OrbitSetupView(userName, onStart)
                }
            }
        }
    }
}

@Composable
fun SanctuaryHalo(isActive: Boolean) {
    if (!isActive) return

    Canvas(modifier = Modifier.fillMaxSize()) {
        val gradient = Brush.radialGradient(
            0.6f to Color.Transparent,
            1.0f to CalmBlue.copy(alpha = 0.15f),
            center = center,
            radius = size.minDimension / 1.5f
        )
        drawRect(brush = gradient)
    }
}

// ... OrbitSetupView, OrbitContractOverlay ... (Keep existing from previous turn)
@Composable
private fun OrbitSetupView(
    userName: String,
    onStart: (Int, String) -> Unit
) {
    var selectedDuration by remember { mutableStateOf(4) }
    var intention by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()
    var showContract by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp),
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
            ) {
                // Header & Explanation
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Reclaim your time.",
                        style = MaterialTheme.typography.headlineMedium,
                        color = TextLight,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = ChipBackground),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
                    ) {
                        Text(
                            "Set a period of 'No Contact' to focus on yourself, not the silence.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextLight.copy(alpha = 0.8f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }

                // Preset Chips (Visual Guide)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OrbitDurationChip("Pause", "4h", selectedDuration == 4) { selectedDuration = 4 }
                    OrbitDurationChip("Reset", "12h", selectedDuration == 12) { selectedDuration = 12 }
                    OrbitDurationChip("Detox", "24h", selectedDuration == 24) { selectedDuration = 24 }
                }

                // Fine-tune Slider
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Custom Duration", color = TextLight.copy(alpha = 0.5f), fontSize = 14.sp)
                        Text("${selectedDuration}h", color = CalmBlue, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                    Slider(
                        value = selectedDuration.toFloat(),
                        onValueChange = { selectedDuration = it.toInt() },
                        valueRange = 1f..72f,
                        steps = 70,
                        colors = SliderDefaults.colors(thumbColor = CalmBlue, activeTrackColor = CalmBlue, inactiveTrackColor = ChipBackground)
                    )
                }

                // Intention Input
                Card(
                    colors = CardDefaults.cardColors(containerColor = ChipBackground),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("My Intention", color = CalmBlue, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(8.dp))
                        BasicTextField(
                            value = intention,
                            onValueChange = { intention = it },
                            textStyle = TextStyle(color = TextLight, fontSize = 16.sp),
                            modifier = Modifier.fillMaxWidth(),
                            decorationBox = { innerTextField ->
                                if (intention.isEmpty()) Text("e.g., Read a book, Sleep...", color = TextLight.copy(0.3f), fontSize = 16.sp)
                                innerTextField()
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Standard Button to Open Contract
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { showContract = true },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CalmBlue),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Review Commitment", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Overlay: The Contract
        if (showContract) {
            OrbitContractOverlay(
                userName = userName,
                duration = selectedDuration,
                onConfirm = { onStart(selectedDuration, intention) },
                onDismiss = { showContract = false }
            )
        }
    }
}

@Composable
private fun OrbitContractOverlay(
    userName: String,
    duration: Int,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    var isHolding by remember { mutableStateOf(false) }
    var holdProgress by remember { mutableStateOf(0f) }

    LaunchedEffect(isHolding) {
        if (isHolding) {
            val startTime = withFrameMillis { it }
            while (isHolding && holdProgress < 1f) {
                val now = withFrameMillis { it }
                holdProgress = ((now - startTime) / 3000f).coerceIn(0f, 1f)
                if (holdProgress >= 1f) {
                    onConfirm()
                    isHolding = false
                }
                delay(16)
            }
        } else {
            holdProgress = 0f
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.85f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .clickable(enabled = false) {},
            colors = CardDefaults.cardColors(containerColor = SpaceDark),
            border = androidx.compose.foundation.BorderStroke(1.dp, CalmBlue.copy(alpha = 0.3f)),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "THE CONTRACT",
                    style = MaterialTheme.typography.labelMedium,
                    letterSpacing = 4.sp,
                    color = TextLight.copy(0.5f)
                )
                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    "I, $userName,",
                    style = MaterialTheme.typography.headlineSmall,
                    color = TextLight,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "am reclaiming these ${duration} hours for myself.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextLight.copy(0.9f),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    "I choose peace over waiting.",
                    style = MaterialTheme.typography.titleMedium,
                    color = StarGold,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(48.dp))

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(80.dp)
                        .scale(if (isHolding) 0.95f else 1f)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onPress = {
                                    isHolding = true
                                    tryAwaitRelease()
                                    isHolding = false
                                }
                            )
                        }
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawCircle(
                            color = CalmBlue.copy(alpha = 0.2f),
                            style = Stroke(width = 2.dp.toPx())
                        )
                    }

                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawArc(
                            brush = Brush.sweepGradient(listOf(CalmBlue, StarGold)),
                            startAngle = -90f,
                            sweepAngle = 360 * holdProgress,
                            useCenter = false,
                            style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        CalmBlue.copy(alpha = 0.1f + (holdProgress * 0.4f)),
                                        Color.Transparent
                                    )
                                )
                            )
                    )

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(
                                if(holdProgress >= 1f) StarGold else CalmBlue.copy(alpha = 0.3f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if(holdProgress >= 1f) {
                            Icon(Icons.Rounded.Check, null, tint = SpaceDark)
                        } else {
                            Icon(Icons.Rounded.Fingerprint, null, tint = TextLight.copy(0.7f))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    if (holdProgress > 0f) "Sealing..." else "Hold to Seal",
                    color = TextLight.copy(0.5f),
                    fontSize = 12.sp,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

@Composable
private fun OrbitDurationChip(label: String, duration: String, isSelected: Boolean, onClick: () -> Unit) {
    val backgroundColor = if (isSelected) CalmBlue.copy(alpha = 0.2f) else ChipBackground
    val borderColor = if (isSelected) CalmBlue else Color.Transparent
    val textColor = if (isSelected) CalmBlue else TextLight.copy(alpha = 0.7f)

    Column(
        modifier = Modifier
            .width(100.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = duration, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextLight)
        Text(text = label, fontSize = 12.sp, color = textColor)
    }
}

// Updated Active View with Home Button and Break Logic
@Composable
private fun OrbitActiveView(
    state: OrbitViewModel.OrbitState,
    onBreak: () -> Unit,
    onGoToHome: () -> Unit, // ⬅ NEW CTA
    onGoToSOS: () -> Unit // ⬅ NEW for Dialog
) {
    var showBreakDialog by remember { mutableStateOf(false) } // Local state for dialog

    val infiniteTransition = rememberInfiniteTransition()
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(300.dp)) {
            Canvas(modifier = Modifier.fillMaxSize().scale(pulseScale)) {
                drawCircle(color = Color.White.copy(0.1f), style = Stroke(width = 4.dp.toPx()))
                drawArc(
                    brush = Brush.sweepGradient(listOf(CalmBlue, StarGold)),
                    startAngle = -90f,
                    sweepAngle = 360 * state.progress,
                    useCenter = false,
                    style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(state.timeReclaimed, style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.Bold, color = TextLight)
                Text("Reclaimed", style = MaterialTheme.typography.labelLarge, color = TextLight.copy(0.6f), letterSpacing = 2.sp)
            }
        }
        Spacer(modifier = Modifier.height(40.dp))
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(0.05f)),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("You are safe. The silence is not a threat.", color = TextLight, fontWeight = FontWeight.Medium, textAlign = TextAlign.Center)
                if (state.intention.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Remember: \"${state.intention}\"", color = CalmBlue, textAlign = TextAlign.Center, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Return Home Button
        Button(
            onClick = onGoToHome,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = CalmBlue.copy(alpha = 0.2f), contentColor = CalmBlue),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(Icons.Rounded.Home, null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Return to Sanctuary", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Break Orbit (Triggers Dialog)
        TextButton(onClick = { showBreakDialog = true }) {
            Text("I'm slipping (Break Orbit)", color = Color.Red.copy(0.7f))
        }
    }

    // Break Confirmation Dialog
    if (showBreakDialog) {
        AlertDialog(
            onDismissRequest = { showBreakDialog = false },
            containerColor = SpaceDark,
            title = { Text("Are you spiraling?", color = TextLight, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "You are doing great. If you are feeling panic, the SOS breathing exercise might help more than breaking your streak.",
                    color = TextLight.copy(alpha = 0.8f)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showBreakDialog = false
                        onGoToSOS()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CalmBlue)
                ) {
                    Text("Go to SOS Mode")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showBreakDialog = false
                        onBreak() // Actual Break
                    }
                ) {
                    Text("Break Orbit", color = Color.Red.copy(0.8f))
                }
            }
        )
    }
}

@Composable
private fun OrbitSuccessView(state: OrbitViewModel.OrbitState, onFinish: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(Icons.Rounded.Check, null, tint = StarGold, modifier = Modifier.size(80.dp))
        Spacer(modifier = Modifier.height(24.dp))
        Text("Orbit Complete.", style = MaterialTheme.typography.headlineLarge, color = StarGold, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        Text("You reclaimed ${state.timeReclaimed} for yourself.\nThat is a massive win.", color = TextLight, textAlign = TextAlign.Center, style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(48.dp))
        Button(onClick = onFinish, modifier = Modifier.fillMaxWidth().height(56.dp), colors = ButtonDefaults.buttonColors(containerColor = StarGold, contentColor = SpaceDark)) {
            Text("Claim Victory", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}