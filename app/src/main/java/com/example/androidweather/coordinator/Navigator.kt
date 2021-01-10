package com.example.androidweather.coordinator

import com.example.androidweather.manager.ActivityManager
import com.example.androidweather.ui.history.HistoryActivity
import javax.inject.Inject
import javax.inject.Singleton

/**
 * A [Navigator] holds the actual navigation implementation and is responsible for its execution.
 *
 * It decouples the navigation implementation from the navigation calling class, as the navigation calling class
 * should not care about the destination implementation (activity / fragment / web view).
 */
@Singleton
class Navigator @Inject constructor(private val activityManager: ActivityManager) {

  fun showSearchHistory() {
    activityManager.currentActivity.get()?.let { activity ->
      activity.startActivity(HistoryActivity.newIntent(activity))
    }
  }
}