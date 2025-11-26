package com.chat.myapplication.ui.fragments.settings.legalStuff

import com.chat.myapplication.R
import com.chat.myapplication.core.data.settings.SettingItem
import com.chat.myapplication.ui.fragments.settings.SettingType

class LegalStuffFactory {

    fun getLegalStuffList(): List<SettingItem> {
        return listOf(
            SettingItem(
                id = SettingType.TERMS_SERVICE,
                icon = R.drawable.ic_info,
                title = R.string.terms_service,
            ),
            SettingItem(
                id = SettingType.PRIVACY_POLICY,
                icon = R.drawable.ic_info,
                title = R.string.policy
            ),
        )
    }

}
