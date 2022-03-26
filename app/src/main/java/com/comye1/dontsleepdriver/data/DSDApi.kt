package com.comye1.dontsleepdriver.data

import com.comye1.dontsleepdriver.data.model.*
import retrofit2.Call
import retrofit2.http.*

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

    @GET("api/user")
    suspend fun getUser(
        @HeaderMap header: Map<String, String>
    ): DSDResponse

    @POST("api/auth/kakao")
    suspend fun kakaoSignIn(
        @Body body: OAuthBody
    ): DSDResponse

    @POST("api/auth/google")
    suspend fun googleSignIn(
        @Body body: OAuthBody
    ): DSDResponse

    @POST("api/auth/naver")
    suspend fun naverSignIn(
        @Body body: OAuthBody
    ): DSDResponse

    @POST("api/driver/gpsData")
    fun postDriving(
        @HeaderMap header: Map<String, String>,
        @Body driving: DrivingBody
    ): Call<DrivingPostResponse>

    @GET("api/driver/pages")
    suspend fun getHistoryPages(
        @HeaderMap header: Map<String, String>
    ): PageResponse

    @GET("api/driver/list/{page}")
    suspend fun getHistoryByPage(
        @HeaderMap header: Map<String, String>,
        @Path("page") page: Int
    ): HistoryResponse

    @GET("api/driver/{id}")
    suspend fun getHistoryItem(
        @HeaderMap header: Map<String, String>,
        @Path("id") id: Int
    ): HistoryItemResponse
}