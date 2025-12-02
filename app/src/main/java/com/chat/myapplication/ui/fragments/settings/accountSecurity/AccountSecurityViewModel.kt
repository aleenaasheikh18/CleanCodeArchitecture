package com.chat.myapplication.ui.fragments.settings.accountSecurity

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.chat.myapplication.base.BaseViewModel
import com.chat.myapplication.core.data.auth.model.PasskeyRegisterData
import com.chat.myapplication.core.data.auth.usecase.RegisterPasskeyUseCase
import com.chat.myapplication.core.data.settings.AccountSecurityFactory
import com.chat.myapplication.core.data.settings.SettingItem
import com.chat.myapplication.core.domain.passkey.PasskeyException
import com.chat.myapplication.core.domain.passkey.PasskeyManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountSecurityViewModel @Inject constructor(
    factory: AccountSecurityFactory,
    private val passkeyManager: PasskeyManager,
    private val registerPasskeyUseCase: RegisterPasskeyUseCase
) : BaseViewModel() {

    private val _settings = MutableLiveData<List<SettingItem>>()
    val settings: LiveData<List<SettingItem>> get() = _settings

    private val _passkeyRegisterState = MutableSharedFlow<PasskeyRegisterState>()
    val passkeyRegisterState: SharedFlow<PasskeyRegisterState> = _passkeyRegisterState.asSharedFlow()

    init {
        _settings.value = factory.getAccountSecurity()
    }

    fun registerPasskey(activity: Activity) {
        viewModelScope.launch {
            _passkeyRegisterState.emit(PasskeyRegisterState.Loading)

            passkeyManager.registerPasskey(activity)
                .onSuccess { registerRequest ->
                    registerPasskeyUseCase(registerRequest)
                        .catch { e ->
                            _passkeyRegisterState.emit(
                                PasskeyRegisterState.Error(e.message ?: "Registration failed")
                            )
                        }
                        .collect { response ->
                            if (response.status && response.data != null) {
                                _passkeyRegisterState.emit(PasskeyRegisterState.Success(response.data))
                            } else {
                                _passkeyRegisterState.emit(
                                    PasskeyRegisterState.Error(response.message.ifEmpty { "Registration failed" })
                                )
                            }
                        }
                }
                .onFailure { exception ->
                    val errorMessage = when (exception) {
                        is PasskeyException.UserCancelled -> null
                        is PasskeyException -> exception.message
                        else -> exception.message ?: "Registration failed"
                    }
                    if (errorMessage != null) {
                        _passkeyRegisterState.emit(PasskeyRegisterState.Error(errorMessage))
                    } else {
                        _passkeyRegisterState.emit(PasskeyRegisterState.Idle)
                    }
                }
        }
    }
}

sealed class PasskeyRegisterState {
    data object Idle : PasskeyRegisterState()
    data object Loading : PasskeyRegisterState()
    data class Success(val data: PasskeyRegisterData) : PasskeyRegisterState()
    data class Error(val message: String) : PasskeyRegisterState()
}
