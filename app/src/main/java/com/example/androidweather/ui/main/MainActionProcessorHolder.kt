package com.example.androidweather.ui.main

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.app.Application
import androidx.room.rxjava3.EmptyResultSetException
import com.example.androidweather.R
import com.example.androidweather.mvi.MviAction
import com.example.androidweather.mvi.MviResult
import com.example.androidweather.mvi.MviView
import com.example.androidweather.mvi.MviViewModel
import com.example.androidweather.mvi.MviViewState
import com.example.androidweather.repository.LocationRepository
import com.example.androidweather.repository.SearchHistoryRepository
import com.example.androidweather.repository.WeatherRepository
import com.example.androidweather.util.kelvinToCelsius
import com.example.androidweather.util.replaceElementFirstIsInstance
import com.example.androidweather.util.toWeatherIconUrl
import com.tbruyelle.rxpermissions3.RxPermissions
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableTransformer
import io.reactivex.rxjava3.functions.BiFunction
import timber.log.Timber
import java.util.Locale

/**
 * Contains and executes the business logic for all emitted [MviAction]
 * and returns one unique [Observable] of [MviResult].
 *
 *
 * This could have been included inside the [MviViewModel]
 * but was separated to ease maintenance, as the [MviViewModel] was getting too big.
 */
