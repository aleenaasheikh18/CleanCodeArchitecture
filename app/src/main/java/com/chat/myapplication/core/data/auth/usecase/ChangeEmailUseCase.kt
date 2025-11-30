package com.chat.myapplication.core.data.auth.usecase

import com.chat.myapplication.core.data.auth.model.ChangeEmailRequest
import com.chat.myapplication.core.data.auth.model.ChangeEmailResponse
import com.chat.myapplication.core.data.auth.remote.AuthRepository
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ViewModelScoped
class ChangeEmailUseCase @Inject constructor(private val authRepository: AuthRepository) {

    operator fun invoke(params: Params): Flow<ChangeEmailResponse> {
        return authRepository.changeEmail(params.request)
    }

    data class Params(val request: ChangeEmailRequest)
}
