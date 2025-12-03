package com.chat.myapplication.ui.wizard.onboarding

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.viewModelScope
import com.chat.myapplication.core.data.auth.model.EmailValidationRequest
import com.chat.myapplication.core.data.auth.model.RegisterRequest
import com.chat.myapplication.core.data.auth.model.SignInData
import com.chat.myapplication.core.data.auth.model.SocialLoginRequest
import com.chat.myapplication.core.data.auth.usecase.AuthenticatePasskeyUseCase
import com.chat.myapplication.core.data.auth.usecase.RegisterUseCase
import com.chat.myapplication.core.data.auth.usecase.SendLoginLinkUseCase
import com.chat.myapplication.core.data.auth.usecase.SocialLoginUseCase
import com.chat.myapplication.core.data.auth.usecase.ValidateEmailUseCase
import com.chat.myapplication.core.domain.google.GoogleSignInCancelledException
import com.chat.myapplication.core.domain.google.GoogleSignInManager
import com.chat.myapplication.core.domain.passkey.PasskeyException
import com.chat.myapplication.core.domain.passkey.PasskeyManager
import com.chat.myapplication.ui.wizard.BaseWizardViewModel
import com.chat.myapplication.utility.AppConstants
import com.chat.myapplication.utility.PreferenceManager
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.util.TimeZone
import javax.inject.Inject

