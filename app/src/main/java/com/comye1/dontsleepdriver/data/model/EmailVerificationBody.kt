package com.comye1.dontsleepdriver.data.model

data class EmailVerificationBody(
    val email: String,
    val token: String
)
