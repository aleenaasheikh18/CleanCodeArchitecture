package com.chat.myapplication.ui.wizard.changepassword

import androidx.lifecycle.viewModelScope
import com.chat.myapplication.base.BaseViewModel
import com.chat.myapplication.core.data.auth.usecase.ChangePasswordUseCase
import com.chat.myapplication.core.domain.State
import com.chat.myapplication.core.domain.onApiError
import com.chat.myapplication.core.domain.onApiSuccess
import com.chat.myapplication.core.exception.BaseResponse
import com.chat.myapplication.utility.collectAsResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val changePasswordUseCase: ChangePasswordUseCase
) : BaseViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _changePasswordResponse = MutableSharedFlow<State<BaseResponse>>()
    val changePasswordResponse: SharedFlow<State<BaseResponse>> = _changePasswordResponse.asSharedFlow()

    private val _resendSuccess = MutableSharedFlow<Boolean>()
    val resendSuccess: SharedFlow<Boolean> = _resendSuccess.asSharedFlow()

    fun changePasswordRequest() {
        changePasswordUseCase()
            .collectAsResult()
            .flowOn(Dispatchers.IO)
            .onApiSuccess { response ->
                if (response.status) {
                    _changePasswordResponse.emit(State.success(response))
                } else {
                    _changePasswordResponse.emit(State.Error(message = response.message))
                }
            }
            .onApiError { error ->
                _changePasswordResponse.emit(State.Error(error.errorMessage))
            }
            .onStart {
                _changePasswordResponse.emit(State.loading())
            }
            .launchIn(viewModelScope)
    }

    fun resendChangePasswordRequest() {
        changePasswordUseCase()
            .collectAsResult()
            .flowOn(Dispatchers.IO)
            .onApiSuccess { response ->
                if (response.status) {
                    _resendSuccess.emit(true)
                }
            }
            .onApiError { }
            .onStart {
                _isLoading.value = true
            }
            .launchIn(viewModelScope)
            .invokeOnCompletion {
                _isLoading.value = false
            }
    }
}
