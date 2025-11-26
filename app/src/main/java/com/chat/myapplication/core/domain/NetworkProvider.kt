package com.chat.myapplication.core.domain

import com.chat.myapplication.di.PublicHttpClient
import com.chat.myapplication.utility.AppConstants.BASE_URL
import com.google.gson.Gson
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkProvider @Inject constructor(
    gson: Gson,
    @PublicHttpClient private val okHttpClient: OkHttpClient,
) {

    private var retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    fun <T> create(serviceClass: Class<T>): T = retrofit.create(serviceClass)
}