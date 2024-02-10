package com.technowave.decathlon.api.verification

data class GetRfidDetails(
    val errorDescription: String,
    val status: Boolean,
    val dataSet: DataSet,
)