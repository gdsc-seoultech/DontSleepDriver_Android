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
import androidx.compose.ui.unit.dp

@Composable
fun SignUpUserNameScreen(
    userName: String,
    setUserName: (String) -> Unit,
    next: () -> Unit
) {

    val showNextButton = rememberSaveable {
        mutableStateOf(false)
    }
    OutlinedTextField(
        value = userName,
        onValueChange = {
            setUserName(it)
        },
        label = { Text(text = "Name") },
        modifier = Modifier.fillMaxWidth(),
    )
    Spacer(modifier = Modifier.height(16.dp))

    // 이름 유효성 검사 (공백, 이메일 패턴)
    if (userName.length < 2 || userName.length > 30) {
        Text(text = "Name must be between 8-30 characters", color = Color.Red)
        Spacer(modifier = Modifier.height(16.dp))
        showNextButton.value = false
    }else {
        showNextButton.value = true
    }

    Button(
        modifier = Modifier.fillMaxWidth(),
        onClick = {
            next()
        },
        enabled = showNextButton.value
    ) {
        Text(text = "Next")
    }

}