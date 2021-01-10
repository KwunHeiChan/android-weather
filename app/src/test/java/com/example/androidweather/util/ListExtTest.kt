package com.example.androidweather.util

import org.junit.Assert.assertEquals
import org.junit.Test

class ListExtTest {

  sealed class Parent {

    data class ChildA(val id: Int) : Parent()

    data class ChildB(val id: Int) : Parent()
  }

  @Test
  fun replaceElementFirstIsInstance_hit() {
    assertEquals(
      /** Make sure only first element of the targeted type is replaced */
      mutableListOf<Parent>(Parent.ChildA(3), Parent.ChildA(2)),
      mutableListOf<Parent>(Parent.ChildA(1), Parent.ChildA(2))
        .replaceElementFirstIsInstance<
            Parent,
            Parent.ChildA> { childA ->
          childA.copy(
            id = 3
          )
        }
    )
  }

  @Test
  fun replaceElementFirstIsInstance_miss() {
    assertEquals(
      mutableListOf<Parent>(Parent.ChildB(1), Parent.ChildB(2)),
      mutableListOf<Parent>(Parent.ChildB(1), Parent.ChildB(2))
        .replaceElementFirstIsInstance<
            Parent,
            Parent.ChildA> { childA ->
          childA.copy(
            id = 3
          )
        }
    )
  }
}