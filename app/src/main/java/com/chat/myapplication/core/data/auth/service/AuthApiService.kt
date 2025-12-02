package com.chat.myapplication.core.data.auth.service

import com.chat.myapplication.core.data.auth.model.AddPhoneNumberRequest
import com.chat.myapplication.core.data.auth.model.AddPhoneNumberResponse
import com.chat.myapplication.core.data.auth.model.AllergiesResponse
import com.chat.myapplication.core.data.auth.model.ChangeEmailRequest
import com.chat.myapplication.core.data.auth.model.ChangeEmailResponse
import com.chat.myapplication.core.data.auth.model.ResetPasswordRequest
import com.chat.myapplication.core.data.auth.model.CountriesAreasResponse
import com.chat.myapplication.core.data.auth.model.PasskeyLoginRequest
import com.chat.myapplication.core.data.auth.model.PasskeyRegisterRequest
import com.chat.myapplication.core.data.auth.model.PasskeyRegisterResponse
import com.chat.myapplication.core.data.auth.model.SignInRequest
import com.chat.myapplication.core.data.auth.model.SignInResponse
import com.chat.myapplication.core.data.auth.model.UpdateAllergiesRequest
import com.chat.myapplication.core.data.auth.model.ViewProfileResponse
import com.chat.myapplication.core.data.auth.model.VerifyEmailChangeRequest
import com.chat.myapplication.core.data.auth.model.VerifyPhoneNumberRequest
import com.chat.myapplication.core.data.auth.model.VerifyPhoneNumberResponse
import com.chat.myapplication.core.exception.BaseResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

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

    @GET("customers/logout")
    suspend fun logout(): BaseResponse

    @GET("customers/allergies")
    suspend fun getAllergies(): AllergiesResponse

    @POST("customers/allergies")
    suspend fun updateAllergies(@Body request: UpdateAllergiesRequest): BaseResponse

    @GET("customers")
    suspend fun getCustomerProfile(): ViewProfileResponse

    @GET("customers/change-password-request")
    suspend fun changePasswordRequest(): BaseResponse

    @GET("customers/change-password-request/{change_password_token}")
    suspend fun verifyChangePasswordToken(@Path("change_password_token") token: String): BaseResponse

    @POST("customers/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): BaseResponse

    @POST("customers/passkey/register")
    suspend fun registerPasskey(@Body request: PasskeyRegisterRequest): PasskeyRegisterResponse

    @POST("customers/passkey/login")
    suspend fun loginWithPasskey(@Body request: PasskeyLoginRequest): SignInResponse
}