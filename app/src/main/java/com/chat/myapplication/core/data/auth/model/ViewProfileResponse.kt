package com.chat.myapplication.core.data.auth.model

import com.chat.myapplication.core.exception.BaseResponse
import com.google.gson.annotations.SerializedName

data class ViewProfileResponse(
    @SerializedName("data") val data: Customer? = null
) : BaseResponse()
