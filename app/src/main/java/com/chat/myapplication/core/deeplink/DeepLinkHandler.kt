package com.chat.myapplication.core.deeplink

import android.app.Activity
import android.content.Intent
import android.util.Log
import io.branch.referral.Branch
import io.branch.referral.BranchError
import io.branch.indexing.BranchUniversalObject
import io.branch.referral.util.LinkProperties
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeepLinkHandler @Inject constructor() {

    companion object {
        private const val TAG = "DeepLinkHandler"
        private const val DEEPLINK_PATH_KEY = "\$deeplink_path"
    }

    fun initBranchSession(
        activity: Activity,
        onDeepLinkReceived: (DeepLinkEvent) -> Unit
    ) {
        Branch.sessionBuilder(activity)
            .withCallback { branchUniversalObject, linkProperties, error ->
                handleBranchCallback(branchUniversalObject, linkProperties, error, onDeepLinkReceived)
            }
            .withData(activity.intent?.data)
            .init()
    }

    fun reInitBranchSession(
        activity: Activity,
        onDeepLinkReceived: (DeepLinkEvent) -> Unit
    ) {
        Branch.sessionBuilder(activity)
            .withCallback { branchUniversalObject, linkProperties, error ->
                handleBranchCallback(branchUniversalObject, linkProperties, error, onDeepLinkReceived)
            }
            .withData(activity.intent?.data)
            .reInit()
    }

    fun shouldReInitSession(intent: Intent): Boolean {
        return intent.getBooleanExtra("branch_force_new_session", false)
    }

    private fun handleBranchCallback(
        branchUniversalObject: BranchUniversalObject?,
        linkProperties: LinkProperties?,
        error: BranchError?,
        onDeepLinkReceived: (DeepLinkEvent) -> Unit
    ) {
        if (error != null) {
            Log.e(TAG, "Branch init failed: ${error.message}")
            onDeepLinkReceived(DeepLinkEvent.None)
            return
        }

        Log.i(TAG, "Branch init complete!")

        if (branchUniversalObject != null) {
            Log.i(TAG, "Title: ${branchUniversalObject.title}")
            Log.i(TAG, "CanonicalIdentifier: ${branchUniversalObject.canonicalIdentifier}")

            try {
                val metaData = branchUniversalObject.contentMetadata.convertToJson()
                Log.i(TAG, "Metadata: $metaData")

                val deepLinkPath = metaData.optString(DEEPLINK_PATH_KEY, "")
                Log.i(TAG, "DeepLink Path: $deepLinkPath")

                val event = parseDeepLinkPath(deepLinkPath)
                onDeepLinkReceived(event)
            } catch (e: Exception) {
                Log.e(TAG, "Error parsing deep link: ${e.message}")
                onDeepLinkReceived(DeepLinkEvent.None)
            }
        } else {
            onDeepLinkReceived(DeepLinkEvent.None)
        }

        linkProperties?.let {
            Log.i(TAG, "Channel: ${it.channel}")
            Log.i(TAG, "Control Params: ${it.controlParams}")
        }
    }

    private fun parseDeepLinkPath(deepLinkPath: String): DeepLinkEvent {
        if (deepLinkPath.isEmpty()) return DeepLinkEvent.None

        return when {
            deepLinkPath.contains(DeepLinkEvent.KEY_VERIFICATION_TOKEN) -> {
                val token = extractValue(DeepLinkEvent.KEY_VERIFICATION_TOKEN, deepLinkPath)
                if (token.isNotEmpty()) {
                    Log.i(TAG, "Verification Token: $token")
                    DeepLinkEvent.VerifyAccount(token)
                } else DeepLinkEvent.None
            }
            deepLinkPath.contains(DeepLinkEvent.KEY_RESET_PASSWORD_TOKEN) -> {
                val token = extractValue(DeepLinkEvent.KEY_RESET_PASSWORD_TOKEN, deepLinkPath)
                if (token.isNotEmpty()) {
                    Log.i(TAG, "Reset Password Token: $token")
                    DeepLinkEvent.ResetPassword(token)
                } else DeepLinkEvent.None
            }
            deepLinkPath.contains(DeepLinkEvent.KEY_REFERRAL_CODE) -> {
                val code = extractValue(DeepLinkEvent.KEY_REFERRAL_CODE, deepLinkPath)
                if (code.isNotEmpty()) {
                    Log.i(TAG, "Referral Code: $code")
                    DeepLinkEvent.ReferralCode(code)
                } else DeepLinkEvent.None
            }
            deepLinkPath.contains(DeepLinkEvent.KEY_INFLUENCER_JOB_ID) -> {
                val jobId = extractValue(DeepLinkEvent.KEY_INFLUENCER_JOB_ID, deepLinkPath)
                if (jobId.isNotEmpty()) {
                    Log.i(TAG, "Influencer Job ID: $jobId")
                    DeepLinkEvent.InfluencerJob(jobId)
                } else DeepLinkEvent.None
            }
            deepLinkPath.contains(DeepLinkEvent.KEY_CHANGE_EMAIL_TOKEN) -> {
                val token = extractValue(DeepLinkEvent.KEY_CHANGE_EMAIL_TOKEN, deepLinkPath)
                if (token.isNotEmpty()) {
                    Log.i(TAG, "Change Email Token: $token")
                    DeepLinkEvent.VerifyEmailChange(token)
                } else DeepLinkEvent.None
            }
            deepLinkPath.contains(DeepLinkEvent.KEY_LOGIN_TOKEN) -> {
                val token = extractValue(DeepLinkEvent.KEY_LOGIN_TOKEN, deepLinkPath)
                if (token.isNotEmpty()) {
                    Log.i(TAG, "Login Token: $token")
                    DeepLinkEvent.LoginToken(token)
                } else DeepLinkEvent.None
            }
            else -> {
                Log.i(TAG, "Unknown deep link path: $deepLinkPath")
                DeepLinkEvent.Unknown(deepLinkPath)
            }
        }
    }

    private fun extractValue(key: String, deepLinkPath: String): String {
        if (!deepLinkPath.contains(key)) return ""

        val parts = deepLinkPath.split("=")
        return if (parts.size > 1) parts[1] else ""
    }
}
