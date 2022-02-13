package com.comye1.dontsleepdriver.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.comye1.dontsleepdriver.ui.theme.Purple700

@Composable
fun SignUpScreen(toLogIn: () -> Unit) {
    val (id, setID) = remember {
        mutableStateOf("")
    }

    val (pw, setPW) = remember {
        mutableStateOf("")
    }

    val (pwAgain, setPWAgain) = remember {
        mutableStateOf("")
    }

    val (notDuplicated, setNotDuplicated) = remember {
        mutableStateOf(false)
    }

    val (duplicationCheckerShown, showDuplicationChecker) = remember {
        mutableStateOf(false)
    }

    val (alertShown, showAlert) = remember {
        mutableStateOf("")
    }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(.8f),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            LogoSection()
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = id,
                onValueChange = {
                    setID(it)
                    setNotDuplicated(false)
                },
                label = { Text(text = "아이디") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    if (notDuplicated) Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "not duplicated",
                        tint = Purple700
                    )
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(
                onClick = {
                    showDuplicationChecker(true)
                    /*TODO*/
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (notDuplicated) {
                    Text(text = "아이디 중복 확인 완료")
                } else {
                    Text(text = "아이디 중복 확인")
                }
            }
            if (duplicationCheckerShown) {
                SignUpDialog(onDismiss = {
                    showDuplicationChecker(false)
                    setNotDuplicated(true)
                }) {
                    if (id.isBlank()) {
                        Text("아이디를 입력해주세요.")
                    }else {
                        Text(buildAnnotatedString {
                            append("아이디 ")
                            pushStyle(SpanStyle(color = Purple700))
                            append(id)
                            pop()
                            append("를 사용할 수 있습니다.")
                        })

                    }
                    Spacer(modifier = Modifier.height(32.dp))
                    OutlinedButton(
                        onClick = {
                            showDuplicationChecker(false)
                            setNotDuplicated(true)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "확인")
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = pw,
                onValueChange = setPW,
                label = { Text(text = "비밀번호") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation()
            )

            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = pwAgain,
                onValueChange = setPWAgain,
                label = { Text(text = "비밀번호 확인") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                trailingIcon = {
                    if (pw.isNotBlank() && (pw == pwAgain))
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "password confirmed",
                            tint = Purple700
                        )
                }
            )

            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(
                onClick = {
                    // 회원가입 및 로그인 화면으로 이동
                    when {
                        id.isBlank() -> {
                            showAlert("아이디를 입력해주세요.")
                        }
                        pw.isBlank() -> {
                            showAlert("비밀번호를 입력해주세요.")
                        }
                        pw != pwAgain -> {
                            showAlert("비밀번호가 서로 다릅니다.")
                        }
                        !notDuplicated -> {
                            showAlert("아이디 중복 확인을 해주세요.")
                        }
                        else -> {
                            toLogIn()
                        }
                    }
                                    },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = "회원가입")
            }
            if(alertShown.isNotBlank()) {
                SignUpDialog(onDismiss = { showAlert("") }) {
                    Text(text = alertShown)
                    Spacer(modifier = Modifier.height(32.dp))
                    OutlinedButton(
                        onClick = {
                            showAlert("")
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "확인")
                    }
                }
            }
        }
    }
}

@Composable
fun SignUpDialog(onDismiss: () -> Unit, content: @Composable () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White)
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            content()
        }
    }
}