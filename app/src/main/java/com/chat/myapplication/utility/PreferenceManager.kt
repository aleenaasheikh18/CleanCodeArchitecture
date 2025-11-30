package com.chat.myapplication.utility

import android.content.Context
import android.content.SharedPreferences
import com.chat.myapplication.core.data.auth.model.SignInData
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferenceManager @Inject constructor(@ApplicationContext private val context: Context) {

    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE)
    }

    companion object {
        private const val PREFS_FILE = "foodchoo_prefs"

        // Auth Keys
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_AUTH_TOKEN = "auth_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_ADMIN_ID = "admin_id"

        // User Profile Keys
        private const val KEY_FIRST_NAME = "first_name"
        private const val KEY_LAST_NAME = "last_name"
        private const val KEY_EMAIL = "email"
        private const val KEY_PHONE_NUMBER = "phone_number"
        private const val KEY_PROFILE_IMAGE = "profile_image"
        private const val KEY_THUMBNAIL = "thumbnail"
        private const val KEY_CUSTOMER_TYPE = "customer_type"
        private const val KEY_IS_ON_BOARDED = "is_on_boarded"

        // Stripe Keys
        private const val KEY_STRIPE_ID = "stripe_id"
        private const val KEY_IS_STRIPE_CONNECTED = "is_stripe_connected"

        // Location/Currency Keys
        private const val KEY_COUNTRY = "country"
        private const val KEY_CURRENCY = "currency"
        private const val KEY_CURRENCY_ICON = "currency_icon"

        // Referral Keys
        private const val KEY_REFERRAL_URL = "referral_url"
        private const val KEY_INVITE_CODE = "invite_code"
        private const val KEY_SHARE_BONUS = "share_bonus"

        // Notification Keys
        private const val KEY_IS_NOTIFICATION = "is_notification"
        private const val KEY_IS_PRODUCT_ANNOUNCEMENTS = "is_product_announcements"
        private const val KEY_I_AM_HUNGRY = "i_am_hungry"
        private const val KEY_MESSAGE = "message"
        private const val KEY_RECEIPT = "receipt"

        // Persona Keys
        private const val KEY_PERSONA_ATTEMPTED_COUNT = "persona_attempted_count"
        private const val KEY_LAST_PERSONA_ATTEMPTED = "last_persona_attempted"

        // Delivery Address Keys
        private const val KEY_EATER_ADDRESS = "eater_address"
        private const val KEY_EATER_CITY = "eater_city"
        private const val KEY_EATER_STATE = "eater_state"
        private const val KEY_EATER_COUNTRY = "eater_country"

        // Deep Link Keys
        private const val KEY_INFLUENCER_JOB_ID = "influencer_job_id"
    }

    // region Auth Properties
    var isLoggedIn: Boolean
        get() = sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
        set(value) = sharedPreferences.edit().putBoolean(KEY_IS_LOGGED_IN, value).apply()

    var authToken: String
        get() = sharedPreferences.getString(KEY_AUTH_TOKEN, "").orEmpty()
        set(value) = sharedPreferences.edit().putString(KEY_AUTH_TOKEN, value).apply()

    var userId: String
        get() = sharedPreferences.getString(KEY_USER_ID, "").orEmpty()
        set(value) = sharedPreferences.edit().putString(KEY_USER_ID, value).apply()

    var adminId: String
        get() = sharedPreferences.getString(KEY_ADMIN_ID, "").orEmpty()
        set(value) = sharedPreferences.edit().putString(KEY_ADMIN_ID, value).apply()
    // endregion

    // region User Profile Properties
    var firstName: String
        get() = sharedPreferences.getString(KEY_FIRST_NAME, "").orEmpty()
        set(value) = sharedPreferences.edit().putString(KEY_FIRST_NAME, value).apply()

    var lastName: String
        get() = sharedPreferences.getString(KEY_LAST_NAME, "").orEmpty()
        set(value) = sharedPreferences.edit().putString(KEY_LAST_NAME, value).apply()

    var email: String
        get() = sharedPreferences.getString(KEY_EMAIL, "").orEmpty()
        set(value) = sharedPreferences.edit().putString(KEY_EMAIL, value).apply()

    var phoneNumber: String
        get() = sharedPreferences.getString(KEY_PHONE_NUMBER, "").orEmpty()
        set(value) = sharedPreferences.edit().putString(KEY_PHONE_NUMBER, value).apply()

    var profileImage: String
        get() = sharedPreferences.getString(KEY_PROFILE_IMAGE, "").orEmpty()
        set(value) = sharedPreferences.edit().putString(KEY_PROFILE_IMAGE, value).apply()

    var thumbnail: String
        get() = sharedPreferences.getString(KEY_THUMBNAIL, "").orEmpty()
        set(value) = sharedPreferences.edit().putString(KEY_THUMBNAIL, value).apply()

    var customerType: String
        get() = sharedPreferences.getString(KEY_CUSTOMER_TYPE, "").orEmpty()
        set(value) = sharedPreferences.edit().putString(KEY_CUSTOMER_TYPE, value).apply()

    var isOnBoarded: Boolean
        get() = sharedPreferences.getBoolean(KEY_IS_ON_BOARDED, false)
        set(value) = sharedPreferences.edit().putBoolean(KEY_IS_ON_BOARDED, value).apply()
    // endregion

    // region Stripe Properties
    var stripeId: String
        get() = sharedPreferences.getString(KEY_STRIPE_ID, "").orEmpty()
        set(value) = sharedPreferences.edit().putString(KEY_STRIPE_ID, value).apply()

    var isStripeConnected: Boolean
        get() = sharedPreferences.getBoolean(KEY_IS_STRIPE_CONNECTED, false)
        set(value) = sharedPreferences.edit().putBoolean(KEY_IS_STRIPE_CONNECTED, value).apply()
    // endregion

    // region Location/Currency Properties
    var country: String
        get() = sharedPreferences.getString(KEY_COUNTRY, "").orEmpty()
        set(value) = sharedPreferences.edit().putString(KEY_COUNTRY, value).apply()

    var currency: String
        get() = sharedPreferences.getString(KEY_CURRENCY, "").orEmpty()
        set(value) = sharedPreferences.edit().putString(KEY_CURRENCY, value).apply()

    var currencyIcon: String
        get() = sharedPreferences.getString(KEY_CURRENCY_ICON, "").orEmpty()
        set(value) = sharedPreferences.edit().putString(KEY_CURRENCY_ICON, value).apply()
    // endregion

    // region Referral Properties
    var referralUrl: String
        get() = sharedPreferences.getString(KEY_REFERRAL_URL, "").orEmpty()
        set(value) = sharedPreferences.edit().putString(KEY_REFERRAL_URL, value).apply()

    var inviteCode: String
        get() = sharedPreferences.getString(KEY_INVITE_CODE, "").orEmpty()
        set(value) = sharedPreferences.edit().putString(KEY_INVITE_CODE, value).apply()

    var shareBonus: Float
        get() = sharedPreferences.getFloat(KEY_SHARE_BONUS, 0f)
        set(value) = sharedPreferences.edit().putFloat(KEY_SHARE_BONUS, value).apply()
    // endregion

    // region Notification Properties
    var isNotificationOn: Boolean
        get() = sharedPreferences.getBoolean(KEY_IS_NOTIFICATION, false)
        set(value) = sharedPreferences.edit().putBoolean(KEY_IS_NOTIFICATION, value).apply()

    var isProductAnnouncementOn: Boolean
        get() = sharedPreferences.getBoolean(KEY_IS_PRODUCT_ANNOUNCEMENTS, false)
        set(value) = sharedPreferences.edit().putBoolean(KEY_IS_PRODUCT_ANNOUNCEMENTS, value).apply()

    var iAmHungry: String
        get() = sharedPreferences.getString(KEY_I_AM_HUNGRY, "").orEmpty()
        set(value) = sharedPreferences.edit().putString(KEY_I_AM_HUNGRY, value).apply()

    var message: String
        get() = sharedPreferences.getString(KEY_MESSAGE, "").orEmpty()
        set(value) = sharedPreferences.edit().putString(KEY_MESSAGE, value).apply()

    var receipt: String
        get() = sharedPreferences.getString(KEY_RECEIPT, "").orEmpty()
        set(value) = sharedPreferences.edit().putString(KEY_RECEIPT, value).apply()
    // endregion

    // region Persona Properties
    var personaAttemptedCount: Int
        get() = sharedPreferences.getInt(KEY_PERSONA_ATTEMPTED_COUNT, 0)
        set(value) = sharedPreferences.edit().putInt(KEY_PERSONA_ATTEMPTED_COUNT, value).apply()

    var lastPersonaAttempted: String
        get() = sharedPreferences.getString(KEY_LAST_PERSONA_ATTEMPTED, "").orEmpty()
        set(value) = sharedPreferences.edit().putString(KEY_LAST_PERSONA_ATTEMPTED, value).apply()
    // endregion

    // region Delivery Address Properties
    var eaterAddress: String
        get() = sharedPreferences.getString(KEY_EATER_ADDRESS, "").orEmpty()
        set(value) = sharedPreferences.edit().putString(KEY_EATER_ADDRESS, value).apply()

    var eaterCity: String
        get() = sharedPreferences.getString(KEY_EATER_CITY, "").orEmpty()
        set(value) = sharedPreferences.edit().putString(KEY_EATER_CITY, value).apply()

    var eaterState: String
        get() = sharedPreferences.getString(KEY_EATER_STATE, "").orEmpty()
        set(value) = sharedPreferences.edit().putString(KEY_EATER_STATE, value).apply()

    var eaterCountry: String
        get() = sharedPreferences.getString(KEY_EATER_COUNTRY, "").orEmpty()
        set(value) = sharedPreferences.edit().putString(KEY_EATER_COUNTRY, value).apply()
    // endregion

    // region Deep Link Properties
    var influencerJobId: String
        get() = sharedPreferences.getString(KEY_INFLUENCER_JOB_ID, "").orEmpty()
        set(value) = sharedPreferences.edit().putString(KEY_INFLUENCER_JOB_ID, value).apply()
    // endregion

    // region Login/Logout Methods
    fun handleDataAfterLogin(loginData: SignInData) {
        val customer = loginData.customer

        // Auth data
        isLoggedIn = true
        authToken = loginData.token.orEmpty()
        adminId = loginData.adminId.orEmpty()

        // Customer data
        customer?.let { c ->
            userId = c.id.orEmpty()
            firstName = c.firstName.orEmpty()
            lastName = c.lastName.orEmpty()
            email = c.email.orEmpty()
            phoneNumber = c.phoneNo.orEmpty()
            profileImage = c.profileImage.orEmpty()
            thumbnail = c.thumbnail.orEmpty()
            customerType = c.customerType.orEmpty()
            isOnBoarded = c.isOnBoarded

            // Stripe
            stripeId = c.stripeId.orEmpty()
            isStripeConnected = c.stripeCompleted

            // Location/Currency
            country = c.country.orEmpty()
            currency = c.currency.orEmpty()
            currencyIcon = c.currencyIcon.orEmpty()

            // Referral
            referralUrl = c.referralUrl.orEmpty()
            inviteCode = c.referralLink.orEmpty()
            shareBonus = c.referralBonus

            // Notifications
            isNotificationOn = c.notification
            isProductAnnouncementOn = c.productAnnouncement
            iAmHungry = c.iAmHungry.orEmpty()
            message = c.newMessage.orEmpty()
            receipt = c.receipt.orEmpty()

            // Persona
            personaAttemptedCount = c.personaAttemptedCount
            lastPersonaAttempted = c.lastPersonaAttempted.orEmpty()

            // Delivery Address
            c.deliveryAddress?.let { addr ->
                eaterAddress = addr.address.orEmpty()
                eaterCity = addr.city.orEmpty()
                eaterState = addr.state.orEmpty()
                eaterCountry = addr.country.orEmpty()
            }
        }
    }

    fun clearSession() {
        sharedPreferences.edit().clear().apply()
    }

    fun clearAuthToken() {
        sharedPreferences.edit().remove(KEY_AUTH_TOKEN).apply()
        isLoggedIn = false
    }
    // endregion
}
