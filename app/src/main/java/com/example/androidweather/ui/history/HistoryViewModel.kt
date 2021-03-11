package com.example.androidweather.ui.history

import android.os.Looper
import androidx.lifecycle.LiveData
import com.example.androidweather.mvvm.BaseViewModel
import com.example.androidweather.mvvm.MvvmViewState
import com.example.androidweather.repository.SearchHistoryRepository
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.plusAssign
import io.reactivex.rxjava3.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

/**
 * Contains and executes all business logic, and returns one unique [LiveData] of [MvvmViewState].
 */
class HistoryViewModel @Inject constructor(
  private val searchHistoryRepository: SearchHistoryRepository,
  isMainThread: () -> Boolean
) : BaseViewModel<HistoryViewState>(HistoryViewState.initial(), isMainThread) {

  fun dismissError() {
    viewState.value = viewState().copy(
      errorMessage = null
    )
  }

  fun getSearchHistory() {
    disposables += searchHistoryRepository.getSearchHistory()
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .map { cities ->
        viewState().copy(
          isLoading = false,
          controllerItems = cities.map { city ->
            HistoryControllerItem.CityItem(
              id = city.id,
              name = city.name
            )
          }
        )
      }
      .onErrorReturn {
        Timber.e(it)
        viewState().copy(
          isLoading = false,
          errorMessage = it.message
        )
      }
      .startWith(
        Observable.just(
          viewState().copy(
            isLoading = true
          )
        )
      )
      .subscribe {
        viewState.value = it
      }
  }

  fun removeSearchHistory(id: Int) {
    disposables += searchHistoryRepository.remove(id)
      .flatMap { searchHistoryRepository.getSearchHistory() }
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .map { cities ->
        viewState().copy(
          isLoading = false,
          controllerItems = cities.map { city ->
            HistoryControllerItem.CityItem(
              id = city.id,
              name = city.name
            )
          }
        )
      }
      .onErrorReturn {
        Timber.e(it)
        viewState().copy(
          isLoading = false,
          errorMessage = it.message
        )
      }
      .startWith(
        Observable.just(
          viewState().copy(
            isLoading = true
          )
        )
      )
      .subscribe {
        viewState.value = it
      }
  }
}