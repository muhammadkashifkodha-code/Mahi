package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.AlarmOn
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ReminderItem
import com.example.ui.theme.CyanDark
import com.example.viewmodel.MahiViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun RemindersScreen(viewModel: MahiViewModel) {
    val reminders by viewModel.reminders.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Info Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.NotificationsActive,
                        contentDescription = "Reminders Alert",
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Hoshiyar Reminders",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Text(
                            text = "Mahi aapko waqt par aawaz aur notification se yaad karwayega!",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (reminders.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Filled.Alarm,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Koi reminder set nahi hai",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Naya reminder banane ke liye + button dabayein",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(reminders, key = { it.id }) { reminder ->
                        ReminderCard(
                            reminder = reminder,
                            onToggle = { viewModel.toggleReminder(reminder) },
                            onDelete = { viewModel.deleteReminder(reminder) }
                        )
                    }
                }
            }
        }

        // Add Reminder FAB
        FloatingActionButton(
            onClick = { showAddDialog = true },
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("add_reminder_fab")
        ) {
            Icon(imageVector = Icons.Filled.Add, contentDescription = "Add Reminder")
        }
    }

    if (showAddDialog) {
        var title by remember { mutableStateOf("") }
        var quickTimeChoice by remember { mutableStateOf("1_HOUR") } // 30_MIN, 1_HOUR, 3_HOURS, TOMORROW
        var repeatChoice by remember { mutableStateOf("ONCE") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Naya Reminder Set Karein") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Kis cheez ki yaad-dihani?") },
                        placeholder = { Text("e.g. Namaz, Dawai lena, Meeting") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("reminder_title_input")
                    )

                    Text("Kab yaad dilana hai?", style = MaterialTheme.typography.labelMedium)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        FilterChip(
                            selected = quickTimeChoice == "30_MIN",
                            onClick = { quickTimeChoice = "30_MIN" },
                            label = { Text("30 Min", fontSize = 11.sp) }
                        )
                        FilterChip(
                            selected = quickTimeChoice == "1_HOUR",
                            onClick = { quickTimeChoice = "1_HOUR" },
                            label = { Text("1 Ghanta", fontSize = 11.sp) }
                        )
                        FilterChip(
                            selected = quickTimeChoice == "3_HOURS",
                            onClick = { quickTimeChoice = "3_HOURS" },
                            label = { Text("3 Ghante", fontSize = 11.sp) }
                        )
                        FilterChip(
                            selected = quickTimeChoice == "TOMORROW",
                            onClick = { quickTimeChoice = "TOMORROW" },
                            label = { Text("Kal Subah", fontSize = 11.sp) }
                        )
                    }

                    Text("Repeat:", style = MaterialTheme.typography.labelMedium)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("ONCE", "DAILY", "WEEKLY").forEach { rep ->
                            FilterChip(
                                selected = repeatChoice == rep,
                                onClick = { repeatChoice = rep },
                                label = { Text(rep, fontSize = 11.sp) }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            val now = System.currentTimeMillis()
                            val targetTime = when (quickTimeChoice) {
                                "30_MIN" -> now + 30 * 60 * 1000L
                                "1_HOUR" -> now + 60 * 60 * 1000L
                                "3_HOURS" -> now + 3 * 60 * 60 * 1000L
                                "TOMORROW" -> now + 24 * 60 * 60 * 1000L
                                else -> now + 60 * 60 * 1000L
                            }
                            viewModel.addReminder(title, targetTime, repeatChoice)
                            showAddDialog = false
                        }
                    },
                    modifier = Modifier.testTag("save_reminder_button")
                ) {
                    Text("Reminder Lagayein")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ReminderCard(
    reminder: ReminderItem,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    val dateStr = remember(reminder.dateTime) {
        SimpleDateFormat("EEE, dd MMM • hh:mm a", Locale.getDefault()).format(Date(reminder.dateTime))
    }

    val isPast = reminder.dateTime < System.currentTimeMillis()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("reminder_card_${reminder.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (reminder.isCompleted || isPast) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
            else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = if (!reminder.isCompleted && !isPast) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (!reminder.isCompleted && !isPast) Icons.Filled.AlarmOn else Icons.Filled.Alarm,
                        contentDescription = null,
                        tint = if (!reminder.isCompleted && !isPast) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = reminder.title,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = dateStr,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isPast && !reminder.isCompleted) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (reminder.repeatType != "ONCE") {
                    Text(
                        text = "Repeat: ${reminder.repeatType}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Switch(
                checked = !reminder.isCompleted && !isPast,
                onCheckedChange = { onToggle() },
                modifier = Modifier.testTag("toggle_reminder_${reminder.id}")
            )

            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Delete Reminder",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }
        }
    }
}
