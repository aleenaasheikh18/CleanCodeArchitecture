package com.chat.myapplication.di


import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DispatchersModule {

    @DefaultDispatcher
    @Provides
    @Singleton
    fun provideDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

    @IODispatcher
    @Provides
    @Singleton
    fun provideIODispatcher(): CoroutineDispatcher = Dispatchers.IO

    @MainDispatcher
    @Provides
    @Singleton
    fun provideMainDispatcher(): CoroutineDispatcher = Dispatchers.Main

    @UnconfinedDispatcher
    @Provides
    @Singleton
    fun provideUnconfinedDispatcher(): CoroutineDispatcher = Dispatchers.Unconfined

    @CoroutineScopeWithIODispatcher
    @Provides
    @Singleton
    fun provideCoroutineScopeWithIODispatcher(@IODispatcher ioDispatcher: CoroutineDispatcher) =
        CoroutineScope(ioDispatcher)

    @CoroutineScopeWithDefaultDispatcher
    @Provides
    @Singleton
    fun provideCoroutineScopeWithDefaultDispatcher(@DefaultDispatcher defaultDispatcher: CoroutineDispatcher) =
        CoroutineScope(defaultDispatcher)

}
