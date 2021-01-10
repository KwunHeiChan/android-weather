package com.example.androidweather.ui.history

import com.example.androidweather.mvi.MviAction
import com.example.androidweather.mvi.MviResult
import com.example.androidweather.mvi.MviView
import com.example.androidweather.mvi.MviViewModel
import com.example.androidweather.mvi.MviViewState
import com.example.androidweather.repository.SearchHistoryRepository
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableTransformer
import io.reactivex.rxjava3.functions.BiFunction
import timber.log.Timber

/**
 * Contains and executes the business logic for all emitted [MviAction]
 * and returns one unique [Observable] of [MviResult].
 *
 *
 * This could have been included inside the [MviViewModel]
 * but was separated to ease maintenance, as the [MviViewModel] was getting too big.
 */
class HistoryActionProcessorHolder(
  private val searchHistoryRepository: SearchHistoryRepository
) {

  private val dismissErrorProcessor: ObservableTransformer<
      HistoryAction.DismissErrorAction,
      HistoryResult.DismissErrorResult> =
    ObservableTransformer { actions ->
      actions.map { HistoryResult.DismissErrorResult }
    }

  private val getSearchHistoryProcessor: ObservableTransformer<
      HistoryAction.GetSearchHistoryAction,
      HistoryResult.GetSearchHistoryResult> =
    ObservableTransformer { actions ->
      actions.flatMap {
        searchHistoryRepository.getSearchHistory()
          .map { HistoryResult.GetSearchHistoryResult.Success(it) }
          .cast(HistoryResult.GetSearchHistoryResult::class.java)
          .onErrorReturn { throwable ->
            Timber.e(throwable)
            HistoryResult.GetSearchHistoryResult.Failure(throwable)
          }
          .startWith(Observable.just(HistoryResult.GetSearchHistoryResult.InFlight))
      }
    }

  private val removeSearchHistoryProcessor: ObservableTransformer<
      HistoryAction.RemoveSearchHistoryAction,
      HistoryResult.RemoveSearchHistoryResult> =
    ObservableTransformer { actions ->
      actions.flatMap { action ->
        searchHistoryRepository.remove(action.id)
          .flatMap { searchHistoryRepository.getSearchHistory() }
          .map { HistoryResult.RemoveSearchHistoryResult.Success(it) }
          .cast(HistoryResult.RemoveSearchHistoryResult::class.java)
          .onErrorReturn { throwable ->
            Timber.e(throwable)
            HistoryResult.RemoveSearchHistoryResult.Failure(throwable)
          }
          .startWith(Observable.just(HistoryResult.RemoveSearchHistoryResult.InFlight))
      }
    }

  /**
   * The Reducer is where [MviViewState], which the [MviView] will use to
   * render itself, is created.
   * It takes the last cached [MviViewState], the latest [MviResult] to
   * create a new [MviViewState] by only updating the related fields.
   * This is basically like a big switch statement of all possible types for the [MviResult]
   */
  private val reducer = BiFunction<
      HistoryViewState,
      HistoryResult,
      HistoryViewState> { preState, result ->
    when (result) {
      is HistoryResult.DismissErrorResult -> {
        preState.copy(
          errorMessage = null
        )
      }
      is HistoryResult.GetSearchHistoryResult -> when (result) {
        is HistoryResult.GetSearchHistoryResult.Success -> {
          preState.copy(
            isLoading = false,
            controllerItems = result.cities.map { city ->
              HistoryControllerItem.CityItem(
                id = city.id,
                name = city.name
              )
            }
          )
        }
        is HistoryResult.GetSearchHistoryResult.Failure -> {
          preState.copy(
            isLoading = false,
            errorMessage = result.error.message
          )
        }
        is HistoryResult.GetSearchHistoryResult.InFlight -> {
          preState.copy(
            isLoading = true
          )
        }
      }
      is HistoryResult.RemoveSearchHistoryResult -> when (result) {
        is HistoryResult.RemoveSearchHistoryResult.Success -> {
          preState.copy(
            isLoading = false,
            controllerItems = result.cities.map { city ->
              HistoryControllerItem.CityItem(
                id = city.id,
                name = city.name
              )
            }
          )
        }
        is HistoryResult.RemoveSearchHistoryResult.Failure -> {
          preState.copy(
            isLoading = false,
            errorMessage = result.error.message
          )
        }
        is HistoryResult.RemoveSearchHistoryResult.InFlight -> {
          preState.copy(
            isLoading = true
          )
        }
      }
    }
  }

  /**
   * Splits the [Observable] to match each type of [MviAction] to
   * its corresponding business logic processor. Each processor takes a defined [MviAction] and
   * returns a defined [MviResult]
   * The global actionProcessor then merges all [Observable] back to
   * one unique [Observable].
   *
   *
   * The splitting is done using [Observable.publish] which allows almost anything
   * on the passed [Observable] as long as one and only one [Observable] is returned.
   *
   *
   * An security layer is also added for unhandled [MviAction] to allow early crash
   * at runtime to easy maintenance.
   *
   *
   * Cache each state and pass it to the reducer to create a new state from
   * the previous cached one and the latest Result emitted from the action processor.
   * The scan operator is used to do the caching.
   */
  val actionProcessor = ObservableTransformer<
      HistoryAction,
      HistoryViewState> { actions ->
    actions.publish { shared ->
      Observable.mergeArray<HistoryResult>(
        shared.ofType(HistoryAction.DismissErrorAction::class.java)
          .compose(dismissErrorProcessor),
        shared.ofType(HistoryAction.GetSearchHistoryAction::class.java)
          .compose(getSearchHistoryProcessor),
        shared.ofType(HistoryAction.RemoveSearchHistoryAction::class.java)
          .compose(removeSearchHistoryProcessor),
        shared.filter {
          it !is HistoryAction.DismissErrorAction
              && it !is HistoryAction.GetSearchHistoryAction
              && it !is HistoryAction.RemoveSearchHistoryAction
        }
          .flatMap { w ->
            Observable.error<HistoryResult>(
              IllegalArgumentException("Unknown Action type: $w")
            )
          }
      )
    }
      .scan(HistoryViewState.initial(), reducer)
      .observeOn(AndroidSchedulers.mainThread())
  }
}