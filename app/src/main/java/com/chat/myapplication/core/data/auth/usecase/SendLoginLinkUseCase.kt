package com.chat.myapplication.core.data.auth.usecase

import com.chat.myapplication.core.data.auth.remote.AuthRepository
import com.chat.myapplication.core.exception.BaseResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SendLoginLinkUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(email: String): Flow<BaseResponse> {
        return authRepository.sendLoginLink(email)
    }
}
