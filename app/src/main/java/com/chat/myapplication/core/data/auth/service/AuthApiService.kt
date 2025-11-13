package com.chat.myapplication.core.data.auth.service

import com.chat.myapplication.core.data.auth.model.SignInRequest
import com.chat.myapplication.core.data.auth.model.SignInResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("customers/login")
    suspend fun signIn(@Body signInResponse: SignInRequest): SignInResponse
}