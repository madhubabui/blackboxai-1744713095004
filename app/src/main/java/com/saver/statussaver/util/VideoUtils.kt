package com.saver.statussaver.util

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import java.io.File
import java.util.concurrent.TimeUnit

object VideoUtils {

    fun getVideoThumbnail(context: Context, videoFile: File): Bitmap? {
        return try {
            val retriever = MediaMetadataRetriever()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                retriever.setDataSource(context, Uri.fromFile(videoFile))
            } else {
                retriever.setDataSource(videoFile.absolutePath)
            }
            retriever.getFrameAtTime(0)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun getVideoDuration(context: Context, videoFile: File): Long {
        return try {
            val retriever = MediaMetadataRetriever()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                retriever.setDataSource(context, Uri.fromFile(videoFile))
            } else {
                retriever.setDataSource(videoFile.absolutePath)
            }
            val time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            time?.toLong() ?: 0L
        } catch (e: Exception) {
            e.printStackTrace()
            0L
        }
    }

    fun formatDuration(durationMs: Long): String {
        val hours = TimeUnit.MILLISECONDS.toHours(durationMs)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(durationMs) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(durationMs) % 60

        return when {
            hours > 0 -> String.format("%02d:%02d:%02d", hours, minutes, seconds)
            else -> String.format("%02d:%02d", minutes, seconds)
        }
    }

    fun getVideoResolution(context: Context, videoFile: File): Pair<Int, Int> {
        return try {
            val retriever = MediaMetadataRetriever()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                retriever.setDataSource(context, Uri.fromFile(videoFile))
            } else {
                retriever.setDataSource(videoFile.absolutePath)
            }
            val width = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)?.toInt() ?: 0
            val height = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)?.toInt() ?: 0
            Pair(width, height)
        } catch (e: Exception) {
            e.printStackTrace()
            Pair(0, 0)
        }
    }

    fun getVideoMetadata(context: Context, videoFile: File): VideoMetadata {
        val retriever = MediaMetadataRetriever()
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                retriever.setDataSource(context, Uri.fromFile(videoFile))
            } else {
                retriever.setDataSource(videoFile.absolutePath)
            }

            val duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong() ?: 0L
            val width = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)?.toInt() ?: 0
            val height = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)?.toInt() ?: 0
            val bitrate = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE)?.toInt() ?: 0
            val rotation = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION)?.toInt() ?: 0

            VideoMetadata(
                duration = duration,
                width = width,
                height = height,
                bitrate = bitrate,
                rotation = rotation,
                thumbnail = retriever.getFrameAtTime(0)
            )
        } catch (e: Exception) {
            e.printStackTrace()
            VideoMetadata()
        } finally {
            try {
                retriever.release()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    data class VideoMetadata(
        val duration: Long = 0L,
        val width: Int = 0,
        val height: Int = 0,
        val bitrate: Int = 0,
        val rotation: Int = 0,
        val thumbnail: Bitmap? = null
    ) {
        val isValid: Boolean
            get() = duration > 0 && width > 0 && height > 0

        val resolution: String
            get() = "${width}x$height}"

        val durationFormatted: String
            get() = formatDuration(duration)

        val bitrateFormatted: String
            get() = "${bitrate / 1000} Kbps"
    }
}
