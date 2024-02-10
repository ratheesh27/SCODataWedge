package com.technowave.decathlon.model.upload

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "upload_rfid_table")
data class EPC(
    val epc: String
){
    @PrimaryKey(autoGenerate = true)
    var id:Long?=null
}