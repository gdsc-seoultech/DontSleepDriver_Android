package com.comye1.dontsleepdriver.signup

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SignUpEmailScreen(
    viewModel: SignUpViewModel,
    next: () -> Unit
) {
    OutlinedTextField(
        value = viewModel.email,
        onValueChange = {
            viewModel.email = it
            viewModel.verificationRequested = false
            viewModel.verificationCode = ""
        },
        label = { Text(text = "Email") },
        modifier = Modifier.fillMaxWidth(),
    )
    Spacer(modifier = Modifier.height(16.dp))

    // 이메일 유효성 검사 (공백, 이메일 패턴)
    when {
        viewModel.email.isBlank() -> {
            Text(text = "Enter your email", color = Color.Red)
            viewModel.showVerifyButton = false
        }
        !android.util.Patterns.EMAIL_ADDRESS.matcher(viewModel.email).matches() -> {
            Text(text = "Your email is not valid", color = Color.Red)
            viewModel.showVerifyButton = false
        }
        else -> {
            Text(text = "OK", color = Color.Green)
            viewModel.showVerifyButton = true
        }
    }
    Spacer(modifier = Modifier.height(16.dp))

    Button(
        modifier = Modifier.fillMaxWidth(),
        onClick = {
            // request
            viewModel.requestEmailVerificationCode()
        },
        enabled = !viewModel.verificationRequested && viewModel.showVerifyButton
    ) {
        Text(text = "Send verification email")
    }

    Spacer(modifier = Modifier.height(16.dp))

    if (viewModel.verificationRequested) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = viewModel.verificationCode,
                onValueChange = {
                    viewModel.verificationCode = it
                },
                label = { Text(text = "Verification code") },
                modifier = Modifier.fillMaxWidth(.7f),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    viewModel.verifyEmailCode()
                },
                enabled = !viewModel.verificationResult
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
        enabled = viewModel.verificationResult
    ) {
        Text(text = "Next")
    }
}

