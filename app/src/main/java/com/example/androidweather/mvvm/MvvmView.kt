package com.example.androidweather.mvvm

/**
 * Object representing a UI that will subscribe to a view model for rendering its UI.
 *
 * @param VM Top class of the [MvvmViewModel] that the [MvvmView] will be subscribing to.
 * @param S Top class of the [MvvmViewState] the [MvvmView] will be subscribing to.
 */
interface MvvmView<VM : MvvmViewModel<S>, S : MvvmViewState> {
  fun render(state: S)
}