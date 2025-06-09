package com.lidar.projektam.viewmodel

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import java.io.File
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class ReceiptViewModel(application: Application) : AndroidViewModel(application) {

    private val _amount = mutableStateOf<String?>(null)
    val amount: State<String?> = _amount

    private val context = application.applicationContext
    private val outputDir = context.cacheDir
    private val executor = ContextCompat.getMainExecutor(context)

    val imageCapture = ImageCapture.Builder().build()

    fun takePhotoAndProcessOCR() {
        val photoFile = File(outputDir, "receipt_${System.currentTimeMillis()}.jpg")
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            executor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    try {
                        val image = InputImage.fromFilePath(context, Uri.fromFile(photoFile))
                        processImageWithOCR(image)
                    } catch (e: Exception) {
                        Log.e("OCR", "Photo processing error", e)
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e("Camera", "Saving photo error", exception)
                }
            }
        )
    }

    private fun processImageWithOCR(image: InputImage) {
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                val amountText = extractAmount(visionText.text)
                _amount.value = amountText
            }
            .addOnFailureListener {
                _amount.value = null
                Log.e("OCR", "OCR error", it)
            }
    }

    private fun extractAmount(text: String): String? {
        val regex = Regex("""(\d{1,3}(?:[.,\s]\d{3})*[.,]\d{2})""")
        return regex.findAll(text)
            .map { it.value.replace(",", ".").replace(" ", "") }
            .maxByOrNull { it.toDoubleOrNull() ?: 0.0 }
    }
}
