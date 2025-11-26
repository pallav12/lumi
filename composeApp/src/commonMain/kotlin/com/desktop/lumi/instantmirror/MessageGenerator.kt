package com.desktop.lumi.instantmirror


object MessageGenerator {

    fun generateFor(match: PatternMatch): InstantInsight {
        return when (match) {
            is PatternMatch.SpiralDetected -> InstantInsight.GentleSupport(
                title = "Take a Breath",
                message = "You've logged ${match.count} times in the last hour. It feels like things are spiraling right now. Put the phone down for 5 minutes?"
            )
            is PatternMatch.TrendFound -> {
                val action = match.type.name.lowercase() // "text", "call"
                if (match.trend == TrendDirection.NEGATIVE) {
                    InstantInsight.NegativePattern(
                        message = "That’s the ${match.count}rd time recently that $action has left you feeling drained. Is this method of communication working for you?"
                    )
                } else {
                    InstantInsight.PositivePattern(
                        message = "You consistently feel better after a $action. This seems to be a safe connection point for you."
                    )
                }
            }
            is PatternMatch.TimeContextFound -> InstantInsight.TimePattern(
                message = "Late night interactions often leave you feeling worse. Our brains are tired and more prone to anxiety at this hour."
            )
            is PatternMatch.ContrastFound -> InstantInsight.ContrastPattern(
                message = "You tend to feel happier after ${match.betterType.name}s than ${match.worseType.name}s. Maybe try switching channels next time?"
            )
            is PatternMatch.NoPattern -> InstantInsight.GentleSupport(
                message = "Entry saved. I'm looking for patterns to help you navigate this better."
            )
        }
    }
}