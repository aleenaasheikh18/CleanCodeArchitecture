package com.chat.myapplication.ui.wizard.youGotMail

import androidx.lifecycle.viewModelScope
import com.chat.myapplication.base.BaseViewModel
import com.chat.myapplication.core.data.auth.usecase.SendLoginLinkUseCase
import com.chat.myapplication.core.domain.State
import com.chat.myapplication.core.domain.onApiError
import com.chat.myapplication.core.domain.onApiSuccess
import com.chat.myapplication.core.exception.BaseResponse
import com.chat.myapplication.utility.collectAsResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

@HiltViewModel
class YouGotMailViewModel @Inject constructor(
    private val sendLoginLinkUseCase: SendLoginLinkUseCase
) : BaseViewModel() {

    private val _resendState = MutableStateFlow<State<BaseResponse>>(State.idle())
    val resendState: StateFlow<State<BaseResponse>> = _resendState

    private var userEmail: String = ""

    fun setEmail(email: String) {
        userEmail = email
    }

    fun resendLoginLink() {
        sendLoginLinkUseCase(userEmail)
            .collectAsResult()
            .flowOn(Dispatchers.IO)
            .onApiSuccess { response ->
                if (response.status) {
                    _resendState.value = State.Success(response)
                } else {
                    _resendState.value = State.Error(response.message)
                }
            }
            .onApiError { error ->
                _resendState.value = State.Error(error.errorMessage)
            }
            .onStart {
                _resendState.value = State.Loading()
            }
            .launchIn(viewModelScope)
    }
}
