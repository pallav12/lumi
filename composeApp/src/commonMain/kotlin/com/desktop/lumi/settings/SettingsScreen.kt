package com.desktop.lumi.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.ui.tooling.preview.Preview

private val SoftPink = Color(0xFFFFE5F1) // Very light pink
private val SoftBlue = Color(0xFFE5F0FF) // Very light blue
private val PrimarySoft = Color(0xFFB8A4D9) // Soft lavender/pastel purple

@Composable
fun SettingsScreen(
    personName: String,
    relationshipType: String,
    reminderTime: String,
    onEditName: () -> Unit,
    onEditRelationshipType: () -> Unit,
    onEditReminderTime: () -> Unit,
    onToggleNotifications: (Boolean) -> Unit,
    notificationsEnabled: Boolean,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .padding(top = 48.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Text("←", fontSize = 24.sp, color = MaterialTheme.colorScheme.onSurface)
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Settings",
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Content
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            // Person Information Section
            item {
                SettingsSection(
                    title = "Person",
                    items = {
                        SettingsRow(
                            label = "Name",
                            value = personName,
                            onClick = onEditName
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        SettingsRow(
                            label = "Relationship type",
                            value = relationshipType,
                            onClick = onEditRelationshipType
                        )
                    }
                )
            }

            // Reminders Section
            item {
                SettingsSection(
                    title = "Reminders",
                    items = {
                        SettingsRow(
                            label = "Daily reminder",
                            value = reminderTime,
                            onClick = onEditReminderTime
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        SettingsToggleRow(
                            label = "Notifications",
                            checked = notificationsEnabled,
                            onCheckedChange = onToggleNotifications
                        )
                    }
                )
            }

            // Bottom padding
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    items: @Composable () -> Unit
) {
    Column {
        // Section label
        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF777777),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Section items
        items()
    }
}

@Composable
private fun SettingsRow(
    label: String,
    value: String,
    onClick: () -> Unit,
    showValue: Boolean = true
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = PrimarySoft.copy(alpha = 0.08f),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (showValue && value.isNotEmpty()) "$label: $value" else label,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        // Chevron icon
        Text(
            text = ">",
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Composable
private fun SettingsToggleRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = PrimarySoft.copy(alpha = 0.08f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = PrimarySoft,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        )
    }
}

@Preview
@Composable
fun PreviewSettingsScreen() {
    MaterialTheme {
        SettingsScreen(
            personName = "Aditi",
            relationshipType = "Situationship",
            reminderTime = "08:30 PM",
            onEditName = {},
            onEditRelationshipType = {},
            onEditReminderTime = {},
            onToggleNotifications = {},
            notificationsEnabled = true,
            onBack = {}
        )
    }
}

