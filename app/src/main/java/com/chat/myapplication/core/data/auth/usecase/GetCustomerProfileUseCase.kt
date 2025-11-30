package com.chat.myapplication.core.data.auth.usecase

import com.chat.myapplication.core.data.auth.model.ViewProfileResponse
import com.chat.myapplication.core.data.auth.remote.AuthRepository
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ViewModelScoped
class GetCustomerProfileUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Flow<ViewProfileResponse> = authRepository.getCustomerProfile()
}
