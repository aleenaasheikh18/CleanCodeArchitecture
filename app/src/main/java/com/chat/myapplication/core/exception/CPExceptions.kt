package com.chat.myapplication.core.exception

import java.io.IOException

class CPHTTPBadRequest constructor(message: String) : Throwable(message)

class CPHTTPNotFoundException constructor(message: String) : IOException(message)

class CPServerNotAvailableException(message: String) : IOException(message)

class CPNetworkException(throwable: Throwable) : IOException(throwable.message, throwable)

data class ApiException(
    val code: CPResponseErrors,
    val errorBody: String?,
    override val message: String = "HTTP error $code",
    val customErrorCode: Int = 0,
    val isCustomErrorCode: Boolean = false
) : IOException(message)

