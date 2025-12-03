package com.chat.myapplication.ui.wizard.editphone

import androidx.lifecycle.viewModelScope
import com.chat.myapplication.R
import com.chat.myapplication.base.BaseViewModel
import com.chat.myapplication.core.data.auth.model.AddPhoneNumberRequest
import com.chat.myapplication.core.data.auth.model.CountryArea
import com.chat.myapplication.core.data.auth.model.VerifyPhoneNumberRequest
import com.chat.myapplication.core.data.auth.usecase.AddPhoneNumberUseCase
import com.chat.myapplication.core.data.auth.usecase.GetCountriesAreasUseCase
import com.chat.myapplication.core.data.auth.usecase.VerifyPhoneNumberUseCase
import com.chat.myapplication.core.domain.onApiError
import com.chat.myapplication.core.domain.onApiSuccess
import com.chat.myapplication.utility.collectAsResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditPhoneViewModel @Inject constructor(
    private val addPhoneNumberUseCase: AddPhoneNumberUseCase,
    private val verifyPhoneNumberUseCase: VerifyPhoneNumberUseCase,
    private val getCountriesAreasUseCase: GetCountriesAreasUseCase
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(EditPhoneUiState())
    val uiState: StateFlow<EditPhoneUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<EditPhoneEvent>()
    val events: SharedFlow<EditPhoneEvent> = _events.asSharedFlow()

    private var resendCooldownJob: Job? = null

    init {
        loadCountries()
    }

    fun initializeWithCurrentPhone(fullPhoneNumber: String) {
        if (fullPhoneNumber.isEmpty()) return

        // Extract phone number without country code
        // Example: "+923061379872" -> country code "+92", phone "3061379872"
        val phoneWithoutPlus = fullPhoneNumber.removePrefix("+")

        // Try to find matching country by checking if phone starts with country code
        val countries = _uiState.value.countries
        var matchedCountry: CountryArea? = null
        var phoneNumber = phoneWithoutPlus

        for (country in countries) {
            val countryCode = country.countryCode?.removePrefix("+") ?: continue
            if (phoneWithoutPlus.startsWith(countryCode)) {
                matchedCountry = country
                phoneNumber = phoneWithoutPlus.removePrefix(countryCode)
                break
            }
        }

        _uiState.update {
            it.copy(
                phoneNumber = phoneNumber,
                selectedCountry = matchedCountry ?: it.selectedCountry
            )
        }
    }

    private fun loadCountries() {
        getCountriesAreasUseCase()
            .collectAsResult()
            .flowOn(Dispatchers.IO)
            .onApiSuccess { response ->
                if (response.status) {
                    val countries = response.data?.countries.orEmpty()
                    _uiState.update {
                        it.copy(
                            countries = countries,
                            selectedCountry = countries.firstOrNull(),
                            isCountriesLoading = false
                        )
                    }
                } else {
                    _uiState.update { it.copy(isCountriesLoading = false) }
                }
            }
            .onApiError {
                _uiState.update { it.copy(isCountriesLoading = false) }
            }
            .onStart {
                _uiState.update { it.copy(isCountriesLoading = true) }
            }
            .launchIn(viewModelScope)
    }

    fun setPhoneNumber(phone: String) {
        _uiState.update { it.copy(phoneNumber = phone, phoneError = null) }
    }

    fun clearPhoneNumber() {
        _uiState.update { it.copy(phoneNumber = "", phoneError = null) }
    }

    fun setOtpCode(otp: String) {
        _uiState.update { it.copy(otpCode = otp, otpError = null) }
    }

    fun clearOtpCode() {
        _uiState.update { it.copy(otpCode = "", otpError = null) }
    }

    fun setSelectedCountry(country: CountryArea) {
        _uiState.update { it.copy(selectedCountry = country, phoneError = null) }
    }

    fun addPhoneNumber() {
        val state = _uiState.value

        if (!validatePhoneInput()) return

        val request = AddPhoneNumberRequest(phoneNo = /*state.fullPhoneNumber*/"923272018758")

        addPhoneNumberUseCase(AddPhoneNumberUseCase.Params(request))
            .collectAsResult()
            .flowOn(Dispatchers.IO)
            .onApiSuccess { response ->
                if (response.status) {
                    _uiState.update { it.copy(isOtpVisible = true, isLoading = false) }
                    startResendCooldown()
                    _events.emit(EditPhoneEvent.PhoneAdded)
                } else {
                    _uiState.update { it.copy(isLoading = false) }
                    _events.emit(EditPhoneEvent.Error(response.message))
                }
            }
            .onApiError { error ->
                _uiState.update { it.copy(isLoading = false) }
                _events.emit(EditPhoneEvent.Error(error.errorMessage))
            }
            .onStart {
                _uiState.update { it.copy(isLoading = true) }
            }
            .launchIn(viewModelScope)
    }

    fun verifyPhoneNumber() {
        val state = _uiState.value

        if (!validateOtpInput()) return

        val country = state.selectedCountry ?: return

        val request = VerifyPhoneNumberRequest(
            phoneNo = /*state.fullPhoneNumber*/"923272018758",
            code = state.otpCode.trim(),
            country = country.name.orEmpty(),
            currency = country.currency.orEmpty(),
            currencyIcon = country.currencyIcon.orEmpty(),
            flag = country.flag.orEmpty(),
            isoCode = country.isoCode.orEmpty()
        )

        verifyPhoneNumberUseCase(VerifyPhoneNumberUseCase.Params(request))
            .collectAsResult()
            .flowOn(Dispatchers.IO)
            .onApiSuccess { response ->
                if (response.status) {
                    _uiState.update {
                        it.copy(
                            currentStep = EditPhoneWizardStep.Success,
                            isLoading = false
                        )
                    }
                    _events.emit(EditPhoneEvent.PhoneVerified)
                } else {
                    _uiState.update { it.copy(isLoading = false) }
                    _events.emit(EditPhoneEvent.Error(response.message))
                }
            }
            .onApiError { error ->
                _uiState.update { it.copy(isLoading = false) }
                _events.emit(EditPhoneEvent.Error(error.errorMessage))
            }
            .onStart {
                _uiState.update { it.copy(isLoading = true) }
            }
            .launchIn(viewModelScope)
    }

    fun resendOtp() {
        val state = _uiState.value
        if (!state.canResendOtp) return

        val request = AddPhoneNumberRequest(phoneNo = state.fullPhoneNumber)

        addPhoneNumberUseCase(AddPhoneNumberUseCase.Params(request))
            .collectAsResult()
            .flowOn(Dispatchers.IO)
            .onApiSuccess { response ->
                if (response.status) {
                    startResendCooldown()
                    _events.emit(EditPhoneEvent.ResendSuccess)
                }
            }
            .onApiError { error ->
                _events.emit(EditPhoneEvent.Error(error.errorMessage))
            }
            .launchIn(viewModelScope)
    }

    private fun startResendCooldown() {
        resendCooldownJob?.cancel()
        resendCooldownJob = viewModelScope.launch {
            var countdown = ValidationConstants.RESEND_COOLDOWN_SECONDS
            while (countdown > 0) {
                _uiState.update { it.copy(resendCooldown = countdown) }
                delay(1000L)
                countdown--
            }
            _uiState.update { it.copy(resendCooldown = 0) }
        }
    }

    private fun validatePhoneInput(): Boolean {
        val state = _uiState.value

        return when {
            state.selectedCountry == null -> {
                _uiState.update { it.copy(phoneError = R.string.error_country_required) }
                false
            }
            state.phoneNumber.trim().isEmpty() -> {
                _uiState.update { it.copy(phoneError = R.string.error_phone_required) }
                false
            }
            state.phoneNumber.trim().length < ValidationConstants.MIN_PHONE_LENGTH -> {
                _uiState.update { it.copy(phoneError = R.string.error_phone_invalid) }
                false
            }
            else -> true
        }
    }

    private fun validateOtpInput(): Boolean {
        val state = _uiState.value
        val otp = state.otpCode.trim()

        return when {
            otp.isEmpty() -> {
                _uiState.update { it.copy(otpError = R.string.error_otp_required) }
                false
            }
            otp.length < ValidationConstants.MIN_OTP_LENGTH -> {
                _uiState.update { it.copy(otpError = R.string.error_otp_invalid) }
                false
            }
            else -> true
        }
    }

    override fun onCleared() {
        super.onCleared()
        resendCooldownJob?.cancel()
    }
}
