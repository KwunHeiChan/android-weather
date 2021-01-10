package com.example.androidweather.ui.main

sealed class MainAction {

  data class ChangeTempFormatAction(val isChecked: Boolean) : MainAction()

  object DismissErrorAction : MainAction()

  object GetHistoryAction : MainAction()

  object HideKeyboardAction : MainAction()

  data class SearchCityAction(val searchKeyword: String) : MainAction()

  object SearchLocationAction : MainAction()
}