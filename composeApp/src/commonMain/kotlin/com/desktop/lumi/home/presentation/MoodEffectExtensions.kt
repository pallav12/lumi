package com.desktop.lumi.home.presentation

/**
 * Extension functions for MoodEffect enum to handle conversions to/from Int
 * This ensures consistent mapping across the codebase:
 * - Better -> 1
 * - Same -> 0
 * - Worse -> -1
 */

/**
 * Converts MoodEffect enum to its corresponding Int value
 */
fun MoodEffect.toInt(): Int = when (this) {
    MoodEffect.Better -> 1
    MoodEffect.Same -> 0
    MoodEffect.Worse -> -1
}

/**
 * Converts Int value to MoodEffect enum
 * Returns null if the Int value doesn't match any MoodEffect
 */
fun Int.toMoodEffect(): MoodEffect? = when (this) {
    1 -> MoodEffect.Better
    0 -> MoodEffect.Same
    -1 -> MoodEffect.Worse
    else -> null
}

/**
 * Converts Int value to MoodEffect enum with a default fallback
 * Returns MoodEffect.Same if the Int value doesn't match any MoodEffect
 */
fun Int.toMoodEffectOrSame(): MoodEffect = when (this) {
    1 -> MoodEffect.Better
    0 -> MoodEffect.Same
    -1 -> MoodEffect.Worse
    else -> MoodEffect.Same
}

