package com.chat.myapplication.core.data.auth.usecase

import com.chat.myapplication.core.data.auth.model.EmailValidationRequest
import com.chat.myapplication.core.data.auth.model.EmailValidationResponse
import com.chat.myapplication.core.data.auth.remote.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ValidateEmailUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(request: EmailValidationRequest): Flow<EmailValidationResponse> {
        return authRepository.validateEmail(request)
    }
}
