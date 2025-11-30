package com.chat.myapplication.ui.fragments.settings

import androidx.lifecycle.viewModelScope
import com.chat.myapplication.base.BaseViewModel
import com.chat.myapplication.core.data.settings.model.NotificationSettingsRequest
import com.chat.myapplication.core.data.settings.model.NotificationSettingsResponse
import com.chat.myapplication.core.data.settings.usecase.UpdateNotificationSettingsUseCase
import com.chat.myapplication.core.domain.State
import com.chat.myapplication.core.domain.onApiError
import com.chat.myapplication.core.domain.onApiSuccess
import com.chat.myapplication.di.IODispatcher
import com.chat.myapplication.utility.collectAsResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

@HiltViewModel
class NotificationSettingsViewModel @Inject constructor(
    private val updateNotificationSettingsUseCase: UpdateNotificationSettingsUseCase,
    @param:IODispatcher private val ioDispatcher: CoroutineDispatcher
) : BaseViewModel() {

    private val _updateSettingsResponse = MutableStateFlow<State<NotificationSettingsResponse>>(State.idle())
    val updateSettingsResponse: StateFlow<State<NotificationSettingsResponse>> = _updateSettingsResponse

    fun updateNotificationSettings(
        notification: Boolean,
        iAmHungry: String,
        newMessage: String,
        receipt: String,
        productAnnouncement: Boolean
    ) {
        val request = NotificationSettingsRequest(
            notification = notification,
            iAmHungry = iAmHungry,
            newMessage = newMessage,
            receipt = receipt,
            productAnnouncement = productAnnouncement
        )

        updateNotificationSettingsUseCase(request)
            .collectAsResult()
            .flowOn(ioDispatcher)
            .onApiSuccess { response ->
                if (response.status) {
                    _updateSettingsResponse.value = State.Success(response)
                } else {
                    _updateSettingsResponse.value = State.Error(response.message)
                }
            }
            .onApiError { error ->
                _updateSettingsResponse.value = State.Error(error.errorMessage)
            }
            .onStart {
                _updateSettingsResponse.value = State.Loading()
            }
            .launchIn(viewModelScope)
    }
}
