package com.technowave.decathlon.model


data class CommonResponse(
    val errorDescription: String?="",
    val retValue: Int,
    val status: Boolean
)