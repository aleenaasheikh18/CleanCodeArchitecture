package com.chat.myapplication.core.data.auth.model

import com.chat.myapplication.utility.empty
import com.google.gson.annotations.SerializedName

data class VerifyEmailChangeRequest(
    @SerializedName("verification_token") val verificationToken: String = String.empty
)
