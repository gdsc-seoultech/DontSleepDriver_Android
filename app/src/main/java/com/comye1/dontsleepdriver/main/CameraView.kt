package com.comye1.dontsleepdriver.main

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.util.Size
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.comye1.dontsleepdriver.analyzer.SleepAnalyzer
import java.util.concurrent.Executors

@Composable
fun CameraView() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val extractedText = remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        DetectionView(
            context = context,
            lifecycleOwner = lifecycleOwner,
            extractedText = extractedText
        )
        Text(
            text = extractedText.value,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp)
        )
    }
}

@SuppressLint("UnsafeOptInUsageError", "RestrictedApi")
@Composable
fun DetectionView(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    extractedText: MutableState<String>
) {
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    var preview by remember { mutableStateOf<Preview?>(null) }
    val executor = ContextCompat.getMainExecutor(context)
    val cameraProvider = cameraProviderFuture.get()
//    val textRecognizer = remember { TextRecognition.getClient() }
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    Box {
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.7f),
            factory = { ctx ->
                val previewView = PreviewView(ctx)
                cameraProviderFuture.addListener({

//                    preview = ImageAnalysis.Builder()
//                        .setTargetResolution(Size(224, 224))

                    val analyzer = SleepAnalyzer(context) { extractedText.value = it }

                    Log.d("SleepAnalyzer", analyzer.toString())

                    val imageAnalysis = ImageAnalysis.Builder()
                        .setMaxResolution(Size(224, 224))
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()
//                        .also {
//                            it.setAnalyzer(executor, analyzer)
//                        }
                        .apply {
                            setAnalyzer(executor, analyzer)
                        }

                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            preview,
                            imageAnalysis
                        )

                    } catch (exception: Exception) {
                        Log.d("CameraView", "use case binding failed $exception")
                    }

                }, ContextCompat.getMainExecutor(ctx))

                preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }
                previewView
            }
        )
    }
}