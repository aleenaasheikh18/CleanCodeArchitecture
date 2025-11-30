package com.chat.myapplication.core.data.auth.model

import com.chat.myapplication.core.exception.BaseResponse
import com.google.gson.annotations.SerializedName

data class CountriesAreasResponse(
    @SerializedName("data") val data: CountriesData? = null
) : BaseResponse()

data class CountriesData(
    @SerializedName("countries") val countries: List<CountryArea>? = null
)

data class CountryArea(
    @SerializedName("_id") val id: String? = null,
    @SerializedName("country") val name: String? = null,
    @SerializedName("iso_code") val isoCode: String? = null,
    @SerializedName("code") val countryCode: String? = null,
    @SerializedName("currency") val currency: String? = null,
    @SerializedName("currency_icon") val currencyIcon: String? = null,
    @SerializedName("flag") val flag: String? = null,
    @SerializedName("areas") val areas: List<String>? = null
)
