package com.example.androidweather.ui.history

sealed class HistoryControllerItem {

  data class CityItem(
    val id: Int,
    val name: String
  ) : HistoryControllerItem()
}