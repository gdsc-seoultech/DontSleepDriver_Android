package com.comye1.dontsleepdriver.history

import androidx.lifecycle.ViewModel
import com.comye1.dontsleepdriver.data.model.Driving
import com.comye1.dontsleepdriver.repository.DSDRepository
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repository: DSDRepository
) : ViewModel() {

    val drivingList = listOf(
        Driving(
            startTime = 1646541190226,
            endTime = 1646541190226,
            gpsData = listOf(
                LatLng(37.625500, 127.079877),
                LatLng(37.627699, 127.081415),
                LatLng(37.627962, 127.084926),
                LatLng(37.626305, 127.085760),
                LatLng(37.626305, 127.085760),
                LatLng(37.626305, 127.085760),
                LatLng(37.626305, 127.085760),
                LatLng(37.622602, 127.086884),
                LatLng(37.620631, 127.087520),
                LatLng(37.618024, 127.091351),
                LatLng(37.616212, 127.100279),
                LatLng(37.614720, 127.105728)
            ),
            sleepData = listOf(0, 4, 1, 2, -1, -1, -1, 0, 0, 1, 2, 0)
        )
    )
}