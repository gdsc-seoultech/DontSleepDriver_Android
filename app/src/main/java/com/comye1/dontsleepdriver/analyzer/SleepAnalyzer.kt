package com.comye1.dontsleepdriver.analyzer

import android.content.Context
import android.graphics.*
import android.media.Image
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.comye1.dontsleepdriver.ml.SleepModel
import org.tensorflow.lite.support.image.TensorImage
import java.io.ByteArrayOutputStream


@androidx.camera.core.ExperimentalGetImage
class SleepAnalyzer(context: Context) : ImageAnalysis.Analyzer {

    private val model: SleepModel by lazy {
        SleepModel.newInstance(context)
    }

    override fun analyze(image: ImageProxy) {
        Log.d("SleepAnalyzer", "image : ${image.image}")
        if (image.image != null) {
            Log.d("SleepAnalyzer", "image : ${image.image}")
            val tfImage = TensorImage.fromBitmap(image.image!!.toBitmap())
//


//            Log.d("SleepAnalyzer", "model : ${model is SleepModel}")
            val output = model.process(tfImage.tensorBuffer)
            Log.d("SleepAnalyzer", output.toString())

            Log.d("SleepAnalyzer", tfImage.toString())

            image.close()
        }
    }

    private fun Image.toBitmap(): Bitmap {
        val yBuffer = planes[0].buffer // Y
        val vuBuffer = planes[2].buffer // VU

        val ySize = yBuffer.remaining()
        val vuSize = vuBuffer.remaining()

        val nv21 = ByteArray(ySize + vuSize)

        yBuffer.get(nv21, 0, ySize)
        vuBuffer.get(nv21, ySize, vuSize)

        val yuvImage = YuvImage(nv21, ImageFormat.NV21, this.width, this.height, null)
        val out = ByteArrayOutputStream()
        yuvImage.compressToJpeg(Rect(0, 0, yuvImage.width, yuvImage.height), 50, out)
        val imageBytes = out.toByteArray()
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }
}

