package com.chat.myapplication.core.data.auth.usecase

import com.chat.myapplication.core.data.auth.model.SignInResponse
import com.chat.myapplication.core.data.auth.remote.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class VerifyLoginTokenUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(token: String): Flow<SignInResponse> {
        return authRepository.verifyLoginToken(token)
    }
}
