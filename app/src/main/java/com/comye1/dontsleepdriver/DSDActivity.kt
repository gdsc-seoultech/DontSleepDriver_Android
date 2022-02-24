package com.comye1.dontsleepdriver

import android.os.Bundle
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.comye1.dontsleepdriver.main.AccountBottomSheetContent
import com.comye1.dontsleepdriver.main.CameraView
import com.comye1.dontsleepdriver.main.MainViewModel
import com.comye1.dontsleepdriver.main.SoundDialog
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
                                    IconButton(onClick = { /*TODO*/ }) {
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
                                    onClick = { },
                                    modifier = Modifier
                                        .offset(y = 48.dp)
                                        .size(108.dp),
                                    backgroundColor = Color.Black,
                                    contentColor = Color.White
                                ) {
//                Icon(
//                    imageVector = Icons.Default.PlayArrow,
//                    contentDescription = "start",
//                    Modifier.size(48.dp)
//                )
                                    Text(
                                        text = "START",
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.Bold
                                    )
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

