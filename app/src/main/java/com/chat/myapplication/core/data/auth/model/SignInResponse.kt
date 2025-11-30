package com.chat.myapplication.core.data.auth.model

import com.chat.myapplication.core.exception.BaseResponse
import com.chat.myapplication.utility.empty
import com.google.gson.annotations.SerializedName

data class SignInResponse(
    @SerializedName("data") val data: SignInData? = null
) : BaseResponse()

data class SignInData(
    @SerializedName("customer") val customer: Customer? = null,
    @SerializedName("token") val token: String? = String.empty,
    @SerializedName("admin_id") val adminId: String? = String.empty,
    @SerializedName("unreadMessage") val unreadMessage: Int = 0,
    @SerializedName("referral_bonus") val referralBonus: Float = 0f
)

data class Customer(
    @SerializedName("_id") val id: String? = String.empty,
    @SerializedName("email") val email: String? = String.empty,
    @SerializedName("first_name") val firstName: String? = String.empty,
    @SerializedName("last_name") val lastName: String? = String.empty,
    @SerializedName("phone_no") val phoneNo: String? = String.empty,
    @SerializedName("dob") val dob: String? = null,
    @SerializedName("profile_image") val profileImage: String? = String.empty,
    @SerializedName("thumbnail") val thumbnail: String? = String.empty,
    @SerializedName("stripeId") val stripeId: String? = String.empty,
    @SerializedName("stripe") val stripe: String? = String.empty,
    @SerializedName("is_kyc_completed") val isKycCompleted: Boolean = false,
    @SerializedName("country") val country: String? = String.empty,
    @SerializedName("currency") val currency: String? = String.empty,
    @SerializedName("currency_icon") val currencyIcon: String? = String.empty,
    @SerializedName("customer_type") val customerType: String? = String.empty,
    @SerializedName("referral_url") val referralUrl: String? = String.empty,
    @SerializedName("referral_link") val referralLink: String? = String.empty,
    @SerializedName("referral_status") val referralStatus: String? = String.empty,
    @SerializedName("referral_bonus") val referralBonus: Float = 0f,
    @SerializedName("product_announcement") val productAnnouncement: Boolean = false,
    @SerializedName("notification") val notification: Boolean = false,
    @SerializedName("i_am_hungry") val iAmHungry: String? = String.empty,
    @SerializedName("new_message") val newMessage: String? = String.empty,
    @SerializedName("receipt") val receipt: String? = String.empty,
    @SerializedName("persona_attemped_count") val personaAttemptedCount: Int = 0,
    @SerializedName("last_persona_attemped") val lastPersonaAttempted: String? = null,
    @SerializedName("is_on_boarded") val isOnBoarded: Boolean = false,
    @SerializedName("delivery_address") val deliveryAddress: DeliveryAddress? = null,
    @SerializedName("flag") val flag: String? = String.empty,
    @SerializedName("iso_code") val isoCode: String? = String.empty,
    @SerializedName("is_persona_verified") val isPersonaVerified: Boolean = false,
    @SerializedName("is_suspended") val isSuspended: Boolean = false,
    @SerializedName("allergies") val allergies: List<String>? = null,
    @SerializedName("location") val location: CustomerLocation? = null,
    @SerializedName("phone_no_verification") val phoneNoVerification: PhoneNoVerification? = null,
    @SerializedName("account_verification") val accountVerification: AccountVerification? = null,
    @SerializedName("cuisine_reminder") val cuisineReminder: CuisineReminder? = null,
    @SerializedName("cuisines") val cuisines: List<String>? = null,
    @SerializedName("menu_types") val menuTypes: List<String>? = null,
    @SerializedName("commission_rates") val commissionRates: Float = 0f,
    @SerializedName("is_search_inclusive") val isSearchInclusive: Boolean = true,
    @SerializedName("area") val area: String? = null,
    @SerializedName("time_zone") val timeZone: String? = String.empty,
    @SerializedName("invited_by") val invitedBy: String? = null,
    @SerializedName("credit") val credit: Float = 0f,
    @SerializedName("canceled_allowed") val canceledAllowed: Int = 0,
    @SerializedName("canceled_orders") val canceledOrders: Int = 0,
    @SerializedName("device_token") val deviceToken: String? = null,
    @SerializedName("self_deactive") val selfDeactive: Boolean = false,
    @SerializedName("is_banned") val isBanned: Boolean = false,
    @SerializedName("deleted_at") val deletedAt: String? = null,
    @SerializedName("authtoken") val authToken: String? = String.empty,
    @SerializedName("social_urls") val socialUrls: List<String>? = null,
    @SerializedName("refund_rate") val refundRate: Float = 0f,
    @SerializedName("commission_percentage") val commissionPercentage: Float = 0f,
    @SerializedName("is_custom_commission_contract") val isCustomCommissionContract: Boolean = false,
    @SerializedName("is_commission_in_percentage") val isCommissionInPercentage: Boolean = true,
    @SerializedName("commision_fixed") val commissionFixed: Float = 0f,
    @SerializedName("social_type") val socialType: String? = null,
    @SerializedName("social_id") val socialId: String? = null,
    @SerializedName("countries") val countries: List<CustomerCountry>? = null,
    @SerializedName("order_count") val orderCount: Int = 0,
    @SerializedName("is_set_password") val isSetPassword: Boolean = false,
    @SerializedName("minimum_withdrawal") val minimumWithdrawal: Float = 0f,
    @SerializedName("foodchoo_deposit_fees") val foodchooDepositFees: Float = 0f,
    @SerializedName("foodchoo_withdrawal_fees") val foodchooWithdrawalFees: Float = 0f,
    @SerializedName("influencer_minimum_radius") val influencerMinimumRadius: Int = 0
)

