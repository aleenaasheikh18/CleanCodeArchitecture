package com.chat.myapplication.ui.wizard.onboarding

import android.app.Activity
import androidx.lifecycle.viewModelScope
import com.chat.myapplication.core.data.auth.model.SignInData
import com.chat.myapplication.core.data.auth.usecase.AuthenticatePasskeyUseCase
import com.chat.myapplication.core.domain.passkey.PasskeyException
import com.chat.myapplication.core.domain.passkey.PasskeyManager
import com.chat.myapplication.ui.wizard.BaseWizardViewModel
import com.chat.myapplication.utility.PreferenceManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WizardViewModel @Inject constructor(
    private val passkeyManager: PasskeyManager,
    private val authenticatePasskeyUseCase: AuthenticatePasskeyUseCase,
    private val preferenceManager: PreferenceManager
) : BaseWizardViewModel() {

    private val _currentStep = MutableStateFlow<WizardStep>(WizardStep.Selection)
    val currentStep: StateFlow<WizardStep> = _currentStep.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _passwordError = MutableStateFlow<String?>(null)
    val passwordError: StateFlow<String?> = _passwordError.asStateFlow()

    private val _selectedOption = MutableStateFlow<Int?>(null)

    private val _passkeyLoginState = MutableSharedFlow<PasskeyLoginState>()
    val passkeyLoginState: SharedFlow<PasskeyLoginState> = _passkeyLoginState.asSharedFlow()

    fun setSelectedOption(option: Int) {
        _selectedOption.value = option
    }

    fun goToEmailStep() {
        _currentStep.value = WizardStep.Email
    }

    fun goToPasswordStep() {
        if (validateEmail()) {
            _currentStep.value = WizardStep.Password
        }
    }

    fun goBack() {
        _currentStep.value = when (_currentStep.value) {
            is WizardStep.Password -> WizardStep.Email
            is WizardStep.Email -> WizardStep.Selection
            else -> WizardStep.Selection
        }
    }

    fun setPassword(password: String) {
        _password.value = password
        _passwordError.value = null
    }

    private fun validatePassword(): Boolean {
        val passwordValue = _password.value
        return when {
            passwordValue.isEmpty() -> {
                _passwordError.value = "Password is required"
                false
            }
            passwordValue.length < 6 -> {
                _passwordError.value = "Password must be at least 6 characters"
                false
            }
            else -> {
                _passwordError.value = null
                true
            }
        }
    }

    fun submit(): Boolean {
        return validatePassword()
    }

    fun authenticateWithPasskey(activity: Activity) {
        viewModelScope.launch {
            _passkeyLoginState.emit(PasskeyLoginState.Loading)

            passkeyManager.authenticatePasskey(activity)
                .onSuccess { loginRequest ->
                    authenticatePasskeyUseCase(loginRequest)
                        .catch { e ->
                            _passkeyLoginState.emit(
                                PasskeyLoginState.Error(e.message ?: "Login failed")
                            )
                        }
                        .collect { response ->
                            if (response.status && response.data != null) {
                                preferenceManager.handleDataAfterLogin(response.data)
                                _passkeyLoginState.emit(PasskeyLoginState.Success(response.data))
                            } else {
                                _passkeyLoginState.emit(
                                    PasskeyLoginState.Error(response.message.ifEmpty { "Login failed" })
                                )
                            }
                        }
                }
                .onFailure { exception ->
                    val errorMessage = when (exception) {
                        is PasskeyException.UserCancelled -> null // Don't show error for user cancellation
                        is PasskeyException -> exception.message
                        else -> exception.message ?: "Authentication failed"
                    }
                    if (errorMessage != null) {
                        _passkeyLoginState.emit(PasskeyLoginState.Error(errorMessage))
                    } else {
                        _passkeyLoginState.emit(PasskeyLoginState.Idle)
                    }
                }
        }
    }
}

sealed class PasskeyLoginState {
    data object Idle : PasskeyLoginState()
    data object Loading : PasskeyLoginState()
    data class Success(val data: SignInData) : PasskeyLoginState()
    data class Error(val message: String) : PasskeyLoginState()
}
