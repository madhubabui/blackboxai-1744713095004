package com.saver.statussaver.presentation.main

sealed class MainEvent {
    data class ShowMessage(val message: String) : MainEvent()
    object NavigateToSettings : MainEvent()
    object NavigateToRecycleBin : MainEvent()
    object NavigateToCustomViews : MainEvent()
}
