package com.chat.myapplication.utility

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.chat.myapplication.R
import com.chat.myapplication.core.domain.ApiState
import com.chat.myapplication.core.exception.ApiException
import com.chat.myapplication.core.exception.CPBaseError
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import com.google.android.material.internal.NavigationMenuItemView
import com.google.android.material.navigation.NavigationView
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
