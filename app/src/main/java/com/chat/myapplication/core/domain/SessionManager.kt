package com.chat.myapplication.core.domain

import com.chat.myapplication.utility.PreferenceManager
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(
    private val preferenceManager: PreferenceManager
) {

    private val _sessionExpired = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val sessionExpired: SharedFlow<Unit> = _sessionExpired.asSharedFlow()

    fun onSessionExpired() {
        preferenceManager.clearSession()
        _sessionExpired.tryEmit(Unit)
    }
}
