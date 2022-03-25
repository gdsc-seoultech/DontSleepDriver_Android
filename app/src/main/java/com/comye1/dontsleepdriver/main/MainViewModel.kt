package com.comye1.dontsleepdriver.main

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.comye1.dontsleepdriver.R
import com.comye1.dontsleepdriver.data.model.LocalUser
import com.comye1.dontsleepdriver.repository.DSDRepository
import com.comye1.dontsleepdriver.util.Resource
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: DSDRepository
) : ViewModel() {

    private val _user: MutableStateFlow<LocalUser?> = MutableStateFlow(null)
    val user: StateFlow<LocalUser?> = _user

    var curTimeText = mutableStateOf("00:00:00")
        private set

    val selectedSound = mutableStateOf(-1)

    val isTracking = mutableStateOf(false)

    val isSaved = mutableStateOf(false)

    val gpsList = mutableListOf<LatLng>()

    val sleepList = mutableListOf<Int>()

    fun updateList(gps: LatLng, level: Int) {
        gpsList.add(gps)
        sleepList.add(level)
    }

    fun saveDriving() {
        // 0인 gps 제거
        Log.d("driving", gpsList.joinToString(" "))
        Log.d("driving", sleepList.joinToString(""))
        isSaved.value = true
    }



    fun setTrackingState(state: Boolean){
        isTracking.value = state
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
                                    id = data.data.id ?: -1
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