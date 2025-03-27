package com.steegler.koopusdemo

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.steegler.koopusdemo.audio.PcmPlayer
import com.steegler.koopusdemo.audio.PcmRecorder
import com.steegler.koopusdemo.ui.theme.KoOpusTheme

class MainActivity : ComponentActivity() {

    private val pcmRecorder = PcmRecorder()
    private val pcmPlayer = PcmPlayer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val context = this
            val audioPermissionGranted = remember { mutableStateOf(false) }

            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission()
            ) { granted ->
                audioPermissionGranted.value = granted
            }

            LaunchedEffect(Unit) {
                val permissionCheck = ContextCompat.checkSelfPermission(
                    context, Manifest.permission.RECORD_AUDIO
                )
                if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                    launcher.launch(Manifest.permission.RECORD_AUDIO)
                } else {
                    audioPermissionGranted.value = true
                }
            }

            if (audioPermissionGranted.value) {
                KoOpusTheme {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Welcome to KoOpus", style = MaterialTheme.typography.headlineMedium)
                            Spacer(modifier = Modifier.height(24.dp))

                            var isRecording by remember { mutableStateOf(false) }

                            Button(
                                onClick = {
                                    if (!isRecording) {
                                        pcmRecorder.start()
                                    } else {
                                        pcmRecorder.stop()
                                    }
                                    isRecording = !isRecording
                                }
                            ) {
                                Text(if (isRecording) "Stop Recording" else "Start Recording")
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    pcmPlayer.play(pcmRecorder.encodedChunks)
                                },
                                enabled = !isRecording && pcmRecorder.encodedChunks.isNotEmpty()
                            ) {
                                Text("Play")
                            }
                        }
                    }
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Microphone permission required")
                }
            }
        }
    }
}