package com.chat.myapplication.core.domain

import android.net.Uri
import android.util.Log
import com.chat.myapplication.core.exception.BaseError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach

sealed class State<T> {
    class Idle<T> : State<T>()
    class Loading<T> : State<T>()
    data class Success<T>(val data: T) : State<T>()
    data class Error<T>(val message: String) : State<T>()

    fun isIdle(): Boolean = this is Idle
    fun isLoading(): Boolean = this is Loading
    fun isSuccessful(): Boolean = this is Success
    fun isFailed(): Boolean = this is Error
    fun getDataOrNull(): T? = (this as? Success)?.data

    companion object {
        fun <T> idle() = Idle<T>()
        fun <T> loading() = Loading<T>()
        fun <T> success(data: T) = Success(data)
        fun <T> error(message: String, code: String = "", uri: Uri? = null) = Error<T>(message)
    }
}

sealed class ApiState<T> {
    class Loading<T> : ApiState<T>()
    data class Success<T>(val data: T) : ApiState<T>()
    data class StatusFailed<T>(val message: String) : ApiState<T>()
    data class Error<T>(val error: BaseError) : ApiState<T>()
    data class CustomError<T>(val customErrorCode: Int, val message: String) : ApiState<T>()

    companion object {
        fun <T> loading() = Loading<T>()
        fun <T> success(data: T) = Success(data)
        fun <T> statusFailed(message: String) = StatusFailed<T>(message)
        fun <T> error(error: BaseError) = Error<T>(error)
        fun <T> customError(customErrorCode: Int, message: String) =
            CustomError<T>(customErrorCode, message)
    }
}

inline fun <T> Flow<ApiState<T>>.onApiSuccess(crossinline block: suspend (T) -> Unit): Flow<ApiState<T>> =
    onEach {
        if (it is ApiState.Success) block(it.data)
    }

inline fun <T> Flow<ApiState<T>>.onApiStatusFailed(crossinline block: suspend (ApiState.StatusFailed<T>) -> Unit): Flow<ApiState<T>> =
    onEach {
        if (it is ApiState.StatusFailed) block(it)
    }

inline fun <T> Flow<ApiState<T>>.onApiError(crossinline block: suspend (BaseError) -> Unit): Flow<ApiState<T>> =
    onEach {
        if (it is ApiState.Error) block(it.error)
    }

inline fun <T> Flow<ApiState<T>>.onCustomError(crossinline block: suspend (Int, String) -> Unit): Flow<ApiState<T>> =
    onEach {
        if (it is ApiState.CustomError) block(it.customErrorCode, it.message)
    }

inline fun <T> Flow<ApiState<T>>.onApiFailure(crossinline block: suspend (String) -> Unit): Flow<ApiState<T>> =
    onEach {
        when (it) {
            is ApiState.StatusFailed -> {
                Log.d("onApiFailure", "StatusFailed - message='${it.message}'")
                block(it.message)
            }
            is ApiState.Error -> {
                Log.d("onApiFailure", "Error - errorMessage='${it.error.errorMessage}'")
                block(it.error.errorMessage)
            }
            else -> Unit
        }
    }
