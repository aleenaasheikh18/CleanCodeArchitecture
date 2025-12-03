package com.chat.myapplication.ui.auth

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.chat.myapplication.base.BaseViewModel
import com.chat.myapplication.core.data.auth.model.SignInRequest
import com.chat.myapplication.core.data.auth.model.SignInResponse
import com.chat.myapplication.core.data.auth.usecase.SignInUseCase
import com.chat.myapplication.core.data.auth.usecase.VerifyAccountTokenUseCase
import com.chat.myapplication.core.data.auth.usecase.VerifyLoginTokenUseCase
import com.chat.myapplication.core.domain.State
import com.chat.myapplication.core.domain.onApiFailure
import com.chat.myapplication.core.domain.onApiSuccess
import com.chat.myapplication.utility.PreferenceManager
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
    private val signInUseCase: SignInUseCase,
    private val verifyLoginTokenUseCase: VerifyLoginTokenUseCase,
    private val verifyAccountTokenUseCase: VerifyAccountTokenUseCase,
    private val preferenceManager: PreferenceManager
) : BaseViewModel() {

    private val _signInResponse: MutableSharedFlow<State<SignInResponse>> = MutableSharedFlow()
    val signInResponse: MutableSharedFlow<State<SignInResponse>> = _signInResponse

    private val _verifyTokenResponse: MutableSharedFlow<State<SignInResponse>> = MutableSharedFlow()
    val verifyTokenResponse: MutableSharedFlow<State<SignInResponse>> = _verifyTokenResponse

    private val _verifyAccountResponse: MutableSharedFlow<State<SignInResponse>> = MutableSharedFlow()
    val verifyAccountResponse: MutableSharedFlow<State<SignInResponse>> = _verifyAccountResponse

    fun signIn(signInRequest: SignInRequest) {

        signInUseCase(params = SignInUseCase.Params.create(signInRequest)).collectAsResult().flowOn(
            Dispatchers.IO
        )
            .onApiSuccess { response ->
                response.data?.let { signInData ->
                    preferenceManager.handleDataAfterLogin(signInData)
                }
                _signInResponse.emit(State.success(response))
            }
            .onApiFailure { errorMessage ->
                Log.d("LauncherViewModel", "signIn onApiFailure - errorMessage='$errorMessage'")
                _signInResponse.emit(State.Error(message = errorMessage))
            }
            .onStart {
                _signInResponse.emit(State.loading())
            }.launchIn(viewModelScope)
    }

    fun verifyLoginToken(token: String) {
        verifyLoginTokenUseCase(token)
            .collectAsResult()
            .flowOn(Dispatchers.IO)
            .onApiSuccess { response ->
                response.data?.let { signInData ->
                    preferenceManager.handleDataAfterLogin(signInData)
                }
                _verifyTokenResponse.emit(State.success(response))
            }
            .onApiFailure { errorMessage ->
                Log.d("LauncherViewModel", "verifyLoginToken onApiFailure - errorMessage='$errorMessage'")
                _verifyTokenResponse.emit(State.Error(errorMessage))
            }
            .onStart {
                _verifyTokenResponse.emit(State.loading())
            }.launchIn(viewModelScope)
    }

    fun verifyAccountToken(token: String) {
        verifyAccountTokenUseCase(token)
            .collectAsResult()
            .flowOn(Dispatchers.IO)
            .onApiSuccess { response ->
                response.data?.let { signInData ->
                    preferenceManager.handleDataAfterLogin(signInData)
                }
                _verifyAccountResponse.emit(State.success(response))
            }
            .onApiFailure { errorMessage ->
                Log.d("LauncherViewModel", "verifyAccountToken onApiFailure - errorMessage='$errorMessage'")
                _verifyAccountResponse.emit(State.Error(errorMessage))
            }
            .onStart {
                _verifyAccountResponse.emit(State.loading())
            }.launchIn(viewModelScope)
    }

}