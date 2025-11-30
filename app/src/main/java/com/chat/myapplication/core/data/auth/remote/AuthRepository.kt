package com.chat.myapplication.core.data.auth.remote

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
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    fun signIn(signInRequest: SignInRequest): Flow<SignInResponse>

    fun changeEmail(request: ChangeEmailRequest): Flow<ChangeEmailResponse>

    fun verifyEmailChange(request: VerifyEmailChangeRequest): Flow<SignInResponse>

    fun getCountriesAreas(): Flow<CountriesAreasResponse>

    fun addPhoneNumber(request: AddPhoneNumberRequest): Flow<AddPhoneNumberResponse>

    fun verifyPhoneNumber(request: VerifyPhoneNumberRequest): Flow<VerifyPhoneNumberResponse>
}