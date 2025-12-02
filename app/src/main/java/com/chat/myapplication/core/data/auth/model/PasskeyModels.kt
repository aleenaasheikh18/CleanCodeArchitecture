package com.chat.myapplication.core.data.auth.model

import com.chat.myapplication.core.exception.BaseResponse
import com.chat.myapplication.utility.empty
import com.google.gson.annotations.SerializedName

// region Registration Request Models
data class PasskeyRegisterRequest(
    @SerializedName("credential") val credential: PasskeyCredential,
    @SerializedName("device_name") val deviceName: String,
    @SerializedName("rp_id") val rpId: String,
    @SerializedName("rp_origin") val rpOrigin: String,
    @SerializedName("device_type") val deviceType: String = "android"
)

data class PasskeyCredential(
    @SerializedName("id") val id: String,
    @SerializedName("raw_id") val rawId: String,
    @SerializedName("type") val type: String = "public-key",
    @SerializedName("response") val response: RegistrationResponseData
)

data class RegistrationResponseData(
    @SerializedName("attestation_object") val attestationObject: String,
    @SerializedName("client_data_json") val clientDataJson: String
)
// endregion

// region Login Request Models
data class PasskeyLoginRequest(
    @SerializedName("credential") val credential: PasskeyAssertionCredential,
    @SerializedName("device_name") val deviceName: String,
    @SerializedName("rp_id") val rpId: String,
    @SerializedName("rp_origin") val rpOrigin: String
)

data class PasskeyAssertionCredential(
    @SerializedName("id") val id: String,
    @SerializedName("raw_id") val rawId: String,
    @SerializedName("type") val type: String = "public-key",
    @SerializedName("response") val response: AssertionResponseData
)

data class AssertionResponseData(
    @SerializedName("authenticator_data") val authenticatorData: String,
    @SerializedName("client_data_json") val clientDataJson: String,
    @SerializedName("signature") val signature: String,
    @SerializedName("user_handle") val userHandle: String
)
// endregion

// region Response Models
data class PasskeyRegisterResponse(
    @SerializedName("data") val data: PasskeyRegisterData? = null
) : BaseResponse()

data class PasskeyRegisterData(
    @SerializedName("passkey_id") val passkeyId: String? = String.empty,
    @SerializedName("device_name") val deviceName: String? = String.empty,
    @SerializedName("created_at") val createdAt: String? = String.empty
)
// endregion
