package com.example.androidweather.ui.main

/**
 * Get city name of weather info from currently displayed
 * [MainControllerItem]s as single source of truth, returns empty string
 * if none is being displayed
 */
fun List<MainControllerItem>.getCityName(): String =
  filterIsInstance<MainControllerItem.WeatherItem>().firstOrNull()?.cityName ?: ""

/**
 * Get temperature format state from currently displayed
 * [MainControllerItem]s as single source of truth, returns `true`
 * if none is being displayed
 */
fun List<MainControllerItem>.getIsTempFormatChecked(): Boolean =
  filterIsInstance<MainControllerItem.WeatherItem>().firstOrNull()?.isTempFormatChecked ?: true