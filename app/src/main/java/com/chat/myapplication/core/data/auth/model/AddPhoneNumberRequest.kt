package com.chat.myapplication.core.data.auth.model

import com.google.gson.annotations.SerializedName

data class AddPhoneNumberRequest(
    @SerializedName("phone_no") val phoneNo: String
)
