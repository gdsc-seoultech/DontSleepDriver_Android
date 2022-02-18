package com.comye1.dontsleepdriver.signup

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun SignUpPasswordScreen(
    password: String,
    setPassword: (String) -> Unit,
    createAccount: () -> Unit
) {
    val showCreateAccountButton = rememberSaveable {
        mutableStateOf(false)
    }
    OutlinedTextField(
        value = password,
        onValueChange = {
            setPassword(it)
        },
        label = { Text(text = "Password") },
        modifier = Modifier.fillMaxWidth(),
        visualTransformation = PasswordVisualTransformation()
    )
    Spacer(modifier = Modifier.height(16.dp))
    // 이메일 유효성 검사 (공백, 이메일 패턴)

    if (password.isValidPassword()) {
        showCreateAccountButton.value = true
    }else {
        Text(text = "Your password must be between 8-20 characters and contain an uppercase letter, a lowercase letter, a number, a special character( !@#\$%^&*() )", color = Color.Red)
        Spacer(modifier = Modifier.height(16.dp))
        showCreateAccountButton.value = false
    }

    Button(
        modifier = Modifier.fillMaxWidth(),
        onClick = {
            createAccount()
        },
        enabled = showCreateAccountButton.value
    ) {
        Text(text = "Create Account")
    }

}

fun String.isValidPassword(): Boolean {
    val reg = Regex("^[A-Za-z\\d!@#\$%^&*()]{8,30}\$")
    return this.matches(reg)
}