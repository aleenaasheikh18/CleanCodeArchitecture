package com.chat.myapplication.core.data.settings.usecase

import com.chat.myapplication.core.data.settings.model.NotificationSettingsRequest
import com.chat.myapplication.core.data.settings.model.NotificationSettingsResponse
import com.chat.myapplication.core.data.settings.remote.SettingRepository
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ViewModelScoped
class UpdateNotificationSettingsUseCase @Inject constructor(
    private val settingRepository: SettingRepository
) {
    operator fun invoke(request: NotificationSettingsRequest): Flow<NotificationSettingsResponse> {
        return settingRepository.updateNotificationSettings(request)
    }
}
