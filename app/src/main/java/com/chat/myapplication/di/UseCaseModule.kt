package com.chat.myapplication.di

import com.chat.myapplication.core.data.auth.remote.AuthRepository
import com.chat.myapplication.core.data.auth.usecase.SignInUseCase
import com.chat.myapplication.core.data.settings.remote.SettingRepository
import com.chat.myapplication.core.data.settings.usecase.UpdateNotificationSettingsUseCase
import com.chat.myapplication.core.data.settings.usecase.UtilityUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideSignInUseCase(
        authRepository: AuthRepository,
    ) = SignInUseCase(authRepository)

    @Provides
    @Singleton
    fun provideSettingUseCase(
        settingRepository: SettingRepository,
    ) = UtilityUseCase(settingRepository)

    @Provides
    @Singleton
    fun provideUpdateNotificationSettingsUseCase(
        settingRepository: SettingRepository,
    ) = UpdateNotificationSettingsUseCase(settingRepository)
}
