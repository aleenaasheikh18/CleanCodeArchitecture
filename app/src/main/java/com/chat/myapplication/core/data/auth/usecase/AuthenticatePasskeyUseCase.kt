package com.chat.myapplication.core.data.auth.usecase

import com.chat.myapplication.core.data.auth.model.PasskeyLoginRequest
import com.chat.myapplication.core.data.auth.model.SignInResponse
import com.chat.myapplication.core.data.auth.remote.AuthRepository
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ViewModelScoped
class AuthenticatePasskeyUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(request: PasskeyLoginRequest): Flow<SignInResponse> {
        return authRepository.loginWithPasskey(request)
    }
}
