package com.chat.myapplication.core.data.auth.model

import com.chat.myapplication.core.exception.BaseResponse
import com.chat.myapplication.utility.empty
import com.google.gson.annotations.SerializedName

data class SignInResponse(
    @SerializedName("data") val data: SignInData? = null
) : BaseResponse()

data class SignInData(
    @SerializedName("customer") val customer: Customer? = null,
    @SerializedName("token") val token: String = String.empty,
    @SerializedName("admin_id") val adminId: String = String.empty
)

data class Customer(
    @SerializedName("id") val id: String? = String.empty,
    @SerializedName("email") val email: String? = String.empty,
    @SerializedName("first_name") val firstName: String? = String.empty,
    @SerializedName("last_name") val lastName: String? = String.empty,
    @SerializedName("phone") val phoneNo: String? = String.empty,
    @SerializedName("profile_image") val profileImage: String? = String.empty,
    @SerializedName("thumbnail") val thumbnail: String? = String.empty,
    @SerializedName("stripe_id") val stripeId: String? = String.empty,
    @SerializedName("stripe_completed") val stripeCompleted: Boolean = false,
    @SerializedName("country") val country: String? = String.empty,
    @SerializedName("currency") val currency: String? = String.empty,
    @SerializedName("currency_icon") val currencyIcon: String? = String.empty,
    @SerializedName("customer_type") val customerType: String? = String.empty,
    @SerializedName("referral_url") val referralUrl: String? = String.empty,
    @SerializedName("referral_link") val referralLink: String? = String.empty,
    @SerializedName("referral_bonus") val referralBonus: Float = 0f,
    @SerializedName("product_announcement") val productAnnouncement: Boolean = false,
    @SerializedName("notification") val notification: Boolean = false,
    @SerializedName("i_am_hungry") val iAmHungry: String? = String.empty,
    @SerializedName("new_message") val newMessage: String? = String.empty,
    @SerializedName("receipt") val receipt: String? = String.empty,
    @SerializedName("persona_attemped_count") val personaAttemptedCount: Int = 0,
    @SerializedName("last_persona_attemped") val lastPersonaAttempted: String? = String.empty,
    @SerializedName("is_on_boarded") val isOnBoarded: Boolean = false,
    @SerializedName("delivery_address") val deliveryAddress: DeliveryAddress? = null
)

data class DeliveryAddress(
    @SerializedName("address") val address: String? = String.empty,
    @SerializedName("city") val city: String? = String.empty,
    @SerializedName("state") val state: String? = String.empty,
    @SerializedName("country") val country: String? = String.empty
)
