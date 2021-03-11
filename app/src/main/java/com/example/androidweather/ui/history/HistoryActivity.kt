package com.example.androidweather.ui.history

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.androidweather.R
import com.example.androidweather.databinding.ActivityHistoryBinding
import com.example.androidweather.mvvm.MvvmView
import com.example.androidweather.mvvm.MvvmViewModel
import com.example.androidweather.util.preventMultipleClicks
import com.google.android.material.snackbar.Snackbar
import com.jakewharton.rxbinding4.material.dismisses
import dagger.android.support.DaggerAppCompatActivity
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.plusAssign
import javax.inject.Inject

class HistoryActivity : DaggerAppCompatActivity(), MvvmView<HistoryViewModel, HistoryViewState> {

  @Inject
  lateinit var controller: HistoryController

  @Inject
  lateinit var disposables: CompositeDisposable

  @Inject
  lateinit var viewModelFactory: ViewModelProvider.Factory

  private lateinit var binding: ActivityHistoryBinding

  private lateinit var snackbar: Snackbar

  private val viewModel: HistoryViewModel by viewModels {
    viewModelFactory
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityHistoryBinding.inflate(layoutInflater)
    setContentView(binding.root)

    snackbar = Snackbar.make(binding.root, "", Snackbar.LENGTH_LONG)

    bind()
    initView()

    viewModel.getSearchHistory()
  }

  override fun onDestroy() {
    super.onDestroy()
    disposables.dispose()
  }

  override fun render(state: HistoryViewState) {
    binding.progressBar.visibility = if (state.isLoading) View.VISIBLE else View.INVISIBLE

    if (!state.errorMessage.isNullOrEmpty()) {
      snackbar.setText(state.errorMessage)
      snackbar.show()
    }

    controller.setData(state.controllerItems)
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
      setTitle(R.string.history)
      setDisplayShowTitleEnabled(true)
      setDisplayHomeAsUpEnabled(true)
    }

    binding.recyclerView.setController(controller)

    disposables += controller.btnRemoveClickRelay
      .preventMultipleClicks()
      .subscribe {
        viewModel.removeSearchHistory(it)
      }

    disposables += snackbar.dismisses().subscribe { viewModel.dismissError() }
  }

  companion object {
    fun newIntent(context: Context): Intent {
      return Intent(context, HistoryActivity::class.java)
    }
  }
}