package com.chat.myapplication.core.data.auth.remote

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
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    fun signIn(signInRequest: SignInRequest): Flow<SignInResponse>

    fun changeEmail(request: ChangeEmailRequest): Flow<ChangeEmailResponse>

    fun verifyEmailChange(request: VerifyEmailChangeRequest): Flow<SignInResponse>

    fun getCountriesAreas(): Flow<CountriesAreasResponse>

    fun addPhoneNumber(request: AddPhoneNumberRequest): Flow<AddPhoneNumberResponse>

    fun verifyPhoneNumber(request: VerifyPhoneNumberRequest): Flow<VerifyPhoneNumberResponse>

    fun logout(): Flow<BaseResponse>

    fun getAllergies(): Flow<AllergiesResponse>

    fun updateAllergies(request: UpdateAllergiesRequest): Flow<BaseResponse>

    fun getCustomerProfile(): Flow<ViewProfileResponse>

    fun changePasswordRequest(): Flow<BaseResponse>

    fun resetPassword(request: ResetPasswordRequest): Flow<BaseResponse>

    fun verifyChangePasswordToken(token: String): Flow<BaseResponse>

    fun registerPasskey(request: PasskeyRegisterRequest): Flow<PasskeyRegisterResponse>

    fun loginWithPasskey(request: PasskeyLoginRequest): Flow<SignInResponse>
}