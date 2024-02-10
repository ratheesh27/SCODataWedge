package com.technowave.decathlon.model.download


import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "masterTable")
data class DATA(
    @SerializedName("CURRENT_STOCK_QTY")
    val currentStockQty: String,
    @SerializedName("ITEM_CODE")
    val itemCode: String,
    @SerializedName("LOCATION_CODE")
    val locationCode: String,
    @SerializedName("RFID")
    val rfid: String,
    @SerializedName("TAG_NAME")
    val tagName: String,
    @SerializedName("SERIAL_NO")
    val serialNo: String?=""
){
    @PrimaryKey(autoGenerate = true)
    var id:Int?=null
}