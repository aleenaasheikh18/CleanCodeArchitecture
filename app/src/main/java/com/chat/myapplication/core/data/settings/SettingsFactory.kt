package com.chat.myapplication.core.data.settings

import com.chat.myapplication.R
import com.chat.myapplication.ui.fragments.settings.SettingType

class SettingsFactory {

    fun getSettingsList(): List<SettingItem> {
        return listOf(
            SettingItem(
                id = SettingType.PAYMENT,
                icon = R.drawable.ic_payments,
                title = R.string.title_payment,
            ),
            SettingItem(
                id = SettingType.NOTIFICATIONS,
                icon = R.drawable.ic_notification,
                title = R.string.title_notifications
            ),
            SettingItem(
                id = SettingType.ACCOUNT_SECURITY,
                icon = R.drawable.ic_account_security,
                title = R.string.title_account_security
            ),
            SettingItem(
                id = SettingType.LEGAL_STUFF,
                icon = R.drawable.ic_legal_stuff,
                title = R.string.title_legal_stuff
            )
        )
    }

}
