package com.chat.myapplication.core.data.auth.model

import com.chat.myapplication.core.exception.BaseResponse
import com.google.gson.annotations.SerializedName

/**
 * Response model for social login API endpoint
 * Structure: data contains Customer object directly (not wrapped in SignInData)
 * Token is stored as 'authtoken' field inside Customer object
 */
data class SocialLoginResponse(
    @SerializedName("data") val data: Customer? = null,
    @SerializedName("driver_signup_url") val driverSignupUrl: String? = null,
    @SerializedName("driver_ios_app_store_url") val driverIosUrl: String? = null,
    @SerializedName("driver_android_app_store_url") val driverAndroidUrl: String? = null
) : BaseResponse()
