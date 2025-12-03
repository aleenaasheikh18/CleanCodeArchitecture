package com.chat.myapplication.core.data.auth.model

import com.google.gson.annotations.SerializedName

data class ResetPasswordRequest(
    @SerializedName("new_password") val newPassword: String
)
