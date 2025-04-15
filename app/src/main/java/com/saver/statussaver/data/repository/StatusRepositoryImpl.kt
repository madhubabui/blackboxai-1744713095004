package com.saver.statussaver.data.repository

import android.content.Context
import android.net.Uri
import android.os.Environment
import com.saver.statussaver.SaverApp
import com.saver.statussaver.data.model.*
import com.saver.statussaver.domain.repository.StatusRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StatusRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : StatusRepository {

    private val whatsAppDir = File(
        Environment.getExternalStorageDirectory(),
        SaverApp.WHATSAPP_RELATIVE_PATH
    )

    private val savedStatusDir = File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
        SaverApp.SAVED_DIRECTORY
    ).apply { mkdirs() }

    private val recycleBinDir = File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
        SaverApp.RECYCLE_BIN_DIRECTORY
    ).apply { mkdirs() }

    override suspend fun getStatuses(): Flow<List<Status>> = flow {
        if (!whatsAppDir.exists()) {
            emit(emptyList())
            return@flow
        }

        val statuses = whatsAppDir.listFiles()
            ?.filter { it.isFile && (it.name.endsWith(".jpg") || it.name.endsWith(".mp4")) }
            ?.map { file ->
                Status.fromFile(
                    file = file,
                    uri = Uri.fromFile(file)
                )
            }
            ?.sortedByDescending { it.timestamp }
            ?: emptyList()

        emit(statuses)
    }.flowOn(Dispatchers.IO)

    override suspend fun getRecycleBinStatuses(): Flow<List<Status>> = flow {
        val statuses = recycleBinDir.listFiles()
            ?.filter { it.isFile && (it.name.endsWith(".jpg") || it.name.endsWith(".mp4")) }
            ?.map { file ->
                Status.fromFile(
                    file = file,
                    uri = Uri.fromFile(file)
                ).copy(isInRecycleBin = true)
            }
            ?.sortedByDescending { it.timestamp }
            ?: emptyList()

        emit(statuses)
    }.flowOn(Dispatchers.IO)

    override suspend fun saveStatus(status: Status): StatusResult = withContext(Dispatchers.IO) {
        try {
            val destination = File(savedStatusDir, status.name)
            copyFile(status.file, destination)
            StatusResult.Success(
                status.copy(
                    file = destination,
                    uri = Uri.fromFile(destination)
                )
            )
        } catch (e: Exception) {
            StatusResult.Error(e.message ?: "Failed to save status")
        }
    }

    override suspend fun saveStatuses(statuses: List<Status>): List<StatusOperation> =
        withContext(Dispatchers.IO) {
            statuses.map { status ->
                when (val result = saveStatus(status)) {
                    is StatusResult.Success -> StatusOperation(
                        status = result.status,
                        operationType = OperationType.SAVE,
                        result = true
                    )
                    is StatusResult.Error -> StatusOperation(
                        status = status,
                        operationType = OperationType.SAVE,
                        result = false,
                        errorMessage = result.message
                    )
                }
            }
        }

    override suspend fun moveToRecycleBin(status: Status): StatusResult = withContext(Dispatchers.IO) {
        try {
            val destination = File(recycleBinDir, status.name)
            copyFile(status.file, destination)
            if (status.file.delete()) {
                StatusResult.Success(
                    status.copy(
                        file = destination,
                        uri = Uri.fromFile(destination),
                        isInRecycleBin = true
                    )
                )
            } else {
                StatusResult.Error("Failed to move status to recycle bin")
            }
        } catch (e: Exception) {
            StatusResult.Error(e.message ?: "Failed to move status to recycle bin")
        }
    }

    override suspend fun moveToRecycleBin(statuses: List<Status>): List<StatusOperation> =
        withContext(Dispatchers.IO) {
            statuses.map { status ->
                when (val result = moveToRecycleBin(status)) {
                    is StatusResult.Success -> StatusOperation(
                        status = result.status,
                        operationType = OperationType.DELETE,
                        result = true
                    )
                    is StatusResult.Error -> StatusOperation(
                        status = status,
                        operationType = OperationType.DELETE,
                        result = false,
                        errorMessage = result.message
                    )
                }
            }
        }

    override suspend fun restoreFromRecycleBin(status: Status): StatusResult =
        withContext(Dispatchers.IO) {
            try {
                val destination = File(whatsAppDir, status.name)
                copyFile(status.file, destination)
                if (status.file.delete()) {
                    StatusResult.Success(
                        status.copy(
                            file = destination,
                            uri = Uri.fromFile(destination),
                            isInRecycleBin = false
                        )
                    )
                } else {
                    StatusResult.Error("Failed to restore status")
                }
            } catch (e: Exception) {
                StatusResult.Error(e.message ?: "Failed to restore status")
            }
        }

    override suspend fun deleteFromRecycleBin(status: Status): StatusResult =
        withContext(Dispatchers.IO) {
            return@withContext try {
                if (status.file.delete()) {
                    StatusResult.Success(status)
                } else {
                    StatusResult.Error("Failed to delete status")
                }
            } catch (e: Exception) {
                StatusResult.Error(e.message ?: "Failed to delete status")
            }
        }

    override suspend fun cleanupRecycleBin() = withContext(Dispatchers.IO) {
        val currentTime = System.currentTimeMillis()
        recycleBinDir.listFiles()?.forEach { file ->
            if (currentTime - file.lastModified() > SaverApp.RECYCLE_BIN_RETENTION_PERIOD) {
                file.delete()
            }
        }
    }

    override suspend fun isWhatsAppAvailable(): Boolean = withContext(Dispatchers.IO) {
        whatsAppDir.exists() && whatsAppDir.isDirectory
    }

    override fun getSavedStatusesPath(): String = savedStatusDir.absolutePath

    override fun getRecycleBinPath(): String = recycleBinDir.absolutePath

    private fun copyFile(source: File, destination: File) {
        FileInputStream(source).use { input ->
            FileOutputStream(destination).use { output ->
                input.copyTo(output)
            }
        }
    }
}
