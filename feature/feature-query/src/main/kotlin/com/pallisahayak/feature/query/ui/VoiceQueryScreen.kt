package com.pallisahayak.feature.query.ui

import android.Manifest
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pallisahayak.core.model.query.EvidenceLevel
import com.pallisahayak.core.ui.theme.*
import com.pallisahayak.core.voice.AudioRecorder
import com.pallisahayak.feature.query.QueryPhase
import com.pallisahayak.feature.query.QueryViewModel

@Composable
fun VoiceQueryScreen(
    viewModel: QueryViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val amplitude by viewModel.amplitude.collectAsState()
    val recordingState by viewModel.recordingState.collectAsState()
    val isOnline by viewModel.isOnline.collectAsState()
    val context = LocalContext.current

    var hasPermission by remember { mutableStateOf(false) }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> hasPermission = granted }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Online/Offline indicator
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(if (isOnline) MaterialTheme.colorScheme.primary else Color(0xFF757575))
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Palli Sahayak",
                style = MaterialTheme.typography.labelLarge,
                color = Color.White,
            )
            Text(
                text = if (isOnline) "Online" else "Offline",
                style = MaterialTheme.typography.labelMedium,
                color = Color.White,
            )
        }

        // Emergency banner
        if (state.isEmergency) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.error,
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "EMERGENCY DETECTED",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:108"))
                            context.startActivity(intent)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                    ) {
                        Icon(Icons.Default.Call, contentDescription = null, tint = ErrorRed)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("CALL 108", color = ErrorRed, style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        }

        // Response area
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            when (state.phase) {
                QueryPhase.IDLE -> {
                    Spacer(modifier = Modifier.height(80.dp))
                    Text(
                        text = "Tap the microphone to ask a question",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                QueryPhase.RECORDING -> {
                    Spacer(modifier = Modifier.height(80.dp))
                    Text(
                        text = "Listening...",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    LinearProgressIndicator(
                        progress = { amplitude },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp),
                    )
                }

                QueryPhase.PROCESSING -> {
                    Spacer(modifier = Modifier.height(80.dp))
                    CircularProgressIndicator(modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Processing your question...", style = MaterialTheme.typography.bodyLarge)
                }

                QueryPhase.RESULT -> {
                    state.queryResult?.let { result ->
                        if (result.transcript != null) {
                            Text(
                                text = "You asked: \"${result.transcript}\"",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        // Evidence badge
                        EvidenceBadge(level = result.evidenceLevel)
                        Spacer(modifier = Modifier.height(12.dp))

                        // Answer
                        Text(
                            text = result.answer,
                            style = MaterialTheme.typography.bodyLarge,
                        )

                        if (state.isOfflineResult) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Offline response — connect for full results",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                        OutlinedButton(onClick = { viewModel.clearResult() }) {
                            Text("Ask another question")
                        }
                    }
                }
            }
        }

        // Voice button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp, top = 16.dp),
            contentAlignment = Alignment.Center,
        ) {
            val isRecording = recordingState == AudioRecorder.RecordingState.RECORDING
            val pulseScale by animateFloatAsState(
                targetValue = if (isRecording) 1.15f else 1f,
                animationSpec = if (isRecording) infiniteRepeatable(
                    animation = tween(600),
                    repeatMode = RepeatMode.Reverse,
                ) else tween(200),
                label = "pulse",
            )

            FloatingActionButton(
                onClick = {
                    if (isRecording) {
                        viewModel.stopRecording()
                    } else if (state.phase != QueryPhase.PROCESSING) {
                        viewModel.startRecording()
                    }
                },
                modifier = Modifier
                    .size(96.dp)
                    .scale(pulseScale),
                shape = CircleShape,
                containerColor = if (isRecording) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.primary,
            ) {
                Icon(
                    imageVector = if (isRecording) Icons.Default.Stop else Icons.Default.Mic,
                    contentDescription = if (isRecording) "Stop recording" else "Start recording",
                    modifier = Modifier.size(40.dp),
                    tint = Color.White,
                )
            }
        }

        // Error
        state.error?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            )
        }
    }
}

@Composable
private fun EvidenceBadge(level: EvidenceLevel) {
    val color = when (level) {
        EvidenceLevel.A -> EvidenceA
        EvidenceLevel.B -> EvidenceB
        EvidenceLevel.C -> EvidenceC
        EvidenceLevel.D -> EvidenceD
        EvidenceLevel.E -> EvidenceE
    }

    Surface(
        color = color.copy(alpha = 0.1f),
        shape = MaterialTheme.shapes.small,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                modifier = Modifier.size(12.dp),
                shape = CircleShape,
                color = color,
            ) {}
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Evidence: ${level.name} — ${level.label}",
                style = MaterialTheme.typography.labelMedium,
                color = color,
            )
        }
    }
}
