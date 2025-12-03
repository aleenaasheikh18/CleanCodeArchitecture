package com.chat.myapplication.core.domain.google

import android.app.Activity
import android.content.Intent
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.tasks.Task
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoogleSignInManager @Inject constructor() {

    companion object {
        const val RC_SIGN_IN = 9001
        private const val TAG = "GoogleSignInManager"
    }

    fun getGoogleSignInClient(activity: Activity): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestProfile()
            .requestId()
            .build()

        return GoogleSignIn.getClient(activity, gso)
    }

    /**
     * Check if there's a previously signed-in Google account
     */
    fun hasLastSignedInAccount(activity: Activity): Boolean {
        val account = GoogleSignIn.getLastSignedInAccount(activity)
        return account != null
    }

    /**
     * Sign out from any previously signed-in Google account before initiating new sign-in
     * This ensures users can choose which account to use
     */
    fun signOutBeforeSignIn(activity: Activity, onComplete: () -> Unit) {
        if (hasLastSignedInAccount(activity)) {
            val client = getGoogleSignInClient(activity)
            client.signOut().addOnCompleteListener {
                onComplete()
            }
        } else {
            onComplete()
        }
    }

    fun handleSignInResult(data: Intent?): Result<GoogleSignInAccount> {
        val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
        return try {
            val account = task.getResult(ApiException::class.java)
            Result.success(account)
        } catch (e: ApiException) {
            Log.e(TAG, "Google Sign-In failed with status code: ${e.statusCode}")
            when (e.statusCode) {
                12501, // GoogleSignInStatusCodes.SIGN_IN_CANCELLED
                CommonStatusCodes.CANCELED -> {
                    // User explicitly canceled the sign-in flow - this is not an error
                    Result.failure(GoogleSignInCancelledException("User cancelled sign-in"))
                }
                12502 -> { // GoogleSignInStatusCodes.SIGN_IN_CURRENTLY_IN_PROGRESS
                    Result.failure(Exception("Sign-in already in progress"))
                }
                10 -> { // CommonStatusCodes.DEVELOPER_ERROR (value is 10)
                    // This usually means SHA-1 fingerprint mismatch or wrong package name
                    Result.failure(Exception("Configuration error: Please check SHA-1 fingerprint and package name in Google Cloud Console"))
                }
                else -> {
                    Result.failure(Exception("Google sign in failed: ${e.statusMessage ?: e.message}"))
                }
            }
        }
    }

    fun signOut(activity: Activity, onComplete: () -> Unit) {
        val client = getGoogleSignInClient(activity)
        client.signOut().addOnCompleteListener {
            onComplete()
        }
    }
}

/**
 * Exception thrown when user explicitly cancels Google Sign-In
 */
class GoogleSignInCancelledException(message: String) : Exception(message)
