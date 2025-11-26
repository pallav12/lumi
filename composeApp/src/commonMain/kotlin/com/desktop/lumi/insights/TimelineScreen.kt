package com.desktop.lumi.insights

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.ChatBubble
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.HistoryEdu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.desktop.lumi.home.presentation.InteractionType
import com.desktop.lumi.home.presentation.MoodEffect
import com.desktop.lumi.insights.models.TimelineItemUi
import kotlinx.datetime.*
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.time.ExperimentalTime
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlinx.datetime.minus
import kotlinx.datetime.DateTimeUnit

// --- Palette ---
private val LumiBackground = Color(0xFFFAFAFA)
private val LumiSurface = Color(0xFFFFFFFF)
private val LumiPrimary = Color(0xFF8E8CD8)
private val TextPrimary = Color(0xFF2D2D39)
private val TextSecondary = Color(0xFF8A8A99)
private val LineColor = Color(0xFFE0E0E0)

// Mood Colors
private val ColorBetter = Color(0xFF98D8AA)
private val ColorWorse = Color(0xFFFF9E9E)
private val ColorSame = Color(0xFFE0E0E0)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimelineScreen(
    items: List<TimelineItemUi>,
    onBack: () -> Unit
) {
    val groupedItems = groupItemsByDate(items)
    val sortedDates = groupedItems.keys.sortedDescending()

    Scaffold(
        containerColor = LumiBackground,
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = LumiBackground)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Header
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Text(
                    text = "Your Story",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold
                    ),
                    color = TextPrimary
                )
                Text(
                    text = "A timeline of moments and feelings.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (items.isEmpty()) {
                EmptyTimelineState()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 48.dp)
                ) {
                    sortedDates.forEach { date ->
                        val dateItems = groupedItems[date] ?: emptyList()

                        // Date Header
                        item {
                            DateHeader(date)
                        }

                        // Items
                        items(dateItems) { item ->
                            TimelineNode(item)
                        }
                    }

                    item {
                        TimelineEnd()
                    }
                }
            }
        }
    }
}

@Composable
private fun DateHeader(date: LocalDate) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = formatDateHeader(date).uppercase(),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = TextSecondary,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.width(16.dp))
        Divider(
            modifier = Modifier.weight(1f),
            color = LineColor
        )
    }
}

@Composable
private fun TimelineNode(item: TimelineItemUi) {
    // Determine visuals based on item type
    val isReflection = item is TimelineItemUi.ReflectionItem

    // Height calculation is tricky in lists, so we fake the line connectivity
    // by letting the line draw full height in the Row.

    IntrinsicHeightRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        // 1. The Timeline Track (Left)
        Column(
            modifier = Modifier.width(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Line
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(20.dp) // Space before node
                    .background(LineColor)
            )

            // The Node Icon
            if (isReflection) {
                val mood = (item as TimelineItemUi.ReflectionItem).mood
                Text(
                    text = getMoodEmoji(mood),
                    fontSize = 24.sp,
                    modifier = Modifier
                        .background(LumiBackground) // Hide line behind emoji
                        .padding(4.dp)
                )
            } else {
                val interaction = item as TimelineItemUi.InteractionItem
                val icon = getInteractionIcon(interaction.type)
                val color = getEffectColor(interaction.moodEffect)

                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(color.copy(alpha = 0.2f), CircleShape)
                        .border(1.dp, color, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Bottom Line (fills rest of height)
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .weight(1f)
                    .background(LineColor)
            )
        }

        // 2. The Content Card (Right)
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp, bottom = 24.dp, top = 12.dp) // Align top with node roughly
        ) {
            when (item) {
                is TimelineItemUi.ReflectionItem -> ReflectionContent(item)
                is TimelineItemUi.InteractionItem -> InteractionContent(item)
            }
        }
    }
}

@Composable
private fun ReflectionContent(item: TimelineItemUi.ReflectionItem) {
    Card(
        shape = RoundedCornerShape(
            topStart = 4.dp,
            bottomStart = 16.dp,
            topEnd = 16.dp,
            bottomEnd = 16.dp
        ),
        colors = CardDefaults.cardColors(containerColor = LumiSurface),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Reflection",
                style = MaterialTheme.typography.labelSmall,
                color = LumiPrimary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (item.note.isBlank()) "Logged mood without note." else item.note,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = FontFamily.Serif, // Journal feel
                    lineHeight = 22.sp
                ),
                color = TextPrimary
            )
        }
    }
}

