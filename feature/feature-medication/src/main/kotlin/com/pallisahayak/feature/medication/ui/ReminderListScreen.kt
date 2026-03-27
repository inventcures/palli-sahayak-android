package com.pallisahayak.feature.medication.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pallisahayak.core.data.database.entity.MedicationReminderEntity
import com.pallisahayak.feature.medication.MedicationViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderListScreen(
    onBack: () -> Unit,
    onCreateReminder: () -> Unit,
    viewModel: MedicationViewModel = hiltViewModel(),
) {
    val reminders by viewModel.activeReminders.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Medication Reminders") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateReminder) {
                Icon(Icons.Default.Add, contentDescription = "Add reminder")
            }
        },
    ) { padding ->
        if (reminders.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "No active medication reminders",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                item { Spacer(modifier = Modifier.height(8.dp)) }
                items(reminders, key = { it.reminderId }) { reminder ->
                    ReminderCard(reminder)
                }
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
private fun ReminderCard(reminder: MedicationReminderEntity) {
    val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    val timeStr = dateFormat.format(Date(reminder.scheduledTime))

    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = if (reminder.patientConfirmed) Icons.Default.CheckCircle else Icons.Default.Schedule,
                contentDescription = null,
                tint = if (reminder.patientConfirmed) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(32.dp),
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${reminder.medicationName} ${reminder.dosage}",
                    style = MaterialTheme.typography.bodyLarge,
                )
                Text(
                    text = "Scheduled: $timeStr",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Surface(
                shape = MaterialTheme.shapes.small,
                color = when (reminder.callStatus) {
                    "confirmed" -> MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    "missed" -> MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
                    else -> MaterialTheme.colorScheme.surfaceVariant
                },
            ) {
                Text(
                    text = reminder.callStatus.replaceFirstChar { it.uppercase() },
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall,
                )
            }
        }
    }
}
