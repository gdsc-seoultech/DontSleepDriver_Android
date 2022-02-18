package com.comye1.dontsleepdriver.signin

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.comye1.dontsleepdriver.R

@Composable
fun SignInScreen(
    toMain: () -> Unit,
    toSignUp: () -> Unit,
    viewModel: SignInViewModel = hiltViewModel()
) {
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
            SignInFields(
                email = viewModel.email,
                password = viewModel.password,
                onEmailChange = { viewModel.email = it },
                onPasswordChange = { viewModel.password = it }
            )
            OutlinedButton(
                onClick = {
                    toMain()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Sign in")
            }
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(
                onClick = {
                    toSignUp()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Sign up")
            }
            Spacer(modifier = Modifier.height(16.dp))
            OAuthSignInButton(
                modifier = Modifier.fillMaxWidth(),
                painter = painterResource(id = R.drawable.ic_google_icon),
                size = 24.dp,
                text = "Sign in with Google"
            ) {

            }
            Spacer(modifier = Modifier.height(16.dp))
            OAuthSignInButton(
                modifier = Modifier.fillMaxWidth(),
                painter = painterResource(id = R.drawable.kakao_logo_img),
                size = 24.dp,
                text = "Sign in with Kakao"
            ) {

            }
            Spacer(modifier = Modifier.height(16.dp))
            OAuthSignInButton(
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
fun SignInFields(
    email: String,
    password: String,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit
) {
    Spacer(modifier = Modifier.height(16.dp))
    OutlinedTextField(
        value = email,
        onValueChange = onEmailChange,
        label = { Text(text = "Email") },
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(16.dp))
    OutlinedTextField(
        value = password,
        onValueChange = onPasswordChange,
        label = { Text(text = "Password") },
        modifier = Modifier.fillMaxWidth(),
        visualTransformation = PasswordVisualTransformation()
    )
    Spacer(modifier = Modifier.height(16.dp))
}


@Composable
fun LogoSection() {
    Text(
        text = "Don't Sleep Driver!",
        style = MaterialTheme.typography.h4,
        fontWeight = FontWeight.Bold
    )
    Spacer(modifier = Modifier.height(16.dp))
    Image(
        painter = painterResource(id = R.drawable.driving_img),
        contentDescription = "driving image",
        modifier = Modifier.size(160.dp)
    )
}

@Composable
fun OAuthSignInButton(
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