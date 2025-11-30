package com.chat.myapplication.di

import android.content.Context
import com.chat.myapplication.components.DialogManager
import com.chat.myapplication.core.data.settings.AccountSecurityFactory
import com.chat.myapplication.core.data.settings.SettingsFactory
import com.chat.myapplication.core.exception.GsonProvider
import com.chat.myapplication.ui.fragments.settings.legalStuff.LegalStuffFactory
import com.chat.myapplication.core.domain.AuthInterceptor
import com.chat.myapplication.utility.NetworkConstants
import com.chat.myapplication.utility.PreferenceManager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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
    fun providePreferenceManager(@ApplicationContext context: Context) = PreferenceManager(context)

    @Provides
    fun provideAccountSecurityFactory(
        @ApplicationContext context: Context,
        preferenceManager: PreferenceManager
    ): AccountSecurityFactory {
        return AccountSecurityFactory(context,preferenceManager)
    }

    @Provides
    fun provideLegalStuffFactory(): LegalStuffFactory = LegalStuffFactory()

    @Provides
    fun provideSettingFactory(): SettingsFactory = SettingsFactory()


    @Provides
    @Singleton
    fun provideAuthInterceptor(preferenceManager: PreferenceManager): AuthInterceptor {
        return AuthInterceptor(preferenceManager)
    }

    @Provides
    @Singleton
    @PublicHttpClient
    fun providePublicHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
        val okHttpBuilder: OkHttpClient.Builder = OkHttpClient.Builder()
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        okHttpBuilder
            .addInterceptor(authInterceptor)
            .addNetworkInterceptor(loggingInterceptor)
            .connectTimeout(NetworkConstants.CONNECT_TIME_OUT, TimeUnit.SECONDS)
            .readTimeout(NetworkConstants.READ_TIME_OUT, TimeUnit.SECONDS)
        return okHttpBuilder.build()
    }

}



