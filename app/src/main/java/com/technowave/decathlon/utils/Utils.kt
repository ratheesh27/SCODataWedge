
package com.technowave.decathlon.utils

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import android.util.Base64
import java.io.UnsupportedEncodingException
import java.util.*


@SuppressLint("SuspiciousIndentation")
fun Context.showToast(message: String) {
    if(message!="")
    Toast.makeText(
        this, message, Toast.LENGTH_LONG).show()
}


private fun getAuthToken(): String {
    var data = ByteArray(0)
    try {
        data = ("").toByteArray(charset("UTF-8"))
    } catch (e: UnsupportedEncodingException) {
        e.printStackTrace()
    }
    return "Basic " + Base64.encodeToString(data, Base64.NO_WRAP)
}














