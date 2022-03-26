package com.comye1.dontsleepdriver.data.model

data class DrivingData(
    val id: Int,
    val startTime: String,
    val endTime: String,
    val avgSleepLevel: Double,
    val totalTime: Int,
    val driverId: Int
)