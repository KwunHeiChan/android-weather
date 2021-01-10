package com.example.androidweather.ui.main

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.ViewModelProvider
import com.example.androidweather.R
import com.example.androidweather.coordinator.HistoryFlowCoordinator
import com.example.androidweather.databinding.ActivityMainBinding
import com.example.androidweather.mvi.MviIntent
import com.example.androidweather.mvi.MviView
import com.example.androidweather.mvi.MviViewModel
import com.example.androidweather.mvi.MviViewState
import com.example.androidweather.util.hideSoftInput
import com.example.androidweather.viewmodel.MainViewModelFactory
import com.google.android.material.snackbar.Snackbar
import com.jakewharton.rxbinding4.appcompat.queryTextChangeEvents
import com.jakewharton.rxbinding4.material.dismisses
import com.jakewharton.rxbinding4.swiperefreshlayout.refreshes
import com.jakewharton.rxrelay3.PublishRelay
import com.tbruyelle.rxpermissions3.RxPermissions
import dagger.android.support.DaggerAppCompatActivity
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.plusAssign
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity(), MviView<MainIntent, MainViewState> {

  @Inject
  lateinit var controller: MainController

  @Inject
  lateinit var disposables: CompositeDisposable

  @Inject
  lateinit var historyFlowCoordinator: HistoryFlowCoordinator

  @Inject
  lateinit var rxPermissions: RxPermissions

  @Inject
  lateinit var viewModelFactory: MainViewModelFactory

  private lateinit var binding: ActivityMainBinding

  private lateinit var snackbar: Snackbar

  private lateinit var viewState: MainViewState

  private val hideKeyboardRelay: PublishRelay<MainIntent.HideKeyboardIntent> =
    PublishRelay.create<MainIntent.HideKeyboardIntent>()

  private val searchLocationRelay: PublishRelay<MainIntent.SearchLocationIntent> =
    PublishRelay.create<MainIntent.SearchLocationIntent>()

  private val viewModel: MainViewModel by lazy {
    ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)

    snackbar = Snackbar.make(binding.root, "", Snackbar.LENGTH_LONG)

    bind()
    initView()
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menuInflater.inflate(R.menu.menu_main, menu)
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      R.id.action_location -> {
        searchLocationRelay.accept(MainIntent.SearchLocationIntent)
      }
      R.id.action_history -> {
        historyFlowCoordinator.start()
      }
    }
    return true
  }

  override fun onDestroy() {
    super.onDestroy()
    disposables.dispose()
  }

  /**
   * Merge all [MviIntent]s to be processed by [MviViewModel]
   */
  override fun intents(): Observable<MainIntent> = Observable.mergeArray(
    changeTempFormatIntent(),
    dismissErrorIntent(),
    hideKeyboardIntent(),
    initialIntent(),
    refreshIntent(),
    searchCityIntent(),
    searchLocationIntent()
  )

  override fun render(state: MainViewState) {
    /** Cache view state */
    viewState = state

    binding.swipeRefreshLayout.isRefreshing = state.isLoading

    if (!state.errorMessage.isNullOrEmpty()) {
      snackbar.setText(state.errorMessage)
      snackbar.show()
    }

    controller.setData(state.controllerItems)

    if (state.isHideKeyboard) {
      binding.searchView.clearFocus()
      hideSoftInput()
      hideKeyboardRelay.accept(MainIntent.HideKeyboardIntent)
    }
  }

  /**
   * Connect the [MviView] with the [MviViewModel].
   * We subscribe to the [MviViewModel] before passing it the [MviView]'s [MviIntent]s.
   * If we were to pass [MviIntent]s to the [MviViewModel] before listening to it,
   * emitted [MviViewState]s could be lost.
   */
  private fun bind() {
    // Subscribe to the ViewModel and call render for every emitted state
    disposables += viewModel.states().subscribe(this::render)
    // Pass the UI's intents to the ViewModel
    viewModel.processIntents(intents())
  }

  private fun initView() {
    setSupportActionBar(binding.toolbar)
    supportActionBar?.apply {
      setTitle(R.string.app_name)
      setDisplayShowTitleEnabled(true)
    }

    binding.recyclerView.setController(controller)
  }

  private fun changeTempFormatIntent(): Observable<MainIntent.ChangeTempFormatIntent> =
    controller.tempFormatChangeRelay.map { MainIntent.ChangeTempFormatIntent(it) }

  private fun dismissErrorIntent(): Observable<MainIntent.DismissErrorIntent> =
    snackbar.dismisses().map { MainIntent.DismissErrorIntent }

  private fun hideKeyboardIntent(): Observable<MainIntent.HideKeyboardIntent> = hideKeyboardRelay

  /**
   * The initial [MviIntent] the [MviView] emit to be converted to [MviViewModel]
   * This initial Intent is also used to pass any parameters the [MviViewModel] might need
   * to render the initial [MviViewState]
   */
  private fun initialIntent(): Observable<MainIntent.InitialIntent> =
    Observable.just(MainIntent.InitialIntent)

  private fun refreshIntent(): Observable<MainIntent.RefreshIntent> =
    binding.swipeRefreshLayout
      .refreshes()
      .map { MainIntent.RefreshIntent(viewState.controllerItems.getCityName()) }

  private fun searchCityIntent(): Observable<MainIntent.SearchCityIntent> =
    binding.searchView
      .queryTextChangeEvents()
      .filter { it.isSubmitted }
      .map { MainIntent.SearchCityIntent(it.queryText.toString()) }

  private fun searchLocationIntent(): Observable<MainIntent.SearchLocationIntent> =
    searchLocationRelay
}