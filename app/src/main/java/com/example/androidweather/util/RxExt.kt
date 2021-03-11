package com.example.androidweather.util

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit

/**
 * Compose the [Observable.throttleFirst] operator to prevent multiple events being emitted at a
 * short period of time, typically used to prevent multiple click events emitted at a short period
 * of time.
 *
 * @param windowDuration the default value is `500`
 * @param timeUnit the default value is [TimeUnit.MILLISECONDS]
 * @param scheduler the default value is [Schedulers.computation], the same as that of [Observable.throttleFirst]
 */
fun <T> Observable<T>.preventMultipleClicks(
  windowDuration: Long = 500,
  timeUnit: TimeUnit = TimeUnit.MILLISECONDS,
  scheduler: Scheduler = Schedulers.computation()
): Observable<T> {
  return compose { upstream -> upstream.throttleFirst(windowDuration, timeUnit, scheduler) }
}