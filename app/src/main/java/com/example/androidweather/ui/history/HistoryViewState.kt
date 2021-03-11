package com.example.androidweather.ui.history

import com.example.androidweather.mvvm.MvvmViewState

data class HistoryViewState(
  val isLoading: Boolean,
  val controllerItems: List<HistoryControllerItem>,
  val errorMessage: String?
) : MvvmViewState {

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