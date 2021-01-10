package com.example.androidweather.util

class KeyedListener<Key, Listener> private constructor(
  val identifier: Key,
  val callback: Listener
) {

  companion object {
    @JvmStatic
    fun <Key, Listener> create(
      identifier: Key,
      callback: Listener
    ): KeyedListener<Key, Listener> {
      return KeyedListener(identifier, callback)
    }
  }

  /** Only include the key, and not the listener, in equals/hashcode */
  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is KeyedListener<*, *>) return false
    return identifier == other.identifier
  }

  override fun hashCode() = identifier?.hashCode() ?: 0
}