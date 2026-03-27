package com.pallisahayak.feature.settings.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pallisahayak.core.evaluation.SusQuestionnaire

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SusScreen(
    onBack: () -> Unit,
    onSubmit: (List<Int>) -> Unit,
) {
    val responses = remember { mutableStateListOf<Int>().apply { repeat(10) { add(0) } } }
    var submitted by remember { mutableStateOf(false) }
    var score by remember { mutableFloatStateOf(0f) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Usability Survey (SUS)") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
        ) {
            if (submitted) {
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = "Your SUS Score: ${score.toInt()}",
                    style = MaterialTheme.typography.displaySmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = SusQuestionnaire.getInterpretation(score),
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primary,
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Thank you for your feedback!",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            } else {
                Text(
                    text = "Please rate each statement from 1 (Strongly Disagree) to 5 (Strongly Agree)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Spacer(modifier = Modifier.height(16.dp))

                SusQuestionnaire.items.forEachIndexed { index, item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "${item.number}. ${item.text}",
                                style = MaterialTheme.typography.bodyLarge,
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                            ) {
                                (1..5).forEach { rating ->
                                    val selected = responses[index] == rating
                                    FilterChip(
                                        selected = selected,
                                        onClick = { responses[index] = rating },
                                        label = { Text("$rating") },
                                        modifier = Modifier.size(48.dp),
                                    )
                                }
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Text("Disagree", style = MaterialTheme.typography.labelSmall)
                                Text("Agree", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                val allAnswered = responses.all { it > 0 }

                Button(
                    onClick = {
                        score = SusQuestionnaire.calculateScore(responses.toList())
                        onSubmit(responses.toList())
                        submitted = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = allAnswered,
                ) {
                    Text("Submit", style = MaterialTheme.typography.labelLarge)
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
