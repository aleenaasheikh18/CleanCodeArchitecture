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

    override fun changeEmail(request: ChangeEmailRequest): Flow<ChangeEmailResponse> {
        return flow {
            emit(authApiService.changeEmail(request))
        }
    }

    override fun verifyEmailChange(request: VerifyEmailChangeRequest): Flow<SignInResponse> {
        return flow {
            emit(authApiService.verifyEmailChange(request))
        }
    }

    override fun getCountriesAreas(): Flow<CountriesAreasResponse> {
        return flow {
            emit(authApiService.getCountriesAreas())
        }
    }

    override fun addPhoneNumber(request: AddPhoneNumberRequest): Flow<AddPhoneNumberResponse> {
        return flow {
            emit(authApiService.addPhoneNumber(request))
        }
    }

    override fun verifyPhoneNumber(request: VerifyPhoneNumberRequest): Flow<VerifyPhoneNumberResponse> {
        return flow {
            emit(authApiService.verifyPhoneNumber(request))
        }
    }
}
