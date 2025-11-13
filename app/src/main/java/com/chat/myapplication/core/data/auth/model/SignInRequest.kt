package com.chat.myapplication.core.data.auth.model

import com.chat.myapplication.utility.empty
import com.google.gson.annotations.SerializedName

class SignInRequest(
    @SerializedName("email") val email: String = String.empty,
    @SerializedName("password") val password: String = String.empty,
)
