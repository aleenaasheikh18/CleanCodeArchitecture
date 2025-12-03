package com.chat.myapplication.ui.wizard

import com.chat.myapplication.base.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class BaseWizardViewModel : BaseViewModel() {

    protected val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    protected val _emailError = MutableStateFlow<String?>(null)
    val emailError: StateFlow<String?> = _emailError.asStateFlow()

    protected val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun setEmail(email: String) {
        // Trim email to remove leading/trailing whitespace
        _email.value = email.trim()
        _emailError.value = null
    }

    fun clearEmail() {
        _email.value = ""
        _emailError.value = null
    }

    protected fun validateEmail(): Boolean {
        val emailValue = _email.value.trim()
        return when {
            emailValue.isEmpty() -> {
                _emailError.value = "Email is required"
                false
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(emailValue).matches() -> {
                _emailError.value = "Please enter a valid email address"
                false
            }
            else -> {
                _emailError.value = null
                true
            }
        }
    }
}
