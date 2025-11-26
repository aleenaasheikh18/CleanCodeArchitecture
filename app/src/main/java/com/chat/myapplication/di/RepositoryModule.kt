package com.chat.myapplication.di

import com.chat.myapplication.core.data.auth.remote.AuthDataRepository
import com.chat.myapplication.core.data.auth.remote.AuthRepository
import com.chat.myapplication.core.data.settings.remote.SettingDataRepository
import com.chat.myapplication.core.data.settings.remote.SettingRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun provideAuthRepository(authDataRepository: AuthDataRepository): AuthRepository

    @Binds
    abstract fun provideSettingRepository(settingDataRepository: SettingDataRepository): SettingRepository

}