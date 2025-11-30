package com.chat.myapplication.core.data.auth.model

import com.google.gson.annotations.SerializedName

data class VerifyPhoneNumberRequest(
    @SerializedName("phone_no") val phoneNo: String,
    @SerializedName("code") val code: String,
    @SerializedName("country") val country: String,
    @SerializedName("currency") val currency: String,
    @SerializedName("currency_icon") val currencyIcon: String,
    @SerializedName("flag") val flag: String,
    @SerializedName("iso_code") val isoCode: String
)
