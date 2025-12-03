package com.chat.myapplication.core.data.auth.model

import com.chat.myapplication.utility.empty
import com.google.gson.annotations.SerializedName

data class SocialLoginRequest(
    @SerializedName("email") val email: String = String.empty,
    @SerializedName("social_id") val socialId: String = String.empty,
    @SerializedName("first_name") val firstName: String = String.empty,
    @SerializedName("last_name") val lastName: String = String.empty,
    @SerializedName("profile_image") val profileImage: String? = null,
    @SerializedName("social_type") val socialType: String = String.empty,
    @SerializedName("password") val password: String = "social_login",
    @SerializedName("invite_code") val inviteCode: String? = null,
    @SerializedName("time_zone") val timeZone: String = String.empty
)
