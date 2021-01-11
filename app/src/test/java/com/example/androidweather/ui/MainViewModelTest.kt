package com.example.androidweather.ui

import android.app.Application
import android.location.Location
import androidx.room.rxjava3.EmptyResultSetException
import com.example.androidweather.R
import com.example.androidweather.RxImmediateSchedulerRule
import com.example.androidweather.api.WeatherService
import com.example.androidweather.db.City
import com.example.androidweather.db.CityDao
import com.example.androidweather.model.GetWeatherResponse
import com.example.androidweather.repository.LocationRepository
import com.example.androidweather.repository.SearchHistoryRepository
import com.example.androidweather.repository.WeatherRepository
import com.example.androidweather.ui.main.MainActionProcessorHolder
import com.example.androidweather.ui.main.MainControllerItem
import com.example.androidweather.ui.main.MainIntent
import com.example.androidweather.ui.main.MainViewModel
import com.example.androidweather.ui.main.MainViewState
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.tbruyelle.rxpermissions3.RxPermissions
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.observers.TestObserver
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.any
import org.mockito.Mockito.anyDouble
import org.mockito.Mockito.anyString
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations
import java.io.File

class MainViewModelTest {

  private lateinit var getWeatherResponse: GetWeatherResponse

  private lateinit var mainActionProcessorHolder: MainActionProcessorHolder

  private lateinit var mainViewModel: MainViewModel

  private lateinit var searchHistoryRepository: SearchHistoryRepository

  private lateinit var weatherRepository: WeatherRepository

  private lateinit var testObserver: TestObserver<MainViewState>

  @Mock
  private lateinit var application: Application

  @Mock
  private lateinit var cityDao: CityDao

  @Mock
  private lateinit var locationRepository: LocationRepository

  @Mock
  private lateinit var rxPermissions: RxPermissions

  @Mock
  private lateinit var weatherService: WeatherService

  @Rule
  @JvmField
  val scheduler = RxImmediateSchedulerRule()

  private val objectMapper: ObjectMapper =
    ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

