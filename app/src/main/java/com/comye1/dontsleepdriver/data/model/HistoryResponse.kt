package com.comye1.dontsleepdriver.data.model

data class HistoryResponse(
    val success: Boolean,
    val message: String,
    val data: List<DrivingResponse> = listOf()
)
