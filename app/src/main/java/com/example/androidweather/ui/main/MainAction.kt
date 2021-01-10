package com.example.androidweather.ui.main

sealed class MainAction {

  object DismissErrorAction : MainAction()

  object GetHistoryAction : MainAction()

  object HideKeyboardAction : MainAction()

  data class SearchCityAction(val searchKeyword: String) : MainAction()

  object SearchLocationAction : MainAction()
}