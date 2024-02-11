package com.dicoding.asclepius.helper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.SystemClock
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.common.ops.CastOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.task.vision.classifier.Classifications
import org.tensorflow.lite.task.vision.classifier.ImageClassifier
import java.io.IOException


class ImageClassifierHelper(
    private var threshold: Float = 0.1f,
    private var maxResults: Int = 3,
    private val modelName: String = "cancer_classification.tflite",
    val context: Context,
    val classifierListener: ClassifierListener?
) {
    private var imageClassifier: ImageClassifier? = null

    init {
        setupImageClassifier()
    }

    private fun setupImageClassifier() {
        val optionsBuilder = ImageClassifier.ImageClassifierOptions.builder()
            .setScoreThreshold(threshold)
            .setMaxResults(maxResults)

        try {
            imageClassifier = ImageClassifier.createFromFileAndOptions(
                context,
                modelName,
                optionsBuilder.build()
            )
        } catch (e: IOException) {
            classifierListener?.onError(
                "Failed to load the image classifier model.\nError: ${e.message}"
            )
        }
    }

    fun classifyStaticImage(imageUri: Uri) {
        if (imageClassifier == null) setupImageClassifier()

        try {
            val inputStream = context.contentResolver.openInputStream(imageUri)
            val bitmap = BitmapFactory.decodeStream(inputStream)

            val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true)
            val tensorImage = TensorImage.fromBitmap(resizedBitmap)

            val imageProcessor = ImageProcessor.Builder()
                .add(ResizeOp(224, 224, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))
                .add(CastOp(DataType.UINT8))
                .build()

            val processedImage = imageProcessor.process(tensorImage)
            var inferenceTime = SystemClock.uptimeMillis()
            val results = imageClassifier?.classify(processedImage)

            inferenceTime = SystemClock.uptimeMillis() - inferenceTime
            classifierListener?.onResults(results, inferenceTime)
        } catch (e: IOException) {
            classifierListener?.onError(
                "Failed to read the image from URI.\nError:${e.message}"
            )
        }
    }

    interface ClassifierListener {
        fun onError(error: String)
        fun onResults(results: List<Classifications>?, inferenceTime: Long)
    }
}