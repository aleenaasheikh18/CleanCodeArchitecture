package com.chat.myapplication.ui.wizard.editemail

import androidx.lifecycle.viewModelScope
import com.chat.myapplication.core.data.auth.model.ChangeEmailRequest
import com.chat.myapplication.core.data.auth.model.ChangeEmailResponse
import com.chat.myapplication.core.data.auth.usecase.ChangeEmailUseCase
import com.chat.myapplication.core.domain.State
import com.chat.myapplication.core.domain.onApiError
import com.chat.myapplication.core.domain.onApiSuccess
import com.chat.myapplication.ui.wizard.BaseWizardViewModel
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
class EditEmailViewModel @Inject constructor(
    private val changeEmailUseCase: ChangeEmailUseCase
) : BaseWizardViewModel() {

    private val _currentStep = MutableStateFlow<EditEmailWizardStep>(EditEmailWizardStep.Email)
    val currentStep: StateFlow<EditEmailWizardStep> = _currentStep.asStateFlow()

    private val _changeEmailResponse = MutableSharedFlow<State<ChangeEmailResponse>>()
    val changeEmailResponse: SharedFlow<State<ChangeEmailResponse>> = _changeEmailResponse.asSharedFlow()

    private val _resendSuccess = MutableSharedFlow<Boolean>()
    val resendSuccess: SharedFlow<Boolean> = _resendSuccess.asSharedFlow()

    fun goToYouGotMailStep() {
        if (validateEmail()) {
            _currentStep.value = EditEmailWizardStep.YouGotMail
        }
    }

    fun goBack() {
        _currentStep.value = EditEmailWizardStep.Email
    }

    fun changeEmail() {
        if (!validateEmail()) return

        changeEmailUseCase(ChangeEmailUseCase.Params(ChangeEmailRequest(_email.value.trim())))
            .collectAsResult()
            .flowOn(Dispatchers.IO)
            .onApiSuccess { response ->
                if (response.status) {
                    if (response.canChangeEmail) {
                        _currentStep.value = EditEmailWizardStep.YouGotMail
                    }
                    _changeEmailResponse.emit(State.success(response))
                } else {
                    _changeEmailResponse.emit(State.Error(message = response.message))
                }
            }
            .onApiError { error ->
                _changeEmailResponse.emit(State.Error(error.errorMessage))
            }
            .onStart {
                _changeEmailResponse.emit(State.loading())
            }
            .launchIn(viewModelScope)
    }

    fun resendEmail() {
        if (_email.value.isEmpty()) return

        changeEmailUseCase(ChangeEmailUseCase.Params(ChangeEmailRequest(_email.value.trim())))
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
