package com.example.androidweather.util

/**
 * Replace an element by the copied value of `copiedValue` that first matches instances of specified type parameter T.
 *
 * @param copiedValue The function to create a new value for the replaced index
 */
inline fun <E, reified T : E> MutableList<E>.replaceElementFirstIsInstance(
  copiedValue: (T) -> T
): MutableList<E> {
  val t = filterIsInstance<T>().firstOrNull()
  if (t != null) {
    set(indexOf(t), copiedValue(t))
  }

  return this
}