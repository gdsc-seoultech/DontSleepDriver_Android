package com.comye1.dontsleepdriver

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.comye1.dontsleepdriver.screens.SplashScreen
import com.comye1.dontsleepdriver.signin.SignInScreen
import com.comye1.dontsleepdriver.signup.SignUpScreen
import com.comye1.dontsleepdriver.ui.theme.Black
import com.comye1.dontsleepdriver.ui.theme.DontSleepDriverTheme
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
            window.statusBarColor = Black.toArgb()

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
                            context = this@MainActivity
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
                /*
                뒤로가기 동작 처리
                 */
                BackHandler(
                    onBack = {
                        showExitDialog(true)
                    }
                )
                /*
                종료 다이얼로그
                 */
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
    ) && PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
        context, Manifest.permission.INTERNET
    ) && PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_BACKGROUND_LOCATION
    ) && PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_FINE_LOCATION
    ) && PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_COARSE_LOCATION
    )
}

private fun requestForegroundPermission(context: Context) {
    val provideRationale = foregroundPermissionApproved(context)

    if (provideRationale) {
        ActivityCompat.requestPermissions(
            context as Activity,
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST
        )

        if (Build.VERSION.SDK_INT >= 29) {
            ActivityCompat.requestPermissions(
                context,
                arrayOf(
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                ),
                REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST
            )
        }
    } else {
        ActivityCompat.requestPermissions(
            context as Activity,
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.INTERNET,
//                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST
        )
    }
}