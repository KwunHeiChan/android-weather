package com.example.androidweather.util

import org.junit.Assert.assertEquals
import org.junit.Test

class DoubleExtText {

  @Test
  fun kelvinToCelsius() {
    assertEquals(.0, 273.15.kelvinToCelsius(), .0)
  }

  @Test
  fun kelvinToFahrenheit() {
    assertEquals(32.0, 273.15.kelvinToFahrenheit(), .0)
  }
}