package com.chat.myapplication.ui.auth

import androidx.lifecycle.viewModelScope
import com.chat.myapplication.base.BaseViewModel
import com.chat.myapplication.core.data.auth.model.SignInRequest
import com.chat.myapplication.core.data.auth.model.SignInResponse
import com.chat.myapplication.core.data.auth.usecase.SignInUseCase
import com.chat.myapplication.core.domain.State
import com.chat.myapplication.core.domain.onApiError
import com.chat.myapplication.core.domain.onApiSuccess
import com.chat.myapplication.utility.collectAsResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

@HiltViewModel
class LauncherViewModel @Inject constructor(
    private val signInUseCase: SignInUseCase
) : BaseViewModel() {

    private val _signInResponse: MutableSharedFlow<State<SignInResponse>> = MutableSharedFlow()
    val signInResponse: MutableSharedFlow<State<SignInResponse>> = _signInResponse

    fun signIn(signInRequest: SignInRequest) {

        signInUseCase(params = SignInUseCase.Params.create(signInRequest)).collectAsResult().flowOn(
            Dispatchers.IO
        )
            .onApiSuccess { response ->
                response.let {
                    if (it.status) {
                        _signInResponse.emit(State.success(it))
                    } else {
                        _signInResponse.emit(State.Error(message = it.message))
                    }
                }
            }.onApiError { error ->
                _signInResponse.emit(State.Error(error.errorMessage))
            }.onStart {
                _signInResponse.emit(State.loading())
            }.launchIn(viewModelScope)
    }

}