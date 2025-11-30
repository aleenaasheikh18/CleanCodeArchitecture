package com.chat.myapplication.ui.dashboard

import androidx.lifecycle.viewModelScope
import com.chat.myapplication.base.BaseViewModel
import com.chat.myapplication.core.data.auth.model.SignInResponse
import com.chat.myapplication.core.data.auth.model.VerifyEmailChangeRequest
import com.chat.myapplication.core.data.auth.usecase.VerifyEmailChangeUseCase
import com.chat.myapplication.core.domain.State
import com.chat.myapplication.core.domain.onApiError
import com.chat.myapplication.core.domain.onApiSuccess
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
    private val verifyEmailChangeUseCase: VerifyEmailChangeUseCase
) : BaseViewModel() {

    private val _verifyEmailResponse = MutableSharedFlow<State<SignInResponse>>()
    val verifyEmailResponse: SharedFlow<State<SignInResponse>> = _verifyEmailResponse.asSharedFlow()

    fun verifyEmailChange(token: String) {
        verifyEmailChangeUseCase(VerifyEmailChangeUseCase.Params(VerifyEmailChangeRequest(token)))
            .collectAsResult()
            .flowOn(Dispatchers.IO)
            .onApiSuccess { response ->
                if (response.status) {
                    _verifyEmailResponse.emit(State.success(response))
                } else {
                    _verifyEmailResponse.emit(State.Error(message = response.message))
                }
            }
            .onApiError { error ->
                _verifyEmailResponse.emit(State.Error(error.errorMessage))
            }
            .onStart {
                _verifyEmailResponse.emit(State.loading())
            }
            .launchIn(viewModelScope)
    }
}
