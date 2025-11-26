package com.chat.myapplication.core.exception

import com.google.gson.annotations.SerializedName

open class BaseResponse: Throwable() {
    @SerializedName("status")
    val status: Boolean = false

    @SerializedName("message")
    override var message: String = ""

}