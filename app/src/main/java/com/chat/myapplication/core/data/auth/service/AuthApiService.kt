package com.chat.myapplication.core.data.auth.service

import com.chat.myapplication.core.data.auth.model.AddPhoneNumberRequest
import com.chat.myapplication.core.data.auth.model.AddPhoneNumberResponse
import com.chat.myapplication.core.data.auth.model.ChangeEmailRequest
import com.chat.myapplication.core.data.auth.model.ChangeEmailResponse
import com.chat.myapplication.core.data.auth.model.CountriesAreasResponse
import com.chat.myapplication.core.data.auth.model.SignInRequest
import com.chat.myapplication.core.data.auth.model.SignInResponse
import com.chat.myapplication.core.data.auth.model.VerifyEmailChangeRequest
import com.chat.myapplication.core.data.auth.model.VerifyPhoneNumberRequest
import com.chat.myapplication.core.data.auth.model.VerifyPhoneNumberResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT

interface AuthApiService {
    @POST("customers/login")
    suspend fun signIn(@Body signInResponse: SignInRequest): SignInResponse

    @PUT("customers/change-email")
    suspend fun changeEmail(@Body request: ChangeEmailRequest): ChangeEmailResponse

    @POST("customers/verify-email-change")
    suspend fun verifyEmailChange(@Body request: VerifyEmailChangeRequest): SignInResponse

    @GET("common/countries-areas")
    suspend fun getCountriesAreas(): CountriesAreasResponse

    @PUT("customers/add-phone-number")
    suspend fun addPhoneNumber(@Body request: AddPhoneNumberRequest): AddPhoneNumberResponse

    @PUT("customers/verify-phone-number")
    suspend fun verifyPhoneNumber(@Body request: VerifyPhoneNumberRequest): VerifyPhoneNumberResponse
}