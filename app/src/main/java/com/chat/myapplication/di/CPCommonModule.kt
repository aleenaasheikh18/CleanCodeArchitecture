package com.chat.myapplication.di

import com.chat.myapplication.components.DialogManager
import com.chat.myapplication.core.exception.GsonProvider
import com.chat.myapplication.utility.NetworkConstants
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit


@Module
@InstallIn(SingletonComponent::class)
object CPCommonModule {

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder().setDateFormat(GsonProvider.ISO_8601_DATE_FORMAT).create()
    }

    @Provides
    @Singleton
    fun provideGsonProvider(): GsonProvider = GsonProvider()

    @Provides
    @Singleton
    fun provideDialogManager() = DialogManager()

    @Provides
    @Singleton
    @PublicHttpClient
    fun providePublicHttpClient() : OkHttpClient {
        val okHttpBuilder: OkHttpClient.Builder = OkHttpClient.Builder()
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        okHttpBuilder.addNetworkInterceptor(loggingInterceptor)
            .connectTimeout(NetworkConstants.CONNECT_TIME_OUT, TimeUnit.SECONDS)
            .readTimeout(NetworkConstants.READ_TIME_OUT, TimeUnit.SECONDS)
        return okHttpBuilder.build()
    }

}



