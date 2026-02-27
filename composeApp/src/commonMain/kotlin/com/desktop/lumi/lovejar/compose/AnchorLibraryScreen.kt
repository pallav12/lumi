package com.desktop.lumi.lovejar.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Anchor
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.desktop.lumi.db.com.desktop.lumi.lovejar.AnchorEntry

// --- Slate & Sky Sanctuary Palette (Neutral & Calming) ---
private val BgGradientTop = Color(0xFFF1F5F9) // Slate 100
private val BgGradientBottom = Color(0xFFE2E8F0) // Slate 200
private val CardSurface = Color(0xFFFFFFFF)
private val TextDeep = Color(0xFF0F172A) // Deep Slate
private val TextMuted = Color(0xFF64748B) // Muted Slate
private val AccentSky = Color(0xFF38BDF8) // Calming Sky Blue
private val AccentBrand = Color(0xFF8E8CD8) // Lumi Lavender for primary CTA

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnchorLibraryScreen(
    entries: List<AnchorEntry>,
    randomEntry: AnchorEntry?,
    onPullRandom: () -> Unit,
    onAddClick: () -> Unit,
    onDismissRandom: () -> Unit,
    onDeleteEntry: (Long) -> Unit, // ⬅ Added delete callback
    onBack: () -> Unit
) {
    // Local state to handle tapping a specific card in the grid to view it larger
    var viewingEntry by remember { mutableStateOf<AnchorEntry?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(BgGradientTop, BgGradientBottom)
                )
            )
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Rounded.ArrowBack, "Back", tint = TextDeep)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = onAddClick,
                    containerColor = TextDeep,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(16.dp),
                    elevation = FloatingActionButtonDefaults.elevation(6.dp)
                ) {
                    Icon(Icons.Rounded.Add, "Add Evidence")
                }
            }
        ) { padding ->
            Box(modifier = Modifier.fillMaxSize().padding(padding)) {

                // The Scrapbook Grid
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Fixed(2),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 100.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalItemSpacing = 12.dp,
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Header Span
                    item(span = StaggeredGridItemSpan.FullLine) {
                        Column(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Replaced heart with a neutral, mature Anchor icon
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .background(AccentSky.copy(alpha = 0.15f), RoundedCornerShape(16.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Rounded.Anchor, null, tint = AccentSky, modifier = Modifier.size(32.dp))
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "The Anchor",
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = TextDeep
                            )
                            Text(
                                text = "Your proof of safety.",
                                style = MaterialTheme.typography.bodyLarge,
                                color = TextMuted,
                                modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
                            )

                            // Emergency Button
                            Button(
                                onClick = onPullRandom,
                                modifier = Modifier.fillMaxWidth().height(56.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = AccentBrand),
                                shape = RoundedCornerShape(16.dp),
                                enabled = entries.isNotEmpty(),
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                            ) {
                                Icon(Icons.Rounded.AutoAwesome, null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Pull a Reality Check", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }

                            if (entries.isEmpty()) {
                                Spacer(modifier = Modifier.height(48.dp))
                                Text(
                                    "Your wall is empty.\nAdd a screenshot or a safe memory.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextMuted,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 22.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }

                    // Grid Items
                    items(entries) { entry ->
                        AnchorGridCard(
                            entry = entry,
                            onClick = { viewingEntry = entry }
                        )
                    }
                }

                // Overlays
                val activeOverlayEntry = randomEntry ?: viewingEntry
                if (activeOverlayEntry != null) {
                    AnchorDetailOverlay(
                        entry = activeOverlayEntry,
                        isRealityCheck = randomEntry != null,
                        onDismiss = {
                            if (randomEntry != null) onDismissRandom()
                            else viewingEntry = null
                        },
                        onNext = if (randomEntry != null) onPullRandom else null,
                        onDelete = {
                            onDeleteEntry(activeOverlayEntry.id)
                            viewingEntry = null
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun AnchorGridCard(
    entry: AnchorEntry,
    onClick: () -> Unit
) {
    // Removed the "glassy" borders. Just clean, solid white cards with soft shadows.
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column {
            // Display image if it exists
            if (entry.imageUri != null) {
                AsyncImage(
                    model = entry.imageUri,
                    contentDescription = "Memory Screenshot",
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Display text if it exists
            if (entry.content.isNotBlank()) {
                Text(
                    text = entry.content,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = FontFamily.Serif,
                        lineHeight = 22.sp
                    ),
                    color = TextDeep,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

@Composable
private fun AnchorDetailOverlay(
    entry: AnchorEntry,
    isRealityCheck: Boolean,
    onDismiss: () -> Unit,
    onNext: (() -> Unit)? = null,
    onDelete: () -> Unit
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f))
            .clickable { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .clickable(enabled = false) {},
            colors = CardDefaults.cardColors(containerColor = CardSurface),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Action Bar (Delete button for normal viewing mode)
                if (!isRealityCheck) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        IconButton(
                            onClick = { showDeleteConfirm = true },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                Icons.Rounded.DeleteOutline,
                                contentDescription = "Delete",
                                tint = Color.Red.copy(alpha = 0.6f)
                            )
                        }
                    }
                }

                if (isRealityCheck) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(AccentBrand.copy(alpha = 0.1f), RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Rounded.AutoAwesome,
                            null,
                            tint = AccentBrand,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "REALITY CHECK",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = AccentBrand,
                        letterSpacing = 2.sp
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }

                // Full Image View
                if (entry.imageUri != null) {
                    AsyncImage(
                        model = entry.imageUri,
                        contentDescription = null,
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }

                // Full Text View
                if (entry.content.isNotBlank()) {
                    Text(
                        text = "\"${entry.content}\"",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontFamily = FontFamily.Serif,
                            lineHeight = 32.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        color = TextDeep,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))

                if (isRealityCheck && onNext != null) {
                    // Reality Check Mode Actions
                    Button(
                        onClick = onNext,
                        colors = ButtonDefaults.buttonColors(containerColor = AccentBrand),
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        elevation = ButtonDefaults.buttonElevation(4.dp)
                    ) {
                        Icon(Icons.Rounded.Refresh, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Show me another ✨", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TextDeep.copy(alpha = 0.05f),
                            contentColor = TextDeep
                        ),
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        elevation = ButtonDefaults.buttonElevation(0.dp)
                    ) {
                        Text("I am grounded 🍃", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                } else {
                    // Normal View Mode Action
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = TextDeep),
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Close", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }
    }

    // Delete Confirmation Dialog
    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            containerColor = CardSurface,
            title = {
                Text("Delete Evidence?", color = TextDeep, fontWeight = FontWeight.Bold)
            },
            text = {
                Text("Are you sure you want to remove this from your Anchor? This cannot be undone.", color = TextMuted)
            },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteConfirm = false
                    onDelete()
                }) {
                    Text("Delete", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Cancel", color = TextMuted)
                }
            }
        )
    }
}