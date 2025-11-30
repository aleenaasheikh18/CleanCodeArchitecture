package com.chat.myapplication.ui.wizard.editphone

import com.chat.myapplication.core.data.auth.model.CountryArea

data class CountrySelectionUiState(
    val isLoading: Boolean = false,
    val countries: List<CountryArea> = emptyList(),
    val filteredCountries: List<CountryArea> = emptyList(),
    val searchQuery: String = "",
    val error: String? = null
) {
    val hasError: Boolean get() = error != null
    val isEmpty: Boolean get() = filteredCountries.isEmpty() && !isLoading && !hasError
}
