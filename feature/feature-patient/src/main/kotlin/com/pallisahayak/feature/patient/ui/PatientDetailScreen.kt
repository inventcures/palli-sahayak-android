package com.pallisahayak.feature.patient.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
    val insights by viewModel.insights.collectAsState()
    val insightsLoading by viewModel.insightsLoading.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }

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

            // Tab selector: Timeline | Insights
            item {
                Spacer(modifier = Modifier.height(8.dp))
                TabRow(selectedTabIndex = selectedTab) {
                    Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) {
                        Text(
                            "Timeline (${observations.size})",
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.labelLarge,
                        )
                    }
                    Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) {
                        Text(
                            "Insights (${insights.size})",
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.labelLarge,
                        )
                    }
                }
            }

            when (selectedTab) {
                0 -> {
                    items(observations, key = { it.observationId }) { obs ->
                        ObservationCard(obs)
                    }
                }
                1 -> {
                    if (insightsLoading) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        }
                    } else if (insights.isEmpty()) {
                        item {
                            Card(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = "No consolidated insights yet. Insights are generated as the system analyzes observation patterns over time.",
                                    modifier = Modifier.padding(16.dp),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    } else {
                        items(insights, key = { it.id }) { insight ->
                            InsightCard(insight)
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
private fun InsightCard(insight: com.pallisahayak.feature.patient.ConsolidatedInsight) {
    val typeColor = when (insight.insightType) {
        "trend" -> PrimaryGreen
        "correlation" -> SecondaryBlue
        "risk" -> com.pallisahayak.core.ui.theme.EvidenceD
        else -> PrimaryGreen
    }
    val typeLabel = when (insight.insightType) {
        "trend" -> "Trend"
        "correlation" -> "Correlation"
        "risk" -> "Risk Alert"
        else -> insight.insightType.replaceFirstChar { it.uppercase() }
    }
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = typeColor.copy(alpha = 0.15f),
                    shape = MaterialTheme.shapes.small,
                ) {
                    Text(
                        text = typeLabel,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = typeColor,
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = dateFormat.format(Date(insight.createdAt)),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = insight.insightText,
                style = MaterialTheme.typography.bodyMedium,
            )
            if (insight.sourceMemoryIds.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Based on ${insight.sourceMemoryIds.size} observations",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
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
