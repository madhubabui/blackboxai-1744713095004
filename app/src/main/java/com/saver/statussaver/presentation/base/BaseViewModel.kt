package com.saver.statussaver.presentation.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saver.statussaver.util.AppError
import com.saver.statussaver.util.ResourceProvider
import com.saver.statussaver.util.Result
import com.saver.statussaver.util.toAppError
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

abstract class BaseViewModel : ViewModel() {

    @Inject
    protected lateinit var resourceProvider: ResourceProvider

    private val _events = MutableSharedFlow<UiEvent>()
    val events: SharedFlow<UiEvent> = _events.asSharedFlow()

    protected fun launchWithHandler(
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        onError: (suspend (AppError) -> Unit)? = null,
        block: suspend () -> Unit
    ) {
        viewModelScope.launch(dispatcher) {
            try {
                block()
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                val error = e.toAppError()
                onError?.invoke(error) ?: handleError(error)
            }
        }
    }

    protected suspend fun <T> executeWithResult(
        block: suspend () -> T
    ): Result<T> {
        return try {
            Result.Success(block())
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Result.Error(e)
        }
    }

    protected suspend fun emitEvent(event: UiEvent) {
        _events.emit(event)
    }

    protected open suspend fun handleError(error: AppError) {
        val message = when (error) {
            is AppError.NoWhatsApp -> "WhatsApp not installed"
            is AppError.NoStatuses -> "No statuses found"
            is AppError.PermissionDenied -> "Storage permission required"
            is AppError.FileNotFound -> "File not found"
            is AppError.InvalidFile -> "Invalid file"
            is AppError.FileTooLarge -> "File too large"
            is AppError.SaveError -> error.message
            is AppError.DeleteError -> error.message
            is AppError.RestoreError -> error.message
            is AppError.ShareError -> error.message
            is AppError.UnknownError -> error.message
        }
        emitEvent(UiEvent.ShowMessage(message))
    }
}

sealed class UiEvent {
    data class ShowMessage(val message: String) : UiEvent()
    data class Navigate(val route: String) : UiEvent()
    object NavigateBack : UiEvent()
    object Refresh : UiEvent()
}

interface UiState {
    val isLoading: Boolean
    val error: String?
}

data class BaseUiState(
    override val isLoading: Boolean = false,
    override val error: String? = null
) : UiState

fun <T : UiState> MutableStateFlow<T>.updateState(update: T.() -> T) {
    value = value.update()
}

fun <T> StateFlow<T>.requireValue(): T {
    return value ?: throw IllegalStateException("State value is null")
}

fun <T> Result<T>.updateUiState(
    currentState: UiState,
    onSuccess: (T) -> UiState,
    onError: ((Exception) -> UiState)? = null
): UiState {
    return when (this) {
        is Result.Success -> onSuccess(data)
        is Result.Error -> onError?.invoke(exception) ?: BaseUiState(
            error = exception.message
        )
        is Result.Loading -> currentState.copy(isLoading = true)
    }
}

private fun UiState.copy(
    isLoading: Boolean = this.isLoading,
    error: String? = this.error
): UiState = BaseUiState(isLoading, error)
