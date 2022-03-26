package com.comye1.dontsleepdriver.data.model

import com.google.android.gms.maps.model.LatLng

data class DrivingBody(
    val startTime: String,
    val endTime: String,
    val totalTime: Int,
    val gpsData: List<LatLng>,
    val gpsLevel: List<Int>,
    val avgSleepLevel: Double
)
