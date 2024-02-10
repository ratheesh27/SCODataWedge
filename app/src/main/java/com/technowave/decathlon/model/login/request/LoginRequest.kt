package com.technowave.decathlon.model.login.request

data class LoginRequest(
    val deviceMACID: String?="1",
    val locationID: Int?=1,
    val password: String?,
    val regToken: String?="1",
    val userID: String?
)