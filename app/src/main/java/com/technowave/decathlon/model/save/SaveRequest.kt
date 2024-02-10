package com.technowave.decathlon.model.save


import com.google.gson.annotations.SerializedName

data class SaveRequest(
    @SerializedName("FlagOverWrt")
    val flagOverWrt: String,
    @SerializedName("STKInfo")
    val sTKInfo: List<STKInfo>
)