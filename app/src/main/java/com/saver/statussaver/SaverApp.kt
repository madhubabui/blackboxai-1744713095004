package com.saver.statussaver

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SaverApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize any required components here
    }

    companion object {
        const val WHATSAPP_RELATIVE_PATH = "WhatsApp/Media/.Statuses"
        const val SAVED_DIRECTORY = "Saver"
        const val RECYCLE_BIN_DIRECTORY = "Saver/.RecycleBin"
        const val RECYCLE_BIN_RETENTION_PERIOD = 24 * 60 * 60 * 1000L // 24 hours in milliseconds
    }
}
