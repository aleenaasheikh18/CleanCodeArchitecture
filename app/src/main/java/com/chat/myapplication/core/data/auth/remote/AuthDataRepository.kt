package com.chat.myapplication.core.data.auth.remote

import com.chat.myapplication.core.data.auth.model.AddPhoneNumberRequest
import com.chat.myapplication.core.data.auth.model.AddPhoneNumberResponse
import com.chat.myapplication.core.data.auth.model.AllergiesResponse
import com.chat.myapplication.core.data.auth.model.ChangeEmailRequest
import com.chat.myapplication.core.data.auth.model.ChangeEmailResponse
import com.chat.myapplication.core.data.auth.model.EmailValidationRequest
import com.chat.myapplication.core.data.auth.model.EmailValidationResponse
import com.chat.myapplication.core.data.auth.model.RegisterRequest
import com.chat.myapplication.core.data.auth.model.ResetPasswordRequest
import com.chat.myapplication.core.data.auth.model.SocialLoginRequest
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
import com.chat.myapplication.core.data.auth.service.AuthApiService
import com.chat.myapplication.core.exception.BaseResponse
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

    override fun logout(): Flow<BaseResponse> {
        return flow {
            emit(authApiService.logout())
        }
    }

    override fun getAllergies(): Flow<AllergiesResponse> {
        return flow {
            emit(authApiService.getAllergies())
        }
    }

    override fun updateAllergies(request: UpdateAllergiesRequest): Flow<BaseResponse> {
        return flow {
            emit(authApiService.updateAllergies(request))
        }
    }

    override fun getCustomerProfile(): Flow<ViewProfileResponse> {
        return flow {
            emit(authApiService.getCustomerProfile())
        }
    }

    override fun changePasswordRequest(): Flow<BaseResponse> {
        return flow {
            emit(authApiService.changePasswordRequest())
        }
    }

    override fun resetPassword(request: ResetPasswordRequest): Flow<BaseResponse> {
        return flow {
            emit(authApiService.resetPassword(request))
        }
    }

    override fun verifyChangePasswordToken(token: String): Flow<BaseResponse> {
        return flow {
            emit(authApiService.verifyChangePasswordToken(token))
        }
    }

    override fun registerPasskey(request: PasskeyRegisterRequest): Flow<PasskeyRegisterResponse> {
        return flow {
            emit(authApiService.registerPasskey(request))
        }
    }

    override fun loginWithPasskey(request: PasskeyLoginRequest): Flow<SignInResponse> {
        return flow {
            emit(authApiService.loginWithPasskey(request))
        }
    }

    override fun validateEmail(request: EmailValidationRequest): Flow<EmailValidationResponse> {
        return flow {
            emit(authApiService.validateEmail(request))
        }
    }

    override fun socialLogin(request: SocialLoginRequest): Flow<SignInResponse> {
        return flow {
            emit(authApiService.socialLogin(request))
        }
    }

    override fun register(request: RegisterRequest): Flow<BaseResponse> {
        return flow {
            emit(authApiService.register(request))
        }
    }

    override fun sendLoginLink(): Flow<BaseResponse> {
        return flow {
            emit(authApiService.sendLoginLink())
        }
    }

    override fun verifyLoginToken(token: String): Flow<SignInResponse> {
        return flow {
            emit(authApiService.verifyLoginToken(token))
        }
    }
}
