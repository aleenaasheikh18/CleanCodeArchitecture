package com.chat.myapplication.core.data.auth.model

import com.chat.myapplication.core.exception.BaseResponse
import com.google.gson.annotations.SerializedName

data class ChangeEmailResponse(@SerializedName("can_change_email") val canChangeEmail: Boolean = false) : BaseResponse()
