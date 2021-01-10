package com.example.androidweather.ui.history

import com.example.androidweather.mvi.MviIntent

sealed class HistoryIntent : MviIntent {

  object DismissErrorIntent : HistoryIntent()

  object InitialIntent : HistoryIntent()

  data class RemoveSearchHistoryIntent(val id: Int) : HistoryIntent()
}