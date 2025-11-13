package com.chat.myapplication.core.exception

import com.chat.myapplication.utility.empty
import com.google.gson.annotations.SerializedName


data class CPBaseError(
    @SerializedName("errors") val errorList: ArrayList<RemittanceErrors>? = arrayListOf(),
    @SerializedName("errorMessage") val errorMessage: String = "",
    @SerializedName("errorCode") val errorCode: CPResponseErrors = CPResponseErrors.RESPONSE_ERROR,
    @SerializedName("customErrorCode") val customErrorCode: Int = 0,
    @SerializedName("isCustomErrorCode") val isCustomErrorCode: Boolean = false,
    @SerializedName("errorBody") val errorBody: String = ""
) : CPBaseResponse()

enum class CPResponseErrors {
    HTTP_UNAUTHORIZED,
    HTTP_TOO_MANY_REQUEST,
    HTTP_BAD_REQUEST,
    HTTP_FORBIDDEN,
    HTTP_NOT_FOUND,
    CONNECTIVITY_EXCEPTION,
    HTTP_UNAVAILABLE,
    RESPONSE_ERROR,
    UNKNOWN_EXCEPTION,
    INTERNAL_SERVER_ERROR,
    HTTP_PRECON_FAILED,
    HTTP_UNPROCESSABLE_ENTITY,
    USE_NOT_FOUND,
    HTTP_TRANSACTION_FAILED,
    PIN_ATTEMPT_EXHAUSTED,
    INVALID_PIN_ATTEMPT,
    SESSION_TOKEN_EXPIRED,
    KYC_REJECTED,
    KYC_PENDING,
    DELETE_ADDED_CARD_ERROR_CODE,
}

enum class CustomResponseCodes(val value: Int) {
    PIN_ATTEMPT_EXHAUSTED(102),
    VALIDATION_ERROR(101),
    NAME_CARD_MISMATCH(128),
    INVALID_PIN_ATTEMPT(110),
    PIN_SCREEN_REQUIRED(109),
    INVALID_AUTH_CODE(103),
    DEVICE_NOT_TRUSTED(105),
    SCHEDULED_MAINTENANCE_ALERT(126),
    VIP_DEVICE_NOT_TRUSTED(106),
    VPN_DETECTED(112),
    USE_NOT_FOUND(113),
    INTERNET_CONNECTION_UNAVAILABLE(799),
    SERVER_UNAVAILABLE(899),
    SESSION_TOKEN_EXPIRED(116),
    KYC_REJECTED(120),
    KYC_PENDING(121),
    DELETE_ADDED_CARD_ERROR_CODE(123),
    UAE_PASS_TOKEN_EXPIRED (129),
    UAE_PASS_REQUEST_EXISTS(130),
}

data class RemittanceErrors(
    @SerializedName("label")
    val label : String? = String.empty,
    @SerializedName("errors")
    val errors : ArrayList<String>? = arrayListOf()
)