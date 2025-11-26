package com.desktop.lumi.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.material.icons.rounded.AccessTime
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview

// --- Palette ---
private val LumiBackground = Color(0xFFFAFAFA)
private val LumiSurface = Color(0xFFFFFFFF)
private val LumiPrimary = Color(0xFF8E8CD8)
private val TextPrimary = Color(0xFF2D2D39)
private val TextSecondary = Color(0xFF8A8A99)
private val DividerColor = Color(0xFFF0F0F0)

@OptIn(ExperimentalMaterial3Api::class)
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
    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = LumiBackground,
        topBar = {
            TopAppBar(
                title = {},
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
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp)
        ) {

            // Header
            Text(
                text = "Settings",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold
                ),
                color = TextPrimary
            )
            Text(
                text = "Manage your preferences.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Section 1: The Relationship
            SettingsSectionTitle("Focus")
            SettingsCard {
                SettingsItem(
                    icon = Icons.Rounded.Person,
                    label = "Name",
                    value = personName,
                    onClick = onEditName
                )
                Divider(
                    color = DividerColor,
                    thickness = 1.dp,
                    modifier = Modifier.padding(start = 56.dp)
                )
                SettingsItem(
                    icon = Icons.Rounded.Favorite,
                    label = "Relationship Type",
                    value = relationshipType,
                    onClick = onEditRelationshipType
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Section 2: Routine
            SettingsSectionTitle("Routine")
            SettingsCard {
                SettingsSwitchItem(
                    icon = Icons.Rounded.Notifications,
                    label = "Notifications",
                    checked = notificationsEnabled,
                    onCheckedChange = onToggleNotifications
                )

                // Only show time picker if notifications are enabled (visual logic)
                if (notificationsEnabled) {
                    Divider(
                        color = DividerColor,
                        thickness = 1.dp,
                        modifier = Modifier.padding(start = 56.dp)
                    )
                    SettingsItem(
                        icon = Icons.Rounded.AccessTime,
                        label = "Daily Reminder",
                        value = reminderTime,
                        onClick = onEditReminderTime
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Section 3: Privacy (Visual Placeholder for MVP)
            SettingsSectionTitle("About")
            SettingsCard {
                SettingsItem(
                    icon = Icons.Rounded.Shield,
                    label = "Privacy & Data",
                    value = "Local Only",
                    onClick = { /* TODO: Open Privacy Policy */ }
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Version Footer
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(
                    text = "Lumi v1.0.0",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary.copy(alpha = 0.5f)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SettingsSectionTitle(title: String) {
    Text(
        text = title.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        color = TextSecondary,
        modifier = Modifier.padding(bottom = 12.dp, start = 8.dp)
    )
}

@Composable
private fun SettingsCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = LumiSurface),
        elevation = CardDefaults.cardElevation(0.dp), // Flat for modern look
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            content()
        }
    }
}

@Composable
private fun SettingsItem(
    icon: ImageVector,
    label: String,
    value: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = LumiPrimary,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        // Label
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = TextPrimary,
            modifier = Modifier.weight(1f)
        )

        // Value
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = TextSecondary,
            modifier = Modifier.padding(end = 8.dp)
        )

        // Chevron
        Icon(
            imageVector = Icons.Rounded.ChevronRight,
            contentDescription = null,
            tint = TextSecondary.copy(alpha = 0.4f),
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun SettingsSwitchItem(
    icon: ImageVector,
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (checked) LumiPrimary else TextSecondary.copy(alpha = 0.5f),
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = TextPrimary,
            modifier = Modifier.weight(1f)
        )

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = LumiPrimary,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color.Gray.copy(alpha = 0.3f),
                uncheckedBorderColor = Color.Transparent
            ),
            modifier = Modifier.scale(0.8f) // Make switch slightly smaller/cleaner
        )
    }
}

private fun Modifier.scale(scale: Float): Modifier = this.then(
    other = Modifier.graphicsLayer(scaleX = scale, scaleY = scale)
)

@Preview
@Composable
fun PreviewSettings() {
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