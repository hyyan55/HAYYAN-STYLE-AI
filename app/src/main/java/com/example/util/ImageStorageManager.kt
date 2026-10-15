package com.example.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.UUID

object ImageStorageManager {

    suspend fun saveImageToInternalStorage(context: Context, uri: Uri): String = withContext(Dispatchers.IO) {
        val wardrobeDir = File(context.filesDir, "wardrobe_images")
        if (!wardrobeDir.exists()) {
            wardrobeDir.mkdirs()
        }

        val fileName = "item_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(6)}.jpg"
        val destinationFile = File(wardrobeDir, fileName)

        var inputStream: InputStream? = null
        var outputStream: FileOutputStream? = null

        try {
            inputStream = context.contentResolver.openInputStream(uri)
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
                ?: throw IllegalArgumentException("Could not decode image from uri")

            // Scale down if larger than 1200px to optimize storage and memory
            val maxDimension = 1200
            val width = originalBitmap.width
            val height = originalBitmap.height
            val scaledBitmap = if (width > maxDimension || height > maxDimension) {
                val ratio = width.toFloat() / height.toFloat()
                val targetW: Int
                val targetH: Int
                if (ratio > 1) {
                    targetW = maxDimension
                    targetH = (maxDimension / ratio).toInt()
                } else {
                    targetH = maxDimension
                    targetW = (maxDimension * ratio).toInt()
                }
                Bitmap.createScaledBitmap(originalBitmap, targetW, targetH, true)
            } else {
                originalBitmap
            }

            outputStream = FileOutputStream(destinationFile)
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
            destinationFile.absolutePath
        } finally {
            inputStream?.close()
            outputStream?.close()
        }
    }

    suspend fun deleteImageFromInternalStorage(filePath: String) = withContext(Dispatchers.IO) {
        if (filePath.isNotBlank() && !filePath.startsWith("res:")) {
            try {
                val file = File(filePath)
                if (file.exists()) {
                    file.delete()
                }
            } catch (_: Exception) {
            }
        }
    }
}
