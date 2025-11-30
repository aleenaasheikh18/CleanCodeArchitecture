package com.chat.myapplication.core.data.settings.model

import com.google.gson.annotations.SerializedName

data class NotificationSettingsRequest(
    @SerializedName("notification") val notification: Boolean,
    @SerializedName("i_am_hungry") val iAmHungry: String,
    @SerializedName("new_message") val newMessage: String,
    @SerializedName("receipt") val receipt: String,
    @SerializedName("product_announcement") val productAnnouncement: Boolean
)
