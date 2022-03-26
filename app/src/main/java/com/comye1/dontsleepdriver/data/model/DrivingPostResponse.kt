package com.comye1.dontsleepdriver.data.model

data class DrivingPostResponse(
    val success: Boolean,
    val message: String,
    val data: DrivingResponse?,
    val error: String
)
