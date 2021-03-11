package com.example.androidweather

import androidx.lifecycle.Observer
import io.reactivex.rxjava3.internal.util.ExceptionHelper
import io.reactivex.rxjava3.observers.BaseTestConsumer

class TestObserver<T> : Observer<T> {

  private val observedValues = mutableListOf<T>()

  override fun onChanged(value: T) {
    observedValues.add(value)
  }

  fun values(): List<T> {
    return observedValues
  }

  /**
   * Equivalent implementation of [BaseTestConsumer.assertValueAt]
   */
  fun assertValueAt(index: Int, value: T): TestObserver<T> {
    val s = observedValues.size
    if (s == 0) {
      throw AssertionError("No values")
    }

    if (index >= s) {
      throw AssertionError("Invalid index: $index")
    }

    val v = observedValues.get(index)
    if (value != v) {
      throw AssertionError(
        "expected: " + BaseTestConsumer.valueAndClass(value)
            + " but was: " + BaseTestConsumer.valueAndClass(v)
      )
    }
    return this
  }

  /**
   * Equivalent implementation of [BaseTestConsumer.assertValueAt]
   */
  fun assertValueAt(index: Int, valuePredicate: (T) -> Boolean): TestObserver<T> {
    val s = observedValues.size
    if (s == 0) {
      throw AssertionError("No values")
    }

    if (index >= observedValues.size) {
      throw AssertionError("Invalid index: $index")
    }

    var found = false

    try {
      observedValues[index]?.let {
        if (valuePredicate(it)) {
          found = true
        }
      }
    } catch (ex: Exception) {
      throw ExceptionHelper.wrapOrThrow(ex)
    }

    if (!found) {
      throw AssertionError("Value not present")
    }
    return this
  }
}