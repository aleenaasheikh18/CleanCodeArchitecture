package com.chat.myapplication.core.data.settings.service

import com.chat.myapplication.core.data.settings.model.NotificationSettingsRequest
import com.chat.myapplication.core.data.settings.model.NotificationSettingsResponse
import com.chat.myapplication.core.data.settings.model.UtilityResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface UtilityApiService {

    @GET("common/policy-documents")
    suspend fun getPolicyDocuments(): UtilityResponse

    @POST("customers/setting")
    suspend fun updateNotificationSettings(@Body request: NotificationSettingsRequest): NotificationSettingsResponse
}