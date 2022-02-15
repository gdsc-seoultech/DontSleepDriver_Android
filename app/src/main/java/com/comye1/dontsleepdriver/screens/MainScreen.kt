package com.comye1.dontsleepdriver.screens

import android.content.Context
import android.media.MediaPlayer
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.comye1.dontsleepdriver.R
import com.comye1.dontsleepdriver.ui.theme.Purple500

@Composable
fun MainScreen() {

    val context = LocalContext.current

    val (soundDialogShown, showSoundDialog) = remember {
        mutableStateOf(false)
    }

    val selectedSound = remember {
        mutableStateOf(R.raw.rooster)
    }

    Scaffold(
        bottomBar = {
            BottomAppBar() {
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = "Driving History"
                    )
                }
                IconButton(onClick = { showSoundDialog(true) }) {
                    Icon(imageVector = Icons.Default.MusicNote, contentDescription = "Ring Tone")
                }
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(imageVector = Icons.Default.PersonOutline, contentDescription = "Account")
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
                Text(text = "START", fontSize = 32.sp, fontWeight = FontWeight.Bold)
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

        }
        if (soundDialogShown) {
            SoundDialog(
                onDismiss = { showSoundDialog(false) },
                onOK = { selectedSound.value = it },
                selected = selectedSound.value
            )
        }
        Text(text = selectedSound.value.toString())
    }

}

@Composable
fun SoundDialog(
    onDismiss: () -> Unit,
    onOK: (Int) -> Unit,
    selected: Int
) {
    val context = LocalContext.current

    val soundList = listOf(
        Pair("rooster", R.raw.rooster),
        Pair("rooster-morning", R.raw.rooster_morning)
    )

    val selectedItem = remember {
        mutableStateOf(selected)
    }

    val mpStop = remember {
        mutableStateOf(false)
    }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxSize(.7f)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White)
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Select your alert sound")
            }
            Divider()
            Row(modifier = Modifier.weight(1f)) {
                SoundsRadioGroup(
                    list = soundList,
                    selectedItem = selectedItem,
                    context = context,
                    stop = mpStop.value
                )
            }
            Divider()
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "Cancel",
                    modifier = Modifier.clickable {
                        onDismiss()
                        mpStop.value = true
                    },
                    color = Purple500
                )
                Spacer(modifier = Modifier.width(32.dp))
                Text(
                    text = "OK",
                    modifier = Modifier.clickable {
                        onDismiss()
                        mpStop.value = true
                        onOK(selectedItem.value) // 선택된 음악으로 설정
                    },
                    color = Purple500
                )
                Spacer(modifier = Modifier.width(16.dp))
            }
        }
    }
}

@Composable
fun SoundsRadioGroup(
    list: List<Pair<String, Int>>,
    selectedItem: MutableState<Int>,
    context: Context,
    stop: Boolean
) {

    val player = remember {
        mutableStateOf(MediaPlayer.create(context, selectedItem.value))
    }

    LazyColumn {
        items(list) {
            SoundRadioRow(soundName = it.first, selected = it.second == selectedItem.value) {
                if (selectedItem.value != it.second) {
                    selectedItem.value = it.second
                    player.value.stop()
                    player.value = MediaPlayer.create(context, it.second)
                    player.value.start()
                } else {
                    player.value.start()
                }
            }
        }
    }
    if (stop) player.value.stop()
}

@Composable
fun SoundRadioRow(
    soundName: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp)) {
        RadioButton(
            selected = selected,
            enabled = false,
            onClick = {},
            colors = RadioButtonDefaults.colors(
                disabledColor = Purple500
            )
        )
        Spacer(modifier = Modifier.width(32.dp))
        Text(text = soundName)
    }
}