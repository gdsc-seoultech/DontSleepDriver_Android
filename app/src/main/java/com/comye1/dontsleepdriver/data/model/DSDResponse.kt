package com.comye1.dontsleepdriver.data.model

data class DSDResponse(
    val success: Boolean,
    val message: String,
    val data: DSDdata? = DSDdata(),
    val error: String
)
