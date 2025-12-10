package com.chat.myapplication.core.data.auth.model

import com.google.gson.annotations.SerializedName

data class SendLoginLinkRequest(
    @SerializedName("email") val email: String
)
