package com.chat.myapplication.core.data.auth.model

import com.chat.myapplication.utility.empty
import com.google.gson.annotations.SerializedName

data class RegisterRequest(
    @SerializedName("first_name") val firstName: String = String.empty,
    @SerializedName("last_name") val lastName: String = String.empty,
    @SerializedName("email") val email: String = String.empty,
    @SerializedName("time_zone") val timeZone: String = String.empty,
    @SerializedName("invite_code") val inviteCode: String? = null
)
