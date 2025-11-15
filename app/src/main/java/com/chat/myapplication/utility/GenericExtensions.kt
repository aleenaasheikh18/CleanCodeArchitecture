package com.chat.myapplication.utility

import com.chat.myapplication.core.domain.ApiState
import com.chat.myapplication.core.exception.ApiException
import com.chat.myapplication.core.exception.CPBaseError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart


fun <T> Flow<T>.collectAsResult(): Flow<ApiState<T>> {
    return this.map<T, ApiState<T>> {
        ApiState.Success(it)
    }.onStart {
        emit(ApiState.Loading())
    }.catch { exception ->
        val error = when (exception) {
            is ApiException -> {
                CPBaseError(
                    errorMessage = exception.message,
                    customErrorCode = exception.customErrorCode,
                    errorBody = exception.errorBody.orEmpty(),
                    isCustomErrorCode = exception.isCustomErrorCode
                )
            }

            else -> {
                CPBaseError(
                    errorMessage = exception.message ?: "Unknown Exception",
                )
            }
        }
        emit(ApiState.Error(error))
    }
}

val Any.TAG: String
    get() {
        val tag = javaClass.simpleName
        return if (tag.length <= 23) tag else tag.take(23)
    }
