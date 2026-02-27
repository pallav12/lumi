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
import androidx.compose.material.icons.rounded.Anchor
import androidx.compose.material.icons.rounded.ChatBubbleOutline
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.HourglassEmpty
import androidx.compose.material.icons.rounded.Insights
import androidx.compose.material.icons.rounded.Lock
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
import com.desktop.lumi.orbit.OrbitViewModel
import com.desktop.lumi.orbit.SanctuaryHalo
import org.jetbrains.compose.ui.tooling.preview.Preview

// --- New Premium Palette ---
private val LumiBackground = Color(0xFFFAFAFA)
private val LumiSurface = Color(0xFFFFFFFF)
private val LumiPrimary = Color(0xFF8E8CD8)
private val LumiSecondary = Color(0xFFFFB7B2)
private val TextPrimary = Color(0xFF2D2D39)
private val TextSecondary = Color(0xFF8A8A99)
private val PositiveGreen = Color(0xFF98D8AA)
private val NegativeRed = Color(0xFFFF9E9E)
private val SOSColor = Color(0xFFE57373)
private val VoidColor = Color(0xFF2D1B4E)
private val ScriptColor = Color(0xFF5E548E)
private val OrbitColor = Color(0xFF0F172A)
private val AnchorColor = Color(0xFFD4A373) // Warm Gold/Brown for Anchor
private val SpaceDark = Color(0xFF0F172A) // Matches Orbit Palette
private val TextLight = Color(0xFFF1F5F9)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    uiState: HomeViewModel.HomeUiState,
    orbitState: OrbitViewModel.OrbitState? = null,
    onLogReflection: () -> Unit,
    onLogInteraction: () -> Unit,
    onOpenInsights: () -> Unit,
    onOpenTimeline: () -> Unit,
    onOpenSettings: () -> Unit,
    onDismissInsight: () -> Unit,
    onOpenSOS: () -> Unit,
    onOpenVoid: () -> Unit,
    onOpenScripts: () -> Unit,
    onOpenOrbit: () -> Unit,
    onOpenAnchor: () -> Unit // ⬅ NEW Callback
) {
    // Sanctuary Mode Logic
    val isSanctuaryMode = orbitState?.isActive == true && !orbitState.isCompleted

    // Theme switching
    val bgColor = if (isSanctuaryMode) SpaceDark else LumiBackground
    val textColor = if (isSanctuaryMode) TextLight else TextPrimary
    val secondaryTextColor = if (isSanctuaryMode) TextLight.copy(alpha = 0.7f) else TextSecondary
    val cardBgColor = if (isSanctuaryMode) Color.White.copy(alpha = 0.1f) else LumiSurface

    Scaffold(
        containerColor = bgColor,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Lumi",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = bgColor),
                actions = {
                    IconButton(onClick = onOpenSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings", tint = secondaryTextColor)
                    }
                }
            )
        }
    ) { padding ->
        // Persistent Halo if in Sanctuary Mode
        SanctuaryHalo(isActive = isSanctuaryMode)

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

                // 1. Personal Greeting OR Active Orbit Banner
                if (isSanctuaryMode && orbitState != null) {
                    ActiveOrbitBanner(orbitState = orbitState, onClick = onOpenOrbit)
                } else {
                    GreetingSection(name = uiState.personName, textColor = secondaryTextColor, highlightColor = textColor)
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 2. SOS Button
                SOSButton(onClick = onOpenSOS)

                Spacer(modifier = Modifier.height(24.dp))

                // 3. Daily Reflection Card
                DailyReflectionCard(
                    reflection = uiState.todayReflection,
                    onClick = onLogReflection,
                    bgColor = cardBgColor,
                    textColor = textColor,
                    secondaryTextColor = secondaryTextColor
                )

                Spacer(modifier = Modifier.height(32.dp))

                // 4. Emotional Trend Graph
                WeeklyVitalsSection(
                    weeklyTrend = uiState.weeklyTrend,
                    onOpenTimeline = onOpenTimeline,
                    textColor = textColor,
                    cardBgColor = cardBgColor
                )

                Spacer(modifier = Modifier.height(32.dp))

                // 5. Primary Actions
                ActionGrid(
                    onLogInteraction = onLogInteraction,
                    onOpenInsights = onOpenInsights,
                    isLocked = isSanctuaryMode,
                    textColor = textColor
                )

                Spacer(modifier = Modifier.height(32.dp))

                // 6. Sanctuary Tools
                SanctuaryToolsSection(
                    orbitState = orbitState,
                    headerColor = textColor,
                    onOpenVoid = onOpenVoid,
                    onOpenOrbit = onOpenOrbit,
                    onOpenScripts = onOpenScripts,
                    onOpenAnchor = onOpenAnchor // ⬅ Pass it
                )

                Spacer(modifier = Modifier.height(48.dp))
            }

            // Bottom Sheet
            InstantInsightBottomSheet(
                insight = uiState.instantInsight,
                onDismiss = onDismissInsight
            )
        }
    }
}

