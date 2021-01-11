package com.example.androidweather.ui.main

import com.airbnb.epoxy.TypedEpoxyController
import com.example.androidweather.epoxy.tutorialView
import com.example.androidweather.epoxy.weatherView
import com.example.androidweather.util.KeyedListener
import com.jakewharton.rxrelay3.PublishRelay

class MainController : TypedEpoxyController<List<MainControllerItem>>() {

  /**
   * The [PublishRelay] of delete city button click listener, which passes the city ID
   * [Int] for subscription outside
   */
  val tempFormatChangeRelay: PublishRelay<Boolean> = PublishRelay.create<Boolean>()

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
            temp(controllerItem.tempStringResource, controllerItem.temp)
            feelsLike(controllerItem.feelsLikeStringResource, controllerItem.feelsLike)
            highTemp(controllerItem.highTempStringResource, controllerItem.highTemp)
            lowTemp(controllerItem.lowTempStringResource, controllerItem.lowTemp)
            tempFormat(controllerItem.tempFormatStringResource)
            isTempFormatChecked(controllerItem.isDegreeCelsius)
            tempFormatKeyedOnChangeListener(
              KeyedListener.create(
                Unit,
                { isChecked ->
                  tempFormatChangeRelay.accept(isChecked)
                }
              )
            )
          }
        }
      }
    }
  }
}