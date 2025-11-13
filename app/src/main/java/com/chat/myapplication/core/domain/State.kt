package com.chat.myapplication.core.domain

import android.net.Uri
import com.chat.myapplication.core.exception.CPBaseError
import com.chat.myapplication.core.exception.CPResponseErrors
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach

/**
 * State Management for UI & Data.
 */
sealed class State<T> {
    class Loading<T> : State<T>()

    data class Success<T>(val data: T) : State<T>()

    data class Error<T>(val message: String, val code : CPResponseErrors = CPResponseErrors.RESPONSE_ERROR, val uri : Uri? = null) : State<T>()

    fun isLoading(): Boolean = this is Loading

    fun isSuccessful(): Boolean = this is Success

    fun isFailed(): Boolean = this is Error

    fun getDataOrNull(): T? = (this as? Success)?.data

    companion object {

        /**
         * Returns [State.Loading] instance.
         */
        fun <T> loading() = Loading<T>()

        /**
         * Returns [State.Success] instance.
         * @param data Data to emit with status.
         */
        fun <T> success(data: T) =
            Success(data)

        /**
         * Returns [State.Error] instance.
         * @param message Description of failure.
         */
        fun <T> error(message: String, code : CPResponseErrors = CPResponseErrors.RESPONSE_ERROR, uri : Uri? = null) =
            Error<T>(message, code, uri)

    }
}

/**
 * State Management for API Response.
 */
sealed class ApiState<T> {

    class Loading<T> : ApiState<T>()

    data class Success<T>(val data: T) : ApiState<T>()
    data class StatusFailed<T>(val message : String) : ApiState<T>()
    data class Error<T>(val error : CPBaseError) : ApiState<T>()
    data class CustomError<T>(val customErrorCode : Int, val message : String) : ApiState<T>()

    companion object {

        /**
         * Returns [State.Loading] instance.
         */
        fun <T> loading() = Loading<T>()

        /**
         * Returns [ApiState.Success] instance.
         * @param data Data to emit with status.
         */
        fun <T> success(data: T) =
            Success(data)

        /**
         * Returns [ApiState.StatusFailed] instance.
         * @param error Description of failure.
         */
        fun <T> statusFailed(message : String) =
            StatusFailed<T>(message)

        /**
         * Returns [ApiState.Error] instance.
         * @param error Description of failure.
         */
        fun <T> error(error : CPBaseError) =
            Error<T>(error)

        /**
         * Returns [ApiState.CustomError] instance.
         * @param error Description of failure.
         */
        fun <T> customError(customErrorCode : Int, message : String) =
            CustomError<T>(customErrorCode,message)

    }
}

/**
 * This can be used to catch 2xx responses.
 */
inline fun <T> Flow<ApiState<T>>.onApiSuccess(
    crossinline block: suspend (T) -> Unit
): Flow<ApiState<T>> = onEach {
    if (it is ApiState.Success) {
        block(it.data)
    }
}

/**
 * This can be used to catch the failure which are success from retrofit side.
 * However, In out body response, status field is false.
 */
inline fun <T> Flow<ApiState<T>>.onApiStatusFailed(
    crossinline block: suspend (ApiState.StatusFailed<T>) -> Unit
): Flow<ApiState<T>> = onEach {
    if (it is ApiState.StatusFailed) {
        block(it)
    }
}

/**
 * This can be used to catch non-2xx response
 */
inline fun <T> Flow<ApiState<T>>.onApiError(
    crossinline block: suspend (CPBaseError) -> Unit
): Flow<ApiState<T>> = onEach {
    if (it is ApiState.Error) {
        block(it.error)
    }
}

/**
 * This is for handling custom error
 */
inline fun <T> Flow<ApiState<T>>.onCustomError(
    crossinline block: suspend (Int, String) -> Unit
): Flow<ApiState<T>> = onEach {
    if (it is ApiState.CustomError) {
        block(it.customErrorCode,it.message)
    }
}