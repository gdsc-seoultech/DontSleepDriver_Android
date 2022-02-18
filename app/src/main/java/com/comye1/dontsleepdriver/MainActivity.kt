package com.comye1.dontsleepdriver

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.comye1.dontsleepdriver.screens.LogInScreen
import com.comye1.dontsleepdriver.screens.MainScreen
import com.comye1.dontsleepdriver.screens.SignUpScreen
import com.comye1.dontsleepdriver.screens.SplashScreen
import com.comye1.dontsleepdriver.ui.theme.DontSleepDriverTheme

@ExperimentalMaterialApi
@androidx.camera.core.ExperimentalGetImage
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DontSleepDriverTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = "splash"
                ) {
                    composable("splash") {
                        SplashScreen {
                            navController.navigate("log_in"){
                                popUpTo("splash"){
                                    inclusive = true
                                }
                            }
                        }
                    }
                    composable("main") {
                        MainScreen()
                    }
                    composable("log_in") {
                        LogInScreen(
                            toMain = {
                                navController.navigate("main"){
                                    popUpTo("log_in"){
                                        inclusive = true
                                    }
                                }
                            },
                            toSignUp = {
                                navController.navigate("sign_up")
                            }
                        )
                    }
                    composable("sign_up") {
                        SignUpScreen {
                            navController.navigate("log_in"){
                                popUpTo("sign_up"){
                                    inclusive = true
                                }
                            }
                        }
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