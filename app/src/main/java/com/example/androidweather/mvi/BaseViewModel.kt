package com.example.androidweather.mvi

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import com.jakewharton.rxrelay3.PublishRelay
import io.reactivex.rxjava3.core.Observable

/**
 * Abstract implementation of [MviViewModel], connecting [MviIntent] and [MviViewState]
 */
abstract class BaseViewModel<I : MviIntent, S : MviViewState> : ViewModel(), MviViewModel<I, S> {

  /**
   * Proxy subject used to keep the stream alive even after the UI gets recycled.
   * This is basically used to keep ongoing events and the last cached State alive
   * while the UI disconnects and reconnects on config changes.
   */
  private val intentsSubject = PublishRelay.create<I>()
  private val statesObservable: Observable<S> by lazy { compose(intentsSubject) }

  @SuppressLint("CheckResult")
  override fun processIntents(intents: Observable<I>) {
    intents.subscribe(intentsSubject)
  }

  override fun states(): Observable<S> = statesObservable

  /**
   * Compose all components to create the stream logic
   */
  abstract fun compose(intentsReplay: PublishRelay<I>): Observable<S>
}