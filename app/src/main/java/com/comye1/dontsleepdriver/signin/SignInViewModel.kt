package com.comye1.dontsleepdriver.signin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
//    repository: DSDRepository
) : ViewModel() {

    var email by mutableStateOf("")
    var password by mutableStateOf("")

    // 로그인 요청
    fun signIn() {

    }

    // 로그인 요청 전에 이메일 유효성 검사 (공백, 이메일 패턴)
    fun emailChecker(): Pair<Boolean, String> {
        return if (email.isBlank()) {
            Pair(false, "Fill up your email")
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Pair(false, "Your email is not valid")
        } else Pair(true, "ok")
    }


}

