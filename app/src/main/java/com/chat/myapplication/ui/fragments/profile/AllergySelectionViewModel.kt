package com.chat.myapplication.ui.fragments.profile

import androidx.lifecycle.viewModelScope
import com.chat.myapplication.base.BaseViewModel
import com.chat.myapplication.core.data.auth.model.Allergy
import com.chat.myapplication.core.data.auth.usecase.GetAllergiesUseCase
import com.chat.myapplication.core.data.auth.usecase.UpdateAllergiesUseCase
import com.chat.myapplication.core.domain.State
import com.chat.myapplication.core.domain.onApiError
import com.chat.myapplication.core.domain.onApiSuccess
import com.chat.myapplication.di.IODispatcher
import com.chat.myapplication.utility.collectAsResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

@HiltViewModel
class AllergySelectionViewModel @Inject constructor(
    private val getAllergiesUseCase: GetAllergiesUseCase,
    private val updateAllergiesUseCase: UpdateAllergiesUseCase,
    @param:IODispatcher private val ioDispatcher: CoroutineDispatcher
) : BaseViewModel() {

    private val _allergiesState = MutableStateFlow<State<List<Allergy>>>(State.idle())
    val allergiesState: StateFlow<State<List<Allergy>>> = _allergiesState

    private val _updateState = MutableStateFlow<State<Unit>>(State.idle())
    val updateState: StateFlow<State<Unit>> = _updateState

    private var allAllergies: MutableList<Allergy> = mutableListOf()

    fun setPreloadedAllergies(allergies: List<Allergy>) {
        allAllergies = allergies.toMutableList()
        _allergiesState.value = State.Success(allergies)
    }

    fun toggleAllergy(allergyName: String) {
        val index = allAllergies.indexOfFirst { it.name == allergyName }
        if (index != -1) {
            allAllergies[index] = allAllergies[index].copy(isSelected = !allAllergies[index].isSelected)
            _allergiesState.value = State.Success(allAllergies.toList())
        }
    }

    fun getSelectedAllergies(): List<String> {
        return allAllergies.filter { it.isSelected }.mapNotNull { it.name }
    }

    fun saveAllergies() {
        val selectedAllergies = getSelectedAllergies()
        updateAllergiesUseCase(selectedAllergies)
            .collectAsResult()
            .flowOn(ioDispatcher)
            .onApiSuccess { response ->
                if (response.status) {
                    _updateState.value = State.Success(Unit)
                } else {
                    _updateState.value = State.Error(response.message)
                }
            }
            .onApiError { error ->
                _updateState.value = State.Error(error.errorMessage)
            }
            .onStart {
                _updateState.value = State.Loading()
            }
            .launchIn(viewModelScope)
    }
}
