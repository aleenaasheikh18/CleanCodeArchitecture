package com.chat.myapplication.core.data.settings

import androidx.annotation.StringRes
import com.chat.myapplication.ui.fragments.settings.SettingType
import com.chat.myapplication.utility.empty

data class SettingItem(
    val id: SettingType = SettingType.NONE,
    val icon: Int,
    @param:StringRes val title: Int,
    val description: String = String.empty,
    val isVerified: Boolean = false
)
