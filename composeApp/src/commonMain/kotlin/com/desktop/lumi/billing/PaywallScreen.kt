package com.desktop.lumi.billing

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.getValue
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AllInclusive
import androidx.compose.material.icons.rounded.Anchor
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.ChatBubbleOutline
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.SaveAlt
import androidx.compose.material.icons.rounded.Spa
import androidx.compose.material.icons.rounded.Timeline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Paywall palette
private val PaywallBackground = Color(0xFFF8F5FF)
private val PaywallSurface = Color(0xFFFFFFFF)
private val PaywallText = Color(0xFF2D2D39)
private val PaywallTextSecondary = Color(0xFF8A8A99)
private val PaywallAccent = Color(0xFF9B7EDE)
private val PaywallGradientStart = Color(0xFF9B7EDE)
private val PaywallGradientEnd = Color(0xFF6B9FFF)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaywallScreen(
    billingState: BillingState,
    onPurchase: () -> Unit,
    onRestore: () -> Unit,
    onBack: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition()
    val breatheScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Scaffold(
        containerColor = PaywallBackground,
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, "Back", tint = PaywallText)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Breathing animated icon
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .scale(breatheScale)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(PaywallGradientStart, PaywallGradientEnd)
                        ),
                        shape = RoundedCornerShape(24.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Rounded.Spa,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Headline — calming, non-aggressive
            Text(
                "Take your healing deeper.",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold
                ),
                color = PaywallText,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                "Unlock unlimited memory storage, behavioral insights,\n" +
                        "and multiple tracking orbits with a single,\n" +
                        "one-time purchase. No subscriptions. Yours forever.",
                style = MaterialTheme.typography.bodyMedium,
                color = PaywallTextSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Feature list
            PremiumFeatureRow(
                icon = Icons.Rounded.Anchor,
                title = "Unlimited Anchors",
                subtitle = "Store unlimited memories and proof of your worth"
            )
            Spacer(modifier = Modifier.height(12.dp))
            PremiumFeatureRow(
                icon = Icons.Rounded.AutoAwesome,
                title = "Instant Mirror Insights",
                subtitle = "Behavioral analytics that reveal your patterns"
            )
            Spacer(modifier = Modifier.height(12.dp))
            PremiumFeatureRow(
                icon = Icons.Rounded.AllInclusive,
                title = "Advanced Detox Orbit",
                subtitle = "Multiple timers and historical streak log"
            )
            Spacer(modifier = Modifier.height(12.dp))
            PremiumFeatureRow(
                icon = Icons.Rounded.Timeline,
                title = "Lifetime Emotional History",
                subtitle = "View mood graphs for months and years"
            )
            Spacer(modifier = Modifier.height(12.dp))
            PremiumFeatureRow(
                icon = Icons.Rounded.ChatBubbleOutline,
                title = "Full Script Library",
                subtitle = "Every template for every situation"
            )
            Spacer(modifier = Modifier.height(12.dp))
            PremiumFeatureRow(
                icon = Icons.Rounded.Palette,
                title = "Sanctuary Themes",
                subtitle = "Midnight Void, Matcha, Ocean and more"
            )
            Spacer(modifier = Modifier.height(12.dp))
            PremiumFeatureRow(
                icon = Icons.Rounded.SaveAlt,
                title = "Secure Local Backup",
                subtitle = "Export and protect your data"
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Purchase button
            val displayPrice = billingState.priceString.ifEmpty { "$19.99" }
            Button(
                onClick = onPurchase,
                enabled = !billingState.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PaywallAccent,
                    disabledContainerColor = PaywallAccent.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(18.dp)
            ) {
                if (billingState.isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        "Unlock for $displayPrice — Lifetime",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            if (billingState.errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    billingState.errorMessage,
                    color = Color(0xFFE57373),
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(onClick = onRestore, enabled = !billingState.isLoading) {
                Text(
                    "Restore Purchase",
                    color = PaywallAccent,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun PremiumFeatureRow(
    icon: ImageVector,
    title: String,
    subtitle: String
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = PaywallSurface),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        color = PaywallAccent.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = PaywallAccent, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(title, fontWeight = FontWeight.SemiBold, color = PaywallText, fontSize = 15.sp)
                Text(subtitle, color = PaywallTextSecondary, fontSize = 13.sp)
            }
        }
    }
}
