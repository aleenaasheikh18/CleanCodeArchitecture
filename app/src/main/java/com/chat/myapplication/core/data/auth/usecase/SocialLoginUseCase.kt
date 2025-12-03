package com.chat.myapplication.core.data.auth.usecase

import com.chat.myapplication.core.data.auth.model.SignInResponse
import com.chat.myapplication.core.data.auth.model.SocialLoginRequest
import com.chat.myapplication.core.data.auth.remote.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SocialLoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(request: SocialLoginRequest): Flow<SignInResponse> {
        return authRepository.socialLogin(request)
    }
}
