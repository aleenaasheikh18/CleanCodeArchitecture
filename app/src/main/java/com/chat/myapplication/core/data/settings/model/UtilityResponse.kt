package com.chat.myapplication.core.data.settings.model

import com.chat.myapplication.core.exception.BaseResponse
import com.chat.myapplication.utility.empty
import com.google.gson.annotations.SerializedName

data class UtilityResponse(
    @SerializedName("data") val policyData: PolicyData
) : BaseResponse()

data class PolicyData(
    @SerializedName("eater_policy") val eaterPolicy: String? = String.empty,
    @SerializedName("eater_terms") val eaterTerms: String? = String.empty
)
