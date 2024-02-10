package com.technowave.decathlon.model.save


import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
@Entity(tableName = "saveRfid")
data class STKInfo(
    @SerializedName("BatchNo")
    val batchNo: String,
    @SerializedName("Location")
    val location: String,
    @SerializedName("RFID")
    val rFID: String,
    @SerializedName("ScanDate")
    val scanDate: String
){
    @PrimaryKey(autoGenerate = true)
    var id:Int?=null
}