package com.desktop.lumi.anchor.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.desktop.lumi.db.com.desktop.lumi.lovejar.AnchorViewModel
import com.desktop.lumi.rememberImagePicker

// Standard Lumi Palette
private val LumiBackground = Color(0xFFFAFAFA)
private val LumiSurface = Color(0xFFFFFFFF)
private val LumiPrimary = Color(0xFF8E8CD8)
private val TextPrimary = Color(0xFF2D2D39)
private val TextSecondary = Color(0xFF8A8A99)
private val AnchorGold = Color(0xFFD4A373)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAnchorScreen(
    state: AnchorViewModel.AddState,
    onContentChange: (String) -> Unit,
    onImagePicked: (String) -> Unit,
    onRemoveImage: () -> Unit,
    onSave: () -> Unit,
    onBack: () -> Unit
) {
    // Triggers the Native Image Picker
    val imagePicker = rememberImagePicker { uri -> onImagePicked(uri) }

    LaunchedEffect(state.isSaved) {
        if (state.isSaved) onBack()
    }

    Scaffold(
        containerColor = LumiBackground,
        topBar = {
            TopAppBar(
                title = { Text("New Evidence", color = TextPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, "Back", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = LumiBackground)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onSave,
                containerColor = AnchorGold,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Rounded.Check, "Save")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
        ) {

            Text(
                text = "Add a screenshot or write down a memory that proves you are safe.",
                color = TextSecondary,
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Text Input (Light Mode)
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = LumiSurface),
                elevation = CardDefaults.cardElevation(0.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.Black.copy(0.05f)),
                modifier = Modifier.fillMaxWidth().height(140.dp)
            ) {
                TextField(
                    value = state.content,
                    onValueChange = onContentChange,
                    modifier = Modifier.fillMaxSize(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    placeholder = {
                        Text(
                            "e.g., 'They brought me tea without asking.'",
                            color = TextSecondary.copy(alpha = 0.5f),
                            fontSize = 16.sp
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Image Attachment Area
            if (state.imageUri != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f) // Takes up remaining space gracefully
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.Black.copy(0.05f)) // subtle grey backdrop
                ) {
                    AsyncImage(
                        model = state.imageUri,
                        contentDescription = "Attached Evidence",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    // Remove Button
                    IconButton(
                        onClick = onRemoveImage,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(12.dp)
                            .background(Color.Black.copy(0.6f), RoundedCornerShape(50))
                            .size(36.dp)
                    ) {
                        Icon(Icons.Rounded.Close, "Remove", tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                }
            } else {
                // Gallery Button
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .clickable { imagePicker.pickImage() },
                    colors = CardDefaults.cardColors(containerColor = LumiSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, LumiPrimary.copy(alpha = 0.3f)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Rounded.AddPhotoAlternate, null, tint = LumiPrimary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Attach Screenshot", color = LumiPrimary, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}