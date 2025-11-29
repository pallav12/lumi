package com.desktop.lumi.home.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.Insights
import androidx.compose.material.icons.rounded.Spa
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.desktop.lumi.home.HomeViewModel
import com.desktop.lumi.instantmirror.InstantInsightBottomSheet
import org.jetbrains.compose.ui.tooling.preview.Preview

// --- New Premium Palette ---
// Using darker text for readability and softer backgrounds for comfort
private val LumiBackground = Color(0xFFFAFAFA) // Off-white, easier on eyes than #FFFFFF
private val LumiSurface = Color(0xFFFFFFFF)
private val LumiPrimary = Color(0xFF8E8CD8) // Deep Lavender
private val LumiSecondary = Color(0xFFFFB7B2) // Soft Coral
private val TextPrimary = Color(0xFF2D2D39)
private val TextSecondary = Color(0xFF8A8A99)
private val PositiveGreen = Color(0xFF98D8AA)
private val NegativeRed = Color(0xFFFF9E9E)
private val SOSColor = Color(0xFFE57373) // Soft Red for SOS
private val TimelineColor = Color(0xFFA0C4FF) // Soft Blue for Timeline
private val VoidColor = Color(0xFF2D1B4E) // Deep mystic purple/black for Void

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    uiState: HomeViewModel.HomeUiState,
    onLogReflection: () -> Unit,
    onLogInteraction: () -> Unit,
    onOpenInsights: () -> Unit,
    onOpenTimeline: () -> Unit,
    onOpenSettings: () -> Unit,
    onDismissInsight: () -> Unit,
    onOpenSOS: () -> Unit,
    onOpenVoid: () -> Unit
) {
    Scaffold(
        containerColor = LumiBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Lumi",
                        fontFamily = FontFamily.Serif, // Branding touch
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = LumiBackground),
                actions = {
                    IconButton(onClick = onOpenSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings", tint = TextSecondary)
                    }
                }
            )
        }
    ) { padding ->
        val scrollState = rememberScrollState()

        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(padding)
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.height(16.dp))

                // 1. Personal Greeting (Large & Warm)
                GreetingSection(name = uiState.personName)

                Spacer(modifier = Modifier.height(24.dp))

                // 2. SOS Button (The Panic Button)
                // Placed prominently at the top for immediate access
                SOSButton(onClick = onOpenSOS)

                Spacer(modifier = Modifier.height(24.dp))

                // 3. The Main "Action" Card (Reflection)
                // If they haven't reflected, this invites them. If they have, it celebrates them.
                DailyReflectionCard(
                    reflection = uiState.todayReflection,
                    onClick = onLogReflection
                )

                Spacer(modifier = Modifier.height(32.dp))

                // 4. Emotional Trend Graph (The "Vitals")
                // Moved up because visuals engage users
                WeeklyVitalsSection(
                    weeklyTrend = uiState.weeklyTrend,
                    onOpenTimeline = onOpenTimeline // Restored the link here
                )

                Spacer(modifier = Modifier.height(32.dp))

                // 5. Primary Actions (Log & Insights)
                ActionGrid(
                    onLogInteraction = onLogInteraction,
                    onOpenInsights = onOpenInsights
                )

                Spacer(modifier = Modifier.height(32.dp))

                // 6. The Void (The "Portal" at the bottom)
                VoidPortalCard(onClick = onOpenVoid)

                Spacer(modifier = Modifier.height(48.dp)) // Bottom breathing room
            }

            // Bottom Sheet for the "Painkiller" Feature
            InstantInsightBottomSheet(
                insight = uiState.instantInsight,
                onDismiss = onDismissInsight
            )
        }
    }
}

@Composable
private fun GreetingSection(name: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Good Morning,",
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary
        )
        Text(
            text = "How are things with $name?",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.SemiBold
            ),
            color = TextPrimary
        )
    }
}

@Composable
private fun SOSButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(25.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = SOSColor.copy(alpha = 0.1f),
            contentColor = SOSColor
        ),
        elevation = ButtonDefaults.buttonElevation(0.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SOSColor.copy(alpha = 0.3f))
    ) {
        Icon(
            imageVector = Icons.Rounded.Spa, // Calming icon
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "I'm Spiraling / Need Grounding",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun DailyReflectionCard(
    reflection: HomeViewModel.ReflectionUiState?,
    onClick: () -> Unit
) {
    if (reflection == null) {
        // Empty State: Inviting, Dashed Border
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .clip(RoundedCornerShape(24.dp))
                .border(
                    width = 2.dp,
                    color = LumiPrimary.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(24.dp),
                    // Dash effect would go here with PathEffect, simple border for now
                )
                .background(LumiPrimary.copy(alpha = 0.05f))
                .clickable { onClick() }
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = LumiPrimary,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Log Today's Mood",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = LumiPrimary
                )
                Text(
                    text = "Track the ups and downs",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }
    } else {
        // Filled State: Solid, Comforting
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() },
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = LumiSurface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier.padding(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Large Emoji
                Text(
                    text = getMoodEmoji(reflection.mood),
                    fontSize = 42.sp
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = "Today's Reflection",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextSecondary.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (reflection.note.isNullOrBlank()) "No notes added." else reflection.note,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary,
                        maxLines = 2
                    )
                }
            }
        }
    }
}