// ... ActiveOrbitBanner, GreetingSection, SOSButton, DailyReflectionCard, WeeklyVitalsSection, StatPill, ActionGrid ... (Keep existing)

@Composable
private fun ActiveOrbitBanner(orbitState: OrbitViewModel.OrbitState, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B).copy(alpha = 0.8f)), // Deep slate
        shape = RoundedCornerShape(24.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.5f)), // Glowy border
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Animated Pulse Icon
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = orbitState.progress,
                    color = Color(0xFF38BDF8),
                    trackColor = Color.White.copy(alpha = 0.1f),
                    modifier = Modifier.size(56.dp),
                    strokeWidth = 4.dp
                )
                Icon(
                    imageVector = Icons.Rounded.HourglassEmpty,
                    contentDescription = null,
                    tint = Color(0xFF38BDF8),
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(20.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Orbit Active",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFF1F5F9),
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${orbitState.timeReclaimed} reclaimed",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Light,
                    color = Color(0xFF38BDF8)
                )
            }

            Icon(
                imageVector = Icons.Rounded.ChevronRight,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
private fun GreetingSection(name: String, textColor: Color, highlightColor: Color) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Good Morning,",
            style = MaterialTheme.typography.bodyLarge,
            color = textColor
        )
        Text(
            text = "How are things with $name?",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.SemiBold
            ),
            color = highlightColor
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
            imageVector = Icons.Rounded.Spa,
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
    onClick: () -> Unit,
    bgColor: Color,
    textColor: Color,
    secondaryTextColor: Color
) {
    if (reflection == null) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .clip(RoundedCornerShape(24.dp))
                .border(
                    width = 2.dp,
                    color = LumiPrimary.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(24.dp),
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
                    color = secondaryTextColor // Keep this readable
                )
            }
        }
    } else {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() },
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = bgColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Row(
                modifier = Modifier.padding(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = getMoodEmoji(reflection.mood),
                    fontSize = 42.sp
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "Today's Reflection",
                        style = MaterialTheme.typography.labelMedium,
                        color = secondaryTextColor
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (reflection.note.isNullOrBlank()) "No notes added." else reflection.note,
                        style = MaterialTheme.typography.bodyMedium,
                        color = textColor,
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
    onOpenTimeline: () -> Unit,
    textColor: Color,
    cardBgColor: Color
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
                color = textColor
            )
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
            colors = CardDefaults.cardColors(containerColor = cardBgColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            modifier = Modifier.border(1.dp, if(cardBgColor == LumiSurface) Color.Black.copy(0.05f) else Color.Transparent, RoundedCornerShape(24.dp))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                SmoothMoodGraph(
                    moodPoints = weeklyTrend.moodPoints,
                    modifier = Modifier.fillMaxWidth().height(100.dp)
                )
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatPill(weeklyTrend.positiveCount, "Positive", PositiveGreen, textColor)
                    StatPill(weeklyTrend.negativeCount, "Drained", NegativeRed, textColor)
                }
            }
        }
    }
}

@Composable
private fun StatPill(count: Int, label: String, color: Color, textColor: Color) {
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
                color = if(textColor == TextLight) color else TextPrimary // Make stats pop in dark mode
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = textColor.copy(alpha = 0.7f))
    }
}

@Composable
private fun ActionGrid(
    onLogInteraction: () -> Unit,
    onOpenInsights: () -> Unit,
    isLocked: Boolean,
    textColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ActionButton(
            title = if (isLocked) "In Orbit\n(Locked)" else "Log\nInteraction",
            icon = if (isLocked) Icons.Rounded.Lock else Icons.Default.Add,
            color = if (isLocked) Color.Gray else LumiPrimary,
            onClick = onLogInteraction,
            modifier = Modifier.weight(1f),
            enabled = !isLocked,
            textColor = textColor
        )

        ActionButton(
            title = "Weekly\nInsights",
            icon = Icons.Rounded.Insights,
            color = LumiSecondary,
            onClick = onOpenInsights,
            modifier = Modifier.weight(1f),
            textColor = textColor
        )
    }
}

