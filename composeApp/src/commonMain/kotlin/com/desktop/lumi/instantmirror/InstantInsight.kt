    package com.desktop.lumi.instantmirror

    sealed class InstantInsight {

        abstract val title: String
        abstract val message: String

        // ---------------------------------------------------------
        // 1. First-time logs (no pattern yet)
        // ---------------------------------------------------------
        data class FirstTime(
            override val title: String = "Thank you",
            override val message: String
        ) : InstantInsight()


        // ---------------------------------------------------------
        // 2. Clear negative pattern (3+ negative outcomes)
        // ---------------------------------------------------------
        data class NegativePattern(
            override val title: String = "I noticed something",
            override val message: String
        ) : InstantInsight()


        // ---------------------------------------------------------
        // 3. Clear positive pattern (3+ positive outcomes)
        // ---------------------------------------------------------
        data class PositivePattern(
            override val title: String = "A helpful pattern",
            override val message: String
        ) : InstantInsight()


        // ---------------------------------------------------------
        // 4. Interaction vs Reflection Contradictions
        // Example: Calls improve mood, texts worsen mood
        // ---------------------------------------------------------
        data class ContrastPattern(
            override val title: String = "An interesting contrast",
            override val message: String
        ) : InstantInsight()


        // ---------------------------------------------------------
        // 5. Time-based patterns (late-night vs morning)
        // ---------------------------------------------------------
        data class TimePattern(
            override val title: String = "A timing pattern",
            override val message: String
        ) : InstantInsight()


        // ---------------------------------------------------------
        // 6. Mood stability (not improving, or consistent)
        // ---------------------------------------------------------
        data class StabilityPattern(
            override val title: String = "A steady trend",
            override val message: String
        ) : InstantInsight()


        // ---------------------------------------------------------
        // 7. Encouragement (no insight but return emotional support)
        // ---------------------------------------------------------
        data class GentleSupport(
            override val title: String = "I’m here",
            override val message: String
        ) : InstantInsight()
    }
