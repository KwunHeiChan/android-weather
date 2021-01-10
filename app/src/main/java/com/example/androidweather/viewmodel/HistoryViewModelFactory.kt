package com.example.androidweather.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.androidweather.ui.history.HistoryActionProcessorHolder
import com.example.androidweather.ui.history.HistoryViewModel

class HistoryViewModelFactory constructor(
  private val historyActionProcessorHolder: HistoryActionProcessorHolder
) : ViewModelProvider.Factory {

  @Suppress("UNCHECKED_CAST")
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    if (modelClass.isAssignableFrom(HistoryViewModel::class.java)) {
      return HistoryViewModel(historyActionProcessorHolder) as T
    }
    throw IllegalArgumentException("Unknown ViewModel class")
  }
}