package com.example.androidweather.ui.main

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.Application
import androidx.lifecycle.LiveData
import androidx.room.rxjava3.EmptyResultSetException
import com.example.androidweather.R
import com.example.androidweather.mvvm.BaseViewModel
import com.example.androidweather.mvvm.MvvmViewState
import com.example.androidweather.repository.LocationRepository
import com.example.androidweather.repository.SearchHistoryRepository
import com.example.androidweather.repository.WeatherRepository
import com.example.androidweather.util.kelvinToCelsius
import com.example.androidweather.util.kelvinToFahrenheit
import com.example.androidweather.util.replaceElementFirstIsInstance
import com.example.androidweather.util.toWeatherIconUrl
import com.tbruyelle.rxpermissions3.RxPermissions
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.plusAssign
import io.reactivex.rxjava3.schedulers.Schedulers
import timber.log.Timber
import java.util.*
import javax.inject.Inject

/**
 * Contains and executes all business logic, and returns one unique [LiveData] of [MvvmViewState].
 */
class MainViewModel @Inject constructor(
  private val application: Application,
  private val locationRepository: LocationRepository,
  private val searchHistoryRepository: SearchHistoryRepository,
  private val weatherRepository: WeatherRepository,
  isMainThread: () -> Boolean
) : BaseViewModel<MainViewState>(MainViewState.initial(), isMainThread) {

  fun changeTempFormat(isTempFormatChecked: Boolean) {
    disposables += Observable
      .fromCallable {
        viewState().copy(
          controllerItems = viewState().controllerItems
            .toMutableList()
            .replaceElementFirstIsInstance<
                MainControllerItem,
                MainControllerItem.WeatherItem> { weatherItem ->
              weatherItem.copy(
                tempStringResource = if (isTempFormatChecked) {
                  R.string.temperature_degree_fahrenheit
                } else {
                  R.string.temperature_degree_celsius
                },
                temp = if (isTempFormatChecked) {
                  weatherItem.tempKelvin.kelvinToFahrenheit().toInt()
                } else {
                  weatherItem.tempKelvin.kelvinToCelsius().toInt()
                },
                feelsLikeStringResource = if (isTempFormatChecked) {
                  R.string.feels_like_degree_fahrenheit
                } else {
                  R.string.feels_like_degree_celsius
                },
                feelsLike = if (isTempFormatChecked) {
                  weatherItem.feelsLikeKelvin.kelvinToFahrenheit().toInt()
                } else {
                  weatherItem.feelsLikeKelvin.kelvinToCelsius().toInt()
                },
                highTempStringResource = if (isTempFormatChecked) {
                  R.string.temperature_degree_fahrenheit
                } else {
                  R.string.temperature_degree_celsius
                },
                highTemp = if (isTempFormatChecked) {
                  weatherItem.highTempKelvin.kelvinToFahrenheit().toInt()
                } else {
                  weatherItem.highTempKelvin.kelvinToCelsius().toInt()
                },
                lowTempStringResource = if (isTempFormatChecked) {
                  R.string.temperature_degree_fahrenheit
                } else {
                  R.string.temperature_degree_celsius
                },
                lowTemp = if (isTempFormatChecked) {
                  weatherItem.lowTempKelvin.kelvinToFahrenheit().toInt()
                } else {
                  weatherItem.lowTempKelvin.kelvinToCelsius().toInt()
                },
                tempFormatStringResource = if (isTempFormatChecked) {
                  R.string.degree_fahrenheit
                } else {
                  R.string.degree_celsius
                },
                isDegreeCelsius = !isTempFormatChecked
              )
            }
        )
      }
      .subscribe {
        viewState.value = it
      }
  }

  fun dismissError() {
    viewState.value = viewState().copy(
      errorMessage = null
    )
  }

  fun getHistory() {
    disposables += searchHistoryRepository.getLastSearchedCity()
      .flatMap { weatherRepository.getCityWeather(it.name) }
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .map { response ->
        viewState().copy(
          isLoading = false,
          controllerItems = listOf(
            MainControllerItem.WeatherItem(
              id = response.id,
              tempKelvin = response.main.temp,
              feelsLikeKelvin = response.main.feelsLike,
              highTempKelvin = response.main.tempMax,
              lowTempKelvin = response.main.tempMin,
              cityName = response.name,
              weatherIconUrl = response
                .weather
                .firstOrNull()
                ?.icon
                .toWeatherIconUrl(),
              weatherDescription = response
                .weather
                .firstOrNull()
                ?.description
                ?.capitalize(Locale.getDefault()) ?: "",
              tempStringResource = R.string.temperature_degree_celsius,
              temp = response.main.temp.kelvinToCelsius().toInt(),
              feelsLikeStringResource = R.string.feels_like_degree_celsius,
              feelsLike = response.main.feelsLike.kelvinToCelsius().toInt(),
              highTempStringResource = R.string.temperature_degree_celsius,
              highTemp = response.main.tempMax.kelvinToCelsius().toInt(),
              lowTempStringResource = R.string.temperature_degree_celsius,
              lowTemp = response.main.tempMin.kelvinToCelsius().toInt(),
              tempFormatStringResource = R.string.degree_celsius,
              isDegreeCelsius = true
            )
          )
        )
      }
      .onErrorReturn {
        Timber.e(it)
        if (it is EmptyResultSetException) {
          viewState().copy(
            isLoading = false,
            controllerItems = listOf(MainControllerItem.TutorialItem)
          )
        } else {
          viewState().copy(
            isLoading = false,
            errorMessage = it.message
          )
        }
      }
      .startWith(
        Observable.just(
          viewState().copy(
            isLoading = true,
            isHideKeyboard = true
          )
        )
      )
      .subscribe {
        viewState.value = it
      }
  }

  fun hideKeyboard() {
    viewState.value = viewState().copy(
      isHideKeyboard = false
    )
  }
  
  fun searchCity(searchKeyword: String) {
    disposables += weatherRepository.getCityWeather(searchKeyword)
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .map { response ->
        val preIsTempFormatChecked = viewState().controllerItems.getIsTempFormatChecked()
        viewState().copy(
          isLoading = false,
          controllerItems = listOf(
            MainControllerItem.WeatherItem(
              id = response.id,
              tempKelvin = response.main.temp,
              feelsLikeKelvin = response.main.feelsLike,
              highTempKelvin = response.main.tempMax,
              lowTempKelvin = response.main.tempMin,
              cityName = response.name,
              weatherIconUrl = response
                .weather
                .firstOrNull()
                ?.icon
                ?.toWeatherIconUrl() ?: "",
              weatherDescription = response
                .weather
                .firstOrNull()
                ?.description
                ?.capitalize(Locale.getDefault()) ?: "",
              tempStringResource = if (preIsTempFormatChecked) {
                R.string.temperature_degree_celsius
              } else {
                R.string.temperature_degree_fahrenheit
              },
              temp = if (preIsTempFormatChecked) {
                response.main.temp.kelvinToCelsius().toInt()
              } else {
                response.main.temp.kelvinToFahrenheit().toInt()
              },
              feelsLikeStringResource = if (preIsTempFormatChecked) {
                R.string.feels_like_degree_celsius
              } else {
                R.string.feels_like_degree_fahrenheit
              },
              feelsLike = if (preIsTempFormatChecked) {
                response.main.feelsLike.kelvinToCelsius().toInt()
              } else {
                response.main.feelsLike.kelvinToFahrenheit().toInt()
              },
              highTempStringResource = if (preIsTempFormatChecked) {
                R.string.temperature_degree_celsius
              } else {
                R.string.temperature_degree_fahrenheit
              },
              highTemp = if (preIsTempFormatChecked) {
                response.main.tempMax.kelvinToCelsius().toInt()
              } else {
                response.main.tempMax.kelvinToFahrenheit().toInt()
              },
              lowTempStringResource = if (preIsTempFormatChecked) {
                R.string.temperature_degree_celsius
              } else {
                R.string.temperature_degree_fahrenheit
              },
              lowTemp = if (preIsTempFormatChecked) {
                response.main.tempMin.kelvinToCelsius().toInt()
              } else {
                response.main.tempMin.kelvinToFahrenheit().toInt()
              },
              tempFormatStringResource = if (preIsTempFormatChecked) {
                R.string.degree_celsius
              } else {
                R.string.degree_fahrenheit
              },
              isDegreeCelsius = preIsTempFormatChecked
            )
          )
        )
      }
      .onErrorReturn {
        Timber.e(it)
        viewState().copy(
          isLoading = false,
          errorMessage = it.message
        )
      }
      .startWith(
        Observable.just(
          viewState().copy(
            isLoading = true,
            isHideKeyboard = true
          )
        )
      )
      .subscribe {
        viewState.value = it
      }
  }

  fun searchLocation(rxPermissions: RxPermissions) {
    disposables += rxPermissions.request(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION)
      .flatMap { isGranted ->
        if (isGranted) {
          locationRepository.getCurrentLocation()
            .flatMap { weatherRepository.getLocationWeather(it.latitude, it.longitude) }
        } else {
          throw Exception(application.getString(R.string.no_permission))
        }
      }
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .map { response ->
        val preIsTempFormatChecked = viewState().controllerItems.getIsTempFormatChecked()
        viewState().copy(
          isLoading = false,
          controllerItems = listOf(
            MainControllerItem.WeatherItem(
              id = response.id,
              tempKelvin = response.main.temp,
              feelsLikeKelvin = response.main.feelsLike,
              highTempKelvin = response.main.tempMax,
              lowTempKelvin = response.main.tempMin,
              cityName = response.name,
              weatherIconUrl = response
                .weather
                .firstOrNull()
                ?.icon
                ?.toWeatherIconUrl() ?: "",
              weatherDescription = response
                .weather
                .firstOrNull()
                ?.description
                ?.capitalize(Locale.getDefault()) ?: "",
              tempStringResource = if (preIsTempFormatChecked) {
                R.string.temperature_degree_celsius
              } else {
                R.string.temperature_degree_fahrenheit
              },
              temp = response.main.temp.kelvinToCelsius().toInt(),
              feelsLikeStringResource = if (preIsTempFormatChecked) {
                R.string.feels_like_degree_celsius
              } else {
                R.string.feels_like_degree_fahrenheit
              },
              feelsLike = response.main.feelsLike.kelvinToCelsius().toInt(),
              highTempStringResource = if (preIsTempFormatChecked) {
                R.string.temperature_degree_celsius
              } else {
                R.string.temperature_degree_fahrenheit
              },
              highTemp = response.main.tempMax.kelvinToCelsius().toInt(),
              lowTempStringResource = if (preIsTempFormatChecked) {
                R.string.temperature_degree_celsius
              } else {
                R.string.temperature_degree_fahrenheit
              },
              lowTemp = response.main.tempMin.kelvinToCelsius().toInt(),
              tempFormatStringResource = if (preIsTempFormatChecked) {
                R.string.degree_celsius
              } else {
                R.string.degree_fahrenheit
              },
              isDegreeCelsius = preIsTempFormatChecked
            )
          )
        )
      }
      .onErrorReturn {
        Timber.e(it)
        viewState().copy(
          isLoading = false,
          errorMessage = it.message
        )
      }
      .startWith(
        Observable.just(
          viewState().copy(
            isLoading = true,
            isHideKeyboard = true
          )
        )
      )
      .subscribe {
        viewState.value = it
      }
  }
}