package com.example.androidweather.util

fun String?.toWeatherIconUrl(): String = if (this == null) {
  ""
} else {
  "https://openweathermap.org/img/wn/${this}@2x.png"
}