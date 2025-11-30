package com.chat.myapplication.core.domain

import com.chat.myapplication.utility.NetworkConstants
import com.chat.myapplication.utility.PreferenceManager
import okhttp3.Interceptor
import okhttp3.Response
import java.net.HttpURLConnection
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val preferenceManager: PreferenceManager,
    private val sessionManager: SessionManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val token = preferenceManager.authToken
        if (token.isEmpty()) {
            return chain.proceed(originalRequest)
        }

        val authenticatedRequest = originalRequest.newBuilder()
            .header(
                NetworkConstants.HEADER_KEY_CONTENT_AUTHORIZATION,
                "${NetworkConstants.HEADER_AUTHORIZATION_TYPE}$token"
            )
            .build()

        val response = chain.proceed(authenticatedRequest)

        if (response.code == HttpURLConnection.HTTP_UNAUTHORIZED) {
            sessionManager.onSessionExpired()
        }

        return response
    }
}
