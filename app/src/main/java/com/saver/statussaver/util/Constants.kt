package com.saver.statussaver.util

import android.os.Environment
import java.io.File

object Constants {
    // App Directories
    const val APP_DIRECTORY = "Saver"
    const val SAVED_DIRECTORY = "Saver"
    const val RECYCLE_BIN_DIRECTORY = "Saver/.RecycleBin"

    // WhatsApp Paths
    const val WHATSAPP_RELATIVE_PATH = "WhatsApp/Media/.Statuses"
    const val WHATSAPP_BUSINESS_RELATIVE_PATH = "WhatsApp Business/Media/.Statuses"

    // File Patterns
    const val IMAGE_PATTERN = ".*\\.(jpg|jpeg|png|gif|webp)$"
    const val VIDEO_PATTERN = ".*\\.(mp4|3gp|mkv|webm)$"

    // Time Constants
    const val RECYCLE_BIN_RETENTION_PERIOD = 24 * 60 * 60 * 1000L // 24 hours in milliseconds
    const val SPLASH_DELAY = 1500L // 1.5 seconds

    // Preferences Keys
    const val PREF_THEME = "theme"
    const val PREF_TUTORIAL_SHOWN = "tutorial_shown"
    const val PREF_LAST_CLEANUP = "last_cleanup"

    // Request Codes
    const val PERMISSION_REQUEST_CODE = 100
    const val SETTINGS_REQUEST_CODE = 101

    // Bundle Keys
    const val EXTRA_STATUS = "extra_status"
    const val EXTRA_POSITION = "extra_position"
    const val EXTRA_FROM_RECYCLE_BIN = "extra_from_recycle_bin"

    // Grid Spans
    const val GRID_SPAN_COUNT = 3

    // File Size Limits
    const val MAX_FILE_SIZE = 100 * 1024 * 1024L // 100MB

    // Paths
    val EXTERNAL_STORAGE_ROOT = Environment.getExternalStorageDirectory()
    val WHATSAPP_STATUS_PATH = File(EXTERNAL_STORAGE_ROOT, WHATSAPP_RELATIVE_PATH)
    val WHATSAPP_BUSINESS_STATUS_PATH = File(EXTERNAL_STORAGE_ROOT, WHATSAPP_BUSINESS_RELATIVE_PATH)
    val SAVED_STATUS_PATH = File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
        SAVED_DIRECTORY
    )
    val RECYCLE_BIN_PATH = File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
        RECYCLE_BIN_DIRECTORY
    )

    // Error Messages
    object ErrorMessages {
        const val NO_WHATSAPP = "WhatsApp not found"
        const val NO_STATUSES = "No statuses found"
        const val PERMISSION_DENIED = "Storage permission denied"
        const val FILE_NOT_FOUND = "File not found"
        const val SAVE_FAILED = "Failed to save status"
        const val DELETE_FAILED = "Failed to delete status"
        const val RESTORE_FAILED = "Failed to restore status"
        const val SHARE_FAILED = "Failed to share status"
        const val INVALID_FILE = "Invalid file"
        const val FILE_TOO_LARGE = "File is too large"
    }

    // Success Messages
    object SuccessMessages {
        const val STATUS_SAVED = "Status saved successfully"
        const val STATUS_DELETED = "Status moved to recycle bin"
        const val STATUS_RESTORED = "Status restored successfully"
        const val MULTIPLE_SAVED = "%d statuses saved"
        const val MULTIPLE_DELETED = "%d statuses moved to recycle bin"
        const val MULTIPLE_RESTORED = "%d statuses restored"
    }

    // MIME Types
    object MimeTypes {
        const val IMAGE = "image/*"
        const val VIDEO = "video/*"
        const val ALL = "*/*"
    }
}
