package com.example.androidweather.repository

import com.example.androidweather.API_KEY
import com.example.androidweather.api.WeatherService
import com.example.androidweather.model.GetWeatherResponse
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRepository @Inject constructor(
  private val searchHistoryRepository: SearchHistoryRepository,
  private val weatherService: WeatherService
) {

  fun getCityWeather(cityName: String): Observable<GetWeatherResponse> {
    return weatherService.getCityWeather(cityName, API_KEY).doOnNext { response ->
      searchHistoryRepository.insert(response.id, response.name)
    }
  }

  fun getLocationWeather(lat: Double, lng: Double): Observable<GetWeatherResponse> {
    return weatherService.getLocationWeather(lat, lng, API_KEY)
  }
}