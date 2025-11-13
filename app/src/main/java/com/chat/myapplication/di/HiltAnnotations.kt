package com.chat.myapplication.di

import javax.inject.Qualifier


@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class DefaultDispatcher

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class IODispatcher

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class MainDispatcher

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class UnconfinedDispatcher

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class GoogleSignInOptionForDriveAppData

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class CoroutineScopeWithIODispatcher

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class CoroutineScopeWithDefaultDispatcher

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class TokenRefreshClient

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class PublicClient
@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class PublicHttpClient
