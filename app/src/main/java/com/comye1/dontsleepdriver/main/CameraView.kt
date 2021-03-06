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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.comye1.dontsleepdriver.analyzer.SleepAnalyzer

@Composable
fun CameraView(addEyeState: (Int) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    DetectionView(
        context = context,
        lifecycleOwner = lifecycleOwner,
        sleepListener = {
            if (it) {
                addEyeState(1)
            } else {
                addEyeState(0)
            }
        }
    )

}

@SuppressLint("UnsafeOptInUsageError", "RestrictedApi")
@Composable
fun DetectionView(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    sleepListener: (Boolean) -> Unit
) {
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    var preview by remember { mutableStateOf<Preview?>(null) }
    val executor = ContextCompat.getMainExecutor(context)
    val cameraProvider = cameraProviderFuture.get()

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

                    val analyzer = SleepAnalyzer {
                        // ?????? ??? ?????????..
                        // true -> 1 ?????? ??? ?????? false -> 0 ?????????
                        sleepListener(it)
                    }

                    Log.d("SleepAnalyzer", analyzer.toString())

                    val imageAnalysis = ImageAnalysis.Builder()
                        .setMaxResolution(Size(480, 360))
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()
                        .apply {
                            setAnalyzer(executor, analyzer)
                        }

                    val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

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