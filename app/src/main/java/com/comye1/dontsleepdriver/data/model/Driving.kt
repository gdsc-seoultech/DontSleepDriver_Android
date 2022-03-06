package com.comye1.dontsleepdriver.data.model

import com.google.android.gms.maps.model.LatLng

data class Driving(
    val startTime: Long,
    val endTime: Long,
    val gpsData: List<LatLng>,
    val sleepData: List<Int>
)
