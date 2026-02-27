package com.desktop.lumi.db.com.desktop.lumi.lovejar.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.desktop.lumi.db.com.desktop.lumi.lovejar.AnchorEntry
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

// Deep, immersive colors
private val SpaceDark = Color(0xFF0F172A)
private val CardGlow = Color(0xFFD4A373) // Warm gold
private val CardBackground = Color(0xFF1E293B)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnchorDeckScreen(
    topCard: AnchorEntry?,
    nextCard: AnchorEntry?,
    onCardSwiped: () -> Unit,
    onAddClick: () -> Unit,
    onBack: () -> Unit
) {
    // State for the "Cute Message" on Swipe Left
    var leftSwipeMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        containerColor = SpaceDark,
        topBar = {
            TopAppBar(
                title = {
                    Text("The Anchor", color = Color.White, fontFamily = FontFamily.Serif)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = onAddClick) {
                        Icon(Icons.Rounded.Add, "Add", tint = CardGlow)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {

            if (topCard == null) {
                EmptyDeckState(onAddClick)
            } else {
                // The Bottom Card (Next up) - slightly scaled down
                if (nextCard != null) {
                    AnchorCard(
                        entry = nextCard,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 16.dp)
                            .graphicsLayer {
                                scaleX = 0.95f
                                scaleY = 0.95f
                                alpha = 0.7f
                            }
                    )
                }

                // The Top Card (Draggable)
                DraggableAnchorCard(
                    entry = topCard,
                    onSwipeRight = { onCardSwiped() },
                    onSwipeLeft = {
                        leftSwipeMessage = listOf(
                            "Letting go of that thought. 🍃",
                            "You are safe here. ✨",
                            "Breathe in, breathe out. 🌬️",
                            "That's okay too. 💛"
                        ).random()
                        onCardSwiped()
                    }
                )
            }

            // Swipe Left Cute Message Overlay
            AnimatedVisibility(
                visible = leftSwipeMessage != null,
                enter = fadeIn(),
                exit = fadeOut(animationSpec = tween(1000)),
                modifier = Modifier.align(Alignment.TopCenter)
            ) {
                Text(
                    text = leftSwipeMessage ?: "",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                        .padding(horizontal = 24.dp, vertical = 12.dp)
                )

                // Auto-hide message
                LaunchedEffect(leftSwipeMessage) {
                    if (leftSwipeMessage != null) {
                        delay(1500)
                        leftSwipeMessage = null
                    }
                }
            }
        }
    }
}

@Composable
fun DraggableAnchorCard(
    entry: AnchorEntry,
    onSwipeRight: () -> Unit,
    onSwipeLeft: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val offsetX = remember { Animatable(0f) }
    val offsetY = remember { Animatable(0f) }

    // Rotation based on how far it's dragged
    val rotation = offsetX.value / 20f

    AnchorCard(
        entry = entry,
        modifier = Modifier
            .fillMaxSize()
            .offset { IntOffset(offsetX.value.roundToInt(), offsetY.value.roundToInt()) }
            .rotate(rotation)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = {
                        coroutineScope.launch {
                            val threshold = size.width * 0.3f // Swipe threshold
                            if (offsetX.value > threshold) {
                                // Swiped Right
                                offsetX.animateTo(size.width.toFloat() * 1.5f, tween(300))
                                onSwipeRight()
                            } else if (offsetX.value < -threshold) {
                                // Swiped Left
                                offsetX.animateTo(-size.width.toFloat() * 1.5f, tween(300))
                                onSwipeLeft()
                            } else {
                                // Snap back to center
                                launch { offsetX.animateTo(0f, tween(300)) }
                                launch { offsetY.animateTo(0f, tween(300)) }
                            }
                        }
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        coroutineScope.launch {
                            offsetX.snapTo(offsetX.value + dragAmount.x)
                            offsetY.snapTo(offsetY.value + dragAmount.y)
                        }
                    }
                )
            }
    )
}

@Composable
fun AnchorCard(
    entry: AnchorEntry,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {

            // Image Background (if exists)
            if (entry.imageUri != null) {
                AsyncImage(
                    model = entry.imageUri,
                    contentDescription = "Memory",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                // Gradient Scrim to make text readable
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.9f)),
                                startY = 300f
                            )
                        )
                )
            } else {
                // No image? Show a beautiful gradient background
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF2D1B4E), Color(0xFF1A1033))
                            )
                        )
                )
            }

            // Text Content (Bottom Aligned)
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(24.dp)
            ) {
                Text(
                    text = entry.content,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 34.sp
                    ),
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Interaction hint
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Swipe right to ground  •  Swipe left to release",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyDeckState(onAddClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(Icons.Rounded.Add, null, modifier = Modifier.size(64.dp), tint = CardGlow.copy(alpha = 0.5f))
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Your Anchor is empty.",
            color = Color.White,
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            "Add a screenshot or memory to start.",
            color = Color.White.copy(0.6f)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onAddClick,
            colors = ButtonDefaults.buttonColors(containerColor = CardGlow, contentColor = SpaceDark)
        ) {
            Text("Add Evidence", fontWeight = FontWeight.Bold)
        }
    }
}