package com.example.androidweather.ui.history

sealed class HistoryAction {

  object DismissErrorAction : HistoryAction()

  object GetSearchHistoryAction : HistoryAction()

  data class RemoveSearchHistoryAction(val id: Int) : HistoryAction()
}