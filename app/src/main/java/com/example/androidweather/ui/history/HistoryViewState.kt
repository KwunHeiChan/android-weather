package com.example.androidweather.ui.history

import com.example.androidweather.mvi.MviViewState

data class HistoryViewState(
  val isLoading: Boolean,
  val controllerItems: List<HistoryControllerItem>,
  val errorMessage: String?
) : MviViewState {

  companion object {
    fun initial(): HistoryViewState {
      return HistoryViewState(
        isLoading = false,
        controllerItems = emptyList(),
        errorMessage = null
      )
    }
  }
}