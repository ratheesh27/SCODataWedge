package com.technowave.decathlon.api

import android.util.Base64
import android.util.Log
import com.technowave.decathlon.storage.SharedPrefManager
import com.technowave.decathlon.utils.Constants.API_PASSWORD
import com.technowave.decathlon.utils.Constants.API_USER_NAME


import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Response
import java.io.UnsupportedEncodingException

class UrlInterceptor(private val preferences: SharedPrefManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
       preferences.getBaseUrl()?.let { baseUrl ->
           val newUrl = HttpUrl.parse(
              chain.request().url().toString().replace("https://localhost/", baseUrl)
         ) ?: chain.request().url()
         request.url(newUrl)
           Log.d("MSD",baseUrl)
       }

        //request.header("Authorization", "Bearer ${preferences.getToken()}")
        request.header("Authorization", "Basic YWRtaW46YWRtaW4=")//preferences.getToken())
       // request.header("Authorization", getAuthToken())//preferences.getToken())
      //  getAuthToken()
      //  request.header("Authorization", getAuthToken())//preferences.getToken())
        return chain.proceed(request.build())
    }

    private fun getAuthToken(): String {
        var data = ByteArray(0)
        try {
            data = ("$API_USER_NAME:$API_PASSWORD").toByteArray(charset("UTF-8"))
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
        return "Basic " + Base64.encodeToString(data, Base64.NO_WRAP)
    }
}