package com.comye1.dontsleepdriver.signup

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun SignUpScreen(
    toLogIn: () -> Unit,
    viewModel: SignUpViewModel = hiltViewModel()
) {
    val subNavController = rememberNavController()

    NavHost(navController = subNavController, startDestination = "email") {
        composable("email") {
            SignUpContainer {
                SignUpEmailScreen(
                    email = viewModel.email,
                    setEmail = { viewModel.email = it },
                    requestEmailVerification = { true },
                    verifyEmailCode = { true }
                ) {
                    // next
                    subNavController.navigate("user_name")
                }
            }
        }

        composable("user_name"){
            SignUpContainer {
                SignUpUserNameScreen(
                    userName = viewModel.userName,
                    setUserName = { viewModel.userName = it},
                ){
                    subNavController.navigate("password")
                }
            }
        }

        composable("password"){
            SignUpContainer {
                SignUpPasswordScreen(
                    password = viewModel.password,
                    setPassword = { viewModel.password = it}
                ){
                    // create
                    viewModel.signUp()
                    toLogIn()
                }
            }
        }

    }

}

@Composable
fun SignUpContainer(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(.8f),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
//            LogoSection()
//            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Sign Up",
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.h3
            )

            Spacer(modifier = Modifier.height(16.dp))
            content()
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

@Composable
fun SignUpDialogText(text: String) {
    Text(text = text)
    Spacer(modifier = Modifier.height(32.dp))
}

@Composable
fun SignUpDialogButton(onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = "확인")
    }
}