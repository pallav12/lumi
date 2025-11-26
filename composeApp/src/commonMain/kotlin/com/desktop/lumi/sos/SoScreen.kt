package com.desktop.lumi.sos.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.desktop.lumi.db.com.desktop.lumi.sos.SosViewModel
import com.desktop.lumi.sos.presentation.components.BreathingCircle

@Composable
fun SosScreen(
    state: SosViewModel.SosStep,
    onBreathingDone: () -> Unit,
    onRealityCheckComplete: () -> Unit,
    onExit: () -> Unit
) {
    Scaffold(
        containerColor = Color(0xFFFAFAFA)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            AnimatedContent(targetState = state) { step ->
                when (step) {
                    SosViewModel.SosStep.BREATHING -> BreathingStep(onNext = onBreathingDone)
                    SosViewModel.SosStep.REALITY_CHECK -> RealityCheckStep(onNext = onRealityCheckComplete)
                    SosViewModel.SosStep.RESOLUTION -> ResolutionStep(onExit = onExit)
                }
            }
        }
    }
}

@Composable
fun BreathingStep(onNext: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            "Let's slow down.",
            style = MaterialTheme.typography.headlineMedium,
            color = Color(0xFF2D2D39),
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Follow the circle. Breathe in as it grows, out as it shrinks.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF8A8A99),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(48.dp))

        BreathingCircle()

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = onNext,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8E8CD8)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("I feel a bit calmer", fontSize = 16.sp)
        }
    }
}

@Composable
fun RealityCheckStep(onNext: () -> Unit) {
    val checks = listOf(
        "Are you reading into silence?" to "Silence is just silence. It rarely means they are upset. They might just be busy.",
        "Is your anxiety predicting the future?" to "Anxiety creates fake scenarios. Stick to facts: what is actually happening right now?",
        "Can you control their actions?" to "No. You can only control your reaction. Let go of the need to manage them."
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.verticalScroll(rememberScrollState())
    ) {
        Text(
            "Reality Check",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2D2D39)
        )
        Text(
            "Tap a card to reveal the rational truth.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF8A8A99),
            modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
        )

        checks.forEach { (question, truth) ->
            InteractiveRealityCard(question, truth)
            Spacer(modifier = Modifier.height(16.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onNext,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8E8CD8)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("I see clearly now", fontSize = 16.sp)
        }
    }
}

@Composable
private fun InteractiveRealityCard(question: String, truth: String) {
    var isRevealed by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(animationSpec = spring(stiffness = Spring.StiffnessMedium))
            .clickable { isRevealed = !isRevealed },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isRevealed) 0.dp else 4.dp),
        shape = RoundedCornerShape(20.dp),
        border = if(isRevealed) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF8E8CD8).copy(0.3f)) else null
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Question Icon
                Icon(
                    imageVector = Icons.Rounded.Info,
                    contentDescription = null,
                    tint = Color(0xFF8E8CD8),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = question,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF2D2D39)
                )
            }

            // The Reveal
            AnimatedVisibility(visible = isRevealed) {
                Column {
                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = Color(0xFFF0F0F0))
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(
                            imageVector = Icons.Rounded.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF98D8AA),
                            modifier = Modifier.size(20.dp).padding(top = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = truth,
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color(0xFF555566),
                            lineHeight = 22.sp
                        )
                    }
                }
            }

            // Hint text if hidden
            if (!isRevealed) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(
                        imageVector = Icons.Rounded.Visibility,
                        contentDescription = null,
                        tint = Color(0xFF8E8CD8).copy(alpha = 0.6f),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Tap to reveal truth",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFF8E8CD8).copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

@Composable
fun ResolutionStep(onExit: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            "You are safe.",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF98D8AA)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "You rode the wave. The feeling is temporary, but you are still here.",
            textAlign = TextAlign.Center,
            color = Color(0xFF8A8A99),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(48.dp))
        Button(
            onClick = onExit,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8E8CD8)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Return Home", fontSize = 16.sp)
        }
    }
}