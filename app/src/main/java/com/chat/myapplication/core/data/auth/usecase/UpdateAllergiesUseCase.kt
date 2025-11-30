package com.chat.myapplication.core.data.auth.usecase

import com.chat.myapplication.core.data.auth.model.UpdateAllergiesRequest
import com.chat.myapplication.core.data.auth.remote.AuthRepository
import com.chat.myapplication.core.exception.BaseResponse
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ViewModelScoped
class UpdateAllergiesUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(allergies: List<String>): Flow<BaseResponse> {
        return authRepository.updateAllergies(UpdateAllergiesRequest(allergies))
    }
}
