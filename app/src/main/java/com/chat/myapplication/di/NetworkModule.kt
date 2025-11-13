package com.chat.myapplication.di


import com.chat.myapplication.core.data.auth.service.AuthApiService
import com.chat.myapplication.core.domain.NetworkProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    fun provideAuthService(networkProvider: NetworkProvider) =
        networkProvider.create(AuthApiService::class.java)

}
