package com.chat.myapplication.core.data.auth.usecase

import com.chat.myapplication.core.data.auth.model.PasskeyRegisterRequest
import com.chat.myapplication.core.data.auth.model.PasskeyRegisterResponse
import com.chat.myapplication.core.data.auth.remote.AuthRepository
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ViewModelScoped
class RegisterPasskeyUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(request: PasskeyRegisterRequest): Flow<PasskeyRegisterResponse> {
        return authRepository.registerPasskey(request)
    }
}
