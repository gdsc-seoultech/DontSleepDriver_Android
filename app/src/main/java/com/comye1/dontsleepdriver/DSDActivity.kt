package com.comye1.dontsleepdriver

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.comye1.dontsleepdriver.history.HistoryScreen
import com.comye1.dontsleepdriver.main.AccountBottomSheetContent
import com.comye1.dontsleepdriver.main.CameraView
import com.comye1.dontsleepdriver.main.MainViewModel
import com.comye1.dontsleepdriver.main.SoundDialog
import com.comye1.dontsleepdriver.other.Constants.ACTION_SHOW_DSD_ACTIVITY
import com.comye1.dontsleepdriver.other.Constants.ACTION_START_OR_RESUME_SERVICE
import com.comye1.dontsleepdriver.service.TrackingService
import com.comye1.dontsleepdriver.ui.theme.DontSleepDriverTheme
import com.comye1.dontsleepdriver.ui.theme.Purple500
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DSDActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    @ExperimentalMaterialApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            val navController = rememberNavController()

            NavHost(navController = navController, startDestination = "dsd_main"){
                composable("dsd_history"){
                    HistoryScreen({ navController.popBackStack() })
                }
                composable("dsd_main"){
                    val (exitDialogShown, showExitDialog) = remember {
                        mutableStateOf(false)
                    }
                    val context = LocalContext.current

                    val user = viewModel.user.collectAsState()

                    val (soundDialogShown, showSoundDialog) = remember {
                        mutableStateOf(false)
                    }

                    // 운전 시작, 정지 제어
                    val (drivingState, setDrivingState) = remember {
                        mutableStateOf(false)
                    }

                    val selectedSound = remember {
                        // 뷰모델, repository에서 가져와야 함
                        viewModel.selectedSound
                    }

                    val modalBottomSheetState =
                        rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)

                    val scope = rememberCoroutineScope()

                    // Notification을 통해 시작된 경우
                    if (intent.action == ACTION_SHOW_DSD_ACTIVITY) {
                        Log.d("Tracking", "Pending Intent")
                        scope.launch {
                            setDrivingState(true) // 버튼 상태 변경
                            // 추후 notification에서의 동작으로 정지, 중지 시키면 판단 필요..
                        }
                    }

                    DontSleepDriverTheme {
                        // A surface container using the 'background' color from the theme
                        Surface(color = MaterialTheme.colors.background) {

                            ModalBottomSheetLayout(
                                sheetContent = {
                                    AccountBottomSheetContent(user.value) {
                                        scope.launch {
                                            modalBottomSheetState.hide()
                                        }
                                    }
                                },
                                sheetState = modalBottomSheetState,
                                sheetShape = RoundedCornerShape(8.dp, 8.dp, 0.dp, 0.dp)
                            ) {
                                Scaffold(
                                    bottomBar = {
                                        BottomAppBar {
                                            IconButton(onClick = { showExitDialog(true) }) {
                                                Icon(
                                                    imageVector = Icons.Default.ExitToApp,
                                                    contentDescription = "Exit this app"
                                                )
                                            }
                                            Spacer(modifier = Modifier.weight(1f))
                                            IconButton(onClick = { navController.navigate("dsd_history") }) {
                                                Icon(
                                                    imageVector = Icons.Default.History,
                                                    contentDescription = "Driving History"
                                                )
                                            }
                                            IconButton(onClick = { showSoundDialog(true) }) {
                                                Icon(
                                                    imageVector = Icons.Default.MusicNote,
                                                    contentDescription = "Ring Tone"
                                                )
                                            }
                                            IconButton(onClick = {
                                                scope.launch {
                                                    modalBottomSheetState.show()
                                                }
                                            }) {
                                                Icon(
                                                    imageVector = Icons.Default.PersonOutline,
                                                    contentDescription = "Account"
                                                )
                                            }
                                        }
                                    },
                                    floatingActionButton = {
                                        FloatingActionButton(
                                            onClick = {
                                                if (drivingState) {
                                                    setDrivingState(false)
                                                } else {
                                                    setDrivingState(true)
                                                    sendCommandToService(ACTION_START_OR_RESUME_SERVICE) // TrackingService 시작
                                                }
                                            },
                                            modifier = Modifier
                                                .offset(y = 48.dp)
                                                .size(108.dp),
                                            backgroundColor = Color.Black,
                                            contentColor = Color.White
                                        ) {
                                            if (drivingState) {
                                                Icon(
                                                    imageVector = Icons.Default.Stop,
                                                    contentDescription = "stop",
                                                    Modifier.size(48.dp)
                                                )
                                            } else {
                                                Icon(
                                                    imageVector = Icons.Default.PlayArrow,
                                                    contentDescription = "start",
                                                    Modifier.size(48.dp)
                                                )
                                            }
                                        }
                                    },
                                    floatingActionButtonPosition = FabPosition.Center
                                ) {
                                    Column(
                                        Modifier
                                            .fillMaxSize()
                                            .padding(16.dp)
                                    ) {
                                        Text(text = "Main Screen")
                                        Text(text = selectedSound.value.toString())
                                        CameraView()
                                    }
                                    if (soundDialogShown) {
                                        SoundDialog(
                                            onDismiss = { showSoundDialog(false) },
                                            onOK = {
                                                selectedSound.value = it
                                                // 뷰모델 통해 repository에 저장
                                                viewModel.saveSound(it)
                                            },
                                            selected = selectedSound.value
                                        )
                                    }
                                }
                            }
                        }
                    }

                    /*
                    뒤로가기 처리 - 다이얼로그 띄우기
                     */
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

    // 서비스 호출
    private fun sendCommandToService(action: String) =
        Intent(applicationContext, TrackingService::class.java).also {
            it.action = action
            applicationContext.startService(it)
        }
}

@Composable
fun ExitDialog(onDismiss: () -> Unit, onYes: () -> Unit) {
    Dialog(onDismissRequest = { onDismiss() }) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White)
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Do you really want to exit?", style = MaterialTheme.typography.h6)
            }
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "Cancel",
                    modifier = Modifier
                        .clickable {
                            onDismiss()
                        }
                        .padding(8.dp),
                    color = Purple500, style = MaterialTheme.typography.h6
                )
                Spacer(modifier = Modifier.width(32.dp))
                Text(
                    text = "Yes",
                    modifier = Modifier
                        .clickable {
                            onYes()
                        }
                        .padding(8.dp),
                    color = Purple500, style = MaterialTheme.typography.h6
                )
                Spacer(modifier = Modifier.width(16.dp))
            }
        }
    }
}