  @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)

    val getWeatherJson = this::class.java.classLoader.getJsonFromResource("get_weather.json")
    getWeatherResponse = objectMapper.readValue(getWeatherJson, GetWeatherResponse::class.java)

    searchHistoryRepository = SearchHistoryRepository(cityDao)
    weatherRepository = WeatherRepository(searchHistoryRepository, weatherService)
    mainActionProcessorHolder = MainActionProcessorHolder(
      application,
      locationRepository,
      rxPermissions,
      searchHistoryRepository,
      weatherRepository
    )
    mainViewModel = MainViewModel(mainActionProcessorHolder)
    testObserver = mainViewModel.states().test()
  }

  @Test
  fun initialIntent_getLastSearchedCity_notFound() {
    `when`(cityDao.getLastSearchedCity())
      .thenReturn(Single.error(EmptyResultSetException(errorMessage)))

    mainViewModel.processIntents(Observable.just(MainIntent.InitialIntent))

    assert(testObserver.values().size == 3)
    testObserver.assertValueAt(0, MainViewState.initial())
    testObserver.assertValueAt(1) {
      it.isLoading && it.controllerItems.isEmpty() && it.errorMessage == null && it.isHideKeyboard
    }
    testObserver.assertValueAt(2) {
      !it.isLoading
          && it.controllerItems == listOf(MainControllerItem.TutorialItem)
          && it.errorMessage == null
          && it.isHideKeyboard
    }
  }

  @Test
  fun initialIntent_getLastSearchedCity_success_getCityWeather_success() {
    `when`(cityDao.getLastSearchedCity())
      .thenReturn(Single.just(City(1819729, "Hong Kong", 1)))
    `when`(weatherService.getCityWeather(anyString(), anyString()))
      .thenReturn(Observable.just(getWeatherResponse))

    mainViewModel.processIntents(Observable.just(MainIntent.InitialIntent))

    assert(testObserver.values().size == 3)
    testObserver.assertValueAt(0, MainViewState.initial())
    testObserver.assertValueAt(1) {
      it.isLoading && it.controllerItems.isEmpty() && it.errorMessage == null && it.isHideKeyboard
    }
    testObserver.assertValueAt(2) {
      !it.isLoading
          && it.controllerItems == listOf(
        MainControllerItem.WeatherItem(
          id = 1819729,
          tempKelvin = 284.98,
          feelsLikeKelvin = 281.86,
          highTempKelvin = 285.37,
          lowTempKelvin = 284.26,
          cityName = "Hong Kong",
          weatherIconUrl = "https://openweathermap.org/img/wn/03d@2x.png",
          weatherDescription = "Scattered clouds",
          tempStringResource = R.string.temperature_degree_celsius,
          temp = 11,
          feelsLikeStringResource = R.string.feels_like_degree_celsius,
          feelsLike = 8,
          highTempStringResource = R.string.temperature_degree_celsius,
          highTemp = 12,
          lowTempStringResource = R.string.temperature_degree_celsius,
          lowTemp = 11,
          tempFormatStringResource = R.string.degree_celsius,
          isDegreeCelsius = true
        )
      )
          && it.errorMessage == null
          && it.isHideKeyboard
    }
  }

  @Test
  fun initialIntent_getLastSearchedCity_success_getCityWeather_failure() {
    `when`(cityDao.getLastSearchedCity())
      .thenReturn(Single.just(City(1819729, "Hong Kong", 1)))
    `when`(weatherService.getCityWeather(anyString(), anyString()))
      .thenReturn(Observable.error(Exception(errorMessage)))

    mainViewModel.processIntents(Observable.just(MainIntent.InitialIntent))

    assert(testObserver.values().size == 3)
    testObserver.assertValueAt(0, MainViewState.initial())
    testObserver.assertValueAt(1) {
      it.isLoading && it.controllerItems.isEmpty() && it.errorMessage == null && it.isHideKeyboard
    }
    testObserver.assertValueAt(2) {
      !it.isLoading
          && it.controllerItems.isEmpty()
          && it.errorMessage == errorMessage
          && it.isHideKeyboard
    }
  }

  @Test
  fun searchCityIntent_getCityWeather_success() {
    `when`(weatherService.getCityWeather(anyString(), anyString()))
      .thenReturn(Observable.just(getWeatherResponse))

    mainViewModel.processIntents(Observable.just(MainIntent.SearchCityIntent("Hong Kong")))

    assert(testObserver.values().size == 3)
    testObserver.assertValueAt(0, MainViewState.initial())
    testObserver.assertValueAt(1) {
      it.isLoading && it.controllerItems.isEmpty() && it.errorMessage == null && it.isHideKeyboard
    }
    testObserver.assertValueAt(2) {
      !it.isLoading
          && it.controllerItems == listOf(
        MainControllerItem.WeatherItem(
          id = 1819729,
          tempKelvin = 284.98,
          feelsLikeKelvin = 281.86,
          highTempKelvin = 285.37,
          lowTempKelvin = 284.26,
          cityName = "Hong Kong",
          weatherIconUrl = "https://openweathermap.org/img/wn/03d@2x.png",
          weatherDescription = "Scattered clouds",
          tempStringResource = R.string.temperature_degree_celsius,
          temp = 11,
          feelsLikeStringResource = R.string.feels_like_degree_celsius,
          feelsLike = 8,
          highTempStringResource = R.string.temperature_degree_celsius,
          highTemp = 12,
          lowTempStringResource = R.string.temperature_degree_celsius,
          lowTemp = 11,
          tempFormatStringResource = R.string.degree_celsius,
          isDegreeCelsius = true
        )
      )
          && it.errorMessage == null
          && it.isHideKeyboard
    }
  }

  @Test
  fun searchCityIntent_getCityWeather_failure() {
    `when`(weatherService.getCityWeather(anyString(), anyString()))
      .thenReturn(Observable.error(Exception(errorMessage)))

    mainViewModel.processIntents(Observable.just(MainIntent.SearchCityIntent("Hong Kong")))

    assert(testObserver.values().size == 3)
    testObserver.assertValueAt(0, MainViewState.initial())
    testObserver.assertValueAt(1) {
      it.isLoading && it.controllerItems.isEmpty() && it.errorMessage == null && it.isHideKeyboard
    }
    testObserver.assertValueAt(2) {
      !it.isLoading
          && it.controllerItems.isEmpty()
          && it.errorMessage == errorMessage
          && it.isHideKeyboard
    }
  }

  @Test
  fun refreshIntent_getCityWeather_success() {
    `when`(weatherService.getCityWeather(anyString(), anyString()))
      .thenReturn(Observable.just(getWeatherResponse))

    mainViewModel.processIntents(Observable.just(MainIntent.RefreshIntent("Hong Kong")))

    assert(testObserver.values().size == 3)
    testObserver.assertValueAt(0, MainViewState.initial())
    testObserver.assertValueAt(1) {
      it.isLoading && it.controllerItems.isEmpty() && it.errorMessage == null && it.isHideKeyboard
    }
    testObserver.assertValueAt(2) {
      !it.isLoading
          && it.controllerItems == listOf(
        MainControllerItem.WeatherItem(
          id = 1819729,
          tempKelvin = 284.98,
          feelsLikeKelvin = 281.86,
          highTempKelvin = 285.37,
          lowTempKelvin = 284.26,
          cityName = "Hong Kong",
          weatherIconUrl = "https://openweathermap.org/img/wn/03d@2x.png",
          weatherDescription = "Scattered clouds",
          tempStringResource = R.string.temperature_degree_celsius,
          temp = 11,
          feelsLikeStringResource = R.string.feels_like_degree_celsius,
          feelsLike = 8,
          highTempStringResource = R.string.temperature_degree_celsius,
          highTemp = 12,
          lowTempStringResource = R.string.temperature_degree_celsius,
          lowTemp = 11,
          tempFormatStringResource = R.string.degree_celsius,
          isDegreeCelsius = true
        )
      )
          && it.errorMessage == null
          && it.isHideKeyboard
    }
  }

  @Test
  fun refreshIntent_getCityWeather_failure() {
    `when`(weatherService.getCityWeather(anyString(), anyString()))
      .thenReturn(Observable.error(Exception(errorMessage)))

    mainViewModel.processIntents(Observable.just(MainIntent.RefreshIntent("Hong Kong")))

    assert(testObserver.values().size == 3)
    testObserver.assertValueAt(0, MainViewState.initial())
    testObserver.assertValueAt(1) {
      it.isLoading && it.controllerItems.isEmpty() && it.errorMessage == null && it.isHideKeyboard
    }
    testObserver.assertValueAt(2) {
      !it.isLoading
          && it.controllerItems.isEmpty()
          && it.errorMessage == errorMessage
          && it.isHideKeyboard
    }
  }

  @Test
  fun searchLocationIntent_requestPermission_rejected() {
    `when`(rxPermissions.request(any())).thenReturn(Observable.just(false))
    `when`(application.getString(R.string.no_permission)).thenReturn(errorMessage)

    mainViewModel.processIntents(Observable.just(MainIntent.SearchLocationIntent))

    assert(testObserver.values().size == 3)
    testObserver.assertValueAt(0, MainViewState.initial())
    testObserver.assertValueAt(1) {
      it.isLoading && it.controllerItems.isEmpty() && it.errorMessage == null && it.isHideKeyboard
    }
    testObserver.assertValueAt(2) {
      !it.isLoading
          && it.controllerItems.isEmpty()
          && it.errorMessage == errorMessage
          && it.isHideKeyboard
    }
  }

  @Test
  fun searchLocationIntent_requestPermission_granted_getCurrentLocation_serviceNotSupported() {
    `when`(rxPermissions.request(any())).thenReturn(Observable.just(true))
    `when`(locationRepository.getCurrentLocation())
      .thenReturn(Observable.error(Exception(errorMessage)))

    mainViewModel.processIntents(Observable.just(MainIntent.SearchLocationIntent))

    assert(testObserver.values().size == 3)
    testObserver.assertValueAt(0, MainViewState.initial())
    testObserver.assertValueAt(1) {
      it.isLoading && it.controllerItems.isEmpty() && it.errorMessage == null && it.isHideKeyboard
    }
    testObserver.assertValueAt(2) {
      !it.isLoading
          && it.controllerItems.isEmpty()
          && it.errorMessage == errorMessage
          && it.isHideKeyboard
    }
  }

  @Test
  fun searchLocationIntent_requestPermission_granted_getCurrentLocation_success_getLocationWeather_success() {
    val location = mock(Location::class.java)
    `when`(rxPermissions.request(any())).thenReturn(Observable.just(true))
    `when`(locationRepository.getCurrentLocation()).thenReturn(Observable.just(location))
    `when`(weatherService.getLocationWeather(anyDouble(), anyDouble(), anyString()))
      .thenReturn(Observable.just(getWeatherResponse))

    mainViewModel.processIntents(Observable.just(MainIntent.SearchLocationIntent))

    assert(testObserver.values().size == 3)
    testObserver.assertValueAt(0, MainViewState.initial())
    testObserver.assertValueAt(1) {
      it.isLoading && it.controllerItems.isEmpty() && it.errorMessage == null && it.isHideKeyboard
    }
    testObserver.assertValueAt(2) {
      !it.isLoading
          && it.controllerItems == listOf(
        MainControllerItem.WeatherItem(
          id = 1819729,
          tempKelvin = 284.98,
          feelsLikeKelvin = 281.86,
          highTempKelvin = 285.37,
          lowTempKelvin = 284.26,
          cityName = "Hong Kong",
          weatherIconUrl = "https://openweathermap.org/img/wn/03d@2x.png",
          weatherDescription = "Scattered clouds",
          tempStringResource = R.string.temperature_degree_celsius,
          temp = 11,
          feelsLikeStringResource = R.string.feels_like_degree_celsius,
          feelsLike = 8,
          highTempStringResource = R.string.temperature_degree_celsius,
          highTemp = 12,
          lowTempStringResource = R.string.temperature_degree_celsius,
          lowTemp = 11,
          tempFormatStringResource = R.string.degree_celsius,
          isDegreeCelsius = true
        )
      )
          && it.errorMessage == null
          && it.isHideKeyboard
    }
  }

  @Test
  fun searchLocationIntent_requestPermission_granted_getCurrentLocation_success_getLocationWeather_failure() {
    val location = mock(Location::class.java)
    `when`(rxPermissions.request(any())).thenReturn(Observable.just(true))
    `when`(locationRepository.getCurrentLocation()).thenReturn(Observable.just(location))
    `when`(weatherService.getLocationWeather(anyDouble(), anyDouble(), anyString()))
      .thenReturn(Observable.error(Exception(errorMessage)))

    mainViewModel.processIntents(Observable.just(MainIntent.SearchLocationIntent))

    assert(testObserver.values().size == 3)
    testObserver.assertValueAt(0, MainViewState.initial())
    testObserver.assertValueAt(1) {
      it.isLoading && it.controllerItems.isEmpty() && it.errorMessage == null && it.isHideKeyboard
    }
    testObserver.assertValueAt(2) {
      !it.isLoading
          && it.controllerItems.isEmpty()
          && it.errorMessage == errorMessage
          && it.isHideKeyboard
    }
  }

  @Test
  fun dismissErrorIntent() {
    `when`(weatherService.getCityWeather(anyString(), anyString()))
      .thenReturn(Observable.error(Exception(errorMessage)))

    mainViewModel.processIntents(
      Observable.mergeArray(
        Observable.just(MainIntent.RefreshIntent("Hong Kong")),
        Observable.just(MainIntent.DismissErrorIntent)
      )
    )

    assert(testObserver.values().size == 4)
    testObserver.assertValueAt(0, MainViewState.initial())
    testObserver.assertValueAt(1) {
      it.isLoading && it.controllerItems.isEmpty() && it.errorMessage == null && it.isHideKeyboard
    }
    testObserver.assertValueAt(2) {
      !it.isLoading
          && it.controllerItems.isEmpty()
          && it.errorMessage == errorMessage
          && it.isHideKeyboard
    }

    testObserver.assertValueAt(3) {
      !it.isLoading && it.controllerItems.isEmpty() && it.errorMessage == null && it.isHideKeyboard
    }
  }

  @Test
  fun changeTempFormatIntent() {
    `when`(cityDao.getLastSearchedCity())
      .thenReturn(Single.just(City(1819729, "Hong Kong", 1)))
    `when`(weatherService.getCityWeather(anyString(), anyString()))
      .thenReturn(Observable.just(getWeatherResponse))

    mainViewModel.processIntents(
      Observable.mergeArray(
        Observable.just(MainIntent.InitialIntent),
        Observable.just(MainIntent.ChangeTempFormatIntent(true)),
        Observable.just(MainIntent.ChangeTempFormatIntent(false))
      )
    )

    assert(testObserver.values().size == 5)
    testObserver.assertValueAt(0, MainViewState.initial())
    testObserver.assertValueAt(1) {
      it.isLoading && it.controllerItems.isEmpty() && it.errorMessage == null && it.isHideKeyboard
    }
    testObserver.assertValueAt(2) {
      !it.isLoading
          && it.controllerItems == listOf(
        MainControllerItem.WeatherItem(
          id = 1819729,
          tempKelvin = 284.98,
          feelsLikeKelvin = 281.86,
          highTempKelvin = 285.37,
          lowTempKelvin = 284.26,
          cityName = "Hong Kong",
          weatherIconUrl = "https://openweathermap.org/img/wn/03d@2x.png",
          weatherDescription = "Scattered clouds",
          tempStringResource = R.string.temperature_degree_celsius,
          temp = 11,
          feelsLikeStringResource = R.string.feels_like_degree_celsius,
          feelsLike = 8,
          highTempStringResource = R.string.temperature_degree_celsius,
          highTemp = 12,
          lowTempStringResource = R.string.temperature_degree_celsius,
          lowTemp = 11,
          tempFormatStringResource = R.string.degree_celsius,
          isDegreeCelsius = true
        )
      )
          && it.errorMessage == null
          && it.isHideKeyboard
    }
    testObserver.assertValueAt(3) {
      !it.isLoading
          && it.controllerItems == listOf(
        MainControllerItem.WeatherItem(
          id = 1819729,
          tempKelvin = 284.98,
          feelsLikeKelvin = 281.86,
          highTempKelvin = 285.37,
          lowTempKelvin = 284.26,
          cityName = "Hong Kong",
          weatherIconUrl = "https://openweathermap.org/img/wn/03d@2x.png",
          weatherDescription = "Scattered clouds",
          tempStringResource = R.string.temperature_degree_fahrenheit,
          temp = 53,
          feelsLikeStringResource = R.string.feels_like_degree_fahrenheit,
          feelsLike = 47,
          highTempStringResource = R.string.temperature_degree_fahrenheit,
          highTemp = 53,
          lowTempStringResource = R.string.temperature_degree_fahrenheit,
          lowTemp = 51,
          tempFormatStringResource = R.string.degree_fahrenheit,
          isDegreeCelsius = false
        )
      )
          && it.errorMessage == null
          && it.isHideKeyboard
    }
    testObserver.assertValueAt(4) {
      !it.isLoading
          && it.controllerItems == listOf(
        MainControllerItem.WeatherItem(
          id = 1819729,
          tempKelvin = 284.98,
          feelsLikeKelvin = 281.86,
          highTempKelvin = 285.37,
          lowTempKelvin = 284.26,
          cityName = "Hong Kong",
          weatherIconUrl = "https://openweathermap.org/img/wn/03d@2x.png",
          weatherDescription = "Scattered clouds",
          tempStringResource = R.string.temperature_degree_celsius,
          temp = 11,
          feelsLikeStringResource = R.string.feels_like_degree_celsius,
          feelsLike = 8,
          highTempStringResource = R.string.temperature_degree_celsius,
          highTemp = 12,
          lowTempStringResource = R.string.temperature_degree_celsius,
          lowTemp = 11,
          tempFormatStringResource = R.string.degree_celsius,
          isDegreeCelsius = true
        )
      )
          && it.errorMessage == null
          && it.isHideKeyboard
    }
  }

  private fun ClassLoader.getJsonFromResource(filePath: String): String {
    val uri = getResource(filePath)
    val file = File(uri.path)
    return String(file.readBytes())
  }

  companion object {
    const val errorMessage = "Some error"
  }
}