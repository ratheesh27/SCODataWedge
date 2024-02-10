package com.technowave.decathlon.api.verification

import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("createdOn")
    val createdOn: String,
    @SerializedName("itemCode")
    val ItemCode: String,
    @SerializedName("rfid")
    val RFID: String,
    val createdBy:String
)