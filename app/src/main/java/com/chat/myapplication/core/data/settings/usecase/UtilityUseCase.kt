package com.chat.myapplication.core.data.settings.usecase

import com.chat.myapplication.core.data.settings.model.UtilityResponse
import com.chat.myapplication.core.data.settings.remote.SettingRepository
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ViewModelScoped
class UtilityUseCase @Inject constructor(val settingRepository: SettingRepository) {

    operator fun invoke(): Flow<UtilityResponse> {
        return settingRepository.getPolicyDocuments()
    }
}