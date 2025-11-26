package com.chat.myapplication.core.data.settings.remote

import com.chat.myapplication.core.data.settings.model.NotificationSettingsRequest
import com.chat.myapplication.core.data.settings.model.NotificationSettingsResponse
import com.chat.myapplication.core.data.settings.model.UtilityResponse
import kotlinx.coroutines.flow.Flow

interface SettingRepository {

    fun getPolicyDocuments(): Flow<UtilityResponse>

    fun updateNotificationSettings(request: NotificationSettingsRequest): Flow<NotificationSettingsResponse>
}