package com.saver.statussaver.util

import android.content.ContentValues
import android.content.Context
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object FileUtils {

    fun copyFile(source: File, destination: File) {
        try {
            FileInputStream(source).use { input ->
                FileOutputStream(destination).use { output ->
                    input.copyTo(output)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            throw e
        }
    }

    fun deleteFile(file: File): Boolean {
        return try {
            file.delete()
        } catch (e: SecurityException) {
            e.printStackTrace()
            false
        }
    }

    fun scanFile(context: Context, file: File) {
        MediaScannerConnection.scanFile(
            context,
            arrayOf(file.absolutePath),
            null
        ) { _, _ -> }
    }

    fun saveToGallery(context: Context, sourceFile: File, isVideo: Boolean): Uri? {
        val filename = sourceFile.name
        val mimeType = if (isVideo) "video/*" else "image/*"
        val directory = if (isVideo) {
            Environment.DIRECTORY_MOVIES
        } else {
            Environment.DIRECTORY_PICTURES
        }

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
                put(MediaStore.MediaColumns.RELATIVE_PATH, directory)
            }

            val contentResolver = context.contentResolver
            val uri = if (isVideo) {
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            } else {
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            }

            try {
                contentResolver.insert(uri, contentValues)?.also { insertedUri ->
                    contentResolver.openOutputStream(insertedUri)?.use { outputStream ->
                        FileInputStream(sourceFile).use { inputStream ->
                            inputStream.copyTo(outputStream)
                        }
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                null
            }
        } else {
            val directory = Environment.getExternalStoragePublicDirectory(directory)
            val destinationFile = File(directory, filename)

            try {
                copyFile(sourceFile, destinationFile)
                scanFile(context, destinationFile)
                Uri.fromFile(destinationFile)
            } catch (e: IOException) {
                e.printStackTrace()
                null
            }
        }
    }

    fun generateUniqueFileName(originalName: String): String {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
            .format(Date())
        val extension = originalName.substringAfterLast(".", "")
        return "STATUS_$timestamp.$extension"
    }

    fun getFileSize(file: File): String {
        val size = file.length()
        return when {
            size < 1024 -> "$size B"
            size < 1024 * 1024 -> "${size / 1024} KB"
            else -> String.format("%.1f MB", size / (1024 * 1024f))
        }
    }

    fun isImageFile(file: File): Boolean {
        return file.name.lowercase(Locale.getDefault()).matches(Regex(".*\\.(jpg|jpeg|png|gif|webp)$"))
    }

    fun isVideoFile(file: File): Boolean {
        return file.name.lowercase(Locale.getDefault()).matches(Regex(".*\\.(mp4|3gp|mkv|webm)$"))
    }
}
