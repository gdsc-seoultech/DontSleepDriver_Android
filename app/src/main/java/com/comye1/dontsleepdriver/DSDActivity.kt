package com.comye1.dontsleepdriver

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.MutableLiveData
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.comye1.dontsleepdriver.data.model.DrivingResponse
import com.comye1.dontsleepdriver.history.HistoryDetailScreen
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
import com.comye1.dontsleepdriver.ui.LoadingAnimation
import com.comye1.dontsleepdriver.ui.theme.Black
import com.comye1.dontsleepdriver.ui.theme.DontSleepDriverTheme
import com.comye1.dontsleepdriver.util.TrackingUtility
import com.comye1.dontsleepdriver.util.getColorByLevel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DSDActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()
    private var curTimeInMillis = 0L
    private lateinit var player: MediaPlayer

    var thirtyAlarm = MutableLiveData(false)
    var twoAlarm = MutableLiveData(false)

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        subscribeToObservers(viewModel::setTrackingState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        return super.onCreateView(name, context, attrs)
    }

    override fun onDestroy() {
        player.release()
        super.onDestroy()
    }

    @ExperimentalMaterialApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            window.statusBarColor = Black.toArgb()

            val navController = rememberNavController()
            player = MediaPlayer.create(this, viewModel.selectedSound.value)
