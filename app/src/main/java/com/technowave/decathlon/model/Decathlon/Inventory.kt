package com.technowave.decathlon.model.Decathlon

data class Inventory(
    val EPCs: List<EPC>,
    val barcode: String,
    val color: String,
    val description1: String,
    val description2: String,
    val description3: String,
    val price: String,
    val size: String
)