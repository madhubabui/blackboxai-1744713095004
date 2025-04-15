package com.saver.statussaver.util

sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
    object Loading : Result<Nothing>()

    val isSuccess: Boolean
        get() = this is Success

    val isError: Boolean
        get() = this is Error

    val isLoading: Boolean
        get() = this is Loading

    fun getOrNull(): T? = when (this) {
        is Success -> data
        else -> null
    }

    fun getOrThrow(): T = when (this) {
        is Success -> data
        is Error -> throw exception
        is Loading -> throw IllegalStateException("Result is Loading")
    }

    fun errorOrNull(): Exception? = when (this) {
        is Error -> exception
        else -> null
    }

    companion object {
        fun <T> success(data: T): Result<T> = Success(data)
        
        fun error(exception: Exception): Result<Nothing> = Error(exception)
        
        fun error(message: String): Result<Nothing> = Error(Exception(message))
        
        fun loading(): Result<Nothing> = Loading

        fun <T> of(action: () -> T): Result<T> = try {
            Success(action())
        } catch (e: Exception) {
            Error(e)
        }

        suspend fun <T> ofSuspend(action: suspend () -> T): Result<T> = try {
            Success(action())
        } catch (e: Exception) {
            Error(e)
        }
    }
}

inline fun <T, R> Result<T>.map(transform: (T) -> R): Result<R> {
    return when (this) {
        is Result.Success -> Result.success(transform(data))
        is Result.Error -> Result.error(exception)
        is Result.Loading -> Result.loading()
    }
}

inline fun <T> Result<T>.onSuccess(action: (T) -> Unit): Result<T> {
    if (this is Result.Success) {
        action(data)
    }
    return this
}

inline fun <T> Result<T>.onError(action: (Exception) -> Unit): Result<T> {
    if (this is Result.Error) {
        action(exception)
    }
    return this
}

inline fun <T> Result<T>.onLoading(action: () -> Unit): Result<T> {
    if (this is Result.Loading) {
        action()
    }
    return this
}

sealed class AppError : Exception() {
    object NoWhatsApp : AppError()
    object NoStatuses : AppError()
    object PermissionDenied : AppError()
    object FileNotFound : AppError()
    object InvalidFile : AppError()
    object FileTooLarge : AppError()
    data class SaveError(override val message: String) : AppError()
    data class DeleteError(override val message: String) : AppError()
    data class RestoreError(override val message: String) : AppError()
    data class ShareError(override val message: String) : AppError()
    data class UnknownError(override val message: String) : AppError()
}

fun Throwable.toAppError(): AppError {
    return when (this) {
        is AppError -> this
        is SecurityException -> AppError.PermissionDenied
        is java.io.FileNotFoundException -> AppError.FileNotFound
        else -> AppError.UnknownError(message ?: "Unknown error occurred")
    }
}

fun <T> Result<T>.requireData(): T {
    return when (this) {
        is Result.Success -> data
        is Result.Error -> throw exception
        is Result.Loading -> throw IllegalStateException("Result is Loading")
    }
}

suspend fun <T> suspendRunCatching(block: suspend () -> T): Result<T> {
    return try {
        Result.Success(block())
    } catch (e: Exception) {
        Result.Error(e)
    }
}
