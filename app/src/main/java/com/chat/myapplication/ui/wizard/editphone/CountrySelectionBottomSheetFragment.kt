package com.chat.myapplication.ui.wizard.editphone

import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.chat.myapplication.base.BaseBottomSheetDialogFragment
import com.chat.myapplication.core.data.auth.model.CountryArea
import com.chat.myapplication.databinding.BottomSheetCountrySelectionBinding
import com.chat.myapplication.utility.SimpleTextWatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CountrySelectionBottomSheetFragment : BaseBottomSheetDialogFragment<BottomSheetCountrySelectionBinding>(
    BottomSheetCountrySelectionBinding::inflate
) {

    private var countries: List<CountryArea> = emptyList()
    private val _uiState = MutableStateFlow(CountrySelectionUiState())

    private val countryAdapter by lazy {
        CountryAdapter { country ->
            onCountrySelected?.invoke(country)
            dismiss()
        }
    }

    var onCountrySelected: ((CountryArea) -> Unit)? = null

    companion object {
        const val TAG = "CountrySelectionBottomSheetFragment"

        fun newInstance(countries: List<CountryArea>): CountrySelectionBottomSheetFragment {
            return CountrySelectionBottomSheetFragment().apply {
                this.countries = countries
            }
        }
    }

    override fun initUserInterface() {
        setupRecyclerView()
        setupSearch()
        setupCloseButton()
        initObservers()
        initializeCountries()
    }

    private fun initializeCountries() {
        _uiState.update {
            it.copy(
                countries = countries,
                filteredCountries = countries
            )
        }
    }

    private fun setupRecyclerView() {
        bi.rvCountries.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = countryAdapter
        }
    }

    private fun setupSearch() {
        bi.etSearch.addTextChangedListener(SimpleTextWatcher { text ->
            filterCountries(text)
            bi.ivClearSearch.isVisible = text.isNotEmpty()
        })

        bi.ivClearSearch.setOnClickListener {
            bi.etSearch.text?.clear()
            filterCountries("")
        }
    }

    private fun filterCountries(query: String) {
        _uiState.update { state ->
            state.copy(
                searchQuery = query,
                filteredCountries = if (query.isBlank()) {
                    state.countries
                } else {
                    state.countries.filter { country ->
                        country.name?.contains(query, ignoreCase = true) == true ||
                                country.countryCode?.contains(query, ignoreCase = true) == true
                    }
                }
            )
        }
    }

    private fun setupCloseButton() {
        bi.ivClose.setOnClickListener { dismiss() }
    }

    private fun initObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                _uiState.collectLatest { state ->
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
