package com.saver.statussaver.data.model

import android.net.Uri
import java.io.File

data class Status(
    val id: String,
    val file: File,
    val uri: Uri,
    val path: String,
    val name: String,
    val type: StatusType,
    val timestamp: Long,
    val size: Long,
    val isInRecycleBin: Boolean = false
) {
    val isVideo: Boolean
        get() = type == StatusType.VIDEO

    val isImage: Boolean
        get() = type == StatusType.IMAGE

    companion object {
        fun fromFile(file: File, uri: Uri): Status {
            return Status(
                id = file.absolutePath.hashCode().toString(),
                file = file,
                uri = uri,
                path = file.absolutePath,
                name = file.name,
                type = when {
                    file.name.endsWith(".mp4") -> StatusType.VIDEO
                    else -> StatusType.IMAGE
                },
                timestamp = file.lastModified(),
                size = file.length()
            )
        }
    }
}

enum class StatusType {
    IMAGE,
    VIDEO
}

sealed class StatusResult {
    data class Success(val status: Status) : StatusResult()
    data class Error(val message: String) : StatusResult()
}

data class StatusOperation(
    val status: Status,
    val operationType: OperationType,
    val result: Boolean,
    val errorMessage: String? = null
)

enum class OperationType {
    SAVE,
    DELETE,
    RESTORE,
    SHARE
}
