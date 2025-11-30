package com.chat.myapplication.core.data.auth.model

import com.chat.myapplication.utility.empty
import com.google.gson.annotations.SerializedName

data class ChangeEmailRequest(
    @SerializedName("new_email") val newEmail: String = String.empty
)
