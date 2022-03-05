package com.comye1.dontsleepdriver.history

import androidx.lifecycle.ViewModel
import com.comye1.dontsleepdriver.data.model.Driving
import com.comye1.dontsleepdriver.data.model.LatLngSlp
import com.comye1.dontsleepdriver.repository.DSDRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repository: DSDRepository
) : ViewModel() {

    val drivingList = listOf(
        Driving(
            startTime = "2022-02-27 20:49:19",
            endTime = "2022-02-27 20:58:43",
            gpsData = listOf(
                LatLngSlp(37.625500, 127.079877, 0),
                LatLngSlp(37.627699, 127.081415, 4),
                LatLngSlp(37.627962, 127.084926, 1),
                LatLngSlp(37.626305, 127.085760, 2),
                LatLngSlp(37.622602, 127.086884, 0),
                LatLngSlp(37.620631, 127.087520, 0),
                LatLngSlp(37.618024, 127.091351, 1),
                LatLngSlp(37.616212, 127.100279, 2),
                LatLngSlp(37.614720, 127.105728, 0)
            )
        )
    )
}