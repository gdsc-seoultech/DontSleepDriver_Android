package com.comye1.dontsleepdriver.signin

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.comye1.dontsleepdriver.repository.DSDRepository
import com.comye1.dontsleepdriver.util.Resource
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val repository: DSDRepository
) : ViewModel() {

    private val messageChannel = Channel<String>()
    val messageFlow = messageChannel.receiveAsFlow()

    var email by mutableStateOf("")
    var password by mutableStateOf("")


    // 로그인 요청
    fun signIn(onComplete: () -> Unit) {
        val emailCheck = emailChecker()
        if (emailCheck.first) {
            viewModelScope.launch {
                repository.signIn(email, password).also {
                    when(it) {
                        is Resource.Success -> {
                            messageChannel.send(it.data?.data?.token?: "no token")
                            Log.d("signup 2", it.data?.message ?: "null")
                            onComplete()
                        }
                        is Resource.Error -> {
                            messageChannel.send("Sign In Failed")
                            Log.d("signup 2", it.data?.error ?: "null")
                        }
                    }
                }
            }
        }
        else {
            // emailCheck.second 띄우기
            viewModelScope.launch {
                messageChannel.send(emailCheck.second)
            }
        }
    }

    // 로그인 요청 전에 이메일 유효성 검사 (공백, 이메일 패턴)
    fun emailChecker(): Pair<Boolean, String> {
        return if (email.isBlank()) {
            Pair(false, "Fill up your email")
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Pair(false, "Your email is not valid")
        } else Pair(true, "ok")
    }

    fun oAuthFailed() {
        viewModelScope.launch {
            messageChannel.send("Sign In Failed")
        }
    }

    fun kakaoSignIn(accessToken: String, onComplete: () -> Unit){
        viewModelScope.launch {
            repository.kakaoSignIn(accessToken = accessToken).also {
                when(it) {
                    is Resource.Success -> {
                        messageChannel.send(it.data?.data?.token?: "no token")
                        Log.d("signup kakao", it.data?.message ?: "null")
                        onComplete()
                    }
                    is Resource.Error -> {
                        messageChannel.send("Sign In Failed")
                        Log.d("signup kakao", it.data?.error ?: "null")
                    }
                }
            }
        }
    }

    fun googleSignIn(idToken: String) {
        Log.d("signup google", idToken)
    }

    fun naverSignIn(accessToken: String) {
        Log.d("signup naver", accessToken)
    }

}

