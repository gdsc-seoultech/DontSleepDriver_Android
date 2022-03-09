package com.comye1.dontsleepdriver.signin

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.comye1.dontsleepdriver.R
import com.comye1.dontsleepdriver.util.getGoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.OAuthLoginCallback
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


@Composable
fun SignInScreen(
    toMain: () -> Unit,
    toSignUp: () -> Unit,
    kakaoSignIn: () -> Unit,
    context: Context,
    viewModel: SignInViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()

    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            Log.d("google", "task : $task")
            try {
                val account = task.getResult(ApiException::class.java)!!
                scope.launch {
                    viewModel.googleSignIn(account.idToken ?: "null")
                    toMain()
                }
            } catch (e: ApiException) {
                scope.launch {
                    viewModel.oAuthFailed()
                }
                Log.d("google", "exception ${e.message}")
            }
        }

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
                    viewModel.signIn { toMain() }
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
                launcher.launch(getGoogleSignInClient(context).signInIntent)
            }
            Spacer(modifier = Modifier.height(16.dp))
            OAuthSignInButton(
                modifier = Modifier.fillMaxWidth(),
                painter = painterResource(id = R.drawable.kakao_logo_img),
                size = 24.dp,
                text = "Sign in with Kakao"
            ) {
                UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
                    if (error != null) {
                        Log.e("kakao", "로그인 실패", error)
                        viewModel.oAuthFailed()
                    } else if (token != null) {
//                        Log.i("kakao", "access token ${token.accessToken}")
//                        Log.i("kakao", "refresh token ${token.refreshToken}")
                        viewModel.kakaoSignIn(token.accessToken) {
                            toMain()
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            OAuthSignInButton(
                modifier = Modifier.fillMaxWidth(),
                painter = painterResource(id = R.drawable.naver_logo_img),
                size = 28.dp,
                text = "Sign in with Naver"
            ) {
                NaverIdLoginSDK.authenticate(context, callback = object : OAuthLoginCallback {
                    override fun onError(errorCode: Int, message: String) {
                        Log.d("naver", "error")
                        viewModel.oAuthFailed()
                    }

                    override fun onFailure(httpStatus: Int, message: String) {
                        Log.d("naver", "failure")
                        viewModel.oAuthFailed()
                    }

                    override fun onSuccess() {
                        NaverIdLoginSDK.getAccessToken()?.let {
                            Log.d("naver", "success")
                            viewModel.naverSignIn(it)
                            toMain()
                        } ?: viewModel.oAuthFailed()
                    }

                })
            }
        }
    }
    LaunchedEffect(true) {
        viewModel.messageFlow.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
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

private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
    try {
        val account: GoogleSignInAccount = completedTask.getResult(ApiException::class.java)
        val idToken = account.idToken

        // TODO(developer): send ID Token to server and validate
        Log.d("googleAuth", idToken ?: "token null")
    } catch (e: ApiException) {
        Log.d("googleAuth", "handleSignInResult:error", e)
    }
}