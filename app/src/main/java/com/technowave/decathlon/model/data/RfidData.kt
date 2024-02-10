package com.technowave.decathlon.model.data

data class RfidData(
    var barcode:String,
    var rfid:String,
    var descOne:String?="",
    var descTwo:String?="",
    var descThree:String?="",
    var color:String?=""
) {
    var id:Int?=null
}

