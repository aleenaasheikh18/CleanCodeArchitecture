package com.chat.myapplication.core.data.auth.usecase

import com.chat.myapplication.core.data.auth.model.SignInResponse
import com.chat.myapplication.core.data.auth.model.VerifyEmailChangeRequest
import com.chat.myapplication.core.data.auth.remote.AuthRepository
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ViewModelScoped
class VerifyEmailChangeUseCase @Inject constructor(private val authRepository: AuthRepository) {

    operator fun invoke(params: Params): Flow<SignInResponse> {
        return authRepository.verifyEmailChange(params.request)
    }

    data class Params(val request: VerifyEmailChangeRequest)
}
