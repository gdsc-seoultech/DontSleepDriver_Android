package com.comye1.dontsleepdriver.data.model

data class DSDResponse(
    val success: Boolean,
    val message: String,
    val data: List<DSDdata?> = listOf(DSDdata()),
    val error: String
)
