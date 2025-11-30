package com.chat.myapplication.core.data.auth.usecase

import com.chat.myapplication.core.data.auth.model.VerifyPhoneNumberRequest
import com.chat.myapplication.core.data.auth.model.VerifyPhoneNumberResponse
import com.chat.myapplication.core.data.auth.remote.AuthRepository
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ViewModelScoped
class VerifyPhoneNumberUseCase @Inject constructor(private val authRepository: AuthRepository) {

    operator fun invoke(params: Params): Flow<VerifyPhoneNumberResponse> {
        return authRepository.verifyPhoneNumber(params.request)
    }

    data class Params(val request: VerifyPhoneNumberRequest)
}
