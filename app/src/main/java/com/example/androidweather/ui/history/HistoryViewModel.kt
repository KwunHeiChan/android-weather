package com.example.androidweather.ui.history

import com.example.androidweather.mvi.BaseViewModel
import com.example.androidweather.mvi.MviAction
import com.example.androidweather.mvi.MviIntent
import com.google.android.material.snackbar.Snackbar
import com.jakewharton.rxrelay3.PublishRelay
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableTransformer

/**
 * Listens to user actions from the UI([HistoryActivity]), retrieves the data and updates
 * the UI as required.
 *
 * @property historyActionProcessorHolder Contains and executes the business logic of all emitted
 * actions.
 */
class HistoryViewModel(
  private val historyActionProcessorHolder: HistoryActionProcessorHolder
) : BaseViewModel<HistoryIntent, HistoryViewState>() {

  override fun compose(intentsReplay: PublishRelay<HistoryIntent>): Observable<HistoryViewState> =
    intentsReplay
      .compose(intentFilter)
      .map(this::actionFromIntent)
      .compose(historyActionProcessorHolder.actionProcessor)
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
   * take only the first ever [HistoryIntent.InitialIntent] and all intents of other types
   * to avoid reloading data on config changes
   */
  private val intentFilter: ObservableTransformer<HistoryIntent, HistoryIntent> =
    ObservableTransformer { intents ->
      intents.publish { shared ->
        Observable.merge(
          shared.ofType(HistoryIntent.InitialIntent::class.java).take(1),
          shared.filter { it !is HistoryIntent.InitialIntent })
      }
    }

  /**
   * Translate an [MviIntent] to an [MviAction].
   * Used to decouple the UI and the business logic to allow easy testing and better reusability.
   */
  private fun actionFromIntent(historyIntent: HistoryIntent): HistoryAction {
    return when (historyIntent) {
      is HistoryIntent.DismissErrorIntent -> {
        HistoryAction.DismissErrorAction
      }
      is HistoryIntent.InitialIntent -> {
        HistoryAction.GetSearchHistoryAction
      }
      is HistoryIntent.RemoveSearchHistoryIntent -> {
        HistoryAction.RemoveSearchHistoryAction(historyIntent.id)
      }
    }
  }
}