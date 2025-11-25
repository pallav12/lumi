package com.desktop.lumi.home.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.desktop.lumi.home.HomeViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview

// UI Models
//data class ReflectionUiState(val mood: Int, val note: String?)
//data class WeeklyTrendUiState(
//    val moodPoints: List<Int>,    // last 7 days mood (1-5)
//    val positiveCount: Int,
//    val negativeCount: Int
//)

private val SoftPink = Color(0xFFFFE5F1) // Very light pink
private val SoftBlue = Color(0xFFE5F0FF) // Very light blue
private val PrimarySoft = Color(0xFFB8A4D9) // Soft lavender/pastel purple
private val SoftGreen = Color(0xFFE8F5E8) // Very light green
private val SoftOrange = Color(0xFFFFF4E6) // Very light orange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    personName: String,
    todayReflection: HomeViewModel.ReflectionUiState?,   // null if not submitted today
    weeklyTrend: HomeViewModel.WeeklyTrendUiState,       // contains mood graph data, positive/negative counts
    onLogReflection: () -> Unit,
    onLogInteraction: () -> Unit,
    onOpenInsights: () -> Unit,
    onOpenTimeline: () -> Unit,
    onOpenSettings: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hi, $personName") },
                actions = {
                    IconButton(onClick = onOpenSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        }) {
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp)
                .padding(top = 60.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top
        ) {

            // Header
            GreetingHeader(
                personName = personName,
                hasReflection = todayReflection != null
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Today Card
            TodayReflectionCard(
                todayReflection = todayReflection,
                onLogReflection = onLogReflection
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Quick Actions
            QuickActions(
                onLogInteraction = onLogInteraction,
                onOpenInsights = onOpenInsights
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Weekly Trend Section
            WeeklyTrendSection(
                weeklyTrend = weeklyTrend
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Timeline Call to Action
            TimelineCallToAction(
                onOpenTimeline = onOpenTimeline
            )

            // Bottom padding for scroll
            Spacer(modifier = Modifier.height(32.dp))
        }

    }

}

@Composable
private fun GreetingHeader(
    personName: String,
    hasReflection: Boolean
) {
    Column {
        Text(
            text = "How did today feel with $personName?",
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.fillMaxWidth()
        )

        if (!hasReflection) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Take a moment to reflect today.",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF777777),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun TodayReflectionCard(
    todayReflection: HomeViewModel.ReflectionUiState?,
    onLogReflection: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(
            containerColor = PrimarySoft.copy(alpha = 0.12f)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (todayReflection == null) {
                // No reflection yet
                Text(
                    text = "No reflection yet",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(16.dp))

                FilledTonalButton(
                    onClick = onLogReflection,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Log Reflection",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            } else {
                // Show today's reflection
                Text(
                    text = getMoodEmoji(todayReflection.mood),
                    fontSize = 48.sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                todayReflection.note?.let { note ->
                    if (note.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "\"$note\"",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color(0xFF777777),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                FilledTonalButton(
                    onClick = onLogReflection,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "View Reflection",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickActions(
    onLogInteraction: () -> Unit,
    onOpenInsights: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ElevatedButton(
            onClick = onLogInteraction,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "Log Interaction",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }

        ElevatedButton(
            onClick = onOpenInsights,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "View Insights",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun WeeklyTrendSection(
    weeklyTrend: HomeViewModel.WeeklyTrendUiState
) {
    Column {
        Text(
            text = "This Week",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Mini mood graph
        MoodGraph(
            moodPoints = weeklyTrend.moodPoints,
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Interaction counts
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            InteractionCountItem(
                count = weeklyTrend.positiveCount,
                label = "Positive interactions",
                color = SoftGreen
            )

            InteractionCountItem(
                count = weeklyTrend.negativeCount,
                label = "Negative interactions",
                color = SoftOrange
            )
        }
    }
}

@Composable
private fun InteractionCountItem(
    count: Int,
    label: String,
    color: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(
                    color = color,
                    shape = RoundedCornerShape(6.dp)
                )
        )

        Column {
            Text(
                text = "$count",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFF777777)
            )
        }
    }
}

@Composable
private fun MoodGraph(
    moodPoints: List<Int>,
    modifier: Modifier = Modifier
) {
    val strokeColor = PrimarySoft.copy(alpha = 0.7f)

    Box(
        modifier = modifier
            .background(
                color = SoftBlue.copy(alpha = 0.3f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp)
    ) {
        if (moodPoints.isEmpty()) {
            // Empty state
            Text(
                text = "No data yet",
                fontSize = 12.sp,
                color = Color(0xFF999999),
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            Canvas(
                modifier = Modifier.fillMaxSize()
            ) {
                val width = size.width
                val height = size.height
                val pointSpacing = width / (moodPoints.size - 1).coerceAtLeast(1)

                // Draw connecting lines
                val path = Path()
                moodPoints.forEachIndexed { index, mood ->
                    val x = index * pointSpacing
                    val y = height - ((mood - 1) / 4f) * height // mood 1-5 mapped to height

                    if (index == 0) {
                        path.moveTo(x, y)
                    } else {
                        path.lineTo(x, y)
                    }
                }

                drawPath(
                    path = path,
                    color = strokeColor,
                    style = Stroke(
                        width = 3.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                )

                // Draw points
                moodPoints.forEachIndexed { index, mood ->
                    val x = index * pointSpacing
                    val y = height - ((mood - 1) / 4f) * height

                    drawCircle(
                        color = strokeColor,
                        radius = 4.dp.toPx(),
                        center = Offset(x, y)
                    )
                }
            }
        }
    }
}

@Composable
private fun TimelineCallToAction(
    onOpenTimeline: () -> Unit
) {
    TextButton(
        onClick = onOpenTimeline,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Open Timeline →",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = PrimarySoft
        )
    }
}

private fun getMoodEmoji(mood: Int): String {
    return when (mood) {
        1 -> "😢"
        2 -> "😕"
        3 -> "😐"
        4 -> "😊"
        5 -> "😍"
        else -> "😐"
    }
}

@Preview
@Composable
fun PreviewHomeScreenNoReflection() {
    MaterialTheme {
        HomeScreen(
            personName = "Alex",
            todayReflection = null,
            weeklyTrend = HomeViewModel.WeeklyTrendUiState(
                moodPoints = listOf(3, 4, 2, 5, 3, 4, 5),
                positiveCount = 12,
                negativeCount = 3
            ),
            onLogReflection = {},
            onLogInteraction = {},
            onOpenInsights = {},
            onOpenTimeline = {},
            onOpenSettings = {}
        )
    }
}

@Preview
@Composable
fun PreviewHomeScreenWithReflection() {
    MaterialTheme {
        HomeScreen(
            personName = "Jordan",
            todayReflection = HomeViewModel.ReflectionUiState(
                mood = 4,
                note = "Had a wonderful dinner together and talked about our future plans."
            ),
            weeklyTrend = HomeViewModel.WeeklyTrendUiState(
                moodPoints = listOf(3, 4, 2, 5, 3, 4, 4),
                positiveCount = 8,
                negativeCount = 2
            ),
            onLogReflection = {},
            onLogInteraction = {},
            onOpenInsights = {},
            onOpenTimeline = {},
            onOpenSettings = {}
        )
    }
}