data class DeliveryAddress(
    @SerializedName("address") val address: String? = String.empty,
    @SerializedName("city") val city: String? = String.empty,
    @SerializedName("state") val state: String? = String.empty,
    @SerializedName("country") val country: String? = String.empty,
    @SerializedName("note") val note: String? = String.empty,
    @SerializedName("building_name") val buildingName: String? = String.empty,
    @SerializedName("floor") val floor: String? = String.empty,
    @SerializedName("postal_code") val postalCode: String? = String.empty,
    @SerializedName("street_address") val streetAddress: String? = String.empty,
    @SerializedName("unit_no") val unitNo: String? = String.empty,
    @SerializedName("coordinates") val coordinates: List<Double>? = null
)

data class CustomerLocation(
    @SerializedName("type") val type: String? = String.empty,
    @SerializedName("coordinates") val coordinates: List<Double>? = null
)

data class PhoneNoVerification(
    @SerializedName("phone_no") val phoneNo: String? = String.empty,
    @SerializedName("verification_code") val verificationCode: String? = String.empty,
    @SerializedName("expires_at") val expiresAt: String? = String.empty,
    @SerializedName("daily_attempts") val dailyAttempts: DailyAttempts? = null
)

data class DailyAttempts(
    @SerializedName("count") val count: Int = 0,
    @SerializedName("date") val date: String? = String.empty
)

data class AccountVerification(
    @SerializedName("status") val status: Boolean = false,
    @SerializedName("verification_token") val verificationToken: String? = String.empty
)

data class CuisineReminder(
    @SerializedName("cuisine") val cuisine: List<String>? = null,
    @SerializedName("status") val status: Boolean = false,
    @SerializedName("notification_count") val notificationCount: Int = 0,
    @SerializedName("start_time") val startTime: String? = String.empty,
    @SerializedName("respect_my_allergens") val respectMyAllergens: Boolean = false
)

data class CustomerCountry(
    @SerializedName("_id") val id: String? = String.empty,
    @SerializedName("country") val country: String? = String.empty,
    @SerializedName("currency") val currency: String? = String.empty,
    @SerializedName("currency_icon") val currencyIcon: String? = String.empty,
    @SerializedName("flag") val flag: String? = String.empty,
    @SerializedName("iso_code") val isoCode: String? = String.empty,
    @SerializedName("is_default") val isDefault: Boolean = false,
    @SerializedName("primary") val primary: Boolean = false,
    @SerializedName("stripe") val stripe: String? = String.empty,
    @SerializedName("stripeId") val stripeId: String? = String.empty,
    @SerializedName("is_kyc_completed") val isKycCompleted: Boolean = false,
    @SerializedName("account_number") val accountNumber: String? = null,
    @SerializedName("routing_number") val routingNumber: String? = null,
    @SerializedName("account_holder_name") val accountHolderName: String? = null,
    @SerializedName("account_holder_type") val accountHolderType: String? = null
)
