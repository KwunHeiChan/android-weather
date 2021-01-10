package com.example.androidweather.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.androidweather.ui.main.MainActionProcessorHolder
import com.example.androidweather.ui.main.MainViewModel

class MainViewModelFactory constructor(
  private val mainActionProcessorHolder: MainActionProcessorHolder
) : ViewModelProvider.Factory {

  @Suppress("UNCHECKED_CAST")
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
      return MainViewModel(mainActionProcessorHolder) as T
    }
    throw IllegalArgumentException("Unknown ViewModel class")
  }
}