package com.comye1.dontsleepdriver.analyzer

import android.content.Context
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@androidx.camera.core.ExperimentalGetImage
class SleepAnalyzer(context: Context, private val listener: (String) -> Unit) :
    ImageAnalysis.Analyzer {

    companion object {
        // High-accuracy landmark detection and face classification
        private val options = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
            .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .build()
        val detector = FaceDetection.getClient(options)
    }

    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            Log.d("facedetection", "running")

            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            // Pass image to an ML Kit Vision API
            // ...

            val result = detector.process(image)
                .addOnSuccessListener { faces ->
                    faces.forEach {
                        listener("${it.leftEyeOpenProbability} ${it.rightEyeOpenProbability}")
                        Log.d(
                            "facedetection",
                            "${it.leftEyeOpenProbability} ${it.rightEyeOpenProbability}"
                        )
                    }

                }
                .addOnFailureListener { e ->
                    Log.d("facedetection", e.message.toString())
                }
                .addOnCompleteListener {
                    CoroutineScope(Dispatchers.IO).launch {
                        delay(5000)
                        imageProxy.close()
                    }
                }
        }
    }
}

