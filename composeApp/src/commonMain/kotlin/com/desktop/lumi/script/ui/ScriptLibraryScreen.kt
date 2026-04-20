package com.desktop.lumi.script.ui

import com.desktop.lumi.script.viewmodel.SafeScript
import com.desktop.lumi.script.viewmodel.ScriptCategory
import com.desktop.lumi.script.viewmodel.ScriptViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Sanctuary Palette (Consistent with App)
private val LumiBackground = Color(0xFFFAFAFA)
private val LumiPrimary = Color(0xFF8E8CD8)
private val LumiSurface = Color(0xFFFFFFFF)
private val TextPrimary = Color(0xFF2D2D39)
private val TextSecondary = Color(0xFF8A8A99)
private val ChipSelected = Color(0xFFE0DDF5)


@Preview
@Composable
fun PreviewScriptLibraryScreen() {
    ScriptLibraryScreen(
        state = ScriptViewModel.UiState(selectedCategory = ScriptCategory.IGNORED),
        onCategorySelect = {},
        onBack = {}
    )
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScriptLibraryScreen(
    state: ScriptViewModel.UiState,
    isPremium: Boolean = false,
    onCategorySelect: (ScriptCategory) -> Unit,
    onOpenPaywall: () -> Unit = {},
    onBack: () -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    var lastCopiedScript by remember { mutableStateOf<String?>(null) }

    Scaffold(
        containerColor = LumiBackground,
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = LumiBackground)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            // Header
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Text(
                    text = "Safe Scripts",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold
                    ),
                    color = TextPrimary
                )
                Text(
                    text = "Words for when you're stuck.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Category Chips (Horizontal Scroll)
            LazyRow(
                contentPadding = PaddingValues(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(ScriptCategory.values()) { category ->
                    CategoryChip(
                        category = category,
                        isSelected = state.selectedCategory == category,
                        onClick = { onCategorySelect(category) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Script List
            AnimatedContent(
                targetState = state.selectedCategory,
                transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(300)) }
            ) { category ->
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(state.visibleScripts) { script ->
                        val isFreeScript = script.title in ScriptViewModel.FREE_SCRIPT_TITLES
                        val isLocked = !isPremium && !isFreeScript

                        if (isLocked) {
                            LockedScriptCard(
                                script = script,
                                onTap = onOpenPaywall
                            )
                        } else {
                            ScriptCard(
                                script = script,
                                isCopied = lastCopiedScript == script.content,
                                onCopy = {
                                    clipboardManager.setText(AnnotatedString(script.content))
                                    lastCopiedScript = script.content
                                }
                            )
                        }
                    }

                    // Bottom padding
                    item { Spacer(modifier = Modifier.height(32.dp)) }
                }
            }
        }
    }
}

@Composable
private fun CategoryChip(
    category: ScriptCategory,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) LumiPrimary else Color.Transparent,
        border = if (isSelected) null else BorderStroke(1.dp, TextSecondary.copy(alpha = 0.3f))
    ) {
        Text(
            text = category.displayName,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            style = MaterialTheme.typography.labelLarge,
            color = if (isSelected) Color.White else TextSecondary,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
private fun ScriptCard(
    script: SafeScript,
    isCopied: Boolean,
    onCopy: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = LumiSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Tone Badge
                val toneColors = getToneColors(script.tone)
                Surface(
                    color = toneColors.container,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = script.tone.uppercase(),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = toneColors.content
                    )
                }

                // Copy Button
                IconButton(onClick = onCopy) {
                    if (isCopied) {
                        Text("Copied!", fontSize = 12.sp, color = LumiPrimary, fontWeight = FontWeight.Bold)
                    } else {
                        Icon(
                            imageVector = Icons.Rounded.ContentCopy,
                            contentDescription = "Copy",
                            tint = TextSecondary.copy(alpha = 0.6f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = script.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            // The Script Content
            Text(
                text = "\"${script.content}\"",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontFamily = FontFamily.Serif,
                    lineHeight = 24.sp
                ),
                color = TextPrimary.copy(alpha = 0.8f)
            )
        }
    }
}

private data class ToneColor(val container: Color, val content: Color)

@Composable
private fun LockedScriptCard(
    script: SafeScript,
    onTap: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = LumiSurface.copy(alpha = 0.6f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier.fillMaxWidth().clickable { onTap() }
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val toneColors = getToneColors(script.tone)
                Surface(
                    color = toneColors.container.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = script.tone.uppercase(),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = toneColors.content.copy(alpha = 0.5f)
                    )
                }
                Icon(
                    imageVector = Icons.Rounded.Lock,
                    contentDescription = "Premium",
                    tint = LumiPrimary.copy(alpha = 0.6f),
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = script.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary.copy(alpha = 0.4f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Unlock Premium to read this script",
                style = MaterialTheme.typography.bodyMedium,
                color = LumiPrimary.copy(alpha = 0.7f),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

private fun getToneColors(tone: String): ToneColor {
    return when (tone) {
        "Soft" -> ToneColor(
            container = Color(0xFFE8F5E9), // Minty Green
            content = Color(0xFF1B5E20)    // Dark Forest Green
        )
        "Direct" -> ToneColor(
            container = Color(0xFFE3F2FD), // Light Blue/Periwinkle
            content = Color(0xFF0D47A1)    // Deep Blue
        )
        "Vulnerable" -> ToneColor(
            container = Color(0xFFFCE4EC), // Soft Rose
            content = Color(0xFF880E4F)    // Deep Berry
        )
        else -> ToneColor(
            container = Color(0xFFF5F5F5),
            content = Color(0xFF424242)
        )
    }
}