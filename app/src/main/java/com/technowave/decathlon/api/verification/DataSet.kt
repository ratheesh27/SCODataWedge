package com.technowave.decathlon.api.verification

import com.google.gson.annotations.SerializedName

data class DataSet(
    @SerializedName("data")
    val Data: List<Data>
)