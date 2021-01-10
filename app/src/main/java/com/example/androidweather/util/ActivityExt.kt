package com.example.androidweather.util

import android.app.Activity
import android.content.Context
import android.os.IBinder
import android.view.inputmethod.InputMethodManager

/**
 * Extension function to hide soft input
 */
fun Activity.hideSoftInput() {
  currentFocus?.windowToken?.let {
    hideSoftInput(it)
  }
}

private fun Activity.hideSoftInput(windowToken: IBinder) {
  (getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)?.apply {
    hideSoftInputFromWindow(windowToken, 0)
  }
}