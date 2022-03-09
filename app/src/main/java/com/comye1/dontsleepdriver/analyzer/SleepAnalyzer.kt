package com.comye1.dontsleepdriver.analyzer

import android.content.Context
import android.graphics.*
import android.media.Image
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.comye1.dontsleepdriver.ml.SleepModel
import com.google.mlkit.vision.common.InputImage
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import java.io.ByteArrayOutputStream


@androidx.camera.core.ExperimentalGetImage
class SleepAnalyzer(context: Context, private val listener: (String) -> Unit) :
    ImageAnalysis.Analyzer {

    private val model: SleepModel by lazy {
        SleepModel.newInstance(context)
    }

    override fun analyze(image: ImageProxy) {
        Log.d("SleepAnalyzer", "image : ${image.image.toString()}")
        if (image.image != null) {

//            val input = TensorBuffer.createFixedSize(intArrayOf(1,320,320,3), DataType.FLOAT32)
//            input.loadBuffer(TensorImage.fromBitmap(image.image!!.toBitmap()).buffer)


            var tfImage = TensorImage(DataType.FLOAT32)
            tfImage.load(image.image!!.toBitmap())
            tfImage = imageProcessor.process(tfImage) // image resize
//            Log.d("SleepAnalyzer", tfImage.tensorBuffer.shape.toString())

//            val output = model.process(tfImage)

//            Log.d("SleepAnalyzer", "model : ${model is SleepModel}")

//            Log.d("SleepAnalyzer", output.toString())

//            listener(output.toString())

//            listener(tfImage.tensorBuffer.intArray.size.toString())
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

    // image resize processor
    private val imageProcessor =
        ImageProcessor.Builder()
            .add(ResizeOp(320, 320, ResizeOp.ResizeMethod.BILINEAR))
            .build()
}

