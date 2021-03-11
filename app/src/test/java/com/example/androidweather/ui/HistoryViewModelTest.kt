package com.example.androidweather.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.androidweather.RxImmediateSchedulerRule
import com.example.androidweather.TestObserver
import com.example.androidweather.db.City
import com.example.androidweather.db.CityDao
import com.example.androidweather.repository.SearchHistoryRepository
import com.example.androidweather.ui.history.HistoryControllerItem
import com.example.androidweather.ui.history.HistoryViewModel
import com.example.androidweather.ui.history.HistoryViewState
import io.reactivex.rxjava3.core.Single
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.anyInt
import org.mockito.MockitoAnnotations

class HistoryViewModelTest {

  private lateinit var historyViewModel: HistoryViewModel

  private lateinit var searchHistoryRepository: SearchHistoryRepository

  private lateinit var testObserver: TestObserver<HistoryViewState>

  @Mock
  private lateinit var cityDao: CityDao

  @Rule
  @JvmField
  val instantExecutorRule = InstantTaskExecutorRule()

  @Rule
  @JvmField
  val rxImmediateSchedulerRule = RxImmediateSchedulerRule()

  @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)

    searchHistoryRepository = SearchHistoryRepository(cityDao)
    historyViewModel = HistoryViewModel(searchHistoryRepository) { true }
    testObserver = TestObserver<HistoryViewState>().also {
      historyViewModel.viewStateLiveData().observeForever(it)
    }
  }

  @Test
  fun getSearchHistory_success() {
    `when`(cityDao.getAll())
      .thenReturn(
        Single.just(
          listOf(
            City(
              id = 1,
              name = "City 1",
              lastSearchedAt = 1
            ),
            City(
              id = 2,
              name = "City 2",
              lastSearchedAt = 2
            )
          )
        )
      )

    historyViewModel.getSearchHistory()

    assert(testObserver.values().size == 3)
    testObserver.assertValueAt(0, HistoryViewState.initial())
    testObserver.assertValueAt(1) {
      it.isLoading && it.controllerItems.isEmpty() && it.errorMessage == null
    }
    testObserver.assertValueAt(2) {
      !it.isLoading
          && it.controllerItems == listOf(
        HistoryControllerItem.CityItem(
          id = 1,
          name = "City 1"
        ),
        HistoryControllerItem.CityItem(
          id = 2,
          name = "City 2"
        )
      )
          && it.errorMessage == null
    }
  }

  @Test
  fun getSearchHistory_failure() {
    `when`(cityDao.getAll())
      .thenReturn(Single.error(Exception(errorMessage)))

    historyViewModel.getSearchHistory()

    assert(testObserver.values().size == 3)
    testObserver.assertValueAt(0, HistoryViewState.initial())
    testObserver.assertValueAt(1) {
      it.isLoading && it.controllerItems.isEmpty() && it.errorMessage == null
    }
    testObserver.assertValueAt(2) {
      !it.isLoading
          && it.controllerItems.isEmpty()
          && it.errorMessage == errorMessage
    }
  }

  @Test
  fun removeSearchHistory_success() {
    `when`(cityDao.getAll())
      .thenReturn(
        Single.just(
          listOf(
            City(
              id = 1,
              name = "City 1",
              lastSearchedAt = 1
            ),
            City(
              id = 2,
              name = "City 2",
              lastSearchedAt = 2
            )
          )
        )
      )
      .thenReturn(
        Single.just(
          listOf(
            City(
              id = 2,
              name = "City 2",
              lastSearchedAt = 2
            )
          )
        )
      )
    `when`(cityDao.deleteCityById(anyInt())).thenReturn(Single.just(1))

    historyViewModel.getSearchHistory()
    historyViewModel.removeSearchHistory(id = 1)

    assert(testObserver.values().size == 5)
    testObserver.assertValueAt(0, HistoryViewState.initial())
    testObserver.assertValueAt(1) {
      it.isLoading && it.controllerItems.isEmpty() && it.errorMessage == null
    }
    testObserver.assertValueAt(2) {
      !it.isLoading
          && it.controllerItems == listOf(
        HistoryControllerItem.CityItem(
          id = 1,
          name = "City 1"
        ),
        HistoryControllerItem.CityItem(
          id = 2,
          name = "City 2"
        )
      )
          && it.errorMessage == null
    }
    testObserver.assertValueAt(3) {
      it.isLoading
          && it.controllerItems == listOf(
        HistoryControllerItem.CityItem(
          id = 1,
          name = "City 1"
        ),
        HistoryControllerItem.CityItem(
          id = 2,
          name = "City 2"
        )
      )
          && it.errorMessage == null
    }
    testObserver.assertValueAt(4) {
      !it.isLoading
          && it.controllerItems == listOf(
        HistoryControllerItem.CityItem(
          id = 2,
          name = "City 2"
        )
      )
          && it.errorMessage == null
    }
  }

  @Test
  fun removeSearchHistory_failure() {
    `when`(cityDao.getAll())
      .thenReturn(
        Single.just(
          listOf(
            City(
              id = 1,
              name = "City 1",
              lastSearchedAt = 1
            ),
            City(
              id = 2,
              name = "City 2",
              lastSearchedAt = 2
            )
          )
        )
      )
    `when`(cityDao.deleteCityById(anyInt())).thenReturn(Single.error(Exception(errorMessage)))

    historyViewModel.getSearchHistory()
    historyViewModel.removeSearchHistory(id = 1)

    assert(testObserver.values().size == 5)
    testObserver.assertValueAt(0, HistoryViewState.initial())
    testObserver.assertValueAt(1) {
      it.isLoading && it.controllerItems.isEmpty() && it.errorMessage == null
    }
    testObserver.assertValueAt(2) {
      !it.isLoading
          && it.controllerItems == listOf(
        HistoryControllerItem.CityItem(
          id = 1,
          name = "City 1"
        ),
        HistoryControllerItem.CityItem(
          id = 2,
          name = "City 2"
        )
      )
          && it.errorMessage == null
    }
    testObserver.assertValueAt(3) {
      it.isLoading
          && it.controllerItems == listOf(
        HistoryControllerItem.CityItem(
          id = 1,
          name = "City 1"
        ),
        HistoryControllerItem.CityItem(
          id = 2,
          name = "City 2"
        )
      )
          && it.errorMessage == null
    }
    testObserver.assertValueAt(4) {
      !it.isLoading
          && it.controllerItems == listOf(
        HistoryControllerItem.CityItem(
          id = 1,
          name = "City 1"
        ),
        HistoryControllerItem.CityItem(
          id = 2,
          name = "City 2"
        )
      )
          && it.errorMessage == errorMessage
    }
  }

  @Test
  fun dismissError() {
    `when`(cityDao.getAll())
      .thenReturn(Single.error(Exception(errorMessage)))

    historyViewModel.getSearchHistory()
    historyViewModel.dismissError()

    assert(testObserver.values().size == 4)
    testObserver.assertValueAt(0, HistoryViewState.initial())
    testObserver.assertValueAt(1) {
      it.isLoading && it.controllerItems.isEmpty() && it.errorMessage == null
    }
    testObserver.assertValueAt(2) {
      !it.isLoading
          && it.controllerItems.isEmpty()
          && it.errorMessage == errorMessage
    }
    testObserver.assertValueAt(3) {
      !it.isLoading
          && it.controllerItems.isEmpty()
          && it.errorMessage == null
    }
  }

  companion object {
    const val errorMessage = "Some error"
  }
}