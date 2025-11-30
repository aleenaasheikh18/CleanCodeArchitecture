package com.chat.myapplication.core.data.auth.model

import com.chat.myapplication.core.exception.BaseResponse
import com.google.gson.annotations.SerializedName

data class AllergiesResponse(
    @SerializedName("data") val data: AllergiesData? = null
) : BaseResponse()

data class AllergiesData(
    @SerializedName("allergy") val allergies: List<Allergy>? = null
)

data class Allergy(
    @SerializedName("_id") val id: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("created_at") val createdAt: String? = null,
    @SerializedName("updated_at") val updatedAt: String? = null,
    var isSelected: Boolean = false
)

data class UpdateAllergiesRequest(
    @SerializedName("allergies") val allergies: List<String>
)
