package com.technowave.decathlon.model.download


import com.google.gson.annotations.SerializedName

data class DataSet(
    @SerializedName("DATA")
    val data: List<DATA>
)