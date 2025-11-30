package com.chat.myapplication.ui.wizard.editphone

import com.chat.myapplication.core.data.auth.model.CountryArea

data class EditPhoneUiState(
    val currentStep: EditPhoneWizardStep = EditPhoneWizardStep.PhoneInput,
    val phoneNumber: String = "",
    val otpCode: String = "",
    val selectedCountry: CountryArea? = null,
    val countries: List<CountryArea> = emptyList(),
    val isCountriesLoading: Boolean = false,
    val isOtpVisible: Boolean = false,
    val isLoading: Boolean = false,
    val phoneError: Int? = null,
    val otpError: Int? = null,
    val resendCooldown: Int = 0
) {
    val isPhoneValid: Boolean
        get() = phoneNumber.length >= ValidationConstants.MIN_PHONE_LENGTH

    val isOtpValid: Boolean
        get() = otpCode.length >= ValidationConstants.MIN_OTP_LENGTH

    val isCountrySelected: Boolean
        get() = selectedCountry != null

    val canSubmitPhone: Boolean
        get() = isPhoneValid && isCountrySelected && !isLoading

    val canSubmitOtp: Boolean
        get() = isOtpValid && !isLoading

    val canResendOtp: Boolean
        get() = resendCooldown == 0 && !isLoading

    val fullPhoneNumber: String
        get() = buildString {
            selectedCountry?.countryCode?.let { append(it) }
            append(phoneNumber.trim())
        }
}

object ValidationConstants {
    const val MIN_PHONE_LENGTH = 7
    const val MIN_OTP_LENGTH = 4
    const val MAX_OTP_LENGTH = 6
    const val RESEND_COOLDOWN_SECONDS = 30
}

sealed class EditPhoneEvent {
    data object PhoneAdded : EditPhoneEvent()
    data object PhoneVerified : EditPhoneEvent()
    data object ResendSuccess : EditPhoneEvent()
    data class Error(val message: String) : EditPhoneEvent()
}
