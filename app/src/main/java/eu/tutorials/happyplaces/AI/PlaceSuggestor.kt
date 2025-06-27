package com.example.happyplaces.ai

import android.content.Context
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.tasks.await
import java.io.IOException

class PlaceSuggestor(context: Context) {
    private val recognizer: TextRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    private val appContext = context.applicationContext

    suspend fun getSuggestions(imageUri: Uri): List<String> {
        return try {
            val image = InputImage.fromFilePath(appContext, imageUri)
            val result = recognizer.process(image).await()

            result.text
                .split("\n")
                .filter { line ->
                    line.length > 10 &&
                            !line.matches(Regex(".*\\d+.*")) // Filter out lines with numbers
                }
                .take(3) // Get top 3 suggestions
        } catch (e: IOException) {
            emptyList()
        }
    }
}