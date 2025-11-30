package com.chat.myapplication.core.data.auth.model

import com.chat.myapplication.core.exception.BaseResponse
import com.google.gson.annotations.SerializedName

data class CountriesAreasResponse(
    @SerializedName("data") val data: List<CountryArea>? = null
) : BaseResponse()

data class CountryArea(
    @SerializedName("id") val id: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("iso_code") val isoCode: String? = null,
    @SerializedName("country_code") val countryCode: String? = null,
    @SerializedName("currency") val currency: String? = null,
    @SerializedName("currency_icon") val currencyIcon: String? = null,
    @SerializedName("flag") val flag: String? = null
)
