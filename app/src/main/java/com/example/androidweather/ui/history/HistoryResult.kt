package com.example.androidweather.ui.history

import com.example.androidweather.db.City

sealed class HistoryResult {

  object DismissErrorResult : HistoryResult()

  sealed class GetSearchHistoryResult : HistoryResult() {

    data class Success(val cities: List<City>) : GetSearchHistoryResult()

    data class Failure(val error: Throwable) : GetSearchHistoryResult()

    object InFlight : GetSearchHistoryResult()
  }

  sealed class RemoveSearchHistoryResult : HistoryResult() {

    data class Success(val cities: List<City>) : RemoveSearchHistoryResult()

    data class Failure(val error: Throwable) : RemoveSearchHistoryResult()

    object InFlight : RemoveSearchHistoryResult()
  }
}