//            warningSound = Uri.parse("android.resource://" + this.packageName + "/" + viewModel.selectedSound.value)
//            twoSound = Uri.parse("android.resource://" + this.packageName + "/" + R.raw.voice_2_hours)
//            thirtySound = Uri.parse("android.resource://" + this.packageName + "/" + R.raw.voice_30_minutes)


            DontSleepDriverTheme {


                NavHost(navController = navController, startDestination = "dsd_main") {
                    composable("dsd_history") {
                        HistoryScreen({ navController.popBackStack() })
                    }

                    composable("dsd_result") {
                        viewModel.getDrivingItem()
                        ResultScreen({ navController.popBackStack() }, viewModel.drivingResult)
                    }

                    composable("dsd_main") {
                        val (exitDialogShown, showExitDialog) = remember {
                            mutableStateOf(false)
                        }

                        val warningState = remember{
                            mutableStateOf(false)
                        }

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
                                            IconButton(
                                                onClick = { showExitDialog(true) },
                                                enabled = !viewModel.isTracking.value
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.ExitToApp,
                                                    contentDescription = "Exit this app"
                                                )
                                            }
                                            Spacer(modifier = Modifier.weight(1f))
                                            IconButton(
                                                onClick = { navController.navigate("dsd_history") },
                                                enabled = !viewModel.isTracking.value
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.History,
                                                    contentDescription = "Driving History"
                                                )
                                            }
                                            IconButton(
                                                onClick = { showSoundDialog(true) },
                                                enabled = !viewModel.isTracking.value
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.MusicNote,
                                                    contentDescription = "Ring Tone"
                                                )
                                            }
                                            IconButton(onClick = {
                                                scope.launch {
                                                    modalBottomSheetState.show()
                                                }
                                            }, enabled = !viewModel.isTracking.value) {
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
                                            .background(
                                                getColorByLevel(viewModel.warningLevel)
                                            )
                                            .padding(16.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {

                                        if (!viewModel.isTracking.value) {
                                            Spacer(modifier = Modifier.height(100.dp))
                                            Text(
                                                text = "Safe Driving!",
                                                style = MaterialTheme.typography.h4
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(32.dp))
                                        if (!viewModel.notStartedYet) {
                                            Text(
                                                text = "Total Driving Time",
                                                style = MaterialTheme.typography.h6
                                            )
                                            Text(
                                                text = viewModel.curTimeText.value,
                                                style = MaterialTheme.typography.h6
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(32.dp))
                                        if (viewModel.isTracking.value)
                                            CameraView(viewModel::addEyeState)
                                        else
                                            Image(
                                                painter = painterResource(id = R.drawable.driving_img),
                                                contentDescription = "image",
                                                modifier = Modifier.fillMaxWidth(.7f)
                                            )

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
                                                Text(
                                                    text = if (viewModel.notStartedYet) "START" else "RESUME",
                                                    style = MaterialTheme.typography.h6
                                                )
                                            }
                                            Button(
                                                onClick = {
                                                    sendCommandToService(
                                                        ACTION_PAUSE_SERVICE
                                                    ) // TrackingService 중단
                                                    player.stop()
                                                },
                                                enabled = viewModel.isTracking.value
                                            ) {
                                                Text(
                                                    text = "STOP",
                                                    style = MaterialTheme.typography.h6
                                                )
                                            }
                                            Button(
                                                onClick = {
                                                    if (viewModel.isTracking.value) {
                                                        // 중지시킴
                                                        sendCommandToService(
                                                            ACTION_PAUSE_SERVICE
                                                        ) // TrackingService 시작
                                                        player.stop()
                                                    }
                                                    // 저장하기
                                                    Log.d("repo repo", "save driving")
                                                    viewModel.saveDriving(curTimeInMillis) {
                                                        navController.navigate("dsd_result")
                                                    }
                                                    // 서비스 종료하기
                                                    sendCommandToService(ACTION_STOP_SERVICE)
                                                },
                                                enabled = curTimeInMillis != 0L
                                            ) {
                                                Text(
                                                    text = "SAVE",
                                                    style = MaterialTheme.typography.h6
                                                )
                                            }
                                        }
                                    }

                                    warningState.value = viewModel.warningLevel > 2
//
//            //                                        thirtyState.value = !warningState.value && (curTimeInMillis > 5000L)                        if (!thirtyStatePlayed.value){
//                                    }
//                                    if (!twoStatePlayed.value) {
//                                        twoState.value = !warningState.value && (curTimeInMillis > 10000L) //7200000L
//                                    }
//
                                    if (warningState.value) {
                                        if (!player.isPlaying) {
                                            stopPlayer()
                                            playWarningSound()
                                        }
                                    } else if (!warningState.value) {
                                        stopPlayer()
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
                                // 서비스 종료하기
                                sendCommandToService(ACTION_STOP_SERVICE)
                                finishAffinity()
                            }
                        }
                    }
                }

            }
        }
    }

    private fun stopPlayer() {
        player.stop()
        player.reset()
    }

    private fun playWarningSound() {
        player = MediaPlayer.create(this, viewModel.selectedSound.value)
        player.start()
    }

    private fun playThirtySound(onComplete: () -> Unit) {
//        onComplete()
        player = MediaPlayer.create(this, R.raw.voice_30_minutes)
        player.start()
//        player.setOnCompletionListener {
//            onComplete()
//        }
    }

    private fun playTwoSound(onComplete: () -> Unit) {
//        onComplete()
        player = MediaPlayer.create(this, R.raw.voice_2_hours)
        player.start()

    }

    private fun subscribeToObservers(setTrackingState: (Boolean) -> Unit) {

        TrackingService.isTracking.observe(this) {
            Log.d("isTracking", it.toString())
            setTrackingState(it)
        }

        TrackingService.timeDrivingInMillis.observe(this) {
            curTimeInMillis = it
//            Log.d("alert", curTimeInMillis.toString())
//            if (it > 5000L && ) {
//                Log.d("alert", "5000L")
//
//            }else if (it > 10000L) {
//                Log.d("alert", "10000L")
//            }
            val formattedTime = TrackingUtility.getFormattedStopWatchTime(curTimeInMillis)
            viewModel.updateTimeText(formattedTime)
        }

        TrackingService.previousLocation.observe(this) {
            // 뷰모델의 리스트에 저장
            if (it.latitude != -1.0)
                viewModel.updateList(it)
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
fun ResultScreen(exit: () -> Boolean, drivingResult: MutableState<DrivingResponse?>) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Driving Result") },
                navigationIcon = {
                    IconButton(onClick = { exit() }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "navigate back"
                        )
                    }
                })
        },
    ) {
        if (drivingResult.value == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                LoadingAnimation()
            }
        } else {
            HistoryDetailScreen(drivingResponse = drivingResult.value)
        }
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
                    color = Color.Red, style = MaterialTheme.typography.h6
                )
                Spacer(modifier = Modifier.width(32.dp))
                Text(
                    text = "Yes",
                    modifier = Modifier
                        .clickable {
                            onYes()
                        }
                        .padding(8.dp),
                    color = Color.Red, style = MaterialTheme.typography.h6
                )
                Spacer(modifier = Modifier.width(16.dp))
            }
        }
    }
}

