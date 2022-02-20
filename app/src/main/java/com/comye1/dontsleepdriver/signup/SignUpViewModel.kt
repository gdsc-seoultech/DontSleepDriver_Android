package com.comye1.dontsleepdriver.signup

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.comye1.dontsleepdriver.repository.DSDRepository
import com.comye1.dontsleepdriver.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val repository: DSDRepository
) : ViewModel() {

    private val messageChannel = Channel<String>()
    val messageFlow = messageChannel.receiveAsFlow()

    var email by mutableStateOf("")
    var userName by mutableStateOf("")
    var password by mutableStateOf("")

    var showVerifyButton by mutableStateOf(false) // 인증 버튼 활성화

    var verificationRequested by mutableStateOf(false) // 인증 버튼이 눌림 -> 입력창 활성화

    var verificationResult by mutableStateOf(false)

    var verificationCode by mutableStateOf("")

    fun requestEmailVerificationCode() {
        viewModelScope.launch {
            repository.requestEmailVerificationCode(email).also {
                when (it) {
                    is Resource.Success -> {
                        verificationRequested = true
                        Log.d("signup 1", it.data?.message ?: "null")
                        messageChannel.send("Verification mail sent")
                    }
                    is Resource.Error -> {
                        verificationRequested = false

                        Log.d("signup 1", it.data?.message ?: "null")
                        Log.d("signup 1", it.data?.error ?: "null")
                        messageChannel.send("Verification request failed")
                    }
                }
            }
        }
    }

    fun verifyEmailCode() {
        viewModelScope.launch {
            repository.verifyEmailCode(email, verificationCode).also {
                when (it) {
                    is Resource.Success -> {
                        verificationResult = true
                        messageChannel.send("Verification succeeded")
                        Log.d("signup 2", it.data?.message ?: "null")
                    }
                    is Resource.Error -> {
                        verificationResult = false
                        messageChannel.send("Verification failed")
                        Log.d("signup 2", it.data?.error ?: "null")
                    }
                }
            }
        }
    }

    var signUpResult by mutableStateOf(false)

    fun signUp() {
//        Log.d("signUp", "$email $password $userName")
        viewModelScope.launch {
            repository.signUp(email = email, password = password, name = userName).also {
                when (it) {
                    is Resource.Success -> {
                        signUpResult = true
                        messageChannel.send("Success")
                        Log.d("signup 2", it.data?.message ?: "null")
                    }
                    is Resource.Error -> {
                        signUpResult = false
                        messageChannel.send("Failed")
                        Log.d("signup 2", it.data?.error ?: "null")
                    }
                }
            }
        }
    }

}