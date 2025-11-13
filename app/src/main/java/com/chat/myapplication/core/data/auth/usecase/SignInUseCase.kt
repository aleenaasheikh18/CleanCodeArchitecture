package com.chat.myapplication.core.data.auth.usecase

import com.chat.myapplication.core.data.auth.model.SignInRequest
import com.chat.myapplication.core.data.auth.model.SignInResponse
import com.chat.myapplication.core.data.auth.remote.AuthRepository
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ViewModelScoped
class SignInUseCase @Inject constructor(val authRepository: AuthRepository) {

    operator fun invoke(params: Params): Flow<SignInResponse> {
        return authRepository.signIn(params.signInRequest)
    }

    data class Params(var signInRequest: SignInRequest) {
        companion object {
            fun create(signInRequest: SignInRequest) = Params(signInRequest)
        }
    }
}