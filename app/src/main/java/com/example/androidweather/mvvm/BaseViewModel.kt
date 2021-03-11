package com.example.androidweather.mvvm

import androidx.annotation.Nullable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.disposables.CompositeDisposable

/**
 * Abstract implementation of [MvvmViewModel], providing view state manipulation methods.
 */
abstract class BaseViewModel<S : MvvmViewState>(
  initialViewState: S,
  private val isMainThread: () -> Boolean
) :
  ViewModel(),
  MvvmViewModel<S> {

  protected val disposables = CompositeDisposable()

  /**
   * Single source of truth for the current UI state, the [S] stored directly represents what the
   * [MvvmView] should show / render.
   *
   * This is made protected to ensure encapsulation.
   */
  protected val viewState = MutableLiveData<S>()

  init {
    viewState.value = initialViewState
  }

  override fun onCleared() {
    super.onCleared()
    disposables.clear()
  }

  /**
   * Transform [MutableLiveData] to [LiveData] before exposing, hence ensuring [S] stored is only
   * mutated by the [MvvmViewModel].
   */
  final override fun viewStateLiveData() = viewState

  /**
   * Getter function for [S] stored in the [MvvmViewModel].
   *
   * This is mainly created to avoid chaining of safe call operators `?.` when
   * accessing stored [S], as the function [LiveData.getValue] is marked [Nullable].
   *
   * In actual use case here it is unlikely the stored [S] would be `null`, as we
   * do initial value assigning in the initializer block.
   *
   * This method must be called from the main thread. If you call this from a background thread,
   * [IllegalStateException] will be thrown.
   *
   * @throws IllegalStateException
   */
  final override fun viewState(): S {
    check(isMainThread()) { "Cannot invoke viewState on a background thread" }
    return viewState.value!!
  }
}