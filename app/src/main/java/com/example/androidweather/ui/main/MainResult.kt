package com.example.androidweather.ui.main

import com.example.androidweather.model.GetWeatherResponse

sealed class MainResult {

  object DismissErrorResult : MainResult()

  sealed class GetHistoryResult : MainResult() {

    data class Success(val response: GetWeatherResponse) : GetHistoryResult()

    object NotFound : GetHistoryResult()

    data class Failure(val error: Throwable) : GetHistoryResult()

    object InFlight : GetHistoryResult()
  }

  object HideKeyboardResult : MainResult()

  sealed class SearchCityResult : MainResult() {

    data class Success(val response: GetWeatherResponse) : SearchCityResult()

    data class Failure(val error: Throwable) : SearchCityResult()

    object InFlight : SearchCityResult()
  }

  sealed class SearchLocationResult : MainResult() {

    data class Success(val response: GetWeatherResponse) : SearchLocationResult()

    data class Failure(val error: Throwable) : SearchLocationResult()

    object InFlight : SearchLocationResult()
  }
}