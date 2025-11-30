package com.chat.myapplication.ui.wizard.resetpassword

import androidx.lifecycle.viewModelScope
import com.chat.myapplication.base.BaseViewModel
import com.chat.myapplication.core.data.auth.model.ResetPasswordRequest
import com.chat.myapplication.core.data.auth.usecase.ResetPasswordUseCase
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
class ResetPasswordViewModel @Inject constructor(
    private val resetPasswordUseCase: ResetPasswordUseCase
) : BaseViewModel() {

    private val _newPassword = MutableStateFlow("")
    val newPassword: StateFlow<String> = _newPassword.asStateFlow()

    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword: StateFlow<String> = _confirmPassword.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Password validation states
    private val _hasMinLength = MutableStateFlow(false)
    val hasMinLength: StateFlow<Boolean> = _hasMinLength.asStateFlow()

    private val _hasNumber = MutableStateFlow(false)
    val hasNumber: StateFlow<Boolean> = _hasNumber.asStateFlow()

    private val _hasCapital = MutableStateFlow(false)
    val hasCapital: StateFlow<Boolean> = _hasCapital.asStateFlow()

    private val _hasSymbol = MutableStateFlow(false)
    val hasSymbol: StateFlow<Boolean> = _hasSymbol.asStateFlow()

    private val _passwordsMatch = MutableStateFlow(false)
    val passwordsMatch: StateFlow<Boolean> = _passwordsMatch.asStateFlow()

    private val _isFormValid = MutableStateFlow(false)
    val isFormValid: StateFlow<Boolean> = _isFormValid.asStateFlow()

    private val _resetPasswordResponse = MutableSharedFlow<State<BaseResponse>>()
    val resetPasswordResponse: SharedFlow<State<BaseResponse>> = _resetPasswordResponse.asSharedFlow()

    fun setNewPassword(password: String) {
        _newPassword.value = password
        validateNewPassword(password)
        validatePasswordsMatch()
        validateForm()
    }

    fun setConfirmPassword(password: String) {
        _confirmPassword.value = password
        validatePasswordsMatch()
        validateForm()
    }

    private fun validateNewPassword(password: String) {
        _hasMinLength.value = password.length >= 12
        _hasNumber.value = password.any { it.isDigit() }
        _hasCapital.value = password.any { it.isUpperCase() }
        _hasSymbol.value = password.any { !it.isLetterOrDigit() }
    }

    private fun validatePasswordsMatch() {
        _passwordsMatch.value = _newPassword.value.isNotEmpty() &&
                _newPassword.value == _confirmPassword.value
    }

    private fun validateForm() {
        _isFormValid.value = _hasMinLength.value &&
                _hasNumber.value &&
                _hasCapital.value &&
                _hasSymbol.value &&
                _passwordsMatch.value
    }

    fun resetPassword() {
        if (!_isFormValid.value) return

        val request = ResetPasswordRequest(
            newPassword = _newPassword.value,
            confirmPassword = _confirmPassword.value
        )
        resetPasswordUseCase(ResetPasswordUseCase.Params(request))
            .collectAsResult()
            .flowOn(Dispatchers.IO)
            .onApiSuccess { response ->
                if (response.status) {
                    _resetPasswordResponse.emit(State.success(response))
                } else {
                    _resetPasswordResponse.emit(State.Error(message = response.message))
                }
            }
            .onApiError { error ->
                _resetPasswordResponse.emit(State.Error(error.errorMessage))
            }
            .onStart {
                _resetPasswordResponse.emit(State.loading())
            }
            .launchIn(viewModelScope)
    }
}
