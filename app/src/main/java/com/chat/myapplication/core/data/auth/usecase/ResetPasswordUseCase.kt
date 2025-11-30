package com.chat.myapplication.core.data.auth.usecase

import com.chat.myapplication.core.data.auth.model.ResetPasswordRequest
import com.chat.myapplication.core.data.auth.remote.AuthRepository
import com.chat.myapplication.core.exception.BaseResponse
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ViewModelScoped
class ResetPasswordUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(params: Params): Flow<BaseResponse> {
        return authRepository.resetPassword(params.request)
    }

    data class Params(val request: ResetPasswordRequest)
}
