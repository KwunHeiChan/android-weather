package com.example.androidweather.ui.main

sealed class MainControllerItem {

  object TutorialItem : MainControllerItem()

  data class WeatherItem(
    val id: Int,
    val cityName: String,
    val weatherIconUrl: String,
    val weatherDescription: String,
    val temp: Int,
    val feelsLike: Int,
    val highTemp: Int,
    val lowTemp: Int
  ) : MainControllerItem()
}