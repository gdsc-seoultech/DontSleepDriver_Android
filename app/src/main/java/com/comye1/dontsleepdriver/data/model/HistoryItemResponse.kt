package com.comye1.dontsleepdriver.data.model

data class HistoryItemResponse(
    val success: Boolean,
    val message: String,
    val data: DrivingResponse,
    val error: String,
    val statusCode: Int,
)
