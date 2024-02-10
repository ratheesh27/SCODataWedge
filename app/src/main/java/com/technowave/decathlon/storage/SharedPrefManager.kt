package com.technowave.decathlon.storage

import android.content.SharedPreferences
import androidx.core.content.edit
import com.technowave.decathlon.utils.Constants.BASE_URL
import javax.inject.Inject

class SharedPrefManager @Inject constructor(private val sharedPreferences: SharedPreferences) {

    fun setBaseUrl(baseUrl: String) = sharedPreferences.edit { putString(BASE_URL, baseUrl) }
    fun getBaseUrl() = sharedPreferences.getString(BASE_URL, "https://shipmenttrackapi.technowavegroup.com/")

    fun setStoreCode(baseUrl: String) = sharedPreferences.edit { putString("location_id", baseUrl) }
    fun getStoreCode() = sharedPreferences.getString("location_id", "0")



}