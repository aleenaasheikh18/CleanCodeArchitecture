package com.chat.myapplication.ui.fragments.settings.legalStuff

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chat.myapplication.base.BaseViewModel
import com.chat.myapplication.core.data.settings.SettingItem
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LegalStuffDataViewModel @Inject constructor(
    factory: LegalStuffFactory
) : BaseViewModel() {

    private val _settings = MutableLiveData<List<SettingItem>>()
    val settings: LiveData<List<SettingItem>> get() = _settings

    init {
        _settings.value = factory.getLegalStuffList()
    }
}
