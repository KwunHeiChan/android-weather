package com.example.androidweather.ui.main

import com.example.androidweather.mvi.BaseViewModel
import com.example.androidweather.mvi.MviAction
import com.example.androidweather.mvi.MviIntent
import com.google.android.material.snackbar.Snackbar
import com.jakewharton.rxrelay3.PublishRelay
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableTransformer

/**
 * Listens to user actions from the UI([MainActivity]), retrieves the data and updates
 * the UI as required.
 *
 * @property mainActionProcessorHolder Contains and executes the business logic of all emitted
 * actions.
 */
class MainViewModel(
  private val mainActionProcessorHolder: MainActionProcessorHolder
) : BaseViewModel<MainIntent, MainViewState>() {

  override fun compose(intentsReplay: PublishRelay<MainIntent>): Observable<MainViewState> =
    intentsReplay
      .compose(intentFilter)
      .map(this::actionFromIntent)
      .compose(mainActionProcessorHolder.actionProcessor)
      /**
       * When a reducer just emits the previous state, there's no reason to call render.
       * In fact, redrawing the UI in cases like this can cause jank (e.g. messing up
       * [Snackbar] animations by showing the same [Snackbar] twice in rapid succession).
       */
      .distinctUntilChanged()
      /**
       * Emit the last event of the reactive stream on subscription.
       * Useful when a View rebinds to the ViewModel after rotation.
       */
      .replay(1)
      /**
       * Create the reactive stream on creation without waiting for subscription.
       * This allows the reactive stream to stay alive even when the UI disconnects and
       * match the stream's lifecycle to the ViewModel's one.
       */
      .autoConnect(0)

  /**
   * take only the first ever [MainIntent.InitialIntent] and all intents of other types
   * to avoid reloading data on config changes
   */
  private val intentFilter: ObservableTransformer<MainIntent, MainIntent> =
    ObservableTransformer { intents ->
      intents.publish { shared ->
        Observable.merge(
          shared.ofType(MainIntent.InitialIntent::class.java).take(1),
          shared.filter { it !is MainIntent.InitialIntent })
      }
    }

  /**
   * Translate an [MviIntent] to an [MviAction].
   * Used to decouple the UI and the business logic to allow easy testing and better reusability.
   */
  private fun actionFromIntent(mainIntent: MainIntent): MainAction {
    return when (mainIntent) {
      is MainIntent.DismissErrorIntent -> {
        MainAction.DismissErrorAction
      }
      is MainIntent.HideKeyboardIntent -> {
        MainAction.HideKeyboardAction
      }
      is MainIntent.InitialIntent -> {
        MainAction.GetHistoryAction
      }
      is MainIntent.RefreshIntent -> {
        MainAction.SearchCityAction(mainIntent.searchKeyword)
      }
      is MainIntent.SearchCityIntent -> {
        MainAction.SearchCityAction(mainIntent.searchKeyword)
      }
      is MainIntent.SearchLocationIntent -> {
        MainAction.SearchLocationAction
      }
    }
  }
}