package com.chat.myapplication.utility

import android.content.Context
import com.chat.myapplication.utility.PreferenceManager.Companion.COMERAPAY_PREFERENCES_FILE
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class PreferenceManager @Inject constructor(@ApplicationContext private val context: Context) {


    private val sharedPreferences by lazy {
        context.getSharedPreferences(COMERAPAY_PREFERENCES_FILE, Context.MODE_PRIVATE)
    }

    companion object {
        private const val COMERAPAY_PREFERENCES_FILE = "comera_pay_prefs_file"
        private const val I_AM_HUNGRY = "I_AM_HUNGRY"
        private const val RECEIPT = "RECEIPT"
        private const val MESSAGE = "MESSAGE"
        private const val IS_NOTIFICATION = "IS_NOTIFICATION"
        private const val IS_PRODUCT_ANNOUNCEMENTS = "IS_PRODUCT_ANNOUNCEMENTS"
    }

    var iAmHungry: String
        get() = sharedPreferences.getString(I_AM_HUNGRY, "").orEmpty()
        set(value) = sharedPreferences.edit().putString(I_AM_HUNGRY, value).apply()

    var receipt: String
        get() = sharedPreferences.getString(RECEIPT, "").orEmpty()
        set(value) = sharedPreferences.edit().putString(RECEIPT, value).apply()

    var message: String
        get() = sharedPreferences.getString(MESSAGE, "").orEmpty()
        set(value) = sharedPreferences.edit().putString(MESSAGE, value).apply()

    var isNotificationOn: Boolean
        get() = sharedPreferences.getBoolean(IS_NOTIFICATION, false)
        set(value) = sharedPreferences.edit().putBoolean(IS_NOTIFICATION, value).apply()

    var isProductAnnouncementOn: Boolean
        get() = sharedPreferences.getBoolean(IS_PRODUCT_ANNOUNCEMENTS, false)
        set(value) = sharedPreferences.edit().putBoolean(IS_PRODUCT_ANNOUNCEMENTS, value).apply()
}