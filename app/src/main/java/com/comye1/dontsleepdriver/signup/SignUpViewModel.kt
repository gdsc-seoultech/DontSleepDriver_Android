package com.comye1.dontsleepdriver.signup

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.comye1.dontsleepdriver.repository.DSDRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
//    repository: DSDRepository
): ViewModel() {

    var email by mutableStateOf("")
    var userName by mutableStateOf("")
    var password by mutableStateOf("")

    fun signUp() {
        Log.d("signup", "$email $userName $password")
    }

}