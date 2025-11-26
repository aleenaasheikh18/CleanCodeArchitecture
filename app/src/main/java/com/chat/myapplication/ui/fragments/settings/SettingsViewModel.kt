package com.chat.myapplication.ui.fragments.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chat.myapplication.base.BaseViewModel
import com.chat.myapplication.core.data.settings.SettingItem
import com.chat.myapplication.core.data.settings.SettingsFactory
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    factory: SettingsFactory
) : BaseViewModel() {

    private val _settings = MutableLiveData<List<SettingItem>>()
    val settings: LiveData<List<SettingItem>> get() = _settings

    init {
        _settings.value = factory.getSettingsList()
    }
}
