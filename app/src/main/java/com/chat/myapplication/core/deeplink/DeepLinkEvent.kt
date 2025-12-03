package com.chat.myapplication.core.deeplink

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
sealed class DeepLinkEvent : Parcelable {

    @Parcelize
    data class VerifyAccount(val token: String) : DeepLinkEvent()

    @Parcelize
    data class ResetPassword(val token: String) : DeepLinkEvent()

    @Parcelize
    data class ReferralCode(val code: String) : DeepLinkEvent()

    @Parcelize
    data class InfluencerJob(val jobId: String) : DeepLinkEvent()

    @Parcelize
    data class VerifyEmailChange(val token: String) : DeepLinkEvent()

    @Parcelize
    data class LoginToken(val token: String) : DeepLinkEvent()

    @Parcelize
    data class Unknown(val path: String) : DeepLinkEvent()

    @Parcelize
    data object None : DeepLinkEvent()

    companion object {
        const val KEY_VERIFICATION_TOKEN = "verification_token"
        const val KEY_RESET_PASSWORD_TOKEN = "reset_password_token"
        const val KEY_REFERRAL_CODE = "referral_code"
        const val KEY_INFLUENCER_JOB_ID = "influencer_job_id"
        const val KEY_CHANGE_EMAIL_TOKEN = "change_email_token"
        const val KEY_LOGIN_TOKEN = "login_token"
        const val EXTRA_DEEP_LINK_EVENT = "extra_deep_link_event"
    }
}