@HiltViewModel
class WizardViewModel @Inject constructor(
    private val passkeyManager: PasskeyManager,
    private val googleSignInManager: GoogleSignInManager,
    private val authenticatePasskeyUseCase: AuthenticatePasskeyUseCase,
    private val validateEmailUseCase: ValidateEmailUseCase,
    private val socialLoginUseCase: SocialLoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val sendLoginLinkUseCase: SendLoginLinkUseCase,
    private val preferenceManager: PreferenceManager
) : BaseWizardViewModel() {

    private val _currentStep = MutableStateFlow<WizardStep>(WizardStep.Selection)
    val currentStep: StateFlow<WizardStep> = _currentStep.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _passwordError = MutableStateFlow<String?>(null)
    val passwordError: StateFlow<String?> = _passwordError.asStateFlow()

    private val _passkeyLoginState = MutableSharedFlow<PasskeyLoginState>()
    val passkeyLoginState: SharedFlow<PasskeyLoginState> = _passkeyLoginState.asSharedFlow()

    private val _googleLoginState = MutableSharedFlow<GoogleLoginState>()
    val googleLoginState: SharedFlow<GoogleLoginState> = _googleLoginState.asSharedFlow()

    private val _registerState = MutableSharedFlow<RegisterState>()
    val registerState: SharedFlow<RegisterState> = _registerState.asSharedFlow()

    private val _firstName = MutableStateFlow("")
    val firstName: StateFlow<String> = _firstName.asStateFlow()

    private val _lastName = MutableStateFlow("")
    val lastName: StateFlow<String> = _lastName.asStateFlow()

    private val _firstNameError = MutableStateFlow<String?>(null)
    val firstNameError: StateFlow<String?> = _firstNameError.asStateFlow()

    private val _lastNameError = MutableStateFlow<String?>(null)
    val lastNameError: StateFlow<String?> = _lastNameError.asStateFlow()

    private val _welcomeBackName = MutableStateFlow<String?>(null)
    val welcomeBackName: StateFlow<String?> = _welcomeBackName.asStateFlow()

    private val _emailValidationEvent = MutableSharedFlow<EmailValidationEvent>()
    val emailValidationEvent: SharedFlow<EmailValidationEvent> = _emailValidationEvent.asSharedFlow()

    fun prepareGoogleSignIn(activity: Activity, onReady: (Intent) -> Unit) {
        // Sign out from any previous account to allow user to choose
        googleSignInManager.signOutBeforeSignIn(activity) {
            val client = googleSignInManager.getGoogleSignInClient(activity)
            onReady(client.signInIntent)
        }
    }

    fun handleGoogleSignInResult(data: Intent?) {
        viewModelScope.launch {
            _googleLoginState.emit(GoogleLoginState.Loading)

            googleSignInManager.handleSignInResult(data)
                .onSuccess { account ->
                    performSocialLogin(account)
                }
                .onFailure { exception ->
                    // Don't show error if user explicitly canceled
                    if (exception is GoogleSignInCancelledException) {
                        _googleLoginState.emit(GoogleLoginState.Idle)
                    } else {
                        _googleLoginState.emit(
                            GoogleLoginState.Error(exception.message ?: "Google Sign-In failed")
                        )
                    }
                }
        }
    }

    private suspend fun performSocialLogin(account: GoogleSignInAccount) {
        val request = SocialLoginRequest(
            email = account.email ?: "",
            socialId = account.id ?: "",
            firstName = account.givenName ?: "",
            lastName = account.familyName ?: "",
            profileImage = account.photoUrl?.toString(),
            socialType = AppConstants.SOCIAL_TYPE_GMAIL,
            password = AppConstants.SOCIAL_LOGIN_PASSWORD,
            inviteCode = AppConstants.REFERRAL_CODE.ifEmpty { null },
            timeZone = TimeZone.getDefault().id
        )

        socialLoginUseCase(request)
            .catch { e ->
                _googleLoginState.emit(
                    GoogleLoginState.Error(e.message ?: "Login failed")
                )
            }
            .collect { response ->
                if (response.status && response.data != null) {
                    preferenceManager.handleDataAfterLogin(response.data)
                    _googleLoginState.emit(GoogleLoginState.Success(response.data))
                } else {
                    _googleLoginState.emit(
                        GoogleLoginState.Error(response.message.ifEmpty { "Login failed" })
                    )
                }
            }
    }

    fun goToEmailStep() {
        _currentStep.value = WizardStep.Email
    }

    fun goToPasswordStep() {
        if (!validateEmail()) {
            return
        }

        _isLoading.value = true
        viewModelScope.launch {
            validateEmailUseCase(EmailValidationRequest(email = _email.value.trim()))
                .catch { e ->
                    _isLoading.value = false
                    _emailError.value = e.message ?: "An error occurred"
                }
                .collect { response ->
                    _isLoading.value = false
                    // Check statusCode for actual success (200-299 range)
                    if (response.status && response.statusCode in 200..299) {
                        handleEmailValidationResponse(response)
                    } else {
                        // Always use message with fallback to ensure we show message, not statusCode
                        _emailError.value = response.message.ifEmpty { "An error occurred" }
                    }
                }
        }
    }

    private suspend fun handleEmailValidationResponse(response: com.chat.myapplication.core.data.auth.model.EmailValidationResponse) {
        val data = response.data ?: return

        when {
            data.canSignup -> {
                // Navigate to name screen
                _currentStep.value = WizardStep.Name
            }
            data.isSetPassword -> {
                // Navigate to password screen with welcome back message
                _welcomeBackName.value = data.firstName
                _currentStep.value = WizardStep.Password
            }
            !data.socialType.isNullOrEmpty() -> {
                // Show error message for social login
                _emailError.value = "This email is registered with ${data.socialType}. Please login via Google."
            }
            !data.isSetPassword -> {
                // User needs to set password - send login link and go to "You Got Mail"
                // Save first name to use in You Got Mail screen
                sendLoginLinkForPasswordSetup(data.firstName.orEmpty())
            }
            else -> {
                _emailError.value = "Unable to proceed with this email"
            }
        }
    }

    private suspend fun sendLoginLinkForPasswordSetup(firstName: String) {
        sendLoginLinkUseCase(_email.value.trim())
            .catch { e ->
                _emailError.value = e.message ?: "Failed to send email"
            }
            .collect { response ->
                if (response.status && response.statusCode in 200..299) {
                    // Navigate to You Got Mail screen with firstName
                    _emailValidationEvent.emit(EmailValidationEvent.ShowYouGotMail(_email.value, firstName))
                } else {
                    _emailError.value = response.message.ifEmpty { "Failed to send email" }
                }
            }
    }

    fun goBack() {
        _currentStep.value = when (_currentStep.value) {
            is WizardStep.Password -> {
                // If we have welcomeBackName, it means we came from email directly
                if (_welcomeBackName.value != null) {
                    _welcomeBackName.value = null
                    WizardStep.Email
                } else {
                    WizardStep.Name
                }
            }
            is WizardStep.Name -> WizardStep.Email
            is WizardStep.Email -> WizardStep.Selection
            else -> WizardStep.Selection
        }
    }

    fun setFirstName(firstName: String) {
        _firstName.value = firstName
        _firstNameError.value = null
    }

    fun setLastName(lastName: String) {
        _lastName.value = lastName
        _lastNameError.value = null
    }

    fun goToPasswordStepFromName() {
        if (!validateName()) {
            return
        }

        // Register the user - register API already sends login link email automatically
        _isLoading.value = true
        viewModelScope.launch {
            val request = RegisterRequest(
                firstName = _firstName.value.trim(),
                lastName = _lastName.value.trim(),
                email = _email.value.trim(),
                timeZone = TimeZone.getDefault().id,
                inviteCode = AppConstants.REFERRAL_CODE.ifEmpty { null }
            )

            registerUseCase(request)
                .catch { e ->
                    _isLoading.value = false
                    _registerState.emit(RegisterState.Error(e.message ?: "Registration failed"))
                }
                .collect { response ->
                    _isLoading.value = false
                    if (response.status && response.statusCode in 200..299) {
                        // Registration successful - register API already sent login link email
                        // Navigate to "You Got Mail" screen
                        _registerState.emit(RegisterState.Success(_email.value))
                    } else {
                        _registerState.emit(RegisterState.Error(response.message.ifEmpty { "Registration failed" }))
                    }
                }
        }
    }

    private fun validateName(): Boolean {
        var isValid = true

        if (_firstName.value.trim().isEmpty()) {
            _firstNameError.value = "First name is required"
            isValid = false
        }

        if (_lastName.value.trim().isEmpty()) {
            _lastNameError.value = "Last name is required"
            isValid = false
        }

        return isValid
    }

    fun setPassword(password: String) {
        // Trim password to remove leading/trailing whitespace
        _password.value = password.trim()
        _passwordError.value = null
    }

    private fun validatePassword(): Boolean {
        val passwordValue = _password.value
        return when {
            passwordValue.isEmpty() -> {
                _passwordError.value = "Password is required"
                false
            }
            passwordValue.length < 6 -> {
                _passwordError.value = "Password must be at least 6 characters"
                false
            }
            else -> {
                _passwordError.value = null
                true
            }
        }
    }

    fun submit(): Boolean {
        return validatePassword()
    }

    fun setLoginError(error: String) {
        _passwordError.value = error
    }

    fun authenticateWithPasskey(activity: Activity) {
        viewModelScope.launch {
            _passkeyLoginState.emit(PasskeyLoginState.Loading)

            passkeyManager.authenticatePasskey(activity)
                .onSuccess { loginRequest ->
                    authenticatePasskeyUseCase(loginRequest)
                        .catch { e ->
                            _passkeyLoginState.emit(
                                PasskeyLoginState.Error(e.message ?: "Login failed")
                            )
                        }
                        .collect { response ->
                            if (response.status && response.data != null) {
                                preferenceManager.handleDataAfterLogin(response.data)
                                _passkeyLoginState.emit(PasskeyLoginState.Success(response.data))
                            } else {
                                _passkeyLoginState.emit(
                                    PasskeyLoginState.Error(response.message.ifEmpty { "Login failed" })
                                )
                            }
                        }
                }
                .onFailure { exception ->
                    val errorMessage = when (exception) {
                        is PasskeyException.UserCancelled -> null // Don't show error for user cancellation
                        is PasskeyException -> exception.message
                        else -> exception.message ?: "Authentication failed"
                    }
                    if (errorMessage != null) {
                        _passkeyLoginState.emit(PasskeyLoginState.Error(errorMessage))
                    } else {
                        _passkeyLoginState.emit(PasskeyLoginState.Idle)
                    }
                }
        }
    }
}

sealed class PasskeyLoginState {
    data object Idle : PasskeyLoginState()
    data object Loading : PasskeyLoginState()
    data class Success(val data: SignInData) : PasskeyLoginState()
    data class Error(val message: String) : PasskeyLoginState()
}

sealed class EmailValidationEvent {
    data class ShowYouGotMail(val email: String, val firstName: String) : EmailValidationEvent()
}

sealed class GoogleLoginState {
    data object Idle : GoogleLoginState()
    data object Loading : GoogleLoginState()
    data class Success(val data: SignInData) : GoogleLoginState()
    data class Error(val message: String) : GoogleLoginState()
}

sealed class RegisterState {
    data class Success(val email: String) : RegisterState()
    data class Error(val message: String) : RegisterState()
}