class MainActionProcessorHolder(
  private val application: Application,
  private val locationRepository: LocationRepository,
  private val rxPermissions: RxPermissions,
  private val searchHistoryRepository: SearchHistoryRepository,
  private val weatherRepository: WeatherRepository
) {

  private val changeTempFormatProcessor: ObservableTransformer<
      MainAction.ChangeTempFormatAction,
      MainResult.ChangeTempFormatResult> =
    ObservableTransformer { actions ->
      actions.map { MainResult.ChangeTempFormatResult(it.isChecked) }
    }

  private val dismissErrorProcessor: ObservableTransformer<
      MainAction.DismissErrorAction,
      MainResult.DismissErrorResult> =
    ObservableTransformer { actions ->
      actions.map { MainResult.DismissErrorResult }
    }

  private val getHistoryProcessor: ObservableTransformer<
      MainAction.GetHistoryAction,
      MainResult.GetHistoryResult> =
    ObservableTransformer { actions ->
      actions.flatMap {
        searchHistoryRepository.getLastSearchedCity()
          .flatMap { weatherRepository.getCityWeather(it.name) }
          .map { MainResult.GetHistoryResult.Success(it) }
          /** Wrap returned data into an immutable object */
          .cast(MainResult.GetHistoryResult::class.java)
          /**
           * Wrap any error into an immutable object and pass it down the reactive stream
           * and prevent crashing.
           */
          .onErrorReturn { throwable ->
            Timber.e(throwable)
            if (throwable is EmptyResultSetException) {
              MainResult.GetHistoryResult.NotFound
            } else {
              MainResult.GetHistoryResult.Failure(throwable)
            }
          }
          /**
           * Emit an InFlight event to notify the subscribers (e.g. the UI) we are
           * doing work and waiting on a response.
           * We emit it after observing on the UI thread to allow the event to be emitted
           * on the current frame and avoid jank.
           */
          .startWith(Observable.just(MainResult.GetHistoryResult.InFlight))
      }
    }

  private val hideKeyboardProcessor: ObservableTransformer<
      MainAction.HideKeyboardAction,
      MainResult.HideKeyboardResult> =
    ObservableTransformer { actions ->
      actions.map { MainResult.HideKeyboardResult }
    }

  private val searchCityProcessor: ObservableTransformer<
      MainAction.SearchCityAction,
      MainResult.SearchCityResult> =
    ObservableTransformer { actions ->
      actions.flatMap { action ->
        weatherRepository.getCityWeather(action.searchKeyword)
          .map { MainResult.SearchCityResult.Success(it) }
          .cast(MainResult.SearchCityResult::class.java)
          .onErrorReturn { throwable ->
            Timber.e(throwable)
            MainResult.SearchCityResult.Failure(throwable)
          }
          .startWith(Observable.just(MainResult.SearchCityResult.InFlight))
      }
    }

  @SuppressLint("MissingPermission")
  private val searchLocationProcessor: ObservableTransformer<
      MainAction.SearchLocationAction,
      MainResult.SearchLocationResult> =
    ObservableTransformer { actions ->
      actions.flatMap {
        rxPermissions.request(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION)
          .flatMap { isGranted ->
            if (isGranted) {
              locationRepository.getCurrentLocation()
                .flatMap { weatherRepository.getLocationWeather(it.latitude, it.longitude) }
                .map { MainResult.SearchLocationResult.Success(it) }
            } else {
              Observable.just(
                MainResult.SearchLocationResult.Failure(
                  Exception(application.getString(R.string.no_permission))
                )
              )
            }
          }
          .cast(MainResult.SearchLocationResult::class.java)
          .onErrorReturn { throwable ->
            Timber.e(throwable)
            MainResult.SearchLocationResult.Failure(throwable)
          }
          .startWith(Observable.just(MainResult.SearchLocationResult.InFlight))
      }
    }

  /**
   * The Reducer is where [MviViewState], which the [MviView] will use to
   * render itself, is created.
   * It takes the last cached [MviViewState], the latest [MviResult] to
   * create a new [MviViewState] by only updating the related fields.
   * This is basically like a big switch statement of all possible types for the [MviResult]
   */
  private val reducer = BiFunction<
      MainViewState,
      MainResult,
      MainViewState> { preState, result ->
    when (result) {
      is MainResult.ChangeTempFormatResult -> {
        preState.copy(
          controllerItems = preState.controllerItems
            .toMutableList()
            .replaceElementFirstIsInstance<
                MainControllerItem,
                MainControllerItem.WeatherItem> { weatherItem ->
              weatherItem.copy(
                tempStringResource = if (result.isChecked) {
                  R.string.temperature_degree_fahrenheit
                } else {
                  R.string.temperature_degree_celsius
                },
                feelsLikeStringResource = if (result.isChecked) {
                  R.string.feels_like_degree_fahrenheit
                } else {
                  R.string.feels_like_degree_celsius
                },
                highTempStringResource = if (result.isChecked) {
                  R.string.degree_fahrenheit
                } else {
                  R.string.degree_celsius
                },
                lowTempStringResource = if (result.isChecked) {
                  R.string.degree_fahrenheit
                } else {
                  R.string.degree_celsius
                },
                tempFormatStringResource = if (result.isChecked) {
                  R.string.degree_fahrenheit
                } else {
                  R.string.degree_celsius
                },
                isTempFormatChecked = !result.isChecked
              )
            }
        )
      }
      is MainResult.DismissErrorResult -> {
        preState.copy(
          errorMessage = null
        )
      }
      is MainResult.GetHistoryResult -> when (result) {
        is MainResult.GetHistoryResult.Success -> {
          preState.copy(
            isLoading = false,
            controllerItems = listOf(
              MainControllerItem.WeatherItem(
                id = result.response.id,
                cityName = result.response.name,
                weatherIconUrl = result.response
                  .weather
                  .firstOrNull()
                  ?.icon
                  .toWeatherIconUrl(),
                weatherDescription = result.response
                  .weather
                  .firstOrNull()
                  ?.description
                  ?.capitalize(Locale.getDefault()) ?: "",
                tempStringResource = R.string.temperature_degree_celsius,
                temp = result.response.main.temp.kelvinToCelsius().toInt(),
                feelsLikeStringResource = R.string.feels_like_degree_celsius,
                feelsLike = result.response.main.feelsLike.kelvinToCelsius().toInt(),
                highTempStringResource = R.string.degree_celsius,
                highTemp = result.response.main.tempMax.kelvinToCelsius().toInt(),
                lowTempStringResource = R.string.degree_celsius,
                lowTemp = result.response.main.tempMin.kelvinToCelsius().toInt(),
                tempFormatStringResource = R.string.degree_celsius,
                isTempFormatChecked = true
              )
            )
          )
        }
        is MainResult.GetHistoryResult.NotFound -> {
          preState.copy(
            isLoading = false,
            controllerItems = listOf(MainControllerItem.TutorialItem)
          )
        }
        is MainResult.GetHistoryResult.Failure -> {
          preState.copy(
            isLoading = false,
            errorMessage = result.error.message
          )
        }
        is MainResult.GetHistoryResult.InFlight -> {
          preState.copy(
            isLoading = true,
            isHideKeyboard = true
          )
        }
      }
      is MainResult.HideKeyboardResult -> {
        preState.copy(
          isHideKeyboard = false
        )
      }
      is MainResult.SearchCityResult -> when (result) {
        is MainResult.SearchCityResult.Success -> {
          val preIsTempFormatChecked = preState.controllerItems.getIsTempFormatChecked()
          preState.copy(
            isLoading = false,
            controllerItems = listOf(
              MainControllerItem.WeatherItem(
                id = result.response.id,
                cityName = result.response.name,
                weatherIconUrl = result.response
                  .weather
                  .firstOrNull()
                  ?.icon
                  ?.toWeatherIconUrl() ?: "",
                weatherDescription = result.response
                  .weather
                  .firstOrNull()
                  ?.description
                  ?.capitalize(Locale.getDefault()) ?: "",
                tempStringResource = if (preState.controllerItems.getIsTempFormatChecked()) {
                  R.string.temperature_degree_celsius
                } else {
                  R.string.temperature_degree_fahrenheit
                },
                temp = result.response.main.temp.kelvinToCelsius().toInt(),
                feelsLikeStringResource = if (preState.controllerItems.getIsTempFormatChecked()) {
                  R.string.feels_like_degree_celsius
                } else {
                  R.string.feels_like_degree_fahrenheit
                },
                feelsLike = result.response.main.feelsLike.kelvinToCelsius().toInt(),
                highTempStringResource = if (preIsTempFormatChecked) {
                  R.string.degree_celsius
                } else {
                  R.string.degree_fahrenheit
                },
                highTemp = result.response.main.tempMax.kelvinToCelsius().toInt(),
                lowTempStringResource = if (preIsTempFormatChecked) {
                  R.string.degree_celsius
                } else {
                  R.string.degree_fahrenheit
                },
                lowTemp = result.response.main.tempMin.kelvinToCelsius().toInt(),
                tempFormatStringResource = if (preIsTempFormatChecked) {
                  R.string.degree_celsius
                } else {
                  R.string.degree_fahrenheit
                },
                isTempFormatChecked = preIsTempFormatChecked
              )
            )
          )
        }
        is MainResult.SearchCityResult.Failure -> {
          preState.copy(
            isLoading = false,
            errorMessage = result.error.message
          )
        }
        is MainResult.SearchCityResult.InFlight -> {
          preState.copy(
            isLoading = true,
            isHideKeyboard = true
          )
        }
      }
      is MainResult.SearchLocationResult -> when (result) {
        is MainResult.SearchLocationResult.Success -> {
          val preIsTempFormatChecked = preState.controllerItems.getIsTempFormatChecked()
          preState.copy(
            isLoading = false,
            controllerItems = listOf(
              MainControllerItem.WeatherItem(
                id = result.response.id,
                cityName = result.response.name,
                weatherIconUrl = result.response
                  .weather
                  .firstOrNull()
                  ?.icon
                  ?.toWeatherIconUrl() ?: "",
                weatherDescription = result.response
                  .weather
                  .firstOrNull()
                  ?.description
                  ?.capitalize(Locale.getDefault()) ?: "",
                tempStringResource = if (preState.controllerItems.getIsTempFormatChecked()) {
                  R.string.temperature_degree_celsius
                } else {
                  R.string.temperature_degree_fahrenheit
                },
                temp = result.response.main.temp.kelvinToCelsius().toInt(),
                feelsLikeStringResource = if (preState.controllerItems.getIsTempFormatChecked()) {
                  R.string.feels_like_degree_celsius
                } else {
                  R.string.feels_like_degree_fahrenheit
                },
                feelsLike = result.response.main.feelsLike.kelvinToCelsius().toInt(),
                highTempStringResource = if (preIsTempFormatChecked) {
                  R.string.degree_celsius
                } else {
                  R.string.degree_fahrenheit
                },
                highTemp = result.response.main.tempMax.kelvinToCelsius().toInt(),
                lowTempStringResource = if (preIsTempFormatChecked) {
                  R.string.degree_celsius
                } else {
                  R.string.degree_fahrenheit
                },
                lowTemp = result.response.main.tempMin.kelvinToCelsius().toInt(),
                tempFormatStringResource = if (preIsTempFormatChecked) {
                  R.string.degree_celsius
                } else {
                  R.string.degree_fahrenheit
                },
                isTempFormatChecked = preIsTempFormatChecked
              )
            )
          )
        }
        is MainResult.SearchLocationResult.Failure -> {
          preState.copy(
            isLoading = false,
            errorMessage = result.error.message
          )
        }
        is MainResult.SearchLocationResult.InFlight -> {
          preState.copy(
            isLoading = true,
            isHideKeyboard = true
          )
        }
      }
    }
  }

  /**
   * Splits the [Observable] to match each type of [MviAction] to
   * its corresponding business logic processor. Each processor takes a defined [MviAction] and
   * returns a defined [MviResult]
   * The global actionProcessor then merges all [Observable] back to
   * one unique [Observable].
   *
   *
   * The splitting is done using [Observable.publish] which allows almost anything
   * on the passed [Observable] as long as one and only one [Observable] is returned.
   *
   *
   * An security layer is also added for unhandled [MviAction] to allow early crash
   * at runtime to easy maintenance.
   *
   *
   * Cache each state and pass it to the reducer to create a new state from
   * the previous cached one and the latest Result emitted from the action processor.
   * The scan operator is used to do the caching.
   */
  val actionProcessor = ObservableTransformer<
      MainAction,
      MainViewState> { actions ->
    actions.publish { shared ->
      Observable.mergeArray<MainResult>(
        shared.ofType(MainAction.ChangeTempFormatAction::class.java)
          .compose(changeTempFormatProcessor),
        shared.ofType(MainAction.DismissErrorAction::class.java)
          .compose(dismissErrorProcessor),
        shared.ofType(MainAction.GetHistoryAction::class.java)
          .compose(getHistoryProcessor),
        shared.ofType(MainAction.HideKeyboardAction::class.java)
          .compose(hideKeyboardProcessor),
        shared.ofType(MainAction.SearchCityAction::class.java)
          .compose(searchCityProcessor),
        shared.ofType(MainAction.SearchLocationAction::class.java)
          .compose(searchLocationProcessor),
        shared.filter {
          it !is MainAction.ChangeTempFormatAction
              && it !is MainAction.DismissErrorAction
              && it !is MainAction.GetHistoryAction
              && it !is MainAction.HideKeyboardAction
              && it !is MainAction.SearchCityAction
              && it !is MainAction.SearchLocationAction
        }
          .flatMap { w ->
            Observable.error<MainResult>(
              IllegalArgumentException("Unknown Action type: $w")
            )
          }
      )
    }
      .scan(MainViewState.initial(), reducer)
      .observeOn(AndroidSchedulers.mainThread())
  }
}