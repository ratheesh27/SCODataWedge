package com.technowave.decathlon.model.download

data class DownloadResponse(
    val dataSet: DataSet,
    val errorDescription: String,
    val status: Boolean
)