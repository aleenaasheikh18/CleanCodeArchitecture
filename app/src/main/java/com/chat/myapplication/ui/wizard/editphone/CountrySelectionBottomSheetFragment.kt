package com.chat.myapplication.ui.wizard.editphone

import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.chat.myapplication.base.BaseBottomSheetDialogFragment
import com.chat.myapplication.core.data.auth.model.CountryArea
import com.chat.myapplication.databinding.BottomSheetCountrySelectionBinding
import com.chat.myapplication.utility.SimpleTextWatcher
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CountrySelectionBottomSheetFragment : BaseBottomSheetDialogFragment<BottomSheetCountrySelectionBinding>(
    BottomSheetCountrySelectionBinding::inflate
) {

    private val viewModel: CountrySelectionViewModel by viewModels()

    private val countryAdapter by lazy {
        CountryAdapter { country ->
            onCountrySelected?.invoke(country)
            dismiss()
        }
    }

    var onCountrySelected: ((CountryArea) -> Unit)? = null

    companion object {
        const val TAG = "CountrySelectionBottomSheetFragment"

        fun newInstance(): CountrySelectionBottomSheetFragment {
            return CountrySelectionBottomSheetFragment()
        }
    }

    override fun initUserInterface() {
        setupRecyclerView()
        setupSearch()
        setupCloseButton()
        setupRetryButton()
        initObservers()
    }

    private fun setupRecyclerView() {
        bi.rvCountries.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = countryAdapter
        }
    }

    private fun setupSearch() {
        bi.etSearch.addTextChangedListener(SimpleTextWatcher { text ->
            viewModel.filterCountries(text)
            bi.ivClearSearch.isVisible = text.isNotEmpty()
        })

        bi.ivClearSearch.setOnClickListener {
            bi.etSearch.text?.clear()
            viewModel.filterCountries("")
        }
    }

    private fun setupCloseButton() {
        bi.ivClose.setOnClickListener { dismiss() }
    }

    private fun setupRetryButton() {
        bi.btnRetry.setOnClickListener { viewModel.retry() }
    }

    private fun initObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest { state ->
                    updateUi(state)
                }
            }
        }
    }

    private fun updateUi(state: CountrySelectionUiState) {
        with(bi) {
            // Loading state
            progressBar.isVisible = state.isLoading

            // Error state
            layoutError.isVisible = state.hasError && !state.isLoading
            state.error?.let { tvError.text = it }

            // Empty state
            tvEmpty.isVisible = state.isEmpty

            // List visibility
            rvCountries.isVisible = !state.isLoading && !state.hasError && !state.isEmpty

            // Update list
            countryAdapter.submitList(state.filteredCountries)
        }
    }
}
