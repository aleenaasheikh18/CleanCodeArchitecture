package com.chat.myapplication.core.data.auth.remote

import com.chat.myapplication.core.data.auth.model.SignInRequest
import com.chat.myapplication.core.data.auth.model.SignInResponse
import com.chat.myapplication.core.data.auth.service.AuthApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AuthDataRepository @Inject constructor(private val authApiService: AuthApiService) :
    AuthRepository {

    override fun signIn(signInRequest: SignInRequest): Flow<SignInResponse> {
        return flow {
            emit(authApiService.signIn(signInRequest))
        }
    }

}
