package com.example.androidweather.ui.history

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.example.androidweather.R
import com.example.androidweather.databinding.ActivityHistoryBinding
import com.example.androidweather.mvi.MviIntent
import com.example.androidweather.mvi.MviView
import com.example.androidweather.mvi.MviViewModel
import com.example.androidweather.mvi.MviViewState
import com.example.androidweather.viewmodel.HistoryViewModelFactory
import com.google.android.material.snackbar.Snackbar
import com.jakewharton.rxbinding4.material.dismisses
import dagger.android.support.DaggerAppCompatActivity
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.plusAssign
import javax.inject.Inject

class HistoryActivity : DaggerAppCompatActivity(), MviView<HistoryIntent, HistoryViewState> {

  @Inject
  lateinit var controller: HistoryController

  @Inject
  lateinit var disposables: CompositeDisposable

  @Inject
  lateinit var viewModelFactory: HistoryViewModelFactory

  private lateinit var binding: ActivityHistoryBinding

  private lateinit var snackbar: Snackbar

  private val viewModel: HistoryViewModel by lazy {
    ViewModelProvider(this, viewModelFactory).get(HistoryViewModel::class.java)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityHistoryBinding.inflate(layoutInflater)
    setContentView(binding.root)

    snackbar = Snackbar.make(binding.root, "", Snackbar.LENGTH_LONG)

    bind()
    initView()
  }

  override fun onDestroy() {
    super.onDestroy()
    disposables.dispose()
  }

  /**
   * Merge all [MviIntent]s to be processed by [MviViewModel]
   */
  override fun intents(): Observable<HistoryIntent> = Observable.merge(
    dismissErrorIntent(),
    initialIntent(),
    removeSearchHistoryIntent()
  )

  override fun render(state: HistoryViewState) {
    binding.progressBar.visibility = if (state.isLoading) View.VISIBLE else View.INVISIBLE

    if (!state.errorMessage.isNullOrEmpty()) {
      snackbar.setText(state.errorMessage)
      snackbar.show()
    }

    controller.setData(state.controllerItems)
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
      setTitle(R.string.history)
      setDisplayShowTitleEnabled(true)
      setDisplayHomeAsUpEnabled(true)
    }

    binding.recyclerView.setController(controller)
  }

  private fun dismissErrorIntent(): Observable<HistoryIntent.DismissErrorIntent> =
    snackbar.dismisses().map { HistoryIntent.DismissErrorIntent }

  /**
   * The initial [MviIntent] the [MviView] emit to be converted to [MviViewModel]
   * This initial Intent is also used to pass any parameters the [MviViewModel] might need
   * to render the initial [MviViewState]
   */
  private fun initialIntent(): Observable<HistoryIntent.InitialIntent> =
    Observable.just(HistoryIntent.InitialIntent)

  private fun removeSearchHistoryIntent(): Observable<HistoryIntent.RemoveSearchHistoryIntent> =
    controller.btnRemoveClickRelay.map { HistoryIntent.RemoveSearchHistoryIntent(it) }

  companion object {
    fun newIntent(context: Context): Intent {
      return Intent(context, HistoryActivity::class.java)
    }
  }
}