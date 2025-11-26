package com.desktop.lumi.instantmirror

import com.desktop.lumi.home.presentation.InteractionType

/**
 * MessageGenerator turns PatternMatch into InstantInsight (human-friendly).
 * Keep templates short, gentle, non-judgmental.
 */
object MessageGenerator {

    fun generateFor(match: PatternMatch): InstantInsight {
        return when (match) {
            is PatternMatch.InteractionPattern -> generateForInteraction(match)
            is PatternMatch.TimePattern -> generateForTime(match)
            is PatternMatch.ContrastPattern -> generateForContrast(match)
            is PatternMatch.NoPattern -> InstantInsight.GentleSupport(
                message = "Thanks — I’m keeping track. With a few more logs I’ll spot patterns for you."
            )
        }
    }

    private fun generateForInteraction(p: PatternMatch.InteractionPattern): InstantInsight {
        val typeName = when (p.type) {
            InteractionType.Call -> "calls"
            InteractionType.Text -> "texts"
            InteractionType.Meet -> "in-person meetings"
//            InteractionType.Other -> "interactions"
        }

        return when {
            p.negativeCount >= 3 && p.negativeCount >= p.positiveCount -> {
                InstantInsight.NegativePattern(
                    message = "I noticed the last ${p.negativeCount} times you had $typeName, your mood dipped afterwards. It might help to pay attention to how those feel and maybe try something different next time."
                )
            }
            p.positiveCount >= 3 && p.positiveCount >= p.negativeCount -> {
                InstantInsight.PositivePattern(
                    message = "I noticed the last ${p.positiveCount} times you had $typeName, you felt better afterwards. Those interactions might be a reliable source of uplift for you."
                )
            }
            else -> InstantInsight.GentleSupport(
                message = "Thanks — I’m watching for patterns. A few more logs will help me give clearer insight."
            )
        }
    }

    private fun generateForTime(p: PatternMatch.TimePattern): InstantInsight {
        return when {
            p.negativeCount >= 3 && p.negativeCount >= p.positiveCount -> {
                InstantInsight.NegativePattern(
                    message = "I noticed several $ {p.windowDescription} interactions that left you feeling worse. Late hours can sometimes feel heavier — consider checking-in earlier in the day."
                )
            }
            p.positiveCount >= 3 && p.positiveCount >= p.negativeCount -> {
                InstantInsight.PositivePattern(
                    message = "You seem to feel better during ${p.windowDescription} interactions. That’s a useful pattern to know."
                )
            }
            else -> InstantInsight.GentleSupport(
                message = "I’m keeping an eye on timing patterns. More data will help me be specific."
            )
        }
    }

    private fun generateForContrast(p: PatternMatch.ContrastPattern): InstantInsight {
        val a = p.interactionTypeA
        val b = p.interactionTypeB
        val name = { t: InteractionType ->
            when (t) {
                InteractionType.Call -> "calls"
                InteractionType.Text -> "texts"
                InteractionType.Meet -> "in-person time"
//                InteractionType.Other -> "interactions"
            }
        }
        val msg = if (p.avgEffectA > p.avgEffectB) {
            "You tend to feel better after ${name(a)} than after ${name(b)}. That contrast might be worth trying more of."
        } else {
            "You tend to feel better after ${name(b)} than after ${name(a)}. That contrast could be important to notice."
        }
        return InstantInsight.ContrastPattern(message = msg)
    }
}
