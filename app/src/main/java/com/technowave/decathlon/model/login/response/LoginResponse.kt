package com.technowave.decathlon.model.login.response

data class LoginResponse(
    val deptID: Int?,
    val errorDescription: String?,
    val regToken: String?,
    val retValue: Int?,
    val roleID: Int?,
    val status: Boolean?,
    val storeCode: String?,
    val storeID: Int?,
    val storeName: String?,
    val userID: String?,
    val userName: String?,
    val userType: Int?
)