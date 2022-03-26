package com.comye1.dontsleepdriver.data.model

data class DrivingResponse(
    val id: Int,
    val startTime: String,
    val endTime: String,
    val totalTime: Int,
    val gpsData: List<Location>,
    val gpsLevel: List<Int>,
    val avgSleepLevel: Double
)