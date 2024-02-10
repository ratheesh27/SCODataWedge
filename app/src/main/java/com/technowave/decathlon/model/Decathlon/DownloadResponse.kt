package com.technowave.decathlon.model.Decathlon

data class DownloadResponse(
    val Status:Boolean,
    val Inventory: List<Inventory>,
    val errorMsg:String,
    val Count:String
)

