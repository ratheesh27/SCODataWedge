package com.technowave.decathlon.model.Decathlon.tables

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "epc_table")
data class EpcTable(
    val epc: String,
    val barcode: String
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}