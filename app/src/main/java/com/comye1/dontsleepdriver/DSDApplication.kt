package com.comye1.dontsleepdriver

import android.app.Application
import com.kakao.sdk.common.KakaoSdk
import com.navercorp.nid.NaverIdLoginSDK
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class DSDApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        KakaoSdk.init(this, BuildConfig.KAKAO_API_KEY)
        NaverIdLoginSDK.initialize(this, "MnQQeQ2vJT_R8VE9zQCY", BuildConfig.NAVER_API_KEY, "Don't Sleep Driver")
    }
}