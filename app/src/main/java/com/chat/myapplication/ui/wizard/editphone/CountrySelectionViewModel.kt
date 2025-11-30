package com.chat.myapplication.ui.wizard.editphone

import androidx.lifecycle.viewModelScope
import com.chat.myapplication.base.BaseViewModel
import com.chat.myapplication.core.data.auth.model.CountryArea
import com.chat.myapplication.core.data.auth.usecase.GetCountriesAreasUseCase
import com.chat.myapplication.core.domain.onApiError
import com.chat.myapplication.core.domain.onApiSuccess
import com.chat.myapplication.utility.collectAsResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class CountrySelectionViewModel @Inject constructor(
    private val getCountriesAreasUseCase: GetCountriesAreasUseCase
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(CountrySelectionUiState())
    val uiState: StateFlow<CountrySelectionUiState> = _uiState.asStateFlow()

    init {
        loadCountries()
    }

    fun loadCountries() {
        getCountriesAreasUseCase()
            .collectAsResult()
            .flowOn(Dispatchers.IO)
            .onApiSuccess { response ->
                if (response.status) {
                    val countries = response.data.orEmpty()
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            countries = countries,
                            filteredCountries = filterList(countries, it.searchQuery),
                            error = null
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = response.message
                        )
                    }
                }
            }
            .onApiError { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = error.errorMessage
                    )
                }
            }
            .onStart {
                _uiState.update { it.copy(isLoading = true, error = null) }
            }
            .launchIn(viewModelScope)
    }

    fun filterCountries(query: String) {
        _uiState.update { state ->
            state.copy(
                searchQuery = query,
                filteredCountries = filterList(state.countries, query)
            )
        }
    }

    fun retry() {
        loadCountries()
    }

    private fun filterList(countries: List<CountryArea>, query: String): List<CountryArea> {
        if (query.isBlank()) return countries

        return countries.filter { country ->
            country.name?.contains(query, ignoreCase = true) == true ||
                    country.countryCode?.contains(query, ignoreCase = true) == true
        }
    }
}
