package com.example.androidweather.util

import org.junit.Assert.assertEquals
import org.junit.Test

class StringExtText {

  @Test
  fun toWeatherIconUrl() {
    assertEquals("", null.toWeatherIconUrl())
    assertEquals("https://openweathermap.org/img/wn/01d@2x.png", "01d".toWeatherIconUrl())
    assertEquals("https://openweathermap.org/img/wn/01n@2x.png", "01n".toWeatherIconUrl())
    assertEquals("https://openweathermap.org/img/wn/02d@2x.png", "02d".toWeatherIconUrl())
    assertEquals("https://openweathermap.org/img/wn/02n@2x.png", "02n".toWeatherIconUrl())
    assertEquals("https://openweathermap.org/img/wn/03d@2x.png", "03d".toWeatherIconUrl())
    assertEquals("https://openweathermap.org/img/wn/03n@2x.png", "03n".toWeatherIconUrl())
    assertEquals("https://openweathermap.org/img/wn/04d@2x.png", "04d".toWeatherIconUrl())
    assertEquals("https://openweathermap.org/img/wn/04n@2x.png", "04n".toWeatherIconUrl())
    assertEquals("https://openweathermap.org/img/wn/09d@2x.png", "09d".toWeatherIconUrl())
    assertEquals("https://openweathermap.org/img/wn/09n@2x.png", "09n".toWeatherIconUrl())
    assertEquals("https://openweathermap.org/img/wn/10d@2x.png", "10d".toWeatherIconUrl())
    assertEquals("https://openweathermap.org/img/wn/10n@2x.png", "10n".toWeatherIconUrl())
    assertEquals("https://openweathermap.org/img/wn/11d@2x.png", "11d".toWeatherIconUrl())
    assertEquals("https://openweathermap.org/img/wn/11n@2x.png", "11n".toWeatherIconUrl())
    assertEquals("https://openweathermap.org/img/wn/13d@2x.png", "13d".toWeatherIconUrl())
    assertEquals("https://openweathermap.org/img/wn/13n@2x.png", "13n".toWeatherIconUrl())
    assertEquals("https://openweathermap.org/img/wn/50d@2x.png", "50d".toWeatherIconUrl())
    assertEquals("https://openweathermap.org/img/wn/50n@2x.png", "50n".toWeatherIconUrl())
  }
}