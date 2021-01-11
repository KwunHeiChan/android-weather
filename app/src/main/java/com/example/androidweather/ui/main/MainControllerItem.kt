package com.example.androidweather.ui.main

import androidx.annotation.StringRes

sealed class MainControllerItem {

  object TutorialItem : MainControllerItem()

  data class WeatherItem(
    val id: Int,
    val tempKelvin: Double,
    val feelsLikeKelvin: Double,
    val highTempKelvin: Double,
    val lowTempKelvin: Double,
    val cityName: String,
    val weatherIconUrl: String,
    val weatherDescription: String,
    @StringRes val tempStringResource: Int,
    val temp: Int,
    @StringRes val feelsLikeStringResource: Int,
    val feelsLike: Int,
    @StringRes val highTempStringResource: Int,
    val highTemp: Int,
    @StringRes val lowTempStringResource: Int,
    val lowTemp: Int,
    @StringRes val tempFormatStringResource: Int,
    val isDegreeCelsius: Boolean
  ) : MainControllerItem()
}