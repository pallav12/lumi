package com.desktop.lumi.widget

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.Button
import androidx.glance.ButtonDefaults
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.ImageProvider
import androidx.glance.action.actionStartActivity
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.desktop.lumi.MainActivity
import com.desktop.lumi.R

class LumiWidget : GlanceAppWidget() {

    // Use Single mode for consistent behavior
    override val sizeMode: SizeMode = SizeMode.Single

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlanceTheme {
                LumiWidgetContent()
            }
        }
    }

    @Composable
    fun LumiWidgetContent() {
        // Refined elegant color palette - Premium dark lavender theme
        // Text Colors with perfect contrast
        val textPrimary = ColorProvider(Color(0xFFFFFFFF))        // Pure white for maximum readability
        val textSecondary = ColorProvider(Color(0xFFE8E0F5))      // Soft lavender-white (95% opacity feel)
        val textTertiary = ColorProvider(Color(0xFFD4C4E9))       // Muted lavender for subtle elements

        // Premium Action Colors - Harmonious with lavender background
        val sosColor = ColorProvider(Color(0xFFEF5350))          // Vibrant coral-red (urgent but not harsh)
        val voidColor = ColorProvider(Color(0xFF9575CD))         // Rich amethyst purple (mystical, calming)
        val orbitColor = ColorProvider(Color(0xFF5C9ED6))        // Soft sky blue (peaceful, expansive)

        // Button Text Colors - Optimized for each button
        val sosButtonText = ColorProvider(Color(0xFFFFFFFF))       // White for SOS (high contrast)
        val voidButtonText = ColorProvider(Color(0xFFFFFFFF))     // White for Void
        val orbitButtonText = ColorProvider(Color(0xFFFFFFFF))   // White for Orbit

        // "Open Sanctuary" Button - Elegant glassmorphism effect
        val openAppBtnBg = ColorProvider(Color(0xFF3A2F4A))      // Deep plum-lavender (sophisticated)
        val openAppBtnText = ColorProvider(Color(0xFFE8DDF5))    // Soft lavender-white (elegant)

        // Main Container
        // Note: Glance modifiers are limited. We use a Box with a background ImageProvider
        // to simulate the gradient drawable you likely created.
        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(ImageProvider(R.drawable.widget_gradient_background)) // Ensure this drawable exists!
                .cornerRadius(24.dp) // Softer corners
        ) {
            Column(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Section
                Row(
                    modifier = GlanceModifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = GlanceModifier.defaultWeight(),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = "Lumi ✨",
                            style = TextStyle(
                                color = textPrimary,
                                fontSize = 21.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Spacer(modifier = GlanceModifier.height(5.dp))
                        Text(
                            text = "Your digital sanctuary.",
                            style = TextStyle(
                                color = textSecondary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }

                // Action Buttons - Grid Layout for better balance
                Row(
                    modifier = GlanceModifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // SOS Button - Most prominent (Emergency)
                    WidgetActionButton(
                        text = "SOS",
                        emoji = "🆘",
                        route = "sos",
                        color = sosColor,
                        contentColor = sosButtonText,
                        modifier = GlanceModifier.defaultWeight()
                    )

                    Spacer(modifier = GlanceModifier.width(12.dp))

                    // Void Button - Mystical release
                    WidgetActionButton(
                        text = "Void",
                        emoji = "☁️",
                        route = "void",
                        color = voidColor,
                        contentColor = voidButtonText,
                        modifier = GlanceModifier.defaultWeight()
                    )

                    Spacer(modifier = GlanceModifier.width(12.dp))

                    // Orbit Button - Peaceful space
                    WidgetActionButton(
                        text = "Orbit",
                        emoji = "🪐",
                        route = "orbit",
                        color = orbitColor,
                        contentColor = orbitButtonText,
                        modifier = GlanceModifier.defaultWeight()
                    )
                }

                Spacer(modifier = GlanceModifier.height(12.dp))

                // Quick Access to Main App - Subtle Pill
                Button(
                    text = "Open Sanctuary",
                    onClick = actionStartActivity<MainActivity>(),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = openAppBtnBg,
                        contentColor = openAppBtnText
                    ),
                    modifier = GlanceModifier
                        .fillMaxWidth()
                        .height(42.dp)
                        .cornerRadius(21.dp) // Fully rounded pill
                )
            }
        }
    }

    @Composable
    private fun WidgetActionButton(
        text: String,
        emoji: String,
        route: String,
        color: ColorProvider,
        contentColor: ColorProvider,
        modifier: GlanceModifier = GlanceModifier
    ) {
        // Create explicit Intent with proper flags for widget clicks
        val intent = Intent().apply {
            setClassName(
                "com.desktop.lumi",
                "com.desktop.lumi.MainActivity"
            )
            putExtra("navigation_route", route)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        Button(
            text = "$emoji\n$text",
            onClick = actionStartActivity(intent),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = color,
                contentColor = contentColor
            ),
            modifier = modifier
                .height(72.dp)
                .cornerRadius(16.dp)
        )
    }
}