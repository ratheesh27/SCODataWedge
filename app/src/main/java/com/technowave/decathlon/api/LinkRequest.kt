package com.technowave.decathlon.api

data class LinkRequest(
    val CreatedOn: String,
    val ItemCode: String,
    val UpInfo: List<UpInfo>,
    val createdBy:String
)