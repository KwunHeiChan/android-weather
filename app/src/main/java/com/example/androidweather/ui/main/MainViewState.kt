package com.example.androidweather.ui.main

import com.example.androidweather.mvvm.MvvmViewState

data class MainViewState(
  val isLoading: Boolean,
  val controllerItems: List<MainControllerItem>,
  val errorMessage: String?,
  val isHideKeyboard: Boolean
) : MvvmViewState {

  companion object {
    fun initial(): MainViewState {
      return MainViewState(
        isLoading = false,
        controllerItems = emptyList(),
        errorMessage = null,
        isHideKeyboard = false
      )
    }
  }
}