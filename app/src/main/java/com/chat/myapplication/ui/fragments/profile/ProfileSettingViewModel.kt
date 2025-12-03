package com.chat.myapplication.ui.fragments.profile

import androidx.lifecycle.viewModelScope
import com.chat.myapplication.base.BaseViewModel
import com.chat.myapplication.core.data.auth.model.Allergy
import com.chat.myapplication.core.data.auth.model.Customer
import com.chat.myapplication.core.data.auth.usecase.GetAllergiesUseCase
import com.chat.myapplication.core.data.auth.usecase.GetCustomerProfileUseCase
import com.chat.myapplication.core.data.auth.usecase.LogoutUseCase
import com.chat.myapplication.core.domain.State
import com.chat.myapplication.core.domain.onApiFailure
import com.chat.myapplication.core.domain.onApiSuccess
import com.chat.myapplication.core.exception.BaseResponse
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
class ProfileSettingViewModel @Inject constructor(
    private val logoutUseCase: LogoutUseCase,
    private val getAllergiesUseCase: GetAllergiesUseCase,
    private val getCustomerProfileUseCase: GetCustomerProfileUseCase,
    @param:IODispatcher private val ioDispatcher: CoroutineDispatcher
) : BaseViewModel() {

    private val _logoutState = MutableStateFlow<State<BaseResponse>>(State.idle())
    val logoutState: StateFlow<State<BaseResponse>> = _logoutState

    private val _allergiesState = MutableStateFlow<State<List<Allergy>>>(State.idle())
    val allergiesState: StateFlow<State<List<Allergy>>> = _allergiesState

    private val _profileState = MutableStateFlow<State<Customer>>(State.idle())
    val profileState: StateFlow<State<Customer>> = _profileState

    fun logout() {
        logoutUseCase()
            .collectAsResult()
            .flowOn(ioDispatcher)
            .onApiSuccess { response ->
                _logoutState.value = State.Success(response)
            }
            .onApiFailure { errorMessage ->
                _logoutState.value = State.Error(errorMessage)
            }
            .onStart {
                _logoutState.value = State.Loading()
            }
            .launchIn(viewModelScope)
    }

    fun loadAllergies(userAllergies: List<String>) {
        getAllergiesUseCase()
            .collectAsResult()
            .flowOn(ioDispatcher)
            .onApiSuccess { response ->
                val allergies = response.data?.allergies.orEmpty().map { allergy ->
                    allergy.copy(isSelected = userAllergies.contains(allergy.name))
                }
                _allergiesState.value = State.Success(allergies)
            }
            .onApiFailure { errorMessage ->
                _allergiesState.value = State.Error(errorMessage)
            }
            .onStart {
                _allergiesState.value = State.Loading()
            }
            .launchIn(viewModelScope)
    }

    fun resetAllergiesState() {
        _allergiesState.value = State.idle()
    }

    fun getCustomerProfile() {
        getCustomerProfileUseCase()
            .collectAsResult()
            .flowOn(ioDispatcher)
            .onApiSuccess { response ->
                response.data?.let { customer ->
                    _profileState.value = State.Success(customer)
                } ?: run {
                    _profileState.value = State.Error("No profile data available")
                }
            }
            .onApiFailure { errorMessage ->
                _profileState.value = State.Error(errorMessage)
            }
            .onStart {
                _profileState.value = State.Loading()
            }
            .launchIn(viewModelScope)
    }
}
