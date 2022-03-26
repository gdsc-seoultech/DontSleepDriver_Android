package com.comye1.dontsleepdriver.main

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.comye1.dontsleepdriver.R
import com.comye1.dontsleepdriver.data.model.DrivingBody
import com.comye1.dontsleepdriver.data.model.DrivingResponse
import com.comye1.dontsleepdriver.data.model.LocalUser
import com.comye1.dontsleepdriver.repository.DSDRepository
import com.comye1.dontsleepdriver.util.Resource
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: DSDRepository
) : ViewModel() {

    private val _user: MutableStateFlow<LocalUser?> = MutableStateFlow(null)
    val user: StateFlow<LocalUser?> = _user

    var curTimeText = mutableStateOf("00:00:00")
        private set

    lateinit var startTime: String
    lateinit var endTime: String

    val selectedSound = mutableStateOf(-1)

    val isTracking = mutableStateOf(false)

    val isSaved = mutableStateOf(false)

    val drivingResult = mutableStateOf<DrivingResponse?>(null)

    var warningLevel by mutableStateOf(0)

    val gpsList = mutableListOf<LatLng>()

    val sleepList = mutableListOf<Int>()

    private val eyeStateList = mutableListOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0)

    var putIndex = 0 // 0 1 2 3 4

    fun addEyeState(state: Int) {
        Log.d("eyetracking", "index: $putIndex, state: $state")
        eyeStateList[putIndex++] = state
        putIndex %= 10
    }

    private fun getSleepState(): Int {
        Log.d("eyetracking", eyeStateList.joinToString(" "))
        Log.d("eyetracking", sleepList.joinToString(" "))
        eyeStateList.sum().let {
            Log.d("eyetracking", "sum : $it")
            when {
                it < 3 -> {
                    // 양호
                    warningLevel = 1
                }
//                it < 6 -> {
//                    //
//                }
//                it < 8 -> {
//
//                }
                else -> {
                    warningLevel = 3
                }
            }
            return it
        }
    }

    fun updateList(gps: LatLng) {
        gpsList.add(gps)
        sleepList.add(getSleepState())
    }

    fun saveDriving(totalTime: Long) {
        // 0인 gps 제거
        Log.d("driving", gpsList.joinToString(" "))
        Log.d("driving", sleepList.joinToString(""))
        endTime = LocalDateTime.now().toString()
        viewModelScope.launch {
            repository.postDrivingItem(
                DrivingBody(
                    startTime = startTime,
                    endTime = endTime,
                    totalTime = (totalTime / 1000).toInt(),
                    gpsData = gpsList,
                    gpsLevel = sleepList,
                    avgSleepLevel = sleepList.average()
                )
            ).also {
                when(it) {
                    is Resource.Success -> {
                        drivingResult.value = it.data
                    }
                    is Resource.Error -> {

                    }
                }
            }

        }
        isSaved.value = true
    }


    fun setTrackingState(state: Boolean) {
        isTracking.value = state
        warningLevel = 0
        if (state) {
            if (curTimeText.value == "00:00:00")
                startTime = LocalDateTime.now().toString()
            eyeStateList.fill(0)
        } else {
            eyeStateList.fill(-1)
        }
    }

    fun updateTimeText(newTime: String) {
        curTimeText.value = newTime
    }

    private fun getSelectedSound() {
        if (user.value != null) {
            repository.getSavedSound(user.value!!.email).also {
                selectedSound.value = if (it != -1) it else R.raw.sound_alarm_tone
            }
        } else {
            selectedSound.value = R.raw.sound_alarm_tone
        }
        // R.raw.rooster
    }

    fun saveSound(id: Int) {
        selectedSound.value = id
        user.value?.let { repository.saveSound(id, it.email) }
    }

    init {
        viewModelScope.launch {
            repository.getUser().also {
                when (it) {
                    is Resource.Success -> {
                        it.data?.let { data ->
                            _user.value =
                                LocalUser(
                                    email = data.data!!.email,
                                    id = data.data.id
                                )
                        }

                    }
                    is Resource.Error -> {
                        _user.value =
                            LocalUser(
                                email = "no email",
                                id = -1
                            )
                    }
                }
            }
            getSelectedSound()
        }
    }
}