@Composable
private fun WeeklyVitalsSection(
    weeklyTrend: HomeViewModel.WeeklyTrendUiState,
    onOpenTimeline: () -> Unit // Passed back in
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Emotional Rhythm",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            // Restored the History Link
            TextButton(onClick = onOpenTimeline) {
                Text(
                    "See History",
                    color = LumiPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = LumiSurface),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            modifier = Modifier.border(1.dp, Color.Black.copy(0.05f), RoundedCornerShape(24.dp))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {

                // The Graph
                SmoothMoodGraph(
                    moodPoints = weeklyTrend.moodPoints,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Stats Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatPill(weeklyTrend.positiveCount, "Positive", PositiveGreen)
                    StatPill(weeklyTrend.negativeCount, "Drained", NegativeRed)
                }
            }
        }
    }
}

@Composable
private fun StatPill(count: Int, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(color.copy(alpha = 0.2f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$count",
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
    }
}

@Composable
private fun ActionGrid(
    onLogInteraction: () -> Unit,
    onOpenInsights: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ActionButton(
            title = "Log\nInteraction",
            icon = Icons.Default.Add,
            color = LumiPrimary,
            onClick = onLogInteraction,
            modifier = Modifier.weight(1f)
        )

        ActionButton(
            title = "Weekly\nInsights",
            icon = Icons.Rounded.Insights,
            color = LumiSecondary,
            onClick = onOpenInsights,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun ActionButton(
    title: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(100.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.15f)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(28.dp)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.align(Alignment.BottomStart)
            )
        }
    }
}

@Composable
private fun VoidPortalCard(onClick: () -> Unit) {
    // Unique "Dark Mode" card to represent entering the Void
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = VoidColor),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Enter The Void",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Vent safely. Burn the message.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }

            // Mysterious Icon
            Icon(
                imageVector = Icons.Rounded.AutoAwesome, // Sparkles/Magic
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.9f),
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
private fun SmoothMoodGraph(
    moodPoints: List<Int>,
    modifier: Modifier = Modifier
) {
    // Gradient definition
    val brush = Brush.verticalGradient(
        colors = listOf(LumiPrimary.copy(alpha = 0.4f), LumiPrimary.copy(alpha = 0.0f))
    )

    Canvas(modifier = modifier) {
        if (moodPoints.isEmpty()) return@Canvas

        val w = size.width
        val h = size.height
        val spacing = w / (moodPoints.size - 1).coerceAtLeast(1)

        val path = Path()

        // Logic to draw smooth bezier curves between points
        moodPoints.forEachIndexed { index, mood ->
            // Map mood (1-5) to height (inverted, 5 is high)
            // 5 -> 0, 1 -> h
            val x = index * spacing
            val y = h - ((mood - 1) / 4f) * h

            if (index == 0) {
                path.moveTo(x, y)
            } else {
                // Calculate control points for smooth curve
                val prevX = (index - 1) * spacing
                val prevY = h - ((moodPoints[index - 1] - 1) / 4f) * h

                // Control points placed halfway between X's, but maintaining Y trends
                val controlX1 = prevX + (x - prevX) / 2
                val controlX2 = prevX + (x - prevX) / 2

                path.cubicTo(controlX1, prevY, controlX2, y, x, y)
            }
        }

        // Draw the fill
        val fillPath = Path().apply {
            addPath(path)
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }

        drawPath(
            path = fillPath,
            brush = brush
        )

        // Draw the stroke
        drawPath(
            path = path,
            color = LumiPrimary,
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
        )

        // Draw Dots
        moodPoints.forEachIndexed { index, mood ->
            val x = index * spacing
            val y = h - ((mood - 1) / 4f) * h
            drawCircle(
                color = LumiSurface,
                radius = 6.dp.toPx(),
                center = Offset(x, y)
            )
            drawCircle(
                color = LumiPrimary,
                radius = 4.dp.toPx(),
                center = Offset(x, y)
            )
        }
    }
}

private fun getMoodEmoji(mood: Int): String {
    return when (mood) {
        1 -> "😫"
        2 -> "😕"
        3 -> "😐"
        4 -> "🙂"
        5 -> "🥰"
        else -> "😐"
    }
}

@Preview
@Composable
fun NewHomePreview() {
    MaterialTheme {
        HomeScreen(
            uiState = HomeViewModel.HomeUiState(
                personName = "Alex",
                todayReflection = null,
                weeklyTrend = HomeViewModel.WeeklyTrendUiState(
                    moodPoints = listOf(3, 2, 4, 3, 5, 4, 2),
                    positiveCount = 12,
                    negativeCount = 4
                )
            ),
            {}, {}, {}, {}, {}, {}, {}, {}
        )
    }
}