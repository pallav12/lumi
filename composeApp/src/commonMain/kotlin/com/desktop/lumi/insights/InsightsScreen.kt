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
import org.jetbrains.compose.ui.tooling.preview.Preview

private val SoftPink = Color(0xFFFFE5F1) // Very light pink
private val SoftBlue = Color(0xFFE5F0FF) // Very light blue
private val PrimarySoft = Color(0xFFB8A4D9) // Soft lavender/pastel purple
private val SoftGreen = Color(0xFFE8F5E8) // Very light green

@Composable
fun InsightsScreen(
    insights: List<String>,
    positiveCount: Int,
    negativeCount: Int,
    onBack: () -> Unit
) {
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
                    text = "Insights",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Your emotional patterns this week",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF777777)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Content
        if (insights.isEmpty()) {
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
                        text = "No insights yet",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Reflect for a few days to start seeing patterns",
                        fontSize = 14.sp,
                        color = Color(0xFF999999)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Weekly Summary Card
                item {
                    WeeklySummaryCard(
                        positiveCount = positiveCount,
                        negativeCount = negativeCount
                    )
                }

                // Insight Cards
                items(insights) { insight ->
                    InsightCard(insight = insight)
                }
            }
        }
    }
}

@Composable
private fun WeeklySummaryCard(
    positiveCount: Int,
    negativeCount: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = PrimarySoft.copy(alpha = 0.12f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "Weekly Overview",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Positive interactions
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "👍",
                    fontSize = 20.sp,
                    modifier = Modifier.padding(end = 12.dp)
                )
                Text(
                    text = "Positive interactions: $positiveCount",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Negative interactions
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "👎",
                    fontSize = 20.sp,
                    modifier = Modifier.padding(end = 12.dp)
                )
                Text(
                    text = "Negative interactions: $negativeCount",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun InsightCard(insight: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = PrimarySoft.copy(alpha = 0.08f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Light bulb indicator
            Text(
                text = "💡",
                fontSize = 24.sp,
                modifier = Modifier.padding(end = 16.dp)
            )
            
            // Insight text
            Text(
                text = insight,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 22.sp,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Preview
@Composable
fun PreviewInsightsScreen() {
    MaterialTheme {
        InsightsScreen(
            insights = listOf(
                "Calls brought you the most positivity this week.",
                "You tend to feel drained after late-night texting.",
                "Your mood was more stable on days you met in person.",
                "You had 3 emotionally uplifting moments this week."
            ),
            positiveCount = 8,
            negativeCount = 3,
            onBack = {}
        )
    }
}

@Preview
@Composable
fun PreviewInsightsScreenEmpty() {
    MaterialTheme {
        InsightsScreen(
            insights = emptyList(),
            positiveCount = 0,
            negativeCount = 0,
            onBack = {}
        )
    }
}

