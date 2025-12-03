package com.chat.myapplication.core.exception

import com.google.gson.annotations.SerializedName

open class BaseResponse: Throwable() {
    @SerializedName("status")
    val status: Boolean = false

    @SerializedName("statusCode")
    val statusCode: Int = 0

    @SerializedName("message")
    override var message: String = ""

    /**
     * Override toString to prevent statusCode from being displayed
     */
    override fun toString(): String {
        return message.ifEmpty { "An error occurred" }
    }
}