package com.chat.myapplication.core.data.auth.remote

import com.chat.myapplication.core.data.auth.model.SignInRequest
import com.chat.myapplication.core.data.auth.model.SignInResponse
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    fun signIn(signInRequest: SignInRequest): Flow<SignInResponse>
}