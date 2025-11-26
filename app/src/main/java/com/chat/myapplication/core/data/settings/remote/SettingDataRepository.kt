package com.chat.myapplication.core.data.settings.remote

import com.chat.myapplication.core.data.settings.model.NotificationSettingsRequest
import com.chat.myapplication.core.data.settings.model.NotificationSettingsResponse
import com.chat.myapplication.core.data.settings.model.UtilityResponse
import com.chat.myapplication.core.data.settings.service.UtilityApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SettingDataRepository @Inject constructor(private val utilityApiService: UtilityApiService) :
    SettingRepository {

    override fun getPolicyDocuments(): Flow<UtilityResponse> {
        return flow { emit(utilityApiService.getPolicyDocuments()) }
    }

    override fun updateNotificationSettings(request: NotificationSettingsRequest): Flow<NotificationSettingsResponse> {
        return flow { emit(utilityApiService.updateNotificationSettings(request)) }
    }
}
