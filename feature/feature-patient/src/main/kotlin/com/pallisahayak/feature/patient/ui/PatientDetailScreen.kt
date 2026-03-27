package com.pallisahayak.feature.patient.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pallisahayak.core.data.database.entity.ObservationEntity
import com.pallisahayak.core.ui.theme.*
import com.pallisahayak.feature.patient.PatientViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientDetailScreen(
    onBack: () -> Unit,
    onRecordObservation: () -> Unit,
    viewModel: PatientViewModel = hiltViewModel(),
) {
    val patient by viewModel.patient.collectAsState()
    val observations by viewModel.observations.collectAsState()
    val careTeam by viewModel.careTeam.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(patient?.name ?: "Patient") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onRecordObservation) {
                Icon(Icons.Default.Add, contentDescription = "Record observation")
            }
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            patient?.let { p ->
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            p.primaryCondition?.let {
                                Text("Condition: $it", style = MaterialTheme.typography.bodyLarge)
                            }
                            p.conditionStage?.let {
                                Text("Stage: $it", style = MaterialTheme.typography.bodyMedium)
                            }
                            p.careLocation?.let {
                                Text("Location: $it", style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }

            if (careTeam.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Care Team", style = MaterialTheme.typography.headlineSmall)
                }
                items(careTeam) { member ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(modifier = Modifier.padding(12.dp)) {
                            Column {
                                Text(member.name, style = MaterialTheme.typography.bodyLarge)
                                Text(
                                    member.role.replaceFirstChar { it.uppercase() },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Observation Timeline (${observations.size})", style = MaterialTheme.typography.headlineSmall)
            }

            items(observations, key = { it.observationId }) { obs ->
                ObservationCard(obs)
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
private fun ObservationCard(observation: ObservationEntity) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
    val dateStr = dateFormat.format(Date(observation.timestamp))

    val categoryColor = when (observation.category) {
        "symptom" -> EvidenceD
        "medication" -> SecondaryBlue
        "vital_sign" -> PrimaryGreen
        "emotional" -> EvidenceC
        else -> PrimaryGreen
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.Top) {
            Surface(
                modifier = Modifier.size(8.dp, 40.dp),
                color = categoryColor,
                shape = MaterialTheme.shapes.extraSmall,
            ) {}
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = observation.entityName.replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.bodyLarge,
                )
                observation.valueText?.let {
                    Text(text = it, style = MaterialTheme.typography.bodyMedium)
                }
                observation.severity?.let { sev ->
                    val sevLabel = when (sev) { 0 -> "None"; 1 -> "Mild"; 2 -> "Moderate"; 3 -> "Severe"; 4 -> "Very Severe"; else -> "" }
                    Text(
                        text = "Severity: $sevLabel",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Text(
                    text = dateStr,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
