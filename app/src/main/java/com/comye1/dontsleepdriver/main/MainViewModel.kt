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
import com.comye1.dontsleepdriver.data.model.Location
import com.comye1.dontsleepdriver.repository.DSDRepository
import com.comye1.dontsleepdriver.util.Resource
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
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

    val notStartedYet
        get() = curTimeText.value == "00:00:00"

    val selectedSound = mutableStateOf(-1)

    val isTracking = mutableStateOf(false)

    val savedId = mutableStateOf(0)

    val drivingResult = mutableStateOf<DrivingResponse?>(null)

    var warningLevel by mutableStateOf(-1)

    private val gpsList = mutableListOf<Location>()

    private val sleepList = mutableListOf<Int>()

    private val eyeStateList = mutableListOf(0, 0, 0, 0, 0, 0, 0)

    var putIndex = 0 // 0 1 2 3 4

    fun addEyeState(state: Int) {
        Log.d("eyetracking", "index: $putIndex, state: $state")
        eyeStateList[putIndex++] = state
        warningLevel = eyeStateList.sum()
        putIndex %= 7
    }

    fun getDrivingItem() {
        viewModelScope.launch {
            repository.getDrivingItem(savedId.value).also {
                when (it) {
                    is Resource.Success -> {
                        drivingResult.value = it.data
                    }
                    is Resource.Error -> {
                        // 에러
                    }
                }
            }
        }
    }

    private fun getCurrentTimeString(): String =
        LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd kk:mm:ss"))

    fun updateList(gps: LatLng) {
        gpsList.add(Location(lng = gps.longitude, lat = gps.latitude))
        sleepList.add(warningLevel)
    }

    fun saveDriving(totalTime: Long, onComplete: () -> Unit) {
        // 0인 gps 제거
        Log.d("driving", gpsList.joinToString(" "))
        Log.d("driving", sleepList.joinToString(""))
        eyeStateList.fill(-1)
        warningLevel = -1
        endTime = getCurrentTimeString()
//        viewModelScope.launch {
        repository.postDrivingItem(
            DrivingBody(
                startTime = startTime,
                endTime = endTime,
                totalTime = (totalTime / 1000).toInt(),
                gpsData = gpsList,
                gpsLevel = sleepList,
                avgSleepLevel = sleepList.average()
            ),
            savedId
        ).also {
            onComplete()
        }
//        }
    }


    fun setTrackingState(state: Boolean) {
        isTracking.value = state
        if (notStartedYet)
            startTime = getCurrentTimeString()
        if (state) { //true
            warningLevel = 0
            eyeStateList.fill(0)
        }else {
            warningLevel = -1
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
        getSelectedSound()
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
        }
    }
}