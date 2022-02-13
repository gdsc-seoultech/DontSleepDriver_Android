package com.comye1.dontsleepdriver.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.comye1.dontsleepdriver.R

@Composable
fun LogInScreen(toMain: () -> Unit, toSignUp: () -> Unit) {
    val (id, setID) = remember {
        mutableStateOf("")
    }

    val (pw, setPW) = remember {
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
                onValueChange = setID,
                label = { Text(text = "아이디") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = pw,
                onValueChange = setPW,
                label = { Text(text = "비밀번호") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(
                onClick = {
                    toMain()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "로그인")
            }
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(
                onClick = {
                    toSignUp()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "회원가입")
            }
            Spacer(modifier = Modifier.height(16.dp))
            OAuthLogInButton(
                modifier = Modifier.fillMaxWidth(),
                painter = painterResource(id = R.drawable.ic_google_icon),
                size = 24.dp,
                text = "Sign in with Google"
            ) {

            }
            Spacer(modifier = Modifier.height(16.dp))
            OAuthLogInButton(
                modifier = Modifier.fillMaxWidth(),
                painter = painterResource(id = R.drawable.kakao_logo_img),
                size = 24.dp,
                text = "Sign in with Kakao"
            ) {

            }
            Spacer(modifier = Modifier.height(16.dp))
            OAuthLogInButton(
                modifier = Modifier.fillMaxWidth(),
                painter = painterResource(id = R.drawable.naver_logo_img),
                size = 28.dp,
                text = "Sign in with Naver"
            ) {

            }
        }
    }
}


@Composable
fun LogoSection() {
    Text(text = "Don't Sleep Driver!", style = MaterialTheme.typography.h4, fontWeight = FontWeight.Bold)
    Spacer(modifier = Modifier.height(16.dp))
    Image(
        painter = painterResource(id = R.drawable.driving_img),
        contentDescription = "driving image",
        modifier = Modifier.size(160.dp)
    )
}

@Composable
fun OAuthLogInButton(
    modifier: Modifier = Modifier,
    painter: Painter,
    size: Dp,
    text: String,
    onClick: () -> Unit
) {
    OutlinedButton(onClick = onClick, modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(.15f),
            horizontalArrangement = Arrangement.Center
        ) {
            Image(painter = painter, contentDescription = text, modifier = Modifier.size(size))
        }
        Spacer(modifier = Modifier.width(8.dp))
        Row(Modifier.fillMaxWidth(.7f), horizontalArrangement = Arrangement.Center) {
            Text(text = text)
        }
    }
}