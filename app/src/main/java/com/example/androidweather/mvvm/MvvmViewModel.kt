package com.example.androidweather.mvvm

import androidx.lifecycle.LiveData

/**
 * Object that will be subscribed by a [MvvmView] and emits [MvvmViewState].
 *
 * @param S Top class of the [MvvmViewState] the [MvvmViewModel] will be emitting.
 */
interface MvvmViewModel<S : MvvmViewState> {
  /** Getter function for live data to be observed by the view */
  fun viewStateLiveData(): LiveData<S>

  /** Getter function for current [S] stored in the view model. */
  fun viewState(): S
}