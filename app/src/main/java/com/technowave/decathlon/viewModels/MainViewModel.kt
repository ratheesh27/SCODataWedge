package com.technowave.decathlon.viewModels

import android.app.Application
import androidx.lifecycle.*
import com.technowave.decathlon.repository.MainRepo
import com.technowave.decathlon.storage.SharedPrefManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    app: Application,
    private val repo: MainRepo,
    private val sharedPrefManager: SharedPrefManager
) :
    AndroidViewModel(app) { }


sealed class LoginValidationResult {
    data class Success(val message: String) : LoginValidationResult()
    data class Error(val errorMessage: String) : LoginValidationResult()
}
