package com.technowave.decathlon.model.upload

data class SaveRequest(
    val EPCs: List<EPC>,
    val qty: String,
    val storecode: String
)

data class UploadResponse(var Result: Boolean? = false, var Msg: String? = "")