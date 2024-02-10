package com.technowave.decathlon.repository

import com.technowave.decathlon.api.Api
import com.technowave.decathlon.storage.SharedPrefManager
import javax.inject.Inject

class MainRepo @Inject constructor(
    private val api: Api,
    private val sharedPrefManager: SharedPrefManager
) { }


