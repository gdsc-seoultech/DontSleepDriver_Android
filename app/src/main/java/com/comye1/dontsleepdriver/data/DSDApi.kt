package com.comye1.dontsleepdriver.data

import com.comye1.dontsleepdriver.data.model.*
import retrofit2.http.Body
import retrofit2.http.POST

interface DSDApi {
    @POST("api/email")
    suspend fun emailVerificationCode(
        @Body emailBody: StringBody
    ): DSDResponse

    @POST("api/email/check")
    suspend fun verifyEmailCode(
        @Body body: EmailVerificationBody
    ): DSDResponse

    @POST("api/user")
    suspend fun signUp(
        @Body body: SignUpBody
    ): DSDResponse

    @POST("api/auth")
    suspend fun signIn(
        @Body body: SignInBody
    ): DSDResponse
}