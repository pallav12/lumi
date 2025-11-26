package com.desktop.lumi.instantmirror

import com.desktop.lumi.domain.model.Interaction
import com.desktop.lumi.home.presentation.InteractionType
import com.desktop.lumi.home.presentation.MoodEffect
import com.desktop.lumi.home.presentation.toMoodEffectOrSame
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Clock.System
import kotlin.time.ExperimentalTime

class InsightEngine {

    // Thresholds
    private val MIN_SAMPLE_SIZE = 3
    private val SPIRAL_WINDOW_MINUTES = 90
    private val LATE_NIGHT_START = 22 // 10 PM
    private val LATE_NIGHT_END = 5    // 5 AM

    /**
     * Main entry point.
     * @param current: The interaction just logged (to ensure relevance).
     * @param history: The last ~20 interactions from DB.
     */
    fun analyze(current: InteractionType, history: List<Interaction>): PatternMatch {
        // 1. Run all analyzers
        val patterns = listOf(
            checkSpiral(current, history),
            checkSpecificTrend(current, history),
            checkTimeContext(current, history),
            checkContrast(current, history)
        )

        // 2. Return the highest priority pattern found
        return patterns
            .filter { it !is PatternMatch.NoPattern }
            .maxByOrNull { it.priority }
            ?: PatternMatch.NoPattern
    }

    // --- ANALYZER 1: The Spiral Detector (Anxiety Check) ---
    @OptIn(ExperimentalTime::class)
    private fun checkSpiral(current: InteractionType, history: List<Interaction>): PatternMatch {

        val now = System.now().toEpochMilliseconds()

        // Filter logs within the spiral window (e.g., last 90 mins)
        val recentLogs = history.filter {
            val logTime = Instant.fromEpochMilliseconds(it.timestamp)
            (now - logTime.epochSeconds) < (SPIRAL_WINDOW_MINUTES * 60)
        }

        // If user logged 3+ times quickly and mood is NOT getting better
        if (recentLogs.size >= 3) {
            val isNegativeSpiral = recentLogs.any {
                it.moodEffect.toMoodEffectOrSame() == MoodEffect.Worse
            }

            if (isNegativeSpiral) {
                return PatternMatch.SpiralDetected(
                    count = recentLogs.size,
                    durationMinutes = SPIRAL_WINDOW_MINUTES
                )
            }
        }
        return PatternMatch.NoPattern
    }

    // --- ANALYZER 2: Specific Trend (Texting = Bad?) ---
    private fun checkSpecificTrend(current: InteractionType, history: List<Interaction>): PatternMatch {
        val type = current

        // Get last N interactions of THIS type
        val sameTypeHistory = history
            .filter { InteractionType.valueOf(it.type) == type }
            .take(5) // Look at last 5 only for recency relevance

        if (sameTypeHistory.size < MIN_SAMPLE_SIZE) return PatternMatch.NoPattern

        val negatives = sameTypeHistory.count { it.moodEffect.toMoodEffectOrSame() == MoodEffect.Worse }
        val positives = sameTypeHistory.count { it.moodEffect.toMoodEffectOrSame() == MoodEffect.Better }

        return when {
            negatives >= 3 -> PatternMatch.TrendFound(type, TrendDirection.NEGATIVE, negatives)
            positives >= 3 -> PatternMatch.TrendFound(type, TrendDirection.POSITIVE, positives)
            else -> PatternMatch.NoPattern
        }
    }

    // --- ANALYZER 3: Time Context (Local Time Aware) ---
    @OptIn(ExperimentalTime::class)
    private fun checkTimeContext(current: InteractionType, history: List<Interaction>): PatternMatch {
        val tz = TimeZone.currentSystemDefault()

        fun isLateNight(ts: Long): Boolean {
            val hour = Instant.fromEpochMilliseconds(ts).toLocalDateTime(tz).hour
            return hour >= LATE_NIGHT_START || hour < LATE_NIGHT_END
        }
        val currentTimeStamp = System.now().toEpochMilliseconds()

        if (!isLateNight(currentTimeStamp)) return PatternMatch.NoPattern

        // Check history for other late night logs
        val lateNightHistory = history.filter { isLateNight(it.timestamp) }

        if (lateNightHistory.size < MIN_SAMPLE_SIZE) return PatternMatch.NoPattern

        val negatives = lateNightHistory.count { it.moodEffect.toMoodEffectOrSame() == MoodEffect.Worse }

        if (negatives >= 3) {
            return PatternMatch.TimeContextFound("Late Night", TrendDirection.NEGATIVE, negatives)
        }
        return PatternMatch.NoPattern
    }

    // --- ANALYZER 4: Contrast (Call vs Text) ---
    private fun checkContrast(current: InteractionType, history: List<Interaction>): PatternMatch {
        // Only run contrast check if we have enough data overall
        if (history.size < 10) return PatternMatch.NoPattern

        // Calculate "Score" for types (Better=1, Same=0, Worse=-1)
        fun getScore(type: InteractionType): Double? {
            val logs = history.filter { InteractionType.valueOf(it.type) == type }
            if (logs.size < 3) return null // Not enough data

            val totalScore = logs.sumOf {
                when(it.moodEffect.toMoodEffectOrSame()) {
                    MoodEffect.Better -> 1.0
                    MoodEffect.Same -> 0.0
                    MoodEffect.Worse -> -1.0
                }
            }
            return totalScore / logs.size
        }

        val textScore = getScore(InteractionType.Text)
        val callScore = getScore(InteractionType.Call)
        val meetScore = getScore(InteractionType.Meet)

        // Logic: Check if one is significantly better than the current one
        // E.g., User just Texted (and felt bad), but Calls usually make them feel good.
        val currentType = current
        val currentScore = getScore(currentType) ?: return PatternMatch.NoPattern

        // Compare against others
        val comparisons = listOfNotNull(
            if (textScore != null) InteractionType.Text to textScore else null,
            if (callScore != null) InteractionType.Call to callScore else null,
            if (meetScore != null) InteractionType.Meet to meetScore else null
        )

        // Find a type that is significantly better (> 0.5 difference)
        val betterOption = comparisons.find { (type, score) ->
            type != currentType && score > (currentScore + 0.5)
        }

        if (betterOption != null) {
            return PatternMatch.ContrastFound(
                betterType = betterOption.first,
                worseType = currentType
            )
        }

        return PatternMatch.NoPattern
    }
}