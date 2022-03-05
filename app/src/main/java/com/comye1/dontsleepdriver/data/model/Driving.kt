package com.comye1.dontsleepdriver.data.model

data class Driving(
    val startTime: String,
    val endTime: String,
    val gpsData: List<LatLngSlp>,

)
