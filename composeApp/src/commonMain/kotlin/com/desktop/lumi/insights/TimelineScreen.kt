package com.desktop.lumi.insights

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.desktop.lumi.insights.models.TimelineItemUi
import com.desktop.lumi.home.presentation.InteractionType
import com.desktop.lumi.home.presentation.MoodEffect
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.time.ExperimentalTime

private val SoftPink = Color(0xFFFFE5F1) // Very light pink
private val SoftBlue = Color(0xFFE5F0FF) // Very light blue
private val PrimarySoft = Color(0xFFB8A4D9) // Soft lavender/pastel purple
private val SoftGreen = Color(0xFFE8F5E8) // Very light green
private val SoftRed = Color(0xFFFFE5E5) // Very light red/pink
private val SoftGray = Color(0xFFF5F5F5) // Very light gray

// Mood emojis for reflections (1-5)
private fun getMoodEmoji(mood: Int): String {
    return when (mood) {
        1 -> "😢"
        2 -> "😔"
        3 -> "😐"
        4 -> "😊"
        5 -> "😍"
        else -> "😐"
    }
}

// Interaction type emojis
private fun getInteractionEmoji(type: InteractionType): String {
    return when (type) {
        InteractionType.Text -> "💬"
        InteractionType.Call -> "📞"
        InteractionType.Meet -> "🤝"
    }
}

// Interaction type labels
private fun getInteractionLabel(type: InteractionType): String {
    return when (type) {
        InteractionType.Text -> "Text message"
        InteractionType.Call -> "Call"
        InteractionType.Meet -> "Met in person"
    }
}

// Format date for display
private fun formatDateHeader(date: LocalDate): String {
    val today = date
    val yesterday = today.minus(1, DateTimeUnit.DAY)
    
    return when {
        date == today -> "Today"
        date == yesterday -> "Yesterday"
        else -> {
            // Calculate days difference for relative dates
            val todayEpoch = today.toEpochDays()
            val dateEpoch = date.toEpochDays()
            val daysDiff = (todayEpoch - dateEpoch).toInt()
            
            when {
                daysDiff == 2 -> "2 days ago"
                daysDiff in 3..7 -> "$daysDiff days ago"
                else -> "${date.monthNumber}/${date.dayOfMonth}/${date.year}"
            }
        }
    }
}

// Convert Long timestamp to LocalDate
@OptIn(ExperimentalTime::class)
private fun Long.toLocalDate(): LocalDate {
    val instant = Instant.fromEpochMilliseconds(this)
    val dateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    return dateTime.date
}

// Group items by date
private fun groupItemsByDate(items: List<TimelineItemUi>): Map<LocalDate, List<TimelineItemUi>> {
    return items.groupBy { item ->
        item.timestamp.toLocalDate()
    }
}

@Composable
fun TimelineScreen(
    items: List<TimelineItemUi>,
    onBack: () -> Unit
) {
    val groupedItems = groupItemsByDate(items)
    val sortedDates = groupedItems.keys.sortedDescending()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .padding(top = 48.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Text("←", fontSize = 24.sp, color = MaterialTheme.colorScheme.onSurface)
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Column {
                Text(
                    text = "Timeline",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Your emotional story, day by day.",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF777777)
                )
            }
        }

        // Content
        if (items.isEmpty()) {
            // Empty state
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "No entries yet",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Start reflecting to see your emotional timeline.",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                sortedDates.forEach { date ->
                    val dateItems = groupedItems[date] ?: emptyList()
                    
                    // Date header
                    item {
                        Text(
                            text = formatDateHeader(date),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF555555),
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    
                    // Items for this date
                    items(dateItems) { item ->
                        when (item) {
                            is TimelineItemUi.ReflectionItem -> {
                                ReflectionCard(reflection = item)
                            }
                            is TimelineItemUi.InteractionItem -> {
                                InteractionCard(interaction = item)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ReflectionCard(reflection: TimelineItemUi.ReflectionItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = PrimarySoft.copy(alpha = 0.08f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Mood emoji
            Text(
                text = getMoodEmoji(reflection.mood),
                fontSize = 32.sp,
                modifier = Modifier.padding(end = 16.dp)
            )
            
            // Content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                if (reflection.note.isNotEmpty()) {
                    Text(
                        text = reflection.note,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 22.sp
                    )
                } else {
                    Text(
                        text = "Reflection",
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Composable
private fun InteractionCard(interaction: TimelineItemUi.InteractionItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = PrimarySoft.copy(alpha = 0.08f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Interaction emoji
            Text(
                text = getInteractionEmoji(interaction.type),
                fontSize = 28.sp,
                modifier = Modifier.padding(end = 16.dp)
            )
            
            // Content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = getInteractionLabel(interaction.type),
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium
                )
            }
            
            // Mood effect chip
            Spacer(modifier = Modifier.width(8.dp))
            MoodEffectChip(moodEffect = interaction.moodEffect)
        }
    }
}

@Composable
private fun MoodEffectChip(moodEffect: MoodEffect) {
    val (text, backgroundColor) = when (moodEffect) {
        MoodEffect.Better -> "Better" to SoftGreen.copy(alpha = 0.6f)
        MoodEffect.Same -> "Same" to SoftGray.copy(alpha = 0.6f)
        MoodEffect.Worse -> "Worse" to SoftRed.copy(alpha = 0.6f)
    }
    
    Box(
        modifier = Modifier
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
        )
    }
}

@OptIn(ExperimentalTime::class)
@Preview
@Composable
fun PreviewTimelineScreen() {
    // Fake sample data for preview
    val now = kotlin.time.Clock.System.now()
    val nowMs = now.toEpochMilliseconds()
    val oneDayMs = 24 * 60 * 60 * 1000L
    
    val sampleItems = listOf(
        TimelineItemUi.ReflectionItem(
            id = 1L,
            mood = 4,
            note = "Had a nice call, felt closer today.",
            timestamp = nowMs - 3600000 // 1 hour ago
        ),
        TimelineItemUi.InteractionItem(
            id = 2L,
            timestamp = nowMs - 7200000, // 2 hours ago
            type = InteractionType.Call,
            moodEffect = MoodEffect.Better
        ),
        TimelineItemUi.ReflectionItem(
            id = 3L,
            mood = 2,
            note = "Felt distant, conversation was dry.",
            timestamp = nowMs - oneDayMs // Yesterday
        ),
        TimelineItemUi.InteractionItem(
            id = 4L,
            timestamp = nowMs - oneDayMs - 3600000, // Yesterday, 1 hour earlier
            type = InteractionType.Text,
            moodEffect = MoodEffect.Worse
        )
    )

    MaterialTheme {
        TimelineScreen(
            items = sampleItems,
            onBack = {}
        )
    }
}


