package com.example.androidweather.ui.main

import com.example.androidweather.mvi.MviIntent

sealed class MainIntent : MviIntent {

  object DismissErrorIntent : MainIntent()

  object HideKeyboardIntent : MainIntent()

  object InitialIntent : MainIntent()

  data class RefreshIntent(val searchKeyword: String) : MainIntent()

  data class SearchCityIntent(val searchKeyword: String) : MainIntent()

  object SearchLocationIntent : MainIntent()
}