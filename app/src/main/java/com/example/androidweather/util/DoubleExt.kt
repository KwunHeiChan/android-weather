package com.example.androidweather.util

fun Double.kelvinToCelsius(): Double {
  return this - 273.15
}

fun Double.kelvinToFahrenheit(): Double {
  return (this - 273.15) * 9 / 5 + 32
}