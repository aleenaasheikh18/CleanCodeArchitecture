package com.chat.myapplication.core.data.auth.model

import com.chat.myapplication.core.exception.CPBaseResponse
import com.chat.myapplication.utility.empty
import com.google.gson.annotations.SerializedName

data class SignInResponse(
    @SerializedName("email") val email: String = String.empty
) : CPBaseResponse()