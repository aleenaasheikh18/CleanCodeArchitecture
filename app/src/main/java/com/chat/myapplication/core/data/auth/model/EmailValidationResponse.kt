package com.chat.myapplication.core.data.auth.model

import com.chat.myapplication.core.exception.BaseResponse
import com.chat.myapplication.utility.empty
import com.google.gson.annotations.SerializedName

data class EmailValidationResponse(
    @SerializedName("data") val data: EmailValidationData? = null
) : BaseResponse()

data class EmailValidationData(
    @SerializedName("exist") val exist: String? = null,
    @SerializedName("is_set_password") val isSetPassword: Boolean = false,
    @SerializedName("first_name") val firstName: String? = String.empty,
    @SerializedName("last_name") val lastName: String? = String.empty,
    @SerializedName("social_type") val socialType: String? = null,
    @SerializedName("can_signup") val canSignup: Boolean = false
)
