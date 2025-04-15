package com.saver.statussaver.domain.repository

import com.saver.statussaver.data.model.Status
import com.saver.statussaver.data.model.StatusOperation
import com.saver.statussaver.data.model.StatusResult
import kotlinx.coroutines.flow.Flow

interface StatusRepository {
    /**
     * Get all available WhatsApp statuses
     */
    suspend fun getStatuses(): Flow<List<Status>>

    /**
     * Get statuses from recycle bin
     */
    suspend fun getRecycleBinStatuses(): Flow<List<Status>>

    /**
     * Save a status to the app's directory
     */
    suspend fun saveStatus(status: Status): StatusResult

    /**
     * Save multiple statuses at once
     */
    suspend fun saveStatuses(statuses: List<Status>): List<StatusOperation>

    /**
     * Move a status to recycle bin
     */
    suspend fun moveToRecycleBin(status: Status): StatusResult

    /**
     * Move multiple statuses to recycle bin
     */
    suspend fun moveToRecycleBin(statuses: List<Status>): List<StatusOperation>

    /**
     * Restore a status from recycle bin
     */
    suspend fun restoreFromRecycleBin(status: Status): StatusResult

    /**
     * Permanently delete a status from recycle bin
     */
    suspend fun deleteFromRecycleBin(status: Status): StatusResult

    /**
     * Clean up expired items from recycle bin
     */
    suspend fun cleanupRecycleBin()

    /**
     * Check if WhatsApp is installed and accessible
     */
    suspend fun isWhatsAppAvailable(): Boolean

    /**
     * Get the path where statuses are saved
     */
    fun getSavedStatusesPath(): String

    /**
     * Get the recycle bin path
     */
    fun getRecycleBinPath(): String
}
