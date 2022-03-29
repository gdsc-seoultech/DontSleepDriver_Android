package com.comye1.dontsleepdriver.main

import android.content.Context
import android.media.MediaPlayer
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.comye1.dontsleepdriver.R
import com.comye1.dontsleepdriver.data.model.LocalUser

@Composable
fun SoundDialog(
    onDismiss: () -> Unit,
    onOK: (Int) -> Unit,
    selected: Int
) {
    val context = LocalContext.current

    val soundList = listOf(
        Pair("Alarm Tone", R.raw.sound_alarm_tone),
        Pair("Beep Sound", R.raw.sound_beep),
        Pair("Bicycle Bell", R.raw.sound_bicycle),
        Pair("Church Bell", R.raw.sound_church_bell),
        Pair("Emergency", R.raw.sound_emergency),
        Pair("Intruder Alert", R.raw.sound_intruder),
        Pair("Wake Up", R.raw.sound_wake_up),
        Pair("Rooster", R.raw.rooster),
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
                .fillMaxSize(.8f)
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
            Column(modifier = Modifier.weight(1f)) {
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
                    color = Color.Red
                )
                Spacer(modifier = Modifier.width(32.dp))
                Text(
                    text = "OK",
                    modifier = Modifier.clickable {
                        onDismiss()
                        mpStop.value = true
                        onOK(selectedItem.value) // 선택된 음악으로 설정
                    },
                    color = Color.Red
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
    LazyColumn(modifier = Modifier.fillMaxSize()) {
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
    if (stop) {
        player.value.stop()
        player.value.release()
    }
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
                disabledColor = Color.Red
            )
        )
        Spacer(modifier = Modifier.width(32.dp))
        Text(text = soundName)
    }
}

@ExperimentalMaterialApi
@Composable
fun AccountBottomSheetContent(user: LocalUser?, signOut: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(32.dp),
        verticalAlignment = Alignment.CenterVertically,
//        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Text(
            text = user?.email ?: "email",
            style = MaterialTheme.typography.h6,
            modifier = Modifier.fillMaxWidth(.6f)
        )
        Spacer(modifier = Modifier.width(16.dp))
        OutlinedButton(
            onClick = signOut,
            border = BorderStroke(1.dp, Color.Red),
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "Sign Out",
                color = Color.Red
            )
        }
    }
}