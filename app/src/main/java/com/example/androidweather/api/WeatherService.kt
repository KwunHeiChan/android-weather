package com.example.androidweather.api

import com.example.androidweather.model.GetWeatherResponse
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {

  @GET("data/2.5/weather")
  fun getCityWeather(
    @Query("q") cityName: String,
    @Query("appid") apiKey: String
  ): Observable<GetWeatherResponse>

  @GET("data/2.5/weather")
  fun getLocationWeather(
    @Query("lat") lat: Double,
    @Query("lon") lng: Double,
    @Query("appid") apiKey: String
  ): Observable<GetWeatherResponse>
}