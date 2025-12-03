package com.chat.myapplication.utility

import android.content.Context
import android.graphics.drawable.LayerDrawable
import android.util.Log
import android.widget.Toast
import com.chat.myapplication.core.domain.ApiState
import com.chat.myapplication.core.exception.ApiException
import com.chat.myapplication.core.exception.BaseError
import com.chat.myapplication.core.exception.BaseResponse
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import retrofit2.HttpException


fun <T> Flow<T>.collectAsResult(): Flow<ApiState<T>> {
    return this.map<T, ApiState<T>> { response ->
        // Check if response is BaseResponse and has error statusCode
        if (response is BaseResponse) {
            Log.d("collectAsResult", "Response: statusCode=${response.statusCode}, message='${response.message}', status=${response.status}")
            when {
                response.statusCode in 200..299 -> {
                    // Success status codes (200-299)
                    Log.d("collectAsResult", "Success - emitting ApiState.Success")
                    ApiState.Success(response)
                }
                else -> {
                    // Error status codes (400, 500, etc.)
                    // Always use message with fallback to ensure message is shown, not statusCode
                    val errorMessage = response.message.ifEmpty { "An error occurred" }
                    Log.d("collectAsResult", "Error - statusCode=${response.statusCode}, extracted errorMessage='$errorMessage'")
                    ApiState.StatusFailed(errorMessage)
                }
            }
        } else {
            // Not a BaseResponse, treat as success
            ApiState.Success(response)
        }
    }.onStart {
        emit(ApiState.Loading())
    }.catch { exception ->
        Log.d("collectAsResult", "Exception caught: ${exception.javaClass.simpleName}, message='${exception.message}'")

        val errorMessage = when (exception) {
            is HttpException -> {
                // Parse error body to extract actual message
                try {
                    val errorBody = exception.response()?.errorBody()?.string()
                    Log.d("collectAsResult", "HttpException errorBody: $errorBody")
                    if (errorBody != null) {
                        val baseResponse = Gson().fromJson(errorBody, BaseResponse::class.java)
                        val message = baseResponse.message.ifEmpty { "An error occurred" }
                        Log.d("collectAsResult", "Parsed error message: '$message'")
                        message
                    } else {
                        exception.message ?: "An error occurred"
                    }
                } catch (e: Exception) {
                    Log.d("collectAsResult", "Failed to parse error body: ${e.message}")
                    exception.message ?: "An error occurred"
                }
            }
            is ApiException -> {
                exception.message ?: "An error occurred"
            }
            else -> {
                exception.message ?: "Unknown Exception"
            }
        }

        Log.d("collectAsResult", "Final errorMessage: '$errorMessage'")

        val error = when (exception) {
            is ApiException -> {
                BaseError(
                    errorMessage = errorMessage,
                    customErrorCode = exception.customErrorCode,
                    errorBody = exception.errorBody.orEmpty(),
                    isCustomErrorCode = exception.isCustomErrorCode
                )
            }
            else -> {
                BaseError(
                    errorMessage = errorMessage,
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

fun NavigationView.setBadgeCount(itemId: Int, count: Int) {
    val menuItem = menu.findItem(itemId) ?: return
    val icon = menuItem.icon ?: return
    val badge = NumericBadgeDrawable(context).apply { this.count = count }
    menuItem.icon = LayerDrawable(arrayOf(icon, badge))
}

fun NavigationView.setBadgeCount(itemId: Int, text: String) {
    val menuItem = menu.findItem(itemId) ?: return
    val icon = menuItem.icon ?: return
    val badge = StringBadgeDrawable(context).apply { this.text = text }
    menuItem.icon = LayerDrawable(arrayOf(icon, badge))
}

fun showToast(context: Context?,text: String) {
    Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
}

