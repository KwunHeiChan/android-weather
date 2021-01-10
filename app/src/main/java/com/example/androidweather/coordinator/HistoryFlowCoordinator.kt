package com.example.androidweather.coordinator

import javax.inject.Inject
import javax.inject.Singleton

/**
 * A Coordinator is an abstraction of the navigation layer. This level of abstraction decouples any in-app
 * navigation business logic from UI (activity / fragment) and view models.
 *
 * This will come in handy when the applications grows in size and eventually involves complicated navigation logic,
 * like A/B testing.
 */
@Singleton
class HistoryFlowCoordinator @Inject constructor(private val navigator: Navigator) {

  fun start() {
    navigator.showSearchHistory()
  }
}