package com.chat.myapplication.ui.wizard.onboarding

import com.chat.myapplication.ui.wizard.BaseWizardViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class WizardViewModel @Inject constructor() : BaseWizardViewModel() {

    private val _currentStep = MutableStateFlow<WizardStep>(WizardStep.Selection)
    val currentStep: StateFlow<WizardStep> = _currentStep.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _passwordError = MutableStateFlow<String?>(null)
    val passwordError: StateFlow<String?> = _passwordError.asStateFlow()

    private val _selectedOption = MutableStateFlow<Int?>(null)

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
}
