package com.chat.myapplication.core.exception

import com.google.gson.annotations.SerializedName


data class CPBaseError(
    @SerializedName("errorMessage") val errorMessage: String = "",
    @SerializedName("customErrorCode") val customErrorCode: Int = 0,
    @SerializedName("isCustomErrorCode") val isCustomErrorCode: Boolean = false,
    @SerializedName("errorBody") val errorBody: String = ""
) : CPBaseResponse()

