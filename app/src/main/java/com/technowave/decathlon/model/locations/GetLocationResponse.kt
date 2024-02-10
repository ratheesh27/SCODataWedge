package com.technowave.decathlon.model.locations

data class GetLocationResponse(
    val ErrorDescription: String,
    val Status: Boolean,
    val dataSet: DataSet
)