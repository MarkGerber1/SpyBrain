package com.example.spybrain.presentation.biosync

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.spybrain.presentation.breathing.components.HeartBeatAnimation
import androidx.camera.view.PreviewView
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview

/**
 * @param viewModel ViewModel Р±РёРѕСЃРёРЅС…СЂРѕРЅРёР·Р°С†РёРё.
 */
@Composable
fun bioSyncScreen(
    viewModel: BioSyncViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val bpm by viewModel.bpm.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        // Camera preview
        AndroidView(
            factory = { ctx: Context ->
                PreviewView(ctx).apply {
                    val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()
                        val preview = Preview.Builder().build().also { previewObj ->
                            previewObj.setSurfaceProvider(surfaceProvider)
                        }
                        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                        // TODO: Fix after cameraProvider.bindToLifecycle signature clarified
                        // cameraProvider.bindToLifecycle(
                        //     lifecycleOwner, cameraSelector, preview
                        // )
                    }, ContextCompat.getMainExecutor(ctx))
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))
        // BPM visualization
        HeartBeatAnimation(bpm = bpm, size = 150.dp)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "BPM: $bpm", modifier = Modifier.padding(start = 16.dp))
    }
}
