package com.chat.myapplication.ui.dashboard

import androidx.lifecycle.viewModelScope
import com.chat.myapplication.base.BaseViewModel
import com.chat.myapplication.core.data.auth.model.SignInResponse
import com.chat.myapplication.core.data.auth.usecase.VerifyChangePasswordTokenUseCase
import com.chat.myapplication.core.data.auth.usecase.VerifyEmailChangeUseCase
import com.chat.myapplication.core.domain.State
import com.chat.myapplication.core.domain.onApiFailure
import com.chat.myapplication.core.domain.onApiSuccess
import com.chat.myapplication.utility.PreferenceManager
import com.chat.myapplication.utility.collectAsResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val verifyEmailChangeUseCase: VerifyEmailChangeUseCase,
    private val verifyChangePasswordTokenUseCase: VerifyChangePasswordTokenUseCase,
    private val preferenceManager: PreferenceManager
) : BaseViewModel() {

    private val _verifyEmailResponse = MutableSharedFlow<State<SignInResponse>>()
    val verifyEmailResponse: SharedFlow<State<SignInResponse>> = _verifyEmailResponse.asSharedFlow()

    private val _verifyChangePasswordTokenResponse = MutableSharedFlow<State<SignInResponse>>()
    val verifyChangePasswordTokenResponse: SharedFlow<State<SignInResponse>> = _verifyChangePasswordTokenResponse.asSharedFlow()

    fun verifyEmailChange(token: String) {
        verifyEmailChangeUseCase(token)
            .collectAsResult()
            .flowOn(Dispatchers.IO)
            .onApiSuccess { response ->
                _verifyEmailResponse.emit(State.success(response))
            }
            .onApiFailure { errorMessage ->
                _verifyEmailResponse.emit(State.Error(message = errorMessage))
            }
            .onStart {
                _verifyEmailResponse.emit(State.loading())
            }
            .launchIn(viewModelScope)
    }

    fun verifyChangePasswordToken(token: String) {
        verifyChangePasswordTokenUseCase(VerifyChangePasswordTokenUseCase.Params(token))
            .collectAsResult()
            .flowOn(Dispatchers.IO)
            .onApiSuccess { response ->
                response.data?.let { signInData ->
                    preferenceManager.handleDataAfterLogin(signInData)
                }
                _verifyChangePasswordTokenResponse.emit(State.success(response))
            }
            .onApiFailure { errorMessage ->
                _verifyChangePasswordTokenResponse.emit(State.Error(message = errorMessage))
            }
            .onStart {
                _verifyChangePasswordTokenResponse.emit(State.loading())
            }
            .launchIn(viewModelScope)
    }
}
