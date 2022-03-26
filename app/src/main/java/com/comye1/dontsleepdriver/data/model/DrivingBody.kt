package com.comye1.dontsleepdriver.data.model

data class DrivingBody(
    val startTime: String,
    val endTime: String,
    val gpsData: List<Location>,
    val gpsLevel: List<Int>,
    val avgSleepLevel: Double,
    val totalTime: Int
)