@Composable
private fun SanctuaryToolsSection(
    orbitState: OrbitViewModel.OrbitState?,
    headerColor: Color,
    onOpenVoid: () -> Unit,
    onOpenOrbit: () -> Unit,
    onOpenScripts: () -> Unit,
    onOpenAnchor: () -> Unit // ⬅ NEW Parameter
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = "Sanctuary Tools",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = headerColor
        )

        // Row 1: The "Release" Tools (Void + Anchor)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ToolsCard(
                title = "The Void",
                subtitle = "Vent safely",
                icon = Icons.AutoMirrored.Rounded.Send,
                color = VoidColor,
                onClick = onOpenVoid,
                modifier = Modifier.weight(1f)
            )

            ToolsCard(
                title = "The Anchor",
                subtitle = "Your reality check",
                icon = Icons.Rounded.Anchor,
                color = AnchorColor, // ⬅ Warm Gold
                onClick = onOpenAnchor,
                modifier = Modifier.weight(1f)
            )
        }

        // Row 2: Maintenance Tools (Orbit + Scripts)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Orbit Card
            if (orbitState != null && (orbitState.isActive || orbitState.isCompleted)) {
                val progressColor = if(orbitState.isCompleted) Color(0xFFFFD700) else Color(0xFF38BDF8)
                val isCompleted = orbitState.isCompleted

                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(110.dp)
                        .clickable { onOpenOrbit() },
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = OrbitColor),
                    elevation = CardDefaults.cardElevation(4.dp),
                    border = if (isCompleted) androidx.compose.foundation.BorderStroke(2.dp, progressColor) else null
                ) {
                    Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                        Column(modifier = Modifier.align(Alignment.BottomStart)) {
                            Text(
                                text = "Orbit",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = if(isCompleted) "Completed" else "In Progress",
                                style = MaterialTheme.typography.bodyMedium,
                                color = progressColor,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Box(
                            modifier = Modifier.align(Alignment.TopEnd).size(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                progress = orbitState.progress,
                                color = progressColor,
                                trackColor = Color.White.copy(0.1f),
                                strokeWidth = 3.dp,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            } else {
                ToolsCard(
                    title = "Detox\nOrbit",
                    subtitle = "Reclaim time",
                    icon = Icons.Rounded.HourglassEmpty,
                    color = OrbitColor,
                    onClick = onOpenOrbit,
                    modifier = Modifier.weight(1f)
                )
            }

            ToolsCard(
                title = "Safe\nScripts",
                subtitle = "Words for you",
                icon = Icons.Rounded.ChatBubbleOutline,
                color = ScriptColor,
                onClick = onOpenScripts,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

// ... ToolsCard, ActionButton, SmoothMoodGraph ... (Keep existing)
@Composable
private fun ToolsCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    layoutHorizontal: Boolean = false // This flag is effectively unused now as we have a 2x2 grid, but keeping signature safe
) {
    Card(
        modifier = modifier
            .height(110.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = color),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.align(Alignment.BottomStart)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    lineHeight = 20.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.7f),
                    maxLines = 1
                )
            }

            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.3f),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(32.dp)
            )
        }
    }
}

@Composable
private fun ActionButton(
    title: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    textColor: Color = TextPrimary
) {
    Card(
        modifier = modifier
            .height(100.dp)
            .clickable(enabled = enabled) { onClick() },
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
                tint = if (enabled) color else Color.Gray,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(28.dp)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (enabled) textColor else Color.Gray,
                modifier = Modifier.align(Alignment.BottomStart)
            )
        }
    }
}

@Composable
private fun SmoothMoodGraph(
    moodPoints: List<Int>,
    modifier: Modifier = Modifier
) {
    val brush = Brush.verticalGradient(
        colors = listOf(LumiPrimary.copy(alpha = 0.4f), LumiPrimary.copy(alpha = 0.0f))
    )

    Canvas(modifier = modifier) {
        if (moodPoints.isEmpty()) return@Canvas

        val w = size.width
        val h = size.height
        val spacing = w / (moodPoints.size - 1).coerceAtLeast(1)

        val path = Path()

        moodPoints.forEachIndexed { index, mood ->
            val x = index * spacing
            val y = h - ((mood - 1) / 4f) * h

            if (index == 0) {
                path.moveTo(x, y)
            } else {
                val prevX = (index - 1) * spacing
                val prevY = h - ((moodPoints[index - 1] - 1) / 4f) * h
                val controlX1 = prevX + (x - prevX) / 2
                val controlX2 = prevX + (x - prevX) / 2
                path.cubicTo(controlX1, prevY, controlX2, y, x, y)
            }
        }

        val fillPath = Path().apply {
            addPath(path)
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }

        drawPath(path = fillPath, brush = brush)
        drawPath(path = path, color = LumiPrimary, style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round))

        moodPoints.forEachIndexed { index, mood ->
            val x = index * spacing
            val y = h - ((mood - 1) / 4f) * h
            drawCircle(color = LumiSurface, radius = 6.dp.toPx(), center = Offset(x, y))
            drawCircle(color = LumiPrimary, radius = 4.dp.toPx(), center = Offset(x, y))
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
            ), OrbitViewModel.OrbitState(),
            {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}
        )
    }
}