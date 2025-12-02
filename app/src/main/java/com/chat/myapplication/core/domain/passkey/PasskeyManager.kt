package com.chat.myapplication.core.domain.passkey

import android.app.Activity
import android.os.Build
import android.util.Base64
import android.util.Log
import androidx.credentials.CreatePublicKeyCredentialRequest
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetPublicKeyCredentialOption
import androidx.credentials.PublicKeyCredential
import androidx.credentials.exceptions.CreateCredentialCancellationException
import androidx.credentials.exceptions.CreateCredentialException
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import com.chat.myapplication.core.data.auth.model.AssertionResponseData
import com.chat.myapplication.core.data.auth.model.PasskeyAssertionCredential
import com.chat.myapplication.core.data.auth.model.PasskeyCredential
import com.chat.myapplication.core.data.auth.model.PasskeyLoginRequest
import com.chat.myapplication.core.data.auth.model.PasskeyRegisterRequest
import com.chat.myapplication.core.data.auth.model.RegistrationResponseData
import com.chat.myapplication.utility.PreferenceManager
import org.json.JSONObject
import java.security.SecureRandom
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PasskeyManager @Inject constructor(
    private val preferenceManager: PreferenceManager
) {
    companion object {
        private const val TAG = "PasskeyManager"
        const val RP_ID = "eater.foodchoo.com"
        const val RP_NAME = "FoodChoo Eater"
        const val RP_ORIGIN = "https://eater.foodchoo.com"
        private const val DEVICE_TYPE = "android"
    }

    private fun generateChallenge(): String {
        val bytes = ByteArray(32)
        SecureRandom().nextBytes(bytes)
        return Base64.encodeToString(bytes, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
    }

    private fun getDeviceName(): String {
        val manufacturer = Build.MANUFACTURER
        val model = Build.MODEL
        return if (model.startsWith(manufacturer)) {
            model.capitalize()
        } else {
            "${manufacturer.capitalize()} $model"
        }
    }

    private fun buildRegistrationRequestJson(
        challenge: String,
        userId: String,
        userName: String
    ): String {
        val userIdBase64 = Base64.encodeToString(
            userId.toByteArray(Charsets.UTF_8),
            Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING
        )

        return JSONObject().apply {
            put("challenge", challenge)
            put("rp", JSONObject().apply {
                put("id", RP_ID)
                put("name", RP_NAME)
            })
            put("user", JSONObject().apply {
                put("id", userIdBase64)
                put("name", userName)
                put("displayName", userName)
            })
            put("pubKeyCredParams", org.json.JSONArray().apply {
                put(JSONObject().apply {
                    put("type", "public-key")
                    put("alg", -7) // ES256
                })
                put(JSONObject().apply {
                    put("type", "public-key")
                    put("alg", -257) // RS256
                })
            })
            put("authenticatorSelection", JSONObject().apply {
                put("authenticatorAttachment", "platform")
                put("residentKey", "required")
                put("userVerification", "required")
            })
            put("timeout", 60000)
            put("attestation", "none")
        }.toString()
    }

    private fun buildAuthenticationRequestJson(challenge: String): String {
        return JSONObject().apply {
            put("challenge", challenge)
            put("rpId", RP_ID)
            put("userVerification", "required")
            put("timeout", 60000)
        }.toString()
    }

    suspend fun registerPasskey(activity: Activity): Result<PasskeyRegisterRequest> {
        val userId = preferenceManager.userId
        if (userId.isEmpty()) {
            return Result.failure(PasskeyException.UserNotLoggedIn)
        }

        val userName = "${preferenceManager.firstName} ${preferenceManager.lastName}".trim()
            .ifEmpty { preferenceManager.email }

        val challenge = generateChallenge()
        val requestJson = buildRegistrationRequestJson(challenge, userId, userName)

        Log.d(TAG, "Registration request JSON: $requestJson")

        val credentialManager = CredentialManager.create(activity)
        val createPublicKeyCredentialRequest = CreatePublicKeyCredentialRequest(
            requestJson = requestJson
        )

        return try {
            val result = credentialManager.createCredential(
                context = activity,
                request = createPublicKeyCredentialRequest
            )

            val credential = result as? androidx.credentials.CreatePublicKeyCredentialResponse
                ?: return Result.failure(PasskeyException.InvalidCredentialResponse)

            Log.d(TAG, "Registration response: ${credential.registrationResponseJson}")

            val responseJson = JSONObject(credential.registrationResponseJson)
            val passkeyRegisterRequest = parseRegistrationResponse(responseJson)
            Result.success(passkeyRegisterRequest)
        } catch (e: CreateCredentialCancellationException) {
            Log.d(TAG, "User cancelled registration")
            Result.failure(PasskeyException.UserCancelled)
        } catch (e: CreateCredentialException) {
            Log.e(TAG, "Registration failed: ${e.type} - ${e.message}", e)
            if (isUserCancelledError(e.type)) {
                Result.failure(PasskeyException.UserCancelled)
            } else {
                Result.failure(PasskeyException.RegistrationFailed("${e.type}: ${e.message}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Registration failed", e)
            Result.failure(PasskeyException.RegistrationFailed(e.message ?: "Unknown error"))
        }
    }

    suspend fun authenticatePasskey(activity: Activity): Result<PasskeyLoginRequest> {
        val challenge = generateChallenge()
        val requestJson = buildAuthenticationRequestJson(challenge)

        Log.d(TAG, "Authentication request JSON: $requestJson")

        val credentialManager = CredentialManager.create(activity)
        val getPublicKeyCredentialOption = GetPublicKeyCredentialOption(
            requestJson = requestJson
        )
        val getCredentialRequest = GetCredentialRequest(
            listOf(getPublicKeyCredentialOption)
        )

        return try {
            val result = credentialManager.getCredential(
                context = activity,
                request = getCredentialRequest
            )

            val credential = result.credential as? PublicKeyCredential
                ?: return Result.failure(PasskeyException.InvalidCredentialResponse)

            Log.d(TAG, "Authentication response: ${credential.authenticationResponseJson}")

            val responseJson = JSONObject(credential.authenticationResponseJson)
            val passkeyLoginRequest = parseAuthenticationResponse(responseJson)
            Result.success(passkeyLoginRequest)
        } catch (e: GetCredentialCancellationException) {
            Log.d(TAG, "User cancelled authentication")
            Result.failure(PasskeyException.UserCancelled)
        } catch (e: GetCredentialException) {
            Log.e(TAG, "Authentication failed: ${e.type} - ${e.message}", e)
            if (isUserCancelledError(e.type)) {
                Result.failure(PasskeyException.UserCancelled)
            } else {
                Result.failure(PasskeyException.AuthenticationFailed("${e.type}: ${e.message}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Authentication failed", e)
            Result.failure(PasskeyException.AuthenticationFailed(e.message ?: "Unknown error"))
        }
    }

    private fun getOrigin(): String {
        return RP_ORIGIN
    }

    private fun isUserCancelledError(errorType: String): Boolean {
        val silentErrors = listOf(
            "CANCELED",
            "CANCELLED",
            "NO_CREDENTIAL",
            "INTERRUPTED",
            "USER_CANCELED"
        )
        return silentErrors.any { errorType.contains(it, ignoreCase = true) }
    }


    private fun parseRegistrationResponse(responseJson: JSONObject): PasskeyRegisterRequest {
        val id = responseJson.getString("id")
        val rawId = responseJson.getString("rawId")
        val response = responseJson.getJSONObject("response")
        val attestationObject = response.getString("attestationObject")
        val clientDataJSON = response.getString("clientDataJSON")

        Log.d(TAG, "Registration - clientDataJSON (Base64URL): $clientDataJSON")
        val decodedClientData = String(Base64.decode(clientDataJSON, Base64.URL_SAFE or Base64.NO_PADDING))
        Log.d(TAG, "Registration - Decoded clientDataJSON: $decodedClientData")

        val credential = PasskeyCredential(
            id = id,
            rawId = rawId,
            type = "public-key",
            response = RegistrationResponseData(
                attestationObject = attestationObject,
                clientDataJson = clientDataJSON
            )
        )

        return PasskeyRegisterRequest(
            credential = credential,
            deviceName = getDeviceName(),
            rpId = RP_ID,
            rpOrigin = getOrigin(),
            deviceType = DEVICE_TYPE
        )
    }

    private fun parseAuthenticationResponse(responseJson: JSONObject): PasskeyLoginRequest {
        Log.d(TAG, "Full authentication response: $responseJson")

        val id = responseJson.getString("id")
        val rawId = responseJson.getString("rawId")
        val response = responseJson.getJSONObject("response")
        val authenticatorData = response.getString("authenticatorData")
        val clientDataJSON = response.getString("clientDataJSON")
        val signature = response.getString("signature")
        val userHandle = response.optString("userHandle", "")

        Log.d(TAG, "Login - id: $id")
        Log.d(TAG, "Login - rawId: $rawId")
        Log.d(TAG, "Login - authenticatorData: $authenticatorData")
        Log.d(TAG, "Login - clientDataJSON (Base64URL): $clientDataJSON")
        val decodedClientData = String(Base64.decode(clientDataJSON, Base64.URL_SAFE or Base64.NO_PADDING))
        Log.d(TAG, "Login - Decoded clientDataJSON: $decodedClientData")
        Log.d(TAG, "Login - signature: $signature")
        Log.d(TAG, "Login - userHandle: $userHandle")

        val credential = PasskeyAssertionCredential(
            id = id,
            rawId = rawId,
            type = "public-key",
            response = AssertionResponseData(
                authenticatorData = authenticatorData,
                clientDataJson = clientDataJSON,
                signature = signature,
                userHandle = userHandle
            )
        )

        val request = PasskeyLoginRequest(
            credential = credential,
            deviceName = getDeviceName(),
            rpId = RP_ID,
            rpOrigin = getOrigin()
        )

        Log.d(TAG, "Login request built successfully")
        return request
    }
}

sealed class PasskeyException : Exception() {
    data object UserNotLoggedIn : PasskeyException() {
        private fun readResolve(): Any = UserNotLoggedIn
        override val message: String = "User is not logged in"
    }

    data object UserCancelled : PasskeyException() {
        private fun readResolve(): Any = UserCancelled
        override val message: String = "User cancelled the operation"
    }

    data object InvalidCredentialResponse : PasskeyException() {
        private fun readResolve(): Any = InvalidCredentialResponse
        override val message: String = "Invalid credential response"
    }

    data class RegistrationFailed(override val message: String) : PasskeyException()

    data class AuthenticationFailed(override val message: String) : PasskeyException()
}
