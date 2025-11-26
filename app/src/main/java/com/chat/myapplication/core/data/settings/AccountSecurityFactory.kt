package com.chat.myapplication.core.data.settings

import android.content.Context
import com.chat.myapplication.R
import com.chat.myapplication.ui.fragments.settings.SettingType
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AccountSecurityFactory@Inject constructor(@param:ApplicationContext private val context: Context) {

    fun getAccountSecurity(): List<SettingItem> {
        return listOf(
            SettingItem(
                id = SettingType.VERIFIED_MOBILE,
                icon = R.drawable.ic_verified,
                title = R.string.title_verified_mobile_number,
            ),
            SettingItem(
                id = SettingType.VERIFIED_EMAIL,
                icon = R.drawable.ic_verified,
                title = R.string.title_verified_email_address
            ),
            SettingItem(
                id = SettingType.FINGERPRINT_AUTH,
                icon = R.drawable.ic_fingerprint,
                title = R.string.title_finger_print_authentication
            ),
            SettingItem(
                id = SettingType.PASSKEY,
                icon = R.drawable.ic_pass_key,
                title = R.string.title_passkey,
                description = context.getString(R.string.description_passkey)
            ),
            SettingItem(
                id = SettingType.PASSWORD,
                icon = R.drawable.ic_password,
                title = R.string.title_password,
                description = context.getString(R.string.description_password)
            )
        )
    }

}
