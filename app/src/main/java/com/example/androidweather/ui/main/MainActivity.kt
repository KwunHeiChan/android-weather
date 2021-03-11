package com.example.androidweather.ui.main

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.androidweather.R
import com.example.androidweather.coordinator.HistoryFlowCoordinator
import com.example.androidweather.databinding.ActivityMainBinding
import com.example.androidweather.mvvm.MvvmView
import com.example.androidweather.mvvm.MvvmViewModel
import com.example.androidweather.util.hideSoftInput
import com.example.androidweather.util.preventMultipleClicks
import com.google.android.material.snackbar.Snackbar
import com.jakewharton.rxbinding4.appcompat.queryTextChangeEvents
import com.jakewharton.rxbinding4.material.dismisses
import com.jakewharton.rxbinding4.swiperefreshlayout.refreshes
import com.tbruyelle.rxpermissions3.RxPermissions
import dagger.android.support.DaggerAppCompatActivity
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.plusAssign
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity(), MvvmView<MainViewModel, MainViewState> {

  @Inject
  lateinit var controller: MainController

  @Inject
  lateinit var disposables: CompositeDisposable

  @Inject
  lateinit var historyFlowCoordinator: HistoryFlowCoordinator

  @Inject
  lateinit var rxPermissions: RxPermissions

  @Inject
  lateinit var viewModelFactory: ViewModelProvider.Factory

  private lateinit var binding: ActivityMainBinding

  private lateinit var snackbar: Snackbar

  private lateinit var viewState: MainViewState

  private val viewModel: MainViewModel by viewModels {
    viewModelFactory
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)

    snackbar = Snackbar.make(binding.root, "", Snackbar.LENGTH_LONG)

    bind()
    initView()

    viewModel.getHistory()
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menuInflater.inflate(R.menu.menu_main, menu)
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      R.id.action_location -> {
        viewModel.searchLocation(rxPermissions)
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
      viewModel.hideKeyboard()
    }
  }

  /** Connect the [MvvmView] with the [MvvmViewModel]. */
  private fun bind() {
    // Subscribe to the ViewModel and call render for every emitted state
    viewModel.viewStateLiveData().observe(this, Observer {
      render(it)
    })
  }

  private fun initView() {
    setSupportActionBar(binding.toolbar)
    supportActionBar?.apply {
      setTitle(R.string.app_name)
      setDisplayShowTitleEnabled(true)
    }

    binding.recyclerView.setController(controller)

    disposables += controller.tempFormatChangeRelay
      .preventMultipleClicks()
      .subscribe {
        viewModel.changeTempFormat(it)
      }

    disposables += snackbar.dismisses().subscribe { viewModel.dismissError() }

    disposables += binding.swipeRefreshLayout
      .refreshes()
      .subscribe {
        viewModel.searchCity(viewState.controllerItems.getCityName())
      }

    disposables += binding.searchView
      .queryTextChangeEvents()
      .filter { it.isSubmitted }
      .subscribe {
        viewModel.searchCity(it.queryText.toString())
      }
  }
}