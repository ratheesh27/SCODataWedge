package com.technowave.decathlon.model.Decathlon.tables

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "epc_master_table")
data class EpcMasterTable(
    val barcode: String,
    val color: String? = "",
    val description1: String? = "",
    val description2: String? = "",
    val description3: String? = "",
    val price: String? = "",
    val size: String? = ""
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}