package com.comye1.dontsleepdriver

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.Toast
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
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.comye1.dontsleepdriver.history.HistoryScreen
import com.comye1.dontsleepdriver.main.AccountBottomSheetContent
import com.comye1.dontsleepdriver.main.CameraView
import com.comye1.dontsleepdriver.main.MainViewModel
import com.comye1.dontsleepdriver.main.SoundDialog
import com.comye1.dontsleepdriver.other.Constants.ACTION_PAUSE_SERVICE
import com.comye1.dontsleepdriver.other.Constants.ACTION_SHOW_DSD_ACTIVITY
import com.comye1.dontsleepdriver.other.Constants.ACTION_START_OR_RESUME_SERVICE
import com.comye1.dontsleepdriver.other.Constants.ACTION_STOP_SERVICE
import com.comye1.dontsleepdriver.service.TrackingService
import com.comye1.dontsleepdriver.ui.theme.DontSleepDriverTheme
import com.comye1.dontsleepdriver.ui.theme.Purple500
import com.comye1.dontsleepdriver.util.TrackingUtility
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DSDActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()
    private var curTimeInMillis = 0L

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        subscribeToObservers(viewModel::setTrackingState)
        return super.onCreateView(name, context, attrs)
    }

    @ExperimentalMaterialApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            val navController = rememberNavController()


            NavHost(navController = navController, startDestination = "dsd_main") {
                composable("dsd_history") {
                    HistoryScreen({ navController.popBackStack() })
                }
                composable("dsd_main") {
                    val (exitDialogShown, showExitDialog) = remember {
                        mutableStateOf(false)
                    }
                    val context = LocalContext.current

                    val user = viewModel.user.collectAsState()

                    val (soundDialogShown, showSoundDialog) = remember {
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
//                            setDrivingState(true) // 버튼 상태 변경
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
                                ) {
                                    Column(
                                        Modifier
                                            .fillMaxSize()
                                            .padding(16.dp)
                                    ) {
                                        if (viewModel.isSaved.value) {
                                            Text(text = "gps : ${viewModel.gpsList.joinToString("\n")}")

                                            Text(text = "eye : ${viewModel.sleepList.joinToString(" ")}")
                                        }else {
                                            if (!viewModel.isTracking.value){
                                                Spacer(modifier = Modifier.height(100.dp))
                                                Text(text = "Start Driving!", fontSize = 64.sp)
                                            }
                                            Spacer(modifier = Modifier.height(32.dp))
                                            Text(
                                                text = "Total Driving Time : ${viewModel.curTimeText.value}",
                                                fontSize = 24.sp,
                                                textAlign = TextAlign.Center
                                            )
                                            Box(
                                                Modifier.fillMaxSize()
                                            ) {
                                                if (viewModel.isTracking.value)
                                                    CameraView()
                                                Row(
                                                    Modifier
                                                        .fillMaxSize()
                                                        .padding(bottom = 100.dp),
                                                    verticalAlignment = Alignment.Bottom,
                                                    horizontalArrangement = Arrangement.SpaceAround
                                                ) {
                                                    Button(
                                                        onClick = {
                                                            sendCommandToService(
                                                                ACTION_START_OR_RESUME_SERVICE
                                                            )
                                                        },
                                                        enabled = !viewModel.isTracking.value
                                                    ) {
                                                        Text(text = "START", fontSize = 24.sp)
                                                    }
                                                    Button(
                                                        onClick = {
                                                            sendCommandToService(ACTION_PAUSE_SERVICE) // TrackingService 시작
                                                        },
                                                        enabled = viewModel.isTracking.value
                                                    ) {
                                                        Text(text = "STOP", fontSize = 24.sp)
                                                    }
                                                    Button(
                                                        onClick = {
                                                            if (viewModel.isTracking.value) {
                                                                // 중지시킴
                                                                sendCommandToService(
                                                                    ACTION_PAUSE_SERVICE
                                                                ) // TrackingService 시작
                                                            }
                                                            // 저장하기
                                                            viewModel.saveDriving()
                                                            // 서비스 종료하기
                                                            sendCommandToService(ACTION_STOP_SERVICE)
                                                        },
                                                        enabled = curTimeInMillis != 0L
                                                    ) {
                                                        Text(text = "SAVE", fontSize = 24.sp)
                                                    }
                                                }
                                            }
                                        }
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

    private fun subscribeToObservers(setTrackingState: (Boolean) -> Unit) {

        TrackingService.isTracking.observe(this) {
            Log.d("isTracking", it.toString())
            setTrackingState(it)
        }

        TrackingService.timeDrivingInMillis.observe(this) {
            curTimeInMillis = it
            val formattedTime = TrackingUtility.getFormattedStopWatchTime(curTimeInMillis)
            viewModel.updateTimeText(formattedTime)
        }

        TrackingService.previousLocation.observe(this) {
            // 뷰모델의 리스트에 저장
            if (it.latitude != -1.0)
                viewModel.updateList(it, 0)
        }

        TrackingService.alwaysPermissionRequest.observe(this) {
            if (it) {
                Toast.makeText(this, "Allow Location permission all the time", Toast.LENGTH_SHORT)
                    .show()
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