@Composable
private fun InteractionContent(item: TimelineItemUi.InteractionItem) {
    val borderColor = getEffectColor(item.moodEffect).copy(alpha = 0.5f)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(LumiSurface, RoundedCornerShape(12.dp))
            .border(1.dp, LineColor, RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = getInteractionLabel(item.type),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            Text(
                text = formatTime(item.timestamp),
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )
        }

        // Effect Badge
        EffectBadge(item.moodEffect)
    }
}

@Composable
private fun EffectBadge(effect: MoodEffect) {
    val color = getEffectColor(effect)
    val text = when (effect) {
        MoodEffect.Better -> "Better"
        MoodEffect.Same -> "Neutral"
        MoodEffect.Worse -> "Worse"
    }

    Surface(
        color = color.copy(alpha = 0.15f),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = text,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = color.copy(alpha = 0.9f),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun TimelineEnd() {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.width(40.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(LineColor, CircleShape)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = "Start of your journey",
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary
        )
    }
}

@Composable
private fun EmptyTimelineState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Rounded.HistoryEdu,
                contentDescription = null,
                tint = LumiPrimary.copy(alpha = 0.5f),
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text("Your timeline is empty", color = TextSecondary)
        }
    }
}

@Composable
private fun IntrinsicHeightRow(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = modifier.height(IntrinsicSize.Min),
        content = content
    )
}

// Helpers
private fun getInteractionIcon(type: InteractionType): ImageVector {
    return when (type) {
        InteractionType.Text -> Icons.Rounded.ChatBubble
        InteractionType.Call -> Icons.Rounded.Call
        InteractionType.Meet -> Icons.Rounded.Groups
    }
}

private fun getEffectColor(effect: MoodEffect): Color {
    return when (effect) {
        MoodEffect.Better -> ColorBetter
        MoodEffect.Same -> ColorSame
        MoodEffect.Worse -> ColorWorse
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

private fun getInteractionLabel(type: InteractionType): String {
    return when (type) {
        InteractionType.Text -> "Texted"
        InteractionType.Call -> "Called"
        InteractionType.Meet -> "Met up"
    }
}

@OptIn(ExperimentalTime::class)
private fun formatDateHeader(date: LocalDate): String {
    // FIX: Use todayIn() directly instead of converting Instant -> LocalDateTime -> Date
    val timeZone = TimeZone.currentSystemDefault()
    val today = Clock.System.todayIn(timeZone)
    val yesterday = today.minus(1, DateTimeUnit.DAY)

    return when {
        date == today -> "Today"
        date == yesterday -> "Yesterday"
        else -> {
            val todayEpoch = today.toEpochDays()
            val dateEpoch = date.toEpochDays()
            val daysDiff = (todayEpoch - dateEpoch)

            when {
                daysDiff == 2 -> "2 days ago"
                daysDiff in 3..7 -> "$daysDiff days ago"
                // Simple formatting for KMP
                else -> "${date.month.name.lowercase().replaceFirstChar { it.uppercase() }} ${date.dayOfMonth}"
            }
        }
    }
}

@OptIn(ExperimentalTime::class)
private fun formatTime(timestamp: Long): String {
    val instant = Instant.fromEpochMilliseconds(timestamp)
    val localTime = instant.toLocalDateTime(TimeZone.currentSystemDefault()).time
    return "${localTime.hour}:${localTime.minute.toString().padStart(2, '0')}"
}

// Logic helpers (Assumed exist based on context)
@OptIn(ExperimentalTime::class)
private fun groupItemsByDate(items: List<TimelineItemUi>): Map<LocalDate, List<TimelineItemUi>> {
    return items.groupBy {
        Instant.fromEpochMilliseconds(it.timestamp)
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .date
    }
}

@Preview
@Composable
fun PreviewTimeline() {
    MaterialTheme {
        TimelineScreen(
            items = listOf(
                TimelineItemUi.ReflectionItem(1, 4, "Felt really heard today.", 10000),
                TimelineItemUi.InteractionItem(
                    2,
                    InteractionType.Call,
                    MoodEffect.Better,
                    0L
                ),
                TimelineItemUi.InteractionItem(
                    3,
                    InteractionType.Text,
                    MoodEffect.Worse,
                    0L,
                )
            ),
            onBack = {}
        )
    }
}