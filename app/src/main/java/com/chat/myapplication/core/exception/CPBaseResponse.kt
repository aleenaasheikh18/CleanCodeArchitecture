package com.chat.myapplication.core.exception

import com.google.gson.annotations.SerializedName

open class CPBaseResponse: Throwable() {
    @SerializedName("status")
    val status: Boolean = false

    @SerializedName("version")
    val version: String = ""

    @SerializedName("message")
    override var message: String = ""

    @SerializedName("code")
    var code: Int = 0
}