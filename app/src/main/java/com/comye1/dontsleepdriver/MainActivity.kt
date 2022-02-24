package com.comye1.dontsleepdriver

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.comye1.dontsleepdriver.main.MainScreen
import com.comye1.dontsleepdriver.screens.SplashScreen
import com.comye1.dontsleepdriver.signin.SignInScreen
import com.comye1.dontsleepdriver.signup.SignUpScreen
import com.comye1.dontsleepdriver.ui.theme.DontSleepDriverTheme
import com.kakao.sdk.user.UserApiClient
import dagger.hilt.android.AndroidEntryPoint

@ExperimentalMaterialApi
@androidx.camera.core.ExperimentalGetImage
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var dsdIntent: Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dsdIntent = Intent().apply {
            setClass(applicationContext, DSDActivity::class.java)
        }

        setContent {

            val (exitDialogShown, showExitDialog) = remember {
                mutableStateOf(false)
            }

            DontSleepDriverTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = "splash"
                ) {
                    composable("splash") {
                        SplashScreen {
                            navController.navigate("sign_in") {
                                popUpTo("splash") {
                                    inclusive = true
                                }
                            }
                        }
                    }
                    composable("sign_in") {
                        requestForegroundPermission(this@MainActivity)
                        SignInScreen(
                            toMain = {
                                startActivity(dsdIntent)
                            },
                            toSignUp = {
                                navController.navigate("sign_up")
                            },
                            kakaoSignIn = {
                                UserApiClient.instance.loginWithKakaoTalk(this@MainActivity) { token, error ->
                                    if (error != null) {
                                        Log.e("kakao", "로그인 실패", error)
                                    } else if (token != null) {
                                        Log.i("kakao", "access token ${token.accessToken}")
                                        Log.i("kakao", "refresh token ${token.refreshToken}")
                                        startActivity(dsdIntent)
                                    }
                                }
                            }
                        )
                    }
                    composable("sign_up") {
                        SignUpScreen(toLogIn = {
                            navController.navigate("sign_in") {
                                popUpTo("sign_up") {
                                    inclusive = true
                                }
                            }
                        })
                    }
                }
                BackHandler(
                    onBack = {
                        showExitDialog(true)
                    }
                )
                if (exitDialogShown) {
                    ExitDialog(onDismiss = { showExitDialog(false) }) {
                        finishAffinity()
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    DontSleepDriverTheme {
        Greeting("Android")
    }
}

private const val REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST = 34
private fun foregroundPermissionApproved(context: Context): Boolean {
    return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
        context, Manifest.permission.CAMERA
    )
}

private fun requestForegroundPermission(context: Context) {
    val provideRationale = foregroundPermissionApproved(context)

    if (provideRationale) {
        ActivityCompat.requestPermissions(
            context as Activity,
            arrayOf(Manifest.permission.CAMERA), REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST
        )
    } else {
        ActivityCompat.requestPermissions(
            context as Activity,
            arrayOf(Manifest.permission.CAMERA), REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST
        )
    }
}