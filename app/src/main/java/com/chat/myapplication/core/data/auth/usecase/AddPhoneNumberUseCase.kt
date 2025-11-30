package com.chat.myapplication.core.data.auth.usecase

import com.chat.myapplication.core.data.auth.model.AddPhoneNumberRequest
import com.chat.myapplication.core.data.auth.model.AddPhoneNumberResponse
import com.chat.myapplication.core.data.auth.remote.AuthRepository
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ViewModelScoped
class AddPhoneNumberUseCase @Inject constructor(private val authRepository: AuthRepository) {

    operator fun invoke(params: Params): Flow<AddPhoneNumberResponse> {
        return authRepository.addPhoneNumber(params.request)
    }

    data class Params(val request: AddPhoneNumberRequest)
}
