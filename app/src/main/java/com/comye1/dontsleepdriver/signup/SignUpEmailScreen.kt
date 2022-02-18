package com.comye1.dontsleepdriver.signup

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SignUpEmailScreen(
    email: String,
    setEmail: (String) -> Unit,
    requestEmailVerification: () -> Boolean,
    verifyEmailCode: (String) -> Boolean,
    next: () -> Unit
) {
    val showVerifyButton = rememberSaveable {
        mutableStateOf(false)
    }

    val verificationRequested = rememberSaveable {
        mutableStateOf(false)
    }

    val verificationResult = rememberSaveable {
        mutableStateOf(false)
    }

    val verificationCode = rememberSaveable {
        mutableStateOf("")
    }

    OutlinedTextField(
        value = email,
        onValueChange = {
            setEmail(it)
            verificationRequested.value = false
            verificationCode.value = ""
        },
        label = { Text(text = "Email") },
        modifier = Modifier.fillMaxWidth(),
    )
    Spacer(modifier = Modifier.height(16.dp))

    // 이메일 유효성 검사 (공백, 이메일 패턴)
    when {
        email.isBlank() -> {
            Text(text = "Enter your email", color = Color.Red)
            showVerifyButton.value = false
        }
        !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
            Text(text = "Your email is not valid", color = Color.Red)
            showVerifyButton.value = false
        }
        else -> {
            Text(text = "OK", color = Color.Green)
            showVerifyButton.value = true
        }
    }
    Spacer(modifier = Modifier.height(16.dp))

    Button(
        modifier = Modifier.fillMaxWidth(),
        onClick = {
            // request
            verificationRequested.value = requestEmailVerification()
        },
        enabled = !verificationRequested.value && showVerifyButton.value
    ) {
        Text(text = "Send verification email")
    }

    Spacer(modifier = Modifier.height(16.dp))

    if (verificationRequested.value) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = verificationCode.value,
                onValueChange = {
                    verificationCode.value = it
                },
                label = { Text(text = "Verification code") },
                modifier = Modifier.fillMaxWidth(.7f),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    verificationResult.value = verifyEmailCode(verificationCode.value)
                },
                enabled = !verificationResult.value
            ) {
                Text(text = "Submit")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }

    Button(
        modifier = Modifier.fillMaxWidth(),
        onClick = {
            next()
        },
        enabled = verificationResult.value
    ) {
        Text(text = "Next")
    }
}

