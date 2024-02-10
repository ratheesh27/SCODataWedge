package com.technowave.decathlon.api.verification.save

data class VerificationRequest(
    val CreatedOn: String,
    val ItemCode: Int,
    val Status: String,
    val UpInfo: List<UpInfo>,
    val createdBy:String
)