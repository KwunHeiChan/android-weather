package com.example.androidweather.ui.main

import com.airbnb.epoxy.TypedEpoxyController
import com.example.androidweather.R
import com.example.androidweather.epoxy.tutorialView
import com.example.androidweather.epoxy.weatherView

class MainController : TypedEpoxyController<List<MainControllerItem>>() {

  override fun buildModels(data: List<MainControllerItem>?) {
    data?.forEach { controllerItem ->
      when (controllerItem) {
        is MainControllerItem.TutorialItem -> {
          tutorialView {
            id("tutorial_tutorial")
          }
        }
        is MainControllerItem.WeatherItem -> {
          weatherView {
            id("weather_weather_${controllerItem.id}")
            cityName(controllerItem.cityName)
            weatherIconUrl(controllerItem.weatherIconUrl)
            weatherDescription(controllerItem.weatherDescription)
            temp(R.string.degree_celsius, controllerItem.temp)
            feelsLike(R.string.feels_like, controllerItem.feelsLike)
            highTemp(R.string.degree_celsius, controllerItem.highTemp)
            lowTemp(R.string.degree_celsius, controllerItem.lowTemp)
          }
        }
      }
    }
  }
}