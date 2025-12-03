package com.chat.myapplication.core.data.auth.usecase

import com.chat.myapplication.core.data.auth.model.RegisterRequest
import com.chat.myapplication.core.data.auth.remote.AuthRepository
import com.chat.myapplication.core.exception.BaseResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(request: RegisterRequest): Flow<BaseResponse> {
        return authRepository.register(request)
    